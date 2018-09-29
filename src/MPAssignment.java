import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;
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
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author David
 */
public class MPAssignment {
  static int _winX = 0;
  static int _winY = 0;
  static int _winW = 0;
  static int _winH = 0;
  
  static ArrayList<JFrame> _frameList = new ArrayList<JFrame>();
  
  /**
  * @param args the command line arguments
  */
  public static void main(String args[]) {
    System.load(System.getProperty("user.dir") + "/lib/" + Core.NATIVE_LIBRARY_NAME + ".dll");
    
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
              processFile(file);
            } catch (IOException e) {
              println("Skipping file due to issue opening: " + file.getName());
            }
          }
        } else {
          println("No images found in " + args[0]);
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

  static void processFile(File file) throws IOException {
    ImageFileObject origImgFO = new ImageFileObject(file);

    if(origImgFO.isImage()) {
      String topColor = "Not Implemented";
      String bottomColor = "Not Implemented";
      String classNum = "Not Implemented";
      String otherText = "Not Implemented";
      String symbol = "Not Implemented";
      
      
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
      
      if(true) {
        PRACWORK(file, imgFO, origImgFO);
        return;
      }
      
      
      Mat blobMat;
      Mat out = Filter.thresholdInv(imgFO.copy().convert(Imgproc.COLOR_BGR2GRAY).getMat(), 80);
      
      ConnectedComponents connComp = new ConnectedComponents(out);
      blobMat = connComp.generate();
      //println(blobMat.dump());
      ConnectedComponentsBlob[] connBlob = connComp.getBlobs();
      
      for(int i = 0; i < connBlob.length; i++) {
        Imgproc.drawContours(imgFO.getMat(), connBlob[i].findAbsContoursFull(), 0, new Scalar(0,0,255));
      }
      
      winShowRight("Blob " + imgFO.getFilename(), imgFO.getMat());
      
      /* Get Colours */
      // [TODO] Create proper way to create masks
      Mat topMask = Imgcodecs.imread("./SampleData/Mask/MaskTopTemp.png");
      Imgproc.cvtColor(topMask, topMask, Imgproc.COLOR_BGR2GRAY);
      Mat bottomMask = Imgcodecs.imread("./SampleData/Mask/MaskBottomTemp.png");
      Imgproc.cvtColor(bottomMask, bottomMask, Imgproc.COLOR_BGR2GRAY);
      int newHeight = imgFO.getHeight() / 2;
      
      Mat topHalf = imgFO.copy().crop(new Point(0,0), imgFO.getWidth(), newHeight).getMat();
      Mat bottomHalf = imgFO.copy().crop(new Point(0, newHeight), imgFO.getWidth(), newHeight).getMat();
      
      topColor = MatInfo.getMainColor(topHalf, topMask);
      bottomColor = MatInfo.getMainColor(bottomHalf, bottomMask);
      
      System.out.println("\nTop: " + topColor
                     + "\nBottom: " + bottomColor
                     + "\nClass: " + classNum
                     + "\nText: " + otherText
                     + "\nSymbol: " + symbol);

      winWait();
    }
  }
  
  @SuppressWarnings("unused")
  private static void PRACWORK(File file, ImageFileObject imgFO, ImageFileObject origImgFO) {
    Mat digitAvg[] = new Mat[4];
    Mat[][] digitSamples = new Mat[4][100];
    
    for(int digit = 0; digit <= 3; digit++) {
      // Use format that has a lot of space so adding the pixels wont go over the max
      Mat currAvg = new Mat(20,20, CvType.CV_64FC3);
      digitAvg[digit] = new Mat(20,20, CvType.CV_64FC3);
      Mat temp = new Mat(20,20, CvType.CV_64FC3);;
      
      for(int sample = 0; sample < 100; sample++) {
        int x = (sample * 20);
        int y = (digit * 100);
        
        digitSamples[digit][sample] = imgFO.copy().crop(new Point(x, y), 20, 20).getMat();
        //[FIXME] Images are inconsistent
        digitSamples[digit][sample].convertTo(temp, CvType.CV_64FC3);
        
        Core.add(currAvg, temp, currAvg);
      }

      currAvg.convertTo(digitAvg[digit], CvType.CV_8U, 0.01);
      winShowRight("tmp "+ imgFO.getFilename(), digitAvg[digit]);
    }
    
    //winShowRight("tmp "+ imgFO.getFilename(), digitSamples[2][0]);
    //winShowRight("tmp "+ imgFO.getFilename(), out);
    winWait();
  }

  // Current progress
  /*
   * placard-7-radioactive.png is misidentified, sees top as white instead of yellow
   */
  
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