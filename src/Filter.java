import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Filter {
  public static Mat filterGaussian(Mat img) {
    Mat outputMat = new Mat();
    
    Mat kernel = Utils.matPut(5, 5, CvType.CV_32F, new int[][]{
      {1, 4, 7, 4,1},
      {4,16,26,16,4},
      {7,26,41,26,7},
      {4,16,26,16,4},
      {1, 4, 7, 4,1}});
    
    // [Q]What is the 1/273?
    
    Imgproc.filter2D(img, outputMat, -1, kernel);

    return outputMat;
  }
  
  public static Mat filterLaplacian(Mat img) {
    Mat outputMat = new Mat();
    
    Mat kernel = Utils.matPut(3, 3, CvType.CV_32F, new int[][]{
      {0, 1,0},
      {1,-4,1},
      {0, 1,0}});
    
    // [Q]What is the 1/273?
    
    Imgproc.filter2D(img, outputMat, -1, kernel);

    return outputMat;
  }
  
  public static Mat filterPrewitt(Mat img) {
    Mat outputMat = new Mat();
    
    Core.add(filterPrewittX(img), filterPrewittY(img), outputMat);;
    
    return outputMat;
  }
  
  public static Mat filterPrewittX(Mat img) {
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
  
  public static Mat filterPrewittY(Mat img) {
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
  
  public static Mat filterSobel(Mat img) {
    Mat outputMat = new Mat();
    
    Core.add(filterSobelX(img), filterSobelY(img), outputMat);;
    
    return outputMat;
  }
  
  public static Mat filterSobelX(Mat img) {
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
  
  public static Mat filterSobelY(Mat img) {
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
}
