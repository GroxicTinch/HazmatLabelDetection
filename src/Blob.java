import org.opencv.core.Mat;

public class Blob {
  Mat _mat;
  int _width;
  int _height;
  
  double _ratio;
  
  /**
   * @param height
   * @param width
   * @param ratio
   */
  public Blob(Mat mat, int width, int height, double ratio) {
    _mat = mat;
    _width = width;
    _height = height;
    _ratio = ratio;
  }

  
  // Getters
  public Mat getMat() {
    return _mat;
  }

  public int getWidth() {
    return _width;
  }
  
  public int getHeight() {
    return _height;
  }

  public double getRatio() {
    return _ratio;
  }
  
  
  // Setters
  public void setMat(Mat mat) {
    this._mat = mat;
  }

  public void setWidth(int width) {
    _width = width;
  }
  
  public void setHeight(int height) {
    _height = height;
  }

  public void setRatio(double ratio) {
    _ratio = ratio;
  }
}
