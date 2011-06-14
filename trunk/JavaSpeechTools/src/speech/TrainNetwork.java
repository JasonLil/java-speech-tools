package speech;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import config.Config;

import speech.spectral.ReadWav;
import speech.spectral.SpectrumToFeature;
import uk.ac.bath.ai.backprop.BackProp;

//
//@author JER
//
/*
 * Trains a neural network for a given set of audio data acquired from
 * a variety of sound sources
 */

public class TrainNetwork {
	
	public static NeuralNet neuralNet;
	public static SpectrumToFeature specAdjust;
	public static ReadWav readWav;
	
	public static float Fs = Config.sampleRate;
	public static int maxAudioLength = 1000;

	public static int inputs = 128;
	public static int hidden = 30;
	public static int outputs = 6;    // TODO
	
	public static int fftSize = 1024;
	public static int onscreenBins = 128;
	
	public static double alpha = 300000.0;
	public static double beta = .000001;
	
	public static double maxError = 0.01;
	
	private static int i_max;

	public static void main(String args[]) throws Exception {

		int sz[] = { inputs, hidden, outputs };

		Random rand=new Random();
		neuralNet = new BackProp(sz, beta, alpha, rand);
		specAdjust = new SpectrumToFeature(onscreenBins);
		readWav = new ReadWav(outputs);

		neuralNet.randomWeights(0.0, 0.01);

		double error = 1.0;
		
		double[] fftSpectrum = new double[fftSize];
		double[] featureVec = new double[onscreenBins];

		int count = 0;
		
		// -------- Train Network------------------ //

		// Read wavs from file
		double[][][] wavs = readWav.getMonoThongWavs(fftSize, outputs, Fs, maxAudioLength);
		
		while (error > maxError) {
			
			error = 0.0;
			
			i_max = readWav.file_length[0] - 1;

			// readWav.file_length[0] can be replaced with a number

			for (int i = 1; i < i_max; i++) { // Cycle through instances of FFT
				for (int p = 0; p < outputs+1; p++) { // Cycle through phonemes
					
					for (int j = 0; j < fftSize; j++) {
						fftSpectrum[j] = wavs[i][j][p];
					}
					
					// phonemeLog = specAdjust.linearLog(onscreenBins, fftSize, phonemeRaw); 
					featureVec = specAdjust.spectrumToFeature(onscreenBins, fftSize, fftSpectrum); //running3Average(onscreenBins, phonemeLog); 

					double[] train_outvals = new double[outputs+1];
					if (p != outputs)
						train_outvals[p] = 1.0;

					neuralNet.backPropTrain(featureVec, train_outvals); // Go!

					double[] output_vals = neuralNet
							.forwardPass(featureVec);

					for (int j = 0; j < outputs; j++) {
						error += (train_outvals[j] - output_vals[j])
								* (train_outvals[j] - output_vals[j]);
					}

				}
			}
			
			if (count % 10 == 0)
				System.out.println("Total Error: " + error);
			count++;
			
		}

		FileOutputStream istr = new FileOutputStream(
				"src/textfiles/network.txt");
		ObjectOutputStream out = new ObjectOutputStream(istr);
		out.writeObject(neuralNet);
		out.close();

		System.out.println("Whooop finished training! \n It took me "
				+ count + " back props");

	}

}