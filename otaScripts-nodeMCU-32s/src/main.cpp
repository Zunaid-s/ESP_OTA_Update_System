#include <Arduino.h>

// #define led 36
void setup() {
  pinMode(32, OUTPUT);
}

void loop() {

  if(touchRead(15) < 50){
    digitalWrite(32, HIGH);
  } 
  else {
    digitalWrite(32, LOW);
  }
}
