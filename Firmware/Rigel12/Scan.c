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


#include "Scan.H"
#include <timers.h>


#pragma udata scan_data
static unsigned int scanbf[240];
#pragma udata

unsigned int *scanbf_init= &scanbf[0];

unsigned char *scanbf_byte; 

// Pointer para o round da conversao (sao 10 rounds);
BYTE burn;

static volatile unsigned short spi_timeout;


BYTE channel_map[16]= {ADC_CH10,ADC_CH11,ADC_CH22,ADC_CH23,ADC_CH24,ADC_CH25,ADC_CH26,ADC_CH27,
					   ADC_CH0,ADC_CH1,ADC_CH8,ADC_CH9,ADC_CH6,ADC_CH7,ADC_CH3,ADC_CH5};

//BYTE channel_enb[16]= {1,2,3,4,5,6,7,8,0,0,0,0,0,0,0,0};
BYTE channel_enb[16]=   {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};


// Armazenamento do pulsos de velociade no timer 1;
static volatile DWORD dwInternalTicks = 0;
// Packet com valores do tick do counter
static BYTE vTickReading[6];


/**
*
*/
void initADCScan(){
	

	unsigned char config1=0x00,config2=0x00,config3=0x00;

		
	//-- clear adc interrupt and turn off adc if in case was on prerviously---
    CloseADC();

	// Config das Portas
	ANSELA=0x0b; // Port A 0,1,  3 				-> analog
	ANSELB=0x1e; // Port B   1,2,3,4 			-> analog
	ANSELC=0x00; // Port C todo digital
	ANSELD=0xfc; // Port D    ,2,3,4,5,6,7 		-> analog
	ANSELE=0x07; // Port E 0,1,2 				-> analog

	TRISA=0x0b; // Port A 0,1,  3 				-> INPUT
	TRISB=0x1e; // Port B   1,2,3,4 			-> INPUT
	TRISD=0xfc; // Port D    ,2,3,4,5,6,7 		-> INPUT
	TRISE=0x07; // Port E 0,1,2 				-> INPUT
	


	//--Inicie o ADC---
	/**** ADC configurado para:
    * FOSC/2 conversion clock
    * right justified
    * Aquisition time 2 TAD
    * Amostragem no canal 1
    * ADC interrupt off
    * ADC reference voltage = VDD & VSS
	*/
    config1 = ADC_FOSC_16 | ADC_RIGHT_JUST | ADC_2_TAD ;
    config2 = ADC_CH0 | ADC_INT_OFF;
    config3 = ADC_REF_VDD_VSS ;
    OpenADC(config1,config2,config3);

	//---Inicie o interrupt do ADC e habilite-o---
    //ADC_INT_ENABLE();

	burn = 0;
	scanbf_byte =(unsigned char *)&scanbf[0];
	
}

/**
*	Inicio do serviço de captura dos pulsos do medidor de distancia
*/
void initCounter(void){
	
	TRISAbits.RA4=1;
	
	WriteTimer0(0x00);
	dwInternalTicks = 0;
	
	OpenTimer0(	TIMER_INT_ON |
				T0_16BIT |
				T0_SOURCE_EXT |
				T0_EDGE_RISE |
				T0_PS_1_1
			  );
	
}


/**
*	Update do valor do counter apos o interupt overflow
*/
void updateCounter(void){

	if(INTCONbits.TMR0IF){
	    
		// Increment o contador interno
		dwInternalTicks++;
	
		// Reset a flag
        INTCONbits.TMR0IF = 0;
    }
}

/**
*	Obtem valor do counter
*/
 DWORD getCounter(void){	
	
	do{
    	INTCONbits.TMR0IE = 1;		// Enable interrupt
        Nop();
        INTCONbits.TMR0IE = 0;		// Disable interrupt
        vTickReading[0] = TMR0L;
        vTickReading[1] = TMR0H;
        *((DWORD*)&vTickReading[2]) = dwInternalTicks;
	} while(INTCONbits.TMR0IF);
	INTCONbits.TMR0IE = 1;	
	
	return *((DWORD*)&vTickReading[0]);
	
}


/**
*	Obtem valor do counter
*/
void doScan(){
	
	
	BYTE temp,i,j,k;
	BYTE dig;
	unsigned int a2d_val;	
	DWORD counter;

//	
//    for (i=0; i<240; i++){ 
//		scanbf[i]=i+1024;	            
//    }	
//	burn++;
//    if (burn>10) burn == 0;
//	return;
//	
	
	
	
	//LATCbits.LATC2 =1;
	
//	//for (k=0; k<32; k++){
//	for (j=0; j<=15; j++){
//		//if (k==0) scanbf[burn*24 + j]=0; 
//		//temp=channel_enb[j];
//		//if (temp==0 || temp==burn){
//			ADCON0=((channel_map[j]) | 0x01);
//	    	ConvertADC();
//	    	while(BusyADC());
//	    	a2d_val = ReadADC();
//	    	scanbf[burn*24 + j]=a2d_val;
//  		//}
//  		//else{
//	  	//	scanbf[burn*24 + j]=scanbf[temp*24 + j]; 	
//	  	//}  	 	
// 	}   
// 	//}	
 	
 	for (i=0; i<10; i++){ 
	 	for (k=0; k<4; k++){
			for (j=0; j<=15; j++){
				if (k==0) scanbf[i*24 + j]=0; 
				ADCON0=((channel_map[j]) | 0x01);
		    	ConvertADC();
		    	while(BusyADC());
		    	a2d_val = ReadADC();
		    	scanbf[i*24 + j]+=a2d_val;  	 	
		 	}   
	 	}
	 	counter = getCounter();
	 	scanbf[i*24 + 16]=counter;
	 	scanbf[i*24 + 17]=counter>>16;
	 	scanbf[i*24 + 18]=PORTC & 0x07; 	
 	}

	//LATCbits.LATC2 =0;
    
    burn++;
    if (burn>10) burn == 0;
   
}

/**
*	Obtem valor de uma celula do array de scans
*/
unsigned int getScanValue(BYTE ptr){ return scanbf[ptr];}


/**
*	Obtem valor de uma celula do array de scans
*/
unsigned int *getScanPointer(){ return &scanbf[0];}




///**
//*	Obtem valor do counter
//*/
//void sendSPIScan(){
//	
//	BYTE bf_ptr = 0;
//	BYTE i, ret;
//	unsigned int temp;
//	
//	scanbf_byte =(unsigned char *)&scanbf[0];
//	
//	//putsrom "\r\nEnviando scan via SPI ...");
//	//
//		
//	while(bf_ptr<240){
//		temp = scanbf[bf_ptr];
//		sendEcho('*');
//		//printf ("%u:", temp);
//		ret = WriteSPI1(temp);
//		if (ret == -1){
//			putsrom " --- Write collision \r\n");
//			break;		
//		}
//		ret = WriteSPI1(temp<<8);
//		if (ret == -1){
//			putsrom " --- Write collision \r\n");
//			break;		
//		}
//		bf_ptr++;
//	}	
//	putsrom " --- Envio terminado \r\n");	
//	
//}


//	do{
//		spi_ptr=SSP1BUF;
//		printf(" %u", spi_ptr);
//		if (spi_ptr<240) WriteSPI1(scanbf[spi_ptr]);	
//		while (!SSPSTATbits.BF);
// 	} while (spi_ptr != 65);   
		
	
//	for (i=0; i<240 ; i++){
//		scanbf[i] = i;	
//	}


//    putsrom "###   ");
//    for (i=0; i<=15; i++){ 
//        printf("-%5.0u", scanbf[i], getCounter());
//        scanbf[i]=0;
//    }
//    counter = getCounter();
//    
//    
//    printf("%u/%u", counter, counter>>16);
//    CRLF;

	
