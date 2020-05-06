

import edu.princeton.cs.algs4.*;


import java.util.Objects;


public class Solver {
    private Node node;
    private final Board initial;


    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (null == initial) throw new IllegalArgumentException();
        this.initial = initial;
        node = new Node(initial);
        MinPQ<Node> pq = new MinPQ<>();
        pq.insert(node); // insert initial node into PQ

        while (!node.board.isGoal()) { // TODO OR not solvable board condition here to avoid infinite loop ??
            Node minNode = pq.delMin(); // dequeue the min node

            for (Node neighbor : minNode.neighbors()) {
                //  TODO avoid NULL Pointer Here CHECK EQUALITY IMPLEMENTATION ON BOARD!!!!
                if (!neighbor.board.equals(minNode.board)) { // DO NOT insert previously inserted board. critical optimisation
                    neighbor.previous = minNode; // we link the two nodes
                    neighbor.moves = minNode.moves + 1; // we add one move to the minNode count
                    pq.insert(neighbor);
                }
            }
            node = minNode; // we replace the current node with the dequeued node.
        }
    }

    // TODO CHECK SOLVABILITY RULE
    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return true;
    }

    // min number of moves to solve initial board
    public int moves() {
        return node.moves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        Stack<Board> reversedStack = new Stack<>();
        Queue<Board> invertedSolutionQueue = new Queue<>();
        Queue<Board> solutionQueue = new Queue<>();

        solutionQueue.enqueue(initial);

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
        return solutionQueue;
    }

    // the Node type that holds:
    private static class Node implements Comparable<Node> {
        Board board;
        Node previous;
        int priority;
        int moves;

        Node(Board board) {
            this.board = board;
            // It takes 1 move from previous to get to this node so we add 1 to the previous moves
            this.moves = 0; //previous == null ? 0 : previous.moves + 1;
            this.priority = priorityFunction();
            this.previous = null;
        }

        int priorityFunction() {
            // int hamming = this.board.hamming + moves;
            int res = this.board.manhattan() + moves;
            return res; // We try the manhattan priority function
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
//
    public static void main(String[] args) {

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
            StdOut.println("Solution board has minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);

        }
    }

}