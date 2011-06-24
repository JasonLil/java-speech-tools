package speech.gui;

import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

public class ConfusionPanel extends JPanel {
	
	
	private List<String> names;
	private double[][] confusion;

	public ConfusionPanel(List<String> names) {
		this.names=names;
	}
	
	
	public void paint(Graphics g) {
		super.paint(g);
		
		
	}
	
	public void update(double [][] confusion) {
		this.confusion=confusion;
		
	}

}
