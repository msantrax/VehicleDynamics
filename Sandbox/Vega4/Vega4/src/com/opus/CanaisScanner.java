/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author opus
 */
public class CanaisScanner {
    
    
    private static final Logger log = Logger.getLogger(MonitorTopComponent.class.getName());  
    
    org.w3c.dom.Document document;
    MonitorTopComponent mtc;
    
    int row=0;
    int col=0;
    
    int maxrow=4;
    int maxcol=4;
    
    
    String display_type;
    Font display_font;
    
    int pn_font_size=14;
    int pn_font_style=Font.PLAIN;
    String pn_font_family="Dialog";
    
    

    /**
     * Create new CanaisScanner with org.w3c.dom.Document.
     */
    public CanaisScanner(org.w3c.dom.Document document, MonitorTopComponent mtc) {
        
        
        this.document = document;
        this.mtc=mtc;
    }

    /**
     * Scan through org.w3c.dom.Document document.
     */
    public void visitDocument() {
        org.w3c.dom.Element element = document.getDocumentElement();
        if ((element != null) && element.getTagName().equals("monitor")) {
            visitElement_monitor(element);
        }
        if ((element != null) && element.getTagName().equals("linha")) {
            visitElement_linha(element);
        }
        if ((element != null) && element.getTagName().equals("display")) {
            visitElement_display(element);
        }
        if ((element != null) && element.getTagName().equals("spacer")) {
            visitElement_display(element);
        }
    }

    /**
     * Scan through org.w3c.dom.Element named monitor.
     */
    void visitElement_monitor(org.w3c.dom.Element element) {
        // <monitor>
        // element.getValue();
        org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
            if (attr.getName().equals("nome")) {
                mtc.setMonitorname(attr.getValue());
            }
            if (attr.getName().equals("desc")) {
                mtc.setToolTipText(attr.getValue());
            }
            if (attr.getName().equals("bkg")) {
                mtc.getFillarea().setBackground(getColor(attr));
                mtc.getGp1().setBackground(getColor(attr));
            }
            if (attr.getName().equals("maxlin")) {
                maxrow= getIntAttr(attr, 10);
            }
            if (attr.getName().equals("maxcol")) {
                maxcol= getIntAttr(attr, 10);
            }
            
            
            if (attr.getName().equals("porta")) {
                mtc.setSdev_port(attr.getValue());
            }
            if (attr.getName().equals("baudrate")) {
                mtc.setSdev_baud(getIntAttr(attr, 10));
            }
            if (attr.getName().equals("databits")) {
                mtc.setSdev_databits(getIntAttr(attr, 10));
            }
            if (attr.getName().equals("stopbits")) {
                mtc.setSdev_stopbits(getIntAttr(attr, 10));
            }
            if (attr.getName().equals("paridade")) {
                mtc.setSdev_parity(attr.getValue());
            }
            if (attr.getName().equals("fluxo")) {
                mtc.setSdev_flow(attr.getValue());
            }
            
            if (attr.getName().equals("pool")) {
                mtc.setPool_time(getIntAttr(attr, 10));
            }
            
        }
        
        org.w3c.dom.NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
                    if (nodeElement.getTagName().equals("linha")) {
                        visitElement_linha(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("spacer")) {
                        visitElement_spacer(nodeElement);
                    }
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
            }
        }
        
         // Instale a area elástica / pane do registrador gráfico
        mtc.getGp1().addFilledComponent(mtc.getFillarea(), 2, 0, maxrow, maxcol, GridBagConstraints.BOTH);
        // Registre de novo os widgets
        mtc.getJsp().setViewportView(mtc.getGp1());
        
    }

    /**
     * Scan through org.w3c.dom.Element named linha.
     */
    void visitElement_spacer(org.w3c.dom.Element element) {
        
        int altura=120;
        boolean elastico = false;
        
        JPanel spacer = new JPanel();
        spacer.setOpaque(true);
        spacer.setBackground(mtc.getGp1().getBackground());
        //spacer.setBackground(Color.WHITE);
        
        
        org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
            if (attr.getName().equals("altura")) {
                altura=getIntAttr(attr, 10);
            }
            if (attr.getName().equals("borda")) {
                String borda=attr.getValue();
                
            }
            if (attr.getName().equals("elastico")) {
               elastico = attr.getValue().equals("sim");
            }
        }
       
        Dimension d = new Dimension(40, altura);
        spacer.setPreferredSize(d);
        
        mtc.getGp1().addFilledComponent(spacer, row, 0, maxcol, 1, 1);
        row++;
        
    }
    
    
    
    /**
     * Scan through org.w3c.dom.Element named linha.
     */
    void visitElement_linha(org.w3c.dom.Element element) {
        
        
        JLabel lbl = new JLabel();
        lbl.setOpaque(true);
        
        boolean visivel = true;
        //lbl.setBorder(BorderFactory.createBevelBorder(2));
        int hd_font_size=14;
        int hd_font_style=Font.PLAIN;
        String hd_font_family="Dialog";
        
        
        org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
            
            if (attr.getName().equals("visivel")) {
                if (attr.getValue().equals("nao")){
                    visivel = false;
                }
            }
            if (attr.getName().equals("display")) {
                display_type= attr.getValue();
            }
            if (attr.getName().equals("pn_tam")) {
                pn_font_size=getIntAttr(attr, 10);
            }
            if (attr.getName().equals("pn_tipo")) {
                pn_font_family=attr.getValue();
            }
            if (attr.getName().equals("pn_estilo")) {
                String estilo =attr.getValue();
                if (estilo.equals("negrito")) pn_font_style=Font.BOLD;
                else if (estilo.equals("italico")) pn_font_style=Font.ITALIC;
            }
            
            
            if (visivel){
                if (attr.getName().equals("nome")) {
                    lbl.setText(attr.getValue());
                }
                if (attr.getName().equals("desc")) {
                    lbl.setToolTipText(attr.getValue());
                }
                if (attr.getName().equals("bkg")) {
                    lbl.setBackground(getColor(attr));
                }
                if (attr.getName().equals("frg")) {
                    lbl.setForeground(getColor(attr));
                }
                if (attr.getName().equals("hd_tam")) {
                    hd_font_size=getIntAttr(attr, 10);
                }
                if (attr.getName().equals("hd_tipo")) {
                    hd_font_family=attr.getValue();
                }
                if (attr.getName().equals("hd_estilo")) {
                    String estilo =attr.getValue();
                    if (estilo.equals("negrito")) hd_font_style=Font.BOLD;
                    else if (estilo.equals("italico")) hd_font_style=Font.ITALIC;
                }
                
            }
            
        }
        
        display_font = new Font(pn_font_family, pn_font_style, pn_font_size);
        
        if (visivel){
            Font f = new Font(hd_font_family, hd_font_style, hd_font_size);
            lbl.setFont(f);
            mtc.getGp1().addComponent(lbl, row, col, 4,1);
            row++;
        }
        
        col=0;
        
        org.w3c.dom.NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
                    if (nodeElement.getTagName().equals("display")) {
                        visitElement_display(nodeElement);
                    }
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
            }
        }
        row++;
        col=0;
    }

    /**
     * Scan through org.w3c.dom.Element named display.
     */
    void visitElement_display(org.w3c.dom.Element element) {
     
        int i;
        int span=1;
        ChannelBlockInterface cb;
        cb = new ChannelBlock();
        
        org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
        // Localize primeiro a definição de widget
        for (i = 0; i < attrs.getLength(); i++) {
           org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i); 
            if (attr.getName().equals("widget")) {
                String attr_type = attr.getValue();
                if (attr_type.equals("velocidade")){
                    cb = new ChannelBlockVel();
                    display_type = "analogico";
                }
                else{           
                    display_type = attr.getValue();
                }
            }
        }
        
        for (i = 0; i < attrs.getLength(); i++) {
            org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
            
            if (attr.getName().equals("nome")) {
                 cb.setName(attr.getValue());
            }
            if (attr.getName().equals("canal")) {
                cb.setCanal(getIntAttr(attr, 10));
            }
            if (attr.getName().equals("desc")) {
                cb.setDesc(attr.getValue());
            }
            if (attr.getName().equals("script")) {
                cb.setScript(attr.getValue());
            }
            if (attr.getName().equals("tipo")) {
                cb.setType(attr.getValue());
            }
            if (attr.getName().equals("armazenar")) {
                cb.setStorefield(attr.getValue().equals("sim"));
            }
            if (attr.getName().equals("span")) {
                    span=getIntAttr(attr, 10);
            }
            
            //if (display_type.equals("analogico")){
                if (attr.getName().equals("unidade")) {
                    cb.setUnit(attr.getValue());
                }
                if (attr.getName().equals("mascara")) {
                    cb.setMask(attr.getValue());
                }
                if (attr.getName().equals("a0")) {
                    cb.setA0(getDoubleAttr(attr));
                }
                if (attr.getName().equals("a1")) {
                    cb.setA1(getDoubleAttr(attr));
                }
                if (attr.getName().equals("a2")) {
                    cb.setA2(getDoubleAttr(attr));
                }
                if (attr.getName().equals("a3")) {
                    cb.setA3(getDoubleAttr(attr));
                }
                if (attr.getName().equals("a4")) {
                    cb.setA4(getDoubleAttr(attr));
                }
                if (attr.getName().equals("a5")) {
                    cb.setA5(getDoubleAttr(attr));
                }
                if (attr.getName().equals("mascara")) {
                   cb.setMask(attr.getValue());
                }
                if (attr.getName().equals("maximo")) {
                    cb.setMax(getDoubleAttr(attr));
                }
                if (attr.getName().equals("minimo")) {
                    cb.setMin(getDoubleAttr(attr));
                }
                if (attr.getName().equals("roffset")) {
                    cb.setRecaloff(getDoubleAttr(attr));
                }
                if (attr.getName().equals("rslope")) {
                     cb.setRecalslo(getDoubleAttr(attr));
                }
                if (attr.getName().equals("ring")) {
                     cb.setRingsize(getIntAttr(attr, 10));
                }
           // }
        }
        
        
        mtc.getGp1().addFilledComponent(cb.commitChannel(display_type, display_font), row, col, span, 1, 1);
        col+=span;
        mtc.getBlocks().add(cb);
        
        org.w3c.dom.NodeList nodes = element.getChildNodes();
        for (i = 0; i < nodes.getLength(); i++) {
            org.w3c.dom.Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    // ((org.w3c.dom.CDATASection)node).getData();
                    break;
                case org.w3c.dom.Node.ELEMENT_NODE:
                    org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
                    break;
                case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                    // ((org.w3c.dom.ProcessingInstruction)node).getTarget();
                    // ((org.w3c.dom.ProcessingInstruction)node).getData();
                    break;
                case org.w3c.dom.Node.TEXT_NODE:
                    // ((org.w3c.dom.Text)node).getData();
                    break;
            }
        }
    }
    
    
    /**
     * Mostra dialog box com erro do config e loga 
     * @param message 
     */
    private void showConfigError(String message){
        
        log.log(Level.SEVERE, "Erro no Parser XML : {0}", message);
        
        NotifyDescriptor d = new NotifyDescriptor(message,"Parser XML",
                            NotifyDescriptor.DEFAULT_OPTION,
                            NotifyDescriptor.ERROR_MESSAGE,
                            null,null);
        DialogDisplayer.getDefault().notify(d);
    }
    
    /**
     * Interpreta a cor do atributo
     * @param attr
     *  String com o atributo de cor
     * @return
     *  Color para uso
     */
    private Color getColor(org.w3c.dom.Attr attr){       
        return new Color(getIntAttr(attr, 16));
    }
    
    
    /**
     * Interpreta attributo para inteiro
     * @param attr
     * @return 
     */
    private int getIntAttr(org.w3c.dom.Attr attr, int radix){
        
        int cl;
        try{
            cl = Integer.parseInt(attr.getValue(), radix);
        } catch (NumberFormatException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append("Erro na conversão de numero inteiro :\nAtributo :");
            sb.append(attr.getName()).append(" = ").append(attr.getValue());
            sb.append(" - Radix = ").append(radix);
            sb.append("\nAjustando valor para zero...");
            showConfigError(sb.toString());    
            cl=0;
        }
        return cl;     
    }  
    
    /**
     * Interpreta atrilbuto para Double
     * @param attr 
     */
    private double getDoubleAttr (org.w3c.dom.Attr attr){
        
        double db;
        
        String sattr = attr.getValue();
        sattr=sattr.replace(',', '.');
        
        try{
            db = Double.valueOf(sattr);
        } catch (NumberFormatException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append("Erro na conversão de ponto flutuante :\nAtributo :");
            sb.append(attr.getName()).append(" = ").append(attr.getValue());
            sb.append("\nAjustando valor para zero...");
            showConfigError(sb.toString());    
            db=0;
        }
        
        return db;
                
    }
    
}
