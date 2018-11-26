//compile with g++ -o test test.c -l wiringPi
// pinout for wiring pi on pinout.xyz/pinout/wiringpi#

#include <stdio.h>
#include <iostream>
#include <wiringPi.h>
#include <unistd.h>
#include <string.h>
#include "Projects/Drivers/RFID/Rfid.hpp"
#include "Projects/Drivers/Servo.hpp"
#include "clients/FeederSettings.h"
#include "Projects/Drivers/Weight/Feeder.cpp"
#include "Projects/Drivers/Weight/MCP3008SPI.cpp"

using namespace std;

double dispense_time;
uint8_t check_weight = 0;
uint8_t ok_to_feed = 0;

//**TODO: set up timer to count down from dispense_time, after timer trips, set check_weight

int main(void){
    wiringPiSetup();

    Rfid rfid_control;
    string tag; // = "55003AAA8540";
    string compare;
    float dispense_amount = 0;


//**TODO: add wait loop to get server information
    //tag = chipID from payload
    //amount to feed - convert to impeller rotation
    //dispense_time = in seconds from payload - set up timer?
    FeederSettings init_settings;

//**add Response object to server to set FeederSettings - KEVIN

    while (!init_settings.is_valid()) {}    //wait for server to send values

    tag = init_settings.getChipID();
    dispense_time = init_settings.getSettingInterval();
    dispense_amount = (init_settings.getSettingCup()) * 8;  //impeller divided in 1/8th cups

//**Initialize Servos, default bowl as open - do not dispense

//**Initialize Scale, set initial weight
    feeder feed1(0); //initialize feeder1 to MCP3008 port 0
    feed1.tare(); //zero out scale
    double weightval = 0; //for weight scale usage
    double tempval = 0; //for if logic

//**Initialize RFID
    rfid_control.Initialize();
    rfid_control.SetTag(tag);

    while(true){

//**check if RFID tag is available
        rfid_control.GetTag(compare);   //check for tag present
        if (rfid_control.CompareTag(tag, compare))  //tags match, allow pet to eat
        {
    //**TODO: add logic to ensure bowl is open
            compare = "0";  
        } 
        else if (!rfid_control.CompareTag(tag, compare) && compare != "0") //tags do not match, close feeder
        {
    //**TODO: add logic to ensure bowl is closed
            compare = "0";
        } 

//**check scale amount - 3 scenarios
        //1 - MUST - when check_weight is true record current weight, set ok_to_feed and allow feeder to 
            //dispense food, check new weight, combine and send to server, clear check_weight
        /*
        if(check_weight == True){
            ok_to_feed = True;
            weightval = feed1.measure();
            //combine and send to server
            check_weight = False;
        }
        */
        //2 - MUST - when a change in weight is detected, wait 15 seconds and compare before and after, 
            //record change and send to server
        /*
        if(tempval < weightval-10 || tempval > weightval + 10){//if the current weight is at least 10 grams difference(tweak range for sensitivity in scale, fluctuates a lot)
            weightval = feed1.measure();
            //combine and send to server
        }
        */
        //3 - periodically check to maintain current weight, should not have significant changes
        /*
        tempval = feed1.measure();
        */

//**check impeller servo logic based on ok_to_feed flag and dispense_amount, clear ok_to_feed

//**check FeederSettings.DispenseNow() to see if user wants to feed off schedule
        //set check_weight flag

    }
}
