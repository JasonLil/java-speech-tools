package speech.spectral;


/**
 * Interface for observers of spectral data
 * @author pjl
 *
 */
public interface SpectralProcess {
	void notifyMoreDataReady(double[] data);
}
