/**
 * A sample program that reads IMU sensor data and prints them.
 */
#include <SoftwareSerial.h>
 
#include "IMUProcessor.h"
#include "pb_encode.h"

IMUProcessor processor;
// BT is connected to serial 10 and 11. 
SoftwareSerial mySerial(10, 11); // RX, TX
SensorData sensor_data = SensorData_init_zero;


void setup()
{
  Serial.begin(38400);
  mySerial.begin(9600);
  processor.init();
}

unsigned long last_time = millis();

IMUData imu_data;

void loop() {
  uint8_t buffer[80];
  // Always read the data, so the data can be calculated correctly.
  processor.readData(&(sensor_data.left.imu_data));
  
  // Print the data only every 2s.
  if (millis() - last_time > 2000) {
    pb_ostream_t stream = pb_ostream_from_buffer(buffer, sizeof(buffer));
    bool status = pb_encode_delimited(&stream, SensorData_fields, &sensor_data);
    processor.print();
    last_time = millis();
  }
}

