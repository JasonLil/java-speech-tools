package speech.gui;

import java.awt.*;

import javax.swing.*;

//
//@author JER
//
/*
 * Paints a vocal tract onscreen, the position of which can be updated
 *  by calling vectorMean
 */

public class DrawHist2 extends DrawHist   {

	int onscreenBins;
	double magn[];
//	int xpoints[] = {0, 320, 320, 0};
//	int ypoints[] = {0, 0, 400, 400};

	
	public DrawHist2(int onscreenBins) {
		this.onscreenBins = onscreenBins;
		magn = new double[onscreenBins];
		setBackground(Color.black);
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		java.awt.Color rgb = new java.awt.Color(0, 0, 255);
		g2.setColor(rgb);
		
//		g2.fillPolygon(xpoints, ypoints, 4);
//		g2.setStroke(new BasicStroke( 2.0f ));
		float scale=((float)getWidth())/onscreenBins;
		int w=getWidth();
		int h=getHeight();
		for (int i = 0; i < onscreenBins; i++) {
			int x=(int) (w-i*scale);
			int red = (int)(magn[i]*200);
			int green = (int)(magn[i]*200);
			//if (red>255) 
			red=255;
			if (green>255) green=255;
			rgb = new java.awt.Color(red, green, 255-green);
			g2.setColor(rgb);
			int hh=Math.max(0,h-(int)(magn[i]*100));
			g2.drawLine(x, h, x,hh);
		}

	}

	public void update(double[] magnIn) {
		int j = onscreenBins - 1;
		for (int i = 0; i < onscreenBins; i++) {
			magn[i] = Math.log10(magnIn[j]+1);
			j--;
		}
	//	repaint();
	}
}