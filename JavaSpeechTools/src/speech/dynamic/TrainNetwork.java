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

//
//@author JER
//
/*
 * Trains a neural network for a given set of audio data acquired from
 * a variety of sound sources
 */

public class TrainNetwork {

	public static NeuralNet neuralNet;

	public static float Fs = Config.sampleRate;

	public static int inputs = 128;
	public static int hidden = 30;
	public static int outputs = 6; // TODO

	public static int fftSize = 1024;
	public static int onscreenBins = 128;

	public static double alpha = 300000.0;
	public static double beta = .000001;

	public static double maxError = 0.01;

	private static int i_max;

	public static void main(String args[]) throws Exception {

		Config config = new Config();
		File root = new File("src/speech/wavfiles/Dynamic");
		WavTrainingPool pool = new WavTrainingPool(root, config);
		int featSize = config.getFeatureVectorSize();

		int sz[] = { config.getFeatureVectorSize(), hidden, pool.nTarget() };

		Random rand = new Random();
		neuralNet = new BackPropRecursive(sz, beta, alpha, rand);
		neuralNet.randomWeights(0.0, 0.01);

		int nTarget = pool.nTarget();

		JFrame frame = new JFrame();
		ConfusionPanel pan = new ConfusionPanel(pool.names);
		frame.setContentPane(pan);
		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		frame = new JFrame();
		ConfusionPanel panEnd = new ConfusionPanel(pool.names);
		frame.setContentPane(panEnd);
		frame.setSize(400, 400);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		

		double target[][] = new double[nTarget][];

		for (int i = 0; i < nTarget; i++) {
			target[i] = new double[nTarget];
			target[i][i] = 1.0;
		}
		Data data = new Data(0, featSize);

		double sum[] = new double[nTarget];
		double confusionSum[][] = new double[nTarget][nTarget];
		double confusion[][] = new double[nTarget][nTarget];
		int itcount[] = new int[nTarget];
		int iters = 0;
		while (true) {

			int kkk = rand.nextInt(pool.trainingData.size());

			TrainingData td = pool.trainingData.get(kkk);

			data.target = target[td.id];

			Arrays.fill(sum, 0.0);
			for (double[] vec : td.featureSequence()) {
				data.feature = vec;
				neuralNet.process(data);
				for (int i = 0; i < nTarget; i++) {
					sum[i] += data.output[i];
				}
			}

			double tot = 0;

			for (int i = 0; i < nTarget; i++) {
				tot += sum[i];
			}

			for (int i = 0; i < nTarget; i++) {
				confusionSum[td.id][i] += sum[i] / tot;
			}

			iters++;
			itcount[td.id]++;

			for (int i = 0; i < nTarget; i++) {
				for (int j = 0; j < nTarget; j++) {
					if (itcount[i] == 0) {
						confusion[i][j]=0;
					}else {
						confusion[i][j]=confusionSum[i][j]/itcount[i];
					}
				}
			}
			if (iters %100 ==0) dump(confusionSum, itcount,iters);
			pan.update(confusion);
			//pan.repaint();

			// 

		}
		
		
		
		// // -------- Train Network------------------ //
		//
		// FileOutputStream istr = new FileOutputStream(
		// "src/textfiles/network.txt");
		// ObjectOutputStream out = new ObjectOutputStream(istr);
		// out.writeObject(neuralNet);
		// out.close();

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