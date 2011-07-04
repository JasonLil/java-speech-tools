package speech.spectral;

import java.util.Arrays;

public class MelSpectrumToFeature implements SpectrumToFeature {


	double[] magnLog;
	
	private int featureSize;
	private int fftSize;
	
	
	public MelSpectrumToFeature(int featureSize,int fftSize,float fLow,float fHigh,float Fs) {

		float mLow=fToMel(fLow);
		float mHigh=fToMel(fHigh);
		int nFreq=fftSize/2;
		
		float melF[]=new float[nFreq+1];
		melF[nFreq]=Float.MAX_VALUE;
		
		float fBin=Fs/fftSize;
		
		for (int i=0;i<nFreq;i++){
			melF[i]=fToMel(i*fBin);
		}
		
		float f0=-1.0e32f;
		
		for (int i=0;i<featureSize;i++) {
			float f1=melF[i];
			float f2=melF[i+1];
			for (int j=0;j<nFreq;j++){
				float f=melF[j];
				if (f>f0) {
					if (f<f1) {
						float w=(f-f0)/(f1-f0);
						addWeight(i,j,w);
					}
				}
				
			}
		}
		
		magnLog = new double[featureSize];
		this.featureSize=featureSize;
		this.fftSize=fftSize;
	}	
	
	public float fToMel(float f) {
		return (float) (2595.0*Math.log10(1.0+f/700.0));
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
}
