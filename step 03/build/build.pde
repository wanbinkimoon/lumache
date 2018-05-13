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

void settings(){ 
	size(stageW, stageH, P3D);
}

// ================================================================

void setup() {
	background(bgC);
	kinect = new Kinect(this);

	kinect.initDepth();
	// kinect.initDevice();

}

// ================================================================
void draw() {
	background(bgC);
	
	PImage img = kinect.getDepthImage();
	image(img, 0, 0);

	int skip = 10;
	for (int x = 0; x < img.width; x += skip) {
		for (int y = 0; y < img.height; y += skip) {
			int index = x + y * img.width;
			float b = brightness(img.pixels[index]);
			// float _r = map(b , 0, 255, skip, skip / 10);
			float _r = skip;
			float z = map(b, 0, 255, 400, -400);
			noStroke();
			fill(255 - b);
			pushMatrix();
				translate(x, y + 480, z);
				rect(0, 0, _r, _r);
			popMatrix();
			
		}
	}

}
