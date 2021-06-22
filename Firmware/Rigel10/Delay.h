/*********************************************************************
 *
 *                  General Delay rouines
 *
 *********************************************************************
 * FileName:        Delay.h
 * Dependencies:    Compiler.h
 
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Nilesh Rajbharti     5/9/02  Original        (Rev 1.0)
 * Nilesh Rajbharti     6/10/02 Fixed C18 ms and us routines
 * Howard Schlunder		4/04/06	Changed for C30
 ********************************************************************/
#ifndef __DELAY_H
#define __DELAY_H

#include "Compiler.h"
#include "HardwareProfile.h"

#if defined(__18CXX) && !defined(HI_TECH_C)
	#include <delays.h>
#endif

#if !defined(GetInstructionClock)
	#error GetInstructionClock() must be defined.
#endif

#if defined(__18CXX) && !defined(HI_TECH_C)
	#define Delay10us(us)		Delay10TCYx(((GetInstructionClock()/1000000)*(us)))
	#define DelayMs(ms)
	do{
		unsigned int _iTemp = (ms);
		while(_iTemp--)							\
			Delay1KTCYx((GetInstructionClock()+999999)/1000000);
	} while(0)
#else
	#define Delay10us(x)			
	do 								
	{								
		unsigned long _dcnt;		
		_dcnt=x*((unsigned long)(0.00001/(1.0/GetInstructionClock())/6));	
		while(_dcnt--);				
	} while(0)
	void DelayMs(WORD ms);
#endif



#endif
