#include "mqtt/client.h"
#include "Callback.h"

class MqttClient {

  public:
    MqttClient(const char* address, const char* client_id) 
            : client(address, client_id) {
        mqtt::connect_options connOpts;
        connOpts.set_keep_alive_interval(20);
        connOpts.set_clean_session(true);

        try {
            client.connect(connOpts);
            callback = std::unique_ptr<Callback>(new Callback(client));
            client.set_callback(*callback.get());
        } catch (const mqtt::exception& e) {
            std::cerr << e.what() << std::endl;
            validClient = false;
        }
    }

    ~MqttClient() {
        if (validClient) {
            client.disconnect();
        }
    }

    void publish(const char* topic, const char* payload) {
        client.publish(mqtt::make_message(topic, payload));
    }

    // send mass

    void send_mass(std::string feeder_id, double mass) {
        json message;
        message["operation"] = "consumption";
        message["serial_id"] = feeder_id;
        message["consumption"] = std::to_string(mass);
        std::string payload = message.dump();
        publish(SERVER_TOPIC.c_str(), payload.c_str());
    }

    // send sync request
    void send_sync(std::string feeder_id) {
        json message;
        message["operation"] = "sync";
        message["serial_id"] = feeder_id;
        std::string payload = message.dump();
        publish(SERVER_TOPIC.c_str(), payload.c_str());
    }
    
    /*
    void subscribe(const char* topic) {
       client.subscribe(topic);
    }
    */

    bool add_function(const std::string& topic, const Response& function) {
        if (validClient) {
            std::cout << "Subscribing to topic " << topic << std::endl;
            return callback->add_function(topic, function);
        }
        std::cout << "invalid client" << std::endl;
        return false;
    }

  private:
    mqtt::client client;
	std::unique_ptr<Callback> callback;
    bool validClient = true;

    const std::string SERVER_TOPIC = "/petprototype/feeder/pull/";
};
