# CMPE195-IOT-Pet-Feeder

To get UART to work on RPi3 or RpiZero, the following steps must be done: 

1- remove the following text in cmdline.txt WITHOUT altering the rest of the line

    - console=serial0,115200

2- add the following two lines in config.txt

    - dtoverlay=pi3-diable-bt
    - enable_uart = 1

3- on the command line, type the following

    - sudo systemctl disable hciuart

4- reboot to effect changes
