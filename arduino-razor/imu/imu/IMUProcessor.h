
 /*
  * Provides functionality to read data from the MPU-9250 sensor.
  * 
  * You need to have Wire.h and MPU9250.h already in your library.
  * Note: You need to call readData() frequently so the acceleration
  *       data can be calculated correctly.
*/

 /*
 Hardware setup:
 MPU9250 Breakout --------- Arduino
 VDD ---------------------- 3.3V
 VDDI --------------------- 3.3V
 SDA ----------------------- A4
 SCL ----------------------- A5
 GND ---------------------- GND
 */
#ifndef IMU_PROCESSOR_H
#define IMU_PROCESSOR_H

#include "quaternionFilters.h"
#include "MPU9250.h"

// Declination of Los Angeles (33.990475, -118.440970) is
//    12.13° E  ± 0.33°  (or 12.3°) on 2016-10-05
// - http://www.ngdc.noaa.gov/geomag-web/#declination
#define LOS_ANGELES_DECLINATION 12.13

/**
 * The main wrapper for reading data.
 * Before using, you need to call init();
 */ 
typedef struct IMUProcessor {
  // The interrupt pin.
  int int_pin;
  // Whether to print out debug statements.
  bool debug;
  // Whether to calculate yaw/pitch/roll or just raw data.
  // There is overhead to computing the data.
  bool calculate_full_location;
  MPU9250 my_imu;
  double declination;

  // Default constructor.
  IMUProcessor() {
    int_pin = 12;
    debug = true;
    calculate_full_location = true;

    declination = LOS_ANGELES_DECLINATION;
  }

  // Constructor to customize variables.
  IMUProcessor(int int_pin_, bool debug_, bool calculate_full_location_) {
    int_pin = int_pin_;
    debug = debug_;
    calculate_full_location = calculate_full_location_;
  }

  // Initializes the sensor. Must be called before any readData() statements.
  void init() {
    Wire.begin();

    // Set up the interrupt pin, it's set as active high, push-pull
    pinMode(int_pin, INPUT);
    digitalWrite(int_pin, LOW);

    // Read the WHO_AM_I register, this is a good test of communication
    byte c =  my_imu.readByte(MPU9250_ADDRESS, WHO_AM_I_MPU9250);
    if (debug) {
      Serial.print("MPU9250 "); Serial.print("I AM "); Serial.print(c, HEX);
      Serial.print(" I should be "); Serial.println(0x71, HEX);
    }

    if (c == 0x71) // WHO_AM_I should always be 0x68
    {
      if (debug) { Serial.println("MPU9250 is online..."); }

      // Start by performing self test and reporting values
      my_imu.MPU9250SelfTest(my_imu.SelfTest);
      if (debug) {
        Serial.print("x-axis self test: acceleration trim within : ");
        Serial.print(my_imu.SelfTest[0],1); Serial.println("% of factory value");
        Serial.print("y-axis self test: acceleration trim within : ");
        Serial.print(my_imu.SelfTest[1],1); Serial.println("% of factory value");
        Serial.print("z-axis self test: acceleration trim within : ");
        Serial.print(my_imu.SelfTest[2],1); Serial.println("% of factory value");
        Serial.print("x-axis self test: gyration trim within : ");
        Serial.print(my_imu.SelfTest[3],1); Serial.println("% of factory value");
        Serial.print("y-axis self test: gyration trim within : ");
        Serial.print(my_imu.SelfTest[4],1); Serial.println("% of factory value");
        Serial.print("z-axis self test: gyration trim within : ");
        Serial.print(my_imu.SelfTest[5],1); Serial.println("% of factory value");
      }
      
      // Calibrate gyro and accelerometers, load biases in bias registers
      my_imu.calibrateMPU9250(my_imu.gyroBias, my_imu.accelBias);

      my_imu.initMPU9250();

      // Initialize device for active mode read of acclerometer, gyroscope, and
      // temperature
      if (debug) { Serial.println("MPU9250 initialized for active data mode...."); }

      // Read the WHO_AM_I register of the magnetometer, this is a good test of
      // communication
      byte d = my_imu.readByte(AK8963_ADDRESS, WHO_AM_I_AK8963);
      if (debug) {
        Serial.print("AK8963 "); Serial.print("I AM "); Serial.print(d, HEX);
        Serial.print(" I should be "); Serial.println(0x48, HEX);
      }
      
      // Get magnetometer calibration from AK8963 ROM
      my_imu.initAK8963(my_imu.magCalibration);
      // Initialize device for active mode read of magnetometer
      if (debug) { Serial.println("AK8963 initialized for active data mode...."); }
      
      if (debug) {
        Serial.println("Calibration values: ");
        Serial.print("X-Axis sensitivity adjustment value ");
        Serial.println(my_imu.magCalibration[0], 2);
        Serial.print("Y-Axis sensitivity adjustment value ");
        Serial.println(my_imu.magCalibration[1], 2);
        Serial.print("Z-Axis sensitivity adjustment value ");
        Serial.println(my_imu.magCalibration[2], 2);
      }
    } // if (c == 0x71)
    else {
      Serial.print("Could not connect to MPU9250: 0x");
      Serial.println(c, HEX);
      while(1) ; // Loop forever if communication doesn't happen
    }
  }  // init

  // Reads and processes sensor data.
  void readData() {
    // If int_pin is not high, data is not available to read.
    if (my_imu.readByte(MPU9250_ADDRESS, INT_STATUS) & 0x01) {
      my_imu.readAccelData(my_imu.accelCount);  // Read the x/y/z adc values
      my_imu.getAres();
  
      // Now we'll calculate the accleration value into actual g's
      // This depends on scale being set
      my_imu.ax = (float)my_imu.accelCount[0]*my_imu.aRes; // - accelBias[0];
      my_imu.ay = (float)my_imu.accelCount[1]*my_imu.aRes; // - accelBias[1];
      my_imu.az = (float)my_imu.accelCount[2]*my_imu.aRes; // - accelBias[2];
  
      my_imu.readGyroData(my_imu.gyroCount);  // Read the x/y/z adc values
      my_imu.getGres();
  
      // Calculate the gyro value into actual degrees per second.
      // This depends on scale being set.
      my_imu.gx = (float)my_imu.gyroCount[0]*my_imu.gRes;
      my_imu.gy = (float)my_imu.gyroCount[1]*my_imu.gRes;
      my_imu.gz = (float)my_imu.gyroCount[2]*my_imu.gRes;
  
      my_imu.readMagData(my_imu.magCount);  // Read the x/y/z adc values
      my_imu.getMres();
      // User environmental x-axis correction in milliGauss, should be
      // automatically calculated
      my_imu.magbias[0] = +470.;
      // User environmental x-axis correction in milliGauss TODO axis??
      my_imu.magbias[1] = +120.;
      // User environmental x-axis correction in milliGauss
      my_imu.magbias[2] = +125.;
  
      // Calculate the magnetometer values in milliGauss
      // Include factory calibration per data sheet and user environmental
      // corrections
      // Get actual magnetometer value, this depends on scale being set
      my_imu.mx = (float)my_imu.magCount[0]*my_imu.mRes*my_imu.magCalibration[0] -
                 my_imu.magbias[0];
      my_imu.my = (float)my_imu.magCount[1]*my_imu.mRes*my_imu.magCalibration[1] -
                 my_imu.magbias[1];
      my_imu.mz = (float)my_imu.magCount[2]*my_imu.mRes*my_imu.magCalibration[2] -
                 my_imu.magbias[2];
    }  // Data was available to read.
    
    // Must be called before updating quaternions!
    my_imu.updateTime();
  
    // Sensors x (y)-axis of the accelerometer is aligned with the y (x)-axis of
    // the magnetometer; the magnetometer z-axis (+ down) is opposite to z-axis
    // (+ up) of accelerometer and gyro! We have to make some allowance for this
    // orientationmismatch in feeding the output to the quaternion filter. For the
    // MPU-9250, we have chosen a magnetic rotation that keeps the sensor forward
    // along the x-axis just like in the LSM9DS0 sensor. This rotation can be
    // modified to allow any convenient orientation convention. This is ok by
    // aircraft orientation standards! Pass gyro rate as rad/s
    //  MadgwickQuaternionUpdate(ax, ay, az, gx*PI/180.0f, gy*PI/180.0f, gz*PI/180.0f,  my,  mx, mz);
    MahonyQuaternionUpdate(my_imu.ax, my_imu.ay, my_imu.az, my_imu.gx*DEG_TO_RAD,
                           my_imu.gy*DEG_TO_RAD, my_imu.gz*DEG_TO_RAD, my_imu.my,
                           my_imu.mx, my_imu.mz, my_imu.deltat);
    my_imu.delt_t = millis() - my_imu.count;
 
    if (calculate_full_location) {
      // Define output variables from updated quaternion---these are Tait-Bryan
      // angles, commonly used in aircraft orientation. In this coordinate system,
      // the positive z-axis is down toward Earth. Yaw is the angle between Sensor
      // x-axis and Earth magnetic North (or true North if corrected for local
      // declination, looking down on the sensor positive yaw is counterclockwise.
      // Pitch is angle between sensor x-axis and Earth ground plane, toward the
      // Earth is positive, up toward the sky is negative. Roll is angle between
      // sensor y-axis and Earth ground plane, y-axis up is positive roll. These
      // arise from the definition of the homogeneous rotation matrix constructed
      // from quaternions. Tait-Bryan angles as well as Euler angles are
      // non-commutative; that is, the get the correct orientation the rotations
      // must be applied in the correct order which for this configuration is yaw,
      // pitch, and then roll.
      // For more see
      // http://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
      // which has additional links.
      my_imu.yaw   = atan2(2.0f * (*(getQ()+1) * *(getQ()+2) + *getQ() *
                    *(getQ()+3)), *getQ() * *getQ() + *(getQ()+1) * *(getQ()+1)
                    - *(getQ()+2) * *(getQ()+2) - *(getQ()+3) * *(getQ()+3));
      my_imu.pitch = -asin(2.0f * (*(getQ()+1) * *(getQ()+3) - *getQ() *
                    *(getQ()+2)));
      my_imu.roll  = atan2(2.0f * (*getQ() * *(getQ()+1) + *(getQ()+2) *
                    *(getQ()+3)), *getQ() * *getQ() - *(getQ()+1) * *(getQ()+1)
                    - *(getQ()+2) * *(getQ()+2) + *(getQ()+3) * *(getQ()+3));
      my_imu.pitch *= RAD_TO_DEG;
      my_imu.yaw   *= RAD_TO_DEG;
      my_imu.yaw   -= declination;
      my_imu.roll  *= RAD_TO_DEG;

      my_imu.tempCount = my_imu.readTempData();  // Read the adc values
      my_imu.temperature = ((float) my_imu.tempCount) / 333.87 + 21.0;
    } // if (calculate_full_location)

    my_imu.count = millis();
    my_imu.sumCount = 0;
    my_imu.sum = 0;
    return true;
  }

    // x acceleration in milligs.
  double ax() {
    return my_imu.ax * 1000.;
  }

  // y acceleration in milligs.
  double ay() {
    return my_imu.ay * 1000.;
  }

  // z acceleration in milligs.
  double az() {
    return my_imu.az * 1000.;
  }

  // gyro x in degree/sec.
  double gyrox() {
    return my_imu.gx;
  }

  // gyro y in degree/sec.
  double gyroy() {
    return my_imu.gy;
  }

  // gyro z in degree/sec.
  double gyroz() {
    return my_imu.gz;
  }

  // Magnet x in degree/sec.
  double magx() {
    return my_imu.mx;
  }

  // Magnet y in degree/sec.
  double magy() {
    return my_imu.my;
  }

  // Magnet z in degree/sec.
  double magz() {
    return my_imu.mz;
  }

  // Temperature in celsius.
  double temperature() {
    return my_imu.temperature;
  }

  double yaw() {
    return my_imu.yaw;
  }

  double pitch() {
    return my_imu.pitch;
  }

  double roll() {
    return my_imu.roll;
  }

  void print() {
    Serial.println("..........");
    Serial.print("ax: "); Serial.print(ax(), 1);
    Serial.print("\tay: "); Serial.print(ay(), 1);
    Serial.print("\taz: "); Serial.println(az(), 1);

    Serial.print("gx: "); Serial.print(magx(), 1);
    Serial.print("\tgy: "); Serial.print(magy(), 1);
    Serial.print("\tgz: "); Serial.println(magz(), 1);

    Serial.print("yaw: "); Serial.print(yaw(), 1);
    Serial.print("\tpitch: "); Serial.print(pitch(), 1);
    Serial.print("\troll: "); Serial.println(roll(), 1);

    Serial.print("temperature: "); Serial.println(temperature());
    Serial.println("");
  }
}IMUProcessor;

#endif  // IMU_PROCESSOR_H
