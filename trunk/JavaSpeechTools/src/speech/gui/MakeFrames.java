package speech.gui;

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
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

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

	// For training
	private double targetNeuralOutputs[];
	private String targetText = "";

	// this is used to feed anything that needs to process each feature 
	// this will be on the realtime audio thread so any observers should take care not 
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



	public MakeFrames(boolean isApplet, String[] phonemeNames, int onscreenBins)
			throws IOException {
		this.phonemeNames = phonemeNames;
		this.nPhonemes = phonemeNames.length;

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

		// int width=800;
		// int height=600;

		JFrame frame = new JFrame("JR Speech Analysis Toolbox");

		Container content = frame.getContentPane();
		frame.setLayout(new GridBagLayout());
		keyHandler = new KeyHandler();
		frame.addKeyListener(keyHandler);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx=1.0;
		c.weighty=1.0;
		c.gridx = 0;
		c.gridy = 0;
		content.add(drawLips, c);
		c.gridx++;
		content.add(drawTargLips, c);

		c.gridx = 0;
		c.gridy = 1;
		content.add(drawTract, c);
		c.gridx++;
		content.add(drawTargTract, c);

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
				drawGraph = new DrawGraph(6);
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

	public void updateGfx(String text, double[] neuralOutputs) { //, double[] magn) {

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

			if (kCode == KeyEvent.VK_A) {
				for (int i = 0; i < 6; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[0] = 1.0;
				targetText = "EEE";
			}

			if (kCode == KeyEvent.VK_S) {
				for (int i = 0; i < 6; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[1] = 1.0;
				targetText = "EHH";
			}

			if (kCode == KeyEvent.VK_D) {
				for (int i = 0; i < 6; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[2] = 1.0;
				targetText = "ERR";
			}

			if (kCode == KeyEvent.VK_F) {
				for (int i = 0; i < 6; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[3] = 1.0;
				targetText = "AHH";
			}

			if (kCode == KeyEvent.VK_G) {
				for (int i = 0; i < 6; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[4] = 1.0;
				targetText = "OOH";
			}

			if (kCode == KeyEvent.VK_H) {
				for (int i = 0; i < 6; i++)
					targetNeuralOutputs[i] = 0;
				targetNeuralOutputs[5] = 1.0;
				targetText = "UHH";
			}

		}
	}

	public SpectralProcess getSpectralProcess() {
		return spectralProcess;
	}
	
}