package speech.gui;

import java.util.Observable;
import java.util.Observer;


public class ValMapper implements Observer, Mapper {

	double maxdb = 50;
	double mindb = -50;
	double max;
	double min;
	boolean linear=false;
//	private Thread thread;

	public final float eval(double val) {
		if (linear) {
			float vv = (float) ((val - min) / (max - min));
			return vv;
		} else {
			double dB = 20 * Math.log10(val + 1e-15);
			float vv = (float) ((dB - mindb) / (maxdb - mindb));
			return vv;
		}
	}

	public void update(Observable o, Object arg) {

		// linear = linearBut.isSelected();

		// maxdb = maxdB.doubleValue();
		max = Math.pow(10, maxdb / 20.0);

		// mindb = mindB.doubleValue();
		min = Math.pow(10, mindb / 20.0);

	}
}