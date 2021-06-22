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


#include "SPIManager.h"
#include "spi.h"
#include "rs232.h"


#pragma udata scan_data
static unsigned int scanbf[240];
#pragma udata

unsigned char *scanbf_init= &scanbf[0];
static volatile unsigned short long spim_timeout;

#define SPI_BFLENGH 64

BYTE spi_sbf[SPI_BFLENGH];
//BYTE spi_rbf[SPI_BFLENGH/2];

BYTE spi_sbf_ptr;
BYTE spi_rbf_ptr;

BYTE spi_hastxdata;

BYTE spi_rx_token;

// Numero do round a recuperar do R12 
BYTE burn;


/**
* Inicia servicos da SPI
*/
void initSPI(){
	
	
	SSPSTAT &= 0x3F;                // power on state 
  	SSPCON1 = 0x00;                 // power on state
	
	// Ajustar pinos do serviço SPI (CONF. PAGINA 211 DO DATASHEET)
	TRISCbits.RC4=1; // SDI<- input data 
	TRISCbits.RC5=0; // SDO-> output data
	TRISCbits.RC3=0; // SCK-> output em master mode
	//TRISAbits.RA5=1; // SS <- input em slave mode	
	TRISBbits.RB5=1; // ACK<- input em master mode
	//WPUBbits.WPUB5=1; // Pullup enabled na RB5

	// Ajustar cond. de leitura no barramento
	SSPSTATbits.CKE = 1;        // data transmitted on rising edge
    SSPCON1bits.CKP = 1;        // clock idle state High
	
	SSPSTATbits.SMP = 0;        // Sample at the middle
	SSPCON1 |= 0x02;  			// SPI em slave mode, SS on
	
	SSPCON1bits.SSPEN = 1;		// Habilite SPI
	
	spi_sbf_ptr=0;
	spi_rbf_ptr=0;
	burn=0x0A;
	spi_hastxdata=FALSE;
	CLR_RTS;


}

/**
* Inicia timeout
*/
void setTimeout(){spim_timeout = TIMEOUT;}

/**
* Verifica timeout
*/
BYTE isTimeout(unsigned char *msg){

	if (--spim_timeout==0) {
		putsrom "\r\n --------------- SPI Timeout ");
		putsrom msg);
		//envie_erro(SPI_TIMEOUT);
		CLR_RTS;
		return TRUE;		
	}
	//else {
		//sendEcho('.');	
	//}
	return FALSE;
}


/**
* Informa se há dados para TX
*/
BYTE hasTXData(){return spi_hastxdata;}

/**
* Requisita transmissao na SPI
*/
void setTXData(BYTE request){spi_hastxdata=request;}

/**
* Carrega Token no buffer de TX
*/
void pushTXData(BYTE token){
	
	spi_sbf[spi_sbf_ptr++] = token;
	if(spi_sbf_ptr > SPI_BFLENGH) spi_sbf_ptr;
}

/**
* Obtem prox. dado do buffer de TX
*/
BYTE popTXData(){
	
	BYTE temp;
		
	temp = spi_sbf[spi_rbf_ptr++];
	if(spi_rbf_ptr == spi_sbf_ptr) {
		spi_rbf_ptr=0;		
		spi_sbf_ptr=0;
		spi_hastxdata=FALSE;
	}
	return temp;
}


/**
* Envia buffer SPI para o slave
*/
void sendSPI(){

	if(spi_hastxdata){
		putSPIM(popTXData());			
	}
	CLR_RTS;
}



/**
* Envia caracter para a SPI
*/
void putSPIM(unsigned char token){
	
	spim_timeout = 20000l;
	
	// Reset para default, aguarde o R12 deve responder com o CTS adequado
	CLR_RTS;
	while(IS_CTS){if(isTimeout("no reset")) return;}
	//putsrom "\r\n\n Reset: RTS=CLR / CTS=CLR");
	
	// Sinalize RTS e aguarde permissao do CTS
	SET_RTS;
	while(!IS_CTS){if(isTimeout("na permissao CTS")) return;}
	//putsrom "\r\n Requisitou: RTS=SET / CTS=SET");
	
	// Sincro OK - Transmita o dado
	while(WriteSPI(token)){if(isTimeout("na transmissao")) return;}
	spi_rx_token = SSPBUF;
	//putsrom "\r\n Enviou dado:");
	//sendEcho(token);
	
	//Libere o RTS e aquarde o default
	CLR_RTS;
	while(IS_CTS){if(isTimeout("na liberacao")) return;}
	//putsrom "\r\n Limpou status: RTS=CLR / CTS=CLR\r\n");			
}


/**
* Envia string para a SPI
*/
void putsSPIM(unsigned char *msg){
	
	while(*msg){
		pushTXData(*msg++);		
	}
	spi_hastxdata=TRUE;	
}


/**
* Obtem valor do buffer de scan
*/
unsigned int getScanValue(BYTE ptr){return scanbf[ptr];}


/**
*	Carrega scan para envio
*/
void loadScan(){
	
	BYTE i,j,k,round;
	unsigned int value;

	// Execute somente se há solicitacao
	//if (burn == 0x0A) return;
	
//	initCommand(3);
//    appendMsg(sprintf(getOutbfPtr(), "Loading burn %u\r\n", burn ));
//    sendCommand ();
	
	putsrom "Carregando da SPI ");
	
	// Precisamos de um loop rápido
	for (round=0; round<10; round++){
		k= round*48;
		j=k+48;
		sendEcho('.');
		for (i=k; i<j; i++){
			// Primeiro byte do inteiro (MSB ?)
			putSPIM(i);
			putSPIM(0xFE);		
			putSPIM(0xFF);
			putSPIM(0xFF);
			value = spi_rx_token * 256;
			// Segundo
			putSPIM(i);
			putSPIM(0xFE);		
			putSPIM(0xFF);
			putSPIM(0xFF);
			value+=spi_rx_token;
			// Armazene valor
			scanbf[i>>1]=value;
		}
	}
	
	
	
	// Proximo round ou Load OK (via burn=0x0A)
	//burn++;		
}	


/**
*	Carrega scan para envio
*/
void loadBatch(){

	BYTE i;
	unsigned int value;
	
	// Coloque o R12 em execute SPI
	putSPIM(0x00);
	putSPIM(0xFE);		
	putSPIM(0xFF);
	//putsrom "\r\n enviada solicitacao executeSPI ");
	
	
	// Execute OK - leia a flag de modo batch
	putSPIM(0xFF);
	
	if (spi_rx_token != 0xF7){
		putsrom "\r\n loadBatch nao habilitou ");
		return;			
	} 
	//putsrom "\r\n loadBatch OK ");
	
	
	for (i=0; i<240; i++){	
		putSPIM(0xFF);
		value = spi_rx_token * 256;	
		if (i==239){
			putSPIM(0xF7);
		}
		else{
			putSPIM(0xFF);
		}
		value+=spi_rx_token;
		scanbf[i]=value;		
	}
}


/**
* Habilita carga do scan do R12 via SPI
*/
void enableScanLoad(){ burn=0;}

/**
* Verifica se estamos carregando o scan do R12
*/
BYTE isScanLoaded(){ return burn==0x0A;}

	

//	BYTE j,k;
//	unsigned int i;
//	unsigned int value;
//	
//	//putsrom "\r\n carregando SPI\r\n ");	
//	sendSPI(0xFE);
//		
//	for (i=0; i<240; i++){scanbf[i]=0;}
//	
//	//Delay100TCYx(1000);
//	
//	SET_SPI_CS;	
//	for (i=0; i<246; i++){
//		LATEbits.LATE1 ^= 1;
//		j = ReadSPI();
//		value = j *256;
//		k = ReadSPI();
//		value +=k;
//		scanbf[i]=value;
//	}
//	CLEAR_SPI_CS;
	
	//for (i=0; i<240; i++){printf("%u,", scanbf[i]);}
	//putsrom " ..... SPI terminou \r\n");





	//Delay10TCYx(10);		
	//sendSPI(0);
	//sendSPI(0);	


//	SET_SPI_CS;	
//	temp = ReadSPI();
//	CLEAR_SPI_CS;	
//	if (temp == 0xFE){
//		putsrom "\r\n SPI Valido...");
//		for (i=0; i<20; i++){
//			sendSPI(i);
//			SET_SPI_CS;	
//			temp=ReadSPI();
//			CLEAR_SPI_CS;
//			printf ("%u,", temp);		
//		}
//		sendSPI(0xFA);		
//	}
//	else{
//		printf ("\r\n [%u] SPI nao validou :-( ", temp);
//	}


//	
//	for (i=0; i<25; i++){ prm[i]=0;}
//	sendSPI(0xFE);
//	Delay10TCYx(10);
//	
//	SET_SPI_CS;
//	getsSPI(prm,22);
//	SET_SPI_CS;
//	
//	for (i=0; i<25; i++){ printf ("%u,", prm[i]);}
//	//printf ("%s", prm);
	
