/*
 * Definition taken from http://www.hertaville.com/interfacing-an-spi-adc-mcp3008-chip-to-the-raspberry-pi-using-c.html
 * Modified to work on Raspberry Pi to be redesigned for scale use for the IOT pet feeder project
*/

//build using g++ -Wall -o petfeed FEEDER.cpp MCP3008SPI.cpp main.cpp

#include "FEEDER.h"
#include <iostream>

using namespace std;


int main(void)
{
    double num;
    int choice;
    bool quit = true;

    //feeder init
    feeder a(0);

    while(quit)
    {
        printf("\n1. Read Value\n2. Zero Scale to current weight\n3. Exit\n");
        scanf("%d", &choice);
        switch(choice){
            case 1:
                num = a.measure();
                printf("%f", num);
                break;
            case 2:
                a.tare();
                printf("Scale Zeroed!\n\n");
                break;
            case 3:
                quit = false;
                break;
            default:
                break;
        }
	while(getchar()!='\n');

    }
    return 0;
}