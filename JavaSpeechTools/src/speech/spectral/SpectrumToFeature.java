package speech.spectral;

import java.util.Arrays;

//
//@author JER
//
/*
 * Contains a number of functions for adjusting a spectrum of information passed to it.
 * 
 * More specifically!!! 
 * 
 * Maps the the RAW FFT magnitudes onto a log frequency scale vector of size nFeature.
 * 
 * No it doesn't it just compresses spectrum to the feature NN inputs size with a bit of averaging.
 * 
 *  Big TODO  here
 */

public class SpectrumToFeature {
	
	double[] smoothed;
	double[] magn_log;
	
	
	
	public SpectrumToFeature(int featureSize) {
		smoothed = new double[featureSize];
		magn_log = new double[featureSize];
	}	
	
	
	
	public double[] spectrumToFeature(int featureSize, int fftsize, double[] spectrum){
		linearLog(featureSize,fftsize,spectrum);
		running3Average(featureSize, magn_log);
		return smoothed;
	}
	
	private void linearLog(int featureSize, int fftsize, double[] spectrum) {

		Arrays.fill(magn_log,0.0);

		int triangular = 0;

		for (int i = 0; i < featureSize; i++) {
			triangular += i;
		}

		double factor = (double) fftsize / triangular / 2;
		int count = 0;
		int count2 = 0;
		while (count != featureSize) {
			for (int j = 0; j < Math.round(count * factor); j++) {
				magn_log[count] += spectrum[count2];
				count2++;
			}
			count++;
		}
		//return magn_log;

	}

	private void running3Average(int featureSize, double[] magnLog) {
		
		
		smoothed[0]=(magnLog[1] + magnLog[0]) / 2;
		smoothed[featureSize - 1]=(magnLog[featureSize - 1] + magnLog[featureSize - 2]) / 2;
		
		for (int i = 1; i < (featureSize - 1); i++) {
			smoothed[i] = (magnLog[i - 1] + magnLog[i] + magnLog[i + 1]) / 3;
		}

	}

}
