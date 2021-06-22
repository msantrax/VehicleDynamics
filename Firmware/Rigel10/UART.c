/*********************************************************************
 *
 *     UART access routines for C18 and C30
 *
 *********************************************************************
 * FileName:        UART.c
 * Dependencies:    Hardware UART module
 * Processor:       PIC18, PIC24F, PIC24H, dsPIC30F, dsPIC33F
 * Compiler:        Microchip C30 v3.12 or higher
 *					Microchip C18 v3.30 or higher
 *					HI-TECH PICC-18 PRO 9.63PL2 or higher
 * Company:         Microchip Technology, Inc.
 *
 
********************************************************************/
#define __UART_C

#include "TCPIPConfig.h"

#if defined(STACK_USE_UART)

//#include "GenericTypeDefs.h"
#include "TCPIP.h"


BYTE ReadStringUART(BYTE *Dest, BYTE BufferLen)
{
	unsigned char c;
	unsigned char count = 0;

	while(BufferLen--)
	{
		*Dest = '\0';

		while(!DataRdyUART());
		c = ReadUART();

		if(c == '\r' || c == '\n')
			break;

		count++;
		*Dest++ = c;
	}

	return count;
}



#if defined(__18CXX)	// PIC18
	char BusyUSART(void)
	{
		return !TXSTAbits.TRMT;
	}
	
	void CloseUSART(void)
	{
	  RCSTA &= 0x4F;  // Disable the receiver
	  TXSTAbits.TXEN = 0;   // and transmitter
	
	  PIE1 &= 0xCF;   // Disable both interrupts
	}
	
	char DataRdyUSART(void)
	{
		if(RCSTAbits.OERR)
		{
			RCSTAbits.CREN = 0;
			RCSTAbits.CREN = 1;
		}
	  return PIR1bits.RCIF;
	}
	
	char ReadUSART(void)
	{
	  return RCREG;                     // Return the received data
	}
	
	void WriteUSART(char data)
	{
	  TXREG = data;      // Write the data byte to the USART
	}
	
	void getsUSART(char *buffer, unsigned char len)
	{
	  char i;    // Length counter
	  unsigned char data;
	
	  for(i=0;i<len;i++)  // Only retrieve len characters
	  {
	    while(!DataRdyUSART());// Wait for data to be received
	
	    data = getcUART();    // Get a character from the USART
	                           // and save in the string
	    *buffer = data;
	    buffer++;              // Increment the string pointer
	  }
	}
	
	void putsUSART( char *data)
	{
	  do
	  {  // Transmit a byte
	    while(BusyUSART());
	    putcUART(*data);
	  } while( *data++ );
	}
	
	void putrsUSART(const rom char *data)
	{
	  do
	  {  // Transmit a byte
	    while(BusyUSART());
	    putcUART(*data);
	  } while( *data++ );
	}



#endif

#endif	//STACK_USE_UART
