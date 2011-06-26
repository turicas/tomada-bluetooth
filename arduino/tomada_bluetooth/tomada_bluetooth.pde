#include <MeetAndroid.h>
#define RELAY 13


MeetAndroid meetAndroid;

void setup() {
  pinMode(RELAY, OUTPUT);
  digitalWrite(RELAY, LOW);
  Serial.begin(115200);
  meetAndroid.registerFunction(changePower, 'p');
  meetAndroid.registerFunction(getStatus, 's');
}

void loop() {
  meetAndroid.receive();
}

void changePower(byte flag, byte numOfValues) {
  int length = meetAndroid.stringLength();
  char data[length];  
  meetAndroid.getString(data);
  digitalWrite(RELAY, !digitalRead(RELAY));
  if (digitalRead(RELAY) == HIGH) {
    meetAndroid.send("OFF");
  }
  else {
    meetAndroid.send("ON");
  }
}

void getStatus(byte flag, byte numOfValues) {
  if (digitalRead(RELAY) == HIGH) {
    meetAndroid.send("OFF");
  }
  else {
    meetAndroid.send("ON");
  }
}
