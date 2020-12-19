/* *****************************************************************************
 *  Name:    Lucas Irwin
 *  NetID:   ljirwin
 *  Precept: P06
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *
 *  Description: Creates a Shortest Common Ancestor object from a DAG. Supports
 *               calculating the length and ancestor of two vertices or two
 *               subsets of vertices in time proportional to the vertices and
 *               edges reachable from the argument vertices by reimplementing
 *               breadth first search and using two Linear Probing HashTables.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class ShortestCommonAncestor {
    // instance variable for the digraph
    private final Digraph G;

    // constructor takes a rooted DAG as argument
    public ShortestCommonAncestor(Digraph G) {
        if (G == null) throw new IllegalArgumentException("null argument");
        this.G = new Digraph(G);
        DirectedCycle acyclic = new DirectedCycle(this.G);
        if (acyclic.hasCycle()) throw new IllegalArgumentException("not a DAG");
        int count = 0;
        // Check to seed if the digraph is rooted by looping over all vertices
        // and maintaining a counter to ensure that only one vertex has 0 edges
        for (int v = 0; v < G.V(); v++) {
            if (G.outdegree(v) == 0) count++;
        }
        if (count != 1) throw new
                IllegalArgumentException("not a DAG");
    }

    // private BFS helper method for extra credit
    private LinearProbingHashST<Integer, Integer> bfs(Digraph digraph,
                                                      Iterable<Integer> s) {
        if (digraph == null) throw new IllegalArgumentException("null argument");
        // Linear Probing Hash Table to store the vertices reachable from s
        // as keys and their distance from s as values
        LinearProbingHashST<Integer, Integer> distTo = new
                LinearProbingHashST<Integer, Integer>();
        Queue<Integer> current = new Queue<>();
        // Enqueue all the vertices passed to bfs
        for (int a : s) {
            current.enqueue(a);
            distTo.put(a, 0);
        }

        // breadth first search
        /* Adapted from:
         * https://www.cs.princeton.edu/courses/archive/fall20/cos226/lectures/4
         * GraphsDigraphsII.pdf  Accessed: 10/24/2020 */
        while (!current.isEmpty()) {
            int v = current.dequeue();
            for (int w : digraph.adj(v)) {
                if (!distTo.contains(w)) {
                    current.enqueue(w);
                    distTo.put(w, distTo.get(v) + 1);
                }
            }
        }
        return distTo;
    }


    // length of shortest ancestral path between v and w
    public int length(int v, int w) {
        Queue<Integer> vw = new Queue<>();
        vw.enqueue(v);
        LinearProbingHashST<Integer, Integer> vTable = bfs(G, vw);
        vw.dequeue();
        vw.enqueue(w);
        LinearProbingHashST<Integer, Integer> wTable = bfs(G, vw);
        // Maintain a champion variable to compare distances
        int champion = Integer.MAX_VALUE;
        int sum;
        // For each vertex in the first ST check if the second ST contains it
        // and if so calculate the sum of their distances
        // if the sum is smaller than the champion, then update champion to the
        // sum
        for (int ancestor : vTable.keys()) {
            if (wTable.contains(ancestor)) {
                sum = vTable.get(ancestor) + wTable.get(ancestor);
                if (sum < champion) champion = sum;
            }
        }
        return champion;
    }

    // a shortest common ancestor of vertices v and w
    public int ancestor(int v, int w) {
        Queue<Integer> vw = new Queue<>();
        vw.enqueue(v);
        LinearProbingHashST<Integer, Integer> vTable = bfs(G, vw);
        vw.dequeue();
        vw.enqueue(w);
        LinearProbingHashST<Integer, Integer> wTable = bfs(G, vw);
        int champion = Integer.MAX_VALUE;
        // For shortest common ancestor
        int sca = 0;
        int sum;
        // As before check if both STs contain the same ancestor vertex and
        // if so compare the sum of distances to the champion
        // If smaller, update champion to the sum and the sca to that ancestor
        for (int ancestor : vTable.keys()) {
            if (wTable.contains(ancestor)) {
                sum = vTable.get(ancestor) + wTable.get(ancestor);
                if (sum < champion) {
                    sca = ancestor;
                    champion = sum;
                }
            }
        }
        return sca;
    }

    // length of shortest ancestral path of vertex subsets A and B
    public int lengthSubset(Iterable<Integer> subsetA,
                            Iterable<Integer> subsetB) {
        if (subsetA == null || subsetB == null) throw new
                IllegalArgumentException("null argument");
        if (!subsetA.iterator().hasNext() || !subsetB.iterator().hasNext()) {
            throw new IllegalArgumentException("empty subset");
        }
        if (!checkIterator(subsetA) || !checkIterator(subsetB)) throw
                new IllegalArgumentException("null argument");
        LinearProbingHashST<Integer, Integer> a = bfs(G, subsetA);
        LinearProbingHashST<Integer, Integer> b = bfs(G, subsetB);
        int champion = Integer.MAX_VALUE;
        int sum;
        for (int ancestor : a.keys()) {
            if (b.contains(ancestor)) {
                sum = a.get(ancestor) + b.get(ancestor);
                if (sum < champion) champion = sum;
            }
        }
        return champion;

    }

    // a shortest common ancestor of vertex subsets A and B
    public int ancestorSubset(Iterable<Integer> subsetA,
                              Iterable<Integer> subsetB) {
        if (subsetA == null || subsetB == null) throw new
                IllegalArgumentException("null argument");
        if (!subsetA.iterator().hasNext() || !subsetB.iterator().hasNext()) {
            throw new IllegalArgumentException("empty subset");
        }
        if (!checkIterator(subsetA) || !checkIterator(subsetB)) throw
                new IllegalArgumentException("null argument");
        LinearProbingHashST<Integer, Integer> a = bfs(G, subsetA);
        LinearProbingHashST<Integer, Integer> b = bfs(G, subsetB);
        int champion = Integer.MAX_VALUE;
        int sca = 0;
        int sum;
        for (int ancestor : a.keys()) {
            if (b.contains(ancestor)) {
                sum = a.get(ancestor) + b.get(ancestor);
                if (sum < champion) {
                    sca = ancestor;
                    champion = sum;
                }
            }
        }
        return sca;
    }

    // private helper method to check if an Iterable contains a null item
    private boolean checkIterator(Iterable<Integer> subset) {
        for (Integer a : subset) {
            if (a == null) return false;
        }
        return true;
    }

    // unit testing
    public static void main(String[] args) {
        // Used digraph25.txt to test
        In in = new In(args[0]);
        Digraph d = new Digraph(in);
        ShortestCommonAncestor SCA = new ShortestCommonAncestor(d);
        int length = SCA.length(4, 10);
        int sca = SCA.ancestor(4, 10);
        StdOut.println(length);
        StdOut.println(sca);
        StdOut.println();

        // Test for subset methods
        Queue<Integer> a = new Queue<Integer>();
        a.enqueue(13);
        a.enqueue(23);
        a.enqueue(24);
        Queue<Integer> b = new Queue<Integer>();
        b.enqueue(16);
        b.enqueue(17);
        b.enqueue(6);
        int subLength = SCA.lengthSubset(a, b);
        int subAncestor = SCA.ancestorSubset(a, b);
        StdOut.println(subLength);
        StdOut.println(subAncestor);
    }
}
