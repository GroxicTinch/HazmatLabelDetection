import org.opencv.core.Point;

public class CharAndLoc implements Comparable<CharAndLoc> {
  private Character _c;
  private Point _loc;
  private Point _loc2;
  
  public CharAndLoc(Character c, Point loc, Point loc2) {
    _c = c;
    _loc = loc;
    _loc2 = loc2;
  }
  
  
  // Getters
  public Character getChar() {
    return _c;
  }

  public Point getLoc() {
    return _loc;
  }
  
  public Point getLoc2() {
    return _loc2;
  }

  
  // Setters
  public void setChar(Character c) {
    this._c = c;
  }

  public void setLoc(Point loc) {
    this._loc = loc;
  }
  
  public void setLoc2(Point loc) {
    this._loc2 = loc;
  }


  @Override
  public int compareTo(CharAndLoc otherCharAndLoc) {
    if (!(otherCharAndLoc instanceof CharAndLoc)) {
      throw new ClassCastException("same object expected");
    }
    
    if(_loc.x < otherCharAndLoc.getLoc().x && _loc.y < otherCharAndLoc.getLoc2().y) {
      return -1;
    }
    
    if(_loc2.y > otherCharAndLoc.getLoc().y) {
      return 1;
    } 

    return 0;
  }
}
