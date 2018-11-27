# CMPE195-IOT-Pet-Feeder

This project is designed to create an automated pet feeder that can track the amount of food consumed by a specific pet. This information is then relayed to a server that stores the information in a database until it is accessed by the registered user via the mobile app. 

The main components used are:
	-Raspberry Pi 3 (or Pi Zero W) with Rasbian OS; 
	-ID3-LA RFID reader with external antenna; 
	-two continuous rotation servo motors; 
	-Force sensitive resistor; 

First configure the RPi as follows:

If not already on the Rpi, download and install the wiringPi libraries, by entering the following commands:

	git clone git://git.drogon.net/wiringPi
	cd wiringPi
	git pull origin
	cd wiringPi
	./build
Or follow the Plan B instructions found at https://projects.drogon.net/raspberry-pi/wiringpi/download-and-install/

To set up the UART for use with ID3-LA:

	1- remove the following text in cmdline.txt WITHOUT altering the rest of the line
	    - console=serial0,115200
	2- add the following two lines in config.txt
	    - dtoverlay=pi3-diable-bt
    	- enable_uart = 1
	3- on the command line, type the following
	    - sudo systemctl disable hciuart
	4- reboot to effect changes

To set up SPI for use with the force sensitve resistor...



Setting up the servo needs the ServoBlaster library. The ServoBlaster library will be needed to interface the servos with the Raspberry Pi. The Author of the libray is Richard Girst and it can be found at https://github.com/richardghirst/PiBits/tree/master/ServoBlaster. To download ServoBlaster a you will need to download "PiBits" at https://github.com/richardghirst/PiBits which is a compilation of libraries made for the Pi by Richard Girst, however, once it is downloaded, the other labraries can be discarded.

	1- Clone the PiBits github by executing the command on the command line: 
	   <git clone https://github.com/richardghirst/PiBits>
	   
	   We only need the ServoBlaster library so once cloned, execute the command:
	   <mv PiBits/ServoBlaster . && rm -rf PiBits>
	 
	2 - The library can now be used, however, to have it start on booting the Pi,
	    head into the ServoBlaster/user directory in command line by typing
	    <cd ServoBlaster/user> and then type <sudo make install> 
	    Execute the command <./servod> to see the current configuration of the library such as
	    GPIO pins available, pulse width, idle timeout, cycle time, etc... 

	3 - Eight GPIO pins are available for ServoBlaster. Provided Below is a Servo mapping of available GPIO pins.
	    The pins used for this project are 0 and 1 or GPIO pins 4 and 17. Refferring to a Raspberry Pi 3 Pinout diagram,
	    Pins used are 7 and 11.
	
		Servo mapping:
    	 0 on P1-7           GPIO-4
    	 1 on P1-11          GPIO-17
    	 2 on P1-12          GPIO-18
     	3 on P1-13          GPIO-21
     	4 on P1-15          GPIO-22
     	5 on P1-16          GPIO-23
     	6 on P1-18          GPIO-24
     	7 on P1-22          GPIO-25
	   


To set up MQTT and server follow the README.md file found in the clients folder at https://github.com/Lawrence-Wan/CMPE195-IOT-Pet-Feeder


The software will start automatically when loaded to the Pi. Code can be found at https://github.com/Lawrence-Wan/CMPE195-IOT-Pet-Feeder

The User will first need to enter the appropriate RFID tag found with their chip via the mobile app. A feeding schedule, number of hours between feedings, and an amount to be fed, in cups, are required as well. Once these values are saved and sent to the server, the server in turn sends these values to the Pi. Once received, the program will run automatically on the Pi. If a matching tag is received, the servo on the bowl should be opened, if it isn't already. If the tag does not match, the bowl will close off. When the time to feed occurs, the bowl will be opened, if it isn't already, and the impeller servo will rotate the appropriate distance to dispense the specified amount. A scale monitors the current weight of the food in the bowl, sending changes back to the server. The server then stores the value and accumulates a daily value that the user can then view. 
