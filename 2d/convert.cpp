const int MOTOR_PIN = 3;

Bmp c_image;
bool imaged_changed = false;
point center;

void setup() {
  pinMode(MOTOR_PIN, OUTPUT);
}


void draw() {
  analogWrite(MOTOR_PIN, 255);

  if (image_changed) {
    center.y = c_image.height / 2;
    center.x = c_image.width / 2;
  }

  
}
