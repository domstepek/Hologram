import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Hologram extends PApplet {

// Simulation Variables
static final int AXEL_COUNT = 1;// Amount of layers
static final int LED_COUNT = 100;// Should be an odd number for best results
static final int TOTAL_ANGLES = 360;
static final int ANGLES_PER_DRAW = 1;

static final float AXEL_LENGTH = 540.0f;
static final float LED_OFFSET = 0.0f;// Spacing between each LED. The smaller the number, the higher the 'resolution of the image.
static final float LED_RADIUS = (AXEL_LENGTH - (LED_COUNT - 1.0f) * LED_OFFSET) / (2.0f * LED_COUNT);

static final Axel AxelArr[] = new Axel[AXEL_COUNT];

// Setup Variables
static final boolean _singleImage = true;
static final String _imagePath = "assets/google.png";
static final String _folderPath = "assets/object/%s.gif"; // Put a '%s' where you want the image count to be

// Image Variables
static final int TOTAL_FRAMES = 25;                 // Amount of frames to loop through 
static final int SCALING_FACTOR = 1;
static int curr_frame = 0;
static int image_dimension;
static PImage frames[] = new PImage[TOTAL_FRAMES];  // Holds the frames of an animation

public void setup() {
  
  if (_singleImage) {
    frames = new PImage[1];
    frames[0] = loadImage(_imagePath);
    frames[0].loadPixels();
  } 
  else {
    for (int i = 1; i <= TOTAL_FRAMES; ++i) {
      frames[i - 1] = loadImage(String.format(_folderPath, i));
      frames[i - 1].loadPixels();
    }
  }

  image_dimension = frames[0].width;

  for (int i = 0; i < AXEL_COUNT; ++i) {
    AxelArr[i] = new Axel(i * PI / AXEL_COUNT);
  }

  noLoop();
  
}

public void draw() {
  translate(width / 2, height / 2);
  background(255);

  for (int i = 0; i < ANGLES_PER_DRAW; ++i) {
    for (int s = 0; s < AXEL_COUNT; ++s) {
      for (int j = 0; j < LED_COUNT; ++j) {
        ImageCoordinate curr_coord = AxelArr[s].LEDs[j].CurrentImageCoordinate();
        AxelArr[s].LEDs[j].DrawNext(frames[curr_frame].get(curr_coord.X, curr_coord.Y));
      }
    }
  }
  save("diagonal.png");
  if (!_singleImage && ++curr_frame == TOTAL_FRAMES) {
    curr_frame = 0;
  }
}

class Axel {
  private LED[] LEDs = new LED[LED_COUNT];
  
  Axel(float angle_offset) {
    for (int i = 0; i < LED_COUNT; ++i) {
      float mag = i * (2.0f * LED_RADIUS + LED_OFFSET) + LED_RADIUS - (AXEL_LENGTH / 2.0f);
      PolarCoordinate pos = new PolarCoordinate(mag, angle_offset);
     this.LEDs[i] = new LED(pos);
    }
  }
}

class LED {
  private int Color;
  private int Iteration = 0;
  private CartesianCoordinate Positions[] = new CartesianCoordinate[TOTAL_ANGLES];
  private ImageCoordinate ImageCoordinates[] = new ImageCoordinate[TOTAL_ANGLES];
  
  LED(PolarCoordinate position) {
    noStroke();

    for (int i = 0; i < TOTAL_ANGLES; ++i) {
      float x = position.Magnitude * cos(TWO_PI / TOTAL_ANGLES * PApplet.parseFloat(i) + position.Angle);
      float y = position.Magnitude * sin(TWO_PI / TOTAL_ANGLES * PApplet.parseFloat(i) + position.Angle);

      this.Positions[i] = new CartesianCoordinate(x, y);
      
      int mappedX = PApplet.parseInt(map(Positions[i].X, -AXEL_LENGTH / SCALING_FACTOR, AXEL_LENGTH / SCALING_FACTOR, 0, image_dimension));
      int mappedY = PApplet.parseInt(map(Positions[i].Y, -AXEL_LENGTH / SCALING_FACTOR, AXEL_LENGTH / SCALING_FACTOR, 0, image_dimension));

      this.ImageCoordinates[i] = new ImageCoordinate(mappedX, mappedY);
    }
  }

  public void DrawNext(int c) {
    if (++Iteration == TOTAL_ANGLES)
      Iteration = 0;
    
    Color = c;
    if (Color == 0)
      fill(0,0,0,0);
    else
      fill(Color);
    circle(this.Positions[Iteration].X, this.Positions[Iteration].Y, LED_RADIUS);
  }

  public CartesianCoordinate CurrentCoordinate() {
    return this.Positions[Iteration];
  }
  
  public ImageCoordinate CurrentImageCoordinate() {
    return this.ImageCoordinates[Iteration];
  }
}

class CartesianCoordinate {
 private float X;
 private float Y;
 
 CartesianCoordinate(float x, float y) {
   X = x;
   Y = y;
 }
}

class ImageCoordinate {
  private int X;
  private int Y;

  ImageCoordinate(int x, int y) {
    X = x;
    Y = y;
  }
}

class PolarCoordinate {
  private float Magnitude;
  private float Angle;
  
  PolarCoordinate(float mag, float angle) {
    Magnitude = mag;
    Angle = angle;
  }
}
  public void settings() {  size(960, 540); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Hologram" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
