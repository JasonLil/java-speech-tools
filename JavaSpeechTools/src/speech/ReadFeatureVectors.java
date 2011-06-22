package speech;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import speech.spectral.SampledToSpectral;
import speech.spectral.SpectrumToFeature;
import uk.org.toot.audio.core.AudioBuffer;

import com.frinika.audio.io.AudioReader;
import com.frinika.audio.io.VanillaRandomAccessFile;

import config.Config;

public class ReadFeatureVectors {

	
	private SpectrumToFeature spectAdjust;

	ReadFeatureVectors(int featureSize,int fftSize) {
		
	
		spectAdjust=new SpectrumToFeature(featureSize,fftSize);
	}
	
	double[][] readVectors(File file) throws IOException {

		int fftSize=Config.getFFTSize();
		int featSize=Config.getFeatureVectorSize();
		
		ArrayList<double[]> list=new ArrayList<double[]>();
		
		SampledToSpectral spectralAnalysis = new SampledToSpectral(
				fftSize, 0, Config.getSampleRate(),featSize);

		RandomAccessFile rafG = new RandomAccessFile(file, "r");
		
		AudioReader audioReader = new AudioReader(new VanillaRandomAccessFile(
				rafG), Config.getSampleRate());
		AudioBuffer chunk = new AudioBuffer("Buf", 2, fftSize,
				Config.getSampleRate());
		chunk.setRealTime(false);

		while (!audioReader.eof()) {

			chunk.makeSilence();
			audioReader.processAudio(chunk);

			
			double spectrum[] = spectralAnalysis.processAudio(chunk);
			double feature[]=new double[featSize];
			spectAdjust.spectrumToFeature(spectrum,feature);
		}
		return (double[][])list.toArray();
	}

}
