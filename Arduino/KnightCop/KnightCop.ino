#include "OneWire.h"
#include "Timer.h"

const int motorL = 200;
const int motorR = 170;
const int motorS = 185;
const int high = 255;
const int low = 0;
int i;
Timer t;  
OneWire  ds(46);
//int tempPin = 46;      //temp sensor pin
int lightPin = A0;   //light sensor pin
  //temp sensor
  byte present = 0;
  byte type_s;
  byte data[12];
  byte addr[8];
  float celsius, fah;
  unsigned long runtime;
  int battery;
  
//u-sonic pins
int anaPin1 = A1;
int anaPin2 = A2;
int anaPin3 = A3;
int anaPin10 = A10;

int anaPin6 = A6;

//int rxPin1 = ;
int arraysize = 5;  //quantity of values to find the median (sample size). Needs to be an odd number
int rangevalue1[] = {0, 0, 0, 0, 0}; 
int rangevalue2[] = {0, 0, 0, 0, 0}; 
int rangevalue3[] = {0, 0, 0, 0, 0}; 
int rangevalue10[] = {0, 0, 0, 0, 0};
int batt[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

void setup()
{ 
  Serial.begin(9600);
  pinMode(53, OUTPUT);    
  digitalWrite(53, HIGH);
  
  pinMode(22, OUTPUT);  //PA0, digital top left corner
  pinMode(23, OUTPUT);  //PA1, across PA0
  
  pinMode(24, OUTPUT);  //PA2
  pinMode(25, OUTPUT);  //PA3
  
  pinMode(26, OUTPUT);  //PA4
  pinMode(19, OUTPUT);  //PA5
 
  pinMode(28, OUTPUT);  //PA6
  pinMode(29, OUTPUT);  //PA7
  
  pinMode(37, OUTPUT);  //PC0
  pinMode(36, OUTPUT);  //PC1
  
  pinMode(A0, INPUT);
  //pinMode(46, INPUT);
  
  //middle controller
  analogWrite(6, 0);
  analogWrite(7, 255);
  analogWrite(8, 0);
  analogWrite(9, 255);
  
  t.every(1000, readSensors, (void*)0);
  
  pinMode(anaPin1, INPUT);
  pinMode(anaPin2, INPUT);
  pinMode(anaPin3, INPUT);
  pinMode(anaPin10, INPUT);
 delay(150);
 
 
 
  
}  //setup ends


void loop()
{
  t.update();
  
   for(int i = 0; i < arraysize; i++)
   {
     rangevalue1[i] = analogRead(anaPin1);
     rangevalue2[i] = analogRead(anaPin2);
     rangevalue3[i] = analogRead(anaPin3);
     rangevalue10[i] = analogRead(anaPin10);
     //delay(5);  //wait between analog samples
    }  
   isort(rangevalue1, arraysize);
   isort(rangevalue2, arraysize);
   isort(rangevalue3, arraysize);
   isort(rangevalue10, arraysize);
 // now show the medaian range   
    int midpoint = arraysize/2;    //midpoint of the array is the medain value in a sorted array
      //note that for an array of 5, the midpoint is element 2, as the first element is element 0
  int clearDistanceR = (rangevalue1[midpoint]);
  int clearDistanceL = (rangevalue2[midpoint]);
  int clearDistanceF = (rangevalue3[midpoint]);
  int clearDistanceB = (rangevalue10[midpoint]);
  
   

  
  
  
  
  
  
  
  
  if (Serial.available() > 0){
    i = Serial.read();
    //Serial.write('#');
    //Serial.write((char)i);
  }
  else{
    i = 'Z';
  }
  
  if (i == 'F'){      //MOVE FORWARD
    if (clearDistanceF < 20){
      Serial.write('I');
     }
     else{  
      analogWrite(44, motorL);
      analogWrite (45, motorR + 4);
      delay(250);
     }
  }else {
    analogWrite(44, 185);
    analogWrite (45, 185);
  }
    
  if (i == 'B'){    //MOVE BACKWARDS
    if (clearDistanceB < 20){
      Serial.write('K');
    }
    else{
      analogWrite(44, motorR + 4);
      analogWrite (45, motorL);
      delay(250);
    }
  }else {
    analogWrite(44, 185);
    analogWrite (45, 185);
  }
  
  if (i == ';'){    //MOVE LEFT
    if (clearDistanceL < 15){
      Serial.write('J');
    }
    else{
      analogWrite(44, motorR - 2);
      analogWrite (45, motorR +10);
      delay(250);
    }
  }else {
    analogWrite(44, 185);
    analogWrite (45, 185);
  }
  
  if (i == 'R'){    //MOVE RIGHT
    if (clearDistanceR < 15){
      Serial.write('L');
    }
    else{
      analogWrite(44, motorL - 2);
      analogWrite (45, motorL + 13);
      delay(250);
    }
  }else {
    analogWrite(44, 185);
    analogWrite (45, 185);
  }
  
  if (i == '1'){    //Joint 1 +
    digitalWrite(22, HIGH);
    digitalWrite(23, LOW);
    delay(250);
  }else {
    digitalWrite(22, LOW);
    digitalWrite(23, LOW);
  }
  
  if (i == '2'){    //Joint 1 -
    digitalWrite(22, LOW);
    digitalWrite(23, HIGH);
    delay(250);
  }else {
    digitalWrite(22, LOW);
    digitalWrite(23, LOW);
  }
  
  if (i == '3'){    //Joint 2 +
    digitalWrite(24, HIGH);
    digitalWrite(25, LOW);
    delay(250);
  }else {
    digitalWrite(24, LOW);
    digitalWrite(25, LOW);
  }
  
  if (i == '4'){    //Joint 2 -
    digitalWrite(24, LOW);
    digitalWrite(25, HIGH);
    delay(250);
  }else {
    digitalWrite(24, LOW);
    digitalWrite(25, LOW);
  }
  
  if (i == '5'){    //Joint 3 +
    digitalWrite(35, HIGH);
    digitalWrite(34, LOW);
    delay(250);
  }else {
    digitalWrite(35, LOW);
    digitalWrite(34, LOW);
  }
  
  if (i == '6'){    //Joint 3 - 
    digitalWrite(35, LOW);
    digitalWrite(34, HIGH);
    delay(250);
  }else {
    digitalWrite(35, LOW);
    digitalWrite(34, LOW);
  }
  
  if (i == '7'){    //Gripper +
    digitalWrite(26, HIGH);
    digitalWrite(19, LOW);
    delay(250);
  }else {
    digitalWrite(26, LOW);
    digitalWrite(19, LOW);
  }
  
  if (i == '8'){    //Gripper -
    digitalWrite(26, LOW);
    digitalWrite(19, HIGH);
    delay(250);
  }else {
    digitalWrite(26, LOW);
    digitalWrite(19, LOW);
  }
  
  if (i == '9'){    //Base +
    digitalWrite(37, HIGH);
    digitalWrite(36, LOW);
    delay(250);
  }else {
    digitalWrite(37, LOW);
    digitalWrite(36, LOW);
  }
  
  if (i == '0'){    //Base -
    digitalWrite(37, LOW);
    digitalWrite(36, HIGH);
    delay(250);
  }else {
    digitalWrite(37, LOW);
    digitalWrite(36 , LOW);
  }
  
 


  i = 'Z';

}  //loop ends

void readSensors(void *context){

  //TEMPERATURE
  if ( !ds.search(addr)) {
    ds.reset_search();
    return;
  }
  type_s = 0;

  ds.reset();
  ds.select(addr);
  ds.write(0x44, 1);        // start conversion, with parasite power on at the end
  
  present = ds.reset();
  ds.select(addr);    
  ds.write(0xBE);         // Read Scratchpad

  for ( i = 0; i < 9; i++) {           // we need 9 bytes
    data[i] = ds.read();
  }
  int16_t raw = (data[1] << 8) | data[0];
  
  celsius = raw / 16.0;
  fah = celsius*9/5 + 32;
  String sfah = String((int)fah);
  Serial.write('$');
  Serial.write(sfah.length());
  for (int i = 0; i < sfah.length(); i++){
    Serial.write(sfah.charAt(i));
  }
  
  //LIGHT
  int lux = analogRead(A0);
  String slux = String(lux);
  Serial.write('#');
  Serial.write(slux.length());
  for (int i = 0; i < slux.length(); i++){
    Serial.write(slux.charAt(i));
  }

  //BATTERY
  battery = analogRead(anaPin6);
   String sbattery = String(battery);
   Serial.write('%');
   Serial.write(sbattery.length());
   for (int i = 0; i < sbattery.length(); i++){
    Serial.write(sbattery.charAt(i));
  }
  
  for(int i = 0; i < 10; i++)
   {
     batt[i] = analogRead(anaPin6);
    }  
   isort(batt, 10);
    int midpoint = 10/2;   
  int level = (rangevalue1[midpoint]);
  Serial.write(level);
  
  
}


// sort function
void isort(int *a, int n)
               //  *a is an array pointer function
{
  for (int i = 1; i < n; ++i)
  {
    int j = a[i];
    int k;
    for (k = i - 1; (k >= 0) && (j < a[k]); k--)
    {
      a[k + 1] = a[k];
    }
    a[k + 1] = j;
  }
}
