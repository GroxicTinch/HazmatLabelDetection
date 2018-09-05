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
  private Mat _img;
  private Bounds _bounds;

  // Constructors
  public ImageObject(Mat img) {
    _img = img;
    _bounds = new Bounds(0, 0, _img.width(), _img.height());
  }

  // Getters
  public Mat getImg() { return _img; }

  public int getWidth() { return _img.width(); }
  public int getHeight() { return _img.height(); }
  
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
    Core.split(_img, bgr_planes);
    
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
  
  public String getMainColor() {
    Mat mask = new Mat();
    return getMainColor(mask);
  }
  
  public String getMainColor(Mat mask) {
    int saturationThreshold = 51; // approx 20%
    int brightnessThreshold = 76; // approx 30%
    
    // Hue goes from 0-180, if we allow 5 for each group it should give us an accurate enough color guess
    int binsH = 36;
    int binsSV = 255;
    
    String mainColor = "ERROR";
    Mat hsvImg = new Mat();
    Mat histH = new Mat();
    Mat histS = new Mat();
    Mat histV = new Mat();
    
    MatOfInt histSizeH = new MatOfInt(binsH+1);
    MatOfInt histSizeSV = new MatOfInt(binsSV+1);
    
    MatOfFloat rangesH = new MatOfFloat(0,180);
    MatOfFloat rangesSV = new MatOfFloat(0,256);
    
    int maxBinH = -1;
    double maxH = 0;
    int maxBinS = -1;
    double maxS = 0;
    int maxBinV = -1;
    double maxV = 0;
    
    Imgproc.cvtColor(_img, hsvImg, Imgproc.COLOR_BGR2HSV);
    
    // We need to split the mat into the different colour channels BGR 
    List<Mat> hsv_planes = new ArrayList<Mat>();
    Core.split(hsvImg, hsv_planes);
    
    //[TODO] Create a mask
    
    Imgproc.calcHist(hsv_planes, new MatOfInt(0), mask, histH, histSizeH, rangesH);
    Imgproc.calcHist(hsv_planes, new MatOfInt(1), mask, histS, histSizeSV, rangesSV);
    Imgproc.calcHist(hsv_planes, new MatOfInt(2), mask, histV, histSizeSV, rangesSV);
    
    /*if(!histH.empty()) {
      return histH.dump();
    }*/
    
    /*if(!histS.empty()) {
      return histS.dump();
    }*/
    
    /*if(!histV.empty()) {
      return histV.dump();
    }*/
    
    for(int i = 0; i < binsH; i++) {
      double countH = histH.get(i, 0)[0];
            
      if(countH > maxH) {
        maxBinH = i;
        maxH = countH;
      }
    }
    
    for(int i = 0; i <= 255; i++) {
      double countS = histS.get(i, 0)[0];
      double countV = histV.get(i, 0)[0];
            
      if(countS > maxS) {
        maxBinS = i;
        maxS = countS;
      }
      if(countV > maxV) {
        maxBinV = i;
        maxV = countV;
      }
    }
    
    if(maxBinV > brightnessThreshold) {
      // It is probably not black
      
      if(maxBinS > saturationThreshold) {
        // It is probably a color
        
        if(maxBinH <= 1 || maxBinH >= 32) {
          // H between 0-10 + 175-180
          mainColor = "Red";
        } else if(maxBinH >= 2 && maxBinH <= 3) {
          // H between 10-25
          mainColor = "Orange" ;
        } else if(maxBinH >= 4 && maxBinH <= 6) {
          // H between 25-35
          mainColor = "Yellow";
        } else if(maxBinH >= 7 && maxBinH <= 16) {
          // H between 35-85
          mainColor = "Green";
        } else if(maxBinH >= 17 && maxBinH <= 27) {
          // H between 85-140
          mainColor = "Blue";
        } else if(maxBinH >= 28 && maxBinH <= 31) {
          // H between 140-160
          mainColor = "Pink"; // This shouldn't happen in the assignment...
        }
      } else {
        // It is probably white
        mainColor = "White";
      }
    } else {
      // It is probably black
      mainColor = "Black";
    }
    
    return mainColor;
  }
  
  // Should be used with grayscale
  public Point[] getHarrisCorners() {
    return getHarrisCorners(3, 1, 0.1, 200);
  }
  
  public Point[] getHarrisCorners(int blockSize, int apertureSize, double k, int threshold) {
    Mat harrisMat = new Mat();
    Mat harrisMatNormal = new Mat();
    
    ArrayList<Point> corners = new ArrayList<Point>();
    
    Imgproc.cornerHarris(_img, harrisMat, blockSize, apertureSize, k);
    
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
    
    Imgproc.goodFeaturesToTrack(_img, corners, maxCorners, quality, minDist);
    
    return corners.toArray();
  }

  // Destructive Modifications, They WILL change the _img
  public ImageObject convert(int convertType) {
    Imgproc.cvtColor(_img, _img, convertType);
    return this;
  }

  public ImageObject crop(Point p1, Point p2) {
    int width = (int) (p2.x - p1.x);
    int height = (int) (p2.y - p1.y);
    
    return crop(p1, width, height);
  }

  public ImageObject crop(Point p1, int width, int height) {
    _bounds.setBox(p1.x, p1.y, width, height);

    _img = _img.submat(_bounds.getBoxRect());
    return this;
  }
  
  public ImageObject drawBoundingBox(Point p1, Point p2, Scalar color) {
    Imgproc.rectangle(_img, p1, p2, color, 2);
    return this;
  }
  
  public ImageObject drawBoundingBox(Bounds bounds, Scalar color) {
    drawBoundingBox(bounds.getBoxP1(), bounds.getBoxP2(), color);
    return this;
  }
  
  public ImageObject drawCornerCircles(Point p1, Point p2, Scalar color) {
    Imgproc.circle(_img, p1, 5, color, 1);
    Imgproc.circle(_img, new Point(p2.x, p1.y), 5, color, 1);
    Imgproc.circle(_img, new Point(p1.x, p2.y), 5, color, 1);
    Imgproc.circle(_img, p2, 5, color, 1);
    return this;
  }
  
  public ImageObject drawCornerCircles(Bounds bounds, Scalar color) {
    drawCornerCircles(bounds.getBoxP1(), bounds.getBoxP2(), color);
    return this;
  }
  
  public ImageObject equalizeContrast() {
    Imgproc.equalizeHist(_img, _img);
    return this;
  }

  public ImageObject morphDilate(int elementSize) {
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*elementSize + 1, 2*elementSize+1));
    Imgproc.morphologyEx(_img, _img, Imgproc.MORPH_DILATE, element);
    
    return this;
  }
  
  public ImageObject morphErode(int elementSize) {
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*elementSize + 1, 2*elementSize+1));
    Imgproc.morphologyEx(_img, _img, Imgproc.MORPH_ERODE, element);
    
    return this;
  }
  
  public ImageObject morphOpen(int elementSize) {
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*elementSize + 1, 2*elementSize+1));
    Imgproc.morphologyEx(_img, _img, Imgproc.MORPH_OPEN, element);
    
    return this;
  }
  
  public ImageObject morphClose(int elementSize) {
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*elementSize + 1, 2*elementSize+1));
    Imgproc.morphologyEx(_img, _img, Imgproc.MORPH_CLOSE, element);
       
    return this;
  }
  
  public ImageObject morphGradient(int elementSize) {
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*elementSize + 1, 2*elementSize+1));
    Imgproc.morphologyEx(_img, _img, Imgproc.MORPH_GRADIENT, element);
    
    return this;
  }
  
  public ImageObject morphBlackhat(int elementSize) {
    Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*elementSize + 1, 2*elementSize+1));
    Imgproc.morphologyEx(_img, _img, Imgproc.MORPH_BLACKHAT, element);
    
    return this;
  }
  
  public ImageObject resizeToPixel(int newWidth, int newHeight) {
    Size size = new Size(newWidth, newHeight);

    Imgproc.resize(_img, _img, size);
    return this;
  }

  public ImageObject resizeToRatio(double newWidthRatio, double newHeightRatio) {
    int newWidth = (int) (_img.width() * newWidthRatio);
    int newHeight = (int) (_img.height() * newHeightRatio);

    Size size = new Size(newWidth, newHeight);

    Imgproc.resize(_img, _img, size);
    return this;
  }
  
  public void saveAs(String name) throws MPException {
    saveAs(name, ".png");
  }

  public void saveAs(String name, String ext) throws MPException {
    ImageObject.saveAs(_img, name, ext);
  }
  
  public static void saveAs(Mat img, String name) throws MPException {
    saveAs(img, name, "png");
  }
  
  public static void saveAs(Mat img, String name, String ext) {
    File file;
    String newName = name;
    
    if(img.width() > 0 && img.height() > 0) {
      if(ext != "") {
        newName += "." + ext;
      }
  
      // These 2 lines are needed to save to folders not already created
      file = new File(newName);
      file.getParentFile().mkdirs();
      
      Imgcodecs.imwrite(newName, img);
    } else {
      try {
        throw new MPException("Matrix trying to be saved has width/height of 0");
      } catch (MPException e) {
        System.out.println(e.toString());
      }
    }
  }
  
  public ImageObject copy() {
    ImageObject copy = new ImageObject(_img);
    copy.setBounds(_bounds.copy());
    
    return copy;
  }
  
  public String matToString() {    
    return _img.dump();
  }
}