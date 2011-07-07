package speech.spectral;

import java.util.Arrays;

public class JRSpectrumToFeature implements SpectrumToFeature {

//	
//	double[] smoothed;
	double[] magnLog;
//	
	private int featureSize;
	private int fftSize;
	
	
	public JRSpectrumToFeature(int featureSize,int fftSize) {
//		smoothed = new double[featureSize];
		magnLog = new double[featureSize];
		this.featureSize=featureSize;
		this.fftSize=fftSize;
	}	
	

	
	public void spectrumToFeature(double[] spectrum,double [] feature){
		
		assert(spectrum.length == fftSize/2);
		assert(feature.length == featureSize);
		Arrays.fill(magnLog,0.0);

		linearLog(spectrum);
		running3Average(feature);
		
	}
	
	private void linearLog( double[] spectrum) {

	
	
		int triangular = 0;

		for (int i = 0; i < featureSize; i++) {
			triangular += i;
		}

		double factor = (double) fftSize / triangular / 2;
		int count = 0;
		int count2 = 0;
		while (count != featureSize) {
			for (int j = 0; j < Math.round(count * factor); j++) {
				magnLog[count] += spectrum[count2];
				count2++;
			}
			count++;
		//	System.out.println(" Conunt2: "+count2);
		}
		//return magn_log;

	}

	private void running3Average( double[] feature) {
		
		
		feature[0]=(magnLog[1] + magnLog[0]) / 2;
		feature[featureSize - 1]=(magnLog[featureSize - 1] + magnLog[featureSize - 2]) / 2;
		
		for (int i = 1; i < (featureSize - 1); i++) {
			feature[i] = (magnLog[i - 1] + magnLog[i] + magnLog[i + 1]) / 3;
		}

	}
	
	public String getName(){
		return "JR"+"_"+fftSize+"_"+featureSize;
	}
}
