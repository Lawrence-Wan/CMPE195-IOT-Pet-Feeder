#include <time.h>
#include <unistd.h>
#include <iostream>

using namespace std;

int main(){
    time_t starttime, endtime;
    time(&starttime);
    printf("Start time: %ld\n", starttime);
    while(true){
        time(&endtime);
        printf("End time: %ld\n", endtime);
        printf("Time since start: %f\n", (difftime(endtime,starttime)));
        sleep(2);
    }
    return 0;
}