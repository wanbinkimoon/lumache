int stageW      = 640;
int stageH      = 480 * 2;
color bgC       = #3F3F3F;
String dataPATH = "../../data";

// ================================================================

import org.openkinect.freenect.*;
import org.openkinect.processing.*;

// ================================================================

Kinect kinect;

// ================================================================

float[] depthLookUp = new float[2048];

// ================================================================

void settings(){ 
	size(stageW, stageH, P3D);
}

// ================================================================

void setup() {
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
float rawDepthToMeters(int depthValue) {
  if (depthValue < 2047) {
    return (float)(1.0 / ((double)(depthValue) * -0.0030711016 + 3.3309495161));
  }
  return 0.0f;
}

// ================================================================
void draw() {
	background(bgC);
	PImage img = kinect.getDepthImage();
	image(img, 0, 0);

	renderPoints();
}

// ================================================================

void renderPoints(){
	int[] depth = kinect.getRawDepth();
	int skip = 10;
	for (int x = 0; x < kinect.width; x += skip) {
		for (int y = 0; y < kinect.height; y += skip) {
			int index = x + y * kinect.width;
			int d = depth[index];
			PVector v = depthToWorld(x, y, d);
			
			calculateColor(d);
	    
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

void calculateColor(int depth){
	colorMode(HSB);
	float hue = map(rawDepthToMeters(depth), 0, 1000, 0, 360);
	fill(hue * 100, 255, 255);
}

// ================================================================

// Only needed to make sense of the ouput depth values from the kinect
PVector depthToWorld(int x, int y, int depthValue) {

  final double fx_d = 1.0 / 5.9421434211923247e+02;
  final double fy_d = 1.0 / 5.9104053696870778e+02;
  final double cx_d = 3.3930780975300314e+02;
  final double cy_d = 2.4273913761751615e+02;

	// Drawing the result vector to give each point its three-dimensional space
  PVector result = new PVector();
  double depth =  depthLookUp[depthValue];//rawDepthToMeters(depthValue);
  result.x = (float)((x - cx_d) * depth * fx_d);
  result.y = (float)((y - cy_d) * depth * fy_d);
  result.z = (float)(depth);
  return result;
}
