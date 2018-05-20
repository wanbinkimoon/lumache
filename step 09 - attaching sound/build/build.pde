int stageW      = 1200;
int stageH      = 1200;
color bgC       = #3F3F3F;
String dataPATH = "../../data";


// ================================================================

void settings(){ 
	size(stageW, stageH, P3D);
}

// ================================================================

void setup() {
	background(bgC);
	kinectSettings(false);
	camSettings();
	audioSettings();
}

// ================================================================

void draw() {
	background(bgC);
	lights();

	audioDataUpdate();
	updateAudio();

	kinectRender(false);
	cam.rotateY(0.0025);
}

