import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

public class Bounds {
  double _x1Unbound;
  double _y1Unbound;
  double _widthUnbound;
  double _heightUnbound;
  
  double _x1;
  double _y1;
  double _width;
  double _height;
  
  public Bounds(double x1, double y1, double x2, double y2) {
    this(x1, y1, (int) (x2 - x1 + 1), (int) (y2 - y1 + 1));
  }
  
  public Bounds(double x1, double y1, int width, int height) {
    if(width < 0) {
      _x1Unbound = x1 - width;
      width = Math.abs(width);
    } else {
      _x1Unbound = x1;
    }
    if(height < 0) {
      _y1Unbound = y1 - height;
      height = Math.abs(height);
    } else {
      _y1Unbound = y1;
    }
    
    _widthUnbound = width;
    _heightUnbound = height;
  }
  
  // Getters
  public double getX1() { return _x1; }
  public double getY1() { return _y1; }
  public double getX2() { return _x1 + _width; }
  public double getY2() { return _x1 + _height; }
  
  public double getWidth() { return _width; }
  public double getHeight() { return _height; }
  
  public Rect getRect() {
    Point point = new Point(_x1, _y1);
    Size size = new Size(_width, _height);
    
    return new Rect(point, size);
  }
  
  public double getX1Unbound() { return _x1Unbound; }
  public double getY1Unbound() { return _y1Unbound; }
  public double getX2Unbound() { return _x1Unbound + _widthUnbound; }
  public double getY2Unbound() { return _x1Unbound + _heightUnbound; }
  
  public double getWidthUnbound() { return _widthUnbound; }
  public double getHeightUnbound() { return _heightUnbound; }
  
  public Rect getRectUnbound() {
    Point point = new Point(_x1Unbound, _y1Unbound);
    Size size = new Size(_widthUnbound, _heightUnbound);
    
    return new Rect(point, size);
  }
  // Setters
  
  
  // Methods
  public void setLimits(double x1, double y1, double x2, double y2) {
    int width = (int) (x2 - x1 + 1);
    int height = (int) (y2 - y1 + 1);
    
    setLimits (x1, y2, width, height);
  }
  
  public void setLimits(double x1, double y1, int width, int height) {
    int maxWidth;
    int maxHeight;
    
    // Make sure point1 is within the current Mat
    _x1 = Utils.clamp(_x1Unbound, x1, width);
    _y1 = Utils.clamp(_y1Unbound, y1, height);
    
    // Make sure the width and height doesn't push past the upper edge
    maxWidth = (int) (width - _x1);
    maxHeight = (int) (height - _y1);
    
    _width = Utils.clamp(_widthUnbound, 0, maxWidth);
    _height = Utils.clamp(_heightUnbound, 0, maxHeight);
  }
  
  public String toString() {
    return "x1: " + _x1 + "\ty1: " + _y1
          + "\nw: " + _width + "\th: " + _height;
  }
}
