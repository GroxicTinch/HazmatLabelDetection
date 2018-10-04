
public class Levenshtein {
  private String _needle;

  // https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance
  public Levenshtein(String needle) {
    _needle = needle;
  }
  
  public int findDistance(String haystack) {
    String needle = _needle.toUpperCase();
    haystack = haystack.toUpperCase();
    
    int[] cost = new int[needle.length() + 1];
    int[] newCost = new int[needle.length() + 1];
    
    // Initialize costs
    for(int i = 0; i < cost.length; i++) {
      cost[i] = i;
    }
    
    for(int i = 1; i <= haystack.length(); i++) {
      cost[0] = i;
      
      for(int ii = 1; ii <= needle.length(); ii++) {
        int costReplace;
        int costIns;
        int costDel;
        
        int isDifferent = 0;
        
        if(needle.charAt(ii - 1) != haystack.charAt(i-1)) {
          isDifferent = 1;
        }
        
        costReplace = cost[ii-1] + isDifferent;
        costIns = cost[ii] + 1;
        costDel = newCost[ii - 1] + 2;
        
        // We want the lowest Cost
        newCost[ii] = smallest(costReplace, costIns, costDel);
      }
      
      int[] tmp = cost;
      cost = newCost;
      newCost = tmp;
    }
    
    return cost[needle.length()];
  }

  private int smallest(int costReplace, int costIns, int costDel) {
    return Math.min(Math.min(costReplace, costIns), costDel);
  }
}
