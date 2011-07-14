package poetry;

import java.util.ArrayList;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;

public class MyMemory {
	ArrayList<float[]> memory = new ArrayList<float[]>();
	private int memPtr;
	private int fftSize;
	private float sampleRate;

	MyMemory(int maxDelay, int fftSize, float sampleRate) {
		float chunkDT = fftSize / sampleRate;

		int nMem = (int) (maxDelay / chunkDT);

		for (int i = 0; i < nMem; i++) {
			memory.add(new float[fftSize]);
		}

		memPtr = 0;
		this.fftSize=fftSize;
		this.sampleRate=sampleRate;
	}

	void process(float in[]) {
		System.arraycopy(in, 0, memory.get(memPtr), 0, fftSize);
		memPtr++;
		if (memPtr >= memory.size())
			memPtr = 0;
	}
	
	
	class Leacher implements AudioProcess {

		
		private int delayInSamples;
		private int ptr;
		private int ptrBit;

		Leacher(float delay) {
			delayInSamples=(int) (delay/sampleRate);
			ptr=(fftSize-1+delayInSamples)/fftSize;
			ptrBit=fftSize - delayInSamples%fftSize;
			
		}
		@Override
		public void open() throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int processAudio(AudioBuffer buffer) {
			int n=buffer.getSampleCount();
			
			int nInBuffer=fftSize-ptrBit;
			
			if (n<= nInBuffer) {
				
				
			}
			
		}

		@Override
		public void close() throws Exception {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
