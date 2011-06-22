package test;

import java.util.Iterator;

import uk.ac.bath.ai.backprop.TrainingData;

public class TestData  implements Iterable<TrainingData> {
	
	static TrainingData TD[];
	
	static {
		double out[][] = { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 0, 0 }, { 1, 1 },
			{ 0, 1 }, { 0, 1 }, { 1, 1 } };

	// prepare test data
		double in[][] = { { 0, 0, 0 }, { 0, 0, 1 }, { 0, 1, 0 },
			{ 0, 1, 1 }, { 1, 0, 0 }, { 1, 0, 1 }, { 1, 1, 0 }, { 1, 1, 1 } };
	
		TD=new TrainingData[out.length];
		
		for(int i=0;i<TD.length;i++){
			TD[i]=new TrainingData(in[i],out[i]);
		}
	
	}
	
	
	
	static int n=TD.length;
	
	//double target[][] = { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 0, 0 }, { 1, 1 },
	//		{ 0, 1 }, { 0, 1 }, { 1, 1 } };

	// prepare test data
	//double testData[][] = { { 0, 0, 0 }, { 0, 0, 1 }, { 0, 1, 0 },
	//		{ 0, 1, 1 }, { 1, 0, 0 }, { 1, 0, 1 }, { 1, 1, 0 }, { 1, 1, 1 } };

	
	
	class TestDataIterator implements Iterator<TrainingData> {
		
	
		int cnt=0;
		
		public TestDataIterator() {
			cnt=0;	
		}
		
		public TrainingData next() {
		
			if (cnt >= n) return null;
			TrainingData td=TD[cnt];
			cnt++;
			return td;
			
		}

		@Override
		public boolean hasNext() {
			return cnt<n;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}

	@Override
	public Iterator<TrainingData> iterator() {
		return new TestDataIterator();
	}

}
