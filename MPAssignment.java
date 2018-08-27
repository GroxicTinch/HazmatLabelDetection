import java.io.File;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author David
 */
public class MPAssignment {

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
    ImageFileObject imgFO = new ImageFileObject(file);

    if(imgFO.isImage()) {
      println(imgFO.toString());
      HighGui.imshow(imgFO.getFilename(), imgFO.copy().getImg());
      
      /*ImageFileObject newImgFO = imgFO.copy();
      HighGui.imshow("Filtered" + newImgFO.getFilename(), newImgFO.filterGaussian().getImg());
      HighGui.waitKey();*/
      
      //https://github.com/opencv/opencv/blob/master/samples/java/tutorial_code/TrackingMotion/good_features_to_track/GoodFeaturesToTrackDemo.java
      MatOfPoint corners = new MatOfPoint();

      int maxCorners = 0; // Infinite
      double quality = 0.01;
      double minDist = 10;
      
      Imgproc.goodFeaturesToTrack(imgFO.copy().convert(Imgproc.COLOR_BGR2GRAY).getImg(), corners, maxCorners, quality, minDist);
      
      for(Point corner : corners.toArray()) {
        Imgproc.circle(imgFO.getImg(), new Point(corner.x, corner.y), 4, new Scalar(0, 0, 255), 1);
      }
      
      HighGui.imshow("Filtered" + imgFO.getFilename(), imgFO.getImg());
      HighGui.waitKey();
    }
  }
  
  static void println(String message) {
    System.out.println(message);
  }
}