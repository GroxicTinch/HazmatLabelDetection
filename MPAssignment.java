import java.io.File;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
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
    if(args.length == 1) {
      File dir = new File(args[0]);
      File[] dirList = dir.listFiles();

      if(dir.exists()) { 
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
      } else {
        println("The directory " + dir.toString() + " does not exist");
      }
    } else {
      println("Please run with a path to a directory of images as an argument");
    }
  }

  static void processFile(File file) throws IOException {
    ImageFileObject imgFO = new ImageFileObject(file);

    if(imgFO.isImage()) {
      println(imgFO.toString());
      
      //ImageObject.saveAs(imgFO.calcRGBHistogram(10, 200, 256) ,"Output/" + imgFO.getName() + "_Histogram",  imgFO.getFileExt());
      
      Point p1 = new Point(339, 341);
      Point p2 = new Point(451, 378);

      //ImageObject.saveAs(imgFO.copy().crop(p1, p2).getImg(), "Output/Crop_" + imgFO.getName(), imgFO.getFileExt());
      
      //ImageObject.saveAs(imgFO.copy().resizeToRatio(0.5, 0.5).getImg(), "Output/" + imgFO.getName() + "_Resize", imgFO.getFileExt());
      //ImageObject.saveAs(imgFO.copy().convert(Imgproc.COLOR_BGR2GRAY).getImg(), "Output/Grayscale_" + imgFO.getName(), imgFO.getFileExt());
      //ImageObject.saveAs(imgFO.copy().convert(Imgproc.COLOR_BGR2HSV).getImg(), "Output/HSV_" + imgFO.getName(), imgFO.getFileExt());
      //ImageObject.saveAs(imgFO.copy().convert(Imgproc.COLOR_BGR2Luv).getImg(), "Output/Luv_" + imgFO.getName(), imgFO.getFileExt());
      //ImageObject.saveAs(imgFO.copy().convert(Imgproc.COLOR_BGR2Lab).getImg(), "Output/Lab_" + imgFO.getName(), imgFO.getFileExt());
      
      //imgFO.drawBoundingBox(p1, p2, new Scalar(0,0,255));
      //imgFO.drawCornerCircles(p1, p2, new Scalar(255,0,0));
      //ImageObject.saveAs(imgFO.getImg(), "Output/BoundingBox_" + imgFO.getName(), imgFO.getFileExt());
      
      // Fricken Broken
      //ImageObject.saveAs(imgFO.copy().filterGaussian().getImg(), "Output/Filtered_" + imgFO.getName(), imgFO.getFileExt());
      ImageFileObject newImgFO = imgFO.copy();
      HighGui.imshow(newImgFO.getName(), newImgFO.filterGaussian().getImg());
      HighGui.waitKey();
    }
  }

  static void println(String message) {
    System.out.println(message);
  }
}