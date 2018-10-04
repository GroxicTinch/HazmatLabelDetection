import java.util.ArrayList;
import java.util.HashSet;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

public class ConnectedComponentsBlob {
  // A list of every pixel this blob is made of
  private HashSet<Point> _pixelList;
  
  // Top left
  private int _x;
  private int _y;
  
  // Bottom Right
  private int _x2;
  private int _y2;
  
  // blob is actually a mat the size of the original image it was taken from
  // this was used to easily locate where the blob was found
  private Mat _mat;
  
  // Width and Height are of the full mat
  private int _matWidth;
  private int _matHeight;
  
  // We only want to generate the Mat once until a pixel should be added, then alter it
  private boolean generateMat;
  
  //[TODO] Blob Boundry Extraction
  public ConnectedComponentsBlob(Mat img) {
    _pixelList = new HashSet<Point>();
    
    generateMat = true;
    
    _matWidth = img.cols();
    _matHeight = img.rows();
    
    _x = -1;
    _y = -1;
  }
  
  
  // Getters
  public int getX() {
    return _x;
  }
  
  public int getY() {
    return _y;
  }
  
  public int getX2() {
    return _x2;
  }
  
  public int getY2() {
    return _y2;
  }
  
  public int getWidth() {
    return (_x2 - _x) + 1;
  }

  public int getHeight() {
    return (_y2 - _y) + 1;
  }
  
  public int getWidthFull() {
    return _matWidth;
  }

  public int getHeightFull() {
    return _matHeight;
  }
  
  public Point tl() {
    return new Point(_x, _y);
  }

  public Point br() {
      return new Point(_x2, _y2);
  }
  
  public Mat getMat() {
    return getMatFull().submat(_y, _y2+1, _x, _x2+1);
  }
  
  public Mat getMatFull() {
    if(generateMat) {
      _mat = Mat.zeros(_matHeight, _matWidth, CvType.CV_8U);
      
      for(Point p : _pixelList) {
        _mat.put((int)p.y, (int)p.x, 255);
      }
      
      generateMat = false;
    }
    
    return _mat;
  }
  
  public HashSet<Point> getPixels() {
    return _pixelList;
  }
  
  // Methods
  public void add(Point pos) {
    generateMat = true;
    
    if(pos.x < _x || _x == -1) {
      _x = (int) pos.x;
    } 
    if(pos.x > _x2) {
      _x2 = (int) pos.x;
    }
    
    if(pos.y < _y || _y == -1) {
      _y = (int) pos.y;
    }
    if(pos.y > _y2) {
      _y2 = (int) pos.y;
    }
    
    _pixelList.add(pos);
  }
  
  // find the contours of the blob using Imgproc.findContours
  // assumes the blob is the whole mat
  public ArrayList<MatOfPoint> findApproxContours() {
    return findContours(Imgproc.CHAIN_APPROX_SIMPLE);
  }
  
  public ArrayList<MatOfPoint> findAbsContours() {
    return findContours(Imgproc.CHAIN_APPROX_NONE);
  }
  
  public ArrayList<MatOfPoint> findContours(int method) {
    ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    
    // FindContours changes the src Mat for some reason, so clone the original
    Imgproc.findContours(getMat().clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL, method);
    
    return contours;
  }
  
  // find the contours of the blob using Imgproc.findContours
  // uses the full mat size, useful for showing where something was found without
  // having to pass around point data as well
  public ArrayList<MatOfPoint> findApproxContoursFull() {
    return findContoursFull(Imgproc.CHAIN_APPROX_SIMPLE);
  }
  
  public ArrayList<MatOfPoint> findAbsContoursFull() {
    return findContoursFull(Imgproc.CHAIN_APPROX_NONE);
  }
  
  public ArrayList<MatOfPoint> findContoursFull(int method) {
    ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    
    // FindContours changes the src Mat for some reason, so clone the original
    Imgproc.findContours(getMatFull().clone(), contours, new Mat(), Imgproc.RETR_EXTERNAL, method);
    
    return contours;
  }

  // Casting because it rounds the number without it
  public double ratioForegroundBackground() {
    return (double)size() / ((double)getWidth() * (double)getHeight());
  }
  
  public double ratioWidthHeight() {
    return (double)getWidth() / (double)getHeight();
  }
  
  public int size() {
    return _pixelList.size();
  }
}
