package speech.spectral;


public class RawSpectrumToFeature implements SpectrumToFeature {


	private int featureSize;

	public RawSpectrumToFeature(int featureSize) {
		this.featureSize=featureSize;
	}


	public void spectrumToFeature(double[] spectrum, double[] feature) {

		assert(feature.length == featureSize);
		assert(spectrum.length >= featureSize);
		for (int i = 0; i < feature.length; i++) {
				feature[i] =  spectrum[i];
			}
		

	}

	public String getName() {
		return "RAW" + "_" +  featureSize;
	}
}
