/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DisplayDefault.java
 *
 * Created on Dec 13, 2011, 10:44:39 PM
 */
package com.opus;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 *
 * @author antrax
 */
public class DisplayTimer extends javax.swing.JPanel implements MDisplay{

    String header;
    String format;
    String unit;
    Font display_font;
    String timer_type;
     
    long start_time;
    GregorianCalendar this_time;
    //Calendar now_time;
    double last_value;
    
    /** Creates new form DisplayDefault */
    public DisplayTimer() {
        
        initComponents();
    }

    
    public DisplayTimer(String header, String format, String unit, Font display_font) {
        
        this.header=header;
        if (format.equals("##0.0")){
            this.format= "fall;12;%1$tH:%1$tM:%1$tS.%1$tL";
        }
        else{
            this.format=format;
        }
        
        this.unit=unit;
        this.display_font=display_font;
        initComponents();
        start_time = System.currentTimeMillis();
        this_time = new GregorianCalendar();
    }
  
    
    @Override
    public void commitDisplay(){
        
        FontMetrics metrics;
        Dimension v_size, u_size, out_size, pn_size;
        int hgt, adv;
        
        String[] splits = format.split(";");
        timer_type = splits[0].toUpperCase();
        int value_lenght=Integer.valueOf(splits[1]);
        format = splits[2];
        
        
        if (value_lenght>12){
            float font_size = display_font.getSize();
            display_font = display_font.deriveFont(font_size-2);
        }
        // Ajuste o widget do valor
        // Configure a fonte utilizada
        VarValue.setFont(display_font);       
        // Calcule o tamanho do widget para o valor a mostrar
        metrics = VarValue.getFontMetrics(display_font);
        hgt = metrics.getHeight()+2;
        
        adv = metrics.stringWidth(new String(new char[value_lenght]))+20;
        v_size = new Dimension(adv, hgt);
        VarValue.setPreferredSize(v_size);
          
        out_size = new Dimension(v_size.width, hgt);
        out_panel.setPreferredSize(out_size);
        
        // Ajuste o header
        v_size = new Dimension(out_size.width, hgt/2);
        Header.setPreferredSize(v_size);
        
        pn_size = new Dimension(out_size.width, out_size.height + v_size.height);
        this.setPreferredSize(pn_size);       
        
        last_value=1;
        setValue(1);    
    }
    
    @Override
    public void setValue(double value){     
      
        if (timer_type.equals("CLOCK")){
            if (value == 1){
                this_time.setTimeInMillis(System.currentTimeMillis());
                String temp = String.format(format, this_time);
                VarValue.setText(temp);
            }
        }
        else if (timer_type.equals("ON")){
            if (value == 1){
                this_time.setTimeInMillis(System.currentTimeMillis()- start_time);
                this_time.add(Calendar.HOUR_OF_DAY, -21);
                String temp = String.format(format, this_time);
                VarValue.setText(temp);
            }
            else  if (value == -1){
                start_time= System.currentTimeMillis();
                this_time.setTimeInMillis(0);
                this_time.add(Calendar.HOUR_OF_DAY, -21);
                String temp = String.format(format, this_time);
                VarValue.setText(temp);
            }
        }
        else if (timer_type.equals("OFF")){
            if (value == 0){
                this_time.setTimeInMillis(System.currentTimeMillis()- start_time);
                this_time.add(Calendar.HOUR_OF_DAY, -21);
                String temp = String.format(format, this_time);
                VarValue.setText(temp);
            }
            else  if (value == -1){
                start_time= System.currentTimeMillis();
                this_time.setTimeInMillis(0);
                this_time.add(Calendar.HOUR_OF_DAY, -21);
                String temp = String.format(format, this_time);
                VarValue.setText(temp);
            }
        }
        else if (timer_type.equals("RAISE")){
            if (value == 1){
                if (last_value==0){
                    start_time= System.currentTimeMillis();
                    last_value = 1;
                }
            }
            last_value = value;
            this_time.setTimeInMillis(System.currentTimeMillis()- start_time);
            this_time.add(Calendar.HOUR_OF_DAY, -21);
            String temp = String.format(format, this_time);
            VarValue.setText(temp);
        }
        else if (timer_type.equals("FALL")){
            if (value == 0){
                if (last_value==1){
                    start_time= System.currentTimeMillis();
                    last_value = 0;
                }
            }
            last_value = value;
            this_time.setTimeInMillis(System.currentTimeMillis()- start_time);
            this_time.add(Calendar.HOUR_OF_DAY, -21);
            String temp = String.format(format, this_time);
            VarValue.setText(temp);
        }
    }
    
    @Override
    public void setValue(String value){     
        VarValue.setText(value);       
    }
  
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Header = new javax.swing.JLabel();
        out_panel = new javax.swing.JPanel();
        VarValue = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(200, 60));
        setPreferredSize(new java.awt.Dimension(200, 60));
        setLayout(new java.awt.BorderLayout());

        Header.setBackground(new java.awt.Color(255, 255, 255));
        Header.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        Header.setForeground(new java.awt.Color(0, 0, 0));
        Header.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Header.setText(getHeader());
        Header.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        Header.setFocusable(false);
        Header.setInheritsPopupMenu(false);
        Header.setOpaque(true);
        Header.setRequestFocusEnabled(false);
        Header.setVerifyInputWhenFocusTarget(false);
        add(Header, java.awt.BorderLayout.SOUTH);

        out_panel.setBackground(new java.awt.Color(255, 255, 255));
        out_panel.setForeground(new java.awt.Color(255, 255, 255));
        out_panel.setLayout(new java.awt.BorderLayout());

        VarValue.setBackground(new java.awt.Color(255, 255, 255));
        VarValue.setFont(new java.awt.Font("Dialog", 1, 20)); // NOI18N
        VarValue.setForeground(new java.awt.Color(0, 0, 0));
        VarValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        VarValue.setText(org.openide.util.NbBundle.getMessage(DisplayTimer.class, "DisplayTimer.VarValue.text")); // NOI18N
        VarValue.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        VarValue.setFocusable(false);
        VarValue.setInheritsPopupMenu(false);
        VarValue.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        VarValue.setMinimumSize(new java.awt.Dimension(0, 0));
        VarValue.setOpaque(true);
        VarValue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                VarValueMouseClicked(evt);
            }
        });
        out_panel.add(VarValue, java.awt.BorderLayout.CENTER);

        add(out_panel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void VarValueMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_VarValueMouseClicked
        setValue(-1.0);
    }//GEN-LAST:event_VarValueMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Header;
    private javax.swing.JLabel VarValue;
    private javax.swing.JPanel out_panel;
    // End of variables declaration//GEN-END:variables

    public String getHeader() {
        return header;
    }

    @Override
    public void setHeader(String header) {
        this.header = header;
        Header.setText(header);
    }

    public String getFormat() {
        return format;
    }

    /**
     *
     * @param format
     */
    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    public String getUnit() {
        return unit;
    }

    /**
     *
     * @param unit
     */
    @Override
    public void setUnit(String unit) {
        
    }

    
}
