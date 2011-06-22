package speech.spectral;

import speech.Data;


/**
 * Interface for observers of spectral data
 * @author pjl
 *
 */
public interface SpectralProcess {
	void notifyMoreDataReady(Data data);
}
