int stageW      = 640;
int stageH      = 480 * 2;
color bgC       = #FF7700;
String dataPATH = "../../data";

// ================================================================

import org.openkinect.freenect.*;
import org.openkinect.processing.*;

// ================================================================

Kinect kinect;

// ================================================================

void settings(){ 
	size(stageW, stageH);
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
	println(img.width);
	println(img.height);

	int skip = 20;
	for (int x = 0; x < img.width; x += skip) {
		for (int y = 0; y < img.height; y += skip) {
			int index = x + y * img.width;
			float b = brightness(img.pixels[index]);

			noStroke();
			fill(b);
			ellipse(x + (skip / 2), y + 480 + (skip / 2), skip / 2, skip / 2);
		}
	}

}
