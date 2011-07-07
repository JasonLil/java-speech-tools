package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import speech.spectral.JRSpectrumToFeature;
import speech.spectral.MelSpectrumToFeature;
import speech.spectral.SpectrumToFeature;

public class Config {

	static float sampleRate = 44100.0f;
	static int featureSize = 128;
	static int fftSize = 1024;
	static String phonemeNames[] = { "EEE", "EHH", "ERR", "AHH", "OOH", "UHH" };

	public static String preferredIn[] = { "U0x46d0x805", "default" };
	public static String preferredOut[] = { "NVidia [plughw:0,0]", "default" };

	public static File defaultWaveFile = new File(
			"/bunty/pjl/Dropbox/SpeechShare/SORTED/Anny/Cat.wav");
	private Properties prop;
	private SpectrumToFeature spectToFeat;

	// public Config(InputStream inStr) {
	// if (inStr == null) {
	// this.prop = null;
	// } else {
	//
	// prop = new Properties();
	// try {
	// prop.load(inStr);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// setProp(prop);
	// }

	private Config(Properties prop) {
		setProp(prop);
	}

	private void setProp(Properties prop) {
		this.prop = prop;
		String feat = prop.getProperty("spectrumTofeature");

		if (feat.equals("mel")) {
			spectToFeat = new MelSpectrumToFeature(featureSize, fftSize,
					200.0f, 10000.0f, sampleRate);
		} else if (feat.equals("jr")) {
			spectToFeat = new JRSpectrumToFeature(featureSize, fftSize);
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

	public static Config current() {
		return mel();
	}
}
