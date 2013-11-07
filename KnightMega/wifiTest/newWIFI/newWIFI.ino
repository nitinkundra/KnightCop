// (Based on Ethernet's WebClient Example)
#include "WiFly.h"
#include "Credentials.h"
#include <SoftwareSerial.h>
SoftwareSerial SerialRNXV(2, 3);

byte server[] = { 74, 125, 224, 50}; // Google

//Client client(server, 80);

WiFlyClient client("google.com", 80);

void setup() {
  Serial.begin(9600);
  SerialRNXV.begin(9600);
 
  WiFly.setUart(&SerialRNXV);
  Serial.println("begin");
  WiFly.begin();
  Serial.println("began");
  
   //if (!WiFly.join(ssid, passphrase, WEP_MODE)) {
   if (!WiFly.join(ssid
   )) {

     Serial.println("Association failed.");
    while (1) {
      // Hang on failure.
    }
  }  

  Serial.println("connecting...");

  if (client.connect()) {
    Serial.println("connected");
    client.println("GET /search?q=arduino HTTP/1.0");
    client.println();
  } else {
    Serial.println("connection failed");
  }
  
}

void loop() {
  if (client.available()) {
    char c = client.read();
    Serial.print(c);
  }
  
  if (!client.connected()) {
    Serial.println();
    Serial.println("disconnecting.");
    client.stop();
    for(;;)
      ;
  }
}
