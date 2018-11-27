#include <iostream>
//#include "mqtt/client.h"
#include "json/json.hpp"

#include "FeederSettings.h"
#include "MqttClient.h"
#include "Response.h"

using json = nlohmann::json;

constexpr char TOPIC[] = "hello";
constexpr char PAYLOAD[] = "hello world";

constexpr char CLIENT_ID[] = "generic id";
constexpr char ADDRESS[] = "ec2-13-57-38-126.us-west-1.compute.amazonaws.com:1883";

const std::string FEEDER_ID = "321";

int main() {

    MqttClient cli(ADDRESS, CLIENT_ID);

    FeederSettings s;

    Response resp(&s);
    if (!cli.add_function("/petprototype/feeder/push/" + FEEDER_ID + "/", resp)) {
        std::cout << "failed" << std::endl;
    } else {
        std::cout << "success" << std::endl;
    }
    cli.send_sync(FEEDER_ID);

    cli.send_mass(FEEDER_ID, 555);

    while (std::tolower(std::cin.get()) != 'q')
            ;
    //cli.publish(TOPIC, PAYLOAD);
    /*
    mqtt::client cli(ADDRESS, CLIENT_ID);

    mqtt::connect_options connOpts;
    connOpts.set_keep_alive_interval(20);
    connOpts.set_clean_session(true);

    try {
        cli.connect(connOpts);
        auto msg = mqtt::make_message(TOPIC, PAYLOAD);
        cli.publish(msg);
        cli.disconnect();
    } catch (const mqtt::exception& e) {
        std::cerr << e.what() << std::endl;
    }
    */
    
    return 0;
}
