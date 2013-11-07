#include <WiFlyHQ.h>

WiFly wifly;

char buf[50];
unsigned long time = 0;
boolean connect_state = false;

void setup()
{
  delay(2000);
  Serial.begin(115200);
  Serial1.begin(9600);
  if(wifly.begin(&Serial1, &Serial)) {
    wifly.setSSID("bananastand");              // WiFi Hotspot Name
    wifly.setPassphrase("knightcop");  // WiFi Hotspot Password
    if(wifly.join()) {
      wifly.getIP(buf, sizeof(buf));
      Serial.print("Connecting");
      //char buf2[sizeof(buf)];
      //sprintf(buf2, "%s:%d", buf, wifly.getPort()); 
    } 
    else {
      Serial.println("Connection Failed");  
    }
  } 
}

void loop()
{
  if(wifly.isConnected()) {
    Serial.println("Connected");
    connect_state = true;
  } 
  else Serial.print("NOT COnnected");

  if(connect_state && millis() - time > 50) {
    time = millis();
    char tmp[20];
    //sprintf(tmp, "L%d,%d:S%d,%d;", analog(0), analog(1), in(22), in(23));
    wifly.println(tmp); 
  }

  if (wifly.available() > 0) {
    delay(10);
    String str = "";
    char c;
    do {
      c = wifly.read();
      if(c != ';') 
        str += c;
    } while(c != ';');
    
    if(str.charAt(0) == 'F') {
      //glcd(7, 0, "Forward    ");
      str = str.substring(1, str.length());
      //fd(stringToInt(str, 5));
    } 
    else if(str.charAt(0) == 'B') {
      //glcd(7, 0, "Backward    ");
      str = str.substring(1, str.length());
      //bk(stringToInt(str, 5));
    } 
    else if(str.charAt(0) == 'L') {
      //glcd(7, 0, "Turn Left ");
      str = str.substring(1, str.length());
      //sl(stringToInt(str, 5));
    } 
    else if(str.charAt(0) == 'R') {
      //glcd(7, 0, "Turn Right");
      str = str.substring(1, str.length());
      //sr(stringToInt(str, 5));
    } 
    else if(str.charAt(0) == 'S') {
      //glcd(7, 0, "Stop      ");
      //ao();
    }
  }
}

int stringToInt(String str, int buffSize) {
  char chBuff[buffSize];
  str.toCharArray(chBuff, sizeof(chBuff));
  char chBuff2[buffSize];
  sprintf(chBuff2, "%s", chBuff); 
  int data = atoi(chBuff2);
  return data;
}
