/* Sample openCV Java Application to do some simple image thresholding
 * Author: Simon McLoughlin
 * For setting up openCV for java development in Eclipse see the link below!
 * You can use version 2.4.11 of openCV as this has the javadoc as part of the download (opencv 3 does not it appears)
 * http://docs.opencv.org/2.4/doc/tutorials/introduction/java_eclipse/java_eclipse.html#java-eclipse
 * Remember to set the PATH environment variable either in windows or locally in Eclipse, to do it in eclipse:
 * Run->Run Configurations->Java Application->Your Project->Environment tab->New
 * Variable: PATH  Value: opencv bin directory. e.g C:\Users\simon\Downloads\opencv2411\opencv\build\x64\vc12\bin
 */

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class OringInspection
{  
   
   public static void main( String[] args )
   {
      System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
      
      //Create and set up the window.
      JFrame frame = new JFrame("OpenCV");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JLabel imageHolder = new JLabel();
      frame.getContentPane().add(imageHolder, BorderLayout.CENTER);
      
      //Display the window.
      frame.pack();
      frame.setVisible(true);
      
      //press Q to quit application
      frame.addKeyListener(new KeyListener() {
    		public void keyPressed(KeyEvent arg0) {
    			if (arg0.getKeyCode() == KeyEvent.VK_Q)
    				System.exit(0);	
    		}
    		public void keyReleased(KeyEvent arg0) {	
    		}
    		public void keyTyped(KeyEvent arg0) {
    		}
      });
      
      //String streamAddr = "http://c-cam.uchicago.edu/mjpg/video.mjpg"; //try in browser to make sure its up!
      //here is a video in the opencv installation folder!
      //String streamAddr = "C:\\Users\\simon\\Downloads\\opencv2411\\opencv\\sources\\samples\\gpu\\768x576.avi";
      
      
      
      System.out.println("Stream Opened");
      Mat img = new Mat();
      Mat out = new Mat();
      Mat histim = new Mat(256,256, CvType.CV_8UC3);
      int i=1;
      while (true) 
      {
    	  String imageName = "C:\\Users\\lialka\\Desktop\\CompVision\\Rings image\\Oring" + (i%15) + ".jpg";
    	  System.out.println(imageName);          
          img = Highgui.imread(imageName);
          int [] h = hist(img);
          
          //calculate the mean processing time per frame and display it
          double before = (double)System.nanoTime()/1000000000;//secs
          
          //convert to greyscale
          Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
          
          
          drawHist(histim,h);
          
          int threshold = calcThreshold(h);
          
          //threshold the image
          int t=120;  // not used any longer as we calculate threshold just above
          
          threshold(img,threshold);
          //openCV version
          //Imgproc.threshold(img, img, 100, 255, Imgproc.THRESH_BINARY);
          //convert to colour so we can put text into the image using whatever colour we want!
          Imgproc.cvtColor(img, out, Imgproc.COLOR_GRAY2BGR);
          double after = (double)System.nanoTime()/1000000000;//secs
          //write the text string below into the image
          Core.putText(out, "Processing Time: " + String.format("%.4f",after-before) + " secs", new Point(20,20), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0,255,0));
          //convert to a Java BufferedImage so we can display in a label
          BufferedImage jimg = Mat2BufferedImage(out);
          imageHolder.setIcon(new ImageIcon(jimg));
          frame.pack();
          i++;
          try {
			Thread.sleep(2000);
          } catch (InterruptedException e) {
			e.printStackTrace();
          }
      }
   }
   
   /**
    * Calculates threshold from histogram
    * @param h
    * @return
    */
   private static int calcThreshold(int[] h) {
	   
	   
	
	return 120;
}

public static void threshold(Mat img, int t)
   {
	   /* threshold the image (img), note here that we need to do an
	    * & with 0xff. this is because Java uses signed two's complement
	    * types. The & operation will give us the pixel in the range we are
	    * used to, 0..255
	    */
	   byte data[] = new byte[img.rows()*img.cols()*img.channels()];
	   img.get(0, 0, data);
	   for (int i=0;i<data.length;i++)
	   {
		   int unsigned = (data[i] & 0xff);
		   if (unsigned > t)
			   data[i] = (byte)0;
		   else
			   data[i] = (byte)255;
	   }
	   img.put(0, 0, data);
   }
   
   public static int [] hist(Mat img)
   {
	   int hist [] = new int[256];
	   byte data[] = new byte[img.rows()*img.cols()*img.channels()];
	   img.get(0, 0, data);
	   for (int i=0;i<data.length;i++)
	   {
		   hist[(data[i] & 0xff)]++;
	   }
	   return hist;
   }
   
   public static void drawHist(Mat img, int [] hist)
   {
	   //get max hist value for range adjustment
	   int max = 0;
	   for(int i=0;i<hist.length;i++)
	   {
		   if(hist[i] > max)
			   max = hist[i];
		   System.out.print(hist[i]  + " ");
	   }
	   System.out.println();
	   int scale = max/256;
	   for(int i=0;i<hist.length-1;i++)
	   {
		   //Core.circle(img, new Point(i*2+1,img.rows()-(hist[i]/scale)+1), 1, new Scalar(0,255,0));
		   Core.line(img, new Point(i+1,img.rows()-(hist[i]/scale)+1), new Point(i+2,img.rows()-(hist[i+1]/scale)+1), new Scalar(0,0,255));
	   }
	
   }
   
   
   
   public static BufferedImage Mat2BufferedImage(Mat m)
   {
	// source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
	// Fastest code
	// The output can be assigned either to a BufferedImage or to an Image

	    int type = BufferedImage.TYPE_BYTE_GRAY;
	    if ( m.channels() > 1 ) {
	        type = BufferedImage.TYPE_3BYTE_BGR;
	    }
	    int bufferSize = m.channels()*m.cols()*m.rows();
	    byte [] b = new byte[bufferSize];
	    m.get(0,0,b); // get all the pixels
	    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
	    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	    System.arraycopy(b, 0, targetPixels, 0, b.length);  
	    return image;

	}

	
}