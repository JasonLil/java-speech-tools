package speech.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

public class DrawGraph extends JPanel {

	/**
	 * 
	 */

	Color cols[] = { new Color(20, 128, 20), new Color(180, 40, 40),
			new Color(255, 130, 71), new Color(250, 20, 128),
			new Color(20, 20, 128), new Color(215, 215, 40) };
	private static final long serialVersionUID = 1L;
	boolean scrollPause = false;
	boolean scrollTransparent = false;
	int scrollSpeed = 15;

	private Image offScreenImage;
	private Dimension screenSize;
	private Graphics offScreenGraphics;
	private AffineTransform at;
	private int nextX = 0;

	int in_value_last[], phonemes;

	int silentCount = 0;
	double silentThresh = 0.2;
	double silentStop = 50;
	KeyHandler keyHandler;
	private int offScreenWidth;
	private int baseLine;

	public DrawGraph(int phonemes) {
		this.phonemes = phonemes;

		scrollTransparent = false;
		at = new AffineTransform();
		in_value_last = new int[phonemes];
		keyHandler = new KeyHandler();
		// setPreferredSize(new Dimension(600,200));
		setDoubleBuffered(false);
	}

	@Override
	public void paint(Graphics g) {
		// super.paint(g);

		// System.out.println(" PAINT" );
		if (offScreenImage != null) {
			g.drawImage(offScreenImage, 0, 0, null);
		}

		// Draw comments on screen
		g.drawString("Pause (Space) Faster (+),Slower (-) Transparent (T)", 10,
				15);
		g.drawString("Playback Speed: " + scrollSpeed, 10, 35);

		if (scrollTransparent)
			g.drawString("Transparent On", 10, 50);
		else
			g.drawString("Transparent Off", 10, 50);

	}

	public void updateGraph(double[] in_values, String text) {

		int dirtX, dirtW, dirtY, dirtH;

		if (offScreenGraphics == null || !getSize().equals(screenSize)) {
			if (getSize().width == 0)
				return;
			screenSize = new Dimension(getSize());
			offScreenWidth = getSize().width;
			offScreenImage = createImage(offScreenWidth, getSize().height);
			offScreenGraphics = offScreenImage.getGraphics();
		//	System.out.println("I just made some gfx");
			nextX = 0;
		}

		boolean silent = true;

		int order[] = { 0, 1, 2, 3, 4, 5 };

		for (int top = 0; top < 6 - 1; top++) {
			for (int cnt = top + 1; cnt < 6; cnt++) {
				if (in_values[order[cnt]] < in_values[order[top]]) {
					int cnt1 = order[cnt];
					double val1 = in_values[order[cnt]];
					in_values[order[cnt]] = in_values[order[top]];
					in_values[order[top]] = val1;
					order[cnt] = order[top];
					order[top] = cnt1;
				}

			}
		}

		for (int i = 0; i < 6; i++) {
			if (in_values[i] > silentThresh) {
				silent = false;
				silentCount = 0;
			}
		}

		if (silent)
			silentCount++;

		if (!scrollPause) { // && silentCount < silentThresh) {

			((Graphics2D) offScreenGraphics).setComposite(AlphaComposite
					.getInstance(AlphaComposite.SRC_ATOP, 1.0f));

			// offScreenGraphics.copyArea(0, 0, screenSize.width - scrollSpeed,
			// screenSize.height, scrollSpeed, 0);

			// offScreenGraphics.clearRect(0, 0, scrollSpeed,
			// screenSize.height);

			if (nextX == 0) {
				offScreenGraphics.clearRect(0, 0, offScreenWidth,
						screenSize.height);
				dirtX = 0;
				dirtW = offScreenWidth;
				dirtY = 0;
				dirtH = getSize().height;

			} else {
				dirtX = nextX;
				dirtW = scrollSpeed;
				dirtY = 0;
				dirtH = getSize().height;

			}

			int baseLine = getSize().height;
			float maxH = getSize().height - 100;

			for (int ii = 0; ii < 6; ii++) {
				int i = order[ii];
				Color col = cols[i];
				if (offScreenGraphics != null) {
					int y1 = (int) (in_values[i] * maxH);
					int y2 = in_value_last[i];

					// If we want it transparent, make it transparent
					if (scrollTransparent) {
						((Graphics2D) offScreenGraphics)
								.setComposite(AlphaComposite.getInstance(
										AlphaComposite.SRC_ATOP, 0.5f));
					} else {
						((Graphics2D) offScreenGraphics)
								.setComposite(AlphaComposite.getInstance(
										AlphaComposite.SRC_ATOP, 1.0f));
					}

					// int x_singlePolygon[] = {nextX, scrollSpeed+nextX,
					// scrollSpeed+nextX, nextX};
					// int y_singlePolygon[] = {400, 400, y1, y1};
					// // offScreenGraphics.fillPolygon(x_singlePolygon,
					// y_singlePolygon, 4);

					offScreenGraphics.setColor(col);
					offScreenGraphics.fillRect(nextX, baseLine - y1,
							scrollSpeed, y1);

					// Draw a line around the polygon to give it definition,
					// if we have transparent on then make it a thin line
					offScreenGraphics.setColor(Color.BLACK);

					if (scrollTransparent)
						((Graphics2D) offScreenGraphics)
								.setStroke(new BasicStroke(0.3f));
					else
						((Graphics2D) offScreenGraphics)
								.setStroke(new BasicStroke(1.0f));

					// offScreenGraphics.drawPolygon(x_singlePolygon,
					// y_singlePolygon, 4);

					offScreenGraphics.setColor(Color.BLACK);

					// at.setToRotation(-Math.PI / 2.0, 1.0, 1.0);
					at.setToRotation(-Math.PI / 2.0, nextX, 1.0);

					((Graphics2D) offScreenGraphics).setTransform(at);

					offScreenGraphics.drawString(text, -90 + nextX, 10);

					// at.setToRotation(0.0, 1.0, 1.0);
					at.setToRotation(0.0, nextX, 1.0);
					((Graphics2D) offScreenGraphics).setTransform(at);

					in_value_last[i] = y1; // (int) ((int) 500 * (1.2 -
											// in_values[i]));

				}

			}
			nextX += scrollSpeed;
			if (nextX + scrollSpeed > offScreenWidth) {
				nextX = 0;
			}
			repaint(dirtX,dirtY,dirtW,dirtH);		
		} else {
			nextX=0;
		}
	
	}

	class KeyHandler extends KeyAdapter {

		public void keyReleased(KeyEvent e) {

			//System.out.println(" KeyHit ZZZZZ");
			int kCode = e.getKeyCode();

			if (kCode == KeyEvent.VK_SPACE) {
				if (scrollPause)
					scrollPause = false;
				else
					scrollPause = true;
			}

			if (kCode == KeyEvent.VK_EQUALS || kCode == KeyEvent.VK_PLUS) {
				scrollSpeed += 5;
			}

			if (kCode == KeyEvent.VK_MINUS) {
				scrollSpeed -= 5;
			}

			if (kCode == KeyEvent.VK_T) {
				if (scrollTransparent)
					scrollTransparent = false;
				else
					scrollTransparent = true;
			}

		}
	}

}
