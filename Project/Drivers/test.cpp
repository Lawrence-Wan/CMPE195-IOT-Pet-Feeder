#include <stdlib.h>
#include <iostream>
#include <stdio.h>
#include <unistd.h>

using namespace std;

int main()
{
  system("echo 0=220 > /dev/servoblaster");
  usleep(300000);
  system("echo 0=210 > /dev/servoblaster");
  usleep(300000);
};
