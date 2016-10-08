/**
 * A sample program that reads IMU sensor data and prints them.
 */
#include "IMUProcessor.h"

IMUProcessor processor;

void setup()
{
  Serial.begin(38400);
  processor.init();
}

unsigned long last_time = millis();

IMUData imu_data;

void loop() {
  // Always read the data, so the data can be calculated correctly.
  processor.readData(&imu_data);
  
  // Print the data only every 2s.
  if (millis() - last_time > 2000) {
    processor.print();
    last_time = millis();
  }
}

