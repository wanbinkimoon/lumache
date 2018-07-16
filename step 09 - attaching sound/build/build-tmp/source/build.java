import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 
import peasy.*; 
import org.openkinect.freenect.*; 
import org.openkinect.processing.*; 
import themidibus.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class build extends PApplet {

int stageW      = 1200;
int stageH      = 1200;
int bgC       = 0xff3F3F3F;
String dataPATH = "../../data";


// ================================================================

public void settings(){ 
	size(stageW, stageH, P3D);
}

// ================================================================

public void setup() {
	background(bgC);
	kinectSettings(false);
	camSettings();
	audioSettings();
}

// ================================================================

public void draw() {
	background(bgC);
	lights();

	audioDataUpdate();
	updateAudio();

	kinectRender(false);
	cam.rotateY(0.0025f);
}





// ================================================================

Minim minim;
AudioInput audio;
FFT audioFFT;

// ================================================================

int audioRange  = 12;
int audioMax = 100;

float audioAmp = 252.0f;
float audioIndex = 0.005f;
float audioIndexAmp = audioIndex;
float audioIndexStep = 0.449f;

float[] audioData = new float[audioRange];

// ================================================================

public void audioSettings(){
	minim = new Minim(this);
  audio = minim.getLineIn(Minim.STEREO);

	audioFFT = new FFT(audio.bufferSize(), audio.sampleRate());
	audioFFT.linAverages(audioRange);

  audioFFT.window(FFT.NONE);
  // audioFFT.window(FFT.BARTLETT);
  // audioFFT.window(FFT.BARTLETTHANN);
  // audioFFT.window(FFT.BLACKMAN);
  // audioFFT.window(FFT.COSINE);
  // audioFFT.window(FFT.GAUSS);
  // audioFFT.window(FFT.HAMMING);
  // audioFFT.window(FFT.HANN);
  // audioFFT.window(FFT.LANCZOS);
  // audioFFT.window(FFT.TRIANGULAR);
}

// ================================================================

public void audioDataUpdate(){
  audioFFT.forward(audio.mix);
  updateAudio();
}

// ================================================================

  public void updateAudio(){
    for (int i = 0; i < audioRange; ++i) {
      float indexAvg = (audioFFT.getAvg(i) * audioAmp) * audioIndexAmp;
      float indexCon = constrain(indexAvg, 0, audioMax);
      audioData[i] = indexCon;
      audioIndexAmp += audioIndexStep;
    }

    audioIndexAmp = audioIndex;
  }

  // ================================================================
  
  public void audioMidiValueUpdate(){
    // audioAmp = map(knob[5], 0, 100, 10, 60);
    // audioIndex = map(knob[6], 0, 100, 50, 100);
    // audioIndexStep = map(knob[7], 0, 100, 2.5, 100);
  }



// ================================================================

PeasyCam cam;

// ================================================================

public void camSettings(){
	cam = new PeasyCam(this, 1200 * -40 );
	cam.rotateX(35);
	cam.rotateY(45);
}

// ================================================================

public void camUpdate(){
	// cam.rotateX((float)knob[0] / 1000);
	// cam.rotateY((float)knob[1] / 1000);
	// cam.rotateZ((float)knob[2] / 1000);
	// cam.setDistance((float)map(knob[3], 0, 100, 100.0, -1200.0));
}



// ================================================================

Kinect kinect;

// ================================================================


float[] depthLookUp = new float[2048];

// ================================================================

public void kinectSettings(boolean camera){
	kinect = new Kinect(this);

	kinect.initDepth();

  // Lookup table for all possible depth values (0 - 2047)
  for (int i = 0; i < depthLookUp.length; i++) {
    depthLookUp[i] = rawDepthToMeters(i);
  }
	
	// if(camera) kinect.initDevice();
}

// ================================================================

public void kinectRender(boolean camera){
	renderPoints();
	if(camera) {
		PImage img = kinect.getDepthImage();
		image(img, 0, 0);
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

public void renderPoints(){
  translate(-(width / 2), - (height / 2));
	int[] depth = kinect.getRawDepth();
	int skip = 10;
	for (int x = 0; x < kinect.width; x += skip) {
		for (int y = 0; y < kinect.height; y += skip) {
			int index = x + y * kinect.width;
			int d = depth[index];

			PVector v = depthToWorld(x, y, d);
			
			objectRender(d, v, x, y);

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
 

// ================================================================

MidiBus myBus; 

// ================================================================

public void controllerChange(int channel, int number, int value) {  

	midiUpdate(channel, number, value);

  // Receive a controllerChange
  // println();
  // println("Controller Change:");
  // println("--------");
  // println("Channel:" + channel);
  // println("Number:" + number);
  // println("Value:" + value);
}

// ================================================================

public void noteOn(int channel, int pitch, int velocity) {
    println(channel, pitch, velocity);
}

int knobNumb = 16;
int[] knob = new int[knobNumb];
String knobTable;

// ================================================================

public void midiSetup(){
  MidiBus.list(); 
  myBus = new MidiBus(this, 0, 1);
}

public void midiUpdate(int channel, int number, int value){
	if(number == 21) knob[0] = (int)map(value, 0, 127, 0, 100);
	if(number == 22) knob[1] = (int)map(value, 0, 127, 0, 100);
	if(number == 23) knob[2] = (int)map(value, 0, 127, 0, 100);
	if(number == 24) knob[3] = (int)map(value, 0, 127, 0, 100);
	if(number == 25) knob[4] = (int)map(value, 0, 127, 0, 100);
	if(number == 26) knob[5] = (int)map(value, 0, 127, 0, 100);
	if(number == 27) knob[6] = (int)map(value, 0, 127, 0, 100);
	if(number == 28) knob[7] = (int)map(value, 0, 127, 0, 100);
	if(number == 41) knob[8] = (int)map(value, 0, 127, 0, 100);
	if(number == 42) knob[9] = (int)map(value, 0, 127, 0, 100);
	if(number == 43) knob[10] = (int)map(value, 0, 127, 0, 100);
	if(number == 44) knob[11] = (int)map(value, 0, 127, 0, 100);
	if(number == 45) knob[12] = (int)map(value, 0, 127, 0, 100);
	if(number == 46) knob[13] = (int)map(value, 0, 127, 0, 100);
	if(number == 47) knob[14] = (int)map(value, 0, 127, 0, 100);
	if(number == 48) knob[15] = (int)map(value, 0, 127, 0, 100);
}

public void midiMonitor(){
	knobTable = "\n\n_________________________________________________________________________________________________________________________________\n|  001  |  002  |  003  |  004  |  005  |  006  |  007  |  008  |  009  |  010  |  011  |  012  |  013  |  014  |  015  |  016  |\n|  "+ String.format("%03d", knob[0]) +"  |  "+ String.format("%03d", knob[1]) +"  |  "+ String.format("%03d", knob[2]) +"  |  "+ String.format("%03d", knob[3]) +"  |  "+ String.format("%03d", knob[4]) +"  |  "+ String.format("%03d", knob[5]) +"  |  "+ String.format("%03d", knob[6]) +"  |  "+ String.format("%03d", knob[7]) +"  |  "+ String.format("%03d", knob[8]) +"  |  "+ String.format("%03d", knob[9]) +"  |  "+ String.format("%03d", knob[10]) +"  |  "+ String.format("%03d", knob[11]) +"  |  "+ String.format("%03d", knob[12]) +"  |  "+ String.format("%03d", knob[13]) +"  |  "+ String.format("%03d", knob[14]) +"  |  "+ String.format("%03d", knob[15]) +"  |\n_________________________________________________________________________________________________________________________________";
	println(knobTable);
}

float size;

// ================================================================

public void objectRender(int d, PVector v, int x, int y){
	calculateColor(d);
	calculateShape(v, x, y);
}

// ================================================================

public void calculateColor(int depth){
	colorMode(HSB);
	float hue = map(rawDepthToMeters(depth), 0, 2047, 0, 255);
	stroke(hue * 100, 255, 255);
	noFill();
}

public void	calculateShape(PVector v, int x, int y){
  pushMatrix();
    // Scale up by 200
    float factor = 200;

    if(v.z < 1) size = map(audioData[2], 0, 100, 8, 48);
    if(v.z < 2) size = map(audioData[3], 0, 100, 8, 48);
		if(v.z >= 2) size = map(audioData[10], 0, 100, 8, 48);



    translate(v.x * factor, v.y * factor , factor - v.z * factor);
    translate(x, y);

    box(size, size, size);
  popMatrix();
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
