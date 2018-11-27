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


The software will start automatically when loaded to the Pi. Code can be found at https://github.com/Lawrence-Wan/CMPE195-IOT-Pet-Feeder

The User will first need to enter the appropriate RFID tag found with their chip via the mobile app. A feeding schedule, number of hours between feedings, and an amount to be fed, in cups, are required as well. Once these values are saved and sent to the server, the server in turn sends these values to the Pi. Once received, the program will run automatically on the Pi. If a matching tag is received, the servo on the bowl should be opened, if it isn't already. If the tag does not match, the bowl will close off. When the time to feed occurs, the bowl will be opened, if it isn't already, and the impeller servo will rotate the appropriate distance to dispense the specified amount. A scale monitors the current weight of the food in the bowl, sending changes back to the server. The server then stores the value and accumulates a daily value that the user can then view. 
