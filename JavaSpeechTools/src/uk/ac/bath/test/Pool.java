package uk.ac.bath.test;

import java.util.Random;
import java.util.TreeSet;

import speech.NeuralNet;
import uk.ac.bath.ai.backprop.BackProp;
import uk.ac.bath.ai.backprop.FeedForward;
import uk.ac.bath.ai.backprop.FeedForwardIF;

public class Pool {

	private TreeSet<Genetic> list = new TreeSet<Genetic>();
	private int size;
	// Yes well .....
	int lSz[] = { 3, 6, 2 };
	double mutateAmount=1.0;
	Random rand=new Random();
	
	
	
	Pool(int size){
		this.size=size;
	}
	
	
	public FeedForward nextGuess() {
		
		FeedForward net;
		if (list.size()==0){
			net = new FeedForward(lSz);
			net.randomWeights(-.5,.5, rand);
		} else {
			net=((FeedForward)list.first().net()).clone();
			net.mutate(mutateAmount*rand.nextDouble(),rand);
		}
		return net;
		
	}
	
	public void addGenetic(FeedForward net, double fit){
		
		if (list.size() >= size &&  fit < list.last().fitness) return;
		Genetic g=new Genetic(net,fit);
		list.add(g);
		if (list.size() > size){
			Genetic last=list.last();
			list.remove(last);	
		}
	//	display();
	}
	
	public void display() {
		System.out.println(" -------------------- POOL -----------------");
		for (Genetic g:list){
			System.out.println(g.fitness);
		}
	}


	public Genetic getBest() {
		
		return  list.first();
	}


}
