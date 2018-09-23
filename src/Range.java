
public class Range {
  // Doesnt need to be protected so using public without _ prefix
  public double min;
  public double max;
  
  /**
   * @param min
   * @param max
   */
  public Range(double min, double max) {
    this.min = min;
    this.max = max;
  }
  
  /**
   * Inclusive is in range
   * 
   * @param value
   */
  public boolean contains(double value) {
    return (min <= value && value <= max);
  }
}
