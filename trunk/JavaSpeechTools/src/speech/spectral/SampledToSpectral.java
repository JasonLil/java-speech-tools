package speech.spectral;

import uk.ac.bath.audio.FFTWorker;
import uk.org.toot.audio.core.AudioBuffer;

/**
 * Takes chunks of one size and packs them into chunks of another size Once we
 * have a full chunk it is fed into FFT and the spectral feature fed to the client.
 */

public class SampledToSpectral {

	private FFTWorker fft;

	private boolean doHanning;
	private double preFft[];
	private double postFft[];
	private double magn[];
	private int ptr;

	private int overlap;

	private int fftSize;

	public SampledToSpectral(int fftSize,int overlap, float Fs) {

		doHanning = true;
		fft = new FFTWorker(Fs, doHanning);
		fft.resize(fftSize);

		preFft = new double[fftSize];
		postFft = new double[2 * fftSize];
		magn = new double[fftSize/2];
		ptr = 0;
		this.overlap=overlap;
		this.fftSize=fftSize;

	}

	public void setOverlap(int overlap){
		assert(overlap < fftSize);
		this.overlap=overlap;	
	}
	
	public void processAudio(AudioBuffer chunk,
			NNSpectralFeatureDetector client) {

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
				for (int j = 0; j < preFft.length/2; j++) {
					double real = postFft[2 * j];
					double imag = postFft[2 * j + 1];
					magn[j] = (float) Math.sqrt((real * real) + (imag * imag));
				}
				
				ptr = overlap;
				if (overlap != 0) {
					System.arraycopy(preFft, fftSize-overlap, preFft, 0, overlap);
				}
				
				if (client != null)
					client.process(magn);
			}
		}
	}

	public double[] getMagn() {   // TODO Deprecate this

		return magn;
	}

}