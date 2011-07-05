package speech.dynamic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;

import config.Config;

import speech.Data;
import speech.NeuralNet;
import speech.ReadWav;
import speech.gui.ConfusionPanel;
import speech.spectral.SpectrumToFeature;
import uk.ac.bath.ai.backprop.BackProp;
import uk.ac.bath.ai.backprop.BackPropRecursive;


/*
 * Trains a neural network for a given set of audio data acquired from
 * a variety of sound sources
 */

public class TrainNetwork {

	
	
	
	public static int hidden = 40;

	
	public static double alpha =  3e6;
	public static double beta =  1e-9;


	public static void main(String args[]) throws Exception {

		Config config = new Config(null);
		File root = new File("src/speech/wavfiles/Dynamic");
		WavTrainingPool pool = new WavTrainingPool(root, config);
		int featSize = config.getFeatureVectorSize();

		int sz[] = { config.getFeatureVectorSize(), hidden, pool.nTarget() };

		Random rand = new Random();
		NeuralNet net = new BackPropRecursive(sz, beta, alpha,true,true);
		net.randomWeights(-0.1, 0.1,rand);

		

		JFrame frame = new JFrame("Average");
		ConfusionPanel panSum = new ConfusionPanel(pool.names);
		frame.setContentPane(panSum);
		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		frame = new JFrame("End");
		ConfusionPanel panEnd = new ConfusionPanel(pool.names);
		frame.setContentPane(panEnd);
		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		Data data = new Data(0, featSize);

		int iter=0;
		
		while (true) {

			// select training set randomly
			int kkk = rand.nextInt(pool.trainingData.size());

			TrainingData td = pool.trainingData.get(kkk);

			data.target = pool.target[td.id];

			// sum of outputs over time.
			net.wash();
			
			for (double[] vec : td.featureSequence()) {
				data.feature = vec;
				net.process(data);
			}
			if (iter %1000 == 0) {
				testNet(net, pool, config, panSum, panEnd);
				
			}

		}
		
		
		
		
		// // -------- Train Network------------------ //
		//
		// FileOutputStream istr = new FileOutputStream(
		// "src/textfiles/network.txt");
		// ObjectOutputStream out = new ObjectOutputStream(istr);
		// out.writeObject(neuralNet);
		// out.close();

	}

	
	static void testNet(NeuralNet net,WavTrainingPool pool,Config config,ConfusionPanel panSum,ConfusionPanel panEnd) throws Exception {

		Data data = new Data(0, config.getFeatureVectorSize());

		int nTarget=pool.nTarget();
		double confusionSum[][] = new double[nTarget][nTarget];
		double confusionEnd[][] = new double[nTarget][nTarget];
		double sum[] = new double[nTarget];
		int itcount[] = new int[nTarget];
		
		for (TrainingData td:pool.trainingData){
			
			// sum of outputs over time.
			Arrays.fill(sum, 0.0);
			net.wash();
			
			for (double[] vec : td.featureSequence()) {
				data.feature = vec;
				net.process(data);
				for (int i = 0; i < nTarget; i++) {
					sum[i] += data.output[i];
				}
			}

			normalize(sum);
			
	
			for (int i = 0; i < nTarget; i++) {
				confusionSum[td.id][i] +=  sum[i];
				confusionEnd[td.id][i] +=  data.output[i];
			}

			itcount[td.id]++;

		}
	
		for (int i = 0; i < nTarget; i++) {
			for (int j = 0; j < nTarget; j++) {
				confusionSum[i][j]=confusionSum[i][j]/itcount[i];
				confusionEnd[i][j]=confusionEnd[i][j]/itcount[i];
			}
		}
		
		
		
		//pan.repaint();
	
		panSum.update(confusionSum);
		panEnd.update(confusionEnd);
	
	}
	
	private static void normalize(double[] sum) {
		double tot = 0;

		for (int i = 0; i < sum.length; i++) {
			tot += sum[i];
		}

		for (int i = 0; i < sum.length; i++) {
			sum[i] *=1.0/tot;
		}

		
		// TODO Auto-generated method stub
		
	}


	static void dump(double[][] array, int[] itcount, int iter) {

		System.out.println(" Iteration : " + iter);

		for (int rowId = 0; rowId < array.length; rowId++) {
			for (double x : array[rowId]) {
				if (itcount[rowId] == 0) {

					System.out.format(" ----");
				} else {
					System.out.format(" %.3f", x / itcount[rowId]);
				}

			}
			System.out.println();

		}
	}
}