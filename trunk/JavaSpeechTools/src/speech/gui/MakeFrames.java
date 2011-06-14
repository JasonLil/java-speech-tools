package speech.gui;

import static com.frinika.localization.CurrentLocale.getMessage;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import com.frinika.global.FrinikaConfig;
import com.frinika.project.ProjectContainer;
import com.frinika.project.gui.ProjectFrame;
import com.frinika.tracker.ProjectFileFilter;

import config.Config;

import speech.MainApp;
import speech.gui.DrawGraph.KeyHandler;
import speech.spectral.SpectralProcess;

// addComponent function from http://www.java-forums.org/

public class MakeFrames {

	private DrawTract drawTract;
	private DrawTract drawTargTract;
	private DrawLips drawLips;
	private DrawLips drawTargLips;
	private DrawGraph drawGraph;
	private DrawScrollingSpect drawScroll;
	private DrawHist drawHist;

	private int nPhonemes;
	private String[] phonemeNames;

	private JFrame specFrame;
	private JFrame graphFrame;
	private JFrame masterFrame;

	private int onscreenBins;
	private KeyHandler keyHandler;
	File defaultWavFile=Config.defaultWaveFile;

	// For training
	private double targetNeuralOutputs[];
	private String targetText = "";

	// this is used to feed anything that needs to process each feature
	// this will be on the realtime audio thread so any observers should take
	// care not
	// to call any GUI stuff from notifyMoreData.

	private SpectralProcess spectralProcess = new SpectralProcess() {

		@Override
		public void notifyMoreDataReady(double[] data) {
			if (drawScroll != null) {
				drawScroll.notifyMoreDataReady(data);
				if (specFrame != null) {
					drawHist.update(data);
					drawHist.repaint();
				}
			}
		}
	};
	private File waveDiretory;
	private MainApp app;

	public MakeFrames(boolean isApplet, String[] phonemeNames, int onscreenBins,MainApp app)
			throws IOException {
		this.phonemeNames = phonemeNames;
		this.nPhonemes = phonemeNames.length;
		this.app=app;
		final ReadImage ri = new ReadImage(phonemeNames);
		drawTract = new DrawTract(nPhonemes, ri);
		drawTargTract = new DrawTract(nPhonemes, ri);
		drawLips = new DrawLips(nPhonemes, ri);
		drawTargLips = new DrawLips(nPhonemes, ri);
		this.onscreenBins = onscreenBins;
		targetNeuralOutputs = new double[nPhonemes];
		targetNeuralOutputs[0] = 1.0;
		targetText = "EEE";

	}

	public void makeMaster() {

		JFrame frame = new JFrame("JR Speech Analysis Toolbox");

		Container content = frame.getContentPane();
		frame.setLayout(new GridLayout(2, 2));
		keyHandler = new KeyHandler();
		frame.addKeyListener(keyHandler);

		content.add(drawLips);
		content.add(drawTargLips);
		content.add(drawTract);
		content.add(drawTargTract);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
		masterFrame = frame;
		makeMenus();
		frame.pack();
	}

	void makeMenus() {

		JMenuBar bar = new JMenuBar();
		masterFrame.setJMenuBar(bar);

		JMenu menu = new JMenu("File");

		bar.add(menu);

		
		
		menu.add(new JMenuItem(new AbstractAction("Quit") {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}));

		
		menu=new JMenu("Source");
		bar.add(menu);
		
		menu.add(new JMenuItem(new AbstractAction("file") {

			@Override
			public void actionPerformed(ActionEvent e) {
				File wav=selectWaveFile();
				if (wav == null) return;
				app.setInputWave(wav);
			}
		}));

		

		menu.add(new JMenuItem(new AbstractAction("mic") {

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setInputWave(null);
			}
		}));

		
		menu = new JMenu("Analyis");

		bar.add(menu);
		menu.add(new JMenuItem(new AbstractAction("Spectrogram") {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame("Spectrogram");
				frame.setLayout(null);
				drawScroll = new DrawScrollingSpect();
				drawHist = new DrawHist(onscreenBins);

				frame.add(drawScroll);
				frame.add(drawHist);

				drawScroll.setBounds(0, 0, 480, 400);
				drawHist.setBounds(480, 0, 200, 400);

				frame.setSize(680, 400);
				frame.setVisible(true);

				specFrame = frame;
				specFrame.addWindowListener(new WindowListener() {

					@Override
					public void windowOpened(WindowEvent e) {
					}

					@Override
					public void windowIconified(WindowEvent e) {
					}

					@Override
					public void windowDeiconified(WindowEvent e) {
					}

					@Override
					public void windowDeactivated(WindowEvent e) {
					}

					@Override
					public void windowClosing(WindowEvent e) {
						System.out.println(" CLOSING");
						drawHist = null;
						drawScroll = null;
						specFrame = null;
					}

					@Override
					public void windowClosed(WindowEvent e) {
					}

					@Override
					public void windowActivated(WindowEvent e) {
					}
				});

			}
		}));

		menu.add(new JMenuItem(new AbstractAction("PhonemeGraph") {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame("Phoneme Classification");
				// frame.setLayout(null);
				drawGraph = new DrawGraph(phonemeNames);
				drawGraph.setBounds(0, 0, 680, 400);
				frame.add(drawGraph);
				frame.addKeyListener(drawGraph.keyHandler);
				frame.setSize(680, 400);
				frame.setVisible(true);

				graphFrame = frame;
				graphFrame.addWindowListener(new WindowListener() {

					@Override
					public void windowOpened(WindowEvent e) {
					}

					@Override
					public void windowIconified(WindowEvent e) {
					}

					@Override
					public void windowDeiconified(WindowEvent e) {
					}

					@Override
					public void windowDeactivated(WindowEvent e) {
					}

					@Override
					public void windowClosing(WindowEvent e) {
						System.out.println(" CLOSING");
						drawGraph = null;
						graphFrame = null;
					}

					@Override
					public void windowClosed(WindowEvent e) {
					}

					@Override
					public void windowActivated(WindowEvent e) {
					}
				});

			}
		}));
		menu = new JMenu("Training");
		bar.add(menu);

		for (int i = 0; i < phonemeNames.length; i++) {

			final int ii = i;

			menu.add(new JMenuItem(new AbstractAction(phonemeNames[i]) {

				@Override
				public void actionPerformed(ActionEvent e) {
					targetText = phonemeNames[ii];
					for (int i = 0; i < 6; i++)
						targetNeuralOutputs[i] = 0;
					targetNeuralOutputs[ii] = 1.0;
				}
			}));

		}
		
	
	}

	public void updateGfx(String text, double[] neuralOutputs) { // , double[]
																	// magn) {

		if (!(masterFrame.getExtendedState() == JFrame.ICONIFIED)) {
			drawTract.vectorMean(neuralOutputs, text);
			drawLips.vectorMean(neuralOutputs);
			drawTargTract.vectorMean(targetNeuralOutputs, targetText);
			drawTargLips.vectorMean(targetNeuralOutputs);
			masterFrame.repaint();
		}

		if (drawGraph != null) {
			drawGraph.updateGraph(neuralOutputs, text);
			// graphFrame.repaint();
		}

	}

	private class KeyHandler extends KeyAdapter {

		public void keyReleased(KeyEvent e) {

			int kCode = e.getKeyCode();
			int n = phonemeNames.length;
			if (kCode == KeyEvent.VK_A) {
				for (int i = 0; i < n; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[0] = 1.0;
				targetText = "EEE";
			}

			if (kCode == KeyEvent.VK_S) {
				for (int i = 0; i < n; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[1] = 1.0;
				targetText = "EHH";
			}

			if (kCode == KeyEvent.VK_D) {
				for (int i = 0; i < n; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[2] = 1.0;
				targetText = "ERR";
			}

			if (kCode == KeyEvent.VK_F) {
				for (int i = 0; i < n; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[3] = 1.0;
				targetText = "AHH";
			}

			if (kCode == KeyEvent.VK_G) {
				for (int i = 0; i < n; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[4] = 1.0;
				targetText = "OOH";
			}

			if (kCode == KeyEvent.VK_H) {
				for (int i = 0; i < n; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[5] = 1.0;
				targetText = "UHH";
			}

		}
	}

	JFileChooser chooser = new JFileChooser(defaultWavFile);

	File selectWaveFile() {
		chooser.setDialogTitle("Select audio file");
		chooser.setFileFilter(new AudioFileFilter());
		
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
	
		return null;
	
	}

	public SpectralProcess getSpectralProcess() {
		return spectralProcess;
	}

}


class AudioFileFilter extends FileFilter {

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    public boolean accept(File f) {
        if(f.getName().toLowerCase().indexOf(".wav")>0 || 
                f.isDirectory())
            return true;
        else
            return false;
    }

    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    public String getDescription() {
        return "Wave file";
    }

}
