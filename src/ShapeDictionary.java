import java.util.HashMap;
import java.util.HashSet;

public class ShapeDictionary {
  private static ShapeDictionary _singleton = null;
  private HashMap<String, HashSet<ShapeDescriptor>> _shapeCategories;

  /**
   * @param _shapeCategories
   */
  private ShapeDictionary() {
    String categoryName;
    HashSet<ShapeDescriptor> shapeSet = new HashSet<ShapeDescriptor>();
    
    // NUMBERS *************************************************************
    categoryName = "Characters";
    shapeSet.add(new ShapeDescriptor(
        "0",                          // Shape Name
        new Range(0,0),               // Foreground\Background Ratio Range
        new Range(0,0),               // Height to Width Ratio Range
        0));                          // Corners in shape
    
    shapeSet.add(new ShapeDescriptor(
        "1",
        new Range(0.65,0.66),
        new Range(0,0),
        7));
    
    shapeSet.add(new ShapeDescriptor(
        "2",
        new Range(0,0),
        new Range(0,0),
        0));
    
    shapeSet.add(new ShapeDescriptor(
        "3",
        new Range(0,0),
        new Range(0,0),
        0));
    
    shapeSet.add(new ShapeDescriptor(
        "4",
        new Range(0,0),
        new Range(0,0),
        0));
    
    shapeSet.add(new ShapeDescriptor(
        "5",
        new Range(0,0),
        new Range(0,0),
        0));
    
    shapeSet.add(new ShapeDescriptor(
        "6",
        new Range(0,0),
        new Range(0,0),
        0));
    
    shapeSet.add(new ShapeDescriptor(
        "7",
        new Range(0,0),
        new Range(0,0),
        0));
    
    shapeSet.add(new ShapeDescriptor(
        "8",
        new Range(0,0),
        new Range(0,0),
        0));
    
    shapeSet.add(new ShapeDescriptor(
        "9",
        new Range(0,0),
        new Range(0,0),
        0));
    
    // LETTERS *************************************************************
    
    _shapeCategories.put(categoryName, shapeSet);
  }
  
  public ShapeDictionary getInstance() {
    if(_singleton == null) {
      _singleton = new ShapeDictionary();
    }
    
    return _singleton;
  }
}
