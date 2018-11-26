/*
Pet Feeder weight sensor class for instancing several feeders
*/



#ifndef FEEDER_H
#define FEEDER_H

#include "MCP3008SPI.h"

class feeder{

public:
    feeder(int channel = 0); //constructor
    ~feeder(); //destructor
    void tare(); //recalibrate scale
    double measure(); // returns value in grams, defaults to channel 0
    

private:
    double tareValue;
    int ch;
    double convert(int reading); //convert ADC value to grams
    double read(); //read current weight value
};

#endif