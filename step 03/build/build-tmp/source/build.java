import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import org.openkinect.freenect.*; 
import org.openkinect.processing.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class build extends PApplet {

int stageW      = 640;
int stageH      = 480 * 2;
int bgC       = 0xff3F3F3F;
String dataPATH = "../../data";

// ================================================================




// ================================================================

Kinect kinect;

// ================================================================

public void settings(){ 
	size(stageW, stageH);
}

// ================================================================

public void setup() {
	background(bgC);
	kinect = new Kinect(this);

	kinect.initDepth();
	// kinect.initDevice();

}

// ================================================================
public void draw() {
	background(bgC);
	
	PImage img = kinect.getDepthImage();
	image(img, 0, 0);

	int skip = 20;
	for (int x = 0; x < img.width; x += skip) {
		for (int y = 0; y < img.height; y += skip) {
			int index = x + y * img.width;
			float b = brightness(img.pixels[index]);
			
			println(b);
			float _r = map(b , 0, 255, 24, 2);
			noStroke();
			fill(b);
			ellipse(x + (skip / 2), y + 480 + (skip / 2), _r, _r);
		}
	}

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "build" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
