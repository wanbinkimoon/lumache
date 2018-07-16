
float size;
float factor = 2000;

// ================================================================

// called in kinect lab
void objectRender(int d, PVector v, int x, int y, int index, int grid){
	calculateColor(d);
	calculateShape(v, x, y, index, grid);
}

// ================================================================

void calculateColor(int depth){
  color fillC = #F96D00;
  float selector = map(rawDepthToMeters(depth), 0, 2047, 0, 30);
  if(selector <= 10) fillC = #5C636E;
  if(selector <= 20) fillC = #FA872E;
  // if(v.z >= 3) // default color
	noStroke(); fill(fillC); 
}

void calculateShape(PVector v, int x, int y, int index, int grid){
  if(v.z < 1) size = map(audioData[2], 0, 100, 48, -8);
  if(v.z < 3) size = map(audioData[10], 0, 100, 2, 128);
	if(v.z >= 3) size = map(audioData[3], 0, 100, 8, 48);

  float elliX = v.x * factor;
  float elliY = v.y * factor;

  float posX = map(elliX, -342.85672, 283.62805, grid, width - grid); // theese are magic numbers
  float posY = map(elliY, -246.59514, 220.71214, grid, height - grid); // theese are magic numbers
  
  ellipse(posX, posY, size, size);
}