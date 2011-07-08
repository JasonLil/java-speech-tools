package uk.ac.bath.ai.backprop;

// converted to java by p.j.leonard
// based  on C++ code found at 
// http://www.codeproject.com/KB/recipes/BP.aspx?msg=2809798#xx2809798xx
//

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

import speech.Data;
import speech.NeuralNet;
import uk.ac.bath.tweaks.Tweakable;

public class BackPropRecursive extends NNProcess implements Serializable, NeuralNet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int inSize;
	public BackProp bp;
	private int hidSize;
	private double[] in;
	private boolean hFeed;
	private boolean oFeed;
	private int outSize;

	public BackPropRecursive(int sz[], double b, double a,boolean hidFeedBack,boolean outFeedBack) {
		this.inSize = sz[0];
		this.hidSize = sz[1];
		this.outSize=sz[2];
		if (hidFeedBack) sz[0]+=sz[1];
	   	if (outFeedBack) sz[0]+=sz[2];
	   	this.hFeed=hidFeedBack;
	   	this.oFeed=outFeedBack;
		 
		this.in = new double[sz[0]];
		bp = new BackProp(sz, b, a);
	}

	public Vector<Tweakable> getTweaks() {
		return bp.tweaks;
	}

	// feed forward one set of input
	private void makeInput(double in1[]) {
		int inPtr=0;
		System.arraycopy(in1, 0, this.in, inPtr, this.inSize);
		
		inPtr+=this.inSize;

		if (hFeed){
			System.arraycopy(bp.out[1], 0, this.in, inPtr, this.hidSize);
			inPtr+=hidSize;
		}
		
		if (oFeed){
			System.arraycopy(bp.out[2], 0, this.in, inPtr, this.outSize);
		}	
	}

	@Override
	public double[] backPropTrain(double in1[], double tgt[]) {
		makeInput(in1);
		return bp.backPropTrain(in, tgt);
	}

	@Override
	public double[] forwardPass(double[] in1) {
		makeInput(in1);
		return bp.forwardPass(in);
	}

	// @Override
	// public void randomWeights() {
	// bp.randomWeights();
	// }

	@Override
	public void randomWeights(double low, double high,Random rand) {
		bp.randomWeights(low, high,rand);

	}

	@Override
	public void printWeights(PrintStream str) {
		// TODO Auto-generated method stub

	}

	@Override
	public void wash() {
		bp.wash();	
	}

	@Override
	public int inputSize() {
		return inSize;
	}

	

}
