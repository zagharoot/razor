#ifndef RF_IO_H
#define RF_IO_H

#define PACKET_SIZE 32

// A class that makes it easier to send struct data over RF24.
// If the struct is less than PACKET_SIZE, which is 32 bytes you don't need to use
// this because you can send everything in one call. If it is over that, we need
// to break the struct into smaller pieces.
class RFIO {
  public:
    RFIO(RF24* rf_radio)
        : rf_radio_(rf_radio), reading_index_(0) {}

    // Tries to make a reading of one packet from RF24 if data is available.
    // If it is available and the current received packet completes the
    // struct, updates foot_data and returns true.
    // Otherwise, this will be a partial reading of the struct and will return false.
    bool tryReadFootSensorData(FootSensorData* foot_data) {
      if (!rf_radio_->isValid() || !rf_radio_->available()) {
        return false;
      }

      // How many bytes are remaining from the struct.
      int remaining = sizeof(FootSensorData) - reading_index_;
      // How many bytes should we read this time?
      int this_read = remaining > PACKET_SIZE ? PACKET_SIZE : remaining;

      rf_radio_->read(&(buffer_[reading_index_]), this_read);

      // This read finished our struct?
      if (this_read == remaining) {
        reading_index_ = 0;
        memcpy(foot_data, buffer_, sizeof(FootSensorData));
        return true;
      } else {
        reading_index_ += this_read;
        return false;
      }
    }

    // Writes foot_data over RF24, possibly using multiple packets.
    void writeFootSensorData(const FootSensorData& foot_data) {
      byte* buffer = (byte*)(&foot_data);
      for (int i = 0; i < sizeof(FootSensorData); i+= PACKET_SIZE) {
        int this_write = sizeof(FootSensorData) - i > PACKET_SIZE ? PACKET_SIZE : sizeof(FootSensorData) - i;
        rf_radio_->write(&(buffer[i]), this_write);
      }
    }

  private:
    RF24* rf_radio_;
    byte buffer_[sizeof(FootSensorData)];
    int reading_index_;
};

#endif  // RF_IO_H

