/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opus;

/**
 *
 * @author opus
 */
public interface MonitorContext {
  
    public void scan();
    public void store();
    public void reset();
    public void showTerminal();
    public void showJurid();
    public void setPort();
    public void toogleScan();
    public void openSimulator();
        
}
