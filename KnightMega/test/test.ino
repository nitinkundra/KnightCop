

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
    Serial.print("Port: ");
    Serial.println(wifly.getPort());

    wifly.setIpProtocol(WIFLY_PROTOCOL_TCP);

    if (wifly.isConnected()) {
        Serial.println("Old connection active. Closing");
	wifly.close();
    }
    
    /*if (wifly.open("192.168.1.2", 8000, true)){
      //LED Test Code
      Serial.println("Socket opened");
      digitalWrite(ledPin, HIGH); 
    }
    else Serial.println("Could not open socket");
    */
    
}


void loop()
{
  
    char ch;
    if (wifly.available() > 0){
        ch = wifly.read();
        Serial.print("Server sent: ");
        Serial.write(ch);
    }
    
    if (ch == 1){
      digitalWrite(ledPin, HIGH);
      delay(2000);
    }
    else if (ch == 0){
      digitalWrite(ledPin, LOW);
      delay(2000);
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
