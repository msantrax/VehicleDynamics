/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opus;

import java.awt.Font;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JComponent;

/**
 *
 * @author opus
 */
public class ChannelBlock implements ChannelBlockInterface {

    private static final Logger log = Logger.getLogger(ChannelBlock.class.getName());
 
    private static LinkedHashMap<String,ChannelBlock> friends = new LinkedHashMap();
    private static ScriptEngineManager smanager = new ScriptEngineManager() ; 

    
    public static enum status {ON,OFF,ERRO, BLINK};
    
    private String name;
    private String mask;
    private String unit;
    private String desc;
    private String type;
   
    
    private double a0;
    private double a1;
    private double a2;
    private double a3;
    private double a4;
    private double a5;
    private double recaloff;
    private double recalslo;
    
    private int valuein;
    private double value;
    private String svalue;
    
    private int canal;
    private double max;
    private double min;
    
    private boolean digital = false;
    private boolean ocult = false;
    private boolean calculated = false;
    private boolean storefield = false;
    
    private MDisplay display;
    
    private int ringsize;
    private double[][]rounds;
    
    
    private String script;
    private boolean scripterror = false;
   
    
    public ChannelBlock (){
       
    }
    
    @Override
    public JComponent commitChannel(String display_type, Font display_font){
        
        //unit = '\u00B0'+unit;
        
        if (display_type.equals("digital")){
            display = new DisplayDigital (name, mask);     
        }
        else if (display_type.equals("timer")){
            display = new DisplayTimer (name, mask, unit, display_font); 
        }
        else if (display_type.equals("contador")){
            display = new DisplayCounter (name, mask, unit, display_font); 
        }
        else{
            display = new DisplayDefault (name, mask, unit, display_font); 
        }
        
        display.commitDisplay();
        friends.put(filteredName(), this);
        return (JComponent)display;
    }
    
    
    /**
     * Filtra caracteres não compatíveis do nome do widget
     * @return 
     *  String com o nome ok para processamento
     */
    private String filteredName(){
        
        String fname = name;
        
        // Filtre ruídos e normalize o nome    
        fname = fname.toUpperCase().trim();
        fname = fname.replace(" ", "_");
        fname = fname.replace(".", "");
        fname = fname.replace("'", "");
        fname = fname.replace(":", "_");
        
        fname = fname.replace("Ã", "A");
        fname = fname.replace("Õ", "O");
        fname = fname.replace("Á", "A");
        fname = fname.replace("Ó", "O");
        fname = fname.replace("Â", "A");
        fname = fname.replace("Õ", "O");
        fname = fname.replace("É", "E");
        fname = fname.replace("Ê", "E");
        fname = fname.replace("Ç", "C");
        
        return fname;
        
    }
    
    /**
     * Ajusta valor para canais numericos
     * @param valuein 
     */
    @Override
    public void setValue (int round, long tstamp, int rawvalue){      
    
        this.setValuein(rawvalue);
        // Marque o slot com os valors brutos
        rounds[round] [0] = tstamp;
        rounds[round] [1] = rawvalue;      
        // Agora calcule e faça o update do valor real.
        value = calculateValue(rawvalue);
        rounds[round] [2] = value; 
        // Update o display para esse round;
        if (round == 0) {           
            display.setValue(value);
        } 
        
    }
    
    /** Ajusta valor para canais digitais
     * @param value the value to set
     */
    @Override
    public void setValue(double value) {
        this.value=value;
        display.setValue(value);
    }
    
    
   
    /**
     * Ajusta display diretamente.
     * @param valuein 
     */
    @Override
    public void setValue(String valuein){      
        svalue=valuein;
        display.setValue(svalue); 
    }  
    
    
    /**
     * Aplica a curva de calibração o os fatores de recalibração;
     */
    @Override
    public double calculateValue(int vin){
        
        double temp = 0;
        // Curva de calibração
        temp = a0 + (a1 * vin);
        if (a2 !=0 ) temp += a2 * (Math.pow(vin, 2));
        if (a3 !=0 ) temp += a3 * (Math.pow(vin, 3));
        if (a4 !=0 ) temp += a4 * (Math.pow(vin, 4));
        if (a5 !=0 ) temp += a5 * (Math.pow(vin, 5));
        // Recal offset e slope
        temp = getRecaloff() + (temp * getRecalslo());
        return temp;        
    }
   
    
    /**
     * Retorna o timestamp para um determinado round; 
     * @param round
     *  Numero do round (de 0 a 15) no array de rounds
     * @return 
     *  long com o timestamp
     */
    @Override
    public long getTimeStamp (int round) { return (long)rounds[round][0];};
   
    
       
     
    
    
    /**
     * Executa update de um canal calculado
     */
    @Override
    public void calculateBlock(){
            
        final String ERRO = "!Erro!"; 
        final String VAZIO = "-//-"; 
        
        // Nada a fazer se o script está vazio com erro
        if (script.equals(" ") || scripterror){
            value=0;
            // Inofrme no widget a condição
            setValue(scripterror ? ERRO : VAZIO);
            return;
        }
        
        ScriptEngine engine = smanager.getEngineByName("JavaScript");
        //int friendsnum = friends.size();
        double saida=2;
        //int pointer=0;
        //double values[] = new double[friendsnum];
        Set<String> fset = friends.keySet();
        for (Iterator i=fset.iterator(); i.hasNext();){
            String channelkey = (String)i.next();
            //values[pointer]=friends.get(channelkey).getValue();
            engine.put(channelkey,friends.get(channelkey).getValue());
            engine.put(channelkey.toLowerCase(),friends.get(channelkey));
            //pointer++;
        }
        engine.put("saida", saida);
        engine.put("canal", this);
//        engine.put("ON", status.ON);
//        engine.put("OFF", status.OFF);
//        engine.put("ERRO", status.ERRO);
//        engine.put("BLINK", status.BLINK);
              
        try {
            engine.eval(script);
            saida=(Double)(engine.get("saida"));
            value=saida;
            //boolean setled = (saida > 300) ? true : false;
            //display.setUpperLed(setled); 
            setValue(String.valueOf(saida));
        } catch (ScriptException ex) {
            log.log(Level.WARNING, "Erro no script : {0}", ex.getMessage());
            setValue(ERRO);
            scripterror=true;
        } 
    }
    
    
    /**
     * @return the display
     */
    @Override
    public JComponent getDisplay() {
        return (JComponent)display;
    }

    /**
     * @param display the display to set
     */
    @Override
    public void setDisplay(DisplayDefault display) {
        this.display = display;
    }
    
     /**
     * @param numeric the numeric to set
     */
    @Override
    public void setDigital(boolean digital) {
        this.digital = digital;
    }
    
    @Override
    public boolean isDigital(){
        return digital;
    }

    /**
     * @return the valuein
     */
    @Override
    public int getValuein() {
        return valuein;
    }

    /**
     * @param valuein the valuein to set
     */
    @Override
    public void setValuein(int valuein) {
        this.valuein = valuein;
    }

    /**
     * @return the value
     */
    @Override
    public double getValue() {
        return value;
    }

    
    /**
     * @return the canal
     */
    @Override
    public int getCanal() {
        return canal;
    }

    /**
     * @param canal the canal to set
     */
    @Override
    public void setCanal(int canal) {
        this.canal = canal;
        if (canal > 100) digital=true;
    }

    /**
     * @return the ocult
     */
    @Override
    public boolean isOcult() {
        return ocult;
    }

    /**
     * @param ocult the ocult to set
     */
    @Override
    public void setOcult(boolean ocult) {
        this.ocult = ocult;
    }
    
    /**
     * Informa se o canal é calculado
     * @return 
     */
    @Override
    public boolean isCalc(){
        return (type.startsWith("C"));
        
    }

    /**
     * @return the recaloff
     */
    @Override
    public double getRecaloff() {
        return recaloff;
    }

    /**
     * @param recaloff the recaloff to set
     */
    @Override
    public void setRecaloff(double recaloff) {
        this.recaloff = recaloff;
    }

    /**
     * @return the recalslo
     */
    @Override
    public double getRecalslo() {
        return recalslo;
    }

    /**
     * @param recalslo the recalslo to set
     */
    @Override
    public void setRecalslo(double recalslo) {
        this.recalslo = recalslo;
    }

    /**
     * @param ringsize the ringsize to set
     */
    @Override
    public void setRingsize(int ringsize) {
        this.ringsize = ringsize;
        rounds = new double[ringsize] [3];
    }

    /**
     * @param a0 the a0 to set
     */
    @Override
    public void setA0(double a0) {
        this.a0 = a0;
    }

    /**
     * @param a1 the a1 to set
     */
    @Override
    public void setA1(double a1) {
        this.a1 = a1;
    }

    /**
     * @param a2 the a2 to set
     */
    @Override
    public void setA2(double a2) {
        this.a2 = a2;
    }

    /**
     * @param a3 the a3 to set
     */
    @Override
    public void setA3(double a3) {
        this.a3 = a3;
    }

    /**
     * @param a4 the a4 to set
     */
    @Override
    public void setA4(double a4) {
        this.a4 = a4;
    }

    /**
     * @param a5 the a5 to set
     */
    @Override
    public void setA5(double a5) {
        this.a5 = a5;
    }

    /**
     * @param script the script to set
     */
    @Override
    public void setScript(String script) {
        this.script = script;
    }
    
    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the mask
     */
    @Override
    public String getMask() {
        return mask;
    }

    /**
     * @param mask the mask to set
     */
    @Override
    public void setMask(String mask) {
        this.mask = mask;
    }

    /**
     * @return the unit
     */
    @Override
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @param desc the desc to set
     */
    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return the max
     */
    @Override
    public double getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    @Override
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * @return the min
     */
    @Override
    public double getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    @Override
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * @return the type
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    @Override
    public void setType(String type) {
        this.type = type;
        ocult=type.endsWith("O");
        calculated=type.startsWith("C");
    }

    /**
     * @return the calculated
     */
    @Override
    public boolean isCalculated() {
        return calculated;
    }

    /**
     * @param calculated the calculated to set
     */
    @Override
    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    /**
     * @return the storefield
     */
    @Override
    public boolean isStorefield() {
        return storefield;
    }

    /**
     * @param storefield the storefield to set
     */
    @Override
    public void setStorefield(boolean storefield) {
        this.storefield = storefield;
    }
}
