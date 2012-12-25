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
//import speech.gui.DrawScrollingSpect;

/**
 * 
 * Process the output of the FFT using an NN which is loaded from file.
 * 
 * Takes raw spectral and produces a feature vector
 * 
 * Then feature is classifies using the NN
 * 
 * There are 2 clients to observe.    
 * 
 *   1. featureClient
 *   2. ouputClient
 * 
 * 
 * 
 * @author pjl
 * 
 */
public class NNSpectralFeatureDetector implements DataProcess  {

	private NeuralNet neuralNet;
	private DataProcess specAdj;
	private DataProcess spectralClient;
	private int featureSize;
	private DataProcess outClient;

	public NNSpectralFeatureDetector(int fftsize, int onscreenBins,
			DataProcess spectralClient, DataProcess fc, URL nnURL,
			Config config) throws IOException, ClassNotFoundException {

		this.featureSize = onscreenBins;
		
		specAdj = config.getSpectrumToFeature(); 
		this.spectralClient = spectralClient;
		this.outClient = fc;
	
		if (nnURL != null) {
			
			ObjectInputStream in = new ObjectInputStream(nnURL.openStream());
			neuralNet = (NeuralNet) in.readObject();
			in.close();
			assert(neuralNet.inputSize() == featureSize);
			
		} 
	}

	/**
	 * 
	 * Data must contain the raw FFT data.
	 * 
	 * This routine:
	 *   convert raw spectrum to a feature vector.
	 *   uses a neural net to classify features. 
	 * 
	 */
	@Override
	public void process(Data data) throws Exception {

		// magnLog = specAdj.linearLog(featureSize, Config.fftSize, spectrum);
		specAdj.process(data); // running3Average(featureSize,
																// magnLog);

		for (int i = 0; i < data.feature.length; i++) {
			data.feature[i] *= 2; // This is adding volume to the input signal.
		} // the USB audio interface isn't 'hot' enough

		if (spectralClient != null)
			spectralClient.process(data); // magnLog);

		if (neuralNet != null) {
			neuralNet.process(data);
			if (outClient != null)
				outClient.process(data);
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
