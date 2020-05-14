
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

import java.util.Objects;

public class Solver {
    private int movesValue;
    private boolean isSolvable;
    private Node node1;
    private Node node2;
    private final Board initial;
    private final Board twin;
    private Iterable<Board> solutionBoards;


    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (null == initial) throw new IllegalArgumentException();
        movesValue = -1;
        this.initial = initial;
        this.twin = initial.twin();
        this.node1 = new Node(initial);
        this.node2 = new Node(twin);

        //Exactly one of the two (initial node or any twin node made from initial) will lead to the goal board.
        // we run A* on two board instances, in locksteps, to find which board will lead to the goal board
        MinPQ<Node> pq1 = new MinPQ<>(); // the initial board PQ
        MinPQ<Node> pq2 = new MinPQ<>(); // a twin board PQ

        int counter1 = 0;// first time in the loop
        int counter2 = 0;

        pq1.insert(node1); // insert initial node into PQ
        pq2.insert(node2);

        // the first goal board found exits the search as they can't both be a goal board
        while (!node1.board.isGoal() && !node2.board.isGoal()) {

            // Delete the node with the minimum priority,
            Node minNode1 = pq1.delMin();
            Node minNode2 = pq2.delMin();

            if (counter1 == 0) {
                counter1++;
                if (minNode1.board.equals(initial)) {
                    pq1.insert(minNode1); // re-insert initial board that has just been dequeued
                }
            } else {
                // insert all its neighbors. build the game tree
                for (Node neighbor1 : minNode1.neighbors()) {
                    int initialNeighborsCount = minNode1.neighbors().size();
                    if (counter1 <= initialNeighborsCount) { // minNode.board is the initial board
                        counter1++;
                        neighbor1.previous = minNode1; // we link the two nodes
                        neighbor1.moves = minNode1.moves + 1;
                        pq1.insert(neighbor1); // we insert all the neighbors of initial board
                    } else if (counter1 > initialNeighborsCount) { // to avoid the null pointer of minNode.previous.board
                        // DO NOT insert previously inserted board. critical optimisation
                        if (!neighbor1.board.equals(minNode1.previous.board)) {
                            neighbor1.previous = minNode1; // we link the two nodes
                            neighbor1.moves = minNode1.moves + 1;
                            pq1.insert(neighbor1);
                        }
                    }
                }
                node1 = minNode1; // we replace the current node with the dequeued node.
            }

            // BUILD OF THE SECOND GAME TREE
            if (counter2 == 0) {
                counter2++;
                if (minNode2.board == twin) {
                    pq2.insert(minNode2);
                }
            } else {
                for (Node neighbor2 : minNode2.neighbors()) {
                    int twinNeighborsCount = minNode2.neighbors().size();
                    if (counter2 <= twinNeighborsCount ) {
                        counter2++;
                        neighbor2.previous = minNode2;
                        neighbor2.moves = minNode2.moves + 1;
                        pq2.insert(neighbor2);
                    } else if (counter2 > twinNeighborsCount) {
                        if (!neighbor2.board.equals(minNode2.previous.board)) {
                            neighbor2.previous = minNode2;
                            neighbor2.moves = minNode2.moves + 1;
                            pq2.insert(neighbor2);
                        }
                    }
                }
                node2 = minNode2;
            }
        }
        // we have exited the search so one of the boards is a goal!
        isSolvable = node1.board.isGoal();
        if (isSolvable) {
            movesValue = node1.moves;
        }
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
        if (!isSolvable) return null;

        if (null == solutionBoards) {
            Stack<Board> reversedStack = new Stack<>();
            Queue<Board> invertedSolutionQueue = new Queue<>();
            Queue<Board> solutionQueue = new Queue<>();

            solutionQueue.enqueue(initial);

            while (null != node1.previous) {
                invertedSolutionQueue.enqueue(node1.board);
                node1 = node1.previous;
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
        int manhattanValue;
        int priority;
        int moves;

        Node(Board board) {
            this.board = board;
            this.manhattanValue = this.board.manhattan();
            this.priority = priorityFunction();
            // It takes 1 move from previous to get to this node so we add +1 to the previous moves
            this.moves = previous == null ? 0 : previous.moves + 1;
        }

        int priorityFunction() {
            return manhattanValue + moves; // We choose the manhattan priority function
        }

        // all the neighboring nodes
        Queue<Node> neighbors() {
            Queue<Node> nodeNeighbors = new Queue<>();
            for (Board neighbor : this.board.neighbors()) {
                Node nodeNeighbor = new Node(neighbor);
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
            if (!this.board.equals(that.board)) return false;
            if (this.priority != that.priority) return false;
            if (this.previous != that.previous) return false;
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(board, previous, priority, moves);
        }

        @Override
        public int compareTo(Node that) {
            return this.priorityFunction() >= that.priorityFunction() ? 1 : 0;
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