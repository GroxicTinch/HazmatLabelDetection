import java.io.File;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Utils {
  public static int clamp(int var, int min, int max) {
    var = Math.min(Math.max(min, var), max);
    return var;
  }
  
  public static float clamp(float var, float min, float max) {
    var = Math.min(Math.max(min, var), max);
    return var;
  }

  public static double clamp(double var, int min, int max) {
    var = Math.min(Math.max(min, var), (double)max);
    return var;
  }

  public static double clamp(double var, double min, int max) {
    var = Math.min(Math.max(min, var), (double)max);
    return var;
  }

  public static double clamp(double var, double min, double max) {
    var = Math.min(Math.max(min, var), max);
    return var;
  }
  
  public static Mat matPut(int type, int[][] matData) {
    int width = matData[0].length;
    int height = matData.length;
    
    Mat matrix = new Mat(height, width, type);
  
    for(int col = 0; col < width; col++) {
      for(int row = 0; row < height; row++) {
        matrix.put(row, col, matData[row][col]);
      }
    }
    
    return matrix;
  }
  
  public static void saveAs(Mat img, String name) throws MPException {
    saveAs(img, name, "png");
  }
  
  public static void saveAs(Mat img, String name, String ext) {
    File file;
    String newName = name;
    
    if(img.width() > 0 && img.height() > 0) {
      if(ext != "") {
        newName += "." + ext;
      }
  
      // These 2 lines are needed to save to folders not already created
      file = new File(newName);
      file.getParentFile().mkdirs();
      
      Imgcodecs.imwrite(newName, img);
    } else {
      try {
        throw new MPException("Matrix trying to be saved has width/height of 0");
      } catch (MPException e) {
        System.out.println(e.toString());
      }
    }
  }
}