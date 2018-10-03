import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
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

  // Destructive Modifications, They WILL change the _img
  public ImageObject convert(int convertType) {
    Imgproc.cvtColor(_mat, _mat, convertType);
    return this;
  }
  
  public ImageObject convertToBW() {
    Imgproc.cvtColor(_mat, _mat, Imgproc.COLOR_BGR2GRAY);
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
  
  public ImageObject copy() {
    ImageObject copy = new ImageObject(_mat);
    copy.setBounds(_bounds.copy());
    
    return copy;
  }
  
  public String matToString() {    
    return _mat.dump();
  }
}