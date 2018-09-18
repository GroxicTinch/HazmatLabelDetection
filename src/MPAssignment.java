import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
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
  
  /* [Q] Prac4 Ex 2 says to implement a CCL, but OpenCV already has this as a function?
   */

  static void processFile(File file) throws IOException {
    ImageFileObject origImgFO = new ImageFileObject(file);

    if(origImgFO.isImage()) {
      ImageFileObject imgFO = new ImageFileObject(file);
      winShow(origImgFO.getFilename(), origImgFO.getImg());
      
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
      
      // Use this when testing so I dont need to remove or add things
      
      if(true) {
        PRACWORK(file, imgFO, origImgFO);
        return;
      }
      
      
      Mat blobMat;
      Mat out = Filter.threshold(imgFO.copy().convert(Imgproc.COLOR_BGR2GRAY).getImg(), 80);
      
      ConnectedComponents connComp = new ConnectedComponents(out);
      blobMat = connComp.generate();
      //println(blobMat.dump());
      ConnectedComponentsBlob[] connBlob = connComp.getBlobs();
      
      for(int i = 0; i < connBlob.length; i++) {
        Imgproc.drawContours(imgFO.getImg(), connBlob[i].findAbsContoursFull(), 0, new Scalar(0,0,255));
      }
      
      winShowRight("Blob " + imgFO.getFilename(), imgFO.getImg());
      
      winWait();
      
      //winWait();
    }
  }
  
  @SuppressWarnings("unused")
  private static void PRACWORK(File file, ImageFileObject imgFO, ImageFileObject origImgFO) {
    
    
    winShowRight("Out "+ imgFO.getFilename(), out);
    winWait();
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