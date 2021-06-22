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
public class ChannelBlockVel implements ChannelBlockInterface  {

    private static final Logger log = Logger.getLogger(ChannelBlockVel.class.getName());
 
    private static LinkedHashMap<String,ChannelBlockVel> friends = new LinkedHashMap();
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
    private long [] []rounds;
    private int round_ptr;
    
    
    private String script;
    private boolean scripterror = false;
   
    
    public ChannelBlockVel (){
       
        rounds = new long[10][2];
        round_ptr=0;
        
    }
    
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
    
        double cpts;
        int ring_ptr;
        long ltstamp;
        long lvalue;
        
        //this.setValuein(rawvalue);
        // Marque o slot com os valores brutos
        rounds[round_ptr] [0] = tstamp;
        rounds[round_ptr] [1] = rawvalue;      
        
        if (round_ptr==9){
            ring_ptr=0;
        }
        else{
            ring_ptr = (round_ptr-10) + 11;
        }
        ltstamp = rounds[ring_ptr][0];
        
        //System.out.println(tstamp + " " + ltstamp + " : " + round_ptr + "/"+ ring_ptr);

        if (ltstamp == 0 || ltstamp>=tstamp){
            setValue(0);
        }
        else{
            lvalue = rounds[round_ptr][1] - rounds[ring_ptr][1];
            ltstamp = rounds[round_ptr][0] - rounds[ring_ptr][0];
            cpts=(double)lvalue/(double)ltstamp;
            //System.out.println(" tdiff :" + ltstamp + "  value diff " + lvalue + " cpts : " + cpts);
            cpts = calculateValue(cpts);
            setValue(cpts);
        }    
        round_ptr++;
        if (round_ptr==rounds.length) round_ptr=0;
        
    }
    
    /** Ajusta valor para canais digitais
     * @param value the value to set
     */
    public void setValue(double value) {
        this.value=value;
        display.setValue(value);
    }
    
    
   
    /**
     * Ajusta display diretamente.
     * @param valuein 
     */
    public void setValue(String valuein){      
        svalue=valuein;
        display.setValue(svalue); 
    }  
    
    
    /**
     * Aplica a curva de calibração o os fatores de recalibração;
     */
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
     * Aplica a curva de calibração o os fatores de recalibração;
     */
    public double calculateValue(double vin){
        
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
    public long getTimeStamp (int round) { return (long)rounds[round][0];};
   
    
    
    
    /**
     * Executa update de um canal calculado
     */
    public void calculateBlock(){
            
    }
    
    /**
     * @return the display
     */
    public JComponent getDisplay() {
        return (JComponent)display;
    }

    /**
     * @param display the display to set
     */
    public void setDisplay(DisplayDefault display) {
        this.display = display;
    }
    
     /**
     * @param numeric the numeric to set
     */
    public void setDigital(boolean digital) {
        this.digital = digital;
    }
    
    public boolean isDigital(){
        return digital;
    }

    /**
     * @return the valuein
     */
    public int getValuein() {
        return valuein;
    }

    /**
     * @param valuein the valuein to set
     */
    public void setValuein(int valuein) {
        this.valuein = valuein;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    
    /**
     * @return the canal
     */
    public int getCanal() {
        return canal;
    }

    /**
     * @param canal the canal to set
     */
    public void setCanal(int canal) {
        this.canal = canal;
        if (canal > 100) digital=true;
    }

    /**
     * @return the ocult
     */
    public boolean isOcult() {
        return ocult;
    }

    /**
     * @param ocult the ocult to set
     */
    public void setOcult(boolean ocult) {
        this.ocult = ocult;
    }
    
    /**
     * Informa se o canal é calculado
     * @return 
     */
    public boolean isCalc(){
        return (type.startsWith("C"));
        
    }

    /**
     * @return the recaloff
     */
    public double getRecaloff() {
        return recaloff;
    }

    /**
     * @param recaloff the recaloff to set
     */
    public void setRecaloff(double recaloff) {
        this.recaloff = recaloff;
    }

    /**
     * @return the recalslo
     */
    public double getRecalslo() {
        return recalslo;
    }

    /**
     * @param recalslo the recalslo to set
     */
    public void setRecalslo(double recalslo) {
        this.recalslo = recalslo;
    }

    /**
     * @param ringsize the ringsize to set
     */
    public void setRingsize(int ringsize) {
        this.ringsize = ringsize;
        //rounds = new long[ringsize] [1];
    }

    /**
     * @param a0 the a0 to set
     */
    public void setA0(double a0) {
        this.a0 = a0;
    }

    /**
     * @param a1 the a1 to set
     */
    public void setA1(double a1) {
        this.a1 = a1;
    }

    /**
     * @param a2 the a2 to set
     */
    public void setA2(double a2) {
        this.a2 = a2;
    }

    /**
     * @param a3 the a3 to set
     */
    public void setA3(double a3) {
        this.a3 = a3;
    }

    /**
     * @param a4 the a4 to set
     */
    public void setA4(double a4) {
        this.a4 = a4;
    }

    /**
     * @param a5 the a5 to set
     */
    public void setA5(double a5) {
        this.a5 = a5;
    }

    /**
     * @param script the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the mask
     */
    public String getMask() {
        return mask;
    }

    /**
     * @param mask the mask to set
     */
    public void setMask(String mask) {
        this.mask = mask;
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return the max
     */
    public double getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * @return the min
     */
    public double getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
        ocult=type.endsWith("O");
        calculated=type.startsWith("C");
    }

    /**
     * @return the calculated
     */
    public boolean isCalculated() {
        return calculated;
    }

    /**
     * @param calculated the calculated to set
     */
    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }

    /**
     * @return the storefield
     */
    public boolean isStorefield() {
        return storefield;
    }

    /**
     * @param storefield the storefield to set
     */
    public void setStorefield(boolean storefield) {
        this.storefield = storefield;
    }
}
