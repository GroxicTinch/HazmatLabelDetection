import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

public class ConnectedComponentsBlob {
  private HashSet<Point> _pixelList;
  private Point[] _corners;
  
  private int _x;
  private int _y;
  private int _x2;
  private int _y2;
  
  private Mat _mat;
  private int _matWidth;
  private int _matHeight;
  
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
  
  public Point[] getCornersHarris() {
    if(_corners == null) {
      Mat img = Filter.borderConstant(getMat(), 0);
      _corners = Filter.cornersHarris(img);
    }
    
    return _corners;
  }
  
  public Point[] getCornersShiTomasi() {
    if(_corners == null) {
      Mat img = Filter.borderConstant(getMat(), 0);
      _corners = Filter.cornersShiTomasi(img);
    }
    
    return _corners;
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
  
  public int getPerimeter() {
    return (_matWidth + _matHeight) * 2;
  }
  
  public HashSet<Point> getPixels() {
    return _pixelList;
  }
  
  // Methods
  public void add(Point pos) {
    generateMat = true;
    
    if(pos.x < _x || _x == -1) {
      _x = (int) pos.x;
    } else if(pos.x > _x2) {
      _x2 = (int) pos.x;
    }
    
    if(pos.y < _y || _y == -1) {
      _y = (int) pos.y;
    } else if(pos.y > _y2) {
      _y2 = (int) pos.y;
    }
    
    _pixelList.add(pos);
  }
  
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
  
  public void generateBoundaryLists() {
    // Get the mat, add a border of 0 so that edge detection gets edge pixels, then perform sobel filter
    Mat edgeMat = Filter.laplacian(Filter.borderConstant(getMat(), 0));
    // [TODO] get edge Mats
  }
  
  // Casting because it rounds the number without it
  public double ratio() {
    return (double)size() / (double)(getWidth() * (double)getHeight());
  }
  
  public int size() {
    return _pixelList.size();
  }
}
