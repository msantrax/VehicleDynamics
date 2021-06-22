/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opus;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.Action;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.windows.IOContainer;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;


/**
 * Top component which displays something.
 */
@ConvertAsProperties(
    dtd="-//com.opus//Jurid//EN",
    autostore=false
)
@TopComponent.Description(
    preferredID="JuridTopComponent", 
    iconBase="com/opus/hdd_mount.png", 
    persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "com.opus.JuridTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
    displayName = "#CTL_JuridAction",
    preferredID="JuridTopComponent"
)
@Messages({
    "CTL_JuridAction=Abrir Terminal Jurid",
    "CTL_JuridTopComponent=Jurid Window",
    "HINT_JuridTopComponent=Jurid window"
})
public final class JuridTopComponent extends TopComponent implements  SerialDevice.SerialDeviceListener{
    
    private static final Logger log = Logger.getLogger(JuridTopComponent.class.getName());  
    
    MonitorTopComponent mtc;
    
    private String sdev_port;
    private int sdev_baud;
    private int sdev_databits;
    private int sdev_stopbits;
    private String sdev_parity;
    private String sdev_flow;
    
    private SerialManager SerialManager;
    private SerialDevice sdev;
    
     private boolean jurid_raw_on;
    public static OutputWriter jout;
    public static OutputWriter jerr;
    private InputOutput jurid;
    private byte[] juridbuffer;
    private int juridbuffer_ptr = 0;

    public static OutputWriter r_jout;
    public static OutputWriter r_jerr;
    private InputOutput r_jurid;
    
    private final static int CTC_INBUFFER_LENGHT = 256;
    private byte[] ctc_inbuffer = new byte[ CTC_INBUFFER_LENGHT];
    private int ctc_inbuffer_ptr=2;
    private boolean ctc_binario = true;

    public static enum JURID_STATE { 
            TRANSFER,
            ESC_REQUEST,
            EOL_REQUEST,
            TAB_REQUEST,
            TAB_HEADER,
            ESC_13,
            ESC_82,
            ESC_27,
            ESC_40,     
    };   
    public static JuridTopComponent.JURID_STATE juridstate;
    
    public static ArrayList<Integer>tabs;
    int tab_ptr;
    String msg;
    
    
    public static ArrayList<String>stabs;
    
    
    //public static HashMap<String, Integer> PARITY = new HashMap();
    
    public JuridTopComponent() {
        
        juridbuffer = new byte[SerialDevice.SERIAL_BUFFER_LENGHT]; 
        this.jurid_raw_on = false;
        
        initComponents();
        setName("Terminal Jurid");
        setToolTipText("");
        
        loadConfig();
        initSerial();
        initTools();   
    }

    
    public void setMonitor (MonitorTopComponent mtc){
        this.mtc = mtc;
    }
    
    
    /**
     * Carrega config dos paramatros do terminal Jurid
     */
    private void loadConfig(){
        
        Preferences node = NbPreferences.root();
      
        
        sdev_port = node.get("jurid.port", "/dev/ttyUSB1");
        sdev_baud = node.getInt("jurid.baud", 19200);
        sdev_databits = node.getInt("jurid.databits", 8);
        sdev_stopbits = node.getInt("jurid.stopbits", 1);
        sdev_parity = node.get("jurid.parity", "N");
        sdev_flow = node.get("jurid.flow", "NONE");
        
        node.put("jurid.port", sdev_port);
        node.putInt("jurid.baud",sdev_baud);
        node.putInt("jurid.databits",  sdev_databits);
        node.putInt("jurid.stopbits",sdev_stopbits);
        node.put("jurid.parity", sdev_parity);
        node.put("jurid.flow", sdev_flow);
        
        try {
            node.flush();
        } catch (BackingStoreException ex) {
            showConfigError(ex.getMessage());
        }
        
        log.log(Level.FINE, "Config do Painel Jurid iniciado na porta ", sdev_port);  
    }
    
    
     
    /**
     * Mostra dialog box com erro do config e loga 
     * @param message 
     */
    private void showConfigError(String message){
        
        log.log(Level.SEVERE, "Erro no configurador do painel : {0}", message);
        
        NotifyDescriptor d = new NotifyDescriptor(message,"Configurador do Painel",
                            NotifyDescriptor.DEFAULT_OPTION,
                            NotifyDescriptor.ERROR_MESSAGE,
                            null,null);
        DialogDisplayer.getDefault().notify(d);
    }
   
    
    /**
     * Abre terminal de dados brutos do Jurid
     */
    public void openRawJurid(){
        
        IOProvider iop = IOProvider.getDefault();
        IOContainer ioc = IOContainer.getDefault();
        
        // Painel jurid dados brutos
        r_jurid = iop.getIO("JURID - Dados Brutos", new Action[]{}, ioc);
        // Mostre-a no canvas
        r_jurid.select();
        // Assinale os pointers das stream de saida e erro para uso durante os trabalhos
        r_jout = r_jurid.getOut();
        r_jerr = r_jurid.getErr();
        // Informe usuário que estamos andando ...
        r_jerr.println("Listagem de dados transitando no canal LPT do MUX :");
        
        jurid_raw_on= true;
        
    }
    
       
    /**
     * Inicia ferramentas auxiliares
     */
    private void initTools(){
        
        IOProvider iop = IOProvider.getDefault();
        IOContainer ioc = IOContainer.getDefault();
        
        jurid = iop.getIO("Rack JURID", new Action[]{}, ioc);
        // Mostre-a no canvas
        jurid.select();
        // Assinale os pointers das stream de saida e erro para uso durante os trabalhos
        jout = jurid.getOut();
        jerr = jurid.getErr();
        // Informe usuário que estamos andando ...
        jerr.println("Iniciando Jurid");
        
        //openRawJurid();   
    }
    
     // ==========================================================================================================================
    // Recepção de eventos
    // ==========================================================================================================================
     
    /**
     * Recepção do evento serial
     * @param e 
     *  Evento armazenador da recepção
     */
    @Override
    public void eventoSerial(SerialDevice.Event e){
    
        
        int eventType = e.getEventType();
        
        switch (eventType){
            case SerialDevice.Event.EVENT_TERM :
                // Dados para o terminal
                System.out.println("Term:"+ new String(e.buffer,0,e.lenght));
                //if (ctc != null) ctc.write(new String(e.buffer,0,e.lenght));
                break;
            case SerialDevice.Event.EVENT_CMDR :
                byte cmd_type = e.buffer[0];
                //block_tx=true;
                switch (cmd_type){
                    case 1 : case 2 :
                        renderJurid (e.buffer, e.lenght, cmd_type);
                        break;
                    case 3:
                        renderTerminal (e.buffer, e.lenght);
                        break;
                }
                
                break;
            case SerialDevice.Event.EVENT_OPEN:
                log.log(Level.INFO, "Porta Serial Aberta");
                break;
        }    
    }
    
    
    /**
     * inicia interface serial e canais de comunicação
     */
    private void initSerial (){
        
        
        SerialManager = new SerialManager();
        sdev = SerialManager.getDevice(getSdev_port());
        
        if (sdev == null){
            StringBuilder sb = new StringBuilder();
            Object[] ports = SerialManager.getAvailablePorts();
            if (ports.length ==0) {
                sb.append("Não há portas disponíveis para conexão...");
            }
            else{
                sb.append("A porta de comunicação solicitada não está disponível. \n");
                sb.append("Há a seguintes alternativas : \n");
                for (Object obj : ports){
                     sb.append(obj.toString()).append("\n");
                }
            }
            
            showConfigError(sb.toString());
             return;
        }
        
        sdev.configPort(sdev_baud, sdev_databits, sdev_stopbits, sdev_parity, sdev_flow);
        
        sdev.connect();
        sdev.addSerialDeviceListener(this);
    } 
     
    
    // ==========================================================================================================================
    // Renderização dos dados de saida da imprressora
    // ==========================================================================================================================
    
     /**
     * Lista dados brutos no canvas de saida
     * @param buffer
     *  Dados a listar
     * @param lenght
     *  Comprimento do buffer
     */
    private void renderRaw (byte[] buffer){
        
        
        // Builder para a montagem dos hexadecimais
        StringBuilder sb0 =new StringBuilder();
        // Builder para os caracteres
        StringBuilder sb1 =new StringBuilder();
        // Counter de linha
        int tcounter = 0;
        // temporarios de trabalho
        int token;
        String temp;
        
        r_jout.println(" ---");
        
        //String temp1 = new String(buffer);
        
        
        // Primeiro a indicação de offset no buffer
        sb0.append(String.format("%03d: ", 0));      
        // Rode no bufeer recebido
        for (int i=0; i<buffer.length; i++, tcounter++){
            // Capture o dado para trabalho
            token = buffer[i];
            if (tcounter == 20){
                // Fim da linha de dados, imprima
                temp = sb0.toString() + "  " + sb1.toString();
                r_jout.println(temp);
                // Zere e inicialize os builders
                sb0=new StringBuilder();
                sb0.append(String.format("%03d: ", i));
                sb1=new StringBuilder();
                tcounter=0;  
            }
            // Se encontramos o delimitador(0x00) o trabalho está concluído
            if (token == 0) {
                break;
            }
            // Situação default trate o dado
            if (token == 27){
                sb0.append("EE[ ] ");
            }
            else if (token == 13){
                sb0.append("->[ ] ");
            }
            else if (token == 9){
                sb0.append("TB[ ] ");
            }
            else{
                if (token <0x20 || token > 0x7e) token = '^';
                sb0.append(String.format("%02X[%c] ", token, token));
            }
            
            if (token <0x20 || token > 0x7e) token = '^';
            sb1.append(String.format("%c",token));
            //sb1.append(" ");
        }
        
       // if (tcounter < 15 && tcounter !=0){
            for (int i=0; i<(20-tcounter); i++){
                sb0.append("   ");                
            }
            temp = sb0.toString() + "  " + sb1.toString();
            r_jout.println(temp);
       // }    
    }
    
    
    /**
     * Renderiza (aplica os códigos ESC da impressora se necessário) os dados recebidos do jurid
     */
    private void renderJurid (byte[] buffer, int lenght, byte cmd_type){
        
        
        ArrayList<String>juridlines;
        StringBuilder sb ;
        int pptr;
        
        int col_ptr;
        
        System.arraycopy(buffer, 1, juridbuffer, juridbuffer_ptr, lenght-1);
        juridbuffer_ptr += lenght-1;
        
        if ( cmd_type == 2){
                       
            if (jurid_raw_on){
                renderRaw (juridbuffer);
            }
        
           // jerr.println("Entrando na máquina de estados ...");
            
            col_ptr=0;
            
            // Mensagem montou, execute o parse
            juridlines = new ArrayList<String>();
            sb = new StringBuilder();
            juridstate = JURID_STATE.TRANSFER;
            pptr = 0;
            // Coloque um EOL caso o jurid não o tenha feito
            juridbuffer[++juridbuffer_ptr] = 0x0D;
            
            do {
                byte b = juridbuffer[pptr];
                switch (juridstate) {
                    case TRANSFER:
                        if (b > 31 && b < 244){
                            // Se for algo imprimível (sic)
                            sb.append((char)b);
                            col_ptr++;
                            pptr++;
                            break;
                        }
                        else if (b == 0x0d){
                            juridstate = JURID_STATE.EOL_REQUEST;
                            pptr++;
                            break;
                        }
                        else if (b == 0x1B){
                            juridstate = JURID_STATE.ESC_REQUEST;
                            pptr++;
                            break;
                        }
                        else if (b == 0x09){
                            juridstate = JURID_STATE.TAB_REQUEST;
                            pptr++;
                            break;
                        }
                        else pptr++;
                    case TAB_REQUEST:
                        msg= String.format("Em tab request col_ptr= %d - tab_pptr=%d - tabs.size=%d", col_ptr, tab_ptr, tabs.size());
                        r_jerr.println(msg);
                        int c_tab = getCurrentTab(tab_ptr);
                         r_jerr.println("c_tab" + c_tab); 
                        int advance = c_tab-col_ptr;
                        if (advance <0){
                            r_jerr.println("Advance < 0 !! -> " + col_ptr + " / " + c_tab);
                            sb.append(' ');
                        }
                        else{
                            for (int j=0; j<=advance; j++){
                                sb.append(' ');
                                col_ptr++;
                            }
                        }
                        tab_ptr++;
                        juridstate = JURID_STATE.TRANSFER;
                        break;
                    case EOL_REQUEST:
                        r_jerr.println("Em eol request");
                        juridlines.add(sb.toString());
                        sb=new StringBuilder();
                        col_ptr=0;
                        tab_ptr=0;
                        tabs = new ArrayList<Integer>();
                        //pptr++;
                        juridstate = JURID_STATE.TRANSFER;
                        break;
                    case ESC_REQUEST:
                        byte b1 = juridbuffer[pptr];
                        r_jerr.println("Encontrado código ESC : " + String.valueOf(b1) + " no offset " + pptr);
                        switch (b1){
                            case 65 : case 78: case 60 : case 102 : case 0x30: case 0x51:
                                pptr++;
                                juridstate = JURID_STATE.TRANSFER;
                                break;
                            case 82:
                                juridstate = JURID_STATE.ESC_82;
                                break;
                             case 0x28:
                                juridstate = JURID_STATE.ESC_40;
                                break;
                             //case 0x30:
                                 // Header de tab, processe o resto
                                 
                             default:
                                pptr++;
                                sb.append("[ESC+");
                                sb.append(String.valueOf(b1));
                                sb.append("]");
                                juridstate = JURID_STATE.TRANSFER;
                        }
                        break;
                     case ESC_40:
                         r_jerr.println("Em Esc 48");
                         // Tab Request 
                        r_jerr.println("Executando parse da tabulação, ESC40 no offset " + pptr);
                        // Comando de tabulaçao
                        //tabs = new ArrayList<Integer>(); 
                        byte b2;
                        StringBuilder tab;
                        tab = new StringBuilder();
                        pptr++;
                        do{
                            b2 = juridbuffer[pptr++];
                            char c = (char)b2;
                            if (pptr> buffer.length) {
                                r_jerr.println("Erro ou fim de bloco no parse do header");
                                juridstate = JURID_STATE.TRANSFER;
                                break;
                            }
                            else {    
                                if (b2 != '.') tab.append(c);
                            }
                        } while (b2 !='.');            
                        
                        String[]splits = tab.toString().split(",");
                        
                        // Header consumado, adquira os dados
                        r_jerr.println("Consumou Header: tab =  " + tab.toString() + " / " + tab.length()); 
                        String[]tab_splits = tab.toString().split(",");
                        r_jerr.println("Encontramos " + tab_splits.length + " splits");
                        
                        //tabs = new ArrayList<Integer>();
                        
                        for (String tsplit : tab_splits){
                            int tab_temp = Integer.valueOf(tsplit);
                            r_jerr.println("\tAdicionando tab em " + tab_temp);
                            tabs.add(tab_temp);
                           //r_jerr.println("tab size = " + tabs.size());
                        }
                        r_jerr.println("Tab size agora em  " + tabs.size());
                        
                        juridstate = JURID_STATE.TRANSFER;
                        break;
                    case ESC_82:
                        int rep = ((juridbuffer[++pptr]-48)*100) + ((juridbuffer[++pptr]-48)*10) + juridbuffer[++pptr]-48;
                        byte c = juridbuffer[++pptr];
                        for (int i = 0; i < rep; i++) {
                            sb.append((char)c);
                        }
                        juridstate = JURID_STATE.TRANSFER;
                        break;
                }
            }while (pptr < juridbuffer_ptr);
                       
            for (String line : juridlines) jout.println(line);              
//            IOPosition.Position pos = IOPosition.currentPosition(jurid);
//            pos.scrollTo();
            
            juridbuffer_ptr=0;           
            for (int j =0 ;j<juridbuffer.length ; j++){
                juridbuffer[j]=0;
            }
           
        }
    }
    
    /**
     * Recupera os taba atual
     * @param index
     * @return 
     */
    private int getCurrentTab (int index){
        
        if (tabs != null && !tabs.isEmpty()){
            return tabs.get(index);
        }
        
        return 1;
    }
    
    
    // ==========================================================================================================================
    // Serviços do terminal centauro
    // ==========================================================================================================================
     
    /**
     * Envia comando montado para o outro terminal
     * @param type
     *  Tipo do comando conf. evento serial
     * @param msg 
     *  Payload do comando, se houver
     */
    public void termSendCmd(int type, String msg){
       
       
       if (msg != null){         
            System.arraycopy(msg.getBytes(), 0, ctc_inbuffer, 2, msg.length());
            ctc_inbuffer_ptr = msg.length()+2;
        }
        
        ctc_inbuffer[ctc_inbuffer_ptr] = (byte)0xff;    
        for (int i = 0; i <=ctc_inbuffer_ptr; i++) {
            char c = (char)ctc_inbuffer[i];
            sdev.putc(c);
        }       
        ctc_inbuffer_ptr=2;
    }
    
    /**
     * Envia caracter simples para o terminal.
     * @param c 
     *  Caracter a enviar
     */
    public void termPutc (char c){
       
        if (ctc_binario){
            if (c == 0x0A || ctc_inbuffer_ptr >=CTC_INBUFFER_LENGHT){
                //termSendCmd(4, new String(ctc_inbuffer, 0, ctc_inbuffer_ptr-1));
                termSendCmd(4,null);
            }
            else{
                ctc_inbuffer[ctc_inbuffer_ptr++]=(byte)c;
            }
        }
        else{
            //System.out.print(c);
            //sdev.putc(c);
        }    
    }
    
    /**
     * Evia retorno de comando para o terminal Centauro
     * @param buffer
     *  Mensagem de retorno
     * @param lenght
     *  Comprimentop da mensagem
     */
    private void renderTerminal (byte[] buffer, int lenght){    
        
        String temp = new String(buffer,1,lenght-2);
        log.log(Level.INFO, "mensagem da impressora Jurid", temp);
        
    }
    
  
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        woutput = new javax.swing.JTextArea();

        setLayout(new java.awt.BorderLayout());

        woutput.setColumns(20);
        woutput.setRows(5);
        jScrollPane1.setViewportView(woutput);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea woutput;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
         
        if (sdev != null) sdev.close();
        if (jurid != null) jurid.closeInputOutput();
        
        if (r_jurid != null){
            r_jurid.closeInputOutput();
            jurid_raw_on=false;
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }
    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    
    /**
     * @return the sdev_port
     */
    public String getSdev_port() {
        return sdev_port;
    }

    /**
     * @param sdev_port the sdev_port to set
     */
    public void setSdev_port(String sdev_port) {
        this.sdev_port = sdev_port;
    }

    /**
     * @return the sdev_baud
     */
    public int getSdev_baud() {
        return sdev_baud;
    }

    /**
     * @param sdev_baud the sdev_baud to set
     */
    public void setSdev_baud(int sdev_baud) {
        this.sdev_baud = sdev_baud;
    }

    /**
     * @return the sdev_databits
     */
    public int getSdev_databits() {
        return sdev_databits;
    }

    /**
     * @param sdev_databits the sdev_databits to set
     */
    public void setSdev_databits(int sdev_databits) {
        this.sdev_databits = sdev_databits;
    }

    /**
     * @return the sdev_stopbits
     */
    public int getSdev_stopbits() {
        return sdev_stopbits;
    }

    /**
     * @param sdev_stopbits the sdev_stopbits to set
     */
    public void setSdev_stopbits(int sdev_stopbits) {
        this.sdev_stopbits = sdev_stopbits;
    }

    /**
     * @return the sdev_parity
     */
    public String getSdev_parity() {
        return sdev_parity;
    }

    /**
     * @param sdev_parity the sdev_parity to set
     */
    public void setSdev_parity(String sdev_parity) {
        this.sdev_parity = sdev_parity;
    }

    /**
     * @return the sdev_flow
     */
    public String getSdev_flow() {
        return sdev_flow;
    }

    /**
     * @param sdev_flow the sdev_flow to set
     */
    public void setSdev_flow(String sdev_flow) {
        this.sdev_flow = sdev_flow;
    }
    
    
    
    
}
