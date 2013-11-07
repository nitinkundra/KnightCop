

#include <WiFlyHQ.h>

/* Change these to match your WiFi network */
const char mySSID[] = "bananastand";
const char myPassword[] = "knightcop";

void terminal();

WiFly wifly;
int ledPin = 9;
    

void setup()
{
    char buf[64];

    Serial.begin(115200);
    Serial.println("Starting");


    Serial1.begin(9600);
    if (!wifly.begin(&Serial1, &Serial)) {
        Serial.println("Failed to start wifly");
	terminal();
    }

    /* Join wifi network if not already associated */
    if (!wifly.isAssociated()) {
	/* Setup the WiFly to connect to a wifi network */
	Serial.println("Joining network");
	wifly.setSSID(mySSID);
	wifly.setPassphrase(myPassword);
	wifly.enableDHCP();

	if (wifly.join()) {
	    Serial.println("Joined wifi network");
	} else {
	    Serial.println("Failed to join wifi network");
	    terminal();
	}
    } else {
        Serial.println("Already joined network");
    }

    Serial.println("Set DeviceID");
    wifly.setDeviceID("wifly");
    Serial.println("Port: ");
    Serial.println(wifly.getPort());

    wifly.setIpProtocol(WIFLY_PROTOCOL_TCP);

    /*if (wifly.isConnected()) {
        Serial.println("Old connection active. Closing");
	wifly.close();
    }
    
    if (wifly.open("192.168.1.2", 8000, true)){
      //LED Test Code
      Serial.println("Socket opened");
      digitalWrite(ledPin, HIGH); 
    }
    else Serial.println("Could not open socket");
    */
    
}


void loop()
{
  
    char i;
    if (wifly.available() > 0){
        i = wifly.read();
        Serial.println("Server sent: ");
        Serial.write(i);
        Serial.println();
    }
    else {
      //Serial.println("WIFILY NOT AVAIALABLW");
      //delay(1000);
    }
    if (i == '1'){
      Serial.println("SUCCESS 1");
      digitalWrite(7, HIGH);
      analogWrite(8, 175);
      analogWrite (9, 195);
      delay(250);
      
    }
    else if ( i == '2'){
      Serial.println("SUCCESS 2");
      digitalWrite(7, LOW);
     analogWrite(8, 195);
    analogWrite (9, 175); 
  delay(250);  
  }
    else {
      //Serial.println("STOPPED");
      analogWrite(8, 185);
     analogWrite (9, 185); 
    }
    
    if (Serial.available() > 0){
      wifly.write(Serial.read());
    }
    
    
}

void terminal()
{
    while (1) {
	if (wifly.available() > 0) {
	    Serial.write(wifly.read());
	}


	if (Serial.available()) { // Outgoing data
	    wifly.write(Serial.read());
	}
    }
}
