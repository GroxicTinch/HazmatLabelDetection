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

  // Constructors
  public ImageObject(Mat img) {
    _img = img;
  }

  // Getters
  public Mat getImg() { return _img; }

  public int getWidth() { return _img.width(); }
  public int getHeight() { return _img.height(); }

  public boolean isImage() { return true; }

  // Setters

  // Methods
  public Mat calcRGBHistogram(int bins, int histH, int histW) {
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
    Imgproc.calcHist(bgr_planes, new MatOfInt(1), new Mat(), histG, histSize, ranges);
    Imgproc.calcHist(bgr_planes, new MatOfInt(2), new Mat(), histR, histSize, ranges);
    
    Core.normalize(histB, histB, 0, histImageMat.height(), Core.NORM_MINMAX, -1, new Mat());
    Core.normalize(histG, histG, 0, histImageMat.height(), Core.NORM_MINMAX, -1, new Mat());
    Core.normalize(histR, histR, 0, histImageMat.height(), Core.NORM_MINMAX, -1, new Mat());

    for (int i = 0; i < bins; i++) {
      Imgproc.line(histImageMat, // ImageMat
                    new Point(binW * i, histH - Math.round(histB.get(i, 0)[0])),            // PointA of Line
                    new Point(binW * (i+1)    , histH - Math.round(histB.get(i+1, 0)[0])),  // PointB of Line
                    new Scalar(255, 0, 0),  // Colour
                    1, // Thickness
                    8, // lineType 8, 4 or CV_AA
                    0);// Shift

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

    return histImageMat;
  }

  // Destructive Modifications
  public void crop(Point p1, Point p2) {
    _img = returnCopyCrop(p1, p2);
  }

  public void crop(Point p1, int width, int height) {
    _img = returnCopyCrop(p1, width, height);
  }

  public void resizeToPixel(int newWidth, int newHeight) {
    _img = returnCopyResizeToPixel(newWidth, newHeight);
  }

  public void resizeToRatio(double newWidthRatio, double newHeightRatio) {
    _img = returnCopyResizeToRatio(newWidthRatio, newHeightRatio);
  }


  // Non-destructive Modifications
  public Mat returnCopyCrop(Point p1, Point p2) {
    int width = (int) (p2.x - p1.x + 1);
    int height = (int) (p2.y - p1.y + 1);
    
    return returnCopyCrop(p1, width, height);
  }

  public Mat returnCopyCrop(Point p1, int width, int height) {
    Bounds bounds = new Bounds(p1.x, p1.y, width, height);
    bounds.setLimits(0, 0, _img.width(), _img.height());

    return _img.submat(bounds.getRect());
  }

  public Mat returnCopyResizeToPixel(int newWidth, int newHeight) {
    Mat resizedImage = new Mat();
    Size size = new Size(newWidth, newHeight);

    Imgproc.resize(_img, resizedImage, size);

    return resizedImage;
  }

  public Mat returnCopyResizeToRatio(double newWidthRatio, double newHeightRatio) {
    Mat resizedImage = new Mat();

    int newWidth = (int) (_img.width() * newWidthRatio);
    int newHeight = (int) (_img.height() * newHeightRatio);

    Size size = new Size(newWidth, newHeight);

    Imgproc.resize(_img, resizedImage, size);

    return resizedImage;
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
  
  public static void saveAs(Mat img, String name, String ext) throws MPException {
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
      throw new MPException("Matrix trying to be saved has width/height of 0");
    }
  }
}