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

constexpr char CLIENT_ID[] = "generic id";
constexpr char ADDRESS[] = "ec2-13-57-38-126.us-west-1.compute.amazonaws.com:1883";
const std::string FEEDER_ID = "321";

//**TODO: set up timer to count down from dispense_time, after timer trips, set check_weight

void checkRfid();
/*
void dispenseFood();
void checkScale();
*/

FeederSettings init_settings;
Servo servo;
Rfid rfid_control;
feeder scale(0);
string tag;			//chipID from server
bool doorIsOpen = true;		//state of door
bool ok_to_feed = false;	//triggers impeller to rotate

int main(void){
    wiringPiSetup();

    double dispense_time;	//interval for feeding schedule
    uint8_t dispense_amount = 0; //number of turns impeller servo makes
    double weightval = 0; //for weight scale usage
    double weightvalAfter = 0; //stores the final weight value
    double weightEaten = 0; //value to send
    double tempval = 0; //for if logic
    bool tempvalmeasure = true;

//Timers
    bool startTimer = false;
    time_t starttime, endtime;
    time_t last_dispense_time = time(NULL); //start timer at this time;
    double elapsedtime;
	
    MqttClient cli(ADDRESS, CLIENT_ID);
//    FeederSettings init_settings;
    Response resp(&init_settings);
	if (!cli.add_function("/petprototype/feeder/push/" + FEEDER_ID + "/", resp)) {
        std::cout << "function add failed" << std::endl;
    } else {
        std::cout << "function add success" << std::endl;
    }
// get settings from the server
    cli.send_sync(FEEDER_ID);

std::cout << "waiting for settings over MQTT" << std::endl;
    while (!init_settings.isValid()) {}    //wait for server to send values

std::cout << "settings retrieved" << std::endl;
    tag = init_settings.getChipId();
    dispense_time = init_settings.getSettingInterval();
    dispense_amount = (init_settings.getSettingCup()) * 8;  //impeller divided in 1/8th cups

//tag = "001662697";
//printf("init servo\n");	//printf("init servo\n");
//**Initialize Servos, default bowl as open - do not dispense
    servo.Init();

//**Initialize Scale, set initial weight
    scale.tare(); //zero out scale
    weightval = scale.measure(); //initialize food current weight
    tempval = scale.measure(); //initialize food current weight

//**Initialize RFID
    rfid_control.Initialize();
    rfid_control.SetTag(tag);
    
    while(true){
	    if(init_settings.didSettingsUpdate()) {
		    // update vars
		    
		    init_settings.setSettingsUpdate(false);
	    }

//**check if RFID tag is available
        checkRfid();
        
//**check scale amount - 3 scenarios
        //1 - when ok_to_feed is true, ensure bowl is open and allow feeder to
            //dispense food, check new weight and send to server, clear ok_to_feed flag
	if (ok_to_feed){	//time to dispense food
	    if (!doorIsOpen){	//door is closed
		servo.OpenDoor();
		doorIsOpen = true;
		sleep(50);	//allow time for door to open before despensing food
	    }
            servo.RotateFeeder(dispense_amount);
//	    checkScale();	//record the new weight
	    printf("dispensing %d cells\n", dispense_amount);
            ok_to_feed = false;
            weightval = scale.measure();
	}

//**TODO: make this section more efficient. There are issues where tempval is always 0
        //2 - MUST - when a large decrease in weight is detected pet is most 
	    //likely eating, set a wait timer and compare before and after  
        if(tempval < weightval-10 || tempval > weightval + 10){
	//if the current weight is at least 10 grams difference(tweak range for sensitivity in scale, fluctuates a lot)
            time(&starttime);
	printf("temp = %d, weight = %d\n", tempval, weightval);
            startTimer = true;
            tempvalmeasure = false;
            tempval = weightval; //prevents this from triggering again till after timeout
        }
        
        if(startTimer) time(&endtime); //only active when feeding
        //elapsedtime = difftime(endtime,starttime); //condition to trigger message sending

        //Section handles the message sending
        if(difftime(endtime, starttime)  > 120){//triggers after 2 minutes
            time(&starttime); //prevents this if loop from doing it continuously
            weightvalAfter = scale.measure();
            weightEaten = weightval - weightvalAfter;
            if(weightEaten < 0) weightEaten = 0; //covers measurement error if nothing is being eaten do not send negative value
            else {
		cli.send_mass(FEEDER_ID, weightEaten); //combine and send to server
                printf("send %d to server\n", weightEaten);
	    }
            tempvalmeasure = true;
            startTimer = false;
        }
//**TODO:*****THIS PART NO LONGER NEEDED
        //3 - periodically check to maintain current weight, should not have significant changes
        if(tempvalmeasure) tempval = feed1.measure(); //measures if it is not in "eating" mode
//********************************

//**check impeller servo logic based on ok_to_feed flag and dispense_amount, clear ok_to_feed
        
        if(ok_to_feed){
            //dispense here
            servo.RotateFeeder();
            ok_to_feed = false;
            weightval = feed1.measure();
        }

//**check to see if it is time to feed
        time_t now = time(NULL);
        if (now - last_dispense_time >
            init_settings.getSettingInterval()) {
            last_dispense_time = now;
            // dispense here TODO: Toan
        }
        
//**check FeederSettings.DispenseNow() to see if user wants to feed off schedule
        //set check_weight flag

    }
}

oid checkRfid()
{
	string compare;		//ID read from RFID module

