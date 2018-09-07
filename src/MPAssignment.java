import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.MSER;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author David
 */
public class MPAssignment {

  static int winX = 0;
  static int winY = 0;
  static int winW = 0;
  static int winH = 0;
  /**
  * @param args the command line arguments
  */
  public static void main(String args[]) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    if(args.length >= 1) {
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
    } else {
      println("Please run with a path to a directory of images as an argument or a path to an image");
    }
  }
  
  /* [Q] Prac4 Ex 2 says to implement a CCL, but OpenCV already has this as a function?
   */

  static void processFile(File file) throws IOException {
    ImageFileObject origImgFO = new ImageFileObject(file);
    ImageFileObject imgFO = new ImageFileObject(file);

    if(imgFO.isImage()) {
      winShow(origImgFO.getFilename(), origImgFO.getImg());

      Mat out = Filter.threshold(imgFO.convert(Imgproc.COLOR_BGR2GRAY).getImg(), 80);
      Mat groups = new Mat();
      ArrayList<Blob> blobs = new ArrayList<Blob>();

      Imgproc.connectedComponents(out, groups);
      
      MinMaxLocResult minMaxLoc = Core.minMaxLoc(groups);
      
      for(int i = 1; i < minMaxLoc.maxVal; i++) {
        Mat blobMat = Mat.zeros(groups.size(), CvType.CV_8U);
        
        int leftMost = groups.cols() + 1;
        int rightMost = -1;
        int topMost = groups.rows() + 1;
        int bottomMost = -1;
        
        int foregroundCount = 0;
        
        for(int row = 0; row < groups.rows(); row++) {
          for(int col = 0; col < groups.cols(); col++) {
            if(groups.get(row, col)[0] == i) {
              foregroundCount++;
              blobMat.put(row, col, 255);
              if(col < leftMost ) {
                leftMost = col;
              }
              if(col > rightMost) {
                rightMost = col;
              }
              
              if(row < topMost) {
                topMost = row;
              }
              if(row > bottomMost) {
                bottomMost = row;
              }
            }
          }
        }
        
        blobMat = blobMat.submat(topMost, bottomMost+1, leftMost, rightMost+1);
        
        if(blobMat.height() > 5 && blobMat.width() > 5) {
          double ratio = (double)foregroundCount / (double)(blobMat.width() * blobMat.height());
          Blob blob = new Blob(blobMat, blobMat.width(), blobMat.height(), ratio);
          
          println(blobMat.dump());
          println("width: " + blobMat.width()
               +"\nheight: " + blobMat.height()
               +"\nratio: " + ratio
               +"\n");
          
          Imgproc.cvtColor(blobMat, blobMat,  Imgproc.COLOR_GRAY2BGR);
          winShowRight("blob "+ i, blobMat);
        
          blobs.add(blob);
        }
      }
      
      winShowRight("Out "+ imgFO.getFilename(), out);
      winWait();
    }
  }
  
  // Current progress
  /*
   * placard-7-radioactive.png is misidentified, sees top as white instead of yellow
   */
  
  static void println(String message) {
    System.out.println(message);
  }
  
  static void winShow(String title , Mat img) {
    winShow(title, img, 0, 0);
  }
  
  static void winShow(String title , Mat img, int x, int y) {
    winX = x;
    winY = y;
    winW = img.width() + 8; // Offset is for windows 10 border(on my pc at least)
    winH = img.height() + 34;
    
    HighGui.imshow(title, img);
    HighGui.moveWindow(title, x, y);
  }
  
  static void winShowRight(String title , Mat img) {
    winX = winX + winW;
    winW = img.width() + 8; // Offset is for windows 10 border(on my pc at least)
    winH = img.height() + 34;
        
    winShow(title, img, winX, winY);
  }
  
  static void winShowLeft(String title , Mat img) {
    winX = winX - img.width() - 8;
    winW = img.width() + 8; // Offset is for windows 10 border(on my pc at least)
    winH = img.height() + 34;

    winShow(title, img, winX, winY);
  }
  
  static void winShowBelow(String title , Mat img) {
    winY = winY + winH;
    winW = img.width() + 8; // Offset is for windows 10 border(on my pc at least)
    winH = img.height() + 34;
        
    winShow(title, img, winX, winY);
  }
  
  static void winShowAbove(String title , Mat img) {
    winY = winY - img.height() - 34;
    winW = img.width() + 8; // Offset is for windows 10 border(on my pc at least)
    winH = img.height() + 34;
        
    winShow(title, img, winX, winY);
  }
  
  static void winWait() {
    winX = 0;
    winY = 0;
    winW = 0;
    winH = 0;

    HighGui.waitKey();
  }
}