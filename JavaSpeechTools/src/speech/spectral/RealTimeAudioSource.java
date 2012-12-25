package speech.spectral;

import uk.org.toot.audio.core.AudioProcess;

import com.frinika.audio.io.AudioReader;


/**
 * Sources of raw audio should implement this to supply a AudioProcess (@see Class AudioProcess)
 * 
 * When streaming a file audio should be monitored realtime (mic) it should be muted.
 * 
 * @author pjl
 *
 */
public interface RealTimeAudioSource {
	/***
	 * 
	 * @param audioReader  file to be read (null should switch to real time source).
	 */
	void streamFile(AudioReader audioReader);
	/***
	 * 
	 * @return true if we have hit the end of file.
	 */
	boolean isEOF();
	/**
	 * 
	 * 
	 * @param  cleint
	 * @throws Exception
	 */
	void startAudio(AudioProcess client) throws Exception;

}