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
}