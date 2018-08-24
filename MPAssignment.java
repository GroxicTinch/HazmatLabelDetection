import java.io.File;
import java.io.IOException;

import org.opencv.core.Core;
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
      
      //ImageObject.saveAs(imgFO.calcHistogram(10, 200, 256, false) ,"Output/" + imgFO.getName() + "_Histogram",  imgFO.getFileExt());
      
      Point p1 = new Point(339, 341);
      Point p2 = new Point(451, 378);
      
      /*
      imgFO.convert(Imgproc.COLOR_BGR2GRAY);
      HighGui.imshow("equalized " + imgFO.getFilename(), imgFO.copy().equalizeContrast().getImg());
      HighGui.imshow("grayscale " + imgFO.getFilename(), imgFO.getImg());
      HighGui.waitKey();
      */

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
      /*
      //ImageObject.saveAs(imgFO.copy().convert(Imgproc.COLOR_BGR2GRAY).filterGaussian().getImg(), "Output/Filtered_" + imgFO.getName(), imgFO.getFileExt());
      
      ImageFileObject newImgFO = imgFO.copy();
      
      HighGui.imshow(newImgFO.getFilename(), newImgFO.getImg());
      HighGui.waitKey();
      
      Mat blur = newImgFO.getImg();
      
      //Imgproc.medianBlur(blur, blur, 5);
      
      HighGui.imshow(newImgFO.getFilename(), blur);
      HighGui.waitKey();
      
      newImgFO.filterGaussian();
      
      println("Filtered\n" + newImgFO.matToString());
      
      Mat normalized = newImgFO.getImg();
      
      HighGui.imshow("PreNormal " + newImgFO.getFilename(), newImgFO.getImg());
      HighGui.waitKey();
      
      Core.normalize(normalized, normalized, 0, 255, Core.NORM_MINMAX, -1, new Mat());
      
      //println("Normalized\n" + newImgFO.matToString());
      
      HighGui.imshow("Normal " + newImgFO.getFilename(), normalized);
      HighGui.waitKey();
      */
      
      // Affine Transform
      /*
      Mat affined = new Mat();
      Mat src = imgFO.copy().getImg();
      
      Point[] srcTri = new Point[3];
      srcTri[0] = new Point( 0, 0 );
      srcTri[1] = new Point( src.cols() - 1, 0 );
      srcTri[2] = new Point( 0, src.rows() - 1 );
      
      Point[] dstTri = new Point[3];
      dstTri[0] = new Point( 200, 0 );
      dstTri[1] = new Point( src.cols() + 200, 0 );
      dstTri[2] = new Point( 0, src.rows() - 1 );
      
      Mat warp_mat = Imgproc.getAffineTransform(new MatOfPoint2f(srcTri), new MatOfPoint2f(dstTri));
      
      Imgproc.warpAffine(src, affined, warp_mat, src.size());
      
      HighGui.imshow(imgFO.getFilename(), affined);
      HighGui.waitKey();
      */
      
      // Morphology
      HighGui.imshow(imgFO.getFilename(), imgFO.getImg());
      HighGui.imshow("morphed" + imgFO.getFilename(), imgFO.copy().morphBlackhat(2).getImg());
      HighGui.waitKey();
    }
  }
  
  static void println(String message) {
    System.out.println(message);
  }
}