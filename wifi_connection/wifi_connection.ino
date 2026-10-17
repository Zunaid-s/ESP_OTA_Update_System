#include <WiFi.h>

// WiFi credentials
const char* ssid = "Airtel_arpa_4942";
const char* password = "air33411";

void setup() {
  // Initialize serial communication
  Serial.begin(115200);
  delay(1000);

  Serial.println();
  Serial.println("Starting WiFi connection...");

  // Connect to WiFi
  WiFi.begin(ssid, password);

  // Wait for connection
  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.println("WiFi connected successfully!");
  Serial.println("---------------------------");
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
  Serial.print("MAC Address: ");
  Serial.println(WiFi.macAddress());
  Serial.print("Signal Strength (RSSI): ");
  Serial.print(WiFi.RSSI());
  Serial.println(" dBm");
  Serial.println("---------------------------");
}

void loop() {
  // Check if still connected
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("WiFi connection lost! Reconnecting...");
    WiFi.disconnect();
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print(".");
    }

    Serial.println();
    Serial.println("Reconnected!");
    Serial.print("IP Address: ");
    Serial.println(WiFi.localIP());
  }

  delay(10000); // Check connection every 10 seconds
}
