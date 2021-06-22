/***************************************************************************
                          isis2.c  -  Descricao
                             -------------------
    Geração              : Wed Jul 13 2011
    copyright            : (C) 2011 por Marcos A. Santos
    email                : msantos@opus-ciencia.com.br
 ***************************************************************************
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


#include <p18F45K22.h>					// Register definitions
#include "rigel12.h"
#include "rs232.h"
#include "SPIManager.h"

#include <usart.h>
#include <string.h>
#include <stdio.h>
#include <ctype.h>
#include <portb.h>
#include <delays.h>
#include <timers.h>
#include <adc.h>
#include <spi.h>



// Posicao da maquina de estados do sistema
static BYTE AppState;
static enum{
	STATE_IDLE = 0,
	STATE_SPI_REQUEST,
	STATE_USART_REQUEST,
	STATE_SPI_RECEIVING,
	STATE_SPI_DISPATCH,
	STATE_SCAN
}STATE_APP;


static volatile BYTE spi_localtoken;
static volatile short long spi_timeout;


//---------------------------------------------------------------------
//Declarations
//---------------------------------------------------------------------
void hp_handler (void);
void lp_handler (void);


//---------------------------------------------------------------------
//ISR´s
//---------------------------------------------------------------------
#pragma code hp_interrupt = 0x8
void hp_int (void){
	_asm goto hp_handler _endasm
}

#pragma code lp_interrupt = 0x18
void lp_int (void){
	_asm goto lp_handler _endasm
}


#pragma code

#pragma interrupt hp_handler
void hp_handler (void){
	
	updateCounter();
	
}



#pragma interrupt lp_handler
void lp_handler (void){	
	
	if (PIR1bits.RC1IF==1){
    	inputUSART();
 	}	   
}


/**
 * Sinaliza Timeout
 * 
 */
BYTE isSPITimeout(unsigned char *msg){

	if (--spi_timeout==0){
		//envie_erro(ERRO_SPI);
		putsrom "\r\n SPI Timeout ");
		putsrom msg);
		CLR_CTS;
		return TRUE;	
	}
	return FALSE;
}


/**
 * Processa tarefas especificas do controlador
 * @return
 */
void processState(void){
			
	switch (AppState){
		
		case STATE_SPI_REQUEST:
			//putsrom "\r\nEM STATE_SPI_REQUEST");
			// Master solicitou linha - verifique se há dado p/ TX 
			if (SPIhasTXData()) {SSP1BUF=SPIgetTXToken();}
			// Sinalize OK p/ TX para o Master
			SET_CTS;
			AppState = STATE_SPI_RECEIVING;
			break;				
		// Sincro OK receba o dado do master e transmita o nosso se for o caso
		case STATE_SPI_RECEIVING:
			//putsrom "\r\nEM RECEIVING..");
			// Aguarde o buffer receber
			// TODO : Verificar framming e overflows
			spi_timeout = 2000;
			while (!SPI_RECEIVED){			
				if (isSPITimeout("na recepcao")){
					AppState = STATE_IDLE;
					break;		
				}
				// Execute outros serviços enquanto espera
				// Execute um scan dos canais
				//doScan();
				// Verifique se temos algum comando
				//receive();	
			}
			// Dado chegou, processe
			spi_localtoken = SSP1BUF;				
			if (spi_localtoken == 0xFF){	
					executeSPI();
			}
			else{
				//printf(" %u", spi_localtoken);
				storeSPIData(spi_localtoken);
			}		
			// Normalize o estado
			CLR_CTS;
			spi_timeout = 200000;
			while (IS_RTS){
				if (isSPITimeout("em clearing")){
					AppState = STATE_IDLE;
					break;
				}
			}
			AppState = STATE_IDLE;
			break;						    	   	
		case STATE_IDLE :			
			//putsrom "\r\nEM STATE_IDLE");
			// Execute um scan dos canais
			doScan();
			// Verifique se temos algum comando
			receive();
			// Verifique se o temos atividade do master SPI
			//LATBbits.LATB5 ^=1;			
			if (PORTAbits.RA5==0){
				//putsrom "\r\nRTS->passando para Request");
				AppState = STATE_SPI_REQUEST;				 	
				spi_timeout = 200000;
			}
			else{
				CLR_CTS;
				
			}
			break;				
	}

}


void main (void){

	
	// Pino de sinalização de atividade
	TRISCbits.RC0=1;
	TRISCbits.RC1=1;
	TRISCbits.RC2=1;
	
	// Pino de ack do canal do SPI entre CPUs
	TRISBbits.RB5=0;
	
	
    /* Enable interrupt priority */
    RCONbits.IPEN = 1;	   
    /* Enable all high priority interrupts */
    INTCONbits.GIEH = 1;
    /* Also the low priority interrupts */
    INTCONbits.GIEL = 1;
	
	
	// Partir serial
	initRS232();
	// Partir conversao A/D
	initADCScan();	
	// Partir contador de distancias
	initCounter();
	
	// Partir SPI
	initSPI();
	CLR_CTS;
	
 	// Partir serviços...
 	AppState = STATE_IDLE;
    
	Delay1KTCYx(100);
	    
    while (1){		
		processState();			
	}
	

}
