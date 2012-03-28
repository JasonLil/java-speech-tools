package speech.dynamic;


import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;

import speech.Data;
import speech.NeuralNet;
import speech.gui.ConfusionPanel;
import uk.ac.bath.ai.backprop.BackPropRecursive;
import config.Config;

public class CreateSerializeDataMain {

	/*
	 * Trains a neural network for a given set of audio data acquired from
	 * a variety of sound sources
	 */

	

		//
		// public static double alpha = 3e6;
		// public static double beta = 1e-9;

		public static double alpha = 300000.0;
		public static double beta = .00000001;

		public static void main(String args[]) throws Exception {

			Config config = Config.current();
			File root = new File("../JavaSpeechToolData/wavfiles/Dynamic");
			String words[]={"Bed_","Red_","Bad_"};
			
			WavTrainingPoolToDirectory pool = 
				new WavTrainingPoolToDirectory(root, config,words);
		
			
			int featSize = config.getFeatureVectorSize();

		}
			
}
