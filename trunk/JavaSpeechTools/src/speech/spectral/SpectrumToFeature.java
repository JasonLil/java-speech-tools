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

public interface SpectrumToFeature {

	void spectrumToFeature(double[] spectrum, double[] feature);

	String getName();


}
