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

@ActionID(
        category = "File",
        id = "com.opus.SimulatorAction")
@ActionRegistration(
        iconBase = "com/opus/clanbomber.png",
        displayName = "Habilitar Simulador")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1070),
    @ActionReference(path = "Shortcuts", name = "F9")
})
//@Messages("CTL_SimulatorAction=Habilitar Simulador")
public final class SimulatorAction implements ActionListener {

    private final MonitorContext context;
    
    public SimulatorAction(MonitorContext context) {
        this.context = context;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        context.openSimulator();
    }
    
}
