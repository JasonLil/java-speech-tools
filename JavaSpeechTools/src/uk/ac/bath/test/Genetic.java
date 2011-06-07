package uk.ac.bath.test;

import speech.NeuralNet;
import uk.ac.bath.ai.backprop.BackProp;

public class Genetic implements Comparable<Genetic> {
	
	
	double fitness;
	Object net;
	
	Genetic(Object o,double fit){
		net=o;
		fitness=fit;
	}
	@Override
	public int compareTo(Genetic o) {

		if (o.fitness > fitness) return 1;
		if (o.fitness < fitness) return  -1;

		if (o.hashCode() > hashCode()) return 1;
		if (o.hashCode() <hashCode()) return -1;
		return 0;
		
	}

	public Object net() {
		return net;
	}
	
	
	

}
