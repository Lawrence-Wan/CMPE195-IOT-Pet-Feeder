#pragma once
#include <iostream>

#include "FeederSettings.h"
#include "json/json.hpp"

using json = nlohmann::json;

class Response {
  public:
    Response(FeederSettings* s) : settings(s) {
    }

    void response(std::string& payload) {
        response(json::parse(payload));
    }

    void response(const json& payload) {
        std::cout << payload << std::endl;

        try {
            std::string operation = payload.at("operation");

            if (operation == "sync") {
                sync_feeder(payload);
            } else if (operation == "dispense") {
                dispense();
            }
        } catch (json::out_of_range& e) {
            std::cout << "message: " << e.what() << std::endl;
        }
        
    }

    void sync_feeder(const json& payload) {
        std::cout << "Updating settings . . ." << std::endl;
        try {
            double setting_cup = payload.at("setting_cup");
            double setting_interval = payload.at("setting_interval");
            std::string chip_id = payload.at("chip_id");

            settings->setSettingCup(setting_cup);
            settings->setSettingInterval(setting_interval);
            settings->setChipId(chip_id);
        } catch (json::out_of_range& e) {
            std::cout << "message: " << e.what() << std::endl;
        }

        std::cout <<
            "    Cup setting: " << settings->getSettingCup() << std:: endl <<
            "    Interval setting: " << settings->getSettingInterval()  << std:: endl <<
            "    Chip id: " << settings->getChipId() << std:: endl;
    }

    void dispense() {
        settings->setFeedRequest(true);
        std::cout << "Request to feed set" << std::endl;
    }

  private:
    FeederSettings* settings;
};
