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
        id = "com.opus.JuridRawAction")
@ActionRegistration(
        iconBase = "com/opus/rebuild.png",
        displayName = "#CTL_JuridRawAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1040),
    @ActionReference(path = "Shortcuts", name = "F5")
})
@Messages("CTL_JuridRawAction=Observar dados Brutos")
public final class JuridRawAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JuridTopComponent jtc = (JuridTopComponent)WindowManager.getDefault().findTopComponent("JuridTopComponent");
        Mode m = WindowManager.getDefault().findMode("output");  
        if (jtc !=null && m.getSelectedTopComponent() instanceof JuridTopComponent) {
            jtc.openRawJurid();
        }
    }
}
