/***************************************************************************
                          isis2.c  -  Descrição
                             -------------------
    Gera��o              : Wed Jul 13 2011
    copyright            : (C) 2011 por Marcos A. Santos
    email                : msantos@opus-ciencia.com.br
 ***************************************************************************
 -------------------------      HISTORIA CVS    ----------------------------

 $Log$

 ***************************************************************************
 *                                                                         *
 *   Os segmentos de código abaixo são partes integrantes de projetos e    *
 *   iniciativas da ANTRAX TECNOLOGIA LTDA. e portanto protegidos pelas    *
 *   garantias de privacidade dos direitos de propriedade industrial ou    *
 *   intelectual. A ANTRAX reserva-se o direito de publicar ou não tais    *
 *   segmentos de acordo com seu critério. Ficam  portanto proibidas as    *
 *   reproduções de qualquer espécie sem sua prévia autorização.           *
 *                                                                         *
 ***************************************************************************/


#ifndef SPIMANAGER_H
#define	SPIMANAGER_H

#include "Compiler.h"
#include "GenericTypeDefs.h"
#include "spi.h"


#define SPI_RTS 			PORTAbits.RA5
#define IS_RTS 				SPI_RTS==0
#define SPI_CTS		 		LATBbits.LATB5
#define SET_CTS 			SPI_CTS=0
#define CLR_CTS 			SPI_CTS=1

#define SPI_RECEIVED 		SSPSTATbits.BF==1


// Declarations
void initSPI();
BYTE SPIhasTXData();
BYTE SPIgetTXToken();
BYTE loadTXData(BYTE data);
void executeSPI();
void storeSPIData(BYTE data);

#endif	/* SPIMANAGER_H */