import org.opencv.core.Mat;

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
}