package opengl;

import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;

import com.sun.opengl.util.GLUT;

public class NetDisplay implements GLEventListener {

	private static NetDisplay nd;
	private double zoom=1.0;
	
	public static void main(String[] args) {
		final Frame frame = new Frame("JOGL HelloWorld");
		final GLCanvas canvas = new GLCanvas();
		canvas.addGLEventListener(nd=new NetDisplay());
		frame.add(canvas);
		frame.setSize(300, 300);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		frame.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(" CLICK");
				
			}
		});
		
		frame.addMouseWheelListener(new MouseWheelListener() {
			
			

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				
				if (e.getWheelRotation() >0) {
					nd.zoom *= 1.1;
				} else {
					nd.zoom *= 1/1.1;
				}
				System.out.println(nd.zoom);
				canvas.repaint();
				//frame.repaint();
			}
		});
	}
	
	

	private GLU glu;
	private int width;
	private int height;

	public void init(GLAutoDrawable drawable) {
		
		GL gl = drawable.getGL(); 
		gl.glClearColor( 1.0f, 1.0f, 1.0f, 1.0f );
		gl.glColor3f( 0.0f, 0.0f, 0.0f ); 
		gl.glPointSize(4.0f);
		
		glu=new GLU();
		
		//glPointSize (4.0f);
	}

	 /**
     * @param gl The GL context.
     * @param glu The GL unit.
     * @param distance The distance from the screen.
     */
    private void setCamera(GL gl, GLU glu, float x,float y,float z) {
        // Change to projection matrix.
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = 1.0f; //this.width/this.height;
        
        glu.gluPerspective(45*zoom, widthHeightRatio, .1, 10000);
        
        
        glu.gluLookAt(x,y,z, 0, 0, 0, 0, 1, 0);

        // Change back to model view matrix.
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		this.width=width;
		this.height=height;
	}

	float pts[][]={{0,0,0},{1,0,0},{0,1,0},{0,0,1}};
	
	public void display(GLAutoDrawable drawable) {
		
		  GL gl = drawable.getGL();
		// Set camera.
        setCamera(gl, glu, 6,5,6);

	  
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		//gl.glOrtho(-2.0, 2.0, -2.0, 2.0, -2.0, 3.0);
		gl.glBegin(GL.GL_POINTS);
		for (float []pt:pts){

			gl.glVertex3f(pt[0],pt[1],pt[2]);
		}
		
		gl.glEnd();
		
		gl.glColor3f(0.0f, 1.0f, 1.0f);
		
		
		gl.glBegin(GL.GL_LINES);
		for (int i=0;i<3;i++){
			for (int j=i+1;j<4;j++){
				float pt[]=pts[i];
				gl.glVertex3f(pt[0],pt[1],pt[2]);
				pt=pts[j];
				gl.glVertex3f(pt[0],pt[1],pt[2]);
			}
		}
		gl.glEnd();
		
		
		
		gl.glFlush();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

}