/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

@ActionID(
        category = "File",
        id = "com.opus.OpenCentauroAction")
@ActionRegistration(
        iconBase = "com/opus/openterm.png",
        displayName = "#CTL_OpenCentauroAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1020),
    @ActionReference(path = "Shortcuts", name = "F4")
})
@Messages("CTL_OpenCentauroAction=Abrir Terminal Centauro")
public final class OpenCentauroAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        MonitorTopComponent mtc = (MonitorTopComponent)WindowManager.getDefault().findTopComponent("MonitorTopComponent");
        Mode m = WindowManager.getDefault().findMode("editor");  
        if (mtc !=null && m.getSelectedTopComponent() instanceof MonitorTopComponent) {
            mtc.openCentauro();
        }
        
    }
}
