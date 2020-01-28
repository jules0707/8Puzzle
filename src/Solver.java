import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;


public class Solver {
    Board board;
    Node node;
    Node minNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (null == initial) throw new IllegalArgumentException();
        int n = initial.dimension();
        node = new Node(initial);
        MinPQ<Node> pq = new MinPQ();


        // board = new Board(initial.board); // TODO to avoid mutation. Check how is this int[][] built from board ???
        // solutionQueue = new Queue(); // redundant structure

        pq.insert(node); // insert initial node into PQ
        node.moves = 0;
        node.previous = null;
        while (!node.board.isGoal()) { // TODO OR not solvable board condition here to avoid infinite loop ??
            minNode = pq.delMin(); // dequeue / pop the min node
            for (Node neighbor : minNode.neighbors()) {
                if (!neighbor.equals(minNode.previous)) { //TODO DO NOT insert previously inserted board. critical optimisation
                    // use the heuristics property here of the priority function
                    // that increases as we go up the graph
                    //  if (minNode.priority > neighbor.priority) {
                    pq.insert(neighbor);
                    neighbor.previous = minNode;// we link the two nodes
                    //}
                }
            }
            node = minNode; // we replace the current node with the dequeued node.
            minNode.moves++; // we add one move to get to the minNode
        }
    }

    // TODO CHECK SOLVABILITY RULE
    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return true;
    }

    // min number of moves to solve initial board
    public int moves() {
        return minNode.moves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        Queue<Board> solutionQueue = new Queue<>();
        while (null != node.previous) {
            solutionQueue.enqueue(node.board);
            node = node.previous;
        }
        // TODO REVERSE OFDER
        return solutionQueue;
    }

    // the Node type that holds:
    private static class Node implements Comparable<Node> {
        int priority;
        int moves;
        Board board;
        Node previous;

        public Node(Board board) {
            this.board = board;
            // TODO DEFINE MOVES HERE
            this.moves = this.previous == null ? 0 : 1;
            priority = this.priority();
            previous = null;
        }

        public int priority() {
            int hamming = this.board.hamming + moves;
            int manhattan = this.board.manhattan + moves;
            return manhattan; // We try the manhattan priority function
        }

        // all the neighboring nodes
        public Queue<Node> neighbors() {
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
        public int compareTo(Node that) {
            return this.priority() >= that.priority() ? 1 : 0;
        }
    }


    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}