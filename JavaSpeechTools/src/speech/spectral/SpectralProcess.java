package speech.spectral;

public interface SpectralProcess {

	void notifyMoreDataReady(double[] smoothed);

}
