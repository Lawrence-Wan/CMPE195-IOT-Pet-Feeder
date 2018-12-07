#pragma once

#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>

/*
  Servo value range: 150 --> 250
  Stop/Stall:        210
  Clock-Wise:        210 >
  Counter-Clock-Wise < 210 
*/

class Servo
{
public:

  void Init()
  {
    system("echo 0=210 > /dev/servoblaster"); //Servo for Door
    system("echo 1=210 > /dev/servoblaster"); //Servo for Dispenser
  }
  void StopDoor()
  {
    system("echo 0=210 > /dev/servoblaster");
  }

  void StopRotate()
  {
    system("echo 1=210 > /dev/servoblaster");
  }

  void OpenDoor()
  {
    system("echo 0=250 > /dev/servoblaster");
    sleep(45);
    StopRotate();
  }

  void CloseDoor()
  {
    system("echo 0=150 > /dev/servoblaster");
    sleep(45);
    StopDoor();
  }

  void RotateFeeder()
  {
     system("echo 1=220 > /dev/servoblaster");
     usleep(300000); //in micro-seconds
     StopRotate();
  }
};
