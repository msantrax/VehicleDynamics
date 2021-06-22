/***************************************************************************
                                Rigel10
                             -------------------
    Geração              : 31/07/2012
    copyright            : (C) 2012 por Marcos A. Santos
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


#ifndef RIGEL10_H
#define	RIGEL10_H

#include <p18F4620.h>


#ifndef RS232
    #define RS232
    #include "rs232.h"
#endif

//---------------------------------------------------------------------
//Declaracoes
//---------------------------------------------------------------------
void hp_handler (void);
void lp_handler (void);
unsigned char dispatch ();
unsigned char getDebugLevel(void);
void initHardware(void);

// tcpip
static void InitAppConfig(void);
static void InitializeBoard(void);
static void ProcessIO(void);
void SaveAppConfig(void)

#endif	/* RIGEL10_H */

