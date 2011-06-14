package speech.spectral;

import java.util.List;

import com.frinika.audio.io.AudioReader;

import uk.org.toot.audio.core.AudioBuffer;
import uk.org.toot.audio.server.AudioClient;
import uk.org.toot.audio.server.IOAudioProcess;
import uk.org.toot.audio.server.JavaSoundAudioServer;
import uk.org.toot.audio.server.MultiIOJavaSoundAudioServer;

public class RealTimeSpectralSource {

	private  JavaSoundAudioServer audioServer;
	private  AudioBuffer chunk;
	private  AudioClient audioClient;
	private  SampledToSpectral spectralProcess;
	private AudioReader reader;
	
	//public static SpectralClient client;
	public static double spectrum[];

	public RealTimeSpectralSource(SampledToSpectral spectralProcess) {
		this.spectralProcess = spectralProcess;
	}

	public void startAudio(String inName1, String outName1,
			final int onscreen_bins, final NNSpectralFeatureDetector client)
			throws Exception {

		// Setup audio server

		audioServer = new JavaSoundAudioServer();
		List<String> outputs = audioServer.getAvailableOutputNames();
		System.out.println("Outputs:");

		for (String name : outputs) {
			System.out.println(name);
		}

		List<String> inputs = audioServer.getAvailableInputNames();
		System.out.println("Inputs:");
		for (String name : inputs) {
			System.out.println(name);
		}

		String inName = outputs.get(0);
		String outName = outputs.get(0);

		if (inName1 != null) {
			for (String name : outputs) {
				if (name.equals(outName1)) {
					outName = outName1;
					break;
				}
			}
		}

		if (outName1 != null) {
			for (String name : inputs) {
				if (name.equals(inName1)) {
					inName = inName1;
					break;
				}
			}
		}
		
		System.out.println(" Outputs " + outName);
		System.out.println(" Inputs " + inName);

		final IOAudioProcess output = audioServer.openAudioOutput(outName,"output");
		final IOAudioProcess input = audioServer
				.openAudioInput(inName, "input");

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
					output.processAudio(chunk);
				} else {
					input.processAudio(chunk);
					output.processAudio(chunkOut);
				}
				spectralProcess.processAudio(chunk,client);
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

	public void streamFile(AudioReader audioReader) {
		reader=audioReader;
	}

}