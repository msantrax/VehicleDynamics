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
#include "rigel10.h"
#include "defines.h"

#include <usart.h>
#include <string.h>
#include <stdio.h>
#include <ctype.h>



/* Variaveis =============================================================*/
unsigned char enviar_comandos = TRUE;
unsigned char cmd_host = FALSE;

/** Pointer de index da linha de comando do Host */
unsigned int cmd_pos = 0;
/** Flag de habilitacao do modo binario */
unsigned char modo_binario = FALSE;
/** Flag de habilitacao do envio do retorno codificado */
unsigned char envie_retorno = FALSE;

/* Area de parametros */
/**
 * Area de armazenamento dos comandos jah decodificados
 */
char param [PARAM_NUM][PARAM_COMP];
/**
 * Area de armazenamento dos token que estao entrando
 */
unsigned char cmd[CMD_BUFFER_LEN];



/**
 * Monta Tela de splash no terminal
 */
void splash_msg(){
           //1234567890123456789012345678901234567890123456789012345678901234567890123456789
    CRLF ; repchar('-',78); CRLF ;
    putsrom "Controlador Centauro - Build Vehicle Dynamics - Versao 9.1.6 \r\n");
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
    putsrom "\r\nCENTAURO:>");
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
                putsrom "OPCBUFOV\r") ;
            }
            else{
                putsrom "\r\nErro !! - Overrun no buffer de comandos");
            }
            break;
        case ERRO_DE_SINTAXE:
            if (modo_binario) {
                putsrom "OPCSINTX\r");
            }
            else{
                putsrom "\r\nErro !! - Comando Desconhecido");
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
	while (BusyUSART());
	WriteUSART(c);
}

/**
 * Inicia terminal lado PIC
 */
void initRS232 (){

    OpenUSART (USART_TX_INT_OFF &
          USART_RX_INT_ON &
          USART_ASYNCH_MODE &
          USART_EIGHT_BIT &
          USART_CONT_RX &
          USART_BRGH_HIGH, 64);

   /* Display a prompt to the USART */
   splash_msg();
   show_prompt();
}


/**
 * Interpreta caratere recem chegado na linha do terminal
 * Vem da ISR low
 * @param token
 */
void receive (){

    unsigned char token_in;
    int i;

    //Obtenha token do host ou do usuario
    token_in = ReadUSART();
    //Comando OK -> trate-o conforme config
    if (modo_binario==TRUE) {
    /*O comando binario -----------------------------------------------------------------------*/

    }
    else{
        /*O comando eh mnemonico -----------------------------------------------------------------------*/
        /*Selecione se o comando estah completo (usuario digitou enter) ou estamos construindo a sintaxe */
        if (token_in == CR){
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
                    if (!modo_binario) {
                        /*Enviar prompt caso em modo mnemonico */
                        show_prompt();
                    }
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
        else if (token_in == '/'){  /* User quer repetir o comando ?  */
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
                putsUSART(cmd);
            }
            else{
                /*O caracter de repeticao de comando estah no meio da linha, provavelmente
                faz parte de uma linha de comando normal, trate-o como caracter comum */
                cmd[cmd_pos++] = token_in;
                sendEcho(token_in);
            }
        }
        else{
            /*Estamos montando o comando no buffer (token != CR) -------------------------*/
            if (cmd_pos == CMD_BUFFER_LEN){     /* Buffer de comandos esta cheio ? */
                envie_erro (CMD_BUFFER_OVERRUN);
                cmd_pos=0;
            }
            else if (token_in == BKSP){    /* Usuario corrigiu digitacao ? */
                /*User digitou Backspace */
                if (!cmd_pos==0){           /*Backspace no inico do prompt nao faz sentido */
                    /*OK - Envie e ajuste o pointer */
                    sendEcho(token_in);
                    cmd_pos--;
                }
            }
            else{
                /* Caracter normal */
                /*Transfira para mailusculas */
                if (islower(token_in) != 0) {
                    token_in = toupper(token_in);
                }
                /*E imprima o caracter */
                if (isprint(token_in)!=0){
                    cmd[cmd_pos++] = token_in;
                    sendEcho(token_in);
                }
            }
        }
    }

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
        putsUSART(cmd);
    }
#endif

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
        putsUSART("Comando Limpo : \r\n");
        draw_rule();
        putsUSART(cmd);
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
            putsUSART (param[i]);
        }
        CRLF ; repchar('-',78); CRLF ;
    }

     if (!strcmppgm2ram(param[0], "DEBUG")) {
        //debug = atoi(param[1]);
        printf ("Nivel de Debug ajustado p/ : %d \r\n", getDebugLevel());
        return TRUE;
    }
#endif

    // Processe os comandos
    return dispatch();

}





