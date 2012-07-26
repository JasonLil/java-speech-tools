package speech.spectral;

import speech.Data;

/**
 * 
 * Interface for classes wanting to observe the output
 * 
 * See for example NNSpectralFeatureDetector
 * 
 * 
 * TODO single interface that observes Data ?
 * 
 * @author eespjl
 *
 */

public interface DataProcess {

	public void process(Data data) throws Exception;
	public String getName();

}
