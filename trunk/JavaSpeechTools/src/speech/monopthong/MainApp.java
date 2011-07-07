package speech.monopthong;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.Timer;

import com.frinika.audio.io.AudioReader;
import com.frinika.audio.io.VanillaRandomAccessFile;

import config.Config;

import speech.gui.MakeFrames;
import speech.spectral.FeatureClient;
import speech.spectral.RealTimeSpectralSource;
import speech.spectral.SampledToSpectral;
import speech.spectral.NNSpectralFeatureDetector;
import speech.spectral.SpectralClient;

public class MainApp implements SpectralClient {



	private MakeFrames frames;
	private Timer timer;
	private NNSpectralFeatureDetector nnFeatureDetector;
	public boolean isApplet = false; // hack hack hack ... eeeek

	double output[];
	public RealTimeSpectralSource realTimeSpectralSource;
	public SampledToSpectral spectralConverter;
	private Config config;
	int fftSize;
	float sampleRate;
	int outSize;
	
	public static void main(String args[]) {
		MainApp app;
		try {
			app = new MainApp(false);
			app.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);

		}
	
	}

	MainApp(boolean isApplet) throws IOException {
		
		config=Config.current();
		output=new double[config.getOutputSize()];
		fftSize=config.getFFTSize();
		sampleRate=config.getSampleRate();
		outSize=config.getOutputSize();
		
		frames = new MakeFrames(isApplet, config,this); // Create gfx for output

		frames.makeMaster();
		timer = new Timer(50, new ActionListener() {
	
			double outputSort[] = new double[outSize];
			
			public void actionPerformed(ActionEvent ae) {
	
				for (int i = 0; i < output.length; i++) {
					outputSort[i] = output[i];
				}
				Arrays.sort(outputSort);

				String text = "";
				int end = output.length - 1;
				if (outputSort[end] > 0.3) {
					for (int i = 0; i < outSize; i++) {
						if (outputSort[end] == output[i]) {
							text = config.getOutputNames()[i];
							break;
						}
					}
				}

				frames.updateGfx(text, output);
			}
		});

	}

	
	
	void start() throws InterruptedException, IOException, ClassNotFoundException {


		/**
		 *  Recieve a feature vector each FFT window.
		 *  damp this to provide the user output
		 */
		
		FeatureClient featureClient=new FeatureClient(){

			double halfLife=.05;   // in secs
			
			double nHalf=halfLife*sampleRate/fftSize;
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
				if (frames == null || frames.drawGraph == null) return;
				
				frames.drawGraph.updateGraph(outputs, "");
			}
			
		};

		// This is used to convert the audio stream to a spectral stream.
		spectralConverter = new SampledToSpectral(
				fftSize,0, sampleRate,config.getFeatureVectorSize());

		// Grabs input and feeds into the spectralConverter
		realTimeSpectralSource = new RealTimeSpectralSource(
				spectralConverter,this);

		// takes the raw FFT from the spectral converter and feeds
		// the neural net classification
		
		URL url=null;
		

			String name=config.getNetName();
			
			String fullName="src/textfiles/"+name+".net";

			url = new File(fullName).toURI().toURL();
		
		
		nnFeatureDetector = new NNSpectralFeatureDetector(fftSize,
				config.getFeatureVectorSize(), frames.getSpectralProcess(),featureClient,url,config);

		// Setup input from soundcard

		

		try {
			// Start audio thread and connect nnFeatureDetector via the chunk size converter
			realTimeSpectralSource.startAudio( nnFeatureDetector);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		timer.start();
	}
	
	public void setInputWave(File waveFile){
		
		if (waveFile == null) {
			realTimeSpectralSource.streamFile(null);
			frames.pauseGraphs(false);
			frames.resetGraphs();
			return;
		}
		
		try {
			RandomAccessFile rafG = new RandomAccessFile(waveFile, "r");
			AudioReader audioReader = new AudioReader(new VanillaRandomAccessFile(
					rafG),sampleRate);	
			realTimeSpectralSource.streamFile(audioReader);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		frames.pauseGraphs(false);
		frames.resetGraphs();
	}

	public void eof(boolean b) {
		frames.pauseGraphs(b);
	}
	
}