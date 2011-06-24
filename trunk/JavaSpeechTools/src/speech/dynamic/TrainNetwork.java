package speech.dynamic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import config.Config;

import speech.NeuralNet;
import speech.ReadWav;
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
	public static int outputs = 6;    // TODO
	
	public static int fftSize = 1024;
	public static int onscreenBins = 128;
	
	public static double alpha = 300000.0;
	public static double beta = .000001;
	
	public static double maxError = 0.01;
	
	private static int i_max;

	public static void main(String args[]) throws Exception {

		Config config=new Config();
		File root=new File("src/speech/wavfiles/Dynamic");
		WavTrainingPool pool= new WavTrainingPool(root,config);
		


		int sz[] = { config.getFeatureVectorSize(), hidden, pool.nTarget() };

		Random rand=new Random();
		neuralNet = new BackPropRecursive(sz, beta, alpha, rand);
		neuralNet.randomWeights(0.0, 0.01);

		int id=rand.nextInt(pool.trainingData.size());
		
		
		
//		// -------- Train Network------------------ //
//
//		FileOutputStream istr = new FileOutputStream(
//				"src/textfiles/network.txt");
//		ObjectOutputStream out = new ObjectOutputStream(istr);
//		out.writeObject(neuralNet);
//		out.close();

		
	}

}