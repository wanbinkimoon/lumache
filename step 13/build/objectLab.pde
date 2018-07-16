
float size;
float factor = 2000;

// ================================================================

// called in kinect lab
void objectRender(int d, PVector v, int x, int y, int index, int grid){
	calculateColor(d);
	calculateShape(v, x, y, index, grid, d);
}

// ================================================================

void calculateColor(int depth){
  float selector = map(rawDepthToMeters(depth), 0, 2047, 0, 100);
  color fillC = #F96D00;
  if(selector < 2) fillC = #5C636E;
  if(selector < .05) fillC = #F96D00;
  if(selector < .035) fillC = #FA872E;
  // if(v.z >= 3) // default color
	noStroke(); fill(fillC); 
}

void calculateShape(PVector v, int x, int y, int index, int grid, int depth){
  float padding = grid / 20;
  float selector = map(rawDepthToMeters(depth), 0, 2047, 0, 100);

	size = 0;
  if(selector <  1) size = map(audioData[10], 0, 100, grid / 2, grid - padding);
  if(selector < .5) size = map(audioData[2], 0, 100,  grid / 2, grid - padding);
  if(selector >= .5) size = map(audioData[3], 0, 100,  grid / 2, grid - padding);

  float elliX = v.x * factor;
  float elliY = v.y * factor;

  // float posX = map(elliX, -342.85672, 283.62805, grid, width - grid); // theese are magic numbers
  // float posY = map(elliY, -246.59514, 220.71214, grid, height - grid); // theese are magic numbers
  float posX = map(elliX, -500, 500, grid, width - grid); // theese are magic numbers
  float posY = map(elliY, -400, 400, grid, height - grid); // theese are magic numbers
  
  if(posX != width / 2) ellipse(posX, posY, size, size);
}