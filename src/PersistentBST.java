public class PersistentBST {
  private static final int MAX_SIZE = 1000;
    
  private BSTNode versions[];
  private int currentTime = 0;
  
  private class BSTNode {
    String data;
    String value;
    BSTNode left;
    BSTNode right;

    BSTNode(String elem, String value) {
      this.data = elem;
      this.value = value;
    }
  }
  
  private String key;
  
  /**
   * Create a new BST, using key as the sorting key. More on what we mean by this later, 
   * under BST Keys.
   */
  public PersistentBST(String key) {
    this.key = key;
    versions = new BSTNode[MAX_SIZE];
    versions[0] = null;
  } 
  
  /** Insert an element into the BST, creating a new tree under a new time step. */
  public void insert(String elem) {
    if (currentTime == versions.length - 1) {
      increaseArray();
    }
    BSTNode leaf = new BSTNode(elem, getValue(elem));
    if (versions[currentTime] == null) {
      currentTime++;
      versions[currentTime] = leaf;
    } else {
      BSTNode old = versions[currentTime];
      BSTNode current = new BSTNode(old.data, old.value);
      currentTime++;
      // New root.
      versions[currentTime] = current;
      boolean found = false;
      while (!found) {
        if (leaf.value.compareToIgnoreCase(current.value) < 0) {
          // go to left
          current.right = old.right;
          if (old.left == null) {
            current.left = leaf;
            found = true;
            break;
          }
          BSTNode newNode = new BSTNode(old.left.data, old.left.value);
          current.left = newNode;
          old = old.left;
          current = current.left;
        } else {
          // go to right
          current.left = old.left;
          if (old.right == null) {
            current.right = leaf;
            found = true;
            break;
          }
          BSTNode newNode = new BSTNode(old.right.data, old.right.value);
          current.right = newNode;
          old = old.right;
          current = current.right;
        }
      }
    }
  }

  private void increaseArray() {
    BSTNode[] newVersions = new BSTNode[versions.length * 2];
    for (int i = 0; i < versions.length; i++) {
      newVersions[i] = versions[i];
    }
    versions = newVersions;
  }

  private String getValue(String elem) {
    String quote = "\"";
    // Remove {}
    elem = elem.substring(1, elem.length() - 2);
    String pairs[] = elem.split(",");
    // Check each pair.
    for (String keyValue : pairs) {
      String parts[] = keyValue.split(":");
      String key = parts[0].split(quote)[1];
      String value = parts[1].split(quote)[1];
      if (key.equalsIgnoreCase(this.key)) {
        return value;
      }
    }
    return "";
  }

  /**
   * Deletes an element from the BST, creating a new tree under a new time step.
   */
  public void delete(String elem) {
    if (versions[currentTime] == null || !find(elem)) {
      throw new IllegalStateException("Element is not in the tree.");
    }
    if (currentTime == versions.length - 1) {
      increaseArray();
    }
    String value = getValue(elem);
    BSTNode old = versions[currentTime];
    BSTNode current = new BSTNode(old.data, old.value);
    currentTime++;
    versions[currentTime] = current;
    BSTNode parent = null;
    boolean done = false;
    while (!done) {
      if (value.equalsIgnoreCase(old.value)) {
        done = true;
        if (old.left == null || old.right == null) {
          // 0 or 1 child.
          BSTNode child = null;
          if (old.left != null) {
            child = old.left;
          } else if (old.right != null) {
            child = old.right;
          }
          if (parent == null) {
            versions[currentTime] = child;
          } else if (parent.left == current) {
            parent.left = child;
          } else {
            parent.right = child;
          }
        } else {
          // Has two children.
          BSTNode toDelete = current;
          toDelete.right = old.right;
          old = old.left;
          parent = current;
          while (old != null) {
            if (old.right == null) {
              parent.right = old.left;
              break;
            } else {
              current = new BSTNode(old.data, old.value);
              current.left = old.left;
              old = old.right;
              if (parent == toDelete) {
                parent.left = current;
              } else {
                parent.right = current;
              }
              parent = current;
            }
          }
          toDelete.data = old.data;
          toDelete.value = old.value;
        }
      } else if (value.compareToIgnoreCase(old.value) < 0) {
        current.right = old.right;
        BSTNode newNode = new BSTNode(old.left.data, old.left.value);
        current.left = newNode;
        old = old.left;
        parent = current;
        current = current.left; 
      } else {
        current.left = old.left;
        BSTNode newNode = new BSTNode(old.right.data, old.right.value);
        current.right = newNode;
        old = old.right;
        parent = current;
        current = current.right;
      }
    }
  }

  
  /**
   * Returns true if elem is in the BST at time "time". 
   * This operation does not change any tree and does not change the current time.
   */
  public boolean find(String elem, int time) {
    BSTNode current = versions[time];
    String value = getValue(elem);
    while (current != null) {
      if (value.equalsIgnoreCase(current.value)) {
        return true;
      } else if (value.compareToIgnoreCase(current.value) > 0) {
        current = current.right;
      } else {
        current = current.left; 
      }
    }
    return false;
 }
    
   
  /**
   * Returns true if the element is in the BST. As above, this operation does not change the tree
   * and does not change the current time.
   */
   public boolean find(String elem) {
     return find(elem, currentTime);
   }

  /**
   * Returns the "current time", that is, how many operations have been done on the BST. Each time an
   * element is inserted or deleted, the curentTime is incremented.
   */
  public int currentTime() {
    return currentTime;
  }

  /** Returns the size (number of elements in the BST) at a given time. */
  public int size(int time) {
    BSTNode tree = versions[time];
    return treeSize(tree);
  }
  
  private int treeSize(BSTNode tree) {
    if (tree == null) {  
      return 0;
    }
    int leftSize = treeSize(tree.left);  
    int rightSize = treeSize(tree.right);
    return 1 + leftSize + rightSize;
  }
  
  /**
   * Returns the size (number of elements in the BST) at the current time. Equivalent
   * to size(currentTime()).
   */
  public int size() {
    return size(currentTime);
  }
  
  /**
   * Returns a sorted array of all elements in the BST at the given time. The length of the array 
   * should be equal to the size of the returned BST
   */
  public String[] getAllElements(int time) {
    BSTNode tree = versions[time];
    int arraySize = size(time);
    String elements[] = new String[arraySize];
    treeToArray(tree, elements, 0);
    return elements;
  }

  private int treeToArray(BSTNode tree, String elements[], int i) {
    if (tree == null) {
      return 0;
    }
    // Leaf.
    if (tree.left == null && tree.right == null) {
      elements[i] = tree.data;
      return i + 1;
    }
    // left
    if (tree.left != null) {
      i = treeToArray(tree.left, elements, i);
    }
    // root
    elements[i] = tree.data;
    ++i;
    // right
    if (tree.right != null) {
      return treeToArray(tree.right, elements, i);
    }
    return i;
  }

  /** 
   * Returns a sorted array of all elements in the BST at the current time. 
   * Equivalent to getAllElements(currentTime()).
   */
  public String[] getAllElements() {
    return getAllElements(currentTime);
  }
   
}
