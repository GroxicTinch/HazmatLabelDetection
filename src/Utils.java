import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Mat;

public class Utils {
  
  /*
   * Used to automatically create a matrix of the required size and fill it from an array so speed up
   * mat creation when hard coding things like filters
   */
  public static Mat matPut(int type, double[][] matData) {
    int width = matData[0].length;
    int height = matData.length;
    
    Mat matrix = new Mat(height, width, type);
    
    for(int row = 0; row < width; row++) {
      matrix.put(row, 0, matData[row]);
    }
    
    return matrix;
  }
  
  /*
   * Used to load in the Histogram Of Gradients (.hog) files
   */
  public static double[] loadDoubleArray(String name) throws MPException {
    return loadDoubleArray(name, "");
  }
  
  public static double[] loadDoubleArray(String name, String ext) throws MPException {
    double[] array = null;
    String newName = name;
    
    if(ext != "") {
      newName += "." + ext;
    }

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
  
  /*
   * Used to load in the the ExpectedTexts
   */
  public static ArrayList<String> loadStringList(String name) throws MPException {
    return loadStringList(name, "");
  }
  
  public static ArrayList<String> loadStringList(String name, String ext) throws MPException {
    ArrayList<String> stringList = new ArrayList<String>();
    String newName = name;
    
    if(ext != "") {
      newName += "." + ext;
    }
    
    try {
      BufferedReader br = new BufferedReader(new FileReader(newName));
      
      String line;
      while((line = br.readLine()) != null) {
        if(!line.startsWith("#")) {
          stringList.add(line);
        }
      }
      br.close();
    } catch (IOException e) {
      throw new MPException("Error reading file to Array:" + newName);
    }
    
    return stringList;
  }
}