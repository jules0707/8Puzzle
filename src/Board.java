
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdRandom;


public class Board {
    private final int dimension; // the dimension of the board
    private final int[][] tiles;
    private int boardCounter=0;
    private int manhattan = -1;
    private int hamming = -1;
    private byte blankTile_i;
    private byte blankTile_j;
    private byte swapTwinTile1;
    private byte swapTwinTile2;
    private boolean foundBlankTile;


    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] initials) {
        // we have to do a double nested loop to copy the entries in a two-dimensional array as Arrays,copyOf or
        // clone only deep-copy one dimensional array
        int length = initials.length;
        assert 2 <= length;
        assert length < 128;
        this.tiles = initials;
        this.dimension = length;
        this.hamming = hamming();
        this.manhattan = manhattan();
        findBlankTile(initials);
    }

    // defensive copy of input to ensure immutability
    private void findBlankTile(int[][] tiles) {
        int N = tiles.length;
        for (byte i = 0; i < N; i++) {
            for (byte j = 0; j < N; j++) {
                // search for blank tile position as we touch on the array for the first time
                if (!foundBlankTile) {
                    if (tiles[i][j] == 0) {
                        foundBlankTile = true;
                        blankTile_i = i;
                        blankTile_j = j;
                    }
                }
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dimension + "\n");
        for (byte i = 0; i < dimension; i++) {
            for (byte j = 0; j < dimension; j++) {
                s.append(String.format("%2d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n * n
    public int dimension() {
        return tiles.length;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattanValue;
        // has manhattan already been computed?
        if (manhattan >= 0) return manhattan;
        else {
            manhattanValue = 0;
            for (byte i = 0; i < dimension; i++) {
                for (byte j = 0; j < dimension; j++) {
                    int tileFound = tiles[i][j];
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
            for (byte i = 0; i < dimension; i++) {
                for (byte j = 0; j < dimension; j++) {
                    int tileFound = tiles[i][j];
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

        // test the values of each instance variables of the two boards
        if (this.dimension != that.dimension) return false;
        if (this.hamming != that.hamming) return false;
        if (this.manhattan != that.manhattan) return false;

        // we assume the two boards are equals until found different
        int false_count = 0;

        // we compare the values of each tiles of the two puzzles one at a time and stop as soon as an inequality is found
        for (byte i = 0; i < dimension; i++) {
            for (byte j = 0; j < dimension; j++) {
                if (this.tiles[i][j] != that.tiles[i][j]) {
                    false_count++;
                }
            }
        }
        return false_count == 0;
    }


    private static int[][] hardCopy(int[][] initial, int blank_i, int blank_j, int swap_i, int swap_j) {
        int N = initial[0].length;
        int[][] hardCopy = new int[N][N];

        // we do an independent copy of initial
        for (byte i = 0; i < N; i++) {
            for (byte j = 0; j < N; j++) {
                // we found the blank tile
                if (i == blank_i && j == blank_j) {
                    // we copy it to the swaped location
                    hardCopy[i][j] = initial[swap_i][swap_j];
                }
                // we found the tile to swap
                else if (i == swap_i && j == swap_j) {
                    // we copy it to the blank tile location
                    hardCopy[swap_i][swap_j] = initial[blank_i][blank_j];
                } else {
                    // we copy all the  other tiles
                    hardCopy[i][j] = initial[i][j];
                }
            }
        }
        return hardCopy;
    }


    // all neighboring boards
    public Iterable<Board> neighbors() {
        Board neighbour;
        Stack<Board> neighbours = new Stack<>();

        // we have a top neighbour
        if (blankTile_i - 1 >= 0) {
            int[][] tilesCopy = hardCopy(tiles, blankTile_i, blankTile_j, blankTile_i - 1, blankTile_j);
            neighbour = new Board(tilesCopy);
            neighbours.push(neighbour);
        }

        // bottom neighbour
        if (blankTile_i + 1 < dimension) {
            int[][] tilesCopy = hardCopy(tiles, blankTile_i, blankTile_j, blankTile_i + 1, blankTile_j);
            neighbour = new Board(tilesCopy);
            neighbours.push(neighbour);
        }

        // right neighbour
        if (blankTile_j + 1 < dimension) {
            int[][] tilesCopy = hardCopy(tiles, blankTile_i, blankTile_j, blankTile_i, blankTile_j + 1);
            neighbour = new Board(tilesCopy);
            neighbours.push(neighbour);
        }

        // left neighbour
        if (blankTile_j - 1 >= 0) {
            int[][] tilesCopy = hardCopy(tiles, blankTile_i, blankTile_j, blankTile_i, blankTile_j - 1);
            neighbour = new Board(tilesCopy);
            neighbours.push(neighbour);
        }
        return neighbours;
    }


    private static int[][] hardCopy2(int[][] initial, int i_1, int j_1, int i_2, int j_2) {
        int N = initial[0].length;
        int[][] hardCopy2 = new int[N][N];

        // we do an independent copy of initial
        for (byte i = 0; i < N; i++) {
            for (byte j = 0; j < N; j++) {
                // we found the tile 1 to swap
                if (i == i_1 && j == j_1) {
                    // we copy it to the swaped location
                    hardCopy2[i][j] = initial[i_2][j_2];
                }
                // we found the tile 2 to swap
                else if (i == i_2 && j == j_2) {
                    // we copy it to the blank tile location
                    hardCopy2[i][j] = initial[i_1][j_1];
                } else {
                    // we copy all the  other tiles
                    hardCopy2[i][j] = initial[i][j];
                }
            }
        }
        return hardCopy2;
    }


    // a board that is obtained by exchanging any pair of tiles except the blank square that is not a tile
    public Board twin() {
        if (swapTwinTile1 == 0 && swapTwinTile2 == 0) {
            do {
                // we pick two random non nul distinct integer that are less than the dimension of the number of tiles in the board
                swapTwinTile1 = (byte) StdRandom.uniform(1, dimension * dimension);
                swapTwinTile2 = (byte) StdRandom.uniform(1, dimension * dimension);
            } while (swapTwinTile1 == swapTwinTile2);
        }

        // find location of tile1 & tile2
        int[] tile1Coordinates = findCoordinates(swapTwinTile1);
        int[] tiles2Coordinates = findCoordinates(swapTwinTile2);

        int i1 = tile1Coordinates[0];
        int j1 = tile1Coordinates[1];
        int i2 = tiles2Coordinates[0];
        int j2 = tiles2Coordinates[1];

        // we reference the copy of tiles
        int[][] twin = hardCopy2(tiles, i1, j1, i2, j2);

        return new Board(twin);
    }

    private int[] findCoordinates(int tile) {
        int coordinate_i = -1;
        int coordinate_j = -1;
        for (byte i = 0; i < dimension; i++) {
            for (byte j = 0; j < dimension; j++) {
                if (tiles[i][j] == tile) {
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
            StdOut.println("The neighbours: " + b.neighbors());
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
//        }
//    } // end of main()
} // end