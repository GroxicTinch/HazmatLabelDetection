public class ShapeDescriptor {
  private String _shapeName;
  private Range _backForeRatioRange;
  private Range _heightWidthRatioRange;
  private int _corners;
  
//[TODO] Add bins, Distribution of pixels in X and Y
  
  /**
   * @param _shapeName
   * @param _backForeRatioRange
   * @param _heightWidthRatioRange
   * @param _corners
   */
  public ShapeDescriptor(String shapeName, Range backForeRatioRange, Range heightWidthRatioRange, int corners) {
    _shapeName = shapeName;
    _backForeRatioRange = backForeRatioRange;
    _heightWidthRatioRange = heightWidthRatioRange;
    _corners = corners;
  }

  public String getShapeName() {
    return _shapeName;
  }

  public Range getBackForeRatioRange() {
    return _backForeRatioRange;
  }

  public Range getHeightWidthRatioRange() {
    return _heightWidthRatioRange;
  }

  public int getCorners() {
    return _corners;
  }

  // Shapes should have unique names
  @Override
  public int hashCode() {
    return _shapeName.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ShapeDescriptor other = (ShapeDescriptor) obj;
    if (_shapeName == null) {
      if (other._shapeName != null)
        return false;
    } else if (!_shapeName.equals(other._shapeName))
      return false;
    return true;
  }
}
