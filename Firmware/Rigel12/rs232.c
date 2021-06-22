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


#include "rs232.h"
#include "rigel12.h"
#include "OutManager.h"

#include "Tick.h"

#include <usart.h>
#include <string.h>
#include <stdio.h>
#include <ctype.h>



/* Variaveis =============================================================*/

/** Pointer de index da linha de comando do Host */
unsigned int cmd_pos = 0;
/** Flag de habilitacao do modo binario */
unsigned char modo_binario;
/** Flag de habilitacao do envio do retorno codificado */
unsigned char envie_retorno = FALSE;

unsigned char token_in;


unsigned char err_bufovr[] = "\r\nErro !! - Overrun no buffer de comandos";
unsigned char err_stx[] = "\r\nErro !! - Comando Desconhecido";

static DWORD cmd_timeout;


#define CMD_BUFFER_LEN  67
#define PARAM_NUM 8
#define PARAM_COMP 12
#define INPUTBUFFER_LEN 32


unsigned char inputbuffer[INPUTBUFFER_LEN];
BYTE inputbuffer_ptr;
BYTE inputreader_ptr;

/**
 * Area de armazenamento dos comandos jah decodificados
 */
char param [PARAM_NUM][PARAM_COMP];
/**
 * Area de armazenamento dos token que estao entrando
 */
unsigned char cmd[CMD_BUFFER_LEN];

BYTE debug_level;


// Posicao da maquina de estados do terminal
static BYTE TermState;
static enum{
	LISTENING = 0,
	ESC_REQUEST,
	CMD_REQUEST,
	CMD_LISTENING
}STATE_TERM;

unsigned char last_token;

/**
 *	Retorna parametro	
 */
char* getParam(BYTE num) { 
	return param[num][0];
}

/**
 *	Retorna nivel de debug	
 */
BYTE getDebugLevel() { 
	return debug_level;
}

/**
 *	Retorna solicitacao para o lexer	
 */
BYTE hasCmd() { 
	return (token_in !=0);
}



/**
 * Monta Tela de splash no terminal
 */
void splash_msg(){
           //1234567890123456789012345678901234567890123456789012345678901234567890123456789
    CRLF ; repchar('-',78); CRLF ;
    putsrom "Controlador Centauro - Build Vehicle Dynamics - Versao 9.1.8 \r\n");
    putsrom "Segmentos de Codigo - Direitos Reservados \r\n");
    putsrom "Opus Ciencia Ltda. - 2011/2012 \r\n");
    repchar('-',78); CRLF ;
}

/**
 * Desenha regua com caracter no terminal
 * @param c
 *  Caracter a preencher
 * @param count
 *  Numero de caracteres
 */
void repchar(unsigned char c, unsigned char count){
    while (count != 0 ){
        sendEcho(c);
        count--;
    }
}

/**
 * Mostra prompt de comando no terminal
 */
void show_prompt(){
    
	
    if (!modo_binario) {
    	putsrom "\r\nCENTAURO:>");
    }
    else{
//		initCommand(3);
//        appendMsg(sprintf(getOutbfPtr(), "CENTAURO:"));
//        sendCommand ();
	}
}

/**
 * Desenha régua de medicao no terminal
 */
void draw_rule(){
    putsrom "\n\r1234567890123456789012345678901234567890\n\r");
}

/**
 * Envia mensagem de erro ao terminal
 * @param erro
 *  Tipo de erro
 */
void envie_erro (int erro){
		
		
    switch(erro){
        case CMD_BUFFER_OVERRUN:
            if (modo_binario) {
//              initCommand(3);
//        		appendMsg(sprintf(getOutbfPtr(), (rom char *)&err_bufovr));
//        		sendCommand ();
            }
            else{
                puts1USART((char *)err_bufovr);
            }
            break;
        case ERRO_DE_SINTAXE:
            if (modo_binario) {
//                initCommand(3);
//        		appendMsg(sprintf(getOutbfPtr(),"Erro: %u %u\r\n", inputbuffer_ptr, inputreader_ptr));
//        		sendCommand ();
            }
            else{
                puts1USART((char *)err_stx);
            }
            break;
        case ERRO_SPI:
            if (modo_binario) {
//                initCommand(3);
//        		appendMsg(sprintf(getOutbfPtr(),"Erro: %u %u\r\n", inputbuffer_ptr, inputreader_ptr));
//        		sendCommand ();
            }
            else{
                puts1USART((char *)"\n\r Timeout na SPI");
            }
            break;
    }
}

/**
 * Devolve caracter digitado ao terminal
 * @param c
 *  Caractere a enviar
 */
void sendEcho( char c){
	while (Busy1USART());
	Write1USART(c);
}

/**
 * Inicia terminal lado PIC
 */
void initRS232 (){

	//unsigned char config, spbrg, baudconfig;

	modo_binario = FALSE;
	TermState = LISTENING;
	//resetOutManager();
	inputbuffer_ptr=0;
	inputreader_ptr=0;	

	
	// Ajustar pinos das saidas RS232 (CONF. PAGINA 267 DO DATASHEET)
	TRISCbits.RC6=0; // TX-> output
	TRISCbits.RC7=1; // (conf. pagina 268)
	ANSELCbits.ANSC7=0; //No analog
	ANSELCbits.ANSC6=0; //No analog

	Open45k22();
	
	  
    if (!modo_binario) {
	    splash_msg();
	    show_prompt();
	}   
 
}

void Open45k22(){
  
  TXSTA1 = 0;          	// Reset USART registers to POR state
  RCSTA1 = 0;
  TXSTA1bits.SYNC = 0;	// Sync or async operation
  TXSTA1bits.TX9 = 0;	// 8 bits (TX)	
  RCSTA1bits.RX9 = 0;	// TX
  RCSTA1bits.CREN = 1;	// Habilita continuous receiver
  TXSTA1bits.BRGH = 1;	// Baud Rate = High
  RCSTA1bits.ADDEN = 0;	// No address detection
  IPR1bits.RC1IP = 0;	// Receiver interrupt = low priority
  PIE1bits.RC1IE=1;		// Habilita RX interrups na serial 1;
  PIE1bits.TX1IE = 0;	// Mas nao TX
    
  SPBRG1 = 64;       	// Write baudrate to SPBRG1
  SPBRGH1 = 64 >> 8; 	// For 16-bit baud rate generation

		
  TXSTA1bits.TXEN = 1;  // Enable transmitter
  RCSTA1bits.SPEN = 1;  // Enable USART  
}




/**
*	Recebe o interrupt de recepcao da Usart e armazena rapido no buffer
*/
void inputUSART(){
	
	//LATCbits.LATC2 =1;
	// O buffer eh circular	
	inputbuffer[inputbuffer_ptr] = Read1USART();	
	inputbuffer_ptr++;
	if (inputbuffer_ptr > (INPUTBUFFER_LEN-1)) inputbuffer_ptr = 0;
	//Delay100TCYx(100);
	//LATCbits.LATC2 =0;
}



/**
 * Interpreta caracter recem chegado na linha do terminal
 * Vem da ISR low
 * @param token
 */
void receive (){

 	if (inputbuffer_ptr == inputreader_ptr) return;
    
    //Obtenha token do host ou do usuario
    token_in = inputbuffer[inputreader_ptr];    
    inputreader_ptr++;
    if (inputreader_ptr > (INPUTBUFFER_LEN-1)) inputreader_ptr = 0;
  
    if (modo_binario==TRUE) {
//    	/*O comando eh binario ------------------------------------------------------------------*/
//		switch (TermState){	
//			case LISTENING :
//				last_token = token_in;
//				if (token_in == 27 && modo_binario == TRUE) {
//					sendEcho('?');				
//					TermState = ESC_REQUEST;
//				}
//				if (token_in == 0xFE) TermState = CMD_REQUEST;
//				//token_in=0;
//				break;
//			case ESC_REQUEST :
//				token_in &=0xdf;
//				if (token_in == 'O'){
//					last_token = token_in;
//					sendEcho(token_in);
//				}
//				else if (token_in == 'I' && last_token == 'O'){
//					modo_binario = FALSE;
//					/* Display o prompt no terminal */
//   					splash_msg();
//   					show_prompt();	
//				}
//				else{
//					TermState = LISTENING;
//				}
//				//token_in=0;
//				break;
//			case CMD_REQUEST :
//				if (token_in == 0xFF){
//					TermState = CMD_LISTENING;
//					//cmd_timeout = TickGet() + (1.5 * TICK_SECOND);	
//				}
//				else{
//					TermState = LISTENING;
//				}
//				//token_in=0;
//				break;	
//			case CMD_LISTENING :
//				if (token_in == 0xFF){
//					//token_in=CR;
//					cmdGate(CR);
//					TermState = LISTENING;	
//				}
////				else if (TickGet() > cmd_timeout){
////					cmdGate(CR);
////					TermState = LISTENING;
////				}
//				else{
//					cmdGate(token_in);
//				}
//				break;	  		
//    	}	
    }
	else{
        /*O comando eh mnemonico ----------------------------------------------------------------*/
        cmdGate(token_in);
	}
}


/**
* Recebe token que deu entrada no terminal e o coloca na estrutura de comandos no lugar exato
*/
void cmdGate(unsigned char cmd_token){

		BYTE i;

		if (cmd_token == 0) cmd_token = token_in;
		
		envie_retorno=TRUE;

		/*Selecione se o comando estah completo (usuario digitou enter) ou estamos construindo a sintaxe */
        if (cmd_token == CR){
            /*OK - comando completo, verifique e despache ... */
            if (cmd_pos==0){      /*User soh pressionou enter ? (comando nulo ...) envie soh o prompt*/
                show_prompt ();
            }
            else{
                /*Linha  de comando terminou e supostamente temos um comando valido,
                adicione o enter (como delimitador para o lexer) e termine a string com NULL */
                cmd[cmd_pos++] = CR;
                cmd[cmd_pos++] = 0;
                /*Agora verifique e execute o comando solicitado */
                if (lexer()== TRUE ){
                	/*Lexer executou com sucesso, aguardar proximo comando */
                    if (envie_retorno) show_prompt();
                }
                else{
                    /*Lexer encontrou erro, sinalize e aguarde proximo comando */
                    envie_erro (ERRO_DE_SINTAXE);
                    show_prompt();
                }
                /*Comando executado, resete o pointer e execute outro loop */
                cmd_pos=0;
            }
        }
        // User estah montando o comando
        else if (cmd_token == '/' && (!modo_binario)){  /* User quer repetir o comando ?  */
            if (cmd_pos==0){
                /*Quer sim, digitou-o no inicio do prompt. Encontre o CR no final do
                Ultimo comando, envie o comando de volta para o usuario e ajuste
                o pointer de edicao apropriadamente. Retire o CR da estrutura pois
                nao queremos que o cursor volte ao inicio do prompt. */
                for ( i=0; i < CMD_BUFFER_LEN; i++){
                        if (cmd[i] == CR) {
                            cmd[i]= 0;
                            cmd_pos=i;
                            break;
                        }
                }
                // Faca o servico para o user
                puts1USART(cmd);
            }
            else{
                /*O caracter de repeticao de comando estah no meio da linha, provavelmente
                faz parte de uma linha de comando normal, trate-o como caracter comum */
                cmd[cmd_pos++] = cmd_token;
                sendEcho(cmd_token);
            }
        }
        else{
            /*Estamos montando o comando no buffer (token != CR) -------------------------*/
            if (cmd_pos == CMD_BUFFER_LEN){     /* Buffer de comandos esta cheio ? */
                envie_erro (CMD_BUFFER_OVERRUN);
                cmd_pos=0;
            }
            else if (cmd_token == BKSP && (!modo_binario)){    /* Usuario corrigiu digitacao ? */
                /*User digitou Backspace */
                if (!cmd_pos==0){           /*Backspace no inico do prompt nao faz sentido */
                    /*OK - Envie e ajuste o pointer */
                    sendEcho(cmd_token);
                    cmd_pos--;
                }
            }
            else{
                /* Caracter normal */
                /*Transfira para mailusculas */
                if (islower(cmd_token) != 0) {
                    cmd_token = toupper(cmd_token);
                }
                /*E imprima o caracter */
                if (isprint(cmd_token)!=0){
                    cmd[cmd_pos++] = cmd_token;
                    if (!modo_binario) sendEcho(cmd_token);
                }
            }
        }	
		
		token_in=0;
}


/**
 * Decodifica comando que deu entrada no terminal
 * @return
 */
unsigned char lexer () {

    register int i;
    register int w;
    register char token1;

    unsigned char param_ptr=0;
    unsigned char spflag = FALSE;


    /*Determina verbos, entidades e qualificadores na linha de comando usando o delimitador espaco. */

#ifdef DEBUG
    //Desenhe uma regua caso esteja em debug
    if (getDebugLevel() == 10) {
        draw_rule();
        puts1USART(cmd);
    }
#endif

//	initCommand(3);
//    appendMsg(sprintf(getOutbfPtr(), "Comando %s\r\n", cmd ));
//    sendCommand ();
//

    /* Execute a varredura de limpeza do buffer */
    for (i=0,w=0; i < CMD_BUFFER_LEN; i++, w++){

        token1=cmd[i];

        if (isspace (token1)){
            // Espaco encontrado, considere condicionantes
            if(spflag){
                /*Jah nao havia um espaco anterior ou outro delimitador de parametro, ignore
                portanto o espaco atual e mantenha o pointer fixo */
                w--;
            }
            else{
                //Primeiro espaco no parametro, mantenha-o de forma a indicar delimitador
                cmd[w]=token1;
                spflag = TRUE;
            }
        }
        else if (token1 == '='){
            //Encontramos equates, contextualize ...
            if (cmd[w-1] == ' '){
                //Havia espaco anterior ao equate, substitua-o pelo dito cujo e fixe o pointer
                w--;
                cmd[w]=token1;
            }
            else {
                /*Operaco normal, soh insira (lembrar que um equate vale um espaco no parser)
                portanto levante a spflag */
                cmd[w]=token1;
            }
            spflag = TRUE;
        }
        else if (token1 == CR){
            //Final do comando
            cmd[w]=CR;
            cmd [w++]=0;
            break;
        }
        else {
            //Token normal de composicao de comando
            cmd[w]=token1;
            spflag = FALSE;
        }
    }


#ifdef DEBUG
    // Buffer limpou separe os comandos
    //Desenhe uma regua caso esteja em debug
    if (getDebugLevel() == 10) {
        puts1USART("Comando Limpo : \r\n");
        draw_rule();
        puts1USART(cmd);
    }
#endif

    /* Limpe Area de parametros */
    for (i=0;i < PARAM_NUM;i++){
        for (w=0;w < PARAM_COMP;w++){
            param [i] [w]=0;
        }
    }
    w=0;

    /* Execute o parse dos comandos  */
    for (i=0;i < CMD_BUFFER_LEN;i++){
        token1=cmd[i];
        /* Delimitador ? */
        if (token1 == ' ' || token1 == '='){
            param_ptr++;
            w=0;
        }
        else if (!token1 || token1 == CR) {
            /* Fim da linha de comando */
            break;
        }
        else {
            /*Copiar parametro no slot correspondente,
            Verifique overflow no slot tambem */
            if (w < PARAM_COMP-1) {
                param[param_ptr] [w] = token1;
                w++;
            }
            else{
                // Parametro estourou o slot, desista e anuncie
                return FALSE;
            }
        }
    }

#ifdef DEBUG
    // Mostre os dados de Debug caso solicitado
    if (getDebugLevel() == 10) {
        CRLF  ; repchar('-',14); CRLF ;
        putsrom " Mapa da Area de Parametros ");
        CRLF ;

        for (i=0;i < 10;i++){
            printf ("\r\n\tParam[%02d]= ", i);
            puts1USART (param[i]);
        }
        CRLF ; repchar('-',78); CRLF ;
    }

     if (!strcmppgm2ram(param[0], "DEBUG")) {
        debug_level = atoi(param[1]);
        printf ("Nivel de Debug ajustado p/ : %d \r\n", getDebugLevel());
        return TRUE;
    }
#endif

    // Processe os comandos
    //return TRUE;
    return dispatch();

}



/**
 * Identifica e despacha comandos
 * @return
 */
BYTE dispatch (){

	int i;

	  
    // Mostra status das flags de habilitcao e outros quetais
    if (!strcmppgm2ram(param[0], "GETID")) {
        //initCommand(3);
        printf ("\tCentauro status = %u\r\n", 32 );
        //appendMsg(sprintf(getOutbfPtr(), "\tCentauro status = %u\r\n", 32 ));
        //sendCommand ();
        return TRUE;
    }
    
   
	
	if (!strcmppgm2ram(param[0], "MODO")) {
        CRLF;
        if (!strcmppgm2ram(param[1], "BINARIO")) {
        	putsrom "Passando para modo binario\r\n");
        	modo_binario = TRUE;
        	TermState = LISTENING;
        }	
	}
	
    return FALSE;

}




    
//	LATEbits.LATE1 ^= 1;		
//    Delay100TCYx(10);
// 	LATEbits.LATE1 ^= 1;
//
	  
	  
