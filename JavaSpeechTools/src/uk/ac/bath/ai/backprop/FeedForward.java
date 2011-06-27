package uk.ac.bath.ai.backprop;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Random;

import speech.Data;
import speech.NeuralNet;

public class FeedForward extends NNProcess implements Cloneable,FeedForwardIF,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// output of each neuron out[layer_no][neuron_index]
	double out[][];
	
	double weight[][][];
	// no of layers in net
	// including input layer
	
	int numlayer;
	// vector of numl elements for size
	// of each layer
	int layersizes[];
	
	
	public void wash() {
		for(double[] row:out){
			for(int i=0;i<row.length;i++){
				row[i]=0.0;
			}
		}
		
	}
	public FeedForward(int sz[]) {
		
		numlayer = sz.length;
		layersizes = new int[numlayer];

		for (int i = 0; i < numlayer; i++) {
			layersizes[i] = sz[i];
		}
		
		// allocate memory for output of each neuron
		out = new double[numlayer][];

		for (int i = 0; i < numlayer; i++) {
			out[i] = new double[layersizes[i]];
		}

		// allocate memory for weights
		weight = new double[numlayer][][];

		for (int i = 1; i < numlayer; i++) {
			weight[i] = new double[layersizes[i]][];
		}

		for (int i = 1; i < numlayer; i++) {
			for (int j = 0; j < layersizes[i]; j++) {
				weight[i][j] = new double[layersizes[i - 1] + 1];
			}
		}
//		if(rand == null){
//			 rand = new Random();
//			}
//			for (int i = 1; i < numlayer; i++) {
//				for (int j = 0; j < layersizes[i]; j++) {
//					for (int k = 0; k < layersizes[i - 1] + 1; k++) {
//						weight[i][j][k] = (double) (rand.nextDouble() - .5);// 32767
//						// initialize previous weights to 0 for first iteration
//					}
//				}
//			}	
	}
	
	
	public FeedForward() {
		
	}


	public void randomWeights(double low,double high,Random rand) {
		for (int i = 1; i < numlayer; i++) {
			for (int j = 0; j < layersizes[i]; j++) {
				for (int k = 0; k < layersizes[i - 1] + 1; k++) {
					double x=rand.nextDouble();
					weight[i][j][k] = (double) (low +(high-low)*x);
				}
			}
		}

	}
	
	public FeedForward clone(){
		FeedForward net=new FeedForward(layersizes);
		for (int i = 1; i < numlayer; i++) {
			for (int j = 0; j < layersizes[i]; j++) {
				System.arraycopy(weight[i][j],0, net.weight[i][j], 0, layersizes[i - 1]);
			}
		}
		return net;
	}

	// feed forward one set of input
	protected void ffwd(double in[]) {
		double sum;

		// assign content to input layer
		for (int i = 0; i < layersizes[0]; i++) {
			out[0][i] = in[i]; // output_from_neuron(i,j) Jth neuron in Ith
								// Layer

			// assign output(activation) value
			// to each neuron usng sigmoid func
		}
		for (int i = 1; i < numlayer; i++) { // For each layer
			for (int j = 0; j < layersizes[i]; j++) { // For each neuron in
														// current layer
				sum = 0.0f;
				for (int k = 0; k < layersizes[i - 1]; k++) { // For input from
																// each neuron
																// in preceeding
																// layer
					sum += out[i - 1][k] * weight[i][j][k]; // Apply weight to
															// inputs and add to
															// sum
				}
				sum += weight[i][j][layersizes[i - 1]]; // Apply bias
				out[i][j] = sigmoid(sum); // Apply sigmoid function
			}
		}
	}
	// sigmoid function
	public static double sigmoid(double in) {
		return (double) (1.0 / (1.0 + Math.exp(-in)));
	}
	
	// sigmoid function
	public static double sigmoid1(double in) {
		double ee=Math.exp(-in);
		return (double) (1.0-ee / (1.0 + ee));
	}

	public double[] forwardPass(double[] input) {
		ffwd(input);
		return out[numlayer-1];
	}
	
	public void mutate(double mutateAmount,Random rand) {
		
		for (int i = 1; i < numlayer; i++) {
			for (int j = 0; j < layersizes[i]; j++) {
				for (int k = 0; k < layersizes[i - 1] + 1; k++) {
					double x=(rand.nextDouble()-0.5)*mutateAmount;
					weight[i][j][k] += x;
				}
			}
		}

		// TODO Auto-generated method stub
		
	}


	@Override
	public void printWeights(PrintStream str) {
		for (int i = 1; i < numlayer; i++) {
			for (int j = 0; j < layersizes[i]; j++) {
				for (int k = 0; k < layersizes[i - 1] + 1; k++) {
					str.print(weight[i][j][k]+" ");
				}
				str.println();
			}
			str.println("--");
		}
		
	}


	@Override
	public double[] backPropTrain(double[] in, double[] tgt) throws Exception {
		
		throw new Exception(" Sorry plain FeedForward net does not implemtent backPropTraining");
		
	}
	

}
