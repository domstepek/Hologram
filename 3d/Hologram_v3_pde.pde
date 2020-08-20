import peasy.*;
import java.io.File;
PeasyCam cam;

final int HS_COUNT = 1;
HoloSphere holospheres[] = new HoloSphere[HS_COUNT]; 

int ic = 8;
PImage images[] = new PImage[ic];;
PImage pic;

PShape globe;
PShape globe2;
PShape earth;

int zoom = 9999999;
float persp = 1.1;

void setup() 
{
  size(1280,720,P3D);
  
  //for (int i = 1; i <= ic; ++i){
  //  images[i-1] = loadImage(String.format("fish/%s.png", i));
  //  images[i-1].loadPixels();
  //}
  
  PImage bg;
  bg = loadImage("bg2.jpg");
  noStroke();
  globe = createShape(SPHERE, 10000); 
  globe.setTexture(bg);
  
  
  PImage bg2; 
  bg2 = loadImage("bg.jpg");
  globe2 = createShape(SPHERE, 2000000);
  globe2.setTexture(bg2);
  
  PImage earthpic;
  earthpic = loadImage("earth hq.jpg");
  earthpic.loadPixels();
  
  earth = createShape(SPHERE, 100);
  earth.setTexture(earthpic);
  
  int max_size = 200;
  cam = new PeasyCam(this, max_size * 1.25);
  perspective(PI/3.0, (float)width/(float)height, 0.001, zoom);
  
  for (int i = 0; i < HS_COUNT; ++i)
  {
    holospheres[i] = new HoloSphere(max_size - (i * (max_size / HS_COUNT)), 1, 0, i * (PI / HS_COUNT), 400, earthpic.width, earthpic.height);
    holospheres[i].curr_image = earthpic;
  }
  
}

float speed = 1;
float max_speed = 200;
float accelerant = 3;

void draw()
{
  background(11);
  lights();
  
  if (frameCount != 1) return;
  for (int j = 0; j < (int)speed; ++j) {
  for (int i = 0; i < HS_COUNT; ++i)
    {
      holospheres[i].Spin((int)speed);
    }
  }
  
  if (speed < max_speed)
    speed += accelerant;
}

class HoloSphere {
  private int G_COUNT;
  private float G_RADIUS;
  private float G_OFFSET;
  private float BIG_THETA;
  private int _angle_index = 0;
  private int max_angles;
  public PImage curr_image = new PImage();
  
  private Gamma allGammas[]; 
  
  HoloSphere(int gamma_count, float gamma_radius, float gamma_offset, float big_theta, int _max_angles, int img_width, int img_height) {
    G_COUNT = gamma_count;
    G_RADIUS = gamma_radius;
    G_OFFSET = gamma_offset;
    BIG_THETA = big_theta;
    max_angles = _max_angles;
    
    allGammas = new Gamma[G_COUNT];
    float length = G_COUNT * (2 * G_RADIUS + G_OFFSET);
    float torus_radius = length / TWO_PI;
  
    for (int i = 0; i < G_COUNT; ++i) {
      allGammas[i] = new Gamma(max_angles, i, torus_radius, BIG_THETA, G_OFFSET, G_RADIUS, img_width, img_height);
    }
  }
  
  void Spin(int angle) {
      _angle_index += angle;
      if (_angle_index >= max_angles)
        _angle_index = _angle_index % max_angles;
       
     for (int j = 0; j < G_COUNT; ++j) {
       PVector new_pos = allGammas[j].GetCoordinate(_angle_index);
       PVector img_pos = allGammas[j].GetPixelCoordinate(_angle_index);
       
       //int img_index = (int)(img_pos.y*curr_image.width+img_pos.x);
       translate(new_pos.x, new_pos.y, new_pos.z);
       allGammas[j].Draw(curr_image.get((int)img_pos.x, (int)img_pos.y));
       translate(-new_pos.x, -new_pos.y, -new_pos.z);
     }
  }
}

class Gamma {
 private int _MAX_ANGLES;
 private float rad;
 
 private int res = 5;
 private PVector coordinates[]; 
 private PVector pixel_coordinates[];
 
 Gamma(int max_angles, int i, float t_rad, float big_theta, float g_delta, float g_radius, int img_width, int img_height) {
   _MAX_ANGLES = max_angles;
   rad = g_radius;
   
   coordinates = new PVector[_MAX_ANGLES];
   pixel_coordinates = new PVector[_MAX_ANGLES];
   
   float phi = (i * (2 * g_radius + g_delta) + g_radius) / t_rad - HALF_PI;
   float v = map(t_rad * sin(phi), -t_rad, t_rad, 0, img_height - 1);
   for (int j = 0; j < _MAX_ANGLES; ++j) {
     //(x * TWO_PI / Theta + Omega + (PI if phi >= HALF_PI else 0)) % TWO_PI
     float theta = (float)j / _MAX_ANGLES * TWO_PI + big_theta;
     
     float u = map((phi >= HALF_PI ? theta + PI : theta) % TWO_PI, 0, TWO_PI, 0, img_width-1);

     float x = -t_rad * cos(theta) * sin(phi);
     float y = -t_rad * cos(phi);
     float z = t_rad * sin(theta) * sin(phi);
  
     coordinates[j] = new PVector(x,y,z);
     pixel_coordinates[j] = new PVector(u, v);
     
     if (i == 100)
       println(u, v);
   }
 }
 
 PVector GetCoordinate(int index)
 {
   if (index >= 0 && index < _MAX_ANGLES)
     return coordinates[index];
    else
     return null;
 }
 
 PVector GetPixelCoordinate(int index) 
 {
   if (index >= 0 && index < _MAX_ANGLES)
     return pixel_coordinates[index];
   else
     return null;
 }
 
 void Draw(color Color) {
   if (Color == 0)
      fill(color(0,0,0,0));
   else
     fill(Color);
     
   noStroke();
   sphereDetail(res);
   sphere(rad);
 }
}
