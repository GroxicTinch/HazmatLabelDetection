import java.io.File;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
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
      
      
      Mat harrisMat = new Mat();
      Mat harrisMatNormal = new Mat();
      //Mat harrisMatNormalScaled = new Mat();
      int blockSize = 3;
      int apertureSize = 1;
      double k = 0.1; // I dont really know what K does
      
      int threshold = 200;
      
      Imgproc.cornerHarris(imgFO.copy().convert(Imgproc.COLOR_BGR2GRAY).getImg(), harrisMat, blockSize, apertureSize, k);
      
      Core.normalize(harrisMat, harrisMatNormal, 0, 255, Core.NORM_MINMAX, CvType.CV_32F, new Mat());
      //Core.convertScaleAbs(harrisMatNormal, harrisMatNormalScaled);
      
      for( int row = 0; row < harrisMatNormal.rows() ; row++){
        for( int col = 0; col < harrisMatNormal.cols(); col++){
          System.out.println(harrisMatNormal.get(row, col)[0] + " < " + threshold);
          if ((int) harrisMatNormal.get(row, col)[0] > threshold){
            Imgproc.circle(imgFO.getImg(), new Point(row, col), 10 , new Scalar(0,0,255), 1 ,8 , 0);
          }
        }
      }
      
      HighGui.imshow("Filtered" + imgFO.getFilename(), imgFO.getImg());
      HighGui.waitKey();
    }
  }
  
  static void println(String message) {
    System.out.println(message);
  }
}