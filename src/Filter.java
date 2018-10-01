import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
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
  
//https://docs.opencv.org/trunk/d2/dbd/tutorial_distance_transform.html
 public static Mat distanceTransformRemoveBackground(Mat img, Mat foregroundMask) throws MPException {
   // White backgrounds cause issue, convert white pixels to black, using the foreground mask if possible
   // Converting to byte array and back means less .get and .put calls
   byte[] imgData = new byte[(int) (img.total() * img.channels())];
   img.get(0, 0, imgData);
   
   if(foregroundMask.size() != img.size()) {
     throw new MPException("Matrix size mismatch");
   }
 
   byte[] maskData = new byte[(int) (foregroundMask.total() * foregroundMask.channels())];
   
   img.get(0, 0, maskData);
   
   for(int row = 0; row < img.rows(); row++) {
     for(int col = 0; col < img.cols(); col++) {
       boolean blueMax = (maskData[(row * img.cols() + col) * 3] == (byte) 0);
       boolean greenMax = (maskData[(row * img.cols() + col) * 3 + 1] == (byte) 0);
       boolean redMax = (maskData[(row * img.cols() + col) * 3 + 2] == (byte) 0);
       
       if (blueMax && greenMax && redMax) {
         imgData[(row * img.cols() + col) * 3] = 0;
         imgData[(row * img.cols() + col) * 3 + 1] = 0;
         imgData[(row * img.cols() + col) * 3 + 2] = 0;
       }
     }
   }
   
   img.put(0, 0, imgData);
   
   return distanceTransform(img);
 }
 
 // https://docs.opencv.org/trunk/d2/dbd/tutorial_distance_transform.html
 public static Mat distanceTransform(Mat img) {
   Mat result = new Mat();
   Mat binary = new Mat();
   Mat dist = new Mat();
   
   int distType = Imgproc.DIST_L2;
   
   // Sharpen by using a strong Laplacian
   Core.subtract(img, laplacianStrong(img), result, new Mat(), CvType.CV_32F);
   
   // Convert 8bit for threshold function
   result.convertTo(result, CvType.CV_8U);
   
   Imgproc.cvtColor(result, binary, Imgproc.COLOR_BGR2GRAY);
   binary = thresholdOtsu(binary, 50);
   
   Imgproc.distanceTransform(binary, dist, distType, 3);
   
   // Normalize and then use threshold to get the groups(hopefully separated)
   Core.normalize(dist, dist, 0, 255, Core.NORM_MINMAX);
   
   // Find Peaks
   dist.convertTo(dist, CvType.CV_8U);
   dist = threshold(dist, 0.4 * 255);
   
   dist = morphDilate(dist, 3);
   
   return dist;
 }
  
  public static Mat gaussian(Mat img) {
    return gaussian(img, 5, 1);
  }
  
  public static Mat gaussian(Mat img, int size, double sigma) {
    Mat outputMat = new Mat();
    
    /*Mat kernel = Utils.matPut(CvType.CV_32F, new int[][]{
      {1, 4, 7, 4,1},
      {4,16,26,16,4},
      {7,26,41,26,7},
      {4,16,26,16,4},
      {1, 4, 7, 4,1}});
    
    Core.divide(kernel, new Scalar(273), kernel);
    
    Imgproc.filter2D(img, outputMat, -1, kernel);*/
    
   Imgproc.GaussianBlur(img, outputMat, new Size(5,5), sigma);
    
    return outputMat;
  }
  
  public static Mat gradientX(Mat img) {
    Mat outputMat = Mat.zeros(img.size(), CvType.CV_32F);
    Mat corrMat = new Mat();
    Mat convMat = new Mat();
    
    Mat kernel = Utils.matPut(CvType.CV_32F, new double[][]{
      {-1,0,1}});
    
    Imgproc.filter2D(img, corrMat, -1, kernel);
    Core.flip(kernel, kernel, 1); // 0 for x-axis flip, 1 for y-axis flip
    Imgproc.filter2D(img, convMat, -1, kernel);
  
    Core.add(corrMat, convMat, outputMat);

    return outputMat;
  }
  
  public static Mat gradientY(Mat img) {
    Mat outputMat = Mat.zeros(img.size(), CvType.CV_32F);
    Mat corrMat = new Mat();
    Mat convMat = new Mat();
    
    Mat kernel = Utils.matPut(CvType.CV_32F, new double[][]{
      {-1},
      { 0},
      { 1}});
    
    Imgproc.filter2D(img, corrMat, -1, kernel);
    Core.flip(kernel, kernel, 0); // 0 for x-axis flip, 1 for y-axis flip
    Imgproc.filter2D(img, convMat, -1, kernel);
  
    Core.add(corrMat, convMat, outputMat);

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
    
    Mat kernel = Utils.matPut(CvType.CV_32F, new double[][]{
      {0, 1,0},
      {1,-4,1},
      {0, 1,0}});
    
    Imgproc.filter2D(img, outputMat, -1, kernel);

    return outputMat;
  }
  
  public static Mat laplacianStrong(Mat img) {
    Mat outputMat = new Mat();
    
    Mat kernel = Utils.matPut(CvType.CV_32F, new double[][]{
      {1, 1,1},
      {1,-8,1},
      {1, 1,1}});
    
    Imgproc.filter2D(img, outputMat, -1, kernel);

    return outputMat;
  }
  
  public static Mat morphDilate(Mat mat, int elementSize) {
    Mat out = new Mat();
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(elementSize, elementSize));
    Imgproc.morphologyEx(mat, out, Imgproc.MORPH_DILATE, element);
    
    return out;
  }
  
  public static Mat morphErode(Mat mat, int elementSize) {
    Mat out = new Mat();
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(elementSize, elementSize));
    Imgproc.morphologyEx(mat, out, Imgproc.MORPH_ERODE, element);
    
    return out;
  }
  
  public static Mat morphOpen(Mat mat, int elementSize) {
    Mat out = new Mat();
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(elementSize, elementSize));
    Imgproc.morphologyEx(mat, out, Imgproc.MORPH_OPEN, element);
    
    return out;
  }
  
  public static Mat morphClose(Mat mat, int elementSize) {
    Mat out = new Mat();
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(elementSize, elementSize));
    Imgproc.morphologyEx(mat, out, Imgproc.MORPH_CLOSE, element);
       
    return out;
  }
  
  public static Mat morphGradient(Mat mat, int elementSize) {
    Mat out = new Mat();
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(elementSize, elementSize));
    Imgproc.morphologyEx(mat, out, Imgproc.MORPH_GRADIENT, element);
    
    return out;
  }
  
  public static Mat morphBlackhat(Mat mat, int elementSize) {
    Mat out = new Mat();
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(elementSize, elementSize));
    Imgproc.morphologyEx(mat, out, Imgproc.MORPH_BLACKHAT, element);
    
    return out;
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
    
    Mat kernel = Utils.matPut(CvType.CV_32F, new double[][]{
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
    
    Mat kernel = Utils.matPut(CvType.CV_32F, new double[][]{
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
  
  public static Mat threshold(Mat img, double thresh) {
    Mat outputMat = new Mat();
    
    Imgproc.threshold(img, outputMat, thresh, 255, Imgproc.THRESH_BINARY);
    
    return outputMat;
  }
  
  public static Mat thresholdInv(Mat img, double thresh) {
    Mat outputMat = new Mat();
    
    Imgproc.threshold(img, outputMat, thresh, 255, Imgproc.THRESH_BINARY_INV);
    
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
}
