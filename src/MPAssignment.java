import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author David Hoy
 */

public class MPAssignment {
  static int _winX = 0;
  static int _winY = 0;
  static int _winW = 0;
  static int _winH = 0;
  
  static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
  
  static ArrayList<JFrame> _frameList = new ArrayList<JFrame>();
  
  /**
  * @param args the command line arguments
  */
  public static void main(String args[]) {
  	boolean foundImage = false;
    if(args.length == 0) {
      args = new String[1];
      args[0] = ".";
    }
    
    for(String arg : args) {
      File dir = new File(arg);
      File[] dirList = dir.listFiles();

      if(dir.isDirectory()) { 
        if(dirList != null && dirList.length > 0 ) {
          for(File file : dirList) {
            try {
              if(processFile(file)) {
              	foundImage = true;	// Check to see if any images have been found at all
              }
            } catch (IOException e) {
              println("Skipping file due to issue opening: " + file.getName());
            }
          }
        }
        
        if(!foundImage) {
          println("No images found in given directory:\n" + args[0]);
        }
        
      } else if(dir.isFile()){
        try {
          processFile(dir);
        } catch (IOException e) {
          println("Skipping file due to issue opening: " + dir.getName());
        }
      } else {
        println("The directory " + dir.toString() + " does not exist");
      }
    }
  }
  
  /* [Q] Prac 5 "Reshape the digit images and their averages to row vectors of size 1x200"
   */ 

  static boolean processFile(File file) throws IOException {
    ImageFileObject origImgFO = new ImageFileObject(file);

    if(!origImgFO.isImage()) {
    	return false;
    }
    
    OutputObject outputObj = new OutputObject();
	  
	  ImageFileObject imgFO = origImgFO.copy();
	  winShow(origImgFO.getFilename(), origImgFO.getMat());
	  
	  /* 
	   * [TODO] Ensure files are read alphabetically
	   * 
	   * [TODO] Colour of top half background
	   * [TODO] Colour of bottom half background
	   * [TODO] Class Number
	   * [TODO] Other text
	   * [TODO] Symbol
	   * 
	   * [Ignore] Labels with background pattern
	   * [Ignore] Labels with explanatory text(text will never be smaller or denser then in figure 1)
	   * [Ignore] Labels with multiple class numbers or with non-numeric chars
	   */
	  
	  /* Subtasks needing to be fixed
	   * [TODO] Fix Blobs to find holes
	   * [TODO] Blob Boundry Extraction
	   * [TODO] Blob Chords
	   * [TODO] Prac4 Ex3 Histogram Feature Extraction
	   */
	  
	  /* [Notes]
	   * Class Number is located 70%:350 from top of the sign and ends at 90%:450, 38%:190 from left start til 63%:315
	   */
	  
	  // Use this when testing so I dont need to remove or add things
	  
	  if(false) {
	    PRACWORK(file, imgFO, origImgFO);
	    return true;
	  }
	  
	  ArrayList<Mat> classNumMatList = new ArrayList<Mat>();
	 
	  Mat out = Filter.thresholdInv(imgFO.copy().convert(Imgproc.COLOR_BGR2GRAY).getMat(), 80);
	  
	  ConnectedComponents connComp = new ConnectedComponents(out);
	  connComp.generate();
	  
	  ConnectedComponentsBlob[] connBlob = connComp.getBlobs();
	  
	  for(int i = 0; i < connBlob.length; i++) {
	    int signH = out.rows();
	    int signW = out.cols();
	    
	    Rect classNumLocation = new Rect(new Point(signW * 0.38, signH * 0.70), new Point(signW * 0.63, signH * 0.90));
	    
	    if(classNumLocation.contains(connBlob[i].tl())) {
	      // It is most likely the class identification number
	      Imgproc.drawContours(imgFO.getMat(), connBlob[i].findAbsContoursFull(), 0, new Scalar(0,255,0), 2);
	      
	      outputObj.classNum = "Not ready but it has this many corners: " + connBlob[i].getCornersShiTomasi().length;
	    } else if(connBlob[i].getY2() < signH/2) {
	      // It is in the top half, which means it could be Symbol\Explosive number or descriptive text,
	      // Sometimes picks up bounding box if it is a different colour to bottom half bounding box
	      Imgproc.drawContours(imgFO.getMat(), connBlob[i].findAbsContoursFull(), 0, new Scalar(255,0,0), 2);
	    } else {
	      Imgproc.drawContours(imgFO.getMat(), connBlob[i].findAbsContoursFull(), 0, new Scalar(0,0,255), 2);
	    }
	  }
	  
	  winShowRight("Blob " + imgFO.getFilename(), imgFO.getMat());
	  
	  /* Get Colours */
	  findSignHalfColors(origImgFO.getMat(), outputObj);
	  
	  System.out.println(outputObj.toString());
	
	  winWait();
	  return true;
  }
  
  //Current progress
  /*
   * placard-7-radioactive.png is identified wrong, sees top as white instead of yellow
   */
  
  private static void findSignHalfColors(Mat img, OutputObject outputObj) {
    // [TODO] Create proper way to create masks
    // [TODO] remove\mask blobs from Mat going to findSignHalfColors()
    Mat topMask = Imgcodecs.imread("./SampleData/Mask/MaskTopTemp.png");
    Imgproc.cvtColor(topMask, topMask, Imgproc.COLOR_BGR2GRAY);
    Mat bottomMask = Imgcodecs.imread("./SampleData/Mask/MaskBottomTemp.png");
    Imgproc.cvtColor(bottomMask, bottomMask, Imgproc.COLOR_BGR2GRAY);
    int newHeight = img.rows() / 2;
    
    Mat topHalf = Filter.crop(img, new Point(0,0), img.cols(), newHeight);
    Mat bottomHalf = Filter.crop(img, new Point(0, newHeight), img.cols(), newHeight);
    
    outputObj.topColor = MatInfo.getMainColor(topHalf, topMask);
    outputObj.bottomColor = MatInfo.getMainColor(bottomHalf, bottomMask);
  }
  
  @SuppressWarnings("unused")
  private static void PRACWORK(File file, ImageFileObject imgFO, ImageFileObject origImgFO) {
    
    
    //winShowRight("out "+ imgFO.getFilename(), Filter.distanceTransform(imgFO.getMat()));
    winWait();
  }
  
  static void println(String message) {
    System.out.println(message);
  }
  
  static JFrame winShow(String title , Mat img) {
    return winShow(title, img, 0, 0);
  }
  
  static JFrame winShow(String title , Mat img, int x, int y) {  
    _winX = x;
    _winY = y;
    _winW = img.width() + 8; // Offset is for windows 10 border(on my pc at least)
    _winH = img.height() + 34;
    
    /* HighGui calls with third party code since downgrading OpenCV removed gui functionality
    HighGui.imshow(title, img);
    HighGui.moveWindow(title, x, y);
    */
    
    return imshow(title, img, x, y);
  }
  
  static JFrame winShowRight(String title , Mat img) {
    _winX = _winX + _winW;
    _winW = img.width() + 8; // Offset is for windows 10 border(on my pc at least)
    _winH = img.height() + 34;
        
    return winShow(title, img, _winX, _winY);
  }
  
  static JFrame winShowLeft(String title , Mat img) {
    _winX = _winX - img.width() - 8;
    _winW = img.width() + 8; // Offset is for windows 10 border(on my pc at least)
    _winH = img.height() + 34;

    return winShow(title, img, _winX, _winY);
  }
  
  static JFrame winShowBelow(String title , Mat img) {
    _winY = _winY + _winH;
    _winW = img.width() + 8; // Offset is for windows 10 border(on my pc at least)
    _winH = img.height() + 34;
        
    return winShow(title, img, _winX, _winY);
  }
  
  static JFrame winShowAbove(String title , Mat img) {
    _winY = _winY - img.height() - 34;
    _winW = img.width() + 8; // Offset is for windows 10 border(on my pc at least)
    _winH = img.height() + 34;
        
    return winShow(title, img, _winX, _winY);
  }
  
  static void winWait() {
    _winX = 0;
    _winY = 0;
    _winW = 0;
    _winH = 0;
    
    Scanner input = new Scanner(System.in);
    input.nextLine();
    
    for(JFrame frame : _frameList) {
      frame.dispose();
    }
    
    _frameList.clear();

    /* HighGui calls with third party code since downgrading OpenCV removed gui functionality
    HighGui.waitKey();
    */
  }
  
  // [TODO] REMOVE, THIS IS THIRD PARTY CODE USED TO DEBUG AND DISPLAY IMAGES
  public static JFrame imshow(String title, Mat src, int x, int y) {
    BufferedImage bufImage = null;
    try {
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", src, matOfByte); 
        byte[] byteArray = matOfByte.toArray();
        InputStream in = new ByteArrayInputStream(byteArray);
        bufImage = ImageIO.read(in);

        JFrame frame = new JFrame(title);
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        _frameList.add(frame);
        
        return frame;
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
}