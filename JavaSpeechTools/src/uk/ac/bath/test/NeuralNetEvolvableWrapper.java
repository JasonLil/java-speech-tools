package uk.ac.bath.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import speech.NeuralNet;
import uk.ac.bath.ai.backprop.BackProp;
import uk.ac.bath.ai.backprop.TrainingData;

public class NeuralNetEvolvableWrapper {

	
	private TestData data;
	private NeuralNet net;


	public NeuralNetEvolvableWrapper(TestData data,NeuralNet net) {
		this.data=data;
		this.net=net;
	}
	
	public void randomGuess(){
		File file = new File("PJLBackprop.net");

//		
//			FileInputStream ostr;
//			try {
//				ostr = new FileInputStream(file);
//			
//			ObjectInputStream in = new ObjectInputStream(ostr);
//			net=(BackProp)in.readObject();
//			in.close();
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.out.println(" Loaded from file .......... ");
		
		
			net.randomWeights(-.1,.1);	
	}
	 
	public double fitness() {
		double maxError = 0;

		
		
		for (TrainingData d :data){
			
			double out[]= net.forwardPass(d.in);
			double err=0;
			for (int i=0;i<out.length;i++){
				err += Math.sqrt((d.out[i]-out[i])*(d.out[i]-out[i]));	
				maxError = Math.max(err, maxError);
			}
		}
		return maxError;
	}

	
}
