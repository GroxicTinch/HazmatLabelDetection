import java.io.File;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.Point;

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
      
      Point p1 = new Point(339, 341);
      Point p2 = new Point(451, 378);

      try {
        //ImageObject.saveAs(imgFO.calcRGBHistogram(10, 200, 256) ,"Output/" + imgFO.getName() + "_Histogram",  imgFO.getFileExt());
        //ImageObject.saveAs(imgFO.returnCopyCrop(p1, p2), "Output/" + imgFO.getName() + "_Crop", imgFO.getFileExt());
        //ImageObject.saveAs(imgFO.returnCopyResizeToRatio(0.5, 0.5), "Output/" + imgFO.getName() + "_Resize", imgFO.getFileExt());
        ImageObject.saveAs(imgFO.returnCopyResizeToRatio(0.5, 0.5), "Output/" + imgFO.getName() + "_Resize", imgFO.getFileExt());
      } catch (MPException e) {
        println(e.toString());
      }
    }
  }

  static void println(String message) {
    System.out.println(message);
  }
}