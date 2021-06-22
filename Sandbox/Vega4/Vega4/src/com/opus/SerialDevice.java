/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opus;


import gnu.io.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author antrax
 */
public class SerialDevice implements SerialPortEventListener , Serializable{

    
    private static final Logger log = Logger.getLogger(SerialDevice.class.getName());   
    
    
    // Herdado do Driver RXTX
    public static final int RXTX_SERIAL   = 1;  // rs232 RXTX
    public static final int RXTX_PARALLEL = 2;  // Parallel RXTX
    public static final int RXTX_I2C      = 3;  // i2c RXTX
    public static final int RXTX_RS485    = 4;  // rs485 RXTX
    public static final int RXTX_RAW      = 5;  // Raw RXTX


    // Erros na inicialização da porta
    public static final int RXTX_INUSE    = 6;  // Porta em Uso
    public static final int RXTX_FAILED   = 7;  // Porta falhou na abertura


    /**
    public static final int  DATABITS_5             =5;
	public static final int  DATABITS_6             =6;
	public static final int  DATABITS_7             =7;
	public static final int  DATABITS_8             =8;
	public static final int  PARITY_NONE            =0;
	public static final int  PARITY_ODD             =1;
	public static final int  PARITY_EVEN            =2;
	public static final int  PARITY_MARK            =3;
	public static final int  PARITY_SPACE           =4;
	public static final int  STOPBITS_1             =1;
	public static final int  STOPBITS_2             =2;
	public static final int  STOPBITS_1_5           =3;
	public static final int  FLOWCONTROL_NONE       =0;
	public static final int  FLOWCONTROL_RTSCTS_IN  =1;
	public static final int  FLOWCONTROL_RTSCTS_OUT =2;
	public static final int  FLOWCONTROL_XONXOFF_IN =4;
	public static final int  FLOWCONTROL_XONXOFF_OUT=8;
    */


    private int baudrate=19200;
    private int databits=SerialPort.DATABITS_8;
    private int stopbits=SerialPort.STOPBITS_1;
    private int parity=SerialPort.PARITY_NONE;
    private int flowctrl=0;


    private transient OutputStream out;
    private transient InputStream in;

    private  transient CommPortIdentifier portId;
    private  transient SerialPort serialPort;

    public static final int SERIAL_BUFFER_LENGHT = 2048;
    private byte[] buffer = new byte[SERIAL_BUFFER_LENGHT];
    private int bufferptr=0;


    private String portName;
    private boolean available = true;
    private String owner;
    private int portType;
    private int error;
    private boolean connected;

    private boolean blockMode=false;
    
    private int synclenght=0;

    private long messagetag = 0;
    private long lastsync = -1;
    private int synclag = 3;
  
    private boolean closerequest = false;
      
    /** Estrutura para armazenamento dos listeners do dispositivo*/ 
    ArrayList<SerialDeviceListener> listeners = new ArrayList();

    private static boolean initHashes = true;
    public static HashMap<String, Integer> PARITY = new HashMap();
    public static HashMap<String, Integer> FLOW = new HashMap();

    
    public static enum SERIAL_STATE { 
            LISTENING,
            CMD_REQUEST,
            CMD_BUILD,
            CMD_BLOCK
    };
    public SERIAL_STATE serialstate = SERIAL_STATE.LISTENING;
    private final int blockflag1 = 0xFE;
    private final int blockflag2 = 0xFF;

    /**
     * Construtor padrão da classe
     * @param type
     * @param name
     * @param aval
     * @param erro
     */
    public SerialDevice(int type, String name, boolean aval, int erro ){
        
        portName = name;
        available = aval;
        portType=type;
        error = erro;
        connected=false;

        if (initHashes) initHashes();
    }

    
    private void initHashes(){

        PARITY.put("N", 0);
        PARITY.put("NONE", 0);
        PARITY.put("NO", 0);
        PARITY.put("O", 1);
        PARITY.put("IMPAR", 1);
        PARITY.put("ODD", 1);
        PARITY.put("E", 2);
        PARITY.put("PAR", 2);
        PARITY.put("EVEN", 2);
        PARITY.put("M", 3);
        PARITY.put("MARK", 3);
        PARITY.put("MARCA", 3);
        PARITY.put("S", 4);
        PARITY.put("SPACE", 4);
        PARITY.put("ESPACO", 4);

        FLOW.put("N", 0);
        FLOW.put("NONE", 0);
        FLOW.put("RTS_CTS IN", 1);
        FLOW.put("RTSCTS IN", 1);
        FLOW.put("RTS_CTS OUT", 2);
        FLOW.put("RTSCTSOUT", 2);
        FLOW.put("RTS_CTS", 3);
        FLOW.put("RTSCTS", 3);
        FLOW.put("XON_XOFF IN", 4);
        FLOW.put("XONXOFF IN", 4);
        FLOW.put("XON_XOFF OUT", 8);
        FLOW.put("XONXOFF OUT", 4);
        FLOW.put("XON_XOFF", 12);
        FLOW.put("XONXOFF", 12);

        initHashes = false;
    }


    /**
     *
     */
    public boolean connect() {

        try{

            portId = CommPortIdentifier.getPortIdentifier(portName);
            if ( portId.isCurrentlyOwned()){
                log.log(Level.WARNING, "OOppss ! - A porta {0} esta em uso no momento", portId);
                return false;
            }
            else{
                CommPort commPort = portId.open(this.getClass().getName(),2000);
                if ( commPort instanceof SerialPort ){
                    serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(baudrate,databits,stopbits,parity);
                    serialPort.setFlowControlMode(flowctrl);

                    in = serialPort.getInputStream();
                    out = serialPort.getOutputStream();

                    serialPort.addEventListener(this);
                    serialPort.notifyOnDataAvailable(true);
                    log.log(Level.INFO, "Porta : {0} conectada com config -> {1}:{2}{3}{4}", new Object[]{portName, baudrate, databits, parity, stopbits});
                    setConnected(true);
                    available=false;
                    messagetag = 0;
                    lastsync = -1;
                    closerequest = false;
                    return true;

                }
                else{
                    log.log(Level.WARNING, "Gerente serial : s\u00f3 portas seriais por favor, a {0} n\u00e3o \u00e9", portName);
                    return false;
                }
            }

        }

        // Secção de reports de falhas
        catch (UnsupportedCommOperationException e) {
            log.log(Level.SEVERE, "Operação não suportada...", e);
            return false;
        }
        catch (NoSuchPortException e) {
            log.log(Level.SEVERE, "Porta não existe..", e);
            return false;
        }
        catch (PortInUseException e) {
            log.log(Level.SEVERE, "Porta está sendo usada...", e);
            return false;
        }
        catch (IOException e) {
            log.log(Level.SEVERE, "Falha de IO", e);
            return false;
        }
        catch (TooManyListenersException e) {
            log.log(Level.SEVERE, "Operação não suportada", e);
            return false;
        }

    }

    /**
     * Encerra funcionamento da porta serial
     */
    public void close(){

        if (isConnected()){
            log.info("Fechando " + portName);
            serialPort.removeEventListener();
   
            serialPort.close();
            setConnected(false);
            available=false;
            closerequest = false;
        }
    }

    
    /**
     * Cofigura os parametros da porta serial
     * @param baud
     * @param data
     * @param stop
     * @param par
     * @param flow
     */
    public void configPort (int baud, int data, int stop, String par, String flow ){

        baudrate = baud;
        databits = data;
        stopbits = stop;
        setParity(par);
        setFlowctrl(flow);
        configDefault();
    }

    
    
    
    /**
     * Executa config da porta serial com paramatros default : 19200 / 8N1
     */
    public void configDefault(){
        
        if (serialPort != null){
            try{
                serialPort.setSerialPortParams(baudrate,databits,stopbits,parity);
                serialPort.setFlowControlMode(flowctrl);
            }
            catch (UnsupportedCommOperationException e) {
               log.log(Level.WARNING, "Unsuported", e);
            }
        }   
    }
    
    
    /**
     * Stub de ajuste dos parametros de operação da porta serial. Usado pelo dialogo de config
     * @param baud
     * @param data
     * @param stop
     * @param par
     * @param flow
     */
    public void setComDevice (int baud, int data, int stop, int par, int flow){
        try{
            serialPort.setSerialPortParams(baud,data+5,stop+1,par+1);
            serialPort.setFlowControlMode(flow+1);
        }
        catch (UnsupportedCommOperationException e) {
            System.out.println("Operação não suportada...");
            //e.printStackTrace();
        }

    }


    @Override
    public void serialEvent(SerialPortEvent event) {

        int data;
        boolean done = false;
        int block_cnt = 0;
        byte mtype;
        
        switch (event.getEventType()) {

            case SerialPortEvent.BI:

            case SerialPortEvent.OE:

            case SerialPortEvent.FE:

            case SerialPortEvent.PE:

            case SerialPortEvent.CD:

            case SerialPortEvent.CTS:

            case SerialPortEvent.DSR:

            case SerialPortEvent.RI:

            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;

            case SerialPortEvent.DATA_AVAILABLE:
           
                try {               
                    do{
                        switch (serialstate){
                            case LISTENING :
                                //System.out.printf("Em Listening (available = %d) \r\n", in.available());
                                bufferptr = 0; // inicie o buffer
                                while ((data = in.read()) > -1){
                                    // Se o token de dados é a flag de bloco parta para montar comando binário
                                    if ( data == blockflag1) {
                                        //Alog.l.info("Achei delimitador : " + String.valueOf(data) );
                                        // Se há dados de Listening, livre-se deles
                                        if (bufferptr !=0){
                                            //notify(SerialDevice.Event.EVENT_TERM, 0, messagetag, new String(buffer,0,bufferptr));
                                            notify(SerialDevice.Event.EVENT_TERM, messagetag, buffer, bufferptr);
                                            // Renove o buffer
                                            bufferptr = 0;
                                        }
                                        // Interrompa leitura da stream de entrada (essa deve ser executada agora em CMD_REQUEST)
                                        serialstate=SERIAL_STATE.CMD_REQUEST;                               
                                        break;
                                    }
                                    // Stream normal de saida para o terminal, só adicione no buffer
                                    buffer[bufferptr++] = (byte)data;
                                }
                                // Se estamos apenas ouvindo envie para o terminal
                                if (serialstate==SERIAL_STATE.LISTENING){
                                    notify(SerialDevice.Event.EVENT_TERM, messagetag, buffer, bufferptr);
                                    // Trabalho feito, saia da maquina de estados
                                    done = true;
                                }
                                break;
                            case CMD_REQUEST :                               
                                //System.out.printf("Em CMD_RQS (available = %d) \r\n" , in.available());
                                if ((data = in.read()) == blockflag2) {
                                    // Header de bloco de mensagem identificado
                                    bufferptr = 0; // inicie o buffer
                                    // Proximo byte é o identificador de bloco
                                    mtype =(byte)in.read();
                                    //System.out.printf(" ---- type = %d \r\n" , mtype);
                                    buffer[bufferptr++] = mtype;
                                    if (mtype == 5 || mtype == 6 || mtype == 7) {
                                        block_cnt = 60;
                                        serialstate=SERIAL_STATE.CMD_BLOCK;
                                    }
                                    else{
                                        serialstate=SERIAL_STATE.CMD_BUILD;
                                    }
                                    //System.out.printf("Em Request com %d \r\n", data);
                                }
                                else{
                                    // Alarme falso volte a somente ouvir
                                    serialstate=SERIAL_STATE.LISTENING;
                                    //System.out.printf("Em Request abort com %d \r\n", data);
                                }
                                break;
                            case CMD_BUILD :
                                //System.out.printf("Em Build (available = %d) \r\n" , in.available());
                                while ((data = in.read()) > -1){
                                    // Se o token de dados é a flag de bloco esse é o fim dele
                                    if ( data == blockflag2) {
                                        //System.out.printf("Em Build end com %d \r\n", data);
                                        serialstate=SERIAL_STATE.LISTENING;
                                        // O bloco está completo, despache para o aplicativo
                                        notify(SerialDevice.Event.EVENT_CMDR, messagetag, buffer, bufferptr);
                                        done = true;
                                        break;
                                    }
                                    // Adicione token no buffer
                                    buffer[bufferptr++] = (byte)data;
                                }
                                break;
                            case CMD_BLOCK :  
                                //System.out.printf("Em CMD_BLOCK (available = %d) / block cnt = %d \r\n" , in.available(), block_cnt);
                                while ((data = in.read()) > -1){
                                    // Se o token de dados é a flag de bloco esse é o fim dele
                                    if (block_cnt == 0) {                                      
                                        serialstate=SERIAL_STATE.LISTENING;
                                        // O bloco está completo, despache para o aplicativo
                                        notify(SerialDevice.Event.EVENT_CMDR, messagetag, buffer, bufferptr);
                                        // Descate o 0xff do fim do comando (estamos em block)
                                        //data = in.read();
                                        done = true;
                                        break;
                                    }
                                    // Adicione token no buffer
                                    buffer[bufferptr++] = (byte)data;
                                    block_cnt--;
                                }
                                break;                              
                        }                   
                    } while (!done && (in.available() !=0));
   
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 
     * @param tokens 
     */
    public void write(String tokens){

        if (isConnected()){
            try{
                this.out.write(tokens.getBytes());
            }
            catch ( IOException e ){
                e.printStackTrace();
            }
        }else{
            System.out.println("Porta não está conectada !");
        }
    }

    
    
    public void putc (int c){
        
        if (isConnected()){
            try{
                this.out.write(c);
            }
            catch ( IOException e ){
                e.printStackTrace();
            }
        }else{
            System.out.println("Porta não está conectada !");
        }
        
    }
    
    /**
     * @return the connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * @param connected the connected to set
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * @return the blockMode
     */
    public boolean isBlockMode() {
        return blockMode;
    }

    /**
     * @param blockMode the blockMode to set
     */
    public void setBlockMode(boolean blockMode) {
        this.blockMode = blockMode;
    }

    /**
     * @return the lastsync
     */
    public long getLastsync() {
        return lastsync;
    }


    // ======================================================================
    // Tratamento e disparo de eventos ======================================


    /** Classe interna que define o tipo de evento gerado por objetos
     *  DBListbox. O nome da classe é Event portanto o nome copmpleto deverá
     *  ser DBListBox.Event */
    public static class Event extends java.util.EventObject{

        public static final int EVENT_DATA   = 1;  // Data disponivel
        public static final int EVENT_OPEN   = 2;  // Porta abriu
        public static final int EVENT_CLOSE  = 3;  // Porta fechou
        public static final int EVENT_FAIL   = 4;  // Porta Falhou
        
        public static final int EVENT_TERM   = 5;  // Caracteres simples para o terminal
        public static final int EVENT_CMDR   = 6;  // Retorno de comando generico
        
        
        int eventType;
        long tag;
        byte []buffer;
        int lenght;
        

        public Event(SerialDevice source, int eventType, long tag, byte[] buffer, int lenght){

            super(source);
            this.eventType=eventType;
            this.buffer=buffer;
            this.lenght=lenght;
            this.tag=tag;

        }

        public SerialDevice getDevice() { return (SerialDevice)getSource();}
        public int getEventType() { return eventType;}
        public long getTag() { return tag;}
        public byte[] getBuffer() {return buffer;}
        public int getLenght() { return lenght;}
       

    }

    /** Interface interna que deverá ser implementada por qualquer objeto
     * que deseja ser notificado quando hopuvrer movimento no dispositivo serial */
    public interface SerialDeviceListener extends java.util.EventListener {
        public void eventoSerial(SerialDevice.Event e);
    }

    /** Método de registro do listener do dispositivo serial */
    public void addSerialDeviceListener (SerialDevice.SerialDeviceListener l){
        listeners.add(l);
    }

    /** Método de remoção do registro do listener do dispositivo serial */
    public void removeSerialDeviceListener (SerialDevice.SerialDeviceListener l){
        listeners.remove(l);
    }

    /** Esse método é chamedo quando algo acontece no dispositivo */
    protected void notify(int localType, long tag, byte[] localBuffer, int lenght) {

        if (!listeners.isEmpty()){
            // Crie um objeto de evento para descrever a seleção
            SerialDevice.Event e = new SerialDevice.Event(this, localType, tag, localBuffer, lenght);

            // Rode entre os listeners
            for (Iterator i=listeners.iterator(); i.hasNext();){
                SerialDevice.SerialDeviceListener l = (SerialDevice.SerialDeviceListener)i.next();
                l.eventoSerial(e); //Notifique cada listener
            }
        }
    }

    // Final do disparo de eventos --------------------------------------------------------
    
    /**
     * @return the portName
     */
    public String getPortName() {
        return portName;
    }

    /**
     * @return the available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * @param available the available to set
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the portType
     */
    public int getPortType() {
        return portType;
    }


    /**
     * @return the error
     */
    public int getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(int error) {
        this.error = error;
    }

    /**
     * @return the baudrate
     */
    public int getBaudrate() {
        return baudrate;
    }

    /**
     * @param baudrate the baudrate to set
     */
    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    /**
     * @return the databits
     */
    public int getDatabits() {
        return databits;
    }

    /**
     * @param databits the databits to set
     */
    public void setDatabits(int databits) {
        this.databits = databits;
    }

    /**
     * @return the stopbits
     */
    public int getStopbits() {
        return stopbits;
    }

    /**
     * @param stopbits the stopbits to set
     */
    public void setStopbits(int stopbits) {
        this.stopbits = stopbits;
    }

    /**
     * @return the parity
     */
    public int getParity() {
        return parity;
    }

    public String getParityKey() {

        if (parity == 1) return "ODD";
        if (parity == 2) return "EVEN";
        if (parity == 3) return "MARK";
        if (parity == 4) return "SPACE";
        return "N";
    }

    
    public void setParity(String parityID) {

        parityID.trim();
        parityID.toUpperCase();

        if (PARITY.containsKey(parityID)){
            this.parity=PARITY.get(parityID);
        }
        else{
            this.parity=0;
        }
    }

    
    public void setParity(int parity){
        this.parity = parity;
    }

    /**
     * @return the flowctrl
     */
    public int getFlowctrl() {
        return flowctrl;
    }

    /**
     * @param flowctrl the flowctrl to set
     */
    public void setFlowctrl(int flowctrl) {
        this.flowctrl = flowctrl;
    }

    public void setFlowctrl (String flow){
        
        flow.trim();
        flow.toUpperCase();

        if (FLOW.containsKey(flow)){
            this.flowctrl=FLOW.get(flow);
        }
        else{
            this.flowctrl=0;
        }      
    }

    public String getFlowKey() {

        if (parity == 1) return "RTS_CTS IN";
        if (parity == 2) return "RTS_CTS OUT";
        if (parity == 3) return "RTS_CTS";
        if (parity == 4) return "XON_XOFF IN";
        if (parity == 8) return "XON_XOFF OUT";
        if (parity == 12) return "XON_XOFF";

        return "NONE";
    }

   
    /**
     * @return the synclenght
     */
    public int getSynclenght() {
        return synclenght;
    }

    /**
     * @param synclenght the synclenght to set
     */
    public void setSynclenght(int synclenght) {
        this.synclenght = synclenght;
    }

    /**
     * @return the synclag
     */
    public int getSynclag() {
        return synclag;
    }

    /**
     * @param synclag the synclag to set
     */
    public void setSynclag(int synclag) {
        this.synclag = synclag;
    }

    /**
     * @return the closerequest
     */
    public boolean isCloserequest() {
        return closerequest;
    }

    /**
     * @param closerequest the closerequest to set
     */
    public void setCloserequest(boolean closerequest) {
        this.closerequest = closerequest;
    }



}
