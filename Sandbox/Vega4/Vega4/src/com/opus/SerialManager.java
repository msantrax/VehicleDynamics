/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opus;


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 * Classe de controle para dispositivos do tipo serial (RS232)
 * @author antrax
 */
public class SerialManager {

    private static final Logger log = Logger.getLogger(SerialManager.class.getName());   
    
    //private RXTXCommDriver CommDriver;
    private ArrayList<SerialDevice> devices;
    private final String pprompt = "Varredura: Gerente RXTX informa que a porta ";

    public SerialManager(){

        log.setLevel(Level.ALL);
        devices = new ArrayList<SerialDevice>();
        scanPorts();       
    }

    
    /**
     * Verifica portas disponíveis no equipamento
     */
    public void scanPorts(){

       Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
                try {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                    log.log(Level.INFO,pprompt + "{0} esta OK para uso", com.getName());
                    getDevices().add(new SerialDevice(SerialDevice.RXTX_SERIAL, com.getName(), true, 0));
                } catch (PortInUseException e) {
                    log.log(Level.INFO,pprompt + "{0}esta em uso no momento...", com.getName());
                    getDevices().add(new SerialDevice(SerialDevice.RXTX_SERIAL, com.getName(), false, SerialDevice.RXTX_INUSE));
                } catch (Exception e) {
                    log.log(Level.WARNING, pprompt +  com.getName() + " falhou para abrir...", e);
                    getDevices().add(new SerialDevice(SerialDevice.RXTX_SERIAL, com.getName(), false, SerialDevice.RXTX_FAILED));
                }
            }
        }

    }

    /**
     * Encerra operações nas portas seriais (todas)
     */
    public void clearPorts(){
        
        for (SerialDevice sd:getDevices()) {
            sd.close();
        }   
    }


    /**
     * Converte tipo de porta para String
     * @param portType
     * @return
     */
    static String getPortTypeName ( int portType ){
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }


    /**
     * Obtém dispositivo serial
     * @param id
     *  String com o identificador do dispositivo
     * @return
     *  Disposito identificado pela rotina. Null se não foi possivel a identificação.
     */
    public SerialDevice getDevice(String id){

        for (SerialDevice sd:getDevices()) {
            if (sd.getPortName().equals(id)) return sd;
        }
        return null;

    }

    /**
     * Verifica quais portas estao disponiveis para conexão.
     * @return array com os noomes das portas disponiveis
     */
    public Object[] getAvailablePorts(){

        ArrayList<String> ports = new ArrayList<String>();

        for (SerialDevice sd:getDevices()) {
            if (sd.isAvailable()){
                ports.add(sd.getPortName());
            };
        }
        return ports.toArray();
    }

    
    
    
    /**
     * @return the devices
     */
    public ArrayList<SerialDevice> getDevices() {
        return devices;
    }


    /**
     * @param devices the devices to set
     */
    public void setDevices(ArrayList<SerialDevice> devices) {
        this.devices = devices;
    }

    

}
