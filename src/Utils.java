import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
  
  public static Mat matPut(int type, double[][] matData) {
    int width = matData[0].length;
    int height = matData.length;
    
    Mat matrix = new Mat(height, width, type);
    
    for(int row = 0; row < width; row++) {
      matrix.put(row, 0, matData[row]);
      //for(int col = 0; col < height; col++) {
        //matrix.put(row, col, matData[row][col]);
      //}
    }
    
    return matrix;
  }
  
  public static void saveAs(Mat img, String name) throws MPException {
    saveAs(img, name, "png");
  }
  
  public static void saveAs(Mat img, String name, String ext) throws MPException {
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
      throw new MPException("Matrix trying to be saved has width/height of 0");
    }
  }
  
  public static void saveAs(double[] array, String name, String ext) throws MPException {
    File file;
    String newName = name;
    
    if(array.length > 0) {
      if(ext != "") {
        newName += "." + ext;
      }
  
      // These 2 lines are needed to save to folders not already created
      file = new File(newName);
      file.getParentFile().mkdirs();

      try {
        BufferedWriter br = new BufferedWriter(new FileWriter(newName));
      
        for(int i = 0; i < array.length; i++) {
          br.write(array[i] + "\n");
        }
        br.close();
      } catch (IOException e) {
        throw new MPException("Error writing to Array");
      }
    } else {
      throw new MPException("Array trying to be saved has a length of 0");
    }
  }
  
  public static double[] load(String name) throws MPException {
    return load(name, "");
  }
  
  public static double[] load(String name, String ext) throws MPException {
    double[] array = null;
    File file;
    String newName = name;
    
    if(ext != "") {
      newName += "." + ext;
    }

    // These 2 lines are needed to save to folders not already created
    file = new File(newName);
    file.getParentFile().mkdirs();

    try {
      BufferedReader br = new BufferedReader(new FileReader(newName));
      ArrayList<Double> lines = new ArrayList<Double>();
      
      String line;
      while((line = br.readLine()) != null) {
        lines.add(Double.parseDouble(line));
      }
      br.close();
      
      array = new double[lines.size()];
      for(int i = 0; i < array.length; i++) {
        array[i] = lines.get(i);
      }
    } catch (IOException e) {
      throw new MPException("Error reading file to Array:" + newName);
    }
    
    return array;
  }
}