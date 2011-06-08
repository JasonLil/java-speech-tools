package speech;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.Timer;

import speech.gui.MakeFrames;
import speech.gui.ReadImage;
import speech.spectral.RealTimeSpectralSource;
import speech.spectral.SpectralAnalysisProcess;
import speech.spectral.SpectralClient;

public class MainApp {
	
	public int onscreenBins = 128;
	public int fftSize = 1024;
	public int phonemes = 6;
	
	public double spectrum[] = new double[fftSize];
	public double outputSort[] = new double[phonemes];
	public double magnLog[];
	public double smoothed[];
	public double outputs[];
//	public double vocalTract[][][];
//	public double lipsInner[][][];
//	public double lipsOuter[][][];
	
	MakeFrames frames;
	ReadImage ri;
	Timer timer;
	SpectralClient client;
	
	public boolean isApplet = false; 			// hack hack hack ... eeeek
	
	public static void main(String args[]) throws Exception {
		MainApp app = new MainApp(false);
		app.start();
	}
	
	String phonemeNames[]={"EEE","EHH","ERR","AHH","OOH","UHH"};
	

	MainApp(boolean isApplet) throws IOException {
		
		frames = new MakeFrames(isApplet, phonemeNames, onscreenBins); 		// Create gfx for output
		
	

		frames.makeMaster();
		
		timer = new Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				outputs = client.outputs;
				
				for (int i = 0; i < outputs.length; i++) {
					outputSort[i] = outputs[i];
				}
				Arrays.sort(outputSort);
				
				String text = "";
				int end=outputs.length-1;
				if (outputSort[end] > 0.3) {
					if (outputSort[end] == outputs[0]) {text = "EEE";}
					if (outputSort[end] == outputs[1]) {text = "EHH";}
					if (outputSort[end] == outputs[2]) {text = "ERR";}
					if (outputSort[end] == outputs[3]) {text = "AHH";}
					if (outputSort[end] == outputs[4]) {text = "OOH";}
					if (outputSort[end] == outputs[5]) {text = "UHH";}
				}
				
				frames.updateGfx( text,  outputs, client.smoothed);
			}
		});
		
	}
	
	void start() throws InterruptedException {
		
		SpectralAnalysisProcess spectralAnalysis = new SpectralAnalysisProcess(
				fftSize, (float) 44100.0);
		
		RealTimeSpectralSource rtSource = new RealTimeSpectralSource(
				spectralAnalysis);
		
		client = new SpectralClient(fftSize, onscreenBins,frames.spectralProcess);
		
		// Setup input from soundcard
		String inName = null;
		String outName = null;
		if (inName == null) {
			inName = "default [default]";
		}
		if (outName == null) {
			outName = "default [default]";
		}
		
		try {
			rtSource.startAudio(inName, outName, onscreenBins, client);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		timer.start();
	}
}