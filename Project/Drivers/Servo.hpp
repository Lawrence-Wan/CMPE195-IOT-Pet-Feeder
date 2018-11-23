#pragma once

class Servo
{
public:
        Servo();
	void Init();
        void StopDoor();
        void StopRotate();
	void OpenDoor(); //Set as false by default
        void CloseDoor();
	void RotateFeed();
};
