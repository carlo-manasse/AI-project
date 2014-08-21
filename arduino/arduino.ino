#include <Servo.h> 

//servo up/down
Servo servo1;
//servo left/right
Servo servo2;

//input (switch ottici)
int opticalSwitchA = 3;
int opticalSwitchB = 2;

int enableMotorA = 5;
int enableMotorB = 6;
int pwmA = 0;
int pwmB = 0;

volatile int countA=0;
volatile int countB=0;
unsigned long prevTimeA=0;
unsigned long prevTimeB=0;
unsigned long timeElapsedA=0;
unsigned long timeElapsedB=0;
//unsigned long lastOkSendTime=0;

//values
//boolean isDoingAction=false;
unsigned long lastActionTime=0;
//unsigned long maxActionDuration=200;

float actualSpeedA=0;
float targetSpeedA=0;

boolean newAcquisitionA=false;
boolean newAcquisitionB=false;

float actualSpeedB=0;
float targetSpeedB=0;

boolean debug=false;

//long currentTimeA=0;
//long currentTimeB=0;
long currentTimeStamp=0;

float vehicleTargetSpeed=0;

float maxTargetSpeed=0.6;
//float minTargetSpeed=0.5;

int angle=0;
int speedIncomingValue=0;

float mA=89.6;
float qA=50;
float mB=89.6;
float qB=50;


float t1A=0;
float t2A=0;
float t1B=0;
float t2B=0;

int numAcquisitionA=0;
int numAcquisitionB=0;

void setup() {
  initServo();
  //input
  Serial.begin(9600);
  attachInterrupt(1, counterA, CHANGE);
  attachInterrupt(0, counterB, CHANGE);
  
  //output
  pinMode(enableMotorA, OUTPUT);
  pinMode(enableMotorB, OUTPUT);
    
    pwmA = 0;
    pwmB = 0;
    //vehicleTargetSpeed=0;
    angle=90;
}


void initServo(){

  int servoPin1 = 9;  
  int angleServo1 = 157;
  int servoPin2 = 10;  
  int angleServo2 = 84;
  
  servo1.attach(servoPin1); 
  servo2.attach(servoPin2);
  
  servo1.write(angleServo1);
  servo2.write(angleServo2);
  
  
  //calcolare qA o 2 punti sulla retta
  //calcolare starting point(pwm)
  //questo può essere usato se il pwm per un tgspeed è < di starting point e il robot è fermo.
  //in tal caso non si deve modificare la retta ma dare una spinta minima al robot per partire.
  //
}

void loop() {
  //delay(2000);
  /*
    if(millis()-currentTimeStamp>2000){
      Serial.print("targetSpeed: ");
      Serial.println(vehicleTargetSpeed);
      
      Serial.print("targetSpeedA: ");
      Serial.println(targetSpeedA);
      Serial.print("giri al sec motore A: ");
      Serial.println(actualSpeedA);
      Serial.print("pwmA: ");
      Serial.println(pwmA);
      Serial.print("qA: ");
      Serial.println(qA);
      Serial.print("mA: ");
      Serial.println(mA);
      Serial.print("targetSpeedB: ");
      Serial.println(targetSpeedB);
      Serial.print("giri al sec motore B: ");
      Serial.println(actualSpeedB);
      Serial.print("pwmB: ");
      Serial.println(pwmB);
      Serial.print("qB: ");
      Serial.println(qB);
      Serial.print("mB: ");
      Serial.println(mB);

     currentTimeStamp=millis();
  }*/

  calculateTargetSpeedA();
  calculateTargetSpeedB();
  
  setPwmA();
  setPwmB();
  
  setMotorSpeed();   
  
  if(targetSpeedA > 0.2){
    if(newAcquisitionA){
      if(t1A!=targetSpeedA){
        t2A=t1A;
        t1A=targetSpeedA;
      }
      /*
      if(numAcquisitionA>1){
        updateRettaA();
        numAcquisitionA=0;
      }else{
        numAcquisitionA++;
      }
      */
        
        //updateRettaA();
        updateqA();
      newAcquisitionA=false;
    }
  }
  
  if(targetSpeedB > 0.2){
    if(newAcquisitionB){
      if(t1B!=targetSpeedB){
        t2B=t1B;
        t1B=targetSpeedB;
      }
      
      /*
      if(numAcquisitionB>1){
        updateRettaB();
        numAcquisitionB=0;
      }else{
        numAcquisitionB++;
      }*/
      
        //updateRettaB();
        updateqB();
      newAcquisitionB=false;
    }
  }
  
  
  controlIsMovingA();
  controlIsMovingB(); 
  getTargetSpeedAndAngle(); 
}

void calculateTargetSpeedA(){
  float v=angle-60;
  if(v>=30){
    targetSpeedA=vehicleTargetSpeed;
  }
  else{
    float x=v/30;
    //targetSpeedA=(v/30)*vehicleTargetSpeed;
    targetSpeedA=(-x*x+2*x)*vehicleTargetSpeed;
  }
}

void calculateTargetSpeedB(){
  float v=angle-60;
  if(v<=30){
    targetSpeedB=vehicleTargetSpeed;
  }
  else{
    float x=1-(v-30)/30;
    //targetSpeedB=(1-(v-30)/30)*vehicleTargetSpeed;
    targetSpeedB=(-x*x+2*x)*vehicleTargetSpeed;
  }
}

float calculatePwmA(float val){
  return val*mA+qA;
}

float calculatePwmB(float val){
  return val*mB+qB;
}

void setPwmA(){
  if(targetSpeedA < 0.3)
    pwmA=0;
  else{
    pwmA = calculatePwmA(targetSpeedA);
  }
}

void setPwmB(){
  if(targetSpeedB < 0.3)
    pwmB=0;
  else{
    pwmB = calculatePwmB(targetSpeedB);
  }
}

void updateRettaA(){
  //modifica retta (m e q)
  
  if(updateqA() && t2A > 0.3){
  
      float y1=t2A*mA+qA;
      float x1=t2A;
      float x2=(actualSpeedA+t1A)/2;
      float y2=t1A*mA+qA;      
          mA=(y2-y1)/(x2-x1);
          qA=-mA*x1+y1;
          if(mA>150)
            mA=150;
          if(mA<50)
            mA=50;
        t2A=0;
    }
    
    
    if(debug){
      Serial.print("targetSpeedA: ");
      Serial.println(targetSpeedA);
      Serial.print("giri al sec motore A: ");
      Serial.println(actualSpeedA);
      Serial.print("pwmA: ");
      Serial.println(pwmA);
      Serial.print("qA: ");
      Serial.println(qA);
      Serial.print("mA: ");
      Serial.println(mA);
    }
   
}

void updateRettaB(){
  //modifica retta (m e q)
  
  if(updateqB() && t2B > 0.3){
  
      float y1=t2B*mA+qA;
      float x1=t2B;
      float x2=(actualSpeedB+t1B)/2;
      float y2=t1B*mB+qB;      
          mB=(y2-y1)/(x2-x1);
          qB=-mB*x1+y1;
          if(mB>150)
            mB=150;
          if(mB<50)
            mB=50;
        t2B=0;
   
    }
    
    if(debug){
      Serial.print("targetSpeedB: ");
      Serial.println(targetSpeedB);
      Serial.print("giri al sec motore B: ");
      Serial.println(actualSpeedB);
      Serial.print("pwmB: ");
      Serial.println(pwmB);
      Serial.print("qB: ");
      Serial.println(qB);
      Serial.print("mB: ");
      Serial.println(mB);
    }
}

boolean updateqA(){

      boolean isGood=true;
      double diff=t1A-actualSpeedA;
      
      if( abs(diff) > 0.03 ){
        if(diff>0)
          qA+=5;
        else
          qA-=5;
        //qA=pwmA-mA*(actualSpeedA+t1A)/2;
    //    Serial.print("new qA: ");
    //    Serial.println(qA);
  
        if(calculatePwmA(t1A)>230)
          qA-=calculatePwmA(t1A)-230;
        
        if(qA<0)
          qA=0;
        isGood=false; 
      }
      return isGood;
}

boolean updateqB(){

      boolean isGood=true;
      double diff=t1B-actualSpeedB;
      
      if( abs(diff) > 0.03 ){
        if(diff>0)
          qB+=5;
        else
          qB-=5;
        
        //qB=pwmB-mB*(actualSpeedB+t1B)/2;
  //      Serial.print("new qB: ");
   //     Serial.println(qB);
  
        if(calculatePwmB(t1B)>230)
          qB-=calculatePwmB(t1B)-230;
        
        if(qB<0)
          qB=0;
        isGood=false; 
      }
      return isGood;
}

void controlIsMovingA(){
  if(millis()-prevTimeA>800){
    actualSpeedA=0;
    prevTimeA=millis();
    newAcquisitionA=true;
  }
}


void controlIsMovingB(){
  if(millis()-prevTimeB>800){
    actualSpeedB=0;
    prevTimeB=millis();
    newAcquisitionB=true;
  }
}

//these functions are called by an interrupt
void counterA(){
  //avoiding casual switching
  //remember in my case 10ms < 1000ms/4*12
  //where 4 is the max number of wheel rotation (at full speed) and 12 is the number of switching per rotation
  if(millis()-prevTimeA>10){
    countA=countA+1;
    
    long timeElapsed=millis()-prevTimeA;
    timeElapsedA+=timeElapsed;
    if(countA%2==0){
      calculateActualSpeedA2(timeElapsedA);
      timeElapsedA=0;
      newAcquisitionA=true;
    }
    
    prevTimeA=millis();
  }
//  isStoppedA=false;
}

//these functions are called by an interrupt
void counterB(){
  //avoiding casual switching
  //remember in my case 10ms < 1000ms/4*12
  //where 4 is the max number of wheel rotation (at full speed) and 12 is the number of switching per rotation
  if(millis()-prevTimeB>10){
    countB=countB+1;
    
    long timeElapsed=millis()-prevTimeB;
    timeElapsedB+=timeElapsed;
    if(countB%2==0){
      calculateActualSpeedB2(timeElapsedB);
      timeElapsedB=0;
      newAcquisitionB=true;
    }
    
    prevTimeB=millis();
  }
//  isStoppedA=false;
}

void calculateActualSpeedA2(long timeElapsed){
      actualSpeedA=1000/(6*(float)timeElapsed);
}
void calculateActualSpeedB2(long timeElapsed){
      actualSpeedB=1000/(6*(float)timeElapsed);
}

void setMotorSpeed() {   
  if( pwmA<256&& pwmB<256&& pwmA>=0&& pwmB>=0){
    analogWrite( enableMotorA, pwmA);
    analogWrite( enableMotorB, pwmB);
  }
}

void getTargetSpeedAndAngle(){

    if(Serial.available() > 0){
      if(readAction()){
        //isDoingAction=true;
        Serial.print("p");
        lastActionTime=millis();
        vehicleTargetSpeed=(float)speedIncomingValue/255*maxTargetSpeed;
        /*
        if (angle!=90){
          vehicleTargetSpeed=0.7*vehicleTargetSpeed;
        }*/
      }
      else{
        Serial.flush();
      }
    }
    else{
        if(millis()-lastActionTime>1000)
          vehicleTargetSpeed=0;
    }
}


//read data of the form sss_aaaE
//where sss is speed and aaa is angle
//ex: 255_090E
boolean readAction(){
  int dataPosition=0;
  char incomingByte = 0;
  char MotorSpeedStr[4];
  char DegreesStr[4];
  
  for(int i=0;i<4;i++){
    DegreesStr[i]=0;
    MotorSpeedStr[i]=0;
  }
  boolean isReadingCorrect=true;
  boolean isReading=true;
  
  unsigned long maxTimeForReading=200;
  unsigned long currentReadingTime=millis();
  
  while(isReadingCorrect && isReading && millis()-currentReadingTime<maxTimeForReading){
    if (Serial.available() > 0) {
      // read the incoming byte:
      incomingByte = Serial.read();
      //reading speed value 
      if(dataPosition<3){
        if(isNumber(incomingByte)){
          MotorSpeedStr[dataPosition]=incomingByte;
        }
        else{
          isReadingCorrect=false;
        }
      }
      else{
        //reading delimiter
        if(dataPosition==3){
          if(incomingByte!='_'){
            isReadingCorrect=false;
          }
        }
        //reading angle value
        else{
          //data position is bigger than 3 and less than 7
          if(dataPosition<7){
            if(isNumber(incomingByte)){
              DegreesStr[dataPosition-4]=incomingByte;
            }
            else{
              isReadingCorrect=false;
            }
          }
          else{
              if(dataPosition==7){
                if(incomingByte!='E'){
                  isReadingCorrect=false;
                }else{
                  isReading=false;
                }
              }
              //data position is bigger than 7
              else{
                isReadingCorrect=false;
              }
          }
        }
      }
      dataPosition++;
    }
  }

  if(isReadingCorrect){
    //desiredSpeed= atoi(MotorSpeedStr);
    speedIncomingValue= atoi(MotorSpeedStr);
    //vehicleTargetSpeed=(float)vehicleSpeed/255*maxTargetSpeed;
    angle = atoi(DegreesStr);
  }  
  

  
  return isReadingCorrect;
}



boolean isNumber(char incomingByte){
  boolean isNumber=false;
  
  if(incomingByte=='0'){
    isNumber=true;
  }
  else{
    if(incomingByte=='1'){
      isNumber=true;
    }
    else{
      if(incomingByte=='2'){
        isNumber=true;
      }
      else{
        if(incomingByte=='3'){
          isNumber=true;
        }
        else{
          if(incomingByte=='4'){
            isNumber=true;
          }
          else{
            if(incomingByte=='5'){
              isNumber=true;
            }
            else{
              if(incomingByte=='6'){
                isNumber=true;
              }
              else{
                if(incomingByte=='7'){
                  isNumber=true;
                }
                else{
                  if(incomingByte=='8'){
                    isNumber=true;
                  }
                  else{
                    if(incomingByte=='9'){
                      isNumber=true;
                    }
                  }
                }
              }
            }
          }        
        }
      }
    }
  }
  
  return isNumber;
}

