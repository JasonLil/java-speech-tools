package speech.spectral;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import config.Config;

import speech.NeuralNet;
import speech.gui.DrawScrollingSpect;

/**
 * Process the output of the FFT using an NN which is loaded from file
 * 
 * 
 * @author pjl
 * 
 */
public class NNSpectralFeatureDetector {

	private NeuralNet neuralNet;
	private SpectrumToFeature specAdj;
	private SampledToSpectral sprectralAnalysis;
	private SpectralProcess spectralClient;
//private double outputs[];
	private  double smoothed[];
	private double magnLog[];
	private int fftSize;
	private int featureSize;
	private FeatureClient featureClient;

	public NNSpectralFeatureDetector(int fftsize, int onscreenBins,
			SpectralProcess spectralClient,FeatureClient fc) {

		this.featureSize = onscreenBins;
		this.fftSize = fftsize;
		specAdj = new SpectrumToFeature(onscreenBins);
		this.spectralClient = spectralClient;
		this.featureClient=fc;
		//outputs = new double[6];

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

	}

	public void process(double[] spectrum) {

		// magnLog = specAdj.linearLog(featureSize, Config.fftSize, spectrum);
		smoothed = specAdj.spectrumToFeature(featureSize, Config.fftSize, spectrum); // running3Average(featureSize, magnLog);

		for (int i = 0; i < smoothed.length; i++) {
			smoothed[i] *= 2; // This is adding volume to the input signal.
		} // the USB audio interface isn't 'hot' enough

		if (spectralClient != null)
			spectralClient.notifyMoreDataReady(smoothed); // magnLog);
		
		double output[] = neuralNet.forwardPass(smoothed);
		if (featureClient != null)  featureClient.notifyMoreDataReady(output);
	}

	/** 
	 * 
	 *  grab the latest neural net outputs
	 * 
	 * @return
	 */
//	public double[] getOutputs() {
//		// TODO synchronize and copy to avoid concurrent modifications
//		
//		return outputs;
//	}
//
//	public double[] getFFTLogMagnitude() {
//		// TODO Auto-generated method stub
//		return smoothed;
//	}

}
