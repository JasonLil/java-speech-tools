package speech.dynamic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Arrays;

import javax.swing.Timer;

import speech.Data;
import speech.gui.MakeFrames;
import speech.spectral.DataProcess;
import speech.spectral.NNSpectralFeatureDetector;
import speech.spectral.RealTimeAudioSource;
import speech.spectral.SampledToSpectral;
import speech.spectral.TootRealTimeAudioSource;


import com.frinika.audio.io.AudioReader;
import com.frinika.audio.io.VanillaRandomAccessFile;

import config.Config;

public class MainDynamicApp  {

	private NNSpectralFeatureDetector nnFeatureDetector;

	double output[];
	public RealTimeAudioSource realTimeSpectralSource;
	public SampledToSpectral spectralConverter;
	private Config config;
	int fftSize;
	float sampleRate;
	int outSize;

	public static void main(String args[]) {
		MainDynamicApp app;
		try {
			app = new MainDynamicApp(false);
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

	MainDynamicApp(boolean isApplet) throws IOException {

		config = Config.current();
		output = new double[config.getOutputSize()];
		fftSize = config.getFFTSize();
		sampleRate = config.getSampleRate();
		outSize = config.getOutputSize();

	}

	void start() throws InterruptedException, IOException,
			ClassNotFoundException {

		/**
		 * Recieve a feature vector each FFT window. damp this to provide the
		 * user output
		 */

		DataProcess featureClient = new DataProcess() {

			String last="";
			
			double halfLife = .05; // in secs

			double nHalf = halfLife * sampleRate / fftSize;
			double damp = Math.exp(Math.log(0.5) / nHalf);
			{
				System.out.println(" damp= " + damp);
			}

			@Override
			public void process(Data data){
				
				double[] outputs=data.output;

				// System.out.println(" HELLO");
				for (int i = 0; i < outputs.length; i++) {
					MainDynamicApp.this.output[i] = MainDynamicApp.this.output[i]
							* damp + outputs[i] * (1.0 - damp);
				}
				String out = "";
				if (outputs[0] > 0.8) {
					out += "R ";
				} else if (outputs[1] > 0.8) {
					out += "S ";
				} else if (outputs[2] > 0.8) {
					out += "B";
				}
				if ( !last.equals(out)) {
					System.out.println(out);
				}
				last=out;
				
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

		};

		// takes the raw FFT from the spectral converter and feeds
		// the neural net classification

		URL url = null;

		String name = config.getNetName();

		String fullName = "src/textfiles/recwork.net";

		if (new File(fullName).exists()) {
			url = new File(fullName).toURI().toURL();
		} else {
			System.err.println(" Could not find NN " + fullName);
		}

		nnFeatureDetector = new NNSpectralFeatureDetector(fftSize,
				config.getFeatureVectorSize(), null, featureClient, url, config);

		// This is used to convert the audio stream to a spectral stream.
		spectralConverter = new SampledToSpectral(fftSize, 0, sampleRate,
				config.getFeatureVectorSize(),nnFeatureDetector);

		// Grabs input and feeds into the spectralConverter
		realTimeSpectralSource = new TootRealTimeAudioSource();

		
		try {
			// Start audio thread and connect nnFeatureDetector via the chunk
			// size converter
			realTimeSpectralSource.startAudio(spectralConverter);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	

}