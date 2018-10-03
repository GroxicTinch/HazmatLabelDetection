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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
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
            } catch (MPException e) {
              println(e.toString());
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
        } catch (MPException e) {
          println(e.toString());
        }
      } else {
        println("The directory " + dir.toString() + " does not exist");
      }
    }
  }
  
  /* [Q] Prac 5 "Reshape the digit images and their averages to row vectors of size 1x200"
   */ 

  static boolean processFile(File file) throws IOException, MPException {
    ImageFileObject origImgFO = new ImageFileObject(file);

    if(!origImgFO.isImage()) {
    	return false;
    }
    
    OutputObject outputObj = new OutputObject(origImgFO.getFilename());
	  
	  ImageFileObject imgFO = origImgFO.copy();
	  //winShow(origImgFO.getFilename(), origImgFO.getMat());
	  
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
	  	  
	  // Use this when testing so I dont need to remove or add things
	  if(false) {
	    PRACWORK(file, imgFO, origImgFO);
	    return true;
	  }
	  
	  /* 
	   * [TODO] Create proper way to create masks
	   */
	  Mat mask = Imgcodecs.imread("./MasksAndTemplates/MaskTemp.png");
    Imgproc.cvtColor(mask, mask, Imgproc.COLOR_BGR2GRAY);
    // ******************************************************************************************

    TemplateMatchResult symbolTmr = new TemplateMatchResult();
    
    ConnectedComponentsBlob[] connBlob = findBlobs(origImgFO.getMat(), mask);
	  
	  
	  for(int i = 0; i < connBlob.length; i++) {
	    int signH = imgFO.getHeight();
	    int signW = imgFO.getWidth();
	    
	    /* [Notes]
	     * Class Number is located 0.70:350 from top of the sign and ends at 0.90:450, 0.38:190 from left start til 0.63:315
	     * Text T 0.38:190, B 0.66:330, L 0.12:60, R 0.89:445
	     * Symbol is located at top 0.04:20, bottom 0.49:245, left 0.21:105, right 0.78:390
	     */
	    Rect classNumLoc = new Rect(new Point(signW * 0.38, signH * 0.70), new Point(signW * 0.63, signH * 0.90));
      Rect classTextLoc = new Rect(new Point(signW * 0.12, signH * 0.38), new Point(signW * 0.89, signH * 0.66));
	    Rect classSymbolLoc = new Rect(new Point(signW * 0.21, signH * 0.04), new Point(signW * 0.78, signH * 0.49));

	    if(classNumLoc.contains(connBlob[i].tl())) {
	      // It is most likely the class identification number
	      Imgproc.drawContours(imgFO.getMat(), connBlob[i].findAbsContoursFull(), 0, new Scalar(0,255,0), 2);
	      
	      outputObj.classNum = "Not ready but it has this many corners: " + connBlob[i].getCornersShiTomasi().length;
	    } else if(classTextLoc.contains(connBlob[i].tl()) && classTextLoc.contains(connBlob[i].br())) {
	      // It is most likely the text
        Imgproc.drawContours(imgFO.getMat(), connBlob[i].findAbsContoursFull(), 0, new Scalar(255,255,0), 2);
	    } else if(classSymbolLoc.contains(connBlob[i].tl()) && classSymbolLoc.contains(connBlob[i].br())) {
	      TemplateMatchResult symbolTmrTmp = new TemplateMatchResult();
	      
        // It is most likely the class symbol
        Imgproc.drawContours(imgFO.getMat(), connBlob[i].findAbsContoursFull(), 0, new Scalar(0,255,255), 2);
        
        symbolTmrTmp = findSymbol(connBlob[i].getMat());
        
        if(symbolTmr.getPercent() < symbolTmrTmp.getPercent()) {
          symbolTmr = symbolTmrTmp;
          
          outputObj.symbol = symbolTmr.getName();
        }
	    } else if(connBlob[i].getY2() < signH/2) {
	      // It is in the top half, which means it could be Symbol\Explosive number or descriptive text,
	      // Sometimes picks up bounding box if it is a different colour to bottom half bounding box
	      Imgproc.drawContours(imgFO.getMat(), connBlob[i].findAbsContoursFull(), 0, new Scalar(255,0,0), 2);
	    } else {
	      //Imgproc.drawContours(imgFO.getMat(), connBlob[i].findAbsContoursFull(), 0, new Scalar(0,0,255), 2);
	    }
	  }
	  
	  winShowRight("Blob " + imgFO.getFilename(), imgFO.getMat());
	  
	  /* Get Colors */
	  findSignHalfColors(origImgFO.getMat(), mask, outputObj);
	  
	  System.out.println(outputObj.toString());
	
	  winWait();
	  return true;
  }

  private static ConnectedComponentsBlob[] findBlobs(Mat mat, Mat mask) {
    Mat blackWhite = new Mat();
    Imgproc.cvtColor(mat, blackWhite, Imgproc.COLOR_BGR2GRAY);

    Mat outBlack = Filter.thresholdInv(blackWhite, 30);
    Mat outWhite = Filter.threshold(blackWhite, 160);
    
    ConnectedComponents connCompBlack = new ConnectedComponents(outBlack, mask);
    ConnectedComponents connCompWhite = new ConnectedComponents(outWhite, mask);
    connCompBlack.generate();
    connCompWhite.generate();
    
    ConnectedComponentsBlob[] connBlobBlack = connCompBlack.getBlobs();
    ConnectedComponentsBlob[] connBlobWhite = connCompWhite.getBlobs();
    
    int combinedLength = connBlobBlack.length + connBlobWhite.length;
    ConnectedComponentsBlob[] connBlob = new ConnectedComponentsBlob[combinedLength];
    
    int ii = 0;
    for(int i = 0; i < connBlobBlack.length; i++) {
      connBlob[i] = connBlobBlack[i];
      ii++;
    }
    for(int i = 0; i < connBlobWhite.length; i++) {
      connBlob[ii] = connBlobWhite[i];
      ii++;
    }
    
    return connBlob;
  }
  
  /*
   * [TODO] remove\mask found blobs from Mat going to findSignHalfColors() to lower chance of
   *        text + symbols being picked up as text
   */
  private static void findSignHalfColors(Mat mat, Mat mask, OutputObject outputObj) {
    int newHeight = mat.rows() / 2;
    Mat topMask = Filter.crop(mask, new Point(0,0), mat.cols(), newHeight);
    Mat bottomMask = Filter.crop(mask, new Point(0, newHeight), mat.cols(), newHeight);

    //topMask = Imgcodecs.imread("./SampleData/Mask/MaskTopTempSmaller.png");
    
    /* placard-7-radioactive.png was identified wrong, saw top as white instead of yellow
     * [TODO] Stop using mask that this benefits from
     */
    topMask = Imgcodecs.imread("./MasksAndTemplates/MaskTopTempSmallerFURadio.png");
    Imgproc.cvtColor(topMask, topMask, Imgproc.COLOR_BGR2GRAY);
    // **************************************************************
    
    Mat topHalf = Filter.crop(mat, new Point(0,0), mat.cols(), newHeight);
    Mat bottomHalf = Filter.crop(mat, new Point(0, newHeight), mat.cols(), newHeight);
    
    outputObj.topColor = MatInfo.getMainColor(topHalf, topMask);
    outputObj.bottomColor = MatInfo.getMainColor(bottomHalf, bottomMask);
  }
  
  private static TemplateMatchResult findSymbol(Mat mat) {
    TemplateMatchResult finalTmr = new TemplateMatchResult("Unknown");
    
    boolean gotGoodResult = false;
    
    // match must be at least 50% to count
    finalTmr.setPercent(0.5);
    
    File dir = new File("./MasksAndTemplates/Templates");
    File[] dirList = dir.listFiles();

    if(dir.isDirectory()) { 
      if(dirList != null && dirList.length > 0 ) {
        for(File file : dirList) {
            try {
              if(gotGoodResult) {
                break;
              }
              ImageFileObject templ = new ImageFileObject(file, Imgcodecs.IMREAD_GRAYSCALE);
              TemplateMatchResult tmr = new TemplateMatchResult();
              
              tmr.setPercent(0);
              
              // for scale 45 we will just check scale 100 since it is the most likely result
              for(int scale = 45; scale <= 200; scale+=5) {                
                Mat templMat;
                if(scale == 45) {
                  templMat = templ.getMat();
                } else if(scale == 100) {
                  // alreadyChecked so skip
                  continue;
                } else {
                  templMat = Filter.resizeToRatio(templ.getMat(), ((double)scale/100));
                }
                
                if(templMat.width() <= mat.width() && templMat.height() <= mat.height()) {
                  // Cant save Laplacian templates or resize will cause issues
                  tmr = MatInfo.templateMatch(Filter.laplacian(mat), Filter.laplacian(templMat));

                  if(finalTmr.getPercent() < tmr.getPercent()) {
                    String name = templ.getName();
                    int indexOf = name.indexOf('_');
                    
                    finalTmr = tmr;
                    
                    if(indexOf > -1) {
                      name = name.substring(0, indexOf);
                    }
                    finalTmr.setName(name);
                    
                    if(tmr.getPercent() > 0.9) {
                      gotGoodResult = true;
                      break;
                    }
                  }
                }
              }
            } catch (IOException e) {
              
            }
        }
      }
    }
    
    return finalTmr;
  }
  
  @SuppressWarnings("unused")
  private static void PRACWORK(File file, ImageFileObject imgFO, ImageFileObject origImgFO) {
    Mat out = imgFO.copy().getMat();
    Mat filtered = Filter.prewitt(imgFO.convert(Imgproc.COLOR_BGR2GRAY).getMat());
    
    FeatureDetector mser = FeatureDetector.create(FeatureDetector.MSER);
    //MSER mser = .create(15, 60, 10000, 0.25, 1, 1000, 1, 1, 1);
    
    MatOfKeyPoint msers = new MatOfKeyPoint();
    mser.detect(filtered, msers);
    
    for(int row = 0; row < msers.rows(); row++) {
      Point p = new Point(msers.get(row, 0)[0], msers.get(row, 0)[1]);
      Imgproc.drawMarker(out, p, new Scalar(0,0,255));
    }
    
    winShowRight("out "+ imgFO.getFilename(), out);
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