/* 
 * File:   rs232.h
 * Author: Opus1
 *
 * Created on 31 de Julho de 2012, 10:06
 */

#ifndef RS232_H
#define	RS232_H

#include "GenericTypeDefs.h"


/* Macros de suporte a transmissao*/
#define putsrom putrsUSART ((const far rom char *)

#define CRLF putsrom "\r\n")

/* Tipos de Erro */
#define CMD_BUFFER_OVERRUN 1
#define ERRO_DE_SINTAXE 2
#define SPI_TIMEOUT 3

// Defines da RS232

// If KHZ is not specified by the makefile, assume it to be 4 MHZ
#ifndef KHZ
#define KHZ	20000
#endif

// Twiddle these as you like BUT remember that not all values work right!
// See the datasheet for what values can work with what clock frequencies.
#define	BAUD	19200
#define BAUD_HI	1

// This section calculates the proper value for SPBRG from the given
// values of BAUD and BAUD_HI.  Derived from Microchip's datasheet.
#if	(BAUD_HI == 1)
#define	BAUD_FACTOR	(16L*BAUD)
#else
#define	BAUD_FACTOR	(64L*BAUD)
#endif
#define SPBRG_VALUE	(unsigned char)(((KHZ*1000L)-BAUD_FACTOR)/BAUD_FACTOR)

// Lookup table for hex-to-ascii macro
static const char bhex[]={'0','1','2','3','4','5','6','7','8','9',
						 'A','B','C','D','E','F'};

// Wait until the PIC is finished transmitting over RS232
#define FLUSH()		while(!PIR1bits.TXIF)

// Sends a literal character down the RS232 pipe.  I.E.  SEND('Q');
#define SEND(C)		do {	FLUSH();	TXREG=C;	} while(0)

// Sends two hex chars.  I.E.  SENDHEX(0xab) will send 'A', then 'B'
#define SENDHEX(H)	do {	SEND(hex[(H)>>4]); SEND(hex[(H)&0x0f]); } while(0)





//---------------------------------------------------------------------
//Declaracoes
//---------------------------------------------------------------------
void sendEcho(char c);
void splash_msg(void);
void show_prompt(void);
void envie_erro (int erro);
void draw_rule(void);
void repchar(unsigned char c, unsigned char count);
unsigned char lexer(void);
void receive(void);
void initRS232(void);
char* getParam(BYTE num);
BYTE getDebugLevel();

BYTE dispatch ();
void cmdGate(unsigned char cmd_token);
void buildComand (BYTE type, unsigned char *msg);
BYTE hasCmd();
void inputUSART();
BYTE isBinary();

#endif	/* RS232_H */

