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

#define TIMEOUT 200000

#define SPI_RTS 		LATEbits.LATE0
#define SET_RTS 		SPI_RTS=0
#define CLR_RTS		 	SPI_RTS=1
#define SPI_CTS 		PORTBbits.RB5
#define IS_CTS			SPI_CTS==0



// Declarations
void initSPI();
void putSPIM(unsigned char token);
void putsSPIM(unsigned char *msg);
void loadScan();
unsigned int getSPIValue(BYTE ptr);
void enableScanLoad();
BYTE isScanLoaded();
void loadBatch();

#endif	/* SPIMANAGER_H */