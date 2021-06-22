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


#ifndef RIGEL12_H
#define	RIGEL12_H

#include "Compiler.h"
#include "GenericTypeDefs.h"


//#define DEBUG

#define CR 0x0d
#define LF 0x0a
#define BKSP 0x08
#define ESC 0x1b

#define TIMEOUT 200000


// Declarations
void processState(void);


#endif	/* RIGEL_H */

