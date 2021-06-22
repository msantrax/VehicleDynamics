/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opus;

import java.awt.Font;
import javax.swing.JComponent;

/**
 *
 * @author opus
 */
public interface ChannelBlockInterface {

    /**
     * Executa update de um canal calculado
     */
    void calculateBlock();

    /**
     * Aplica a curva de calibração o os fatores de recalibração;
     */
    double calculateValue(int vin);

    JComponent commitChannel(String display_type, Font display_font);

    /**
     * @return the canal
     */
    int getCanal();

    /**
     * @return the display
     */
    JComponent getDisplay();

    /**
     * @return the mask
     */
    String getMask();

    /**
     * @return the max
     */
    double getMax();

    /**
     * @return the min
     */
    double getMin();

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the recaloff
     */
    double getRecaloff();

    /**
     * @return the recalslo
     */
    double getRecalslo();

    /**
     * Retorna o timestamp para um determinado round;
     * @param round
     *  Numero do round (de 0 a 15) no array de rounds
     * @return
     *  long com o timestamp
     */
    long getTimeStamp(int round);

    /**
     * @return the type
     */
    String getType();

    /**
     * @return the unit
     */
    String getUnit();

    /**
     * @return the value
     */
    double getValue();

    /**
     * @return the valuein
     */
    int getValuein();

    /**
     * Informa se o canal é calculado
     * @return
     */
    boolean isCalc();

    /**
     * @return the calculated
     */
    boolean isCalculated();

    boolean isDigital();

    /**
     * @return the ocult
     */
    boolean isOcult();

    /**
     * @return the storefield
     */
    boolean isStorefield();

    /**
     * @param a0 the a0 to set
     */
    void setA0(double a0);

    /**
     * @param a1 the a1 to set
     */
    void setA1(double a1);

    /**
     * @param a2 the a2 to set
     */
    void setA2(double a2);

    /**
     * @param a3 the a3 to set
     */
    void setA3(double a3);

    /**
     * @param a4 the a4 to set
     */
    void setA4(double a4);

    /**
     * @param a5 the a5 to set
     */
    void setA5(double a5);

    /**
     * @param calculated the calculated to set
     */
    void setCalculated(boolean calculated);

    /**
     * @param canal the canal to set
     */
    void setCanal(int canal);

    /**
     * @param desc the desc to set
     */
    void setDesc(String desc);

    /**
     * @param numeric the numeric to set
     */
    void setDigital(boolean digital);

    /**
     * @param display the display to set
     */
    void setDisplay(DisplayDefault display);

    /**
     * @param mask the mask to set
     */
    void setMask(String mask);

    /**
     * @param max the max to set
     */
    void setMax(double max);

    /**
     * @param min the min to set
     */
    void setMin(double min);

    /**
     * @param name the name to set
     */
    void setName(String name);

    /**
     * @param ocult the ocult to set
     */
    void setOcult(boolean ocult);

    /**
     * @param recaloff the recaloff to set
     */
    void setRecaloff(double recaloff);

    /**
     * @param recalslo the recalslo to set
     */
    void setRecalslo(double recalslo);

    /**
     * @param ringsize the ringsize to set
     */
    void setRingsize(int ringsize);

    /**
     * @param script the script to set
     */
    void setScript(String script);

    /**
     * @param storefield the storefield to set
     */
    void setStorefield(boolean storefield);

    /**
     * @param type the type to set
     */
    void setType(String type);

    /**
     * @param unit the unit to set
     */
    void setUnit(String unit);

    /**
     * Ajusta valor para canais numericos
     * @param valuein
     */
    void setValue(int round, long tstamp, int rawvalue);

    /** Ajusta valor para canais digitais
     * @param value the value to set
     */
    void setValue(double value);

    /**
     * Ajusta display diretamente.
     * @param valuein
     */
    void setValue(String valuein);

    /**
     * @param valuein the valuein to set
     */
    void setValuein(int valuein);
    
}
