package speech.spectral;

import java.util.ArrayList;
import java.util.Arrays;

public class MelSpectrumToFeature implements SpectrumToFeature {


	double[] magnLog;
	
	private int featureSize;
	private int fftSize;
	
	
	ArrayList<Weight>[]  weights;

	private float fLow;

	private float fHigh;
	
	public MelSpectrumToFeature(int featureSize,int fftSize,float fLow,float fHigh,float Fs) {

		this.fLow=fLow;
		this.fHigh=fHigh;
		
		float mLow=fToMel(fLow);
		float mHigh=fToMel(fHigh);
		
		float featMelFreq[]=new float[featureSize];
		
		for (int i=0;i<this.featureSize+1;i++){
			featMelFreq[i]=mLow+i*(mHigh-mLow)/(this.featureSize-1);
		}
		
		int nFreq=fftSize/2;
		
		weights=new ArrayList[nFreq];
		
		for (int i=0;i<nFreq;i++){
			weights[i]=new ArrayList<Weight>();
		}
		
		float fBin=Fs/fftSize;

		float spectMelFreq[]=new float[nFreq];
		
		for (int i=0;i<nFreq;i++){
			spectMelFreq[i]=fToMel(i*fBin);
		}
		
		float f0=0.0f;
		
		for (int i=0;i<featureSize;i++) {
			float f1=spectMelFreq[i];
			float f2=spectMelFreq[i+1];
			for (int bin=0;bin<nFreq;bin++){
				float f=spectMelFreq[bin];
				if (f>f0) {
					if (f<f1) {
						float w=(f-f0)/(f1-f0);
						addWeight(i,bin,w);
					} else if (f < f2) {
						float w=(f2-f)/(f2-f1);
						addWeight(i,bin,w);
					}
				} 
			}
			f0=f1;
		}
		
		magnLog = new double[featureSize];
		this.featureSize=featureSize;
		this.fftSize=fftSize;
	}	
	
	private void addWeight(int i, int bin, float w) {
		weights[bin].add(new Weight(i,w));
		
	}

	public float fToMel(float f) {
		return (float) (2595.0*Math.log10(1.0+f/700.0));
	}

	
	public void spectrumToFeature(double[] spectrum,double [] feature){
		
		assert(spectrum.length == fftSize/2);
		assert(feature.length == featureSize);
		Arrays.fill(magnLog,0.0);
		int nBins=fftSize/2;
		
		for(int bin=0;bin<nBins;bin++){
			double val=spectrum[bin];
			for (Weight w:weights[bin]){
				magnLog[w.j] += w.w*val;
			}
		}
		
	}
	
	public String getName(){
		return "MEL"+"_"+fftSize+"_"+featureSize+"_"+fLow+"_"+fHigh;
	}
	
}

class Weight{
	public Weight(int i, float w2) {
		j=i;
		w=w2;
	}
	int j;
	double w;
};
