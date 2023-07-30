package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
  @Test
  public void randomizedTest(){
      AListNoResizing<Integer> L = new AListNoResizing<>();
      BuggyAList<Integer> B = new BuggyAList<>();
      int N = 50000;
      for (int i = 0; i < N; i += 1) {
          int operationNumber = StdRandom.uniform(0, 3);

          if (operationNumber == 0) {
              int randVal = StdRandom.uniform(0, 100);
              L.addLast(randVal);
              B.addLast(randVal);
              assertEquals(L.size(), B.size());
          }
          else if (operationNumber == 1 ) {
              if (L.size() > 0 && B.size() > 0){
                  assertEquals(L.removeLast(), B.removeLast());
                  assertEquals(L.size(), B.size());
              }

          }
          else if (operationNumber == 3 ) {
              if (L.size() > 0 && B.size() > 0){
                  assertEquals(L.getLast(), B.getLast());
              }
          }

      }
  }
}
