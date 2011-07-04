package config;


import java.io.File;

import speech.spectral.JRSpectrumToFeature;
import speech.spectral.SpectrumToFeature;

public class Config {

	static float sampleRate=44100.0f;
	static int featureSize = 128;
	static int fftSize = 1024;
	//static int phonemes = 6;
	static String phonemeNames[]={"EEE","EHH","ERR","AHH","OOH","UHH"};
	
	public static String preferredIn[]={"U0x46d0x805","default"};
	public static String preferredOut[]={"NVidia [plughw:0,0]","default"};
	
	public static File defaultWaveFile=new File("/bunty/pjl/Dropbox/SpeechShare/SORTED/Anny/Cat.wav");
	
	
	public  int getFFTSize() {
		return fftSize;
	}
	
	public  float getSampleRate() {	
		return sampleRate;
	}
	
	public  int getFeatureVectorSize() {		
		return featureSize;
	}

	public  int getNumberOfTargets() {
		return phonemeNames.length;
	}
	
	
	public SpectrumToFeature getSpectrumToFeature(){
		return new JRSpectrumToFeature(featureSize,fftSize);
	}

	public int getOutputSize() {	
		return phonemeNames.length;
	}

	public String[] getOutputNames() {	
		return phonemeNames;
	}

}
