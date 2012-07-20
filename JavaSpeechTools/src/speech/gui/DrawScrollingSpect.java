package speech.gui;

/* 
 * Copyright (c) 2007 P.J.Leonard
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 *    1. Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote
 *       products derived from this software without specific prior
 *       written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;

/**
 * 
 * notifies observers if it need to be redrawn (typically the panel(s))
 * 
 * update() redoes all the drawing.
 * 
 * SpectrogramListener - does image resize - incrementally redraws as the data
 * become ready.
 * 
 * @author pjl
 * 
 */
public class DrawScrollingSpect extends JPanel {

	private static final long serialVersionUID = 1L;
	Dimension size;
	private int[] screenBuffer;
	private MemoryImageSource screenConverter;

	int nChunks;
	int nBins;
	int ptr = 0;
	Image offscreen;
	ValMapper mapper;
	boolean recursion = false;
	private boolean reset=true;
	private boolean pause=false;


	public DrawScrollingSpect() {
		
		this.mapper = new ValMapper();
		mapper.update(null, null);
		setDoubleBuffered(true);
	
	}

	final Object imagSync = new Object();
	

	void createGraphics() {
		
		synchronized (imagSync) {
			reset=true;
			
			int nnn=getWidth();
			if (nnn <=0) return;
			nChunks=nnn;
			size = new Dimension(nChunks, nBins);
			System.out.println(" Spectrogram  nBins="+nBins+ " width="+nChunks);
			int width = nChunks;
			int height = nBins;
			
			screenBuffer = new int[width * height];

			screenConverter = new MemoryImageSource(width, height,
					screenBuffer, 0, width);
			screenConverter.setAnimated(true);
			screenConverter.setFullBufferUpdates(false);
			offscreen = Toolkit.getDefaultToolkit()
					.createImage(screenConverter);
			ptr=0;
		
		}
	}

	
	public void notifyMoreDataReady(double[] bins) {
				
		if (pause) return;
		if (recursion) {
			System.err.println(" RECURSION ");
		}
		
		nBins = bins.length;
		
		if (nBins == 0) {
			return;
		}

		recursion = true;

		if (size == null || nBins != size.height || nChunks != getWidth()) {
			createGraphics();
		}

		if (reset) {
			ptr=0;
			Arrays.fill(screenBuffer, 0);
			reset=false;
			screenConverter.newPixels(0, 0, nChunks, nBins);
		}
		
		int width = size.width;

		for (int i = 0; i < nBins; i++) {
			int bin = nBins - i - 1;
			float val = mapper.eval(bins[bin]);

			if (val < 0) {
				val = 0.0f;
			}
			if (val > 1.0) {
				val = 1.0f;
			}
			int c_r = (int) (255 * val);
			int c_g = c_r;
			int c_b = 255 - c_r;

			int color = (c_b) + (c_g << 8) + (c_r << 16) + (0xFF << 24);
			screenBuffer[i * width + ptr] = (255 << 24) + color;
			// rgbarray[i] = color;
		}

		if (ptr % 1 == 0) {
			screenConverter.newPixels(ptr, 0, 1, nBins);
		//	screenRepaint = true;
		//	repaint();
		}

		ptr = (ptr + 1) % size.width;
		recursion = false;
	}

	// public void paintX(Graphics g) {
	// if (screenRepaint) {
	// g.drawImage(offscreen, 0, 0, this);
	// screenRepaint = false;
	// }
	// }
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// System.out.println(" Spectro DRAWIMAGE");
		if (size == null || nChunks==0 )   return;
		
		int w = size.width - ptr;
		int h = size.height;

		int magX = Math.max(1, getWidth() / size.width);
		//int magY = Math.max(1, getHeight() / size.height);
		// System.out.println(magX+" "+magY);
		//float magX=(float)getWidth() / size.width;
		float magY=(float)getHeight() / size.height;
		g.drawImage(offscreen, 0, 0, w * magX, (int)(h * magY), ptr, 0, size.width, h,
				this);
		if (ptr != 0) {
			g.drawImage(offscreen, w * magX, 0, size.width * magX, (int)(h * magY), 0,
					0, ptr, h, this);
		}


	}

	public int getHeightXX() {
		if (size == null) {
			return 200;
		}
		return size.height;
	}

	public void reset() {
		reset=true;
	}

	public void pause(boolean yes) {
		pause=yes;	
	}

}
