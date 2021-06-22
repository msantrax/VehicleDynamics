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
#include "rigel12.h"
#include "OutManager.h"

#include <usart.h>
#include <string.h>
#include <stdio.h>
#include <ctype.h>


/* Variaveis =============================================================*/

#define OUTBF_LENGHT 64
BYTE outbf[OUTBF_LENGHT];
BYTE outbf_ptr;
unsigned int mark; 

//#define DATABF_LENGHT 
//BYTE outbf[DATABF_LENGHT];


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
		while(Busy1USART());
	    Write1USART(	outbf[i]);
	}		
}


void sendScan(){

	BYTE i;
	unsigned int channel_val;
			
	for (i=0;i<16;i++){
		
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
			channel_val = getChannel((outbf_ptr-11)/2);			
			outbf[outbf_ptr+1]=channel_val & 0xff;
			outbf[outbf_ptr]=channel_val >> 8;	
		}
		outbf[outbf_ptr++]=0x55;
		outbf[outbf_ptr++]=0xAA;
		sendCommand();
	}		
}


unsigned int getChannel(int channel){
	
	//DWORD value;
	//value = TickGet();
    return (channel * 1000) + mark;
	
}




