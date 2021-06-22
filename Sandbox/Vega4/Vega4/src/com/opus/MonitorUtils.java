/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opus;

/**
 *
 * @author opus
 */
public class MonitorUtils {
  
    
    static public enum endian {BIG,LITTLE};
    
    
    /**
     * Calcula valor em bytes de um array
     * @param buf
     *  Array onde estão os bytes a calcular 
     * @param index
     *  Inicio dos dados
     * * @param endian
     *  Ordem dos bytes no array
     * @return 
     *  Integer com o campo calculado
     */
    static public int getUnsignedInt (byte[] buf, int index, endian end){
        
        int result=0;
        
        int msb = (end==endian.BIG) ? buf[index] : buf[index+1] ;
        result = (msb<0) ? (256-Math.abs(msb))*256 : msb * 256;
        int lsb = (end==endian.BIG) ? buf[index+1] : buf[index];
        result += (lsb<0) ? (256-Math.abs(lsb)) : lsb;
        
        return result;
    }
    
    
    
}
