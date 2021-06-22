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


#include <p18F4620.h>					// Register definitions
#include "rigel7.h"
#include "config.h"
#include "rs232.h"
#include "SPIManager.h"

#include "Tick.h"

#include <usart.h>
#include <string.h>
#include <stdio.h>
#include <ctype.h>
#include <portb.h>
#include <delays.h>
#include <timers.h>
#include <adc.h>
#include <spi.h>


//#pragma config WDT=OFF, LVP=OFF, FOSC=HS

//---------------------------------------------------------------------
//Declarations
//---------------------------------------------------------------------
void hp_handler (void);
void lp_handler (void);


// Posicao da maquina de estados do sistema
static BYTE AppState;
static enum{
	STATE_IDLE = 0,
	STATE_LPT_REQUEST,
	STATE_SPI_TX
}STATE_APP;


//unsigned int a2d_val;
//unsigned char channel=0;
//unsigned int temp_buf[16];

/* Area de recepcao da LPT */
unsigned int lpt_pos;
char lptbuf[LPT_BUF_LEN];
char lptc;
BYTE lptActive = 0;
static DWORD lpt_flush;
static DWORD lpt_beacon;


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
	
	
	// LPT Service	
	if (INTCONbits.INT0IF = 1){			
		LPT_BUSY = 1;		
				
//		lptc = PORTD;
//		AppState = STATE_LPT_REQUEST;
//		while (BusyUSART());
//		putcUSART(lptc);
//		Delay100TCYx(10);
//		LPT_BUSY = 0;
//		LPT_ACK = 0;
//		Delay100TCYx(10);
//		LPT_ACK = 1;		
//		INTCONbits.INT0IF = 0;
				
						
		lptbuf[lpt_pos]=PORTD;		
	    if (lpt_pos >= LPT_BUF_LEN) {
			AppState = STATE_LPT_REQUEST;
			INTCONbits.INT0IE = 0;
			INTCONbits.INT0IF = 0;
		}
		else {
			lpt_pos++;
			lptbuf[lpt_pos]=0;
			Delay100TCYx(10);
			LPT_BUSY = 0;
			LPT_ACK = 0;
			Delay100TCYx(10);
			LPT_ACK = 1;
			INTCONbits.INT0IF = 0;
		}		
    }

}



#pragma interrupt lp_handler
void lp_handler (void){	
	
	TickUpdate();
	
	if (PIR1bits.RCIF ==1){
    	inputUSART();
    	PIR1bits.RCIF = 0;
 	}   
}


///**
//*
//*/
//void listLPT(){
//	
//	BYTE i;
//	BYTE j=0;
//	BYTE k;
//	
//	unsigned char tokens[16];
//	char *lptp;
//	unsigned char c;
//
////	lptp = lptbuf;
////	putsrom " LPT : [");
////	putsUSART(lptbuf);
////	putsrom "]\r\n");
//	
//	
//	for ( i=0;i<=LPT_BUF_LEN;i++){
//		c = lptbuf[i];
//		if (isgraph(c)){
//			tokens[j] = c;
//		}
//		else{
//			tokens[j]='.';
//		}
//		j++;
//		while (BusyUSART());
//		putcUSART(bhex[(c)>>4]);
//		while (BusyUSART());
//		putcUSART(bhex[(c)&0x0f]);
//		while (BusyUSART());
//		putcUSART(' ');
//		lptbuf[i]=0;
//		if (j >=16){
//			while (BusyUSART());
//			putsrom "    ");
//			for ( k=0;k<16;k++){
//				while (BusyUSART());
//				putcUSART(tokens[k]);
//			}		
//			while (BusyUSART());
//			putcUSART(10);
//			j=0;	
//		}		
//	}
//		
//	
//	//strncpypgm2ram (lptbuf, "Buffer da LPT", 14);	
//	lpt_pos=0;
//
//	
//}

/**
*
*/
void sendJurid(){
	
	BYTE i;	
	unsigned char c;
	
	for ( i=0;i<=LPT_BUF_LEN;i++){
		c = lptbuf[i];
		if (c == 0) break;
		while (BusyUSART());
		putcUSART(c);					
	}
		
	lpt_pos=3;	
}



/**
*
*/
void toggleLPT(int width){
	
	LPT_ACK = 0;
	Delay100TCYx(width);
	LPT_ACK = 1;
	//LATEbits.LATE1 ^= 1;		
    //Delay100TCYx(10);
    //LATEbits.LATE1 ^= 1;		
}



/**
 * Processa tarefas especificas do controlador
 * @return
 */
void processState(void){
		
	
	switch (AppState){
		
		case STATE_LPT_REQUEST:
			lptActive=1;
			lpt_flush = TickGet() + (0.5 * TICK_SECOND);
			AppState = STATE_IDLE;			
			lptbuf[2]=0x01;
			sendJurid();
			sendEcho(0xFF);
			Delay100TCYx(10);
			LPT_BUSY = 0;
			LPT_ACK = 0;
			Delay100TCYx(10);
			LPT_ACK = 1;		
			AppState = STATE_IDLE;
			INTCONbits.INT0IE = 1;
		    break;	
		case STATE_IDLE :			
			// Determine estado da impressora
			doLPT();				
			// Verifique se há movomento na USART 
			receive();
			// Execute o Heart beat da impressora
			doLPTBeacon();
			// Execute a transmissao SPI caso solicitado
			sendSPI();
			// Carregue o scan da SPI caso solicitado
			//loadScan();
			// Envie o scan para o host caso solicitado
			sendScan(); 						
			break;
		case STATE_SPI_TX :			
			break;
		
	}

}

/**
 *	Atende requisicao da LPT 
 *
 */
void doLPT(void){
	
	if (lptActive==1){			
		if (TickGet()>lpt_flush){					
			lptActive=0;
			INTCONbits.INT0IE = 0;
			lptbuf[2]=0x02;
			sendJurid();
			sendEcho(0xFF);
			INTCONbits.INT0IE = 1;
			Delay100TCYx(10);
			LPT_BUSY = 0;
			LPT_ACK = 0;
			Delay100TCYx(10);
			LPT_ACK = 1;		
			INTCONbits.INT0IF = 0;					
		}
	}
}

/**
 * Envia sinal de sincro para a interface LPT do Jurid 
 *
 */
void doLPTBeacon(void){
	if (TickGet() > lpt_beacon){
		lpt_beacon = TickGet() + (2 * TICK_SECOND);
		toggleLPT(10);
	}	'
}



void main (void){


    /* Configure porda D como input  -- Entrada da centronics*/
    LPT_DATA_TRIS = 0xff;
    
	
	LPT_STROBE_TRIS = 1;		//Input 	-> INT0 / LPT_STROBE
	LPT_BUSY_TRIS = 0;			//Output	-> LPT_BUSY	
	LPT_ACK_TRIS = 0;			//Output	-> LPT_ACK
	LPT_SELECT_TRIS = 1;		//Input		-> LPT_SELECT
	LPT_POUT_TRIS = 0;			//Outpút	-> LPT_POUT

	// LPT online
	LPT_SELECT=0;
	// Papel OK
	LPT_POUT=0;
	
	// LPT estah disponivel
	LPT_BUSY=0;
	// Defaul do Ack
	LPT_ACK=1;


	//N1_CS
	TRISEbits.RE0=0;
	//SD_CS
	TRISEbits.RE1=0;
	//ETH_CS
	TRISEbits.RE2=0;


	initRS232();
    
    /* Enable interrupt priority */
    RCONbits.IPEN = 1;
	
    /* Make receive interrupt low priority */
    IPR1bits.RCIP = 0;

    /* Enable all high priority interrupts */
    INTCONbits.GIEH = 1;
    /* Also the low priority interrupts */
    INTCONbits.GIEL = 1;

	// Turn on Pullups on RB port
	INTCON2bits.RBPU = 0;
	// Detect Interrupt at the falling edge
	INTCON2bits.INTEDG0 = 0;

    /* Habilite interrupts no INT0 */
    INTCONbits.INT0IE = 1;
    INTCONbits.INT0IF = 0;		


	lptbuf[0]=0xFE;
	lptbuf[1]=0xFF;
	lptbuf[2]=0x01;
	lptbuf[3]=0;	
	lpt_pos=3;
	toggleLPT(LPT_TGL_WIDTH);
	lpt_beacon = 0;

	// Inicie os serviço de tick
    TickInit();
    
    // Sem envio de scans por default
    enableScanSend(FALSE);
    
    initSPI();
    	
	AppState = STATE_IDLE;
    /* Loop forever */
    while (1){		
		processState();			

	}
	

}


























































//    channel = 0;
//    ADCON0=0x01;
//    ADCON1=0x00;
//    ADCON2=0xA6;



//            ADCON0=((channel<< 2) | 0x01);
//            //printf ("ADCON1=%u -- Channel=%u\r\n", ADCON0, channel);
//            ConvertADC();
//            while(BusyADC());
//            a2d_val = ReadADC();
//            //printf ("A2D_VAL=%u\r\n", a2d_val);
//            temp_buf[channel]=a2d_val;
//            channel++;
//
//            if(channel > 15){
//                channel=0;
//                putsrom "###");
//                for (i=0; i<15; i++){ printf("-%4.0u", temp_buf[i]);}
//                CRLF;
//            }


