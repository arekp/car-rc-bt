/*
  Sterowanie zmodyfikowanym samochodem RC za pomoca telefonu i BT
 
 */

#include <MeetAndroid.h>

// MeetAndroid meetAndroid();
// you can define your own error function to catch messages
// where not fuction has been attached for
MeetAndroid meetAndroid(error);

void error(uint8_t flag, uint8_t values){
  Serial.print("ERROR: ");
  Serial.print(flag);
}

int onboardLed = 13;
int lewo = 2;
int prawo = 3;
int przod = 4;
int tyl = 5;

int predkosc1 = 5; 
int predkosc2 = 4; 

int czulosc=1;
 int inChar;

float x = 0;
float y = 0;

void setup()  
{
  //  ustawiamy na 9600 bo tak dziaĹa nasz BT
  Serial.begin(9600); 

  // rejestrujemy funkcje odbierajaca komunikacje z telefonu na kanale A przy pomocy pluginu amarino sensory
  meetAndroid.registerFunction(car, 'A');  

  pinMode(onboardLed, OUTPUT);
  pinMode(lewo, OUTPUT);
  pinMode(prawo, OUTPUT);
  pinMode(przod, OUTPUT);
  pinMode(tyl, OUTPUT);

  pinMode(predkosc1, OUTPUT); 
  pinMode(predkosc2, OUTPUT); 
  
  digitalWrite(onboardLed, LOW);
  digitalWrite(lewo, LOW);
  digitalWrite(prawo, LOW);
  digitalWrite(przod, LOW);
  digitalWrite(tyl, LOW);
  analogWrite(predkosc1, 100);
    analogWrite(predkosc2, 100);
}

void loop()
{
  meetAndroid.receive(); // odbieranie 
  
//if (Serial.available() > 0) {
//    int inByte = Serial.read();
//    switch (inByte) {
//    case '1':    
//    {Serial.println(inByte);
//       strowanie(2,0);
      // analogWrite(predkosc2, 200);
 //     break;
 //   }
 //   case '2':    
 //   {Serial.println("inByte");
     //  analogWrite(predkosc2, 100);
 //     strowanie(-2,0);
 //     break;
 //   }

 //   case '3':    
 //     strowanie(0,0);
 //     break;
 //   default:
 //     strowanie(0,0);
 //   }
 // }
}


/*
 * Procedura obslugujaca sterowanie samochodem
 */
 
void car(byte flag, byte numOfValues)
{
  int data[numOfValues];
  meetAndroid.getIntValues(data);

  x = data[0];
  y = data[1];

  strowanie(x,y);

  for (int i=0; i<numOfValues;i++)
  {
    meetAndroid.send(data[i]);
  }
}

// procedura odpowiedzialna za sterowani silnikami
void strowanie(float x, float y)
{
  if (x > czulosc)
  {
    digitalWrite(przod, HIGH);
    digitalWrite(tyl, LOW);
    analogWrite(predkosc1, (x * 28));
  }
  else if (x < (czulosc * -1))
  { 
    // flushLed(3);
    digitalWrite(przod, LOW);
    digitalWrite(tyl, HIGH);
    analogWrite(predkosc1, ((x * -1) * 28));
  }
  else 
  { 
    digitalWrite(przod, LOW);
    digitalWrite(tyl, LOW);
    analogWrite(predkosc1, 0);
 meetAndroid.send("silnik kirunku stoi");
  }

  if (y < (czulosc* -1))
  {
    digitalWrite(lewo, HIGH);
    digitalWrite(prawo, LOW);
    analogWrite(predkosc2, ((y * -1) * 28));
  }
  else if (y > czulosc)
  { 
    digitalWrite(lewo, LOW);
    digitalWrite(prawo, HIGH);
        analogWrite(predkosc2, (y * 28));
  }
  else 
  { 
    digitalWrite(lewo, LOW);
    digitalWrite(prawo, LOW);
        analogWrite(predkosc2, 0);
     meetAndroid.send("silnik ruchu stoi");
  }

}

void flushLed(int time)
{
  digitalWrite(onboardLed, LOW);
  delay(time);
  digitalWrite(onboardLed, HIGH);
}



