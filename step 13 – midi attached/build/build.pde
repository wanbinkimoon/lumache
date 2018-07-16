int stageW      = 1200;
int stageH      = 800;
color bgC       = #393E46;
String dataPATH = "../../data";


// ================================================================

void settings(){ 
	size(stageW, stageH);
	// fullScreen(P3D);
}

// ================================================================

void setup() {
	surface.setResizable(true);
	
	midiSetup();
	background(bgC);
	kinectSettings(false);
	// camSettings();
	audioSettings();
}

// ================================================================

void draw() {
	surface.setTitle("🐌 – Lumache – FPS: " + nf(frameRate, 0, 2));

	background(bgC);
	// lights();
	audioDataUpdate();
	updateAudio();
	kinectRender(false);
}

// ================================================================

void keyPressed(){	
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

void screenShot(){
	letsRender = true;
	if (letsRender) {
		letsRender = false;
		save(renderPATH + renderNum + ".png");
		renderNum++;
	}
}