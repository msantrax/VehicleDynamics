/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opus;

/**
 *
 * @author opus
 */
public interface MDisplay {

    
    public void setValue(double value);
    public void setValue(String value);
    public void setHeader(String header);
    public void setFormat(String format);
    public void setUnit(String unit);
    
    public void commitDisplay();
    
}
