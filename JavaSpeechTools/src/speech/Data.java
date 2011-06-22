package speech;

import config.Config;
import uk.org.toot.audio.core.AudioBuffer;

public class Data {
	
	public AudioBuffer chunk;
	public double[] spectrum;
	public double[] feature;
	public double[] target;
	public double[] output;
	
	public Data(int fftSize,int featureSize) {
		spectrum=new double[fftSize/2];
		feature=new double[featureSize];
	}
	
}
