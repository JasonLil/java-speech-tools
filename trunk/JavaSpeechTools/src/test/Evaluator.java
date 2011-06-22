package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import speech.NeuralNet;
import uk.ac.bath.ai.backprop.BackProp;
import uk.ac.bath.ai.backprop.FeedForward;
import uk.ac.bath.ai.backprop.FeedForwardIF;
import uk.ac.bath.ai.backprop.TrainingData;

public class Evaluator {

	
	private TestData data;
	
	Evaluator(TestData d){
		data=d;
	}
	
	 
	public double fitness(FeedForwardIF net) {
		double maxError = 0;

		for (TrainingData d :data){		
			double out[]= net.forwardPass(d.in);
			double err = Math.max(Util.mse(out,d.out), maxError);
			maxError = Math.max(err, maxError);
		}
		return -maxError;
	}

	
	public void testNet(FeedForwardIF bp) {
		//int nTest = target.length;
		System.out
				.println("Now using the trained network to make predctions on test data....");
		for (TrainingData t :data) {
			double out[]=bp.forwardPass(t.in);
			System.out.println(t.in[0] + "  " + t.in[1] + "  "
					+ t.in[2] + "  " + t.out[0] + "  " + out[0]
					+ "  " + t.out[1] + "  " + out[1]);
		}
	}
	
}
