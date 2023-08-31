
#include "Firebase_Arduino_WiFiNINA.h"
#include <Wire.h>
#include "MAX30105.h"

#include "heartRate.h"

#define DATABASE_URL "android2-a0742-default-rtdb.firebaseio.com" //<databaseName>.firebaseio.com or <databaseName>.<region>.firebasedatabase.app
#define DATABASE_SECRET "bSN98IBs6oHyKzTSgNMpvSaKjNt3CyzeauDMrUWO"
#define WIFI_SSID "KUCDT_2G"
#define WIFI_PASSWORD "kusw_0413"

MAX30105 particleSensor;
FirebaseData fbdo;

// 서미스터 변수
int analPin = 0;
int val = 0;       
long x=0, vcc=4840;
float th=0,ce=0;
int tt = 0;


const byte RATE_SIZE = 4; //Increase this for more averaging. 4 is good.
byte rates[RATE_SIZE]; //Array of heart rates
byte rateSpot = 0;
long lastBeat = 0; //Time at which the last beat occurred
unsigned long timeVal=0;
unsigned long previousVal=0; //이전시간값 저장변수
int sec = 0;
int t = 0;
int time[10] = {0, 0, 0, 0, 0,0,0,0,0,0};
int zero = 0;

float beatsPerMinute;
int beatAvg;
int lastAvg;
void setup() {
  Serial.begin(115200);
  delay(100);
  Serial.println();

  Serial.print("Connecting to Wi-Fi");
  int status = WL_IDLE_STATUS;
  while (status != WL_CONNECTED)
  {
    status = WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print(".");
    delay(100);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  //Provide the autntication data
  Firebase.begin(DATABASE_URL, DATABASE_SECRET, WIFI_SSID, WIFI_PASSWORD);
  Firebase.reconnectWiFi(true);
   // Initialize sensor
  if (!particleSensor.begin(Wire, I2C_SPEED_FAST)) //Use default I2C port, 400kHz speed
  {
    Serial.println("MAX30105 was not found. Please check wiring/power. ");
    while (1);
  }
  Serial.println("Place your index finger on the sensor with steady pressure.");

  particleSensor.setup(); //Configure sensor with default settings
  particleSensor.setPulseAmplitudeRed(0x0A); //Turn Red LED to low to indicate sensor is running
  particleSensor.setPulseAmplitudeGreen(0); //Turn off Green LED
}

void loop() {
  String path = "/BPM";
  String path1 = "/temp";
  String jsonStr;
  timeVal=millis();
  if(timeVal - previousVal>=1000){
    sec++;
    tt++;
    previousVal = timeVal;
  }
  
  // BPM 값 얻어오는 코드
    long irValue = particleSensor.getIR();

    if (checkForBeat(irValue) == true)
    {
      //We sensed a beat!
      
      long delta = millis() - lastBeat;
      lastBeat = millis();

      beatsPerMinute = 60 / (delta / 1000.0);

      if (beatsPerMinute < 160 && beatsPerMinute > 60)
      {
        rates[rateSpot++] = (byte)beatsPerMinute; //Store this reading in the array
        rateSpot %= RATE_SIZE; //Wrap variable

        //Take average of readings
        beatAvg = 0;
        for (byte x = 0 ; x < RATE_SIZE ; x++)
          beatAvg += rates[x];
        beatAvg /= RATE_SIZE;
      }
    }
   /* Serial.print("IR=");
    Serial.print(irValue);
    Serial.print(", BPM=");
    Serial.print(beatsPerMinute);
    Serial.print(", Avg BPM=");
    Serial.print(beatAvg);*/

    if (irValue < 50000)
      Serial.print(" No finger?");

    Serial.println();
    // --------------------------------------------------------  firebase 저장
   int a = lastAvg;
  while(beatAvg > 90 && beatAvg != a){ 
    if(Firebase.getInt(fbdo, path + "/ONOFF")){
      if(fbdo.dataType() == "int"){
        if(int av = fbdo.intData() == 1){
          Firebase.setInt(fbdo, path + "/warningBPM", beatAvg);
          lastAvg = beatAvg;
          break;
        }
        else{
          lastAvg = beatAvg;
          break;
        }
      }
    }
  }

  while(sec == 30){ 
    if(Firebase.getInt(fbdo, path + "/ONOFF")){
      if(fbdo.dataType() == "int"){
        if(int av = fbdo.intData() == 1){
          if(t == 0){
            if(zero == 0){
              time[0]= beatAvg;
            }
            else{
              for(int t = 0; t<9; t++)
              {
                time[t] = time[t+1];
              }
              time[9] = beatAvg;
            }
            for(int a =0; a <10; a++){
            Firebase.setInt(fbdo, path + "/timeBPM" +  "/time" + a, time[a]);
            }
            sec = 0;
            t  += 1;
            break;
          }
          else if(t == 1){
             if(zero == 0){
              time[1] = beatAvg;
            }
            else{
              for(int t = 0; t<9; t++)
              {
                time[t] = time[t+1];
              }
              time[9] = beatAvg;
            }
            for(int a =0; a <10; a++){
            Firebase.setInt(fbdo, path + "/timeBPM" +  "/time" + a, time[a]);
            }
            sec = 0;
            t  += 1;
           
            break;
          }
          else if(t == 2){
             if(zero == 0){
              time[2] = beatAvg;
            }
            else{
              for(int t = 0; t<9; t++)
              {
                time[t] = time[t+1];
              }
              time[9] = beatAvg;
            }
            for(int a =0; a <10; a++){
            Firebase.setInt(fbdo, path + "/timeBPM" +  "/time" + a, time[a]);
            }
            sec = 0;
            t  += 1;
            
            break;
          }
          else if(t == 3){
           if(zero == 0){
            time[3]= beatAvg;
          }
          else{
          for(int t = 0; t<9; t++)
          {
            time[t] = time[t+1];
            }
            time[9] = beatAvg;
          }
          for(int a =0; a <10; a++){
          Firebase.setInt(fbdo, path + "/timeBPM" +  "/time" + a, time[a]);
          }
          sec = 0;
          t  += 1;
          break;
        }
          else if(t == 4){
            if(zero == 0){
            time[4]= beatAvg;
            }
            else{
              for(int t = 0; t<9; t++)
            {
              time[t] = time[t+1];
            }
              time[9] = beatAvg;
            }
            for(int a =0; a <10; a++){
            Firebase.setInt(fbdo, path + "/timeBPM" +  "/time" + a, time[a]);
            }
            sec = 0;
            t  += 1;
            break;
          }
          else if(t == 5){
            if(zero == 0){
            time[5]= beatAvg;
          }
            else{
              for(int t = 0; t<9; t++)
             {
               time[t] = time[t+1];
            }
              time[9] = beatAvg;
            }
            for(int a =0; a <10; a++){
            Firebase.setInt(fbdo, path + "/timeBPM" +  "/time" + a, time[a]);
            }
            sec = 0;
            t  += 1;
            break;
          }
          else if(t == 6){
            if(zero == 0){
            time[6]= beatAvg;
          }
            else{
              for(int t = 0; t<9; t++)
             {
               time[t] = time[t+1];
            }
              time[9] = beatAvg;
            }
            for(int a =0; a <10; a++){
            Firebase.setInt(fbdo, path + "/timeBPM" +  "/time" + a, time[a]);
            }
            sec = 0;
            t  += 1;
            break;
          }
          else if(t == 7){
            if(zero == 0){
            time[7]= beatAvg;
          }
            else{
              for(int t = 0; t<9; t++)
             {
               time[t] = time[t+1];
            }
              time[9] = beatAvg;
            }
            for(int a =0; a <10; a++){
            Firebase.setInt(fbdo, path + "/timeBPM" +  "/time" + a, time[a]);
            }
            sec = 0;
            t  += 1;
            break;
          }
          else if(t == 8){
            if(zero == 0){
            time[8]= beatAvg;
          }
            else{
              for(int t = 0; t<9; t++)
             {
               time[t] = time[t+1];
            }
              time[9] = beatAvg;
            }
            for(int a =0; a <10; a++){
            Firebase.setInt(fbdo, path + "/timeBPM" +  "/time" + a, time[a]);
            }
            sec = 0;
            t  += 1;
            break;
          }
          else if(t == 9){
            if(zero == 0){
            time[9]= beatAvg;
          }
            else{
              for(int t = 0; t<9; t++)
             {
               time[t] = time[t+1];
            }
              time[9] = beatAvg;
            }
            for(int a =0; a <10; a++){
            Firebase.setInt(fbdo, path + "/timeBPM" +  "/time" + a, time[a]);
            }
            sec = 0;
            t  = 0;
            zero = 1;
            break;
          }
        }
        else{
          sec = 0;
          break;
        }
      }
    }
  }
  val = analogRead(analPin);    // read the value from the sensor
  x = map(val,0,1023,0,vcc);
  th = (((float)(vcc-x)*10.0)/(float)x)*1000.0;
  ce = ((log(4.0*th - 3000.0) / (-0.024119329) + 473)-32.0)/1.8;
  if(ce < 30)
    {
      ce = ce + 6;
    }
  else if(ce > 41)
    {
      ce = ce - 6;
    }
  while(tt == 20){
    if(Firebase.getInt(fbdo, path1 + "/ONOFF")){
      if(fbdo.dataType() == "int"){
        if(int av = fbdo.intData() == 1){
          
          Firebase.setFloat(fbdo, path1 + "/TEMP", ce);
          Firebase.setInt(fbdo, path1 + "/ONOFF", 0);
          tt =0;
          break;
        }
        else{
          tt =0;
          break;
        }
      }
    }
  }
}
