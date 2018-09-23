import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Filter {
  public static Mat borderConstant(Mat img, int value) {
    return borderConstant(img, 1, value);
  }
  
  public static Mat borderConstant(Mat img, int thickness, int value) {
    // src, dst, top, btm, left, right, type
    Core.copyMakeBorder(img, img, thickness, thickness, thickness, thickness, Core.BORDER_CONSTANT, new Scalar(value));
    return img;
  }
  
  public static Mat gaussian(Mat img) {
    return gaussian(img, 1);
  }
  
  public static Mat gaussian(Mat img, double multi) {
    Mat outputMat = new Mat();
    
    Mat kernel = Utils.matPut(5, 5, CvType.CV_32F, new int[][]{
      {1, 4, 7, 4,1},
      {4,16,26,16,4},
      {7,26,41,26,7},
      {4,16,26,16,4},
      {1, 4, 7, 4,1}});
    
    Core.divide(kernel, new Scalar(273), kernel);
    
    Imgproc.filter2D(img, outputMat, -1, kernel);

    return outputMat;
  }
  
  public static Mat houghCustom(Mat img) {
    Mat outputMat = new Mat();
    Mat houghImage = new Mat();
    
    for(int col = 0; col < img.width(); col++) {
      for(int row = 0; row < img.width(); row++) {
        if(img.get(row, col)[0] == 255) {
          // Should pass if on a line,       don't need 360 because a line pointing up is the same as a line pointing down
          for(int currAngle = 0; currAngle < 180; currAngle++) {
            double xcosO = col*Math.cos(currAngle);
            double ysinO = row*Math.sin(currAngle);
            
            int dist = (int) Math.round(xcosO + ysinO);
            
            if(dist >= 0) { // [Q] Dont fully understand this
              int currVal = (int) (houghImage.get(currAngle, dist)[0] + 1);
              
              houghImage.put(currAngle, dist, currVal);
            }
          }
        }
      }
    }

    return outputMat;
  }
  
  public static Mat hough(Mat img) {
    Mat outputMat = new Mat();
    
    Imgproc.HoughLines(img, outputMat, 0, 0, 1);

    return outputMat;
  }
  
  public static Mat laplacian(Mat img) {
    Mat outputMat = new Mat();
    
    Mat kernel = Utils.matPut(3, 3, CvType.CV_32F, new int[][]{
      {0, 1,0},
      {1,-4,1},
      {0, 1,0}});
    
    Imgproc.filter2D(img, outputMat, -1, kernel);

    return outputMat;
  }
  
  public static Mat prewitt(Mat img) {
    Mat outputMat = new Mat();
    
    Core.add(prewittX(img), prewittY(img), outputMat);;
    
    return outputMat;
  }
  
  public static Mat prewittX(Mat img) {
    Mat outputMat = new Mat();
    Mat corrMat = new Mat();
    Mat convMat = new Mat();
    
    Mat kernel = Utils.matPut(3, 3, CvType.CV_32F, new int[][]{
      {-1,0,1},
      {-1,0,1},
      {-1,0,1}});
    
    Imgproc.filter2D(img, corrMat, -1, kernel);
    Core.flip(kernel, kernel, 1); // 0 for x-axis flip, 1 for y-axis flip
    Imgproc.filter2D(img, convMat, -1, kernel);
  
    Core.add(corrMat, convMat, outputMat);

    return outputMat;
  }
  
  public static Mat prewittY(Mat img) {
    Mat outputMat = new Mat();
    Mat corrMat = new Mat();
    Mat convMat = new Mat();
    
    Mat kernel = Utils.matPut(3, 3, CvType.CV_32F, new int[][]{
      {-1,-1,-1},
      { 0, 0, 0},
      { 1, 1, 1}});
    
    Imgproc.filter2D(img, corrMat, -1, kernel);
    Core.flip(kernel, kernel, 0); // 0 for x-axis flip, 1 for y-axis flip
    Imgproc.filter2D(img, convMat, -1, kernel);
  
    Core.add(corrMat, convMat, outputMat);

    return outputMat;
  }
  
  public static Mat sobel(Mat img) {
    Mat outputMat = new Mat();
    
    Core.add(sobelX(img), sobelY(img), outputMat);;
    
    return outputMat;
  }
  
  public static Mat sobelX(Mat img) {
    Mat outputMat = new Mat();
    Mat corrMat = new Mat();
    Mat convMat = new Mat();
    
    Mat kernel = Utils.matPut(3, 3, CvType.CV_32F, new int[][]{
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
    
    Mat kernel = Utils.matPut(3, 3, CvType.CV_32F, new int[][]{
      {-1,-2,-1},
      { 0, 0, 0},
      { 1, 2, 1}});
    
    Imgproc.filter2D(img, corrMat, -1, kernel);
    Core.flip(kernel, kernel, 0); // 0 for x-axis flip, 1 for y-axis flip
    Imgproc.filter2D(img, convMat, -1, kernel);
  
    Core.add(corrMat, convMat, outputMat);

    return outputMat;
  }
  
  public static Mat threshold(Mat img, double thresh) {
    Mat outputMat = new Mat();
    
    Imgproc.threshold(img, outputMat, thresh, 255, Imgproc.THRESH_OTSU);
    
    return outputMat;
  }
  
  public static Mat thresholdInv(Mat img, double thresh) {
    Mat outputMat = new Mat();
    
    Imgproc.threshold(img, outputMat, thresh, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY_INV);
    
    return outputMat;
  }
}
