import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
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

  static void processFile(File file) throws IOException {
    ImageFileObject origImgFO = new ImageFileObject(file);
    ImageFileObject imgFO = new ImageFileObject(file);

    if(imgFO.isImage()) {
      winShow(origImgFO.getFilename(), origImgFO.getImg());
      
      Mat filtered = new Mat();
      Mat out = imgFO.copy().getImg();
      filtered = Filter.prewitt(imgFO.convert(Imgproc.COLOR_BGR2GRAY).getImg());
      
      MSER mser = MSER.create(15, 60, 10000, 0.25, 1, 1000, 1, 1, 1);
      
      List<MatOfPoint> msers = new ArrayList<MatOfPoint>();
      MatOfRect bboxes = new MatOfRect();
      
      mser.detectRegions(filtered, msers, bboxes);
            
      //Imgproc.Canny(filtered, filtered, 900, 1000);
      
      for(MatOfPoint blob : msers) {
        Rect rect = Imgproc.boundingRect(blob);
        // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
        Imgproc.rectangle(out, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0,0,255), 2);
      }

      winShowRight("Prewitt "+ imgFO.getFilename(), out);
      winWait();
            
      /*ImageFileObject newImgFO = imgFO.copy();
      HighGui.imshow("Filtered" + newImgFO.getFilename(), newImgFO.filterGaussian().getImg());
      HighGui.waitKey();*/
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