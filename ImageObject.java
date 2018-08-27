import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
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
  
  public ImageObject filterGaussian() {
    Mat kernel = Utils.matPut(5, 5, CvType.CV_32F, new int[][]{
      {1, 4, 7, 4,1},
      {4,16,26,16,4},
      {7,26,41,26,7},
      {4,16,26,16,4},
      {1, 4, 7, 4,1}});
    
    // [Q]What is the 1/273?
    
    Imgproc.filter2D(_img, _img, -1, kernel);

    return this;
  }
  
  public ImageObject filterLaplacian() {
    Mat kernel = Utils.matPut(3, 3, CvType.CV_32F, new int[][]{
      {0, 1,0},
      {1,-4,1},
      {0, 1,0}});
    
    // [Q]What is the 1/273?
    
    Imgproc.filter2D(_img, _img, -1, kernel);

    return this;
  }

  public ImageObject filterPrewit() {
    Mat kernel = Utils.matPut(3, 3, CvType.CV_32F, new int[][]{
      {-1,0,1},
      {-1,0,1},
      {-1,0,1}});
    
    Imgproc.filter2D(_img, _img, -1, kernel);

    kernel = Utils.matPut(3, 3, CvType.CV_32F, new int[][]{
      {-1,-1,-1},
      { 0, 0, 0},
      { 1, 1, 1}});
    
    Imgproc.filter2D(_img, _img, -1, kernel);
    return this;
  }
  
  public ImageObject filterSobel() {
    Mat kernel = Utils.matPut(3, 3, CvType.CV_32F, new int[][]{
      {-1,0,1},
      {-2,0,2},
      {-1,0,1}});
    
    Imgproc.filter2D(_img, _img, -1, kernel);

    kernel = Utils.matPut(3, 3, CvType.CV_32F, new int[][]{
      {-1,-2,-1},
      { 0, 0, 0},
      { 1, 2, 1}});
    
    Imgproc.filter2D(_img, _img, -1, kernel);
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