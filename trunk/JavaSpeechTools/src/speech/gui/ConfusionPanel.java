package speech.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.swing.JPanel;

public class ConfusionPanel extends JPanel {
	
	private AffineTransform at=new AffineTransform();
	private List<String> names;
	private double[][] confusion;
	private Color[] color;

	public ConfusionPanel(List<String> names) {
		this.names=names;
		color=new Color[256];
		for(int i=0;i<256;i++) {
			color[i]=new Color(i,i,255-i);
		}
	}
	
	
	public void paint(Graphics g1) {
		super.paint(g1);
		Graphics2D g=(Graphics2D)g1;
		
		if (confusion == null) return;
		int ww=getWidth();
		int hh=getHeight();
		int textW=60;
		int textH=textW;
		int siz=Math.min(ww-textW, hh-textH);
		int n=names.size();
		int dx=siz/names.size();
		
		double maxx=0.0;
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				maxx=Math.max(maxx,confusion[i][j]);
			}
		}
		
		for(int i=0;i<n;i++) {
			g.setColor(Color.black);
			int y1=textH+i*dx;
			
			g.drawRect(0, y1, textW, dx);

			g.drawString(names.get(i), 3, textH+(i+1)*dx-3);
			
			
			at.setToRotation(-Math.PI / 2.0,textH+(i+1)*dx-3,3 );
			g.setTransform(at);
			g.drawString(names.get(i),(i+1)*dx+3,0) ; //,textH+(i+1)*dx-3);
			at.setToRotation(0.0, .0, .0);
			g.setTransform(at);

			for(int j=0;j<n;j++) {
				int x1=textW+j*dx;
				g.setColor(valueToColour(confusion[i][j]/maxx));
				g.fillRect(x1, y1, dx, dx);
				g.setColor(Color.black);
				g.drawRect(x1, y1, dx, dx);
			}
		}
		
	}
	
	private Color valueToColour(double d) {
		return color[Math.min((int) (d*255),255)];
	}


	public void update(double [][] confusion) {
		this.confusion=confusion;
		repaint();
	}

}





