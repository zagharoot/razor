

#include <SoftwareSerial.h>

// BT is connected to serial 10 and 11. 
SoftwareSerial mySerial(10, 11); // RX, TX

int sensorPin = A0;    // select the input pin for the potentiometer
int ledPin = 13;      // select the pin for the LED
int sensorValue = 500;  // variable to store the value coming from the sensor


void setup() {
  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  // declare the ledPin as an OUTPUT:
  pinMode(ledPin, OUTPUT);

  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

  Serial.println("Goodnight moon!");

  // set the data rate for the SoftwareSerial port
  mySerial.begin(9600);
}

struct Sensors {
  int first;
  int second;
  int third;  

  String toString() {
    String firstStr(first);
    String secondStr(second);
    String thirdStr(third);
    return String("{{" + firstStr + "," + secondStr + "," + thirdStr + "}, {" + firstStr + "," + secondStr + "," + thirdStr + "}}");
  }
};

Sensors sensor = { 5, 4, 3};

void loop() { // run over and over
/*  sensorValue = analogRead(sensorPin);
  Serial.println(sensorValue);
  mySerial.write(sensorValue);
*/

  mySerial.println(sensor.toString());
//  mySerial.println("abcdf");
  
  delay(5000);  
  /*
  if (mySerial.available()) {
    char a = mySerial.read();
    char b = a + 1;
    mySerial.write(b);
    Serial.write(b);
  }
  if (Serial.available()) {
    mySerial.write(Serial.read());
  }
  */
}
