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

int czulosc=1;

float x = 0;
float y = 0;

void setup()  
{
  //ustawiamy na 9600 bo tak działa nasz BT
  Serial.begin(9600); 

  // rejestrujemy funkcje odbierajaca komunikacje z telefonu na kanale A przy pomocy pluginu amarino sensory
  meetAndroid.registerFunction(car, 'A');  

  pinMode(onboardLed, OUTPUT);
  pinMode(lewo, OUTPUT);
  pinMode(prawo, OUTPUT);
  pinMode(przod, OUTPUT);
  pinMode(tyl, OUTPUT);

  digitalWrite(onboardLed, LOW);
  digitalWrite(lewo, LOW);
  digitalWrite(prawo, LOW);
  digitalWrite(przod, LOW);
  digitalWrite(tyl, LOW);

}

void loop()
{
  meetAndroid.receive(); // odbieranie 
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


  }
  else if (x < czulosc)
  { 
    // flushLed(3);
    digitalWrite(przod, LOW);
    digitalWrite(tyl, HIGH);

  }
  else 
  { 
    digitalWrite(przod, LOW);
    digitalWrite(tyl, LOW);
 meetAndroid.send("silnik kirunku stoi");
  }

  if (y < czulosc)
  {
    digitalWrite(lewo, HIGH);
    digitalWrite(prawo, LOW);

  }
  else if (y > czulosc)
  { 
    digitalWrite(lewo, LOW);
    digitalWrite(prawo, HIGH);
  }
  else 
  { 
    digitalWrite(lewo, LOW);
    digitalWrite(prawo, LOW);
     meetAndroid.send("silnik ruchu stoi");
  }

}

void flushLed(int time)
{
  digitalWrite(onboardLed, LOW);
  delay(time);
  digitalWrite(onboardLed, HIGH);
}


