import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageObject {
  private Mat _mat;
  private Bounds _bounds;

  // Constructors
  public ImageObject(Mat img) {
    _mat = img;
    _bounds = new Bounds(0, 0, _mat.width(), _mat.height());
  }

  // Getters
  public Mat getMat() { return _mat; }

  public int getWidth() { return _mat.width(); }
  public int getHeight() { return _mat.height(); }
  
  public Bounds getBoundsCopy() { return _bounds.copy(); }

  public boolean isImage() { return true; }

  // Setters
  public void setBounds(Bounds bounds) {
    _bounds = bounds;
  }
  
  // Methods
  public Mat calcHistogram(int bins, int histH, int histW, boolean grayscale) {
    Mat histB = new Mat();
    Mat histG = new Mat();
    Mat histR = new Mat();
    
    // We need to add 1 to the bins or there will be an empty bin at the end
    MatOfInt histSize = new MatOfInt(bins+1);
    // 0 of any color is black so ignore it(which is why range starts from 1)
    MatOfFloat ranges = new MatOfFloat(1,255);
    
    // The last bin will cut off the last pixel unless histW is one less than its total
    float binW = (histW-1) / (float)bins;

    Mat histImageMat = new Mat(histH, histW, CvType.CV_8UC3, new Scalar(0,0,0));

    // We need to split the mat into the different colour channels BGR 
    List<Mat> bgr_planes = new ArrayList<Mat>();
    Core.split(_mat, bgr_planes);
    
    //                Mat array, channel as ints,      mask,  dest,     size, colour range
    Imgproc.calcHist(bgr_planes, new MatOfInt(0), new Mat(), histB, histSize, ranges);
    
    if(!grayscale) {
      Imgproc.calcHist(bgr_planes, new MatOfInt(1), new Mat(), histG, histSize, ranges);
      Imgproc.calcHist(bgr_planes, new MatOfInt(2), new Mat(), histR, histSize, ranges);
    }
    
    Core.normalize(histB, histB, 0, histImageMat.height(), Core.NORM_MINMAX, -1, new Mat());
    if(!grayscale) {
      Core.normalize(histG, histG, 0, histImageMat.height(), Core.NORM_MINMAX, -1, new Mat());
      Core.normalize(histR, histR, 0, histImageMat.height(), Core.NORM_MINMAX, -1, new Mat());
    }

    for (int i = 0; i < bins; i++) {
      Imgproc.line(histImageMat, // ImageMat
                    new Point(binW * i, histH - Math.round(histB.get(i, 0)[0])),            // PointA of Line
                    new Point(binW * (i+1)    , histH - Math.round(histB.get(i+1, 0)[0])),  // PointB of Line
                    new Scalar(255, 0, 0),  // Colour
                    1, // Thickness
                    8, // lineType 8, 4 or CV_AA
                    0);// Shift

      if(!grayscale) {
        Imgproc.line(histImageMat, // ImageMat
                    new Point(binW * i, histH - Math.round(histG.get(i, 0)[0])),           // PointA of Line
                    new Point(binW * (i+1), histH - Math.round(histG.get(i+1, 0)[0])),     // PointB of Line
                    new Scalar(0, 255, 0),  // Colour
                    1, // Thickness
                    8, // lineType 8, 4 or CV_AA
                    0);// Shift

        Imgproc.line(histImageMat, // ImageMat
                    new Point(binW * i, histH - Math.round(histR.get(i, 0)[0])),           // PointA of Line
                    new Point(binW * (i+1), histH - Math.round(histR.get(i+1, 0)[0])),     // PointB of Line
                    new Scalar(0, 0, 255),  // Colour
                    1, // Thickness
                    8, // lineType 8, 4 or CV_AA
                    0);// Shift
      }
    }

    return histImageMat;
  }
  
  // Should be used with grayscale
  public Point[] getHarrisCorners() {
    return getHarrisCorners(3, 1, 0.1, 200);
  }
  
  public Point[] getHarrisCorners(int blockSize, int apertureSize, double k, int threshold) {
    Mat harrisMat = new Mat();
    Mat harrisMatNormal = new Mat();
    
    ArrayList<Point> corners = new ArrayList<Point>();
    
    Imgproc.cornerHarris(_mat, harrisMat, blockSize, apertureSize, k);
    
    Core.normalize(harrisMat, harrisMatNormal, 0, 255, Core.NORM_MINMAX, CvType.CV_32F, new Mat());
    //Core.convertScaleAbs(harrisMatNormal, harrisMatNormalScaled);
    
    for( int row = 0; row < harrisMatNormal.rows() ; row++){
      for( int col = 0; col < harrisMatNormal.cols(); col++){
        int angle = (int) harrisMatNormal.get(row, col)[0]; // I think its angle
        
        if (angle > threshold){
          corners.add(new Point(row, col));
        }
      }
    }
    
    return (Point[])corners.toArray();
  }
  
  //Should be used with grayscale
  public Point[] getShiTomasiCorners() {
    return getShiTomasiCorners(0, 0.01, 10);
  }
  
  //https://github.com/opencv/opencv/blob/master/samples/java/tutorial_code/TrackingMotion/good_features_to_track/GoodFeaturesToTrackDemo.java
  public Point[] getShiTomasiCorners(int maxCorners, double quality, double minDist) {
    MatOfPoint corners = new MatOfPoint();
    
    Imgproc.goodFeaturesToTrack(_mat, corners, maxCorners, quality, minDist);
    
    return corners.toArray();
  }

  // Destructive Modifications, They WILL change the _img
  public ImageObject convert(int convertType) {
    Imgproc.cvtColor(_mat, _mat, convertType);
    return this;
  }
  
  public ImageObject convertToBW() {
    Imgproc.cvtColor(_mat, _mat, Imgproc.COLOR_BGR2GRAY);
    return this;
  }

  public ImageObject crop(Point p1, Point p2) {
    int width = (int) (p2.x - p1.x);
    int height = (int) (p2.y - p1.y);
    
    return crop(p1, width, height);
  }

  public ImageObject crop(Point p1, int width, int height) {
    _bounds.setBox(p1.x, p1.y, width, height);

    _mat = _mat.submat(_bounds.getBoxRect());
    return this;
  }
  
  public ImageObject drawBoundingBox(Point p1, Point p2, Scalar color) {
    Imgproc.rectangle(_mat, p1, p2, color, 2);
    return this;
  }
  
  public ImageObject drawBoundingBox(Bounds bounds, Scalar color) {
    drawBoundingBox(bounds.getBoxP1(), bounds.getBoxP2(), color);
    return this;
  }
  
  public ImageObject drawCornerCircles(Point p1, Point p2, Scalar color) {
    Imgproc.circle(_mat, p1, 5, color, 1);
    Imgproc.circle(_mat, new Point(p2.x, p1.y), 5, color, 1);
    Imgproc.circle(_mat, new Point(p1.x, p2.y), 5, color, 1);
    Imgproc.circle(_mat, p2, 5, color, 1);
    return this;
  }
  
  public ImageObject drawCornerCircles(Bounds bounds, Scalar color) {
    drawCornerCircles(bounds.getBoxP1(), bounds.getBoxP2(), color);
    return this;
  }
  
  public ImageObject equalizeContrast() {
    Imgproc.equalizeHist(_mat, _mat);
    return this;
  }
  
  public ImageObject resizeToPixel(int newWidth, int newHeight) {
    Size size = new Size(newWidth, newHeight);

    Imgproc.resize(_mat, _mat, size);
    return this;
  }

  public ImageObject resizeToRatio(double newWidthRatio, double newHeightRatio) {
    int newWidth = (int) (_mat.width() * newWidthRatio);
    int newHeight = (int) (_mat.height() * newHeightRatio);

    Size size = new Size(newWidth, newHeight);

    Imgproc.resize(_mat, _mat, size);
    return this;
  }
  
  public ImageObject copy() {
    ImageObject copy = new ImageObject(_mat);
    copy.setBounds(_bounds.copy());
    
    return copy;
  }
  
  public String matToString() {    
    return _mat.dump();
  }
}