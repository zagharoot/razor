/**
 * The main Razor Program:
 * Collects sensor data and sends them over bluetooth to the Razor app on Phone.
 */

// So we can communicate to the Bluetooth module.
// Make sure you put a symlink to the IMUProcessor.h in the Arduino libraries for this to work.
#include "IMUProcessor.h"
#include "sensor.pb.h"
#include "pb_encode.h"

// Uncomment one of these lines based on what board you want to use the code with:
//  #define BOARD_UNO
#define BOARD_MKR1000

#ifdef BOARD_UNO

#include <SoftwareSerial.h>
// BT is connected to serial 10 and 11.
SoftwareSerial Serial1(10, 11); // RX, TX
// The pin that pressure sensor is connected to.
const int kPressureFrontPin = A0;
// Interrupt pin used for MPU-9250.
const int kInterruptPin = 12;
#else
#ifdef BOARD_MKR1000
// The pin that pressure sensor is connected to.
const int kPressureFrontPin = A3;
// Interrupt pin used for MPU-9250.
const int kInterruptPin = 7;
#endif
#endif


IMUProcessor imu_processor(kInterruptPin, false /* no debug */, true /* get all data */);
SensorData sensor_data = SensorData_init_zero;

void setup() {
  // To write any messages to the serial for debugging etc.
  Serial.begin(38400);

  Serial.println(F("Welcome to Razor!"));

  // Set the data rate for the SoftwareSerial port. Bluetooth module works on 9600, so
  // don't change this.
  Serial1.begin(9600);

  imu_processor.init();
}

unsigned long last_time = millis();

void loop() {
  uint8_t buffer[160];

  // Always read the data, so the data can be calculated correctly.
  imu_processor.readData(&(sensor_data.left.imu_data));

  // Print the data only every 2s.
  if (millis() - last_time > 500) {
    // Read Analog sensor data:
    sensor_data.left.pressure_front = analogRead(kPressureFrontPin);

    // Things we need to send protos over bluetooth.
    pb_ostream_t stream = pb_ostream_from_buffer(buffer, sizeof(buffer));
    bool status = pb_encode_delimited(&stream, SensorData_fields, &sensor_data);
    if (!status) {
      Serial.println(F("Error encoding"));
    } else {
//      Serial.print(F("Wrote bytes: ")); Serial.println(stream.bytes_written);
      Serial1.write(buffer, stream.bytes_written);
    }
    last_time = millis();
  }
}

