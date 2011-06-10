package speech.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
	private boolean clear;
	private Image offScreenImage;
	private Dimension screenSize;
	private Graphics offScreenGraphics;
	private AffineTransform at;
	private int nextX = 0;

	int in_value_last[];
	String[] phonemes;

	KeyHandler keyHandler;
	private int offScreenWidth;
	int gleft = 40;
	private int baseLine;
	private int maxH;
	private int topY;

	public DrawGraph(String[] phonemeNames) {
		this.phonemes = phonemeNames;

		scrollTransparent = false;
		at = new AffineTransform();
		in_value_last = new int[phonemes.length];
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
			nextX = gleft;
			// Draw comments on screen
			offScreenGraphics.drawString(
					"Pause (Space) Clear (RET) Faster (+),Slower (-) ", 10, 15);

			topY = 20;

			baseLine = getSize().height;
			maxH = (getSize().height - topY) / phonemes.length;

			for (int i = 0; i < phonemes.length; i++) {
				int yBase = (int) (baseLine - maxH * i);
				offScreenGraphics.setColor(Color.BLACK);
				offScreenGraphics
						.drawString(phonemes[i], 0, (int) (yBase - 10));
				offScreenGraphics.drawLine(0, yBase, gleft, yBase);
			}
		}

		if (!scrollPause) {

			if (nextX == gleft) {
				offScreenGraphics.clearRect(gleft, topY, offScreenWidth,
						screenSize.height - topY);
				dirtX = 0;
				dirtW = offScreenWidth;
				dirtY = 0;
				dirtH = getSize().height;
				for (int i = 0; i < phonemes.length; i++) {
					int yBase = (int) (baseLine - maxH * i);
					offScreenGraphics.setColor(Color.BLACK);
					offScreenGraphics.drawString(phonemes[i], 0,
							(int) (yBase - 10));
					offScreenGraphics.drawLine(0, yBase, offScreenWidth, yBase);
				}

			} else {
				dirtX = nextX;
				dirtW = scrollSpeed;
				dirtY = 0;
				dirtH = getSize().height;
			}

			for (int i = 0; i < phonemes.length; i++) {

				Color col = cols[i];

				int yBase = (int) (baseLine - maxH * i);
				int y1 = (int) (in_values[i] * maxH);

				// clear segment
				offScreenGraphics.setColor(col);
				offScreenGraphics.fillRect(nextX, yBase - y1, scrollSpeed, y1);

			}
			nextX += scrollSpeed;
			if (nextX + scrollSpeed > offScreenWidth) {
				nextX = gleft;
			}
			repaint(dirtX, dirtY, dirtW, dirtH);
		} 
		
		if (clear) {
			
				nextX = gleft;
				clear=false;
		}

	}

	class KeyHandler extends KeyAdapter {

	

		public void keyReleased(KeyEvent e) {

			// System.out.println(" KeyHit ZZZZZ");
			int kCode = e.getKeyCode();

			if (kCode == KeyEvent.VK_SPACE) {
				if (scrollPause)
					scrollPause = false;
				else
					scrollPause = true;
			}

			if (kCode == KeyEvent.VK_ENTER) {
				clear = true;
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
