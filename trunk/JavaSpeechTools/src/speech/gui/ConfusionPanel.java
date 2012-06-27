package speech.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.swing.JPanel;

public class ConfusionPanel extends JPanel {
	
	private AffineTransform at;
	private List<String> names;
	private float[][] confusion;
	private Color[] color;

	public ConfusionPanel(List<String> names) {
		this.names=names;
		color=new Color[256];
		for(int i=0;i<256;i++) {
			color[i]=new Color(i,i,255-i);
		}
			}
	
	@Override
	public void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		
		Graphics2D g=(Graphics2D)g1.create();
		
		
		
		if (confusion == null) return;
		

		// Create a rotation transformation for the font.
		AffineTransform fontAT = new AffineTransform();


		// get the current font
		Font horFont = g.getFont();


		// Derive a new font using a rotatation transform
		fontAT.rotate(-Math.PI/2.0);
		
		Font rotFont = horFont.deriveFont(fontAT);
		
		
		int ww=getWidth();
		int hh=getHeight();
		
		int textW=60;
		int textH=textW;
		
		int siz=Math.min(ww-textW, hh-textH);
		System.out.print(" GRID SIZE="+siz);
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
			
			//stem.out.println(y1);
			g.drawRect(0, y1, textW, dx);
			g.drawString(names.get(i), 3, textH+(i+1)*dx-3);
			
			if ( i == 0) {
				g.setFont(rotFont);
			}
			
			for(int j=0;j<n;j++) {
				
				int x1=textW+j*dx;
				if (Math.abs(maxx) !=0.0) {
					g.setColor(valueToColour(confusion[i][j]/maxx));
				} else {
					g.setColor(valueToColour(0.0));
				}
			
	
				g.fillRect(x1, y1, dx, dx);
	
				g.setColor(Color.black);
				if (i == 0){
					g.drawString(names.get(j),x1+dx,textW) ; //,textH+(i+1)*dx-3);
				}

				g.drawRect(x1, y1, dx, dx);
			}
			
			if (i == 0){
				g.setFont(horFont);
			}
			
		}
		
	}
	
	private Color valueToColour(double d) {
		return color[Math.min((int) (d*255),255)];
	}


	public void update(float [][] confusion) {
		this.confusion=confusion;
		repaint();
	}

}





