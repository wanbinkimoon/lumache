
float size;
float factor = 2000;
float treshold = 0;
// ================================================================

// called in kinect lab
void objectRender(int d, PVector v, int x, int y, int index, int grid){
	treshold = map(knob[1], 0, 100, 0, .05);
  calculateColor(d);
	calculateShape(v, x, y, index, grid, d);
}

// ================================================================

void calculateColor(int depth){
  float selector = map(rawDepthToMeters(depth), 0, 2047, 0, 100);
  color fillC = #F96D00;

  if(selector < treshold + .004) fillC = #E86400;
  if(selector < treshold  + .003) fillC = #F27210;
  if(selector < treshold  + .002) fillC = #FA872E;
  if(selector < treshold  + .001) fillC = #FF9647;
  if(selector < treshold) fillC = #F2B07D;
  // if(v.z >= 3) // default color
	noStroke(); fill(fillC); 
}

void calculateShape(PVector v, int x, int y, int index, int grid, int depth){
  float selector = map(rawDepthToMeters(depth), 0, 2047, 0, 100);
	size = 0;
  if(selector < treshold) size = map(audioData[11], 0, 100, grid / 2, grid * knob[8]);
  if(selector < treshold + .5) size = map(audioData[5], 0, 100,  grid / 2, grid * knob[8]);
  if(selector >= treshold + 1.5) size = map(audioData[3], 0, 100,  grid / 2, grid * knob[9]);

  float elliX = v.x * factor;
  float elliY = v.y * factor;

  // float posX = map(elliX, -342.85672, 283.62805, grid, width - grid); // theese are magic numbers
  // float posY = map(elliY, -246.59514, 220.71214, grid, height - grid); // theese are magic numbers
  float posX = map(elliX, -500, 500, grid, width - grid); // theese are magic numbers
  float posY = map(elliY, -400, 400, grid, height - grid); // theese are magic numbers
  
  if(posX != width / 2) ellipse(posX, posY, size, size);
}