
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

import java.util.Objects;

public class Solver {
    private Node node;
    private int movesValue;
    private final boolean isSolvable;
    private Iterable<Board> solutionBoards;


    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board given) {
        if (null == given) throw new IllegalArgumentException();
        movesValue = -1;
        Board initial = given;
        Board twin = initial.twin();
        // to avoid a null pointer when we querry the previous board as part of the critical optimisation we add itself as previous

        Node node1 = new Node(initial, 0, new Node(initial, 0, null));
        Node node2 = new Node(twin, 0, new Node(twin, 0, null));

        // Exactly one of the two (initial node or any twin node made from initial) will lead to the goal board.
        // we run A* on two board instances, in locksteps, to find which board will lead to the goal board
        MinPQ<Node> pq1 = new MinPQ<>(); // the initial board PQ
        MinPQ<Node> pq2 = new MinPQ<>(); // a twin board PQ

        pq1.insert(node1); // insert initial node into PQ
        pq2.insert(node2);

        // the first goal board found exits the search as they can't both be a goal board
        while (!node1.board.isGoal() && !node2.board.isGoal()) {
            Node minNode1 = pq1.delMin();
            for (Node neighbor : minNode1.neighbors()) {
                // critical optimisation do not re-insert the previous node
                // this hack of setting previous of initial to initial itself gives the following error message :
                // - equals() compares a board to a board that is not a neighbor of a neighbor
                // - this suggests either a bug in the critical optimization or an unnecessary
                //   call to equals() for some purpose other than the critical optimization
                // but we can ignore it has It has no consequences on timing nor memory nor correctness
                if (!neighbor.board.equals(minNode1.previous.board)) {
                    neighbor.previous = minNode1;
                    neighbor.moves = minNode1.moves + 1;
                    pq1.insert(neighbor);
                }
            }
            node1 = minNode1;

            // build of the second tree with twin as root
            Node minNode2 = pq2.delMin();
            for (Node neighbor : minNode2.neighbors()) {

                if (!neighbor.board.equals(minNode2.previous.board)) {
                    neighbor.previous = minNode2;
                    neighbor.moves = minNode2.moves + 1;
                    pq2.insert(neighbor);
                }
            }
            node2 = minNode2;
        }

        // we have exited the search so one of the boards is a goal!
        isSolvable = node1.board.isGoal(); // we are only interested in the solvability of node1?
        if (isSolvable) {
            node = node1;
            movesValue = node1.moves;
        } // if node2 is solvable we can be certain that node1 isn't see propriety of boards.
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        return movesValue;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;

        if (null != solutionBoards) return solutionBoards;
        else {
            Stack<Board> reversedStack = new Stack<>();
            Queue<Board> invertedSolutionQueue = new Queue<>();
            Queue<Board> solutionQueue = new Queue<>();

            while (null != node.previous) {
                invertedSolutionQueue.enqueue(node.board);
                node = node.previous;
            }
            while (!invertedSolutionQueue.isEmpty()) {
                reversedStack.push(invertedSolutionQueue.dequeue());
            }
            while (!reversedStack.isEmpty()) {
                solutionQueue.enqueue(reversedStack.pop());
            }
            solutionBoards = solutionQueue;
        }
        return solutionBoards;
    }

    // the Node type that holds:
    private static class Node implements Comparable<Node> {
        Board board;
        Node previous;
        int moves;
        final int manhattanValue;

        Node(Board board, int moves, Node previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
            this.manhattanValue = this.board.manhattan();
        }

        private final int priorityFunction() {
            return manhattanValue + moves; // We choose the manhattan priority function
        }

        // all the neighboring nodes
        Queue<Node> neighbors() {
            Queue<Node> nodeNeighbors = new Queue<>();
            for (Board neighbor : this.board.neighbors()) {
                Node nodeNeighbor = new Node(neighbor, this.moves + 1, this);
                nodeNeighbors.enqueue(nodeNeighbor);
            }
            return nodeNeighbors;
        }

        @Override
        public boolean equals(Object y) {
            if (y == this) return true;
            if (null == y) return false;
            if (y.getClass() != this.getClass()) return false;

            Node that = (Node) y;
            if (this.moves != that.moves) return false;
            if (this.priorityFunction() != that.priorityFunction()) return false;
            if (this.previous != that.previous) return false;
            if (!this.board.equals(that.board)) return false;
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(board, previous, priorityFunction(), moves);
        }

        @Override
        public int compareTo(Node that) {
            int res = -1;
            if (this.priorityFunction() > that.priorityFunction()) res = 1;
            else if (this.priorityFunction() < that.priorityFunction()) res = 0;
            else if (this.priorityFunction() == that.priorityFunction()) {
                if (this.manhattanValue > that.manhattanValue) res = 1;
                else if (this.manhattanValue < that.manhattanValue) res = 0;
                else res = 1;
            }
            return res;
        }


    } // END of Node class


// test client (see below)

/*    public static void main(String[] args) {

        // create initial board from file
         In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        StdOut.println("initial board :");
        StdOut.println(initial);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("1st call to moves() = " + solver.moves());
            solver.solution();
            StdOut.println("2nd call to moves() = " + solver.moves());
            solver.isSolvable();
            StdOut.println("3rd call to moves() = " + solver.moves());

            for (Board board : solver.solution())
                StdOut.println(board);

        }
    }
*/
}