package speech.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

public class ConfusionPanel extends JPanel {
	
	
	private List<String> names;
	private double[][] confusion;
	private Color[] color;

	public ConfusionPanel(List<String> names) {
		this.names=names;
		color=new Color[255];
		for(int i=0;i<255;i++) {
			color[i]=new Color(i,i,255-i);
		}
	}
	
	
	public void paint(Graphics g) {
		super.paint(g);
		if (confusion == null) return;
		int ww=getWidth();
		int hh=getHeight();
		int textW=40;
		int textH=40;
		int siz=Math.min(ww-textW, hh-textH);
		int n=names.size();
		int dx=siz/names.size();
		
		for(int i=0;i<n;i++) {
			g.setColor(Color.black);
			int y1=textH+i*dx;
			
			g.drawRect(0, y1, textW, dx);

			g.drawString(names.get(i), 0, textH+(i+1)*dx-3);
			for(int j=0;j<n;j++) {
				int x1=textW+j*dx;
				g.setColor(valueToColour(confusion[i][j]));
				g.fillRect(x1, y1, dx, dx);
				g.setColor(Color.black);
				g.drawRect(x1, y1, dx, dx);
			}
		}
		
	}
	
	private Color valueToColour(double d) {
		return color[(int) (d*255)];
	}


	public void update(double [][] confusion) {
		this.confusion=confusion;
		repaint();
	}

}
