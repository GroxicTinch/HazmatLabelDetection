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
  private int _x;
  private int _y;
  private int _x2;
  private int _y2;
  
  private Mat _mat;
  private int _matWidth;
  private int _matHeight;
  
  private boolean generateMat;
  
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
  
  public ArrayList<MatOfPoint> findApproxContours() {
    return findContours(Imgproc.CHAIN_APPROX_SIMPLE);
  }
  
  public ArrayList<MatOfPoint> findAbsContours() {
    return findContours(Imgproc.CHAIN_APPROX_NONE);
  }
  
  public ArrayList<MatOfPoint> findContours(int method) {
    ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    
    Imgproc.findContours(getMat(), contours, new Mat(), Imgproc.RETR_EXTERNAL, method);
    
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
    
    Imgproc.findContours(getMatFull(), contours, new Mat(), Imgproc.RETR_EXTERNAL, method);
    
    return contours;
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
  
  // Casting because it rounds the number without it
  public double ratio() {
    return (double)size() / (double)(getWidth() * (double)getHeight());
  }
  
  public int size() {
    return _pixelList.size();
  }
}
