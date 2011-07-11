package speech.spectral;

import speech.Data;
import uk.ac.bath.audio.FFTWorker;
import uk.org.toot.audio.core.AudioBuffer;

/**
 * Takes chunks of one size and packs them into chunks of another size Once we
 * have a full chunk it is fed into FFT and the spectral feature fed to the
 * client.
 */

public class SampledToSpectral {

	private FFTWorker fft;

	private boolean doHanning;
	private double preFft[];
	private double postFft[];
	// private double magn[];
	private int ptr;

	private int overlap;

	private int fftSize;

	private Data data;

	public SampledToSpectral(int fftSize, int overlap, float Fs,int featureSize) {

		doHanning = true;
		fft = new FFTWorker(Fs, doHanning);
		fft.resize(fftSize);

		preFft = new double[fftSize];
		postFft = new double[2 * fftSize];
		// magn = new double[fftSize/2];
		ptr = 0;
		this.overlap = overlap;
		this.fftSize = fftSize;
		data = new Data(fftSize,featureSize);
	

	}

	public void setOverlap(int overlap) {
		assert (overlap < fftSize);
		this.overlap = overlap;
	}

	public void processAudio(AudioBuffer chunk, SpectralProcessor client) throws Exception {

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
				for (int j = 0; j < preFft.length / 2; j++) {
					double real = postFft[2 * j];
					double imag = postFft[2 * j + 1];
					data.spectrum[j] = (float) Math.sqrt((real * real)
							+ (imag * imag));
				}

				ptr = overlap;
				if (overlap != 0) {
					System.arraycopy(preFft, fftSize - overlap, preFft, 0,
							overlap);
				}

				if (client != null)
					client.process(data);
			}
		}
	}

	public double[] processAudio(AudioBuffer chunk) {

		// PJL: Mix left and right so we don't need to worry about which channel
		// is
		// being used.

		float chn0[] = chunk.getChannel(0);
		float chn1[] = chunk.getChannel(1);

		if (chn0.length != preFft.length) {
			try {
				throw new Exception(" Chunk size must be same as FFT window");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < chn0.length; i++) {
			preFft[i] = chn0[i] + chn1[i];
		}

		fft.process(preFft, postFft);
		for (int j = 0; j < preFft.length / 2; j++) {
			double real = postFft[2 * j];
			double imag = postFft[2 * j + 1];
			data.spectrum[j] = (float) Math.sqrt((real * real) + (imag * imag));
		}
		return data.spectrum;
	}

	// public double[] getMagn() { // TODO Deprecate this
	//
	// return spectrum;
	// }

}