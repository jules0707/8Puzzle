
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Random;


public class Board {
    int dimension; // the dimension of the board
    int[][] board;
    int hamming;
    int manhattan;
    int[] zeroTilePosition = new int[]{-1, -1}; // we set this impossible value

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        dimension = tiles.length;  // size of the n-by-n array of tiles
        assert 2 <= dimension;
        assert dimension < 128;

        // we have to do a double nested loop to copy the entries in a two-dimensional array as Arrays,copyOf or
        // clone only deep-copy one dimensional array
        int[][] copy = doubleArrayCopy(tiles);
        board = copy;

        hamming = hamming(); // we set an impossible value to enable checks in manhattan() and  hamming() and so avoid call repeats
        manhattan = manhattan();
    }

    private int[][] doubleArrayCopy(int[][] tiles) {
        // defensive copy of input to ensure immutability
        int[][] copy = new int[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                copy[i][j] = tiles[i][j];
            }
        }
        return copy;
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(dimension + "\n");
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                s.append(String.format("%2d ", board[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n * n
    public int dimension() {
        return board.length;
    }

    // number of tiles out of place
    public int hamming() {
//        if (hamming > -1) return hamming; // call to hamming already completed no need to recall
//        else {
        int count = 0;
        manhattan = 0; // now we will compute the value and need to count from 0 start
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                int tile = board[i][j]; // we ignore the blank square
                int expected = getTile(i, j);
                if (tile == 0) {
                    zeroTilePosition = new int[]{i, j};
                }
                if (tile != expected && tile != 0) {
                    int i_expected = get_index_i_forTile(tile);
                    int j_expected = get_index_j_forTile(tile);
                    manhattan += Math.abs(i - i_expected) + Math.abs(j - j_expected);
                    count++;
                }
            }
        } // if count has been incremented we update the hamming value
        if (count != 0) hamming = count;
            // all tiles are in place
        else hamming = 0; // we set hamming here to skip potential call repeats
        return hamming;
    }
    //  }

    private int get_index_j_forTile(int tile) {
        return tile - (get_index_i_forTile(tile) * dimension + 1);
    }

    private int get_index_i_forTile(int tile) {
        return (tile - 1) / dimension;
    }

    private int getTile(int i, int j) {
        return i * dimension + j + 1;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        if (manhattan == -1) hamming();  // hamming hasn't already been called so we call it
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming == 0 ? true : false;
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
        that.manhattan();

        // test the values of each instance variables of the two boards
        if (this.dimension != that.dimension) return false;
        if (this.hamming != that.hamming) return false;
        if (this.manhattan != that.manhattan) return false;

        // we assume the two boards are equals until found different
        boolean areNotEquals = false;

        // we compare the values of each tiles of the two puzzles one at a time and stop as soon as an inequality is found
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (this.board[i][j] != that.board[i][j]) areNotEquals = true;
                break;
            }
        }
        return !areNotEquals;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {

        Board neighbour_board;
        //empty stack
        Stack<Board> neighbors = new Stack<>();

        // in hamming() we find the coordinates of the blank tile
        if (Arrays.equals(zeroTilePosition, new int[]{-1, -1})) hamming();

        int i = zeroTilePosition[0];
        int j = zeroTilePosition[1];


        // we find the top neighbour
        if (i - 1 >= 0) {
            // deep copy of board
            int[][] copy_board = doubleArrayCopy(board);
            // we swap the tiles values
            copy_board[i - 1][j] = board[i][j];
            copy_board[i][j] = board[i - 1][j];
            // and add if to the stack
            neighbour_board = new Board(copy_board);
            // and add it to the stack
            neighbors.push(neighbour_board);
        }

        // we find the bottom neighbour
        if (i + 1 < dimension) {
            // deep copy of board
            int[][] copy_board = doubleArrayCopy(board);
            copy_board[i + 1][j] = board[i][j];
            copy_board[i][j] = board[i + 1][j];
            // and add if to the stack
            neighbour_board = new Board(copy_board);
            // and add it to the stack
            neighbors.push(neighbour_board);
        }

        // we find the right neighbour
        if (j + 1 < dimension) {
            // deep copy of board
            int[][] copy_board = doubleArrayCopy(board);
            // we swap the tiles values
            copy_board[i][j + 1] = board[i][j];
            copy_board[i][j] = board[i][j + 1];
            // and add if to the stack
            neighbour_board = new Board(copy_board);
            // and add it to the stack
            neighbors.push(neighbour_board);
        }

        // we find the left neighbour
        if (j - 1 >= 0) {
            // deep copy of board
            int[][] copy_board = doubleArrayCopy(board);
            // we swap the tiles values
            copy_board[i][j - 1] = board[i][j];
            copy_board[i][j] = board[i][j - 1];
            // and add if to the stack
            neighbour_board = new Board(copy_board);
            // and add it to the stack
            neighbors.push(neighbour_board);
        }
        return neighbors;
    }


    // a board that is obtained by exchanging any pair of tiles except the blank square that is not a tile
    public Board twin() {
        Random random = new Random();
        int tile1;
        int tile2;
        do {// blank tile is excluded with the +1 and so n is included
            tile1 = random.nextInt(dimension * dimension);
            tile2 = random.nextInt(dimension * dimension);
        } while (tile1 == tile2 && tile1 != 0 && tile2 != 0); // the blank tiles is not to be swaped

        return swapTiles(tile1, tile2);
    }

    private Board swapTiles(int tile1, int tile2) {
        // find location of tile1
        int[] coordinates_tile1 = findCoordinates(tile1);
        int[] coordinates_tile2 = findCoordinates(tile2);

        int i1 = coordinates_tile1[0];
        int j1 = coordinates_tile1[1];
        int i2 = coordinates_tile2[0];
        int j2 = coordinates_tile2[1];

        int[][] copy = doubleArrayCopy(board);

        // now swap
        copy[i1][j1] = tile2;
        copy[i2][j2] = tile1;

        return new Board(copy);
    }

    private int[] findCoordinates(int tile) {
        int coordinate_i = -1;
        int coordinate_j = -1;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (board[i][j] == tile) {
                    coordinate_i = i;
                    coordinate_j = j;
                    break;
                }
                ;
            }
        }
        // TODO try catch here for corner cases of NaN and others like not found -1??
        return new int[]{coordinate_i, coordinate_j};
    }

    // unit testing (not graded)
    public static void main(String[] args) {

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

            StdOut.println(b.toString());
            StdOut.println("dimension: " + b.dimension());
            StdOut.println("hamming count: " + b.hamming());
            StdOut.println("manhattan value: " + b.manhattan());

            StdOut.println("Boards neighbors are: ");
            b.neighbors().forEach(board1 -> StdOut.println(board1.toString()));

            // test for equality
            Board b3x3 = new Board(new int[][]{{8, 1, 3}, {4, 0, 2}, {7, 6, 5}});
            Board b49 = new Board(new int[][]{{2, 10, 15, 4}, {14, 11, 0, 6}, {9, 13, 8, 3}, {5, 7, 12, 1}});

            StdOut.println("test equality of boards is: " + b.equals(b49));
            StdOut.println("a twin of this board is : " + b.twin().toString());
            StdOut.println("an other twin of this board is : " + b.twin().toString());

        }
    }
}
