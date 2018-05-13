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

float[] depthLookUp = new float[2048];

// ================================================================

public void settings(){ 
	size(stageW, stageH, P3D);
}

// ================================================================

public void setup() {
	background(bgC);
	kinect = new Kinect(this);

	kinect.initDepth();
	// kinect.initDevice();

  // Lookup table for all possible depth values (0 - 2047)
  for (int i = 0; i < depthLookUp.length; i++) {
    depthLookUp[i] = rawDepthToMeters(i);
  }

}

// ================================================================

// These functions come from: http://graphics.stanford.edu/~mdfisher/Kinect.html
public float rawDepthToMeters(int depthValue) {
  if (depthValue < 2047) {
    return (float)(1.0f / ((double)(depthValue) * -0.0030711016f + 3.3309495161f));
  }
  return 0.0f;
}

// ================================================================
public void draw() {
	background(bgC);
	PImage img = kinect.getDepthImage();
	image(img, 0, 0);

	int[] depth = kinect.getRawDepth();

	int skip = 10;
	for (int x = 0; x < kinect.width; x += skip) {
		for (int y = 0; y < kinect.height; y += skip) {
			int index = x + y * kinect.width;
			int d = depth[index];

			PVector v = depthToWorld(x, y, d);

	    stroke(255);
	    pushMatrix();
		    // Scale up by 200
		    translate(x, y + 480);
		    float factor = 200;
		    translate(v.x * factor, v.y * factor , factor - v.z * factor);
		    ellipse(x, y, 8, 8);
	    popMatrix();
		}
	}
}

// ================================================================

// Only needed to make sense of the ouput depth values from the kinect
public PVector depthToWorld(int x, int y, int depthValue) {

  final double fx_d = 1.0f / 5.9421434211923247e+02f;
  final double fy_d = 1.0f / 5.9104053696870778e+02f;
  final double cx_d = 3.3930780975300314e+02f;
  final double cy_d = 2.4273913761751615e+02f;

	// Drawing the result vector to give each point its three-dimensional space
  PVector result = new PVector();
  double depth =  depthLookUp[depthValue];//rawDepthToMeters(depthValue);
  result.x = (float)((x - cx_d) * depth * fx_d);
  result.y = (float)((y - cy_d) * depth * fy_d);
  result.z = (float)(depth);
  return result;
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
