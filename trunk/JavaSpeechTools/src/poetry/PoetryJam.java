package poetry;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.sound.sampled.AudioFormat;
import javax.swing.Timer;

import speech.Data;
import speech.gui.MakeFrames;
import speech.spectral.RealTimeSpectralSource;
import speech.spectral.SampledToSpectral;
import speech.spectral.SpectralClient;
import speech.spectral.SpectralProcessor;

import com.frinika.audio.io.AudioReader;
import com.frinika.audio.io.AudioWriter;
import com.frinika.audio.io.VanillaRandomAccessFile;

import config.Config;

public class PoetryJam implements SpectralClient {

	private MakeFrames frames;
	private Timer timer;
	
	public boolean isApplet = false; // hack hack hack ... eeeek

	double output[];
	public RealTimeSpectralSource realTimeSpectralSource;
	public SampledToSpectral spectralConverter;
	private Config config;
	int fftSize;
	float sampleRate;
	int outSize;

	public static void main(String args[]) {
		PoetryJam app;
		try {
			app = new PoetryJam(false);
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

	PoetryJam(boolean isApplet) throws IOException {

		config = Config.current();
		output = new double[config.getOutputSize()];
		fftSize = config.getFFTSize();
		sampleRate = config.getSampleRate();
		outSize = config.getOutputSize();

		
	}

	void start() throws InterruptedException, IOException,
			ClassNotFoundException {

		
		// This is used to convert the audio stream to a spectral stream.
		spectralConverter = new SampledToSpectral(fftSize, 0, sampleRate,
				config.getFeatureVectorSize());

		// Grabs input and feeds into the spectralConverter
		realTimeSpectralSource = new RealTimeSpectralSource(spectralConverter,
				this);

		
		SpectralProcessor client=new SpectralProcessor(){

			@Override
			public void process(Data data) throws Exception {
				
				
			}
			
		};

		try {
			// Start audio thread and connect nnFeatureDetector via the chunk
			// size converter
			realTimeSpectralSource.startAudio(client);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		setOutputWave(new File("/tmp/PJ.wav"));
	//	timer.start();
	}

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
	
	public void setOutputWave(File waveFile) {

		if (waveFile == null) {
			realTimeSpectralSource.record(null);
			return;
		}

		try {

			AudioFormat format = new AudioFormat(sampleRate,16,1, true,
		            false);
			AudioWriter rec = new AudioWriter(waveFile,format);
			realTimeSpectralSource.record(rec);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void eof(boolean b) {
		frames.pauseGraphs(b);
	}

}