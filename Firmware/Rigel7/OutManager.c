/***************************************************************************
 -------------------------      HISTORIA CVS    ----------------------------

 $Log$

 ***************************************************************************
 *                                                                         *
 *   Os segmentos de codigo abaixo sao partes integrantes de projetos e    *
 *   iniciativas da ANTRAX TECNOLOGIA LTDA. e portanto protegidos pelas    *
 *   garantias de privacidade dos direitos de propriedade industrial ou    *
 *   intelectual. A ANTRAX reserva-se o direito de publicar ou nao tais    *
 *   segmentos de acordo com seu criterio. Ficam  portanto proibidas as    *
 *   reproducoes de qualquer especie sem sua previa autorizacao.           *
 *                                                                         *
 ***************************************************************************/


#include "rs232.h"
#include "rigel7.h"
#include "OutManager.h"
#include "SPIManager.h"

#include <usart.h>
#include <string.h>
#include <stdio.h>
#include <ctype.h>


/* Variaveis =============================================================*/

#define OUTBF_LENGHT 192
BYTE outbf[OUTBF_LENGHT];
BYTE outbf_ptr;
unsigned int mark;

BYTE enablescan;
 


void resetOutManager(void){

	BYTE i;
	
	for ( i=0;i<=OUTBF_LENGHT;i++){
			outbf[i]=i&0x0f;
	}	
	outbf[0]=0xfe;
	outbf[1]=0xff;
	outbf_ptr=2;
		
}

BYTE *getOutbfPtr(){
	return &outbf[outbf_ptr];	
}



void initCommand (BYTE type){
	
	resetOutManager();
	outbf[outbf_ptr]=type;
	outbf_ptr++;	
} 


void appendMsg(int tokens_written){
	outbf_ptr+=tokens_written+1;	
}



void sendCommand(){
	BYTE i;
	
	outbf[outbf_ptr] = 0xff;
	
	for ( i=0;i<=outbf_ptr;i++){
		while(BusyUSART());
	    WriteUSART(	outbf[i]);
	}		
}


/**
* Habilita envio do scan para o host 
*/
void enableScanSend(BYTE enable){ enablescan = enable;}


/**
* Envia o scan para o Host
*/
void sendScan(){

	BYTE i;
	unsigned int channel_val;
	
	// Teste para verificar necessidade de envio
	if(enablescan && isScanLoaded()){
		// Há requisicao e já carregamos o scan do R12	
		for (i=0;i<10;i++){		
			// Selecione o comando
			if(i==0){
				initCommand (5);	
			}
			else if (i==15){
				initCommand (7);
			}
			else{
				initCommand (6);	
			}	
			
			for (outbf_ptr=11;outbf_ptr<=60;outbf_ptr+=2){
				mark++;
				channel_val = getChannel((outbf_ptr-11)/2, i);			
				outbf[outbf_ptr+1]=channel_val & 0xff;
				outbf[outbf_ptr]=channel_val >> 8;	
			}
			if (isBinary()){
				outbf[outbf_ptr++]=0x55;
				outbf[outbf_ptr++]=0xAA;
				sendCommand();
			}
		}
		enablescan=FALSE;
		initCommand(3);
        appendMsg(sprintf(getOutbfPtr(), "Scan enviado\r\n", 32 ));
        sendCommand ();
	}			
}


unsigned int getChannel(BYTE channel, BYTE round){
	
	unsigned int temp; 
	
	temp = getScanValue(round*24+channel);
    return temp;	
}

