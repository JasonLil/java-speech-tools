package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import speech.spectral.JRSpectrumToFeature;
import speech.spectral.MelSpectrumToFeature;
import speech.spectral.SpectrumToFeature;

public class Config {

	static float sampleRate = 44100.0f;
	static int featureSize = 128;
	static int fftSize = 1024;
	// static int phonemes = 6;
	static String phonemeNames[] = { "EEE", "EHH", "ERR", "AHH", "OOH", "UHH" };

	public static String preferredIn[] = { "U0x46d0x805", "default" };
	public static String preferredOut[] = { "NVidia [plughw:0,0]", "default" };

	public static File defaultWaveFile = new File(
			"/bunty/pjl/Dropbox/SpeechShare/SORTED/Anny/Cat.wav");
	private Properties prop;

	public Config(File file) {
		if (file == null) {
			this.prop = null;
		} else {

			prop = new Properties();
			try {
				prop.load(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

		if (prop != null) {
			String feat = prop.getProperty("spectrumTofeature");

			if (feat != "mel") {
				return new MelSpectrumToFeature(featureSize, fftSize, 200.0f,
						10000.0f, sampleRate);
			}
		}

		return new JRSpectrumToFeature(featureSize, fftSize);
	}

	public int getOutputSize() {
		return phonemeNames.length;
	}

	public String[] getOutputNames() {
		return phonemeNames;
	}

}
