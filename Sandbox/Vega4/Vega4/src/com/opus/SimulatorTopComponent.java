/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opus;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Timer;
import javax.swing.border.Border;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;


/**
 * Top component which displays something.
 */
@ConvertAsProperties(
    dtd="-//com.opus//Simulator//EN",
    autostore=false
)
@TopComponent.Description(
    preferredID="SimulatorTopComponent", 
    iconBase="com/opus/clanbomber.png", 
    persistenceType=TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "com.opus.SimulatorTopComponent")
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
    displayName = "Simulador",
    preferredID="SimulatorTopComponent"
)
//@Messages({
//    "CTL_SimulatorAction=Simulator",
//    "CTL_SimulatorTopComponent=Simulator Window",
//    "HINT_SimulatorTopComponent=This is a Simulator window"
//})
public final class SimulatorTopComponent extends TopComponent{
    
    MonitorTopComponent mtc;
    
    private boolean running;
    private Timer simtimer;
    private double ref_counts;
    private byte[] burn;
    
    
    static Border run_border = BorderFactory.createLineBorder(Color.green, 3, true);
    static Border stop_border = BorderFactory.createLineBorder(Color.BLACK, 3, true);
    static Border record_border = BorderFactory.createLineBorder(Color.RED, 3, true);
    
    
    public SimulatorTopComponent() {
        initComponents();
        setName("Simulador");
        setToolTipText("Simulador de dados");
//	putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.TRUE);
//	putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
//	putClientProperty(TopComponent.PROP_SLIDING_DISABLED, Boolean.TRUE);
//	putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);

        putClientProperty(TopComponent.PROP_KEEP_PREFERRED_SIZE_WHEN_SLIDED_IN, Boolean.TRUE);
        
        running = false;
        simtimer = new Timer(100, taskPerformer);
        ref_counts=0;
        burn = new byte[80];
        
    }

    
    public void setMonitor (MonitorTopComponent mtc){
        this.mtc = mtc;
    }
    
    
    /**
     *  Temporizador de envio dos resultados
     */
    ActionListener taskPerformer = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
          
          long tstamp = System.currentTimeMillis();
          int channel;
          
          int lppkm = Integer.valueOf(ppkm.getText());
          int vel = Integer.valueOf(velocidade.getText());
          double lpooltime = Integer.valueOf(pooltime.getText());
          
          int pphr = lppkm * vel;
          double ppsl = pphr / (3600 / (lpooltime/1000));
          
          pulsos.setText(String.format("%2.1f", ppsl));
          ref_counts += ppsl;
          counts.setText(String.format("%5.0f", ref_counts));
          
          for (int i = 1; i < 20; i++) {         
              byte[]b = getchannel(i);
               channel = ((i)*2) + 7;
               burn[channel]=b[1];
               burn[channel+1]=b[0];
          }    
          mtc.updateRun(4, burn);
          
      }
    };

    /**
     * Obtem valor de um canal
     * @param channel
     * @return 
     */
    private byte[] getchannel(int channel){
        
        byte[] temp = new byte[2];
        int itemp=0;
        long ltemp;
        
        switch (channel){
            case 1:
                itemp = jSlider1.getValue();
                break;
            case 2:
                itemp = jSlider2.getValue();
                break;
            case 3:
                itemp = jSlider3.getValue();
                break;
            case 4:
                itemp = jSlider4.getValue();
                break;                 
            case 5:
                itemp = jSlider5.getValue();
                break;
           case 6:
                itemp = jSlider6.getValue();
                break;
           case 7:
                itemp = jSlider7.getValue();
                break;
           case 8:
                itemp = jSlider8.getValue();
                break;
           case 9:
                itemp = jSlider9.getValue();
                break;
           case 10:
                itemp = jSlider10.getValue();
                break;
           case 11:
                itemp = jSlider11.getValue();
                break;
           case 12:
                itemp = jSlider12.getValue();
                break;
           case 13:
                itemp = jSlider13.getValue();
                break;
           case 14:
                itemp = jSlider14.getValue();
                break;
           case 15:
                itemp = jSlider15.getValue();
                break;
           case 16:
                itemp = jSlider16.getValue();
                break;
           case 17:
                ltemp = Math.round(ref_counts);
                temp[0]=(byte)ltemp;
                ltemp=ltemp>>8;
                temp[1]=(byte)ltemp;
                return temp;
           case 18:
                ltemp = Math.round(ref_counts);
                ltemp=ltemp>>16;
                temp[0]=(byte)ltemp;
                ltemp=ltemp>>8;
                temp[1]=(byte)ltemp;
                return temp;
           case 19:
                itemp = 7;
                break;                                                            
        }
            
        temp[0]=(byte)itemp;
        itemp=itemp>>8;
        temp[1]=(byte)itemp;
        
        return temp;
        
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        cn1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSlider2 = new javax.swing.JSlider();
        cn2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jSlider3 = new javax.swing.JSlider();
        cn3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jSlider4 = new javax.swing.JSlider();
        cn4 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jSlider5 = new javax.swing.JSlider();
        cn5 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jSlider6 = new javax.swing.JSlider();
        cn6 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jSlider7 = new javax.swing.JSlider();
        cn7 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jSlider8 = new javax.swing.JSlider();
        cn8 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jSlider9 = new javax.swing.JSlider();
        cn9 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jSlider10 = new javax.swing.JSlider();
        cn10 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jSlider11 = new javax.swing.JSlider();
        cn11 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jSlider12 = new javax.swing.JSlider();
        cn12 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jSlider13 = new javax.swing.JSlider();
        cn13 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jSlider14 = new javax.swing.JSlider();
        cn14 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jSlider15 = new javax.swing.JSlider();
        cn15 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jSlider16 = new javax.swing.JSlider();
        cn16 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        ppkm = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jSlider17 = new javax.swing.JSlider();
        velocidade = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        pulsos = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        pooltime = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        counts = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        loadbt = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        gotostart = new javax.swing.JButton();
        pause = new javax.swing.JButton();
        play = new javax.swing.JButton();
        stop = new javax.swing.JButton();
        record = new javax.swing.JButton();
        runfile = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        Mark = new javax.swing.JCheckBox();
        Sep = new javax.swing.JCheckBox();
        Trigger = new javax.swing.JCheckBox();

        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 3, true));
        setPreferredSize(new java.awt.Dimension(891, 500));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel1.text")); // NOI18N
        jPanel4.add(jLabel1, java.awt.BorderLayout.NORTH);

        jSlider1.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider1.setMajorTickSpacing(1000);
        jSlider1.setMaximum(16387);
        jSlider1.setMinorTickSpacing(250);
        jSlider1.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider1.setPaintTicks(true);
        jSlider1.setValue(350);
        jPanel4.add(jSlider1, java.awt.BorderLayout.CENTER);

        cn1.setBackground(new java.awt.Color(255, 255, 255));
        cn1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn1.setMinimumSize(new java.awt.Dimension(50, 19));
        cn1.setOpaque(true);
        cn1.setPreferredSize(new java.awt.Dimension(50, 19));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider1, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel4.add(cn1, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel4);

        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel2.text")); // NOI18N
        jPanel5.add(jLabel2, java.awt.BorderLayout.NORTH);

        jSlider2.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider2.setMajorTickSpacing(1000);
        jSlider2.setMaximum(16387);
        jSlider2.setMinorTickSpacing(250);
        jSlider2.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider2.setPaintTicks(true);
        jSlider2.setValue(350);
        jPanel5.add(jSlider2, java.awt.BorderLayout.CENTER);

        cn2.setBackground(new java.awt.Color(255, 255, 255));
        cn2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn2.setMinimumSize(new java.awt.Dimension(50, 19));
        cn2.setOpaque(true);
        cn2.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider2, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel5.add(cn2, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel5);

        jPanel6.setLayout(new java.awt.BorderLayout());

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel3.text")); // NOI18N
        jPanel6.add(jLabel3, java.awt.BorderLayout.NORTH);

        jSlider3.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider3.setMajorTickSpacing(1000);
        jSlider3.setMaximum(16387);
        jSlider3.setMinorTickSpacing(250);
        jSlider3.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider3.setPaintTicks(true);
        jSlider3.setValue(350);
        jPanel6.add(jSlider3, java.awt.BorderLayout.CENTER);

        cn3.setBackground(new java.awt.Color(255, 255, 255));
        cn3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn3.setMinimumSize(new java.awt.Dimension(50, 19));
        cn3.setOpaque(true);
        cn3.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider3, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn3, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel6.add(cn3, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel6);

        jPanel7.setLayout(new java.awt.BorderLayout());

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel4.text")); // NOI18N
        jPanel7.add(jLabel4, java.awt.BorderLayout.NORTH);

        jSlider4.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider4.setMajorTickSpacing(1000);
        jSlider4.setMaximum(16387);
        jSlider4.setMinorTickSpacing(250);
        jSlider4.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider4.setPaintTicks(true);
        jSlider4.setValue(350);
        jPanel7.add(jSlider4, java.awt.BorderLayout.CENTER);

        cn4.setBackground(new java.awt.Color(255, 255, 255));
        cn4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn4.setMinimumSize(new java.awt.Dimension(50, 19));
        cn4.setOpaque(true);
        cn4.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider4, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn4, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel7.add(cn4, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel7);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel5.text")); // NOI18N
        jPanel8.add(jLabel5, java.awt.BorderLayout.NORTH);

        jSlider5.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider5.setMajorTickSpacing(1000);
        jSlider5.setMaximum(16387);
        jSlider5.setMinorTickSpacing(250);
        jSlider5.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider5.setPaintTicks(true);
        jSlider5.setValue(350);
        jPanel8.add(jSlider5, java.awt.BorderLayout.CENTER);

        cn5.setBackground(new java.awt.Color(255, 255, 255));
        cn5.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn5.setMinimumSize(new java.awt.Dimension(50, 19));
        cn5.setOpaque(true);
        cn5.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider5, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn5, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel8.add(cn5, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel8);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel6.text")); // NOI18N
        jPanel9.add(jLabel6, java.awt.BorderLayout.NORTH);

        jSlider6.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider6.setMajorTickSpacing(1000);
        jSlider6.setMaximum(16387);
        jSlider6.setMinorTickSpacing(250);
        jSlider6.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider6.setPaintTicks(true);
        jSlider6.setValue(350);
        jPanel9.add(jSlider6, java.awt.BorderLayout.CENTER);

        cn6.setBackground(new java.awt.Color(255, 255, 255));
        cn6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn6.setMinimumSize(new java.awt.Dimension(50, 19));
        cn6.setOpaque(true);
        cn6.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider6, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn6, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel9.add(cn6, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel9);

        jPanel10.setLayout(new java.awt.BorderLayout());

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel7.text")); // NOI18N
        jPanel10.add(jLabel7, java.awt.BorderLayout.NORTH);

        jSlider7.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider7.setMajorTickSpacing(1000);
        jSlider7.setMaximum(16387);
        jSlider7.setMinorTickSpacing(250);
        jSlider7.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider7.setPaintTicks(true);
        jSlider7.setValue(350);
        jPanel10.add(jSlider7, java.awt.BorderLayout.CENTER);

        cn7.setBackground(new java.awt.Color(255, 255, 255));
        cn7.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn7.setMinimumSize(new java.awt.Dimension(50, 19));
        cn7.setOpaque(true);
        cn7.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider7, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn7, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel10.add(cn7, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel10);

        jPanel11.setLayout(new java.awt.BorderLayout());

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel8.text")); // NOI18N
        jPanel11.add(jLabel8, java.awt.BorderLayout.NORTH);

        jSlider8.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider8.setMajorTickSpacing(1000);
        jSlider8.setMaximum(16387);
        jSlider8.setMinorTickSpacing(250);
        jSlider8.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider8.setPaintTicks(true);
        jSlider8.setValue(350);
        jPanel11.add(jSlider8, java.awt.BorderLayout.CENTER);

        cn8.setBackground(new java.awt.Color(255, 255, 255));
        cn8.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn8.setMinimumSize(new java.awt.Dimension(50, 19));
        cn8.setOpaque(true);
        cn8.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider8, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn8, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel11.add(cn8, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel11);

        jPanel12.setLayout(new java.awt.BorderLayout());

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel9.text")); // NOI18N
        jPanel12.add(jLabel9, java.awt.BorderLayout.NORTH);

        jSlider9.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider9.setMajorTickSpacing(1000);
        jSlider9.setMaximum(16387);
        jSlider9.setMinorTickSpacing(250);
        jSlider9.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider9.setPaintTicks(true);
        jSlider9.setValue(350);
        jPanel12.add(jSlider9, java.awt.BorderLayout.CENTER);

        cn9.setBackground(new java.awt.Color(255, 255, 255));
        cn9.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn9.setMinimumSize(new java.awt.Dimension(50, 19));
        cn9.setOpaque(true);
        cn9.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider9, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn9, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel12.add(cn9, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel12);

        jPanel13.setLayout(new java.awt.BorderLayout());

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel10.text")); // NOI18N
        jPanel13.add(jLabel10, java.awt.BorderLayout.NORTH);

        jSlider10.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider10.setMajorTickSpacing(1000);
        jSlider10.setMaximum(16387);
        jSlider10.setMinorTickSpacing(250);
        jSlider10.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider10.setPaintTicks(true);
        jSlider10.setValue(350);
        jPanel13.add(jSlider10, java.awt.BorderLayout.CENTER);

        cn10.setBackground(new java.awt.Color(255, 255, 255));
        cn10.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn10.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn10.setMinimumSize(new java.awt.Dimension(50, 19));
        cn10.setOpaque(true);
        cn10.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider10, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn10, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel13.add(cn10, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel13);

        jPanel14.setLayout(new java.awt.BorderLayout());

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel11.text")); // NOI18N
        jPanel14.add(jLabel11, java.awt.BorderLayout.NORTH);

        jSlider11.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider11.setMajorTickSpacing(1000);
        jSlider11.setMaximum(16387);
        jSlider11.setMinorTickSpacing(250);
        jSlider11.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider11.setPaintTicks(true);
        jSlider11.setValue(350);
        jPanel14.add(jSlider11, java.awt.BorderLayout.CENTER);

        cn11.setBackground(new java.awt.Color(255, 255, 255));
        cn11.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn11.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn11.setMinimumSize(new java.awt.Dimension(50, 19));
        cn11.setOpaque(true);
        cn11.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider11, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn11, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel14.add(cn11, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel14);

        jPanel15.setLayout(new java.awt.BorderLayout());

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel12.text")); // NOI18N
        jPanel15.add(jLabel12, java.awt.BorderLayout.NORTH);

        jSlider12.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider12.setMajorTickSpacing(1000);
        jSlider12.setMaximum(16387);
        jSlider12.setMinorTickSpacing(250);
        jSlider12.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider12.setPaintTicks(true);
        jSlider12.setValue(350);
        jPanel15.add(jSlider12, java.awt.BorderLayout.CENTER);

        cn12.setBackground(new java.awt.Color(255, 255, 255));
        cn12.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn12.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn12.setMinimumSize(new java.awt.Dimension(50, 19));
        cn12.setOpaque(true);
        cn12.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider12, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn12, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel15.add(cn12, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel15);

        jPanel16.setLayout(new java.awt.BorderLayout());

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel13.text")); // NOI18N
        jPanel16.add(jLabel13, java.awt.BorderLayout.NORTH);

        jSlider13.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider13.setMajorTickSpacing(1000);
        jSlider13.setMaximum(16387);
        jSlider13.setMinorTickSpacing(250);
        jSlider13.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider13.setPaintTicks(true);
        jSlider13.setValue(350);
        jPanel16.add(jSlider13, java.awt.BorderLayout.CENTER);

        cn13.setBackground(new java.awt.Color(255, 255, 255));
        cn13.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn13.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn13.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn13.setMinimumSize(new java.awt.Dimension(50, 19));
        cn13.setOpaque(true);
        cn13.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider13, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn13, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel16.add(cn13, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel16);

        jPanel17.setLayout(new java.awt.BorderLayout());

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel14, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel14.text")); // NOI18N
        jPanel17.add(jLabel14, java.awt.BorderLayout.NORTH);

        jSlider14.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider14.setMajorTickSpacing(1000);
        jSlider14.setMaximum(16387);
        jSlider14.setMinorTickSpacing(250);
        jSlider14.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider14.setPaintTicks(true);
        jSlider14.setValue(350);
        jPanel17.add(jSlider14, java.awt.BorderLayout.CENTER);

        cn14.setBackground(new java.awt.Color(255, 255, 255));
        cn14.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn14.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn14.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn14.setMinimumSize(new java.awt.Dimension(50, 19));
        cn14.setOpaque(true);
        cn14.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider14, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn14, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel17.add(cn14, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel17);

        jPanel18.setLayout(new java.awt.BorderLayout());

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel15, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel15.text")); // NOI18N
        jPanel18.add(jLabel15, java.awt.BorderLayout.NORTH);

        jSlider15.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider15.setMajorTickSpacing(1000);
        jSlider15.setMaximum(16387);
        jSlider15.setMinorTickSpacing(250);
        jSlider15.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider15.setPaintTicks(true);
        jSlider15.setValue(350);
        jPanel18.add(jSlider15, java.awt.BorderLayout.CENTER);

        cn15.setBackground(new java.awt.Color(255, 255, 255));
        cn15.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn15.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn15.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn15.setMinimumSize(new java.awt.Dimension(50, 19));
        cn15.setOpaque(true);
        cn15.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider15, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn15, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel18.add(cn15, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel18);

        jPanel19.setLayout(new java.awt.BorderLayout());

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel16, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel16.text")); // NOI18N
        jPanel19.add(jLabel16, java.awt.BorderLayout.NORTH);

        jSlider16.setFont(new java.awt.Font("Dialog", 0, 8)); // NOI18N
        jSlider16.setMajorTickSpacing(1000);
        jSlider16.setMaximum(16387);
        jSlider16.setMinorTickSpacing(250);
        jSlider16.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider16.setPaintTicks(true);
        jSlider16.setValue(350);
        jPanel19.add(jSlider16, java.awt.BorderLayout.CENTER);

        cn16.setBackground(new java.awt.Color(255, 255, 255));
        cn16.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cn16.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        cn16.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cn16.setMinimumSize(new java.awt.Dimension(50, 19));
        cn16.setOpaque(true);
        cn16.setPreferredSize(new java.awt.Dimension(50, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider16, org.jdesktop.beansbinding.ELProperty.create("${value}"), cn16, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel19.add(cn16, java.awt.BorderLayout.SOUTH);

        jPanel1.add(jPanel19);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        ppkm.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        ppkm.setText(org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.ppkm.text")); // NOI18N
        ppkm.setPreferredSize(new java.awt.Dimension(50, 19));
        jPanel3.add(ppkm);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel17, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel17.text")); // NOI18N
        jPanel3.add(jLabel17);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel18, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel18.text")); // NOI18N
        jPanel3.add(jLabel18);

        jSlider17.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jSlider17.setMajorTickSpacing(10);
        jSlider17.setMaximum(150);
        jSlider17.setMinorTickSpacing(5);
        jSlider17.setPaintTicks(true);
        jSlider17.setValue(10);
        jPanel3.add(jSlider17);

        velocidade.setBackground(new java.awt.Color(255, 255, 255));
        velocidade.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        velocidade.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        velocidade.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        velocidade.setMaximumSize(new java.awt.Dimension(40, 20));
        velocidade.setMinimumSize(new java.awt.Dimension(40, 20));
        velocidade.setOpaque(true);
        velocidade.setPreferredSize(new java.awt.Dimension(40, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider17, org.jdesktop.beansbinding.ELProperty.create("${value}"), velocidade, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel3.add(velocidade);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel24, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel24.text")); // NOI18N
        jPanel3.add(jLabel24);

        pulsos.setEditable(false);
        pulsos.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        pulsos.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        pulsos.setText(org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.pulsos.text")); // NOI18N
        pulsos.setMaximumSize(new java.awt.Dimension(30, 30));
        pulsos.setMinimumSize(new java.awt.Dimension(30, 30));
        pulsos.setPreferredSize(new java.awt.Dimension(50, 19));
        jPanel3.add(pulsos);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel19, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel19.text")); // NOI18N
        jLabel19.setToolTipText(org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel19.toolTipText")); // NOI18N
        jPanel3.add(jLabel19);

        pooltime.setText(org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.pooltime.text")); // NOI18N
        jPanel3.add(pooltime);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel20, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel20.text")); // NOI18N
        jLabel20.setToolTipText(org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel20.toolTipText")); // NOI18N
        jPanel3.add(jLabel20);

        jLabel21.setFont(new java.awt.Font("Dialog", 1, 8)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel21, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel21.text")); // NOI18N
        jLabel21.setToolTipText(org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel21.toolTipText")); // NOI18N
        jLabel21.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel3.add(jLabel21);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel22, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel22.text")); // NOI18N
        jLabel22.setToolTipText(org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel22.toolTipText")); // NOI18N
        jPanel3.add(jLabel22);

        counts.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        counts.setText(org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.counts.text")); // NOI18N
        counts.setToolTipText(org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.counts.toolTipText")); // NOI18N
        counts.setMaximumSize(new java.awt.Dimension(70, 20));
        counts.setMinimumSize(new java.awt.Dimension(70, 20));
        counts.setPreferredSize(new java.awt.Dimension(70, 20));
        counts.setRequestFocusEnabled(false);
        counts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                countsMouseClicked(evt);
            }
        });
        jPanel3.add(counts);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel23, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel23.text")); // NOI18N
        jLabel23.setToolTipText(org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.jLabel23.toolTipText")); // NOI18N
        jPanel3.add(jLabel23);

        add(jPanel3, java.awt.BorderLayout.SOUTH);

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        loadbt.setMnemonic('L');
        org.openide.awt.Mnemonics.setLocalizedText(loadbt, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.loadbt.text")); // NOI18N
        jPanel2.add(loadbt);

        jSeparator2.setPreferredSize(new java.awt.Dimension(20, 2));
        jPanel2.add(jSeparator2);

        gotostart.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/opus/player_start.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(gotostart, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.gotostart.text")); // NOI18N
        jPanel2.add(gotostart);

        pause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/opus/player_pause.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(pause, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.pause.text")); // NOI18N
        jPanel2.add(pause);

        play.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/opus/player_play.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(play, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.play.text")); // NOI18N
        play.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playActionPerformed(evt);
            }
        });
        jPanel2.add(play);

        stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/opus/player_stop.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(stop, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.stop.text")); // NOI18N
        jPanel2.add(stop);

        record.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/opus/ksame.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(record, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.record.text")); // NOI18N
        jPanel2.add(record);

        org.openide.awt.Mnemonics.setLocalizedText(runfile, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.runfile.text")); // NOI18N
        runfile.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        runfile.setMaximumSize(new java.awt.Dimension(150, 20));
        runfile.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel2.add(runfile);

        jSeparator3.setPreferredSize(new java.awt.Dimension(30, 2));
        jPanel2.add(jSeparator3);

        Mark.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        Mark.setMnemonic('M');
        org.openide.awt.Mnemonics.setLocalizedText(Mark, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.Mark.text")); // NOI18N
        jPanel2.add(Mark);

        Sep.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        Sep.setMnemonic('S');
        org.openide.awt.Mnemonics.setLocalizedText(Sep, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.Sep.text")); // NOI18N
        jPanel2.add(Sep);

        Trigger.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        Trigger.setMnemonic('T');
        org.openide.awt.Mnemonics.setLocalizedText(Trigger, org.openide.util.NbBundle.getMessage(SimulatorTopComponent.class, "SimulatorTopComponent.Trigger.text")); // NOI18N
        jPanel2.add(Trigger);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void countsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_countsMouseClicked
        if (evt.getClickCount()== 2){
            counts.setText("0");
        }
    }//GEN-LAST:event_countsMouseClicked

    private void playActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playActionPerformed
        if(running){
            simtimer.stop();
            this.setBorder(stop_border);
            running = false;
        }
        else{
            simtimer.start();
            this.setBorder(run_border);
            running=true;
        }
    }//GEN-LAST:event_playActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox Mark;
    private javax.swing.JCheckBox Sep;
    private javax.swing.JCheckBox Trigger;
    private javax.swing.JLabel cn1;
    private javax.swing.JLabel cn10;
    private javax.swing.JLabel cn11;
    private javax.swing.JLabel cn12;
    private javax.swing.JLabel cn13;
    private javax.swing.JLabel cn14;
    private javax.swing.JLabel cn15;
    private javax.swing.JLabel cn16;
    private javax.swing.JLabel cn2;
    private javax.swing.JLabel cn3;
    private javax.swing.JLabel cn4;
    private javax.swing.JLabel cn5;
    private javax.swing.JLabel cn6;
    private javax.swing.JLabel cn7;
    private javax.swing.JLabel cn8;
    private javax.swing.JLabel cn9;
    private javax.swing.JTextField counts;
    private javax.swing.JButton gotostart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider10;
    private javax.swing.JSlider jSlider11;
    private javax.swing.JSlider jSlider12;
    private javax.swing.JSlider jSlider13;
    private javax.swing.JSlider jSlider14;
    private javax.swing.JSlider jSlider15;
    private javax.swing.JSlider jSlider16;
    private javax.swing.JSlider jSlider17;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSlider3;
    private javax.swing.JSlider jSlider4;
    private javax.swing.JSlider jSlider5;
    private javax.swing.JSlider jSlider6;
    private javax.swing.JSlider jSlider7;
    private javax.swing.JSlider jSlider8;
    private javax.swing.JSlider jSlider9;
    private javax.swing.JButton loadbt;
    private javax.swing.JButton pause;
    private javax.swing.JButton play;
    private javax.swing.JTextField pooltime;
    private javax.swing.JTextField ppkm;
    private javax.swing.JTextField pulsos;
    private javax.swing.JButton record;
    private javax.swing.JLabel runfile;
    private javax.swing.JButton stop;
    private javax.swing.JLabel velocidade;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
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
}
