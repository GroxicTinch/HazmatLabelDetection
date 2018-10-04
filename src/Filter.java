import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Filter {
  // Apply Sobel filtering to generate edges
  public static Mat sobel(Mat img) {
    Mat outputMat = new Mat();
    
    Core.add(sobelX(img), sobelY(img), outputMat);;
    
    return outputMat;
  }
  
  public static Mat sobelX(Mat img) {
    Mat outputMat = new Mat();
    Mat corrMat = new Mat();
    Mat convMat = new Mat();
    
    Mat kernel = Utils.matPut(CvType.CV_32F, new double[][]{
      {-1,0,1},
      {-2,0,2},
      {-1,0,1}});
    
    Imgproc.filter2D(img, corrMat, -1, kernel);
    Core.flip(kernel, kernel, 1); // 0 for x-axis flip, 1 for y-axis flip
    Imgproc.filter2D(img, convMat, -1, kernel);
  
    Core.add(corrMat, convMat, outputMat);

    return outputMat;
  }
  
  public static Mat sobelY(Mat img) {
    Mat outputMat = new Mat();
    Mat corrMat = new Mat();
    Mat convMat = new Mat();
    
    Mat kernel = Utils.matPut(CvType.CV_32F, new double[][]{
      {-1,-2,-1},
      { 0, 0, 0},
      { 1, 2, 1}});
    
    Imgproc.filter2D(img, corrMat, -1, kernel);
    Core.flip(kernel, kernel, 0); // 0 for x-axis flip, 1 for y-axis flip
    Imgproc.filter2D(img, convMat, -1, kernel);
  
    Core.add(corrMat, convMat, outputMat);

    return outputMat;
  }
  
  // Shortcut functions to thresholding functions  
  public static Mat threshold(Mat img, double thresh) {
    Mat outputMat = new Mat();
    
    Imgproc.threshold(img, outputMat, thresh, 255, Imgproc.THRESH_BINARY);
    
    return outputMat;
  }
  
  public static Mat thresholdInv(Mat img, double thresh) {
    Mat outputMat = new Mat();
    
    Imgproc.threshold(img, outputMat, thresh, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_BINARY_INV);
    
    return outputMat;
  }
  
  public static Mat thresholdOtsu(Mat img, double thresh) {
    Mat outputMat = new Mat();
    
    Imgproc.threshold(img, outputMat, thresh, 255, Imgproc.THRESH_OTSU);
    
    return outputMat;
  }
  
  public static Mat thresholdOtsuInv(Mat img, double thresh) {
    Mat outputMat = new Mat();
    
    Imgproc.threshold(img, outputMat, thresh, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY_INV);
    
    return outputMat;
  }
  
  // Crop\Resize
  public static Mat crop(Mat mat, Point p1, int width, int height) {
    return crop(mat, p1, new Point(p1.x + width, p1.y + height));
  }
  
  public static Mat crop(Mat mat, Point p1, Point p2) {
    return mat.submat((int)p1.y, (int)p2.y, (int)p1.x, (int)p2.x);
  }
  
  public static Mat resizeToPixel(Mat mat, int newWidth, int newHeight) {
    Mat out = new Mat();
    Size size = new Size(newWidth, newHeight);

    Imgproc.resize(mat, out, size);
    return out;
  }
  
  // Used for template resizing to keep image ratio
  public static Mat resizeToPixelWidth(Mat mat, int newWidth) {
    Mat out = new Mat();
    double ratio = (double)newWidth / (double)mat.width();
    Size size = new Size(newWidth, (double)mat.height() * ratio);

    Imgproc.resize(mat, out, size);
    return out;
  }
}
