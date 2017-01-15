/**
   The main Razor Program:
   Collects sensor data and sends them over bluetooth to the Razor app on Phone.
*/

// So we can communicate to the Bluetooth module.
// Make sure you put a symlink to the IMUProcessor.h in the Arduino libraries for this to work.
#include <SPI.h>

#include "IMUProcessor.h"
#include "RF24.h"
#include "pb_encode.h"
#include "rfio.h"
#include "sensor.pb.h"

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
const int kPressureFrontPin = A2;
// Interrupt pin used for MPU-9250.
const int kInterruptPin = 7;

// CE and CSN ports for the RF24 transmitter.
// Set either one to 0xFF if the radio hardware is not present.
const int kRF24CE = 0;
const int kRF24CSN = 1;

// Connect this pin to ground for the slave one, leave it open for master.
const int kSlaveDetectorPin = 5;
// This is connected to the switch to sense when we press the switch.
// This is an input and used as pull_down, so whenever the button is pressed it goes high.
const int kSwitchSensePin = 4;
// This is an output pin that powers the soft switch, we set it to high programmatically
// as soon as we power up.
const int kAlwaysUpPin = A3;
#endif
#endif

IMUProcessor imu_processor(kInterruptPin, false /* no debug */, true /* get all data */);

// Where we store the sensor data.
SensorData sensor_data = SensorData_init_zero;
FootSensorData* foot_data = 0;  // This will point to the foot we're running on.

RF24 rf_radio(kRF24CE, kRF24CSN);
RFIO rfio(&rf_radio);
byte pipe_address[][6] = {"1Node"};

// How often the two feet communicate with each other.
const int kBetweenFeetDataSyncMsec = 100;
// How often master foot communicates with phone.
const int kPhoneDataSyncMsec = 200;

// Which foot are we running as.
// Left foot is the slave. It only reads sensor data and RF sends it to right foot.
// Right foot is the master. Receives data from left foot, packages it into the
// proto and bluetooth sends it to the cell phone.
//
// We automatically detect this by connecting kSlaveDetectorPin to ground for the
// slave circuit, while leaving it open for the master.
bool is_right_foot;

void setupRF24() {
  rf_radio.begin();
  rf_radio.setChannel(108);  // For our purpose anything works. This is above wifi.
  rf_radio.setPALevel(RF24_PA_MIN);

  if (is_right_foot) {
    rf_radio.openReadingPipe(1, pipe_address[0]);
    rf_radio.startListening();
  } else {
    rf_radio.openWritingPipe(pipe_address[0]);
  }
}

void setup() {
  pinMode(5, INPUT_PULLUP);

  // To write any messages to the serial for debugging etc.
  Serial.begin(38400);

  // This is just to make sur our println statements work in setup function.
  delay(3000);
  Serial.println(F("Welcome to Razor!"));

  is_right_foot = digitalRead(kSlaveDetectorPin) == HIGH;

  // TODO: fix the temp code.
  if (is_right_foot) {
    imu_processor.init();
  }

  setupRF24();

  if (is_right_foot) {
    Serial.println(F("Running as master for right foot"));
    // Set the data rate for the SoftwareSerial port. Bluetooth module works on 9600, so
    // don't change this.
    Serial1.begin(9600);

    foot_data = &(sensor_data.right);
  } else {
    Serial.println(F("Running as slave for left foot"));
    foot_data = &(sensor_data.left);
  }
}

unsigned long last_time = millis();

void loopForMaster() {
  uint8_t buffer[160];
  // Receive data from the other foot if available.
  if (rfio.tryReadFootSensorData(&(sensor_data.left))) {
    Serial.println(F("Received slave foot data"));
  }

  if (millis() - last_time > kPhoneDataSyncMsec) {
    // Things we need to send over bluetooth.
    pb_ostream_t stream = pb_ostream_from_buffer(buffer, sizeof(buffer));
    bool status = pb_encode_delimited(&stream, SensorData_fields, &sensor_data);
    if (!status) {
      Serial.println(F("Error encoding"));
    } else {
      Serial1.write(buffer, stream.bytes_written);
      sensor_data = SensorData_init_zero;
    }
    last_time = millis();
  }
}

void loopForSlave() {
  if (millis() - last_time > kBetweenFeetDataSyncMsec) {
    rfio.writeFootSensorData(*foot_data);
    Serial.println(rf_radio.getPayloadSize());
    last_time = millis();
  }
}

void loop() {
  foot_data->pressure_front = analogRead(kPressureFrontPin);
  // Always read the data, so the data can be calculated correctly.
  // TODO: get rid of the temp code here.
  if (is_right_foot) {
    imu_processor.readData(&(foot_data->imu_data));
  } else {
    foot_data->imu_data.ax = 11;
    foot_data->imu_data.ay = 12;
    foot_data->imu_data.az = 13;
    foot_data->imu_data.gx = 14;
    foot_data->imu_data.gy = 15;
    foot_data->imu_data.gz = 16;
    foot_data->imu_data.yaw = 17;
    foot_data->imu_data.pitch = 18;
    foot_data->imu_data.roll = 19;
    foot_data->imu_data.temperature = 20;
    foot_data->pressure_front = 21; // analogRead(kPressureFrontPin);
  }

  if (is_right_foot) {
    loopForMaster();
  } else {
    loopForSlave();
  }
}

