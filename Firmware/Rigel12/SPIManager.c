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


#define SPI_BF 16
BYTE spi_sbf[SPI_BF];
BYTE spi_rbf[SPI_BF];

BYTE spi_sbf_ptr;
BYTE spi_rbf_ptr;

BYTE txtoken;
BYTE rxtoken;
BYTE hasTXdata;
BYTE hasRXdata;

static volatile short long lspi_timeout;


/**
*
*/
void initSPI(){
	
	spi_sbf_ptr=0;
	spi_rbf_ptr=0;
	
	SSP1STAT &= 0x3F;                // power on state 
  	SSP1CON1 = 0x00;                 // power on state
	
	// Ajustar pinos do servi� SPI (CONF. PAGINA 211 DO DATASHEET)
	TRISCbits.RC4=1; // SDI<- input data 
	TRISCbits.RC5=0; // SDO-> output data
	TRISCbits.RC3=1; // SCK<- input em slave mode
	TRISAbits.RA5=1; // SS<- input em slave mode	
	TRISBbits.RB5=0; // ACK-> output em slave mode
	WPUBbits.WPUB5=1; // PULL-up Enabled na RB5		

	// Ajustar cond. de leitura no barramento
	SSP1STATbits.CKE = 1;        // data transmitted on rising edge
    SSPCON1bits.CKP = 1;        // clock idle state High
	
	SSP1STATbits.SMP = 0;        // Clear em slave mode
	SSP1CON1 |= 0x04;  			// SPI em slave mode, SS on
	
	SSP1CON1bits.SSPEN = 1;        // Habilite SPI

	SSP1BUF=0xFF;
	spi_rbf_ptr=0;
	spi_sbf_ptr=0;
	hasTXdata=FALSE;
	hasRXdata=FALSE;

}


/**
 * Sinaliza Timeout
 * 
 */
BYTE isLSPITimeout(unsigned char *msg){

	if (--lspi_timeout==0){
		//envie_erro(ERRO_SPI);
		putsrom "\r\n SPI Timeout ");
		putsrom msg);
		CLR_CTS;
		return TRUE;	
	}
	return FALSE;
}




void storeSPIData(BYTE data){
	
	spi_rbf[spi_rbf_ptr++] = data;
	if (spi_rbf_ptr > SPI_BF) spi_rbf_ptr;
	
}

/**
* Carrega token para transmissao
*/
void executeSPI(){
	
	
	BYTE *lptr;// = &spi_rbf[0];
	BYTE lscanptr;// = *lptr && 0xFE;
	unsigned int temp;
	
	unsigned int scan_cnt = 0;
	unsigned int *scanpt; 
	
	// Comando 0xFF de leitura ? - Buffer esta vazio ?
	if(spi_rbf_ptr==0) return;
	
	scanpt=(unsigned int *)getScanPointer();
	
	
	//CRLF;
	if (spi_rbf[spi_rbf_ptr-1] == 0xFE){
		// Comando eh de carga de valor de scan
		// Ajuste ptrs : *lptr obtem o addr do byte a ser enviado do primeiro byte do comando
		// lscanptr ajusta para um array de int (scanbf) - anda de dois em dois !
		//lptr = &spi_rbf[0];
		//lscanptr = *lptr >> 1;
		// Obtenha o inteiro adequado do buffer
		//temp = getScanValue(lscanptr);
		// Se o byte for impar envie o MSB
		//if ((*lptr & 0x01)==0){temp=temp<<8;}
		//loadTXData(temp);
		
		// Rotina Batch -- recebeu comando de carga do scan
		//putsrom "\r\n Recebido comando de envio de batch");	
		// Envie o Batch ACK
		SSP1BUF=0xF7;		
		
		CLR_CTS;
		lspi_timeout = 200000;
		while (!IS_RTS){if (isLSPITimeout("Batch: enviando first ack")) return;}
		SET_CTS;
		lspi_timeout = 20000;
		while (IS_RTS){if (isLSPITimeout("Batch: aguardando clearing do first ack")) return;}
		CLR_CTS;
		//putsrom "\r\n Comando first ack enviado ..");	
		
		
		while(1){
			//LATCbits.LATC2 ^=1;
			lspi_timeout = 20000;
			//temp=getScanValue(scan_cnt);			
			temp=*scanpt;
			//SSP1BUF=0x1;
			SSP1BUF=temp>>8;
			while (!IS_RTS){if (isLSPITimeout("Batch: enviando MSB")) return;}
			SET_CTS;
			while (IS_RTS){if (isLSPITimeout("Batch: aguardando clearing envio MSB")) return;}
			CLR_CTS;
			lspi_timeout = 20000;
			//SSP1BUF=0xff;
			SSP1BUF=temp;
			while (!IS_RTS){if (isLSPITimeout("Batch: enviando LSB")) return;}
			SET_CTS;
			while (IS_RTS){if (isLSPITimeout("Batch: aguardando clearing envio LSB")) return;}
			CLR_CTS;
			if (SSP1BUF== 0xF7) break;
			scanpt++;
		}

		//putsrom "Batch Terminado !!");		
	}
	else {	
		// Comando de config
		lptr = &spi_rbf[0];
		spi_rbf[spi_rbf_ptr] = 0;	
		putsrom "comando :");	
		do{
			printf("%x[%c]", *lptr, *lptr);	
		}while (*lptr++);
		CRLF;
	}
	
	// Zere o pointer de leitura
	spi_rbf_ptr=0;	
}

/**
* Carrega token para transmissao
*/
BYTE loadTXData(BYTE data){ 
	txtoken = data;
	hasTXdata=TRUE;
}

/**
* Verifica se h�novo dado para transmitir
*/
BYTE SPIhasTXData(){ return hasTXdata;}


/**
* Obtem token da SPI
*/
BYTE SPIgetTXToken(){ 
	hasTXdata=FALSE;
	return txtoken;
}


