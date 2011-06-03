package speech.spectral;

import uk.ac.bath.audio.FFTWorker;
import uk.org.toot.audio.core.AudioBuffer;




public class SpectralAnalysisProcess {

	private FFTWorker fft;
	
	private boolean doHanning;
	private double preFft[];
	private double postFft[];
	private double magn[];
	private int ptr;

	public SpectralAnalysisProcess(int fftSize, float Fs) {
		
		doHanning = true;
		fft = new FFTWorker(Fs, doHanning);
		fft.resize(fftSize);
		
		preFft = new double[fftSize];
		postFft = new double[2 * fftSize];
		magn = new double[fftSize];
		ptr = 0;
		
	}
	
	public void processAudio(AudioBuffer chunk,SpectralClient client) {

		// PJL: Mix left and right so we don't need to worry about which channel
		// is
		// being used.

		float chn0[] = chunk.getChannel(0);
		float chn1[] = chunk.getChannel(1);

		for (int i = 0; i < chn0.length; i++) {
			preFft[ptr] = chn0[i] + chn1[i];
			ptr++;
			if (ptr == preFft.length) {
				fft.process(preFft, postFft);
				for (int j = 0; j < preFft.length; j++) {
					double real = postFft[2 * j];
					double imag = postFft[2 * j + 1];
					magn[j] = (float) Math.sqrt((real * real) + (imag * imag));
				}
				ptr = 0;
				if (client != null)
					client.process(magn);

			}
		}
	}

	public double[] getMagn() {
		
		return magn;
	}

}