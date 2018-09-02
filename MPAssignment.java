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
import org.opencv.imgcodecs.Imgcodecs;
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
      //HighGui.imshow(imgFO.getFilename(), imgFO.copy().getImg());
      
      /*ImageFileObject newImgFO = imgFO.copy();
      HighGui.imshow("Filtered" + newImgFO.getFilename(), newImgFO.filterGaussian().getImg());
      HighGui.waitKey();*/
      
      int newHeight = imgFO.getHeight() / 2;
      
      ImageObject imgTop = imgFO.copy().crop(new Point(0,0), imgFO.getWidth(), newHeight);
      ImageObject imgBottom = imgFO.copy().crop(new Point(0, newHeight), imgFO.getWidth(), newHeight);
      
      // [TODO] Create proper way to create masks
      Mat topMask = Imgcodecs.imread("./SampleData/Mask/MaskTopTemp.png");
      Imgproc.cvtColor(topMask, topMask, Imgproc.COLOR_BGR2GRAY);
      Mat bottomMask = Imgcodecs.imread("./SampleData/Mask/MaskBottomTemp.png");
      Imgproc.cvtColor(bottomMask, bottomMask, Imgproc.COLOR_BGR2GRAY);
      
      HighGui.imshow("Top " + imgFO.getFilename(), imgTop.getImg());
      HighGui.imshow("Bottom " + imgFO.getFilename(), imgBottom.getImg());
      
      HighGui.moveWindow("Top " + imgFO.getFilename(), 0, 0);
      HighGui.moveWindow("Bottom " + imgFO.getFilename(), 0, imgTop.getHeight()+30);
      
      System.out.println("Top colour: " + imgTop.getMainColor(topMask)
                     + "\nBottom colour: " + imgBottom.getMainColor(bottomMask));
      
      HighGui.waitKey();
      
      /*HighGui.imshow(imgFO.getFilename(), imgFO.convert(Imgproc.COLOR_BGR2HSV).getImg());
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
}