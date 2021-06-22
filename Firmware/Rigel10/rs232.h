/* 
 * File:   rs232.h
 * Author: Opus1
 *
 * Created on 31 de Julho de 2012, 10:06
 */

#ifndef RS232_H
#define	RS232_H



#define CMD_BUFFER_LEN  67
#define PARAM_NUM 10
#define PARAM_COMP 15

/* Macros de suporte a transmissao*/
#define putsrom putrsUSART ((const far rom char *)
#define CRLF putsrom "\r\n")

/* Tipos de Erro */
#define CMD_BUFFER_OVERRUN 1
#define ERRO_DE_SINTAXE 2



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

#endif	/* RS232_H */

