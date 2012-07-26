package stream;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.Timer;

import speech.Data;
import speech.gui.AppBase;

import speech.spectral.DataProcess;
import speech.spectral.NNSpectralFeatureDetector;
import speech.spectral.RealTimeAudioSource;
import speech.spectral.TootRealTimeAudioSource;

import speech.spectral.SampledToSpectral;

import com.frinika.audio.io.AudioReader;
import com.frinika.audio.io.VanillaRandomAccessFile;

import config.Config;

public class MainApp implements AppBase {

	private MakeFrames frames;
	private Timer timer;
	private NNSpectralFeatureDetector nnFeatureDetector;
	public boolean isApplet = false; // hack hack hack ... eeeek

	double output[];
	public RealTimeAudioSource realTimeSpectralSource;
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

		config = Config.byName("raw");
		output = new double[config.getOutputSize()];
		fftSize = config.getFFTSize();
		sampleRate = config.getSampleRate();
		outSize = config.getOutputSize();

		frames = new MakeFrames(isApplet, config, this); // Create gfx for

		// output
		// int w= frames.windowSize.width;
		// int h=frames.windowSize.height;

		JFrame fs = frames.makeSpectrogramFrame2();
		fs.setVisible(true);

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

				if (realTimeSpectralSource.isEOF()) {

					frames.pauseGraphs(true);

				} else {
					frames.pauseGraphs(false);

				}
				frames.updateGfx(text, output);
			}
		});

	}

	void start() throws InterruptedException, IOException,
			ClassNotFoundException {

		/**
		 * Recieve a feature vector each FFT window. damp this to provide the
		 * user output
		 */

		// DataProcess outputClient = new DataProcess() {
		//
		// double halfLife = .05; // in secs
		//
		// double nHalf = halfLife * sampleRate / fftSize;
		// double damp = Math.exp(Math.log(0.5) / nHalf);
		// {
		// System.out.println(" damp= " + damp);
		// }
		//
		// @Override
		// public void process(Data data){
		// double[] outputs=data.output;
		//
		//
		// for (int i = 0; i < outputs.length; i++) {
		// MainApp.this.output[i] = MainApp.this.output[i] * damp
		// + outputs[i] * (1.0 - damp);
		// }
		// // if (frames == null || frames.drawGraph == null)
		// // return;
		// //
		// // frames.drawGraph.updateGraph(outputs, "");
		// }
		//
		// };
		//
		// URL url = null;
		//
		// String name = config.getNetName();
		//
		// String fullName = "/textfiles/" + name + ".net";
		//
		// URL url1 = MainApp.class.getResource(fullName);
		//
		//
		// if (new File(fullName).exists()) {
		// url1 = new File(fullName).toURI().toURL();
		// } else {
		// System.err.println(" Could not find NN "+fullName );
		// }
		//
		// takes the raw FFT from the spectral converter and feeds
		// the neural net classification

		// nnFeatureDetector = new NNSpectralFeatureDetector(fftSize,
		// config.getFeatureVectorSize(), frames.getSpectralProcess(),
		// outputClient, url1, config);
		//
		int overlap = (int) ((fftSize * config.getPercentOverlap()) / 100);
		assert (((4 * fftSize) % overlap) == 0);
		// This is used to convert the audio stream to a spectral stream.

		DataProcess myProcess = new DataProcess() {

			public void process(Data data) throws Exception {
				// magnLog = specAdj.linearLog(featureSize, Config.fftSize,
				// spectrum);
				config.getSpectrumToFeature().process(data); // running3Average(featureSize,
																			// magnLog);

				for (int i = 0; i < data.feature.length; i++) {
					data.feature[i] *= 2; // This is adding volume to the input
											// signal.
				} // the USB audio interface isn't 'hot' enough
				frames.getSpectralProcess().process(data);
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}
		};

		spectralConverter = new SampledToSpectral(fftSize, overlap, sampleRate,
				config.getFeatureVectorSize(), myProcess);

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

		timer.start();
	}

	@Override
	public void setInputWave(File waveFile) {

		if (waveFile == null) {
			realTimeSpectralSource.streamFile(null);
			frames.pauseGraphs(false);
			frames.resetGraphs();
			return;
		}

		try {
			RandomAccessFile rafG = new RandomAccessFile(waveFile, "r");
			AudioReader audioReader = new AudioReader(
					new VanillaRandomAccessFile(rafG), sampleRate);
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

	@Override
	public void setOverlap(int sampsOverLap) {
		spectralConverter.setOverlap(sampsOverLap);
	}
}