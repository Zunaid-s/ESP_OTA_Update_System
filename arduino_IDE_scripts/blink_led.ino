// Simple LED Blinking Sketch for ESP32
// Connects an LED to GPIO pin 5 and blinks it

const int LED_PIN = 5;  // GPIO pin connected to LED

void setup() {
  // Initialize serial for debugging
  Serial.begin(115200);
  delay(1000);

  // Set LED pin as output
  pinMode(LED_PIN, OUTPUT);

  Serial.println("LED Blink Started!");
  Serial.print("LED Pin: ");
  Serial.println(LED_PIN);
}

void loop() {
  // Turn LED on
  digitalWrite(LED_PIN, HIGH);
  Serial.println("LED ON");
  delay(1000);  // Wait 1 second

  // Turn LED off
  digitalWrite(LED_PIN, LOW);
  Serial.println("LED OFF");
  delay(1000);  // Wait 1 second
}
