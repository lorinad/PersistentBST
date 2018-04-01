public class PersistentStack {

  private static final int MAX_SIZE = 1000;

  private LinkedList versions[];
  private int currentTime = 0;
  
  /** Single entry in a stack. */
  private class LinkedList {
    String data;
    LinkedList next;

    LinkedList(String data, LinkedList next) {
      this.data = data;
      this.next = next;
    }
  }  
  
  public PersistentStack() {
    versions = new LinkedList[MAX_SIZE];
    versions[0] = null;
  }
  
  /**
   * Push the string elem onto the current stack, creating a new current stack. If the array
   * containing all of the old stacks is full, create a new one twice as large and copy all the data
   * across. 
   */
  public void push(String elem) {
    // Full array.
    if (currentTime == versions.length - 1) {
      increaseArray();
    }
    LinkedList entry = new LinkedList(elem, versions[currentTime]);
    currentTime++;
    versions[currentTime] = entry;
  }
  
  /**
   * Pops the top element from the current stack, creating a new current stack. If the array
   * containing all of the old stacks is full, create a new one twice as large and copy all the data
   * across.
   */
  public String pop() {
    if (versions[currentTime] == null) {
      throw new IllegalStateException("Stack is empty.");
    }
    if (currentTime == versions.length - 1) {
      increaseArray();
    }
    LinkedList poppedvalue = versions[currentTime];
    currentTime++;
    versions[currentTime] = versions[currentTime - 1].next;
    return poppedvalue.data;
  }

  private void increaseArray() {
    LinkedList[] newVersions = new LinkedList[versions.length * 2];
    for (int i = 0; i < versions.length; i++) {
      newVersions[i] = versions[i];
    }
    versions = newVersions;
  }

  /**
   * Returns the "current time", that is, how many operations have been done on the stack. 
   * Each time a push or pop operation is done, the curentTime is incremented.
   */
  public int currentTime() {
    return currentTime;
  }
  
  /** Returns the size (number of elements in the stack) at a given time. */
  public int size(int time) {
    LinkedList stack = versions[time];
    int size = 0;
    while (stack != null) {
      stack = stack.next;
      size++;
    }
    return size;
  }
  
  /**
   * Returns the size (number of elements in the stack) at the current time.
   * Equivalent to size(currentTime()).
   */
  public int size()  {
    return size(currentTime);
  }
  
  /**
   * Returns an array of all of the elements at a given time. 
   * The length of the returned string array should be the same as the number of elements 
   * in the stack at that time. reversed == false, then first element in the return array is
   * the top of the stack, if reversed == true, the last element in
   * the return array is the top of the stack.
   */
   public String[] getAllElements(int time, boolean reversed) {
     int arraySize = size(time);
     String elements[] = new String[arraySize];
     LinkedList current = versions[time];
     int i = reversed ? arraySize - 1 : 0;
     while (current != null) {
       elements[i] = current.data;
       i = reversed ? --i : ++i;
       current = current.next;
     }
     return elements;
   }
}
