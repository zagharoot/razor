/**
 A simple program to send a dummy SensorData proto over bluetooth.
*/

// So we can communicate to the Bluetooth module.
#include <SoftwareSerial.h>
// Support for our proto.
#include "sensor.pb.h"
#include "pb_encode.h"
#include "pb_decode.h"

// BT is connected to serial 10 and 11. 
SoftwareSerial mySerial(10, 11); // RX, TX

SensorData sd = SensorData_init_zero;

void setup() {
  Serial.begin(38400);
  mySerial.begin(9600);
}

void loop() {
  sd.left.imu_data.ax = 1.0;
  sd.left.imu_data.ay = 1.1;
  sd.left.imu_data.az = 1.2;
  sd.left.imu_data.gx = 1.3;
  sd.left.imu_data.gy = 1.4;
  sd.left.imu_data.gz = 1.5;
  sd.left.imu_data.yaw = 1.6;
  sd.left.imu_data.pitch = 1.7;
  sd.left.imu_data.roll = 1.8;
  sd.left.imu_data.temperature = 1.9;
  
  sd.right.imu_data.ax = 2.0;
  sd.right.imu_data.ay = 2.1;
  sd.right.imu_data.az = 2.2;
  sd.right.imu_data.gx = 2.3;
  sd.right.imu_data.gy = 2.4;
  sd.right.imu_data.gz = 2.5;
  sd.right.imu_data.yaw = 2.6;
  sd.right.imu_data.pitch = 2.7;
  sd.right.imu_data.roll = 2.8;
  sd.right.imu_data.temperature = 2.9;
  
  uint8_t buffer[256];
  pb_ostream_t stream = pb_ostream_from_buffer(buffer, sizeof(buffer));
  bool status = pb_encode_delimited(&stream, SensorData_fields, &sd);
  if (!status) {
    Serial.println("Error encoding");
  } else {
    Serial.print("Wrote bytes: "); Serial.println(stream.bytes_written);
    mySerial.write(buffer, stream.bytes_written);
  }

  delay(1000);
}
