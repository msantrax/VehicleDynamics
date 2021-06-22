/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opus;

import com.opus.SerialDevice.SerialDeviceListener;
import java.awt.BorderLayout;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;
import org.openide.windows.WindowManager;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//com.opus//Monitor//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "MonitorTopComponent",
        iconBase = "com/opus/monitor_icon.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "com.opus.MonitorTopComponent")
@ActionReference(path = "Menu/Monitores" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        ///displayName = "#CTL_MonitorAction",
        displayName = "Carregar Monitor",
        preferredID = "MonitorTopComponent")
@Messages({
    "CTL_MonitorAction=Carregar Monitor",
    "CTL_MonitorTopComponent=Janela Monitor",
    "HINT_MonitorTopComponent=Essa é a janela de Monitor"
})


@ServiceProviders({
        @ServiceProvider (service = MonitorContext.class)
})


public final class MonitorTopComponent extends TopComponent implements  SerialDeviceListener,
                                                                        MonitorContext{ 
    
    private static final Logger log = Logger.getLogger(MonitorTopComponent.class.getName());  
    
    
    private String Monitorname;
    
    private GriddedPanel gp1;
    private JScrollPane jsp;
    private JPanel fillarea;
    
    private boolean centauro_on;  
    
    private int pool_time;
    private boolean pool_on;
    
    private String sdev_port;
    private int sdev_baud;
    private int sdev_databits;
    private int sdev_stopbits;
    private String sdev_parity;
    private String sdev_flow;
    
    private SerialManager SerialManager;
    private SerialDevice sdev;
    
    private SimulatorTopComponent stc ;
    private boolean simulator_on;
    private boolean simulador_running;
    
    private CentauroTopComponent ctc; 
    private final static int CTC_INBUFFER_LENGHT = 256;
    private byte[] ctc_inbuffer = new byte[ CTC_INBUFFER_LENGHT];
    private int ctc_inbuffer_ptr=2;
    private boolean ctc_binario = true;
    
    private ArrayList<ChannelBlockInterface> blocks;
   
    
    /**
     * Flag de comando de armazenamento de dados
     * Configurada pelo XML
     */
    private boolean storedata;
    private boolean freshscan;
    private byte[] burn;
    private transient Timer poolTimer;
    private poolTaskPerformer pooltaskperformer;
    private boolean block_tx;
    
    private CanaisScanner scanner;
    
    public MonitorTopComponent() {
        
        log.info(" \n \n");
        log.info("Iniciando Novo Monitor");
           
        this.storedata = false;
        this.centauro_on = false;
        this.simulator_on = false;
        this.simulador_running=false;
        
        this.pool_on = false;
        
        initComponents();
        
        gp1 = new GriddedPanel();
        fillarea = new JPanel();
        jsp = new JScrollPane();
        jsp.setViewportView(gp1);
        add(jsp, BorderLayout.CENTER);
   
        
        blocks = new ArrayList();
        burn = new byte[60];
        freshscan = false;
        block_tx=false;
        
        ctc_inbuffer[0]=(byte)0xfe;
        ctc_inbuffer[1]=(byte)0xff;        
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
     * Executa carga dos widgets e paineis, conecta a interface serial e outros quetais
     * @param std 
     */
    public void initSystem(boolean std){
        
        if (initDisplay(std)){
            setName(getMonitorname() + " [parado]");
            //initTools();
            initSerial();
            termSendCmd(4,"SET SCAN="+String.valueOf(pool_time));
        }
        associateLookup(Lookups.fixed(this, getActionMap()));
    }
    
    /**
     * Inicia sistema de captura de dados do Vega
     * @return 
     */
    private boolean initDisplay(boolean std){
        
        log.info("Iniciando config do painel de monitoramento e saida do Jurid");
        StatusDisplayer.getDefault().setStatusText("Iniciando display de monitoramento");
        
        Preferences node = NbPreferences.root();
        String rootdir = "/Vega/Jurid";
        node.put("config_rootdir", rootdir);
        
        
        try {
            node.flush();
        } catch (BackingStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        //userdir = System.getProperty("user.dir") + System.getProperty("file.separator") + rootdir ;
        
        if (!std) {
            JFileChooser chooser;
            chooser = new JFileChooser(rootdir);
            FileNameExtensionFilter filter = new FileNameExtensionFilter( "Arquivos de configuração do Jurid", "xml");
            chooser.setFileFilter(filter);      
            int returnVal = chooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    return loadConfig(chooser.getSelectedFile().getCanonicalPath());
                } catch (IOException ex) {
                   showConfigError("O arquivo não existe !");
                   return false;
                }
            }
            else {
                return false;
            }
        } else{
            try {
                //String temp = userdir + System.getProperty("file.separator") + "Canais.xml";
                return loadConfig(rootdir + System.getProperty("file.separator") + "Canais_Default.xml");
            } catch (IOException ex) {
               showConfigError("O arquivo não existe !");
               return false;
            }
        }
        
    }
    
    /**
     * Executa o serviço de carga do config dos paineis de trabalho
     * @param filepath
     * @return 
     */
    private boolean loadConfig(String filepath) throws IOException{
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        DocumentBuilder db;
        XMLErrorHandler error_handler;
        
        try {
            db = dbf.newDocumentBuilder();
            error_handler = new XMLErrorHandler();
            db.setErrorHandler(error_handler);
            Document doc = db.parse(new File(filepath));
            scanner = new CanaisScanner(doc, this);
            scanner.visitDocument();  
            if (error_handler.isHas_errors()){
                showConfigError(error_handler.getMessages().toString());
                return false;
            }
            else{
                return true;
            }
        } catch (ParserConfigurationException ex) {
            showConfigError("Erro no Parser do arquivo xml");
            //Exceptions.printStackTrace(ex);
        } catch (SAXException ex) {
            String dmesg = ex.getMessage();
            StringBuilder sb = new StringBuilder();
            sb.append("Erro no Processador SAX  : \n");
            sb.append(dmesg);
            showConfigError(sb.toString());
        }
        
        return false;
    }
    
    /**
     * Abre Simulador
     */
    @Override
    public void openSimulator(){
      
      stc = (SimulatorTopComponent)WindowManager.getDefault().findTopComponent("SimulatorTopComponent");
        if (stc !=null){
            stc.setMonitor(this);
            // Se não aberto, dito
            if (!stc.isOpened()){
                stc.open();
                stc.requestActive();
            }
            // Já está aberto , só revalide pois queremos somente um painel.
            stc.requestFocus();
            stc.revalidate();
        }     
        simulator_on=true;
    }
 
    
    
    /**
     * Abre terminal Centauro
     */
    public void openCentauro(){
      
      ctc = (CentauroTopComponent)WindowManager.getDefault().findTopComponent("centauroTopComponent");
        if (ctc !=null){
            ctc.setMonitor(this);
            // Se não aberto, dito
            if (!ctc.isOpened()){
                ctc.open();
                ctc.requestActive();
            }
            // Já está aberto , só revalide pois queremos somente um painel.
            ctc.requestFocus();
            ctc.revalidate();
        }
        ctc_inbuffer[0]=(byte)0xfe;
        ctc_inbuffer[1]=(byte)0xff;
        ctc.write("Iniciando Terminal...\r\n");
        ctc.write("CENTAURO>");
        
        centauro_on=true;
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
                    case 3:
                        renderTerminal (e.buffer, e.lenght);
                        break;
                    case 5: case 6: case 7:
                        //storeScan (cmd_type, e.buffer);
                        updateRun (cmd_type, e.buffer);
                        break;
                }
                
                break;
            case SerialDevice.Event.EVENT_OPEN:
                log.log(Level.INFO, "Porta Serial Aberta");
                break;
        }    
    }
   
    /**
     * Dispara update do display com widgets
     * @param type
     * @param buffer 
     */
    public void updateRun (int type, byte[] buffer){
        //System.out.println("Scan recebido @" + System.currentTimeMillis());   
        System.arraycopy(buffer, 1, burn, 0, 58);
        renderChannels();
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
     
    
    /**
     * Chaveia acionamento do timer de varredura
     */
    @Override
    public void toogleScan (){
         
        if (pool_on){    
            setName(getMonitorname() + " [parado]");
            pool_on = false;
            termSendCmd(4,"SCANOFF");
        }
        else{
            setName(getMonitorname() + " [rodando]");
            pool_on = true;
            termSendCmd(4,"SCANON");           
        }
        
        
    }
    
    
    // ==========================================================================================================================
    // Recepção e update dos canais de display
    // ==========================================================================================================================
    
     /**
     * Executa a atualização dos dados nos displays
     */
    public void renderChannels(){
        
        ChannelBlockInterface cb;
        long tstamp;
        int channel;
        int value;
        int dist;
        int binary;
  
        
        // Se chegamos aqui é porque o Jurid já estava mandando sinal ...
        //setName(getMonitorname() + " [rodando]");
        //pool_on = true;
        
        // A data stream abaixo é a melhor forma de capturar os timestamps de cada round
        //DataInputStream datain = new DataInputStream(new ByteArrayInputStream(burn));
//        try {
            // Recupere o Timestamp
            tstamp = System.currentTimeMillis();
            // Agora execute a varredura de updade com os dados do ultimo scan ja com o segmento no buffer ajustadinho
            int temp = getBlocks().size();
            for (int i = 0; i < getBlocks().size(); i++) {
                // Obtenha o canal binario
                binary = MonitorUtils.getUnsignedInt(burn, 44, MonitorUtils.endian.BIG);
                // Obtenha o Bloco
                cb=getBlocks().get(i);
                channel = cb.getCanal();
                //System.out.println("Renderchannels chamado no canal " + getCanal() + " com " + rawvalue);
                // Canais marcados como 0 ou negativos são calculados, deixe para depois do update dos canais de scan
                if ((channel <17 || channel >22) && !(channel<1) ){                      
                    // Canal de scan, calcule então offset no segmento ->
                    // 2 bytes para cada canal - compensação do array (que começa em 0) + offset do segmento
                    channel = ((channel-1)*2) + 8;  
                    // TODO - Resultado do scan é little endian ! 
                    value = MonitorUtils.getUnsignedInt(burn, channel, MonitorUtils.endian.BIG);
                    //System.out.println("canal : " + channel + " = " + value);
                    // Faça o bloco calcular o resultado e armazenar os rounds em seu buffer
                    //System.out.println("Renderchannels chamado !!");
                    cb.setValue(0, tstamp, value);
                }
                // Canal é o medidor de distancia ?
                else if (channel == 17 || channel == 18){
                    dist = MonitorUtils.getUnsignedInt(burn, 40, MonitorUtils.endian.BIG);
                    dist += MonitorUtils.getUnsignedInt(burn, 42, MonitorUtils.endian.BIG) * 65536;
                    cb.setValue(0, tstamp, dist);  
                }
                // Canais binarios
                else if (channel == 20){
                    cb.setValue(binary & 0x01); 
                }
                else if (channel == 21){
                    cb.setValue((binary & 0x02)>>1); 
                }
                else if (channel == 22){
                    cb.setValue((binary & 0x04)>>2); 
                }
                
            }  
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
        
        // Execute a varredura dos canais calculados
        for (int i = 0; i < getBlocks().size(); i++) {
            cb=getBlocks().get(i);
            if (cb.isCalc()) cb.calculateBlock();
        }      
        
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
        //if (!centauro_on) return;
        String temp = new String(buffer,1,lenght-2);
        if (ctc != null) {
            ctc.write(temp);
        }
        log.log(Level.INFO, "Mensagem da impressora Jurid ;", temp);
    }
    
  
    
    
    // ==========================================================================================================================
    //  GET/SET e auxiliares
    // ==========================================================================================================================
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
  
    }

    @Override
    public void componentClosed() {
        
        //TopComponent.Registry registry = TopComponent.getRegistry();   
        // Desligue o Jurid
        //termSendCmd(4,"SCANOFF");
       
        if (ctc != null) {          
            ctc.close();
        }
        
        log.info("Fechando Top Component Vega");
    }

     @Override
    public void requestFocus(){
        super.requestFocus();
        //int ret = loadFrame();
    }
    
    /**
     * @return the gp1
     */
    public GriddedPanel getGp1() {
        return gp1;
    }

    /**
     * @return the fillarea
     */
    public JPanel getFillarea() {
        return fillarea;
    }
    
    /**
     * @return the jsp
     */
    public JScrollPane getJsp() {
        return jsp;
    }

    /**
     * @return the blocks
     */
    public ArrayList<ChannelBlockInterface> getBlocks() {
        return blocks;
    }
      
    /**
     * 
     */
    @Override
    public void store() {

    }
    
    @Override
    public void scan() {   
    }
    
    @Override
    public void setPort() {
    }
    
    @Override
    public void reset() {   
    }

    @Override
    public void showTerminal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void showJurid() {
    }
       
    void writeProperties(java.util.Properties p) {
        
    }

    void readProperties(java.util.Properties p) {
      
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
    
    /**
     * @return the freshscan
     */
    public boolean isFreshscan() {
        return freshscan;
    }

    /**
     * @param freshscan the freshscan to set
     */
    public void setFreshscan(boolean freshscan) {
        this.freshscan = freshscan;
    }

    /**
     * @return the Monitorname
     */
    public String getMonitorname() {
        return Monitorname;
    }

    /**
     * @param Monitorname the Monitorname to set
     */
    public void setMonitorname(String Monitorname) {
        this.Monitorname = Monitorname;
    }

    /**
     * @return the pool_time
     */
    public int getPool_time() {
        return pool_time;
    }

    /**
     * @param pool_time the pool_time to set
     */
    public void setPool_time(int pool_time) {
        this.pool_time = pool_time;
    }
    
    
}