/* *****************************************************************************
 *  Name:    Lucas Irwin
 *  NetID:   ljirwin
 *  Precept: P06
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *
 *  Description: Identifies the outcast in a group of nouns by utilizing methods
 *               in the WordNet class.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    // Instance variable for WordNet object
    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int distance;
        int greatest = 0;
        String champion = "";
        for (int i = 0; i < nouns.length; i++) {
            distance = 0;
            for (int j = 0; j < nouns.length; j++) {
                distance += wordnet.distance(nouns[i], nouns[j]);
            }
            if (distance > greatest) {
                champion = nouns[i];
                greatest = distance;
            }
        }
        return champion;

    }


    public static void main(String[] args) {

        // Test for different outcasts
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}

