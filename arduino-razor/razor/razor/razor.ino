/**
 * The main Razor Program:
 * Collects sensor data and sends them over bluetooth to the Razor app on Phone.
 */

// So we can communicate to the Bluetooth module.
#include <SoftwareSerial.h>
// Make sure you put a symlink to the IMUProcessor.h in the Arduino libraries for this to work.
#include <IMUProcessor.h>

// BT is connected to serial 10 and 11. 
SoftwareSerial mySerial(10, 11); // RX, TX

IMUProcessor imu_processor;
IMUData imu_data;

void setup() {
  // To write any messages to the serial for debugging etc.
  Serial.begin(38400);

  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

  Serial.println("Welcome to Razor!");

  // Set the data rate for the SoftwareSerial port. Bluetooth module works on 9600, so
  // don't change this.
  mySerial.begin(9600);

  imu_processor.init();  
}

// Sensor data coming from one foot.
struct FootSensorData {
  IMUData imu_data;

  String toString() {
    return String("{") + imu_data.toString() + "}";
  }
};

// A struct to hold all the sensor data.
struct SensorData {
  FootSensorData left;
  FootSensorData right;

  String toString() {
    return String("{") + left.toString() + "," + right.toString() + "}";
  }
};

SensorData sensor_data;

unsigned long last_time = millis();
void loop() {
  // Always read the data, so the data can be calculated correctly.
  imu_processor.readData(&(sensor_data.left.imu_data));
  
  // Print the data only every 2s.
  if (millis() - last_time > 2000) {
//    mySerial.println(sensor.toString());
//    Serial.println(sensor_data.left.imu_data.toString());
    Serial.println(sizeof(IMUData));
    mySerial.println(sensor_data.left.imu_data.toString());
    last_time = millis();
  }
}
