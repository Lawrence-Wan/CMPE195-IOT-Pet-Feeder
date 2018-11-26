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
    std::mutex state_mutex;
};
