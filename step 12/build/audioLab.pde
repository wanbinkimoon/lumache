
import ddf.minim.*;
import ddf.minim.analysis.*;

// ================================================================

Minim minim;
AudioInput audio;
FFT audioFFT;

// ================================================================

int audioRange  = 12;
int audioMax = 100;

float audioAmp = 91.0;
float audioIndex = 0.005;
float audioIndexAmp = audioIndex;
float audioIndexStep = 0.309;

float[] audioData = new float[audioRange];

// ================================================================

void audioSettings(){
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

void audioDataUpdate(){
  audioFFT.forward(audio.mix);
  updateAudio();
}

// ================================================================

  void updateAudio(){
    for (int i = 0; i < audioRange; ++i) {
      float indexAvg = (audioFFT.getAvg(i) * audioAmp) * audioIndexAmp;
      float indexCon = constrain(indexAvg, 0, audioMax);
      audioData[i] = indexCon;
      audioIndexAmp += audioIndexStep;
    }

    audioIndexAmp = audioIndex;
  }

  // ================================================================
  
  void audioMidiValueUpdate(){
    // audioAmp = map(knob[5], 0, 100, 10, 60);
    // audioIndex = map(knob[6], 0, 100, 50, 100);
    // audioIndexStep = map(knob[7], 0, 100, 2.5, 100);
  }

