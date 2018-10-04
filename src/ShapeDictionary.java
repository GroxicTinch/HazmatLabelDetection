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
        new Range(0.60,0.66),
        new Range(0.45,0.55),
        6));
    
    shapeSet.add(new ShapeDescriptor(
        "2",
        new Range(0.62,0.68),
        new Range(0.81,0.85),
        13));
    
    shapeSet.add(new ShapeDescriptor(
        "3",
        new Range(0.59,0.62),
        new Range(0.81,0.84),
        13));
    
    shapeSet.add(new ShapeDescriptor(
        "4",
        new Range(0.58,0.61),
        new Range(0.84,0.87),
        10));
    
    shapeSet.add(new ShapeDescriptor(
        "5",
        new Range(0.61,0.63),
        new Range(0.81,0.85),
        14));
    
    shapeSet.add(new ShapeDescriptor(
        "6",
        new Range(0.65,0.67),
        new Range(0.82,0.85),
        13));
    
    shapeSet.add(new ShapeDescriptor(
        "7",
        new Range(0.47,0.49),
        new Range(0.79,0.81),
        9));
    
    shapeSet.add(new ShapeDescriptor(
        "8",
        new Range(0.69,0.72),
        new Range(0.81,0.84),
        13));
    
    shapeSet.add(new ShapeDescriptor(
        "9",
        new Range(0,0),
        new Range(0,0),
        0));
    
    shapeSet.add(new ShapeDescriptor(
        ".",
        new Range(0.99,1.01),
        new Range(0.99,1.01),
        4));
    
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
