package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import speech.spectral.JRSpectrumToFeature;
import speech.spectral.MelSpectrumToFeature;
import speech.spectral.RawSpectrumToFeature;
import speech.spectral.SpectrumToFeature;

public class Config {

	static float sampleRate = 44100.0f;
	static int featureSize = 128;
	static int fftSize = 1024;
	static String phonemeNames[] = { "EEE", "EHH", "ERR", "AHH", "OOH", "UHH" };
	static String phonemeWords[] = { "Read", "red", "Great", "Father", "Blue", "Burn" };
	
	public static String preferredIn[] = { "U0x46d0x805", "default" };
	public static String preferredOut[] = { "NVidia [plughw:0,0]", "default" };

	public static File defaultWaveFile = new File(
			"/bunty/pjl/Dropbox/SpeechShare/SORTED/Anny/Cat.wav");
	private Properties prop;
	private SpectrumToFeature spectToFeat;
	private float lowFreq=50;
	private float highFreq=12000;

	

	private Config(Properties prop) {
		setProp(prop);
	}

	private void setProp(Properties prop) {
		this.prop = prop;
		String feat = prop.getProperty("spectrumTofeature");

		if (feat.equals("mel")) {
			featureSize=200;
			spectToFeat = new MelSpectrumToFeature(featureSize, fftSize,
					lowFreq, highFreq, sampleRate);
		} else if (feat.equals("jr")) {
			spectToFeat = new JRSpectrumToFeature(featureSize, fftSize);
		} else if (feat.equals("raw")) {
			featureSize=fftSize/2;
			spectToFeat = new RawSpectrumToFeature(featureSize);
		} else {
			assert (false);
		}

	}

	public int getFFTSize() {
		return fftSize;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public int getFeatureVectorSize() {
		return featureSize;
	}

	public int getNumberOfTargets() {
		return phonemeNames.length;
	}

	public SpectrumToFeature getSpectrumToFeature() {
		return spectToFeat;
	}

	public int getOutputSize() {
		return phonemeNames.length;
	}

	public String[] getOutputNames() {
		return phonemeNames;
	}

	public double getLowFreq() {
		return lowFreq;
	}

	
	public double getHighFreq() {
		return highFreq;
	}

	public String getNetName() {

		return spectToFeat.getName();
	}

	public static Config mel() {
		Properties prop = new Properties();
		prop.setProperty("spectrumTofeature", "mel");
		return new Config(prop);
	}
	
	
	public static Config jr() {
		Properties prop = new Properties();
		prop.setProperty("spectrumTofeature", "jr");
		return new Config(prop);
	}
	
	public static Config raw() {
		Properties prop = new Properties();
		prop.setProperty("spectrumTofeature", "raw");
		return new Config(prop);
	}

	public static Config current() {
		return jr();
	}
}
