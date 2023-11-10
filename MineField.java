// Name: jchen885
// USC NetID: 7335856960
// CS 455 PA3
// Spring 2023


import java.util.Random;

/** 
   MineField
      class with locations of mines for a game.
      This class is mutable, because we sometimes need to change it once it's created.
      mutators: populateMineField, resetEmpty
      includes convenience method to tell the number of mines adjacent to a location.
 */
public class MineField {
   
   private final int numRows;
   private final int numCols;
   private final boolean[][] mineData;
   private final int numMines;

   
   
   /**
      Create a minefield with same dimensions as the given array, and populate it with the mines in the array
      such that if mineData[row][col] is true, then hasMine(row,col) will be true and vice versa.  numMines() for
      this minefield will correspond to the number of 'true' values in mineData.
      @param mineData  the data for the mines; must have at least one row and one col,
                       and must be rectangular (i.e., every row is the same length)
    */
   public MineField(boolean[][] mineData) {
      this.mineData = deepCopy(mineData);
      this.numRows = mineData.length;
      this.numCols = mineData[0].length;
      this.numMines = CalcNumMines(mineData);
   }
   
   
   /**
      Create an empty minefield (i.e. no mines anywhere), that may later have numMines mines (once 
      populateMineField is called on this object).  Until populateMineField is called on such a MineField, 
      numMines() will not correspond to the number of mines currently in the MineField.
      @param numRows  number of rows this minefield will have, must be positive
      @param numCols  number of columns this minefield will have, must be positive
      @param numMines   number of mines this minefield will have,  once we populate it.
      PRE: numRows > 0 and numCols > 0 and 0 <= numMines < (1/3 of total number of field locations). 
    */
   public MineField(int numRows, int numCols, int numMines) {
      this.numRows = numRows;
      this.numCols = numCols;
      this.mineData = new boolean[numRows][numCols];
      this.numMines = numMines;
   }
   

   /**
      Removes any current mines on the minefield, and puts numMines() mines in random locations on the minefield,
      ensuring that no mine is placed at (row, col).
      @param row the row of the location to avoid placing a mine
      @param col the column of the location to avoid placing a mine
      PRE: inRange(row, col) and numMines() < (1/3 * numRows() * numCols())
    */
   public void populateMineField(int row, int col) {
      resetEmpty(); // Reset the mineField

      Random rand = new Random();

      int count = this.numMines;
      while (count > 0) {
         int randRow = rand.nextInt(this.numRows);
         int randCol = rand.nextInt(this.numCols);

         if (randRow == row && randCol == col) { continue; } // Avoid putting mine on starting position
         if (hasMine(randRow, randCol)) { continue; } // Avoid putting mine on used positions

         this.mineData[randRow][randCol] = true;
         count--;
      }
   }
   
   
   /**
      Reset the minefield to all empty squares.  This does not affect numMines(), numRows() or numCols()
      Thus, after this call, the actual number of mines in the minefield does not match numMines().  
      Note: This is the state a minefield created with the three-arg constructor is in 
         at the beginning of a game.
    */
   public void resetEmpty() {
      for (int i = 0; i < this.numRows; i++) {
         for (int j = 0; j < this.numCols; j++) {
            this.mineData[i][j] = false;
         }
      }
   }

   
   /**
     Returns the number of mines adjacent to the specified mine location (not counting the possible
     mine at (row, col) itself).
     Diagonals are also considered adjacent, so the return value will be in the range [0,8]
     @param row  row of the location to check
     @param col  column of the location to check
     @return  the number of mines adjacent to the square at (row, col)
     PRE: inRange(row, col)
   */
   public int numAdjacentMines(int row, int col) {
      int nMines = 0;
      for (int dy = -1; dy < 2; dy++) {
         for (int dx = -1; dx < 2; dx++) {
            if (dx == 0 && dy == 0) { continue; }

            int newRow = row + dy;
            int newCol = col + dx;
            if (inRange(newRow, newCol)) {
               if (this.mineData[newRow][newCol]) {
                  nMines++;
               }
            }
         }
      }

      return nMines;
   }
   
   
   /**
      Returns true iff (row,col) is a valid field location.  Row numbers and column numbers
      start from 0.
      @param row  row of the location to consider
      @param col  column of the location to consider
      @return whether (row, col) is a valid field location
   */
   public boolean inRange(int row, int col) {
      return row >= 0 && row < this.numRows && col >= 0 && col < this.numCols;
   }
   
   
   /**
      Returns the number of rows in the field.
      @return number of rows in the field
   */  
   public int numRows() {
      return this.numRows;
   }
   
   
   /**
      Returns the number of columns in the field.
      @return number of columns in the field
   */    
   public int numCols() {
      return this.numCols;
   }
   
   
   /**
      Returns whether there is a mine in this square
      @param row  row of the location to check
      @param col  column of the location to check
      @return whether there is a mine in this square
      PRE: inRange(row, col)   
   */    
   public boolean hasMine(int row, int col) {
      return this.mineData[row][col];
   }
   
   
   /**
      Returns the number of mines you can have in this minefield.  For mines created with the 3-arg constructor,
      some of the time this value does not match the actual number of mines currently on the field.  See doc for that
      constructor, resetEmpty, and populateMineField for more details.
    * @return number of mines in the mineField
    */
   public int numMines() {
      return this.numMines;
   }

   /**
    * Return customized information of minefield
    *
    * @return customized information of minefield
    */
   public String toString() {
      StringBuilder strBuilder = new StringBuilder("[");
      for (int i = 0; i < this.numRows; i++) {
         strBuilder.append("[");
         for (int j = 0; j < this.numCols; j++) {
            strBuilder.append(this.mineData[i][j]);
            if (j != this.numCols - 1) {
               strBuilder.append(", ");
            }
         }
         strBuilder.append("]");
         if (i != this.numRows - 1) {
            strBuilder.append("\n");
         }
      }
      strBuilder.append("]\n");

      strBuilder.append("numRows: ").append(this.numRows).append(", numCols: ").append(this.numCols)
              .append(", numMines: ").append(this.CalcNumMines(this.mineData)).append(" (").append(this.numMines)
              .append(")").append("\n");;

      return strBuilder.toString();
   }

   // <put private methods here>

   /**
    * Return defensive copy of minefield data
    *
    * @param array minefield data
    * @return defensive copy of minefield data
    */
   private boolean[][] deepCopy(boolean[][] array) {
      if (array == null) { return null; }
      boolean[][] copy = new boolean[array.length][];
      for (int i = 0; i < array.length; i++) {
         copy[i] = array[i].clone();
      }
      return copy;
   }

   /**
    * Return number of mines in minefield
    *
    * @param mineData minefield data
    * @return number of mines in minefield
    */
   private int CalcNumMines(boolean[][] mineData) {
      int count = 0;
      for (boolean[] row: mineData) {
         for (boolean val: row) {
            if (val) {
               count++;
            }
         }
      }
      return count;
   }
         
}

