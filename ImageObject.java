import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageObject {
  Mat _img;

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
  public Mat calcRGBHistogram(int bins) {
    Mat histB = new Mat();
    Mat histG = new Mat();
    Mat histR = new Mat();
    MatOfInt histSize = new MatOfInt(256);
    MatOfFloat ranges = new MatOfFloat(0,256);

    Mat histImageMat = new Mat(histSize.height(), histSize.width(), CvType.CV_8UC3, new Scalar(0,0,0));

    List<Mat> bgr_planes = new ArrayList<Mat>();
    bgr_planes.add(_img);

    Imgproc.calcHist(bgr_planes, new MatOfInt(0), new Mat(), histB, histSize, ranges);
    Imgproc.calcHist(bgr_planes, new MatOfInt(1), new Mat(), histG, histSize, ranges);
    Imgproc.calcHist(bgr_planes, new MatOfInt(2), new Mat(), histR, histSize, ranges);

    Core.normalize(histB, histB, 0, histImageMat.rows(), Core.NORM_MINMAX, -1, new Mat() );
    Core.normalize(histG, histG, 0, histImageMat.rows(), Core.NORM_MINMAX, -1, new Mat() );
    Core.normalize(histR, histR, 0, histImageMat.rows(), Core.NORM_MINMAX, -1, new Mat() );

    for (int i = 1; i < histSize.get(0, 0)[0]; i++) {
      Imgproc.line(histImageMat, // ImageMat
                    new Point(bins * (i - 1), histSize.height() - Math.round(histB.get(i - 1, 0)[0])), // PointA of Line
                    new Point(bins * (i)    , histSize.height() - Math.round(histB.get(i, 0)[0])),     // PointB of Line
                    new Scalar(255, 0, 0),  // Colour
                    2, // Thickness
                    8, // lineType 8, 4 or CV_AA
                    0);// Shift

      Imgproc.line(histImageMat, // ImageMat
                    new Point(bins * (i - 1), histSize.height() - Math.round(histG.get(i - 1, 0)[0])), // PointA of Line
                    new Point(bins * (i)    , histSize.height() - Math.round(histG.get(i, 0)[0])),     // PointB of Line
                    new Scalar(0, 255, 0),  // Colour
                    2, // Thickness
                    8, // lineType 8, 4 or CV_AA
                    0);// Shift

      Imgproc.line(histImageMat, // ImageMat
                    new Point(bins * (i - 1), histSize.height() - Math.round(histR.get(i - 1, 0)[0])), // PointA of Line
                    new Point(bins * (i)    , histSize.height() - Math.round(histR.get(i, 0)[0])),     // PointB of Line
                    new Scalar(0, 0, 255),  // Colour
                    2, // Thickness
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

  public void resizeToRatio(int newWidthRatio, int newHeightRatio) {
    _img = returnCopyResizeToPixel(newWidthRatio, newHeightRatio);
  }


  // Non-destructive Modifications
  public Mat returnCopyCrop(Point p1, Point p2) {
    int width = (int) (p2.x - p1.x + 1);
    int height = (int) (p2.y - p1.y + 1);

    return returnCopyCrop(p1, width, height);
  }

  public Mat returnCopyCrop(Point p1, int width, int height) {
    Size size = new Size(width, height);
    Rect rect = new Rect(p1, size);

    return _img.submat(rect);
  }

  public Mat returnCopyResizeToPixel(int newWidth, int newHeight) {
    Mat resizedImage = new Mat();
    Size size = new Size(newWidth, newHeight);

    Imgproc.resize(_img, resizedImage, size);

    return resizedImage;
  }

  public Mat returnCopyResizeToRatio(int newWidthRatio, int newHeightRatio) {
    Mat resizedImage = new Mat();

    int newWidth = _img.width() * newWidthRatio;
    int newHeight = _img.height() * newHeightRatio;

    Size size = new Size(newWidth, newHeight);

    Imgproc.resize(_img, resizedImage, size);

    return resizedImage;
  }
}