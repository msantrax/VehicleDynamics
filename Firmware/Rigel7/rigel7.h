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


#ifndef RIGEL7_H
#define	RIGEL7_H

#include "Compiler.h"
#include "GenericTypeDefs.h"
//#include <p18F4620.h>


//#define DEBUG

#define CR 0x0d
#define LF 0x0a
#define BKSP 0x08
#define ESC 0x1b

#define LPT_BUF_LEN 16


// Defines do Hardware


// Printer locations
#define LPT_STROBE_TRIS 	(TRISBbits.RB0)
#define LPT_STROBE			(LATBbits.LATB0)
#define LPT_BUSY_TRIS 		(TRISBbits.RB4)
#define LPT_BUSY			(LATBbits.LATB4)
#define LPT_ACK_TRIS 		(TRISBbits.RB3)
#define LPT_ACK				(LATBbits.LATB3)
#define LPT_SELECT_TRIS 	(TRISBbits.RB2)
#define LPT_SELECT			(LATBbits.LATB2)
#define LPT_POUT_TRIS 		(TRISCbits.RC2)
#define LPT_POUT			(LATCbits.LATC2)

#define LPT_DATA_TRIS 		(TRISD)
#define LPT_GET()			(LATD)
#define LPT_PUT(a)			(LATD = (a))

#define LPT_TGL_WIDTH 10


#define putsrom putrsUSART ((const far rom char *)
#define CRLF putsrom "\r\n")


// Declarations
//void listLPT();
void toggleLPT(int width);
void processState(void);
void doLPT(void);
void doLPTBeacon(void);

#endif	/* RIGEL_H */

