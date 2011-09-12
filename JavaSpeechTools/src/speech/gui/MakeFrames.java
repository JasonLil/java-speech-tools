package speech.gui;

import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.filechooser.FileFilter;

import speech.Data;
import speech.monopthong.MainApp;
import speech.spectral.SpectralProcess;
import config.Config;

// addComponent function from http://www.java-forums.org/

public class MakeFrames {

	private DrawTract drawTract;
	//private DrawTract drawTargTract;
	private DrawLips drawLips;
	//private DrawLips drawTargLips;
	public DrawGraph drawGraph;
	private DrawScrollingSpect drawScroll;
	private DrawHist drawHist;

	private int nPhonemes;
	private String[] phonemeNames;

	private JFrame specFrame;
	private JFrame graphFrame;
	private JFrame masterFrame;

	private int onscreenBins;
//	private KeyHandler keyHandler;
	File defaultWavFile = Config.defaultWaveFile;

	

	// this is used to feed anything that needs to process each feature
	// this will be on the realtime audio thread so any observers should take
	// care not
	// to call any GUI stuff from notifyMoreData.

	private SpectralProcess spectralProcess = new SpectralProcess() {

		@Override
		public void notifyMoreDataReady(Data data) {
			if (drawScroll != null) {
				drawScroll.notifyMoreDataReady(data.feature);
				if (specFrame != null) {
					drawHist.update(data.feature);
					drawHist.repaint();
				}
			}
		}
	};

	private MainApp app;
	private Config config;
	public Rectangle windowSize;
	private ReadImage ri;
	
	void calcScreenSize() {
		

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		GraphicsEnvironment ge = java.awt.GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice()
				.getDefaultConfiguration();
//		if (gc == null)
//			gc = getGraphicsConfiguration();

		if (gc != null) {
			windowSize = gc.getBounds();
		} else {
			windowSize = new java.awt.Rectangle(toolkit.getScreenSize());
		}
	}

	public MakeFrames(boolean isApplet, Config config, MainApp app)
			throws IOException {
		calcScreenSize();
		this.phonemeNames = config.getOutputNames(); // phonemeNames;
		this.nPhonemes = phonemeNames.length;
		this.config = config;
		this.app = app;
		ri = new ReadImage(config);
		drawTract = new DrawTract(nPhonemes, ri);
		//drawTargTract = new DrawTract(nPhonemes, ri);
		drawLips = new DrawLips(nPhonemes, ri);
		//drawTargLips = new DrawLips(nPhonemes, ri);
		this.onscreenBins = config.getFeatureVectorSize();
		

	}

	public JFrame makeExamples() throws IOException {

		JFrame frame = new JFrame("Example phonemes");

		Container content = frame.getContentPane();
		frame.setLayout(new GridLayout(config.getNumberOfTargets(),2));
		// For training
		double targetNeuralOutputs[];
		String targetText = "";
		for (int i=0;i<nPhonemes;i++){
			targetNeuralOutputs = new double[nPhonemes];
			targetNeuralOutputs[0] = 1.0;
			targetText = "EEE";
			targetText = phonemeNames[i];
//			for (int ii = 0; ii < nPhonemes; ii++)
//				targetNeuralOutputs[ii] = 0;
//			targetNeuralOutputs[i] = 1.0;
			DrawTract drawTargTract = new DrawTract(nPhonemes, ri);
			DrawLips drawTargLips = new DrawLips(nPhonemes, ri);
			content.add(drawTargLips);
			content.add(drawTargTract);
			drawTargTract.vector(i, targetText);
			drawTargLips.vector(i);
		}
		
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
		frame.pack();
		return frame;
		
	}
	
	public JFrame makeMaster() {

		JFrame frame = new JFrame("JR Speech Analysis Toolbox");

		Container content = frame.getContentPane();
		frame.setLayout(new GridLayout(1, 2));
//		keyHandler = new KeyHandler();
//		frame.addKeyListener(keyHandler);

		content.add(drawLips);
		content.add(drawTract);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
		masterFrame = frame;
		makeMenus();
		frame.pack();
		return frame;
	}

	void makeMenus() {

		JMenuBar bar = new JMenuBar();
		masterFrame.setJMenuBar(bar);

		bar.add(makeFileMenu());

		bar.add(makeSourceMenu());

		bar.add(makeAnalysisMenu());
		bar.add(makeSettingMenu());
	//	bar.add(makeTrainingMenu());

	}

	JMenu makeSettingMenu() {
		JMenu menu = new JMenu("Setting");
		JMenu overLapMenu = new JMenu("Window Overlap");
		menu.add(overLapMenu);
		ButtonGroup group = new ButtonGroup();
		int fftSize = config.getFFTSize();

		for (int i = 0; i < 4; i++) {

			final int sampsOverLap = (fftSize * i) / 4;
			String percent = (100 * sampsOverLap) / fftSize + "%";
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(
					new AbstractAction(percent) {

						@Override
						public void actionPerformed(ActionEvent e) {
							app.spectralConverter.setOverlap(sampsOverLap);
						}
					});
			overLapMenu.add(item);
			group.add(item);
			if (i == 0)
				item.setSelected(true);
		}
		return menu;
	}

	JMenu makeFileMenu() {
		JMenu menu = new JMenu("File");

		menu.add(new JMenuItem(new AbstractAction("Quit") {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}));
		return menu;
	}

	JMenu makeSourceMenu() {
		JMenu menu = new JMenu("Source");

		menu.add(new JMenuItem(new AbstractAction("file") {

			@Override
			public void actionPerformed(ActionEvent e) {
				File wav = selectWaveFile();
				if (wav == null)
					return;
				app.setInputWave(wav);
			}
		}));

		menu.add(new JMenuItem(new AbstractAction("mic") {

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setInputWave(null);
			}
		}));
		return menu;
	}

	public JFrame makeSpectrogramFrame() {
		final JFrame frame = new JFrame("Spectrogram");
		frame.setLayout(null);
		drawScroll = new DrawScrollingSpect();
		drawHist = new DrawHist(onscreenBins);

		frame.add(drawScroll);
		frame.add(drawHist);

		frame.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				int h=frame.getHeight();
				int w=frame.getWidth();

				int barW=Math.min(50,w/2);
				
				drawScroll.setLocation(0, 0);
				drawScroll.setSize(w-barW, h);
				drawHist.setLocation(w-barW, 0);
				drawHist.setSize(barW, h);
				
				System.out.println(" HELLO "+ w+" "+h);
				frame.repaint();
				
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		//frame.setSize(680, 400);
		//frame.setVisible(true);

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
		
		return frame;
	}

	public JFrame makephoneGraph() {
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
		return frame;
	}

	JMenu makeAnalysisMenu() {
		JMenu menu = new JMenu("Analyis");

		menu.add(new JMenuItem(new AbstractAction("Spectrogram") {

			@Override
			public void actionPerformed(ActionEvent e) {
				makeSpectrogramFrame();
			}
		}));

		menu.add(new JMenuItem(new AbstractAction("PhonemeGraph") {

			@Override
			public void actionPerformed(ActionEvent e) {
				makephoneGraph();

			}
		}));

		return menu;
	}

//	JMenu makeTrainingMenu() {
//		JMenu menu = new JMenu("Training");
//
//		for (int i = 0; i < phonemeNames.length; i++) {
//
//			final int ii = i;
//
//			menu.add(new JMenuItem(new AbstractAction(phonemeNames[i]) {
//
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					targetText = phonemeNames[ii];
//					for (int i = 0; i < 6; i++)
//						targetNeuralOutputs[i] = 0;
//					targetNeuralOutputs[ii] = 1.0;
//				}
//			}));
//
//		}
//		return menu;
//	}

	public void updateGfx(String text, double[] neuralOutputs) { // , double[]
																	// magn) {

		if (!(masterFrame.getExtendedState() == JFrame.ICONIFIED)) {
			drawTract.vectorMean(neuralOutputs, text);
			drawLips.vectorMean(neuralOutputs);
		
			masterFrame.repaint();
		}

		if (drawGraph != null) {
			// drawGraph.updateGraph(neuralOutputs, text);
			// graphFrame.repaint();
		}

	}

	public void resetGraphs() {
		if (drawGraph != null) {
			drawGraph.reset();
			// graphFrame.repaint();
		}
		if (drawScroll != null) {
			drawScroll.reset();
		}

	}

	public void pauseGraphs(boolean yes) {
		if (drawGraph != null) {
			drawGraph.pause(yes);
			// graphFrame.repaint();
		}
		if (drawScroll != null) {
			drawScroll.pause(yes);
		}

	}

//	private class KeyHandler extends KeyAdapter {
//
//		public void keyReleased(KeyEvent e) {
//
//			int kCode = e.getKeyCode();
//			int n = phonemeNames.length;
//			if (kCode == KeyEvent.VK_A) {
//				for (int i = 0; i < n; i++)
//					targetNeuralOutputs[i] = 0;
//				targetNeuralOutputs[0] = 1.0;
//				targetText = "EEE";
//			}
//
//			if (kCode == KeyEvent.VK_S) {
//				for (int i = 0; i < n; i++)
//					targetNeuralOutputs[i] = 0;
//				targetNeuralOutputs[1] = 1.0;
//				targetText = "EHH";
//			}
//
//			if (kCode == KeyEvent.VK_D) {
//				for (int i = 0; i < n; i++)
//					targetNeuralOutputs[i] = 0;
//				targetNeuralOutputs[2] = 1.0;
//				targetText = "ERR";
//			}
//
//			if (kCode == KeyEvent.VK_F) {
//				for (int i = 0; i < n; i++)
//					targetNeuralOutputs[i] = 0;
//				targetNeuralOutputs[3] = 1.0;
//				targetText = "AHH";
//			}
//
//			if (kCode == KeyEvent.VK_G) {
//				for (int i = 0; i < n; i++)
//					targetNeuralOutputs[i] = 0;
//				targetNeuralOutputs[4] = 1.0;
//				targetText = "OOH";
//			}
//
//			if (kCode == KeyEvent.VK_H) {
//				for (int i = 0; i < n; i++)
//					targetNeuralOutputs[i] = 0;
//				targetNeuralOutputs[5] = 1.0;
//				targetText = "UHH";
//			}
//
//		}
//	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if (f.getName().toLowerCase().indexOf(".wav") > 0 || f.isDirectory())
			return true;
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return "Wave file";
	}

}
