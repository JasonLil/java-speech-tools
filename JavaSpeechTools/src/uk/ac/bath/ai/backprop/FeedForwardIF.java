package uk.ac.bath.ai.backprop;

import java.io.OutputStream;
import java.io.PrintStream;

public interface FeedForwardIF {

	double[] forwardPass(double[] in);
	void printWeights(PrintStream str);

}
