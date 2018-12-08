/*
Pet Feeder weight sensor class for instancing several feeders
*/

#include "FEEDER.h"
#include "MCP3008SPI.h"
using namespace std;

feeder::feeder(int channel){ //constructor defaults to channel 0 if not specified
    this->tareValue = 0;
    this->ch = 0;
}

feeder::~feeder(){ //destructor needs nothing, int will be released normally
}

double feeder::convert(int reading){ //convert ADC value to grams, has math equations
    double val;
    if(reading < 25){
        val = (0.028 * reading * reading * reading) - (0.3229 * reading * reading) + (1.6571 * reading) - 0.5429;
    }
    else{
        val = (0.0008 * reading * reading * reading) - (0.2066 * reading * reading) + (20.893 * reading) - 152.53;
    }
    return val;
}

double feeder::read(){ //read current weight value

    int SAMPLE_COUNT = 10;
    double avg = 0;

    for(int i = 0; i < SAMPLE_COUNT; i++){
        mcp3008 a2d("/dev/spidev0.0", SPI_MODE_0, 1000000, 8);
        int val = 0;
        unsigned char data[3];

        //data to send
        data[0] = 1;
        data[1] = 0b10000000 |( ((this->ch & 7) << 4));
        data[2] = 0;

        //send data
        a2d.spiWriteRead(data, sizeof(data) );

        //read result
        val = 0;
        val = (data[1]<< 8) & 0b1100000000; //2^10 so 10 bits, message is in two parts
        val |=  (data[2] & 0xff);

        //convert result to grams, subtract tare(pre coverted to grams)
        avg += convert(val);
    }
    avg /= SAMPLE_COUNT;
    return avg;
} 

double feeder::measure(){
    int SAMPLE_COUNT = 10;
    double value = 0;
    for(int i = 0; i < SAMPLE_COUNT; i++){
        value += read();
    }
    value /= SAMPLE_COUNT;
    value -= this->tareValue;
    if(value <= 0) value = 0; //in the case that its 0 or negative, slight misreading of weight
    return value;
}

void feeder::tare(){ //recalibrate scale
    this->tareValue = read();
}
