# CMPE195-IOT-Pet-Feeder

This project is designed to create an automated pet feeder that can track the amount of food consumed by 
a specific pet. This information is then relayed to a server that stores the information in a database 
until it is accessed by the registered user via the mobile app. 

The main components used are:
	-Raspberry Pi 3 (or Pi Zero W) with Rasbian OS; 
	-ID3-LA RFID reader with external antenna; 
	-two continuous rotation servo motors; 
	-Force sensitive resistor; 

First configure the RPi as follows:

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



To set up stuff for servo....



To set up MQTT and server...


The software will start automatically when loaded to the Pi.
