/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author opus
 */
public class poolTaskPerformer implements ActionListener{
  
    MonitorTopComponent mtc;
    
    public poolTaskPerformer(MonitorTopComponent mtc){
        this.mtc=mtc;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
            
            //System.out.println("Timer chamado @ " + System.currentTimeMillis()) 
            if (!mtc.isFreshscan()){
                //System.out.println("Scan requested @ " + System.currentTimeMillis());
                mtc.termSendCmd(4,"SCAN");
            }
            //mtc.renderChannels();
        }
    
    
}
