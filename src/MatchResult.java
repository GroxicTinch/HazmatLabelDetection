/*
 * Used to easily store\return what template\ocr matches and the weight that it does
 */
public class MatchResult {
  private String _name;
  private double _result;
  
  public MatchResult() {
    _name = "";
  }

  public MatchResult(String name) {
    _name = name;
    _result = 0;
  }

  public MatchResult(String name, double result) {
    _name = name;
    _result = result;
  }
  
  
  // Getters
  public String getName() {
    return _name;
  }

  public double getResult() {
    return _result;
  }

  
  // Setters
  public void setName(String name) {
    _name = name;
  }

  public void setResult(double result) {
    _result = result;
  }
}
