package speech.spectral;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import speech.NeuralNet;
import speech.gui.DrawScrollingSpect;


/**
 * Process the output of the FFT 
 * 
 * 
 * @author pjl
 *
 */
public class SpectralClient {
	
	NeuralNet neuralNet;
	SpectrumAdjust specAdj;
	SpectralAnalysisProcess sprectralAnalysis;
	DrawScrollingSpect scrollingSpect;
	public double outputs[];
	public double smoothed[];
	double magnLog[];
	int fftSize;
	int onscreenBins;
	
	public SpectralClient(int fftsize, int onscreenBins,DrawScrollingSpect scrollingSpect) {
		
		this.onscreenBins = onscreenBins;
		this.fftSize=fftsize;
		specAdj = new SpectrumAdjust();
		this.scrollingSpect=scrollingSpect;
		outputs = new double[6];
		
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
		
		magnLog = specAdj.linearLog(onscreenBins, fftSize, spectrum);
		
		smoothed = specAdj.running3Average(onscreenBins, magnLog);

		for (int i=0; i<smoothed.length; i++) {
			smoothed[i]*=2;									// This is adding volume to the input signal.
		}													// the USB audio interface isn't 'hot' enough
		
		if (scrollingSpect != null) scrollingSpect.notifyMoreDataReady(smoothed); // magnLog);
		outputs = neuralNet.forwardPass(smoothed);

	}
	
}
