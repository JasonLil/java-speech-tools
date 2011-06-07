package speech;

import java.io.IOException;

import javax.swing.JApplet;


public class MainApplet extends JApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		MainApp app=null;
		try {
			app = new MainApp(true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			app.start();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}

}
