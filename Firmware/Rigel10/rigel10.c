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


#include "rigel10.h"
#include "TCPIP.h"
#include "Compiler.h"


#ifdef DEBUG
    unsigned char debug_level = 1;
#endif

#define THIS_IS_STACK_APPLICATION

APP_CONFIG AppConfig;
BYTE AN0String[8];




//---------------------------------------------------------------------
//ISRs
//---------------------------------------------------------------------
#pragma code hp_interrupt = 0x8
void hp_int (void){
	_asm goto hp_handler _endasm
}

#pragma code lp_interrupt = 0x18
void lp_int (void){
	_asm goto lp_handler _endasm
}


#pragma code
#pragma interrupt hp_handler
void hp_handler (void){

#ifdef RS232
    receive();
#endif

    /* Limpe a flag do interrupt de recepcao */
    PIR1bits.RCIF = 0;

}

#pragma interrupt lp_handler
void lp_handler (void){

    TickUpdate();
}


/**
 * Rotima de entrada no sistema
 * @return 
 */
void main(void) {


    static DWORD t = 0;
    static DWORD dwLastIP = 0;

    // Configure mo hardware
    initHardware();


#ifdef RS232
    initRS232();
#endif

    /* Habilite prioridades no interrupt*/
    RCONbits.IPEN = 1;
    /* Defina Interrupt de recepcao como HIGH priority */
    IPR1bits.RCIP = 1;

    /* Habilite os interrupts high priority */
    INTCONbits.GIEH = 1;
    /* E tambem os low priority*/
    INTCONbits.GIEL = 1;


    // Inicie possivel hardware relativo ao stack
    TickInit();

    // Inicie as variaveis de configuracao
    InitAppConfig();

    // Inicie os stack layers (MAC, ARP, TCP, UDP) e os modulos (HTTP, SNMP, etc.)
    StackInit();


    /* Loop forever */
    while (1){

        StackTask();

        StackApplications();

        ProcessIO();
   }
  
}

/**
 * Configura portas e outros periféricos
 */
void initHardware(){
    
    
    
    
}

/**
 * Processa tarefas especificas do controlador
 * @return
 */
static void ProcessIO(void){


}


static ROM BYTE SerializedMACAddress[6] = {MY_DEFAULT_MAC_BYTE1, MY_DEFAULT_MAC_BYTE2, MY_DEFAULT_MAC_BYTE3, MY_DEFAULT_MAC_BYTE4, MY_DEFAULT_MAC_BYTE5, MY_DEFAULT_MAC_BYTE6};

/**
 *
 */
static void InitAppConfig(void){

    AppConfig.Flags.bIsDHCPEnabled = TRUE;
    AppConfig.Flags.bInConfigMode = TRUE;
    memcpypgm2ram((void*)&AppConfig.MyMACAddr, (ROM void*)SerializedMACAddress, sizeof(AppConfig.MyMACAddr));
    AppConfig.MyIPAddr.Val = MY_DEFAULT_IP_ADDR_BYTE1 | MY_DEFAULT_IP_ADDR_BYTE2<<8ul | MY_DEFAULT_IP_ADDR_BYTE3<<16ul | MY_DEFAULT_IP_ADDR_BYTE4<<24ul;
    AppConfig.DefaultIPAddr.Val = AppConfig.MyIPAddr.Val;
    AppConfig.MyMask.Val = MY_DEFAULT_MASK_BYTE1 | MY_DEFAULT_MASK_BYTE2<<8ul | MY_DEFAULT_MASK_BYTE3<<16ul | MY_DEFAULT_MASK_BYTE4<<24ul;
    AppConfig.DefaultMask.Val = AppConfig.MyMask.Val;
    AppConfig.MyGateway.Val = MY_DEFAULT_GATE_BYTE1 | MY_DEFAULT_GATE_BYTE2<<8ul | MY_DEFAULT_GATE_BYTE3<<16ul | MY_DEFAULT_GATE_BYTE4<<24ul;
    AppConfig.PrimaryDNSServer.Val = MY_DEFAULT_PRIMARY_DNS_BYTE1 | MY_DEFAULT_PRIMARY_DNS_BYTE2<<8ul  | MY_DEFAULT_PRIMARY_DNS_BYTE3<<16ul  | MY_DEFAULT_PRIMARY_DNS_BYTE4<<24ul;
    AppConfig.SecondaryDNSServer.Val = MY_DEFAULT_SECONDARY_DNS_BYTE1 | MY_DEFAULT_SECONDARY_DNS_BYTE2<<8ul  | MY_DEFAULT_SECONDARY_DNS_BYTE3<<16ul  | MY_DEFAULT_SECONDARY_DNS_BYTE4<<24ul;
	

    // Load the default NetBIOS Host Name
    memcpypgm2ram(AppConfig.NetBIOSName, (ROM void*)MY_DEFAULT_HOST_NAME, 16);
    FormatNetBIOSName(AppConfig.NetBIOSName);


    #if defined(EEPROM_CS_TRIS)
    {
            BYTE c;

        // When a record is saved, first byte is written as 0x60 to indicate
        // that a valid record was saved.  Note that older stack versions
        // used 0x57.  This change has been made to so old EEPROM contents
        // will get overwritten.  The AppConfig() structure has been changed,
        // resulting in parameter misalignment if still using old EEPROM
        // contents.
            XEEReadArray(0x0000, &c, 1);
        if(c == 0x60u)
                XEEReadArray(0x0001, (BYTE*)&AppConfig, sizeof(AppConfig));
        else
            SaveAppConfig();
    }
    #elif defined(SPIFLASH_CS_TRIS)
    {
            BYTE c;

            SPIFlashReadArray(0x0000, &c, 1);
            if(c == 0x60u)
                    SPIFlashReadArray(0x0001, (BYTE*)&AppConfig, sizeof(AppConfig));
            else
                    SaveAppConfig();
    }
    #endif
}


#if defined(EEPROM_CS_TRIS) || defined(SPIFLASH_CS_TRIS)
void SaveAppConfig(void){

    // Ensure adequate space has been reserved in non-volatile storage to
    // store the entire AppConfig structure.  If you get stuck in this while(1)
    // trap, it means you have a design time misconfiguration in TCPIPConfig.h.
    // You must increase MPFS_RESERVE_BLOCK to allocate more space.
    #if defined(STACK_USE_MPFS) || defined(STACK_USE_MPFS2)
            if(sizeof(AppConfig) > MPFS_RESERVE_BLOCK)
                    while(1);
    #endif

    #if defined(EEPROM_CS_TRIS)
        XEEBeginWrite(0x0000);
        XEEWrite(0x60);
        XEEWriteArray((BYTE*)&AppConfig, sizeof(AppConfig));
#else
        SPIFlashBeginWrite(0x0000);
        SPIFlashWrite(0x60);
        SPIFlashWriteArray((BYTE*)&AppConfig, sizeof(AppConfig));
#endif
}
#endif




/**
 * Identifica e despacha comandos
 * @return
 */
byte dispatch (){

    // Mostra status das flags de habilitaÃ§ao e outros quetais
    if (!strcmppgm2ram(param[0], "INFO")) {
        CRLF;
        printf ("SCR -> Largura de Pulso : %u \r\n", 1000);
        return TRUE;
    }


    // Mostra status das flags de habilitaÃ§Ã£o e outros quetais
//    if (!strcmppgm2ram(param[0], "SET")) {
//        CRLF;
//        if (!strcmppgm2ram(param[1], "SCR")) {
//            if (!strcmppgm2ram(param[2], "CICLO")) {
//                scr_cycleperiod = scr_convert(param[3]);
//                printf ("SCR->Ciclo : %u \r\n", scr_cycleperiod);
//                return TRUE;
//            }
//            if (!strcmppgm2ram(param[2], "PULSO")) {
//                scr_pulselenght = scr_convert(param[3]);
//                printf ("SCR->Largura do pulso : %u \r\n", scr_pulselenght);
//                return TRUE;
//            }
//            if (!strcmppgm2ram(param[2], "BANDGAP")) {
//                scr_bandgap = scr_convert(param[3]);
//                printf ("SCR->BandGap : %u \r\n", scr_bandgap);
//                return TRUE;
//            }
//            if (!strcmppgm2ram(param[2], "MEDLINHA")) {
//                scr_measureac = scr_convert(param[3]);
//                printf ("SCR->Medida da Linha : %u \r\n", scr_measureac);
//                return TRUE;
//            }
//        }
//        if (!strcmppgm2ram(param[1], "POT")) {
//            if (!strcmppgm2ram(param[2], "1")) {
//                scr_pwr[0]=atoi(param[3]);
//                printf ("POT 1 : %d \r\n", scr_pwr[0]);
//                return TRUE;
//            }
//            if (!strcmppgm2ram(param[2], "2")) {
//                scr_pwr[1]=atoi(param[3]);
//                printf ("POT 2 : %d \r\n", scr_pwr[1]);
//                return TRUE;
//            }
//            if (!strcmppgm2ram(param[2], "3")) {
//                scr_pwr[2]=atoi(param[3]);
//                printf ("POT 3 : %d \r\n", scr_pwr[2]);
//                return TRUE;
//            }
//        }


    return FALSE;

}


// Get Set Area

#ifdef DEBUG
    byte getDebugLevel(void) { return debug_level;}
#endif



