package speech.spectral;

import java.io.IOException;
import java.util.List;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.core.AudioProcess;
import uk.org.toot.audio.server.AudioClient;
import uk.org.toot.audio.server.IOAudioProcess;
import uk.org.toot.audio.server.JavaSoundAudioServer;

import com.frinika.audio.io.AudioReader;
import com.frinika.audio.io.AudioWriter;

import config.Config;

public class AndroidRealTimeAudioSource implements RealTimeAudioSource{

	private  AndroidAudioServer audioServer;
	private  AudioBuffer chunk;
	private  AudioClient audioClient;
	
	private AudioReader reader;

	private boolean eof=false;
	private AudioWriter recorder;
	
	//public static SpectralClient client;
	public static double spectrum[];

	public AndroidRealTimeAudioSource() {
		
		
	}

	public void startAudio(final AudioProcess client)
			throws Exception {

		// Setup audio server
	
		
		audioServer = new AndroidAudioServer();
		
	
		chunk = audioServer.createAudioBuffer("default");
		chunk.setRealTime(true);
		final AudioBuffer chunkOut = audioServer.createAudioBuffer("default");
		chunkOut.setRealTime(true);
		chunkOut.makeSilence();
		
		audioClient = new AudioClient() {
			
			public void work(int arg0) {
				chunk.makeSilence();
				if (reader != null) {
					reader.processAudio(chunk);
					
					if (!eof) {
						if (reader.eof()) {
							eof=true;
						}
					}
				} else {
					input.processAudio(chunk);
				}
				
				
				try {
					client.processAudio(chunk);
					
					output.processAudio(chunk);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			public void setEnabled(boolean arg0) {
			}
		};

		audioServer.setClient(audioClient);
		audioServer.start();

		// shutdown hook to close the audio devices.
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {

				System.out.println("Stop...");
				audioServer.stop();

			}
		});
	}

	public void record(AudioWriter recorder){
		this.recorder=recorder;
	}
	
	public void streamFile(AudioReader audioReader) {
		reader=audioReader;
		eof=false;
		if (reader == null) return;
		
		try {
			reader.seekFrame(0, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * returns true if streaming from file and we are at the end. 
	 */
	public boolean isEOF() {
		
		return eof;
	}

}