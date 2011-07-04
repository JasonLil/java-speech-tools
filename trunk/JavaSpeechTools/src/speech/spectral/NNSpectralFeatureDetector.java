package speech.spectral;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;

import config.Config;

import speech.Data;
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
//	private SampledToSpectral sprectralAnalysis;
	private SpectralProcess spectralClient;
//private double outputs[];
//	private  double smoothed[];
//	private double magnLog[];
//	private int fftSize;
	private int featureSize;
	private FeatureClient featureClient;

	public NNSpectralFeatureDetector(int fftsize, int onscreenBins,
			SpectralProcess spectralClient,FeatureClient fc,URL nnURL,Config config) {

		this.featureSize = onscreenBins;
	//	this.fftSize = fftsize;
		specAdj = config.getSpectrumToFeature(); // new SpectrumToFeature(onscreenBins,fftsize);
		this.spectralClient = spectralClient;
		this.featureClient=fc;
		//outputs = new double[6];

		//FileInputStream ostr;
		
		
		try {
			//ostr = new FileInputStream("src/textfiles/network.txt");
			//ostr = new FileInputStream(nnURL);
			ObjectInputStream in = new ObjectInputStream(nnURL.openStream());
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

	public void process(Data data) throws Exception {

		// magnLog = specAdj.linearLog(featureSize, Config.fftSize, spectrum);
		specAdj.spectrumToFeature(data.spectrum,data.feature); // running3Average(featureSize, magnLog);

		for (int i = 0; i < data.feature.length; i++) {
			data.feature[i] *= 2; // This is adding volume to the input signal.
		} // the USB audio interface isn't 'hot' enough

		if (spectralClient != null)
			spectralClient.notifyMoreDataReady(data); // magnLog);
		
		 neuralNet.process(data);
		 
		if (featureClient != null)  featureClient.notifyMoreDataReady(data.output);
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
