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
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

@ActionID(
        category = "File",
        id = "com.opus.JuridAction")
@ActionRegistration(
        iconBase = "com/opus/atlantik.png",
        displayName = "Abrir terminal Jurid")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1045),
    @ActionReference(path = "Shortcuts", name = "F6")
})
//@Messages("CTL_JuridAction=Observar dados Brutos")
public final class JuridAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JuridTopComponent jtc = (JuridTopComponent)WindowManager.getDefault().findTopComponent("JuridTopComponent");
         if (jtc !=null){
            //jtc.setMonitor(this);
            // Se não aberto, dito
            if (!jtc.isOpened()){
                jtc.open();
                jtc.requestActive();
            }
            // Já está aberto , só revalide pois queremos somente um painel.
            jtc.requestFocus();
            jtc.revalidate();
        }  
        
    }
}
