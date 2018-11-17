
#include "Servo.hpp"

class Servo
{
public:

  void Servo::Init()
  {
    system("echo 0=150 > /dev/servoblaster");
    system("echo 1=150 > /dev/servoblaster");
  }
  void Servo::StopDoor();
  {
    system("echo 0=210 > /dev/servoblaster");
  }

  void Servo::StopRotate();
  {
    system("echo 1=210 > /dev/servoblaster");
  }

  void Servo::OpenDoor()
  {
    system("echo 0=250 > /dev/servoblaster");
    sleep(20);
    StopRotate();
  }

  void Servo::CloseDoor()
  {
    system("echo 0=150 > /dev/servoblaster");
    sleep(20);
    StopDoor();
  }

  void RotateFeeder()
  {
     system("echo 1=220 > /dev/servoblaster");
     usleep(300000); //in micro-seconds
     StopRotate();
  }
};
