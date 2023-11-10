// Name: jchen885
// USC NetID: 735856960
// CS 455 PA3
// Spring 2023

public class MineFieldTester {

   public static void main (String[] args) {

      System.out.println("============ Test 1-arg Constructor ============");

      boolean[][] smallMineField =
              {{false, false, false, false},
              {true, false, false, false},
              {false, true, true, false},
              {false, true, false, true}};

      MineField mineField = new MineField(smallMineField);
      System.out.println("> Fixed MineField");
      System.out.println(mineField);

      System.out.println("============ Test Defensive Copy ============");
      System.out.println("> Set pos[0][1]=true, pos[3][3]=false in smallMineField not in mineField");
      smallMineField[0][1] = true;
      smallMineField[3][3] = false;
      System.out.println(mineField);


      System.out.println("============ Test numAdjacentMines ============");
      System.out.println("> Find number of adjacent mines");
      System.out.println("Position [0, 0]: " + mineField.numAdjacentMines(0, 0) + " (Exp: 1)");
      System.out.println("Position [2, 2]: " + mineField.numAdjacentMines(2, 2) + " (Exp: 3)");
      System.out.println("Position [3, 1]: " + mineField.numAdjacentMines(3, 1) + " (Exp: 2)");
      System.out.println();

      System.out.println("============ Test resetEmpty ============");
      System.out.println("> Reset mineField");
      mineField.resetEmpty();
      System.out.println(mineField);

      System.out.println("============ Test 3-args Constructor ============");
      System.out.println("> Input: numRows=5, numCols=5, numMines=8");
      MineField randMineField = new MineField(5, 5, 8);
      System.out.println(randMineField);

      System.out.println("============ Test PopulateMineField ============");
      System.out.println("> Populate [1, 2]");
      randMineField.populateMineField(1, 2);
      System.out.println(randMineField);

      System.out.println("> Populate [2, 3]");
      randMineField.populateMineField(2, 3);
      System.out.println(randMineField);
   }
}