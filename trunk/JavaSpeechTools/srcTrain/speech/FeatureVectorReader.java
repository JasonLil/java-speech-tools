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

public class FeatureVectorReader {

	
	private SpectrumToFeature spectAdjust;
	private Config config;

	public FeatureVectorReader(Config config) {
		spectAdjust=config.getSpectrumToFeature();
		this.config=config;
	}
	
	public ArrayList<double[]> readVectors(File file) throws IOException {

		int fftSize=config.getFFTSize();
		int featSize=config.getFeatureVectorSize();
		int bitSize=(int) Math.round(fftSize*((100.0-config.getPercentOverlap())/100.0));
			
		assert(fftSize % bitSize == 0);
		
		int nBitPerChunk=fftSize/bitSize;
		
		ArrayList<double[]> list=new ArrayList<double[]>();
		
		SampledToSpectral spectralAnalysis = new SampledToSpectral(
				fftSize, 0, config.getSampleRate(),featSize);

		RandomAccessFile rafG = new RandomAccessFile(file, "r");
		
		AudioReader audioReader = new AudioReader(new VanillaRandomAccessFile(
				rafG), config.getSampleRate());
		
		
		AudioBuffer chunk = new AudioBuffer("Buf", 2, fftSize,
				config.getSampleRate());
		chunk.setRealTime(false);

		
		AudioBuffer bit = new AudioBuffer("Bufbit", 2, bitSize,
				config.getSampleRate());
		chunk.setRealTime(false);

		
		
		
		while (!audioReader.eof()) {

			chunk.makeSilence();
			audioReader.processAudio(chunk);

			
			double spectrum[] = spectralAnalysis.processAudio2(chunk);
			double feature[]=new double[featSize];
			spectAdjust.spectrumToFeature(spectrum,feature);
			list.add(feature);
		}
		return list;
	}

}
