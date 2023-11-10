// Name: jchen885
// USC NetID: 735856960
// CS 455 PA3
// Spring 2023


/**
  VisibleField class
  This is the data that's being displayed at any one point in the game (i.e., visible field, because it's what the
  user can see about the minefield). Client can call getStatus(row, col) for any square.
  It actually has data about the whole current state of the game, including  
  the underlying mine field (getMineField()).  Other accessors related to game status: numMinesLeft(), isGameOver().
  It also has mutators related to actions the player could do (resetGameDisplay(), cycleGuess(), uncover()),
  and changes the game state accordingly.
  
  It, along with the MineField (accessible in mineField instance variable), forms
  the Model for the game application, whereas GameBoardPanel is the View and Controller in the MVC design pattern.
  It contains the MineField that it's partially displaying.  That MineField can be accessed (or modified) from 
  outside this class via the getMineField accessor.  
 */
public class VisibleField {
   // ----------------------------------------------------------   
   // The following public constants (plus numbers mentioned in comments below) are the possible states of one
   // location (a "square") in the visible field (all are values that can be returned by public method 
   // getStatus(row, col)).
   
   // The following are the covered states (all negative values):
   public static final int COVERED = -1;   // initial value of all squares
   public static final int MINE_GUESS = -2;
   public static final int QUESTION = -3;

   // The following are the uncovered states (all non-negative values):
   
   // values in the range [0,8] corresponds to number of mines adjacent to this opened square
   
   public static final int MINE = 9;      // this loc is a mine that hasn't been guessed already (end of losing game)
   public static final int INCORRECT_GUESS = 10;  // is displayed a specific way at the end of losing game
   public static final int EXPLODED_MINE = 11;   // the one you uncovered by mistake (that caused you to lose)
   // ----------------------------------------------------------   
  
   // <put instance variables here>
   private final int[][] status;
   private final boolean isRandomMineField;
   private final MineField mineField;

   private boolean isGameOver;
   private int numMinesLeft;
   private int numNonMinesLeft;

   /**
      Create a visible field that has the given underlying mineField.
      The initial state will have all the locations covered, no mines guessed, and the game
      not over.
      @param mineField  the minefield to use for this VisibleField
    */
   public VisibleField(MineField mineField) {
      this.mineField = mineField;
      this.isRandomMineField = checkIsRandomField(mineField); // Check if the minefield is random generated
      this.status = getInitialStatus(mineField.numRows(), mineField.numCols());  // Initialize the status for every square
      this.numMinesLeft = mineField.numMines();
      this.numNonMinesLeft = mineField.numCols() * mineField.numRows() - mineField.numMines();
      this.isGameOver = false;
   }
   
   
   /**
      Reset the object to its initial state (see constructor comments), using the same underlying
      MineField. 
   */     
   public void resetGameDisplay() {
      resetMineField();
      resetStatus();
      this.isGameOver = false;
      this.numMinesLeft = this.mineField.numMines();
      this.numNonMinesLeft = this.mineField.numCols() * this.mineField.numRows() - this.mineField.numMines();
   }
  
   
   /**
      Returns a reference to the mineField that this VisibleField "covers"
      @return the minefield
    */
   public MineField getMineField() {
      return this.mineField;
   }
   
   
   /**
      Returns the visible status of the square indicated.
      @param row  row of the square
      @param col  col of the square
      @return the status of the square at location (row, col).  See the public constants at the beginning of the class
      for the possible values that may be returned, and their meanings.
      PRE: getMineField().inRange(row, col)
    */
   public int getStatus(int row, int col) {
      return this.status[row][col];
   }

   
   /**
      Returns the number of mines left to guess.  This has nothing to do with whether the mines guessed are correct
      or not.  Just gives the user an indication of how many more mines the user might want to guess.  This value will
      be negative if they have guessed more than the number of mines in the minefield.     
      @return the number of mines left to guess.
    */
   public int numMinesLeft() {
      return this.numMinesLeft;
   }
 
   
   /**
      Cycles through covered states for a square, updating number of guesses as necessary.  Call on a COVERED square
      changes its status to MINE_GUESS; call on a MINE_GUESS square changes it to QUESTION;  call on a QUESTION square
      changes it to COVERED again; call on an uncovered square has no effect.  
      @param row  row of the square
      @param col  col of the square
      PRE: getMineField().inRange(row, col)
    */
   public void cycleGuess(int row, int col) {
      if (this.status[row][col] > 0) { return; }

      int guessStatus = this.status[row][col];
      if (guessStatus == QUESTION) {
         this.status[row][col] = COVERED;
      } else if (guessStatus == MINE_GUESS) {
         this.status[row][col] = QUESTION;
         this.numMinesLeft++;
      } else {
         this.status[row][col] = MINE_GUESS;
         this.numMinesLeft--;
      }
   }

   
   /**
      Uncovers this square and returns false iff you uncover a mine here.
      If the square wasn't a mine or adjacent to a mine it also uncovers all the squares in 
      the neighboring area that are also not next to any mines, possibly uncovering a large region.
      Any mine-adjacent squares you reach will also be uncovered, and form 
      (possibly along with parts of the edge of the whole field) the boundary of this region.
      Does not uncover, or keep searching through, squares that have the status MINE_GUESS. 
      Note: this action may cause the game to end: either in a win (opened all the non-mine squares)
      or a loss (opened a mine).
      @param row  of the square
      @param col  of the square
      @return false   iff you uncover a mine at (row, col)
      PRE: getMineField().inRange(row, col)
    */
   public boolean uncover(int row, int col) {
      if (this.mineField.hasMine(row, col)) {
         this.status[row][col] = EXPLODED_MINE;  // Set status for exploded mine
         failTheGame();   // Update the status of every square for failing the game
         return false;
      }

      dfs(row, col);
      if (isGameOver()) {
         winTheGame();  // Update the status of every square for winning the game
      }

      return true;
   }
 
   
   /**
      Returns whether the game is over.
      (Note: This is not a mutator.)
      @return whether game has ended
    */
   public boolean isGameOver() {
      return this.numNonMinesLeft == 0 || this.isGameOver;
   }
 
   
   /**
      Returns whether this square has been uncovered.  (i.e., is in any one of the uncovered states, 
      vs. any one of the covered states).
      @param row of the square
      @param col of the square
      @return whether the square is uncovered
      PRE: getMineField().inRange(row, col)
    */
   public boolean isUncovered(int row, int col) {
      return this.status[row][col] >= 0;
   }
   
 
   // <put private methods here>

   /**
    * Return whether the minefield is random generated. We can use unused minefield to determine if the minefield is
    * randomly generated. If the number of mines in the minefield is different to minefield.numMines(), it means that
    * the minefield is unset and waiting for update the value later. In this case, that minefield should be randomly
    * generated.
    * @param unusedMineField
    * @return whether the minefield is randomly generated
    */
   private boolean checkIsRandomField(MineField unusedMineField) {
      int numMines = unusedMineField.numMines();
      int count = 0;
      for (int i = 0; i < unusedMineField.numRows(); i++) {
         for (int j = 0; j < unusedMineField.numCols(); j++) {
            if (unusedMineField.hasMine(i, j)) {
               count++;
            }
         }
      }
      return numMines != count; // Check if the number of mines in the minefield is the same as minefield.numMines()
   }

   /**
    * Return a 2D array with all the value set to "COVERED"
    *
    * @param numRows number of rows in the minefield
    * @param numCols number of columns in the minefield
    * @return a 2D array with initial status value
    */
   private int[][] getInitialStatus(int numRows, int numCols) {
      int[][] initialStatus = new int[numRows][numCols];
      for (int i = 0; i < numRows; i++) {
         for (int j = 0; j < numCols; j++) {
            initialStatus[i][j] = COVERED;
         }
      }
      return initialStatus;
   }

   /**
    * Using DFS algorithm to find all the squares which should be opened when user click on one of the square.
    * The algorithm will keep searching until reaching boundary or a square with any number of mines adjacent to it.
    *
    * @param row the row position for target square
    * @param col the col position for target square
    */
   private void dfs(int row, int col) {
      this.numNonMinesLeft--; // decrease by 1 when uncover a new square
      int state = this.mineField.numAdjacentMines(row, col);
      this.status[row][col] = state; // Update square status
      if (state != 0) { return; } // If the square is adjacent to any mine, stop searching

      // Search all the adjacent squares from left-top corner to right-bottom corner
      for (int dy = -1; dy < 2; dy++) {   // [-1, 0, +1]
         for (int dx = -1; dx < 2; dx++) {   // [-1, 0, +1]
            int newRow = row + dy;
            int newCol = col + dx;
            if (!this.mineField.inRange(newRow, newCol) || this.mineField.hasMine(newRow, newCol)
                    || this.isUncovered(newRow, newCol) || this.status[newRow][newCol] == MINE_GUESS) {
               continue;
            }
            dfs(newRow, newCol);
         }
      }
   }

   /**
    * Update all the necessary information when user fail the game. The exploded mine should be labeled
    * as "EXPLODED_MINE" with red color. The covered mines should be labeled as "MINE" with black color.
    * If there are some incorrect guess of mine's position, those square should be labeled as "INCORRECT_GUESS" with
    * yellow background and a cross logo on top of it.
    */
   private void failTheGame() {
      this.isGameOver = true; // Game is over

      for (int row = 0; row < this.mineField.numRows(); row++) {
         for (int col = 0; col < this.mineField.numRows(); col++) {
            if (this.mineField.hasMine(row, col)) {
               if (this.status[row][col] == COVERED || this.status[row][col] == QUESTION) {
                  this.status[row][col] = MINE;
               }
            } else {
               if (this.status[row][col] == MINE_GUESS) {
                  this.status[row][col] = INCORRECT_GUESS;
               }
            }
         }
      }
   }

   /**
    * Update all the necessary information when user won the game. When the user won the game, all the mines should
    * be labeled as "MINE_GUESS" status.
    */
   private void winTheGame() {
      this.isGameOver = true; // Game is over

      for (int row = 0; row < this.mineField.numRows(); row++) {
         for (int col = 0; col < this.mineField.numRows(); col++) {
            if (this.mineField.hasMine(row, col)) {
               this.status[row][col] = MINE_GUESS;
            }
         }
      }
   }

   /**
    * Reset all the square to "COVERED" status.
    */
   private void resetStatus() {
      for (int i = 0; i < this.status.length; i++) {
         for (int j = 0; j < this.status[0].length; j++) {
            this.status[i][j] = COVERED;
         }
      }
   }

   /**
    * Reset the minefield to its initial condition. If the minefield is random generated, we have to set all the value
    * to false. The value would be updated later. In contrast, the fixed minefield is never changed, so we don't need
    * to reset it.
    */
   private void resetMineField() {
      // Only reset the random generated minefield
      if (this.isRandomMineField) {
         this.mineField.resetEmpty();
      }
   }
}
