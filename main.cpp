//compile with g++ -o test test.c -l wiringPi
// pinout for wiring pi on pinout.xyz/pinout/wiringpi#

#include <stdio.h>
#include <iostream>
#include <wiringPi.h>
#include <unistd.h>
#include <string.h>
#include <time.h>
#include "client/FeederSettings.h"
#include "client/MqttClient.h"
#include "client/Response.h"
#include "Project/Drivers/RFID/Rfid.hpp"
#include "Project/Drivers/Servo/Servo.hpp"
#include "Project/Drivers/Weight/FEEDER.cpp"
#include "Project/Drivers/Weight/MCP3008SPI.cpp"

using namespace std;

double dispense_time;
bool check_weight = false;
bool ok_to_feed = false;

constexpr char CLIENT_ID[] = "generic id";
constexpr char ADDRESS[] = "ec2-13-57-38-126.us-west-1.compute.amazonaws.com:1883";
const std::string FEEDER_ID = "321";

//**TODO: set up timer to count down from dispense_time, after timer trips, set check_weight

int main(void){
    wiringPiSetup();
    
    Servo servo;	//initialize Servos
    Rfid rfid_control;	//initialize RFID
    feeder feed1(0)	//initialize scale to MCP3008 port 0
//RFID variables
    string tag; // = "55003AAA8540";
    string compare;
//servo variables
    uint8_t dispense_amount = 0; // not being used
    bool doorIsOpen = 1;	//default state of door
//Scale variables
    double weightval = 0; //for weight scale usage
    double tempval = 0; //for if logic
    bool tempvalmeasure = true;
    bool startTimer = false; 
//timers
    time_t starttime, endtime;
    double elapsedtime;

//**Set up link to server
    MqttClient cli(ADDRESS, CLIENT_ID);
    FeederSettings init_settings;

//**add Response object to server to set FeederSettings - KEVIN
    Response resp(&init_settings);
    if (!cli.add_function("/petprototype/feeder/push/" + FEEDER_ID + "/", resp)) {
        std::cout << "function add failed" << std::endl;
    } else {
        std::cout << "function add success" << std::endl;
    }
// get settings from the server
    cli.send_sync(FEEDER_ID);

    while (!init_settings.isValid()) {}    //wait for server to send values

//	std::cout << "settings retrieved" << std::endl;
//**update local variables 
//tag = "55003AAA8540";
//dispense_time = 1000;
//dispense_amount  = 2;
    tag = init_settings.getChipId();
    dispense_time = init_settings.getSettingInterval();
    dispense_amount = (uint8_t)((init_settings.getSettingCup()) * 8);  //impeller divided in 1/8th cups

//**set up Servos, default bowl as open - do not dispense
    servo.Init();

//**set up Scale, set initial weight
    feed1.tare(); //zero out scale

//**Initilize Scale timers
    time_t starttime, endtime;
    double elapsedtime;

//**Initialize RFID
    rfid_control.Initialize();
    rfid_control.SetTag(tag);

//printf("tag to match: %s\n", tag.c_str());
    
    while(true){  //run program

//**check if RFID tag is available
        rfid_control.GetTag(compare);   //check for tag present
        if (rfid_control.CompareTag(tag, compare))  //tags match, allow pet to eat
        {
//printf("compare tag: %s\n", compare.c_str());
	    if (!doorIsOpen)
	    	servo.OpenDoor();
            compare = "0";  
	    doorIsOpen = 1;
        } 
        else if (!rfid_control.CompareTag(tag, compare) && compare != "0") //tags do not match, close feeder
        {
//printf("compare: %s\n", compare.c_str());
	    if (doorIsOpen)
	    	servo.CloseDoor();
            compare = "0";
	    doorIsOpen = 0;
        } 

//**check scale amount - 3 scenarios
        //1 - MUST - when check_weight is true record current weight, set ok_to_feed and allow feeder to 
            //dispense food, check new weight, combine and send to server, clear check_weight
//        if(check_weight){
//            ok_to_feed = true;
//            weightval = feed1.measure();
            //combine and send to server
//            check_weight = false;
//        }

        //2 - MUST - when a change in weight is detected, wait 15 seconds and compare before and after, 
            //record change and send to server
//        if(tempval < weightval-10 || tempval > weightval + 10){//if the current weight is at least 10 grams difference(tweak range for sensitivity in scale, fluctuates a lot)
//            time(&starttime);
//            startTimer = true;
//            tempvalmeasure = false;
//        }
//        if(startTimer) time(&endtime);
        
//        elapsedtime = difftime(endtime,starttime);

//        if(elapsedtime > 60){//triggers after a minute
//            time(&starttime); //prevents this if loop from doing it continuously
//            weightval = feed1.measure();

            //combine and send to server
			//cli.send_mass(FEEDER_ID, /*weightval*/); TODO: @TOAN what variable to put here

//            tempvalmeasure = true;
//            startTimer = false;
//        }

        //3 - periodically check to maintain current weight, should not have significant changes
//        if(tempvalmeasure) tempval = feed1.measure(); //measures if it is not in "eating" mode
        

//**check impeller servo logic based on ok_to_feed flag and dispense_amount, clear ok_to_feed
        
        if(ok_to_feed){
            //dispense here
            servo.RotateFeeder(dispense_amount);
            ok_to_feed = false;
        }
        
//**check FeederSettings.DispenseNow() to see if user wants to feed off schedule
        //set check_weight flag

    }
}
