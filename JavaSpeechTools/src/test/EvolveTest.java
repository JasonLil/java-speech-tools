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
import uk.ac.bath.ai.backprop.FeedForward;
import uk.ac.bath.ai.backprop.FeedForwardIF;
import uk.ac.bath.ai.backprop.TrainingData;

public class EvolveTest {


	public static void mutationTrain(Pool pool,Evaluator evaluator) {

		double thresh = -.01;
		double fit;
		long cnt = 0;
		do {
			FeedForward net=pool.nextGuess();
			fit = evaluator.fitness(net);
			pool.addGenetic(net,fit);
			
			if ((cnt) % 10000 == 0)			
				System.out.println(cnt + " Fitness: " + pool.getBest().fitness);
			
			cnt++;
		} while (fit < thresh);

	}



	public static void main(String arg[]) {

		TestData testData = new TestData();
//
//		// Yes well .....
//		int lSz[] = { 3, 6, 2 };
//
//		double beta = .01, alpha = 1000.;  // NOT USED
//		// Creating the net
//		NeuralNet net = new BackProp(lSz, beta, alpha, new Random());

		Evaluator evo = new Evaluator(testData);
		Pool pool = new Pool(20);
		
		mutationTrain(pool,evo);
		FeedForwardIF net=(FeedForwardIF) pool.getBest().net;
		evo.testNet(net);

		File file = new File("PJLBackprop.net");

		try {
			FileOutputStream istr = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(istr);
			out.writeObject(net);
			out.close();
			net = null;
			FileInputStream ostr = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(ostr);
			net = (FeedForward) in.readObject();
			in.close();
			System.out.println(" Loaded from file .......... ");
			evo.testNet(net);

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
