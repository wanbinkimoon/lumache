import peasy.*;

// ================================================================

PeasyCam cam;

// ================================================================

void camSettings(){
	cam = new PeasyCam(this, 1200 * -40 );
	// cam.rotateX(35);
	// cam.rotateY(45);
	cam.lookAt(-600, -400, 0, 1200);
}

// ================================================================

void camUpdate(){
	// cam.rotateX((float)knob[0] / 1000);
	// cam.rotateY((float)knob[1] / 1000);
	// cam.rotateZ((float)knob[2] / 1000);
	// cam.setDistance((float)map(knob[3], 0, 100, 100.0, -1200.0));
}