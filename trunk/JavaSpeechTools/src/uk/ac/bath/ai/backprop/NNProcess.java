package uk.ac.bath.ai.backprop;

import speech.Data;

public abstract class NNProcess {
	
	public abstract double[] forwardPass(double [] in);
	public abstract double[] backPropTrain(double in [], double tgt[]) throws Exception;
	
	public void process(Data data) throws Exception {
		if (data.target==null) {
			data.output=forwardPass(data.feature);
		} else {
			data.output=backPropTrain(data.feature,data.target);
		}
	}

}
