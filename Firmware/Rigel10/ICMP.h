/*********************************************************************
 *
 *                  ICMP Module Defs for Microchip TCP/IP Stack
 *
 *********************************************************************
 * FileName:        ICMP.h
 * Dependencies:    StackTsk.h
 *                  IP.h
 *                  MAC.h
 * 
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Howard Schlunder		03/16/07	Original
 ********************************************************************/
#ifndef __ICMP_H
#define __ICMP_H

void ICMPProcess(NODE_INFO *remote, WORD len);

BOOL ICMPBeginUsage(void);
void ICMPSendPing(DWORD dwRemoteIP);
void ICMPSendPingToHost(BYTE * szRemoteHost);
LONG ICMPGetReply(void);
void ICMPEndUsage(void);

#if defined(__18CXX)
	void ICMPSendPingToHostROM(ROM BYTE * szRemoteHost);
#else
	#define ICMPSendPingToHostROM(a) 	ICMPSendPingToHost((BYTE*)(a))
#endif


#endif
