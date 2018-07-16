import org.openkinect.freenect.*;
import org.openkinect.processing.*;

// ================================================================

Kinect kinect;

// ================================================================


float[] depthLookUp = new float[2048];

// ================================================================

void kinectSettings(boolean camera){
	kinect = new Kinect(this);

	kinect.initDepth();

  // Lookup table for all possible depth values (0 - 2047)
  for (int i = 0; i < depthLookUp.length; i++) {
    depthLookUp[i] = rawDepthToMeters(i);
  }
	
	// if(camera) kinect.initDevice();
}

// ================================================================

void kinectRender(boolean camera){
	renderPoints();
	if(camera) {
		PImage img = kinect.getDepthImage();
		image(img, 0, 0);
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

void renderPoints(){
	int[] depth = kinect.getRawDepth();
	int skip = 20;
	for (int x = 0; x < kinect.width; x += skip) {
		for (int y = 0; y < kinect.height; y += skip) {
			int index = x + y * kinect.width;
			int d = depth[index];
			PVector v = depthToWorld(x, y, d);
			objectRender(d, v, x, y, index, skip);
		}
	}
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
