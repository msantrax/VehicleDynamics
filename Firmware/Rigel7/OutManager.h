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


#ifndef OUTMANAGER_H
#define	OUTMANAGER_H



// Declarations
void sendComand ();
void resetOutManager(void);
BYTE *getOutbfPtr();
void appendMsg(int tokens_written);
unsigned int getChannel(BYTE channel, BYTE round);
void enableScanSend(BYTE enable);

#endif	/* OUTMANAGER_H */
