package speech;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import javax.swing.Timer;

import com.frinika.audio.io.AudioReader;
import com.frinika.audio.io.VanillaRandomAccessFile;

import config.Config;

import speech.gui.MakeFrames;
import speech.spectral.FeatureClient;
import speech.spectral.RealTimeSpectralSource;
import speech.spectral.SampledToSpectral;
import speech.spectral.NNSpectralFeatureDetector;

public class MainApp {



	private MakeFrames frames;
	private Timer timer;
	private NNSpectralFeatureDetector nnFeatureDetector;
	public boolean isApplet = false; // hack hack hack ... eeeek

	double output[]=new double[Config.phonemes];
	public RealTimeSpectralSource realTimeSpectralSource;
	public SampledToSpectral spectralConverter;
	
	public static void main(String args[]) throws Exception {
		MainApp app = new MainApp(false);
		app.start();
	}

	MainApp(boolean isApplet) throws IOException {

		frames = new MakeFrames(isApplet, Config.phonemeNames,
				Config.featureSize,this); // Create gfx for output

		frames.makeMaster();
		timer = new Timer(50, new ActionListener() {
	
			double outputSort[] = new double[Config.phonemes];
			
			public void actionPerformed(ActionEvent ae) {

			
				
				for (int i = 0; i < output.length; i++) {
					outputSort[i] = output[i];
				}
				Arrays.sort(outputSort);

				String text = "";
				int end = output.length - 1;
				if (outputSort[end] > 0.3) {
					for (int i = 0; i < Config.phonemes; i++) {
						if (outputSort[end] == output[i]) {
							text = Config.phonemeNames[i];
							break;
						}
					}
				}

				frames.updateGfx(text, output);
			}
		});

	}

	
	
	void start() throws InterruptedException {


		/**
		 *  Recieve a feature vector each FFT window.
		 *  damp this to provide the user output
		 */
		
		FeatureClient featureClient=new FeatureClient(){

			double halfLife=.05;   // in secs
			double nHalf=halfLife*Config.sampleRate/Config.fftSize;
			double damp=Math.exp(Math.log(0.5)/nHalf);
			{
				System.out.println(" damp= "+damp);
			}
			@Override
			public void notifyMoreDataReady(double[] outputs) {
	
				for (int i=0;i<outputs.length;i++){
					MainApp.this.output[i] = MainApp.this.output[i]*damp +
					outputs[i]*(1.0-damp);
				}
			}
			
		};

		// This is used to convert the audio stream to a spectral stream.
		spectralConverter = new SampledToSpectral(
				Config.fftSize,0, Config.sampleRate);

		// Grabs input and feeds into the spectralConverter
		realTimeSpectralSource = new RealTimeSpectralSource(
				spectralConverter);

		// takes the raw FFT from the spectral converter and feeds
		// the neural net classification
		nnFeatureDetector = new NNSpectralFeatureDetector(Config.fftSize,
				Config.featureSize, frames.getSpectralProcess(),featureClient);

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
			// Start audio thread and connect nnFeatureDetector via the chunk size converter
			realTimeSpectralSource.startAudio(inName, outName,
					Config.featureSize, nnFeatureDetector);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		timer.start();
	}
	
	public void setInputWave(File waveFile){
		
		if (waveFile == null) {
			realTimeSpectralSource.streamFile(null);
			return;
		}
		RandomAccessFile rafG;
		try {
			rafG = new RandomAccessFile(waveFile, "r");
			AudioReader audioReader = new AudioReader(new VanillaRandomAccessFile(
					rafG),Config.sampleRate);	
			realTimeSpectralSource.streamFile(audioReader);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}