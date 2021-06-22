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


#ifndef SCAN_H
#define	SCAN_H

#include "Compiler.h"
#include "GenericTypeDefs.h"
#include "rs232.h"


#define USE_OR_MASKS
//#include <p18cxxx.h>
#include "adc.h"

// Declarations
void initADCScan();
void initCounter(void);
void updateCounter(void);
DWORD getCounter(void);
void doScan();
//void sendSPIScan();
unsigned int getScanValue(BYTE ptr);
unsigned int *getScanPointer();

#endif	/* SCAN_H */