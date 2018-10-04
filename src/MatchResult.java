import org.opencv.core.Rect;

public class MatchResult {
  private String _name;
  private Rect _rect;
  private double _result;
  
  public MatchResult() {
    _name = "";
  }

  public MatchResult(String name) {
    _name = name;
    _result = 0;
  }

  public MatchResult(String name, Rect rect, double result) {
    _name = name;
    _rect = rect;
    _result = result;
  }
  
  
  // Getters
  public String getName() {
    return _name;
  }

  public Rect getRect() {
    return _rect;
  }

  public double getResult() {
    return _result;
  }

  
  // Setters
  public void setName(String name) {
    _name = name;
  }
  
  public void setRect(Rect rect) {
    _rect = rect;
  }

  public void setResult(double result) {
    _result = result;
  }
}
