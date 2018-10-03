import org.opencv.core.Rect;

public class TemplateMatchResult {
  private String _name;
  private Rect _rect;
  private double _percent;
  
  public TemplateMatchResult() {
    _name = "";
  }

  public TemplateMatchResult(String name) {
    _name = name;
    _percent = 0;
  }

  public TemplateMatchResult(String name, Rect rect, double percent) {
    _name = name;
    _rect = rect;
    _percent = percent;
  }
  
  
  // Getters
  public String getName() {
    return _name;
  }

  public Rect getRect() {
    return _rect;
  }

  public double getPercent() {
    return _percent;
  }

  
  // Setters
  public void setName(String name) {
    _name = name;
  }
  
  public void setRect(Rect rect) {
    _rect = rect;
  }

  public void setPercent(double percent) {
    _percent = percent;
  }
}
