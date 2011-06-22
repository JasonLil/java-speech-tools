package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import speech.NeuralNet;
import uk.ac.bath.ai.backprop.BackProp;
import uk.ac.bath.ai.backprop.TrainingData;

public class BackPropTest {

	public static void train(NeuralNet bp, TestData tD,Evaluator eval) throws Exception {
		//int nTest = target.length;
		double Thresh = 0.00001;

		// maximum no of iterations during training
		long num_iter = 2000000;

		System.out.println("Now training the network....");
		int i;
		
		for (i = 0; i < num_iter; i++) {
			double maxError = 0;

			for (TrainingData d:tD) {
				bp.backPropTrain(d.in,d.out);
				double err=eval.fitness(bp);
				double out[]=bp.forwardPass(d.in);
				maxError = Math.max(Util.mse(out,d.out), maxError);
			}

			if (maxError < Thresh) {
				System.out
						.println("Network Trained. Threshold value achieved in "
								+ i
								+ " iterations.\n"
								+ "MSE:  "
								+ maxError);
				break;
			}

			System.out.println("MSE:  " + String.format("%f7.5", maxError)
					+ "... Training...");
		}

	
	}

	

	public static void main(String arg[]) throws Exception {

		// prepare XOR traing data

		TestData testData=new TestData();
		
		
		int lSz[] = { 3, 6, 2 };

				double beta = .01, alpha = 1000.;
		
		// Creating the net
		BackProp bp = new BackProp(lSz, beta, alpha, new Random());
		Evaluator eval=new Evaluator(testData);
		train(bp,testData,eval);
		eval.testNet(bp);
		bp.printWeights(System.out);
		
		File file = new File("PJLBackprop.net");

		try {
			FileOutputStream istr = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(istr);
			out.writeObject(bp);
			out.close();
			bp=null;
			FileInputStream ostr = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(ostr);
			bp=(BackProp)in.readObject();
			in.close();
			System.out.println(" Loaded from file .......... ");
			eval.testNet(bp);
			

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
