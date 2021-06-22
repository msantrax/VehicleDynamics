/*********************************************************************
 *
 *                  Tick Manager for PIC18
 *
 *********************************************************************
 * FileName:        Tick.h
 
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Nilesh Rajbharti     6/28/01 Original        (Rev 1.0)
 * Nilesh Rajbharti     2/9/02  Cleanup
 * Nilesh Rajbharti     5/22/02 Rev 2.0 (See version.log for detail)
 ********************************************************************/
#ifndef __TICK_H
#define __TICK_H

//Defina Peripheral clock aqui quando não estamos usando o stack TCP
#if defined(THIS_IS_STACK_APPLICATION)
	#include "TCPIP.h"
#else
	#define GetSystemClock()		(20000000ul)      // Hz
	#define GetInstructionClock()	(GetSystemClock()/4)
	#define GetPeripheralClock()	GetInstructionClock()
#endif

//#include "TCPIP.h"

// All TICKS are stored as 32-bit unsigned integers.
// This is deprecated since it conflicts with other TICK definitions used in 
// other Microchip software libraries and therefore poses a merge and maintence 
// problem.  Instead of using the TICK data type, just use the base DWORD data 
// type instead.
typedef __attribute__((__deprecated__)) DWORD TICK;


// Valor do Timer para : prescaler = 1/64 - fd88=64.904 ou 632 to go - 120 Hz de tick - 8.33 ms
//#define TICK_T0H 0xfd
//#define TICK_T0L 0x88


// This value is used by TCP and other modules to implement timeout actions.
// For this definition, the Timer must be initialized to use a 1:256 prescalar 
// in Tick.c.  If using a 32kHz watch crystal as the time base, modify the 
// Tick.c file to use no prescalar.
#define TICKS_PER_SECOND		(19531ul)//((GetPeripheralClock()+128ull)/256ull)	// Internal core clock drives timer with 1:256 prescaler
//#define TICKS_PER_SECOND		(32768ul)				// 32kHz crystal drives timer with no scalar

// Represents one second in Ticks
#define TICK_SECOND				((QWORD)TICKS_PER_SECOND)
// Represents one minute in Ticks
#define TICK_MINUTE				((QWORD)TICKS_PER_SECOND*60ull)
// Represents one hour in Ticks
#define TICK_HOUR				((QWORD)TICKS_PER_SECOND*3600ull)


void TickInit(void);
DWORD TickGet(void);
DWORD TickGetDiv256(void);
DWORD TickGetDiv64K(void);
DWORD TickConvertToMilliseconds(DWORD dwTickValue);
void TickUpdate(void);

#endif
