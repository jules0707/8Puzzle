
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;


public class Board {
    private final int dimension; // the dimension of the board
    private final int[][] copy;
    private int manhattan = -1;
    private int hamming = -1;
    private byte blankTile_i_index;
    private byte blankTile_j_index;
    private byte swapTwinTile1;
    private byte swapTwinTile2;


    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        // we have to do a double nested loop to copy the entries in a two-dimensional array as Arrays,copyOf or
        // clone only deep-copy one dimensional array
        int length = tiles.length;
        assert 2 <= length;
        assert length < 128;
        this.dimension = length;
        this.copy = copy(tiles);
        this.hamming = hamming();
        this.manhattan = manhattan();
    }

    private int[][] copy(int[][] tiles) {
        boolean foundBlankTile = false;
        int[][] tilesCopy = new int[dimension][dimension];
        // defensive copy of input to ensure immutability
        for (byte i = 0; i < dimension; i++) {
            for (byte j = 0; j < dimension; j++) {
                int tile = tiles[i][j];
                // search for blank tile position as we touch on the array for the first time
                if (!foundBlankTile) {
                    if (tile == 0) {
                        foundBlankTile = true;
                        blankTile_i_index = i;
                        blankTile_j_index = j;
                    }
                }
                tilesCopy[i][j] = tile;
            }
        }
        return tilesCopy;
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dimension + "\n");
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                s.append(String.format("%2d ", copy[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n * n
    public int dimension() {
        return copy.length;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattanValue;
        // has manhattan already been computed?
        if (manhattan >= 0) return manhattan;
        else {
            manhattanValue = 0;
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    int tileFound = copy[i][j];
                    int tileExpected = tile(i, j); // the expected number at position (i,j)
                    if (tileFound != 0) {  // we ignore the blank tile
                        if (tileFound != tileExpected) {
                            int i_expected = index_i_of(tileFound);
                            int j_expected = index_j_of(tileFound);
                            manhattanValue += Math.abs(i - i_expected) + Math.abs(j - j_expected);
                        }
                    }
                }
            }
        }
        manhattan = manhattanValue;
        return manhattan;
    }

    // number of tiles out of place
    public int hamming() {
        int hammingValue;
        if (hamming >= 0) return hamming;
        else {
            hammingValue = 0;
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    int tileFound = copy[i][j];
                    if (tileFound != 0) { // we ignore the blank tile
                        if (tileFound != tile(i, j)) {
                            hammingValue++;
                        } // tileFound is not at its sequential place
                    }
                }
            }
            hamming = hammingValue; // we set the hamming value to avoid recomputation
        }
        return hamming;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming == 0;
    }

    private int index_j_of(int tile) {
        return tile - (index_i_of(tile) * dimension + 1);
    }

    private int index_i_of(int tile) {
        return (tile - 1) / dimension;
    }

    private int tile(int i, int j) {
        return i * dimension + j + 1;
    }


    // does this board equal y?
    @Override
    public boolean equals(Object y) {
        // taken from  p. 103 of Algorithms, 4th edition.
        if (this == y) return true;
        if (y == null) return false;
        if (this.getClass() != y.getClass()) return false;
        Board that = (Board) y;

        // we make sure we compute hamming and manhattan of that object

        // test the values of each instance variables of the two boards
        if (this.dimension != that.dimension) return false;
        if (this.hamming != that.hamming) return false;
        if (this.manhattan != that.manhattan) return false;

        // we assume the two boards are equals until found different
        int false_count = 0;

        // we compare the values of each tiles of the two puzzles one at a time and stop as soon as an inequality is found
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (this.copy[i][j] != that.copy[i][j]) {
                    false_count++;
                }
            }
        }
        return false_count == 0;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Board neighbour;
        Stack<Board> neighbours = new Stack<>();

        // in copy() we find the coordinates of the blank tile
        int i = blankTile_i_index;
        int j = blankTile_j_index;
        final int[][] hardCopy = this.copy;

        // we find the top neighbour
        if (i - 1 >= 0) {
            // a fresh copy of boardTiles
            int[][] tilesCopy = copy(this.copy);
            // we swap the tiles values
            tilesCopy[i - 1][j] = this.copy[i][j];
            tilesCopy[i][j] = this.copy[i - 1][j];
            //create the neighbour
            neighbour = new Board(tilesCopy);
            //and add it to the stack
            neighbours.push(neighbour);
        }

        // bottom neighbour
        if (i + 1 < dimension) {
            int[][] tilesCopy = copy(this.copy);
            tilesCopy[i + 1][j] = this.copy[i][j];
            tilesCopy[i][j] = this.copy[i + 1][j];
            neighbour = new Board(tilesCopy);
            neighbours.push(neighbour);
        }

        // right neighbour
        if (j + 1 < dimension) {
            int[][] tilesCopy = copy(this.copy);
            // we swap the tiles values
            tilesCopy[i][j + 1] = this.copy[i][j];
            tilesCopy[i][j] = this.copy[i][j + 1];
            neighbour = new Board(tilesCopy);
            neighbours.push(neighbour);
        }

        // left neighbour
        if (j - 1 >= 0) {
            int[][] tilesCopy = copy(this.copy);
            tilesCopy[i][j - 1] = this.copy[i][j];
            tilesCopy[i][j] = this.copy[i][j - 1];
            neighbour = new Board(tilesCopy);
            neighbours.push(neighbour);
        }
        return neighbours;
    }

    // a board that is obtained by exchanging any pair of tiles except the blank square that is not a tile
    public Board twin() {
        if (swapTwinTile1 == 0 && swapTwinTile2 == 0) {
            do {
                // we pick two random non nul distinct integer
                swapTwinTile1 = (byte) StdRandom.uniform(1, dimension * dimension);
                swapTwinTile2 = (byte) StdRandom.uniform(1, dimension * dimension);
            } while (swapTwinTile1 == swapTwinTile2);
        }

        // find location of tile1
        int[] tile1Coordinates = findCoordinates(swapTwinTile1);
        int[] tiles2Coordinates = findCoordinates(swapTwinTile2);

        int i1 = tile1Coordinates[0];
        int j1 = tile1Coordinates[1];
        int i2 = tiles2Coordinates[0];
        int j2 = tiles2Coordinates[1];

        // we reference the copy of tiles
        int[][] twin = copy(this.copy);

        // swap
        twin[i1][j1] = swapTwinTile2;
        twin[i2][j2] = swapTwinTile1;

        return new Board(twin);
    }

    private int[] findCoordinates(int tile) {
        int coordinate_i = -1;
        int coordinate_j = -1;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (copy[i][j] == tile) {
                    coordinate_i = i;
                    coordinate_j = j;
                    break;
                }
            }
        }
        // TODO try catch here for corner cases of NaN and others like not found -1??
        return new int[]{coordinate_i, coordinate_j};
    }

    // unit testing (not graded)
/*    public static void main(String[] args) {

        for (String filename : args) {
            In in = new In(filename);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tiles[i][j] = in.readInt();
                }
            }
            Board b = new Board(tiles);

            StdOut.println("The board: " + b.toString());
            StdOut.println("manhattan of board: " + b.manhattan);
            StdOut.println("hamming of board: " + b.hamming);

*/

//            StdOut.println("A twin 1st time : " + b.twin().toString());
//            StdOut.println("twin equals twin : " + b.twin().equals(b.twin()));
//
//            b.neighbors();
//
//            StdOut.println("A twin 2nd time : " + b.twin().toString());
//            b.neighbors();
//
//            StdOut.println("A twin 3rd time : " + b.twin().toString());
//            b.neighbors();
//
//            StdOut.println("A twin 4th time : " + b.twin().toString());
//            b.neighbors();
//
//
//            StdOut.println("twin equals twin once : " + b.twin().equals(b.twin()));
//            StdOut.println("twin equals twin twice : " + b.twin().equals(b.twin()));
//            StdOut.println("twin equals twin three times : " + b.twin().equals(b.twin()));
//
//            StdOut.println("list of neighbours : " + " \n " + b.neighbors().toString());
//            StdOut.println("A list of neighbours 5th time : " + b.neighbors().toString());
//
//            int[][] tiles1 = {{8, 5, 4}, {7, 0, 6}, {3, 1, 2}};
//            int[][] tiles2 = {{8, 5, 4}, {7, 0, 6}, {3, 2, 1}};
//
//            Board board1 = new Board(tiles1);
//            Board board2 = new Board(tiles2);
//
//            StdOut.println(board1.equals(board2));
//
//
//}
//}

}
