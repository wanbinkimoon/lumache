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
int stageH      = 800;
int bgC       = 0xff393E46;
String dataPATH = "../../data";


// ================================================================

public void settings(){ 
	// size(stageW, stageH);
	fullScreen();
}

// ================================================================

public void setup() {
	surface.setResizable(true);
	
	midiSetup();
	background(bgC);
	kinectSettings(false);
	// camSettings();
	audioSettings();
}

// ================================================================

public void draw() {
	surface.setTitle("\ud83d\udc0c \u2013 Lumache \u2013 FPS: " + nf(frameRate, 0, 2));

	background(bgC);
	// lights();
	audioDataUpdate();
	updateAudio();
	kinectRender(false);
}

// ================================================================

public void keyPressed(){	
	switch (key) {
		case 'q':
			exit();
			break;
		case 'p':
			screenShot();
			break;
	}
}

// ================================================================

boolean letsRender = false;
int     renderNum  = 0;
String  renderPATH = "../render/";

// ================================================================

public void screenShot(){
	letsRender = true;
	if (letsRender) {
		letsRender = false;
		save(renderPATH + renderNum + ".png");
		renderNum++;
	}
}




// ================================================================

Minim minim;
AudioInput audio;
FFT audioFFT;

// ================================================================

int audioRange  = 12;
int audioMax = 100;

float audioAmp = 523.0f;
float audioIndex = 0.037f;
float audioIndexAmp = audioIndex;
float audioIndexStep = 0.376f;

float[] audioData = new float[audioRange];

// ================================================================

public void audioSettings(){
	minim = new Minim(this);
  audio = minim.getLineIn(Minim.STEREO);

	audioFFT = new FFT(audio.bufferSize(), audio.sampleRate());
	audioFFT.linAverages(audioRange);

  // audioFFT.window(FFT.NONE);
  // audioFFT.window(FFT.BARTLETT);
  audioFFT.window(FFT.BARTLETTHANN);
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
	// cam.rotateX(35);
	// cam.rotateY(45);
	cam.lookAt(-600, -400, 0, 1200);
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
	int[] depth = kinect.getRawDepth();
	int skip = (int)map(knob[0], 0, 100, 2, 30);
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

	// midiMonitor();
}

public void midiMonitor(){
	knobTable = "\n\n_________________________________________________________________________________________________________________________________\n|  001  |  002  |  003  |  004  |  005  |  006  |  007  |  008  |  009  |  010  |  011  |  012  |  013  |  014  |  015  |  016  |\n|  "+ String.format("%03d", knob[0]) +"  |  "+ String.format("%03d", knob[1]) +"  |  "+ String.format("%03d", knob[2]) +"  |  "+ String.format("%03d", knob[3]) +"  |  "+ String.format("%03d", knob[4]) +"  |  "+ String.format("%03d", knob[5]) +"  |  "+ String.format("%03d", knob[6]) +"  |  "+ String.format("%03d", knob[7]) +"  |  "+ String.format("%03d", knob[8]) +"  |  "+ String.format("%03d", knob[9]) +"  |  "+ String.format("%03d", knob[10]) +"  |  "+ String.format("%03d", knob[11]) +"  |  "+ String.format("%03d", knob[12]) +"  |  "+ String.format("%03d", knob[13]) +"  |  "+ String.format("%03d", knob[14]) +"  |  "+ String.format("%03d", knob[15]) +"  |\n_________________________________________________________________________________________________________________________________";
	println(knobTable);
}

float size;
float factor = 2000;
float treshold = 0;
// ================================================================

// called in kinect lab
public void objectRender(int d, PVector v, int x, int y, int index, int grid){
	treshold = map(knob[1], 0, 100, 0, .05f);
  calculateColor(d);
	calculateShape(v, x, y, index, grid, d);
}

// ================================================================

public void calculateColor(int depth){
  float selector = map(rawDepthToMeters(depth), 0, 2047, 0, 100);
  int fillC = 0xffF96D00;

  if(selector < treshold + .004f) fillC = 0xffE86400;
  if(selector < treshold  + .003f) fillC = 0xffF27210;
  if(selector < treshold  + .002f) fillC = 0xffFA872E;
  if(selector < treshold  + .001f) fillC = 0xffFF9647;
  if(selector < treshold) fillC = 0xffF2B07D;
  // if(v.z >= 3) // default color
	noStroke(); fill(fillC); 
}

public void calculateShape(PVector v, int x, int y, int index, int grid, int depth){
  float selector = map(rawDepthToMeters(depth), 0, 2047, 0, 100);
	size = 0;
  if(selector < treshold) size = map(audioData[11], 0, 100, grid / 2, grid * knob[8]);
  if(selector < treshold + .5f) size = map(audioData[5], 0, 100,  grid / 2, grid * knob[8]);
  if(selector >= treshold + 1.5f) size = map(audioData[3], 0, 100,  grid / 2, grid * knob[9]);

  float elliX = v.x * factor;
  float elliY = v.y * factor;

  // float posX = map(elliX, -342.85672, 283.62805, grid, width - grid); // theese are magic numbers
  // float posY = map(elliY, -246.59514, 220.71214, grid, height - grid); // theese are magic numbers
  float posX = map(elliX, -500, 500, grid, width - grid); // theese are magic numbers
  float posY = map(elliY, -400, 400, grid, height - grid); // theese are magic numbers
  
  if(posX != width / 2) ellipse(posX, posY, size, size);
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
