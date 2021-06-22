/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opus;

import java.util.ArrayList;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author opus
 */
public class XMLErrorHandler extends DefaultHandler {

    
    private boolean has_errors;
    private StringBuilder messages;
    
    /**
     * Construtor básico
     */
    public XMLErrorHandler(){
        
        super();
        has_errors=false;
        messages = new StringBuilder();
        getMessages().append("Existem erros de validação no arquivo de config : \n\n");
        
    }

    
    @Override
    public void error(SAXParseException e) {
        
        has_errors=true;
        
        String msg = e.getMessage();
        
        getMessages().append("Erro na linha ");
        getMessages().append(e.getLineNumber());
        getMessages().append(", coluna " );
        getMessages().append(e.getColumnNumber());
        getMessages().append(" : \n" );
        getMessages().append(msg);
        getMessages().append("\n");
        
    }
    
    
    
    /**
     *
     * @param e
     */
    @Override
    public void fatalError(SAXParseException e){
        
        has_errors=true;
        
        String msg = e.getMessage();
        
        getMessages().append("Fatal na linha ");
        getMessages().append(e.getLineNumber());
        getMessages().append(", coluna " );
        getMessages().append(e.getColumnNumber());
        getMessages().append(" : \n" );
        getMessages().append(msg);
        getMessages().append("\n");
        
    }
    
     /**
     *
     * @param e
     */
    @Override
    public void warning(SAXParseException e){
        
        has_errors=true;
        
        String msg = e.getMessage();
        
        getMessages().append("Warning ");
        getMessages().append(e.getLineNumber());
        getMessages().append(", coluna " );
        getMessages().append(e.getColumnNumber());
        getMessages().append(" : \n" );
        getMessages().append(msg);
        getMessages().append("\n");
        
    }
    
    
    
    /**
     * @return the has_errors
     */
    public boolean isHas_errors() {
        return has_errors;
    }

    /**
     * @param has_errors the has_errors to set
     */
    public void setHas_errors(boolean has_errors) {
        this.has_errors = has_errors;
    }

    /**
     * @return the messages
     */
    public StringBuilder getMessages() {
        return messages;
    }
    
    
    
    
    
}
