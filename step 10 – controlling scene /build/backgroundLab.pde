void backgroundSettings(){

}

// ================================================================

void backgroundUpdate(){
	pushMatrix();
		translate(0, 0, -1200);		

		for (int j = 0; j < (height * 2); ++j) {
			beginShape();
				for (int i = 0; i < (audioRange - 1); ++i) {
					float step = i * ((width * 4) / audioRange);
					colorMode(HSB);
					stroke(j, 100, 100);
					noFill();
					strokeWeight(2);
					vertex(step - (width * 2), (audioData[i] * 1000) - (j * 20), (audioData[i] * 100));
				}
			endShape();
		}

 	popMatrix();
	
}