import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

public class Bounds {
  private double _boundsX1;
  private double _boundsY1;
  private double _boundsWidth;
  private double _boundsHeight;
  
  private double _boxX1;
  private double _boxY1;
  private double _boxWidth;
  private double _boxHeight;
  
  public Bounds(double boxX1, double boxY1, double boxX2, double boxY2) {
    this(boxX1, boxY1, (int) (boxX2 - boxX1 + 1), (int) (boxY2 - boxY1 + 1));
  }
  
  public Bounds(double boxX1, double boxY1, int boxWidth, int boxHeight) {
    this(boxX1, boxY1, boxWidth, boxHeight, boxX1, boxY1, boxWidth, boxHeight);
  }
  
  public Bounds(double boxX1, double boxY1, double boxX2, double boxY2, double boundsX1, double boundsY1, double boundsX2, double boundsY2) {
    this(boxX1, boxY1, (int) (boxX2 - boxX1), (int) (boxY2 - boxY1),
         boundsX1, boundsY1, (int) (boundsX2 - boundsX1), (int) (boundsY2 - boundsY1));
  }
  
  public Bounds(double boxX1, double boxY1, int boxWidth, int boxHeight, double boundsX1, double boundsY1, int boundsWidth, int boundsHeight) {
    setBounds(boundsX1, boundsY1, boundsWidth, boundsHeight);
    setBox(boxX1, boxY1, boxWidth, boxHeight);
  }
  
  // Getters
  public double getBoxX1() { return _boxX1; }
  public double getBoxY1() { return _boxY1; }
  public double getBoxX2() { return _boxX1 + _boxWidth; }
  public double getBoxY2() { return _boxX1 + _boxHeight; }
  
  public double getBoxWidth() { return _boxWidth; }
  public double getBoxHeight() { return _boxHeight; }
  
  public Point getBoxP1() { return new Point(getBoxX1(), getBoxY1()); }
  public Point getBoxP2() { return new Point(getBoxX2(), getBoxY2()); }
  
  public Rect getBoxRect() {
    Point point = new Point(_boxX1, _boxY1);
    Size size = new Size(_boxWidth, _boxHeight);
    
    return new Rect(point, size);
  }
  
  public double getBoundsX1() { return _boundsX1; }
  public double getBoundsY1() { return _boundsY1; }
  public double getBoundsX2() { return _boundsX1 + _boundsWidth; }
  public double getBoundsY2() { return _boundsX1 + _boundsHeight; }
  
  public double getBoundsWidth() { return _boundsWidth; }
  public double getBoundsHeight() { return _boundsHeight; }
  
  public Point getBoundsP1() { return new Point(getBoundsX1(), getBoundsY1()); }
  public Point getBoundsP2() { return new Point(getBoundsX2(), getBoundsY2()); }
  
  public Rect getBoundsRect() {
    Point point = new Point(_boundsX1, _boundsY1);
    Size size = new Size(_boundsWidth, _boundsHeight);
    
    return new Rect(point, size);
  }

  // Methods
  public void setBounds(double boxX1, double boxY1, double boxX2, double boxY2) {
    int boxWidth = (int) (boxX2 - boxX1);
    int boxHeight = (int) (boxY2 - boxY1);
    
    setBounds(boxX1, boxY2, boxWidth, boxHeight);
  }
  
  public void setBounds(double boundsX1, double boundsY1, int boundsWidth, int boundsHeight) {
    if(boundsWidth < 0) {
      _boundsX1 = boundsX1 - boundsWidth;
      boundsWidth = Math.abs(boundsWidth);
    } else {
      _boundsX1 = boundsX1;
    }
    if(boundsHeight < 0) {
      _boundsY1 = boundsY1 - boundsHeight;
      boundsHeight = Math.abs(boundsHeight);
    } else {
      _boundsY1 = boundsY1;
    }
    
    _boundsWidth = boundsWidth;
    _boundsHeight = boundsHeight;
  }
  
  public void setBox(double boxX1, double boxY1, double boxX2, double boxY2) {
    int boxWidth = (int) (boxX2 - boxX1);
    int boxHeight = (int) (boxY2 - boxY1);
    
    setBox(boxX1, boxY2, boxWidth, boxHeight);
  }
  
  public void setBox(double boxX1, double boxY1, int boxWidth, int boxHeight) {
    int maxWidth;
    int maxHeight;
    
    if(boxWidth < 0) {
      boxX1 = boxX1 - boxWidth;
      boxWidth = Math.abs(boxWidth);
    }
    if(boxHeight < 0) {
      boxY1 = boxY1 - boxHeight;
      boxHeight = Math.abs(boxHeight);
    }
    
    // Make sure point1 is within the current Mat
    _boxX1 = Utils.clamp(boxX1, _boundsX1, _boundsX1+_boundsWidth);
    _boxY1 = Utils.clamp(boxY1, _boundsY1, _boundsY1+_boundsHeight);
    
    // Make sure the boxWidth and boxHeight doesn't push past the upper edge
    maxWidth = (int) (_boundsWidth - (_boxX1 - _boundsX1));
    maxHeight = (int) (_boundsHeight - (_boxY1 - _boundsY1));
    
    _boxWidth = Utils.clamp(boxWidth, 0, maxWidth);
    _boxHeight = Utils.clamp(boxHeight, 0, maxHeight);
  }
  
  public Bounds copy() {
    Bounds bounds = new Bounds(getBoxX1(), getBoxY1(), getBoxWidth(), getBoxHeight(),
                              getBoundsX1(), getBoundsY1(), getBoundsWidth(), getBoundsHeight());
    
    return bounds;
  }
  
  public String toString() {
    return "boxX1: " + _boxX1 + "\tboxY1: " + _boxY1
          + "\nboxW: " + _boxWidth + "\tboxH: " + _boxHeight + "\n"
          + "boundsX1: " + _boundsX1 + "\tboundsY1: " + _boundsY1
          + "\nboundsW: " + _boundsWidth + "\tboundsH: " + _boundsHeight + "\n";
  }
}
