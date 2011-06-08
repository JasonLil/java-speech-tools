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

	private boolean isApplet;

	DrawTract drawTract;
	DrawTract drawTargTract;
	DrawLips drawLips;
	DrawLips drawTargLips;
	AnalyserPanel meterPanel;
	DrawGraph drawGraph;
	public DrawScrollingSpect drawScroll;
	DrawHist drawHist;
	private JFrame specFrame;
	private JFrame graphFrame;
	private JFrame masterFrame;

	private int onscreenBins;

	double targetNeuralOutputs[] = new double[6];
	String targetText = "";

	public SpectralProcess spectralProcess = new SpectralProcess() {

		@Override
		public void notifyMoreDataReady(double[] smoothed) {
			if (drawScroll != null) {
				drawScroll.notifyMoreDataReady(smoothed);
			}
		}
	};

	private int phonemes;

	private KeyHandler keyHandler;

	private String [] phonemeNames;

	public MakeFrames(boolean isApplet, String[] phonemeNames, int onscreenBins)
			throws IOException {
		this.phonemeNames=phonemeNames;
		this.phonemes=phonemeNames.length;
		this.isApplet = isApplet;
		final ReadImage ri = new ReadImage(phonemeNames);
		drawTract = new DrawTract(phonemes, ri);
		drawTargTract = new DrawTract(phonemes, ri);
		drawLips = new DrawLips(phonemes, ri);
		drawTargLips = new DrawLips(phonemes, ri);
		this.onscreenBins = onscreenBins;

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

	public void updateGfx(String text, double[] neuralOutputs, double[] magn) {

		drawTract.vectorMean(neuralOutputs, text);
		drawLips.vectorMean(neuralOutputs);

		if (drawGraph != null) {
			drawGraph.updateGraph(neuralOutputs, text);
			// graphFrame.repaint();
		}

		if (specFrame != null) {
			drawHist.update(magn);
			specFrame.repaint();
		}

		masterFrame.repaint();
		drawTargTract.vectorMean(targetNeuralOutputs, targetText);
		drawTargLips.vectorMean(targetNeuralOutputs);
	}

	// private void addComponent(Container container, Component c, int x, int y,
	// int width, int height) {
	// c.setBounds(x, y, width, height);
	// container.add(c);
	// }

	class KeyHandler extends KeyAdapter {

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
}