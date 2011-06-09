package speech.spectral;

import com.frinika.audio.io.AudioReader;
import com.frinika.audio.io.VanillaRandomAccessFile;

import config.Config;

import java.io.File;
import java.io.RandomAccessFile;

import uk.org.toot.audio.core.AudioBuffer; 

//
//@author JER
//
/*
 * 
 * Reads a wav file from disk, performs a FFT on it, 
 * and return the information in an array
 *  
 */

public class ReadWav {

	public static int file_length[];

	public ReadWav(int outputs) {
		file_length = new int[outputs + 1];
	}

	public static double[][][] getMonoThongWavs(int fftSize, int outputs,
			float Fs, int maxAudioLength) throws Exception {

		double allWavs[][][] = new double[maxAudioLength][fftSize][21];

		String names[] = { "eee_all", "ehh_all", "err_all", "ahh_all",
				"ooh_all", "uhh_all", "silence_all" };

		for (int i = 0; i < outputs + 1; i++) {

			String resource = "src/speech/wavfiles/" + names[i] + ".wav";
			double wav[][] = readWav(resource, fftSize, Fs, i);

			for (int j = 0; j < wav.length; j++) {
				for (int k = 0; k < wav[0].length; k++) {
					allWavs[j][k][i] = wav[j][k];
				}
			}
		}

		return allWavs;

	}

	public static double[][] readWav(String filename, int fftSize, float Fs,
			int num) throws Exception {

		SpectralConvertor spectralAnalysis = new SpectralConvertor(
				fftSize, Config.sampleRate);
		File file = new File(filename);
		RandomAccessFile rafG = new RandomAccessFile(file, "r");
		AudioReader audioReader = new AudioReader(new VanillaRandomAccessFile(
				rafG),Fs);
		AudioBuffer chunk = new AudioBuffer("James buffer", 2, fftSize, Fs);
		chunk.setRealTime(true);

		double[][] output = new double[1000][fftSize];

		int i = 0;

		while (!audioReader.eof()) {

			chunk.makeSilence();
			audioReader.processAudio(chunk);
			spectralAnalysis.processAudio(chunk,null);
			double magn[]=spectralAnalysis.getMagn();
			
			for (int j = 0; j < magn.length; j++) {
				output[i][j] = magn[j];
			}
			i++;

		}

		file_length[num] = i;
		System.out.println("The file has a length of: " + i);

		return output;

	}

}
