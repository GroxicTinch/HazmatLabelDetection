import java.util.HashSet;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

public class ConnectedComponentsBlob {
  private HashSet<Point> _pixelList;
  private int _x;
  private int _y;
  private int _x2;
  private int _y2;
  
  private Mat _mat;
  
  private boolean generateMat;
  
  public ConnectedComponentsBlob() {
    _pixelList = new HashSet<Point>();
    generateMat = true;
    
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
    return (_x2 - _x);
  }

  public int getHeight() {
    return (_y2 - _y);
  }
  
  public Mat getMat() {
    if(generateMat) {
      _mat = Mat.zeros(getHeight()+1, getWidth()+1, CvType.CV_8U);
      
      for(Point p : _pixelList) {
        int newY = (int)p.y - _y;
        int newX = (int)p.x - _x;
        
        _mat.put(newY, newX, 255);
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
