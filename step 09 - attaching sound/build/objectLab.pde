
float size;

// ================================================================

void objectRender(int d, PVector v, int x, int y){
	calculateColor(d);
	calculateShape(v, x, y);
}

// ================================================================

void calculateColor(int depth){
	colorMode(HSB);
	float hue = map(rawDepthToMeters(depth), 0, 2047, 0, 255);
	stroke(hue * 100, 255, 255);
	noFill();
}

void	calculateShape(PVector v, int x, int y){
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