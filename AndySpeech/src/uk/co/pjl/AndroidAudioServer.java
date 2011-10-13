package uk.co.pjl;


import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Handler;

public class AndroidAudioServer {
	private final int sampleRate = 8000;

	int idx = 0;
	private RawClient client;
	boolean isBigendian=false;
	
	AndroidAudioServer(RawClient client){
		this.client=client;
		new AudioIn();
	}
	
	
	private class AudioIn extends Thread {
		private boolean stopped = false;

		private AudioIn() {
		
			android.os.Process
					.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			start();
		}

		@Override
		public void run() {
		
			AudioRecord recorder = null;
			short[][] buffers = new short[256][160];
			int ix = 0;

			try { // ... initialise

				int N = AudioRecord.getMinBufferSize(sampleRate,
						AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT);

				recorder = new AudioRecord(AudioSource.MIC, sampleRate,
						AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT, N * 1);

				recorder.startRecording();

			//	audioTrack.write(generatedSnd, 0, generatedSnd.length);		
				
				// ... loop

				while (!stopped) {
					short[] buffer = buffers[ix++ % buffers.length];

					N = recorder.read(buffer, 0, buffer.length);

					
					process(buffer);
				}
			} catch (Throwable x) {
				// log.warning(TAG,"Error reading voice audio",x);
			} finally {
				close();
			}
		}

		private void process(short buffer[]){
			
			
		}
		
		private void close() {
			stopped = true;
		}

	}
}