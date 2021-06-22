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
        id = "com.opus.MonitorPadraoAction")
@ActionRegistration(
        iconBase = "com/opus/view_remove.png",
        displayName = "#CTL_MonitorPadraoAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1000),
    @ActionReference(path = "Shortcuts", name = "F2")
})
@Messages("CTL_MonitorPadraoAction=Abrir Monitor Padrao")
public final class MonitorPadraoAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        MonitorTopComponent tc = new MonitorTopComponent();
        tc.initSystem(true);
        Mode m = WindowManager.getDefault().findMode("editor");
        if(m != null){
            m.dockInto(tc);
            tc.open();
            tc.requestActive();
        }
    }
}
