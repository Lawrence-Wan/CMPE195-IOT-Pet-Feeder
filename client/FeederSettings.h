#pragma once
#include <mutex>

class FeederSettings {

public:
    FeederSettings() { }

    void setSettingCup(double setting) {
        state_mutex.lock();
        setting_cup = setting;
        state_mutex.unlock();
    }

    void setSettingInterval(double setting) {
        state_mutex.lock();
        setting_interval = setting;
        state_mutex.unlock();
    }

    void setChipId(std::string id) {
        state_mutex.lock();
        chip_id = id;
        state_mutex.unlock();
    }

    void setFeedRequest(bool request) {
        state_mutex.lock();
        feed_requested = request;
        state_mutex.unlock();
    }

    void setValid() {
        state_mutex.lock();
        is_valid = true;
        state_mutex.unlock();
    }

    bool isValid() {
        return is_valid;
    }
    
    void setSettingsUpdate(bool state) {
        settings_updated = state;
    }
    
    bool didSettingsUpdate() {
        return settings_updated;
    }

    bool isFeedingRequested() {
        return feed_requested;
    }

    double getSettingCup() {
        return setting_cup;    
    }

    double getSettingInterval() {
        return setting_interval;
    }

    std::string getChipId() {
        return chip_id;
    }

    const double INVALID = -1;

private:
    // number of cups to dispense for every interval
    double setting_cup = INVALID;

    // length of interval between feedings
    double setting_interval = INVALID;
    std::string chip_id = "";

    bool is_valid = false;
    bool settings_updated = false;
    bool feed_requested = false;

    std::mutex state_mutex;
};
