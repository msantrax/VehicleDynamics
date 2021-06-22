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
import org.openide.windows.WindowManager;

@ActionID(
        category = "File",
        id = "com.opus.ScanAction")
@ActionRegistration(
        iconBase = "com/opus/player_play.png",
        displayName = "#CTL_ScanAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1050, separatorBefore = 1045),
    @ActionReference(path = "Shortcuts", name = "F8")
})
@Messages("CTL_ScanAction=Habilitar Scan")
public final class ScanAction implements ActionListener {

    private final MonitorContext context;
    
    public ScanAction(MonitorContext context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        context.toogleScan();
    }
    
}
