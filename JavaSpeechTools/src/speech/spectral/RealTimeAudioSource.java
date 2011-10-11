package speech.spectral;

import uk.org.toot.audio.core.AudioProcess;

import com.frinika.audio.io.AudioReader;


public interface RealTimeAudioSource {
	void streamFile(AudioReader audioReader);	
	boolean isEOF();
	void startAudio(AudioProcess spectralConverter) throws Exception;

}