/* *****************************************************************************
 *  Name:    Lucas Irwin
 *  NetID:   ljirwin
 *  Precept: P06
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *
 *  Description:  Creates a WordNet Digraph by reading in a file of synsets
 *                and a file of hypernyms and storing the synsets in two symbol
 *                tables, and the hypernyms in a digraph. Supports iteration
 *                over all Wordnet nouns, checking if a noun is in the Wordnet,
 *                finding the distance between two nouns and finding the
 *                shortest common ancestor of two nouns.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

public class WordNet {
    // Instance variable for WordNet digraph
    private Digraph G;
    // Instance variable for symbol table to contain ids as keys and synsets as
    // values
    private final ST<Integer, String> holder;
    // Instance variable for symbol table to contain nouns as keys and the ids
    // at which they appear as values
    private final ST<String, Bag<Integer>> holder2;
    // Instance variable for the ShortestCommonAncestor object
    private final ShortestCommonAncestor sca;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) throw new
                IllegalArgumentException("null argument");
        holder = new ST<Integer, String>();
        holder2 = new ST<String, Bag<Integer>>();
        In synset = new In(synsets);
        createSynset(synset);
        In hyper = new In(hypernyms);
        G = createDigraph(hyper);
        sca = new ShortestCommonAncestor(G);
    }

    // Used to test whether the symbol table was storing the values correctly
    //public ST<String, Bag<Integer>> print() {
    //return holder2;
    //}


    // private helper method to create two symbol tables to store ids and
    // synsets, and the ids which correspond to each noun
    private void createSynset(In synset) {
        while (synset.hasNextLine()) {
            String current = synset.readLine();
            String[] entry = current.split(",");
            int id = Integer.parseInt(entry[0]);
            holder.put(id, entry[1]);
            String[] nouns = entry[1].split(" ");
            for (int i = 0; i < nouns.length; i++) {
                if (!holder2.contains(nouns[i])) {
                    holder2.put(nouns[i], new Bag<Integer>());
                }
                holder2.get(nouns[i]).add(id);
            }
        }
    }

    // private helper method to create a Digraph from the hypernyms file
    private Digraph createDigraph(In hyper) {
        G = new Digraph(holder.size());
        while (hyper.hasNextLine()) {
            String current = hyper.readLine();
            String[] entry = current.split(",");
            for (int i = 1; i < entry.length; i++) {
                G.addEdge(Integer.parseInt(entry[0]),
                          Integer.parseInt(entry[i]));
            }
        }
        return G;
    }

    // all WordNet nouns
    public Iterable<String> nouns() {
        return holder2.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new
                IllegalArgumentException("null argument");
        return holder2.contains(word);

    }

    // a synset (second field of synsets.txt) that is a shortest common ancestor
    // of noun1 and noun2 (defined below)
    public String sca(String noun1, String noun2) {
        if (noun1 == null || noun2 == null) throw new
                IllegalArgumentException("null argument");
        if (!isNoun(noun1) || !isNoun(noun2)) throw new
                IllegalArgumentException("Not a Wordnet noun");
        Bag<Integer> firstNoun = holder2.get(noun1);
        Bag<Integer> secondNoun = holder2.get(noun2);
        // Uses the ancestorSubset method in the ShortestCommonAncestor class
        // to calculate the shortest common ancestor
        int ancestor = sca.ancestorSubset(firstNoun, secondNoun);
        return holder.get(ancestor);
    }

    // distance between noun1 and noun2 (defined below)
    public int distance(String noun1, String noun2) {
        if (noun1 == null || noun2 == null) throw new
                IllegalArgumentException("null argument");
        if (!isNoun(noun1) || !isNoun(noun2)) throw new
                IllegalArgumentException("Not a Wordnet noun");
        Bag<Integer> firstNoun = holder2.get(noun1);
        Bag<Integer> secondNoun = holder2.get(noun2);
        // Uses the lengthSubset method in the ShortestCommonAncestor class to
        // calculate distance
        int distance = sca.lengthSubset(firstNoun, secondNoun);
        return distance;
    }

    // unit testing
    public static void main(String[] args) {
        WordNet word = new WordNet(args[0], args[1]);
        // Test for several different paths
        // Should expect "physical_entity" and 7
        StdOut.println(word.sca("individual", "edible_fruit"));
        StdOut.println(word.distance("individual", "edible_fruit"));

        StdOut.println();
        // Nouns that are far apart
        // Should expect 23,33,27,29
        StdOut.println(word.distance("white_marlin", "mileage") + ":23");
        StdOut.println(word.distance("Black_Plague", "black_marlin") + ":33");
        StdOut.println(word.distance("American_water_spaniel", "histology")
                               + ":27");
        StdOut.println(word.distance("Brown_Swiss", "barrel_roll") + ":29");

        StdOut.println();
        // Should expect 3
        StdOut.println(word.distance("municipality", "region"));
        // Should expect "true"
        StdOut.println(word.isNoun("President"));
        StdOut.println();
        // Print out all nouns
        StdOut.println(word.nouns());

        // Test to check if symbol table was working
        /*ST<String, Bag<Integer>> holder = word.print();
        for (String s : holder) {
            StdOut.print(s);
            for (int t : holder.get(s)) {
                StdOut.println(" " + t);
            }
        }*/
    }
}
