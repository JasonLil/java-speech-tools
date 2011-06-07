package uk.ac.bath.ai.backprop;

// converted to java by p.j.leonard
// based  on C++ code found at 
// http://www.codeproject.com/KB/recipes/BP.aspx?msg=2809798#xx2809798xx
//

import java.io.IOException;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Vector;

import speech.NeuralNet;
import uk.ac.bath.tweaks.Tweakable;
import uk.ac.bath.tweaks.TweakableDouble;

public class BackProp extends FeedForward implements Serializable, NeuralNet,FeedForwardIF {
	/**
	 *
	 *
	 *   weight[layer=0:numlayer-1][out_node=0:layersize[layer]-1][in_node=0:layersize[layer-1]]
	 * 
	 *   weight[layer][out_node][layer_size[layer-1]]  is the bais (connected to unit input)
	 *   
	 *   weight[0]=null   
	 *   
	 */
	private static final long serialVersionUID = 1L;

	
	// delta error value for each neuron
	double delta[][];
	// vector of weights for each neuron
	
	
	// learning rate
	double beta;
	// momentum parameter
	double alpha;
	// storage for weight-change made
	// in previous epoch
	double prevDwt[][][];

	transient Vector<Tweakable> tweaks;
	transient private TweakableDouble betaTweak;
	transient private TweakableDouble alphaTweak;
	
	
	public BackProp(){
		super();
	}
	
	public BackProp(int sz[], double b, double a, Random rand) {
		super(sz);
		
		
		beta = b;
		alpha = a;

		makeTweaks();
		// set no of layers and their sizes
		

		
		// allocate memory for delta
		delta = new double[numlayer][];

		for (int i = 1; i < numlayer; i++) {
			delta[i] = new double[layersizes[i]];
		}

		
		// allocate memory for previous weights
		prevDwt = new double[numlayer][][];

		for (int i = 1; i < numlayer; i++) {
			prevDwt[i] = new double[layersizes[i]][];

		}
		for (int i = 1; i < numlayer; i++) {
			for (int j = 0; j < layersizes[i]; j++) {
				prevDwt[i][j] = new double[layersizes[i - 1] + 1];
			}
		}

		// seed and assign random weights
		// srand((unsigned)(time(NULL)));

		

		for (int i = 1; i < numlayer; i++) {
			for (int j = 0; j < layersizes[i]; j++) {
				for (int k = 0; k < layersizes[i - 1] + 1; k++) {
					prevDwt[i][j][k] = 0.0f;// Note that the following variables
											// are unused,
			
				}
			}
		}
		randomWeights(-.5,.5,rand);
	}

	public Vector<Tweakable> getTweaks() {
		return tweaks;
	}



	
	// backpropogate errors from output
	// layer uptill the first hidden layer
	public void bpgt(double in[], double tgt[]) {
		double sum;

		// update output values for each neuron
		ffwd(in);

		// find delta for output layer
		for (int i = 0; i < layersizes[numlayer - 1]; i++) {
			delta[numlayer - 1][i] = out[numlayer - 1][i]
					* (1 - out[numlayer - 1][i])
					* (tgt[i] - out[numlayer - 1][i]);
		}

		// find delta for hidden layers
		for (int i = numlayer - 2; i > 0; i--) {
			for (int j = 0; j < layersizes[i]; j++) {
				sum = 0.0f;
				for (int k = 0; k < layersizes[i + 1]; k++) {
					sum += delta[i + 1][k] * weight[i + 1][k][j];
				}
				delta[i][j] = out[i][j] * (1 - out[i][j]) * sum;
			}
		}

		// apply momentum ( does nothing if alpha=0 )
		for (int i = 1; i < numlayer; i++) {
			for (int j = 0; j < layersizes[i]; j++) {
				for (int k = 0; k < layersizes[i - 1]; k++) {
					weight[i][j][k] += alpha * prevDwt[i][j][k];
				}
				weight[i][j][layersizes[i - 1]] += alpha
						* prevDwt[i][j][layersizes[i - 1]];
			}
		}

		// adjust weights usng steepest descent
		for (int i = 1; i < numlayer; i++) {
			for (int j = 0; j < layersizes[i]; j++) {
				for (int k = 0; k < layersizes[i - 1]; k++) {
					prevDwt[i][j][k] = (double) (beta * delta[i][j] * out[i - 1][k]);
					weight[i][j][k] += prevDwt[i][j][k];
				}
				prevDwt[i][j][layersizes[i - 1]] = (double) (beta * delta[i][j]);
				weight[i][j][layersizes[i - 1]] += prevDwt[i][j][layersizes[i - 1]];
			}
		}
	}

	void makeTweaks() {
		tweaks = new Vector<Tweakable>();
		betaTweak = new TweakableDouble(0., 20., beta, .00001, "beta");
		alphaTweak = new TweakableDouble(0., 1000., alpha, .1, "alpha");

		betaTweak.addObserver(new Observer() {

			public void update(Observable o, Object arg) {
				beta = betaTweak.doubleValue();
			}
		});

		alphaTweak.addObserver(new Observer() {

			public void update(Observable o, Object arg) {
				alpha = alphaTweak.doubleValue();
			}
		});

		tweaks.add(alphaTweak);
		tweaks.add(betaTweak);

	}

	void dump() {
		for (int i = 0; i < numlayer; i++) {
			System.out.println("Layer " + i);

			for (int j = 0; j < layersizes[i]; j++) {
				if (i > 0) {
					System.out.println(" Neuron " + j + " =" + out[i][j] + "  "
							+ delta[i][j]);

					for (int k = 0; k < layersizes[i - 1] + 1; k++) {
						System.out.println("w prev [" + k + "]"
								+ weight[i][j][k] + " " + prevDwt[i][j][k]);
					}
				} else {
					System.out.println(" Neuron " + j + " =" + out[i][j]);
				}
			}
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		makeTweaks();
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

	@Override
	public double[] forwardPass(double[] input) {
		ffwd(input);
		return out[numlayer-1];
	}



	@Override
	public double [] backPropTrain(double[] input, double[] target) {
		 bpgt(input, target);
		 return out[numlayer-1];
	}

	@Override
	public void randomWeights(double low, double high) {
		// TODO Auto-generated method stub
		
	}
}
