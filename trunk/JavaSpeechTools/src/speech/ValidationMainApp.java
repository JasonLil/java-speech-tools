package speech;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import javax.swing.Timer;

import config.Config;

import speech.gui.MakeFrames;
import speech.gui.ReadImage;
import speech.spectral.NNSpectralFeatureDetector;
import speech.spectral.SpectrumToFeature;

public class ValidationMainApp {
	
//	public static int onscreenBins = 128;
//	public static int fftSize = 1024;
//	public static int phonemes = 6;   // TODO
//	public static int Fs = 44100;
	
	public static int onscreenBins = Config.featureSize;
	public static int fftSize = Config.fftSize;
	public static int phonemes = Config.phonemes;   // TODO
	public static float Fs = Config.sampleRate;
	public static int maxAudioLength = 1000;
	
	public static double spectrum[] = new double[fftSize];
	public static double outputSort[] = new double[phonemes];
	public static double magnLog[];
	public static double smoothed[];
	public static double outputs[];
	public static double vocalTract[][][];
	public static double lipsInner[][][];
	public static double lipsOuter[][][];
	
	static ValidationReadWav readTestWav;
	static NeuralNet neuralNet;
	static SpectrumToFeature specAdj;
	static MakeFrames frames;
	static ReadImage ri;
	static NNSpectralFeatureDetector client;
	static String phonemeNames[]={"EEE","EHH","ERR","AHH","OOH","UHH"};
	public static void main(String args[]) throws Exception {
	
		frames = new MakeFrames(false, phonemeNames, onscreenBins); 		// Create gfx for output
		

		frames.makeMaster();
		
		readTestWav = new ValidationReadWav(phonemes, 11);
		specAdj = new SpectrumToFeature(onscreenBins);
		double testWav[][] = readTestWav.getPatientWavs(fftSize, phonemes, Fs,
				maxAudioLength);
		
		FileInputStream ostr;
		try {
			ostr = new FileInputStream("src/textfiles/network.txt");
			ObjectInputStream in = new ObjectInputStream(ostr);
			neuralNet = (NeuralNet) in.readObject();
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		for (int k = 0; k < readTestWav.file_length_patient; k++) {
			for (int j = 0; j < fftSize; j++) {
				spectrum[j] = 2.0*testWav[k][j];
			}

			magnLog = specAdj.linearLog(onscreenBins, fftSize, spectrum);
			smoothed = specAdj.running3Average(onscreenBins, magnLog);
			outputs = neuralNet.forwardPass(smoothed);
			
			for (int l = 0; l < outputs.length; l++) {
				outputSort[l] = outputs[l];
			}
			Arrays.sort(outputSort);
			
			String text = "";
			if (outputSort[5] > 0.3) {
				if (outputSort[5] == outputs[0]) {text = "EEE";}
				if (outputSort[5] == outputs[1]) {text = "EHH";}
				if (outputSort[5] == outputs[2]) {text = "ERR";}
				if (outputSort[5] == outputs[3]) {text = "AHH";}
				if (outputSort[5] == outputs[4]) {text = "OOH";}
				if (outputSort[5] == outputs[5]) {text = "UHH";}
			}
			
			frames.updateGfx(text,  outputs);

			Thread.sleep(40);

		}
		}
	
}