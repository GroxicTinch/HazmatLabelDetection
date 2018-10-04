import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ImageObject {
  private Mat _mat;

  // Constructors
  public ImageObject(Mat img) {
    _mat = img;
  }

  // Getters
  public Mat getMat() { return _mat; }

  public int getWidth() { return _mat.width(); }
  public int getHeight() { return _mat.height(); }

  public boolean isImage() { return true; }

  // Destructive Modifications, They WILL change the _img
  public ImageObject convert(int convertType) {
    Imgproc.cvtColor(_mat, _mat, convertType);
    return this;
  }
  
  public ImageObject convertToBW() {
    Imgproc.cvtColor(_mat, _mat, Imgproc.COLOR_BGR2GRAY);
    return this;
  }
  
  public ImageObject equalizeContrast() {
    Imgproc.equalizeHist(_mat, _mat);
    return this;
  }
  
  public ImageObject copy() {
    ImageObject copy = new ImageObject(_mat);
    
    return copy;
  }
  
  public String matToString() {    
    return _mat.dump();
  }
}