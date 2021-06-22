/*********************************************************************
 *
 *                  ARP Module Defs for Microchip TCP/IP Stack
 *
 *********************************************************************
 * FileName:        ARP.h
 * Dependencies:    Stacktsk.h
 *                  MAC.h
 
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Nilesh Rajbharti     5/1/01  Original        (Rev 1.0)
 * Nilesh Rajbharti     2/9/02  Cleanup
 * Nilesh Rajbharti     5/22/02 Rev 2.0 (See version.log for detail)
 * Howard Schlunder		8/17/06	Combined ARP.h and ARPTsk.h into ARP.h
 ********************************************************************/
#ifndef __ARP_H
#define __ARP_H

#include "StackTsk.h"
#include "MAC.h"

#ifdef STACK_CLIENT_MODE
	void ARPInit(void);
#else
	#define ARPInit()
#endif

#ifndef _ARP_STATUS
#define _ARP_STATUS
#define SM_ARP_IDLE 0
#define SM_ARP_REPLY 1
#endif



#define ARP_OPERATION_REQ       0x0001u		// Operation code indicating an ARP Request
#define ARP_OPERATION_RESP      0x0002u		// Operation code indicating an ARP Response

#define HW_ETHERNET             (0x0001u)	// ARP Hardware type as defined by IEEE 802.3
#define ARP_IP                  (0x0800u)	// ARP IP packet type as defined by IEEE 802.3


// ARP packet structure
typedef struct __attribute__((aligned(2), packed))
{
    WORD        HardwareType;
    WORD        Protocol;
    BYTE        MACAddrLen;
    BYTE        ProtocolLen;
    WORD        Operation;
    MAC_ADDR    SenderMACAddr;
    IP_ADDR     SenderIPAddr;
    MAC_ADDR    TargetMACAddr;
    IP_ADDR     TargetIPAddr;
} ARP_PACKET;

BOOL ARPProcess(void);
void ARPResolve(IP_ADDR* IPAddr);
BOOL ARPIsResolved(IP_ADDR* IPAddr, MAC_ADDR* MACAddr);
void SwapARPPacket(ARP_PACKET* p);


#endif


