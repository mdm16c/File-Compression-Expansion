/*
 * Software Engineering II
 * Fall 2019
 * Matthew McCracken
 *
 * expand files based on their extension
 * java Deschubs <compressed file path>
 */
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Deschubs {

    // alphabet size of extended ASCII
    private static final int R = 256;        // number of input chars
    private static final int L = 4096;       // number of codewords = 2^W
    private static final int W = 12;         // codeword width

    // Huffman trie node
    private static class Node implements Comparable<Node> {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }

        // is the node a leaf node?
        private boolean isLeaf() {
            assert (left == null && right == null) || (left != null && right != null);
            return (left == null && right == null);
        }

        // compare, based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }

    private static String getExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else 
            return "";
    }

    private static String readFile(String filePath) throws IOException {
        String content = "";
        content = new String(Files.readAllBytes(Paths.get(filePath)));
        return content;
    }

    private static String getFilename(File f) {
        String input = f.getAbsolutePath();
        if (input == null) 
            return null;
        int pos = input.lastIndexOf(".");
        if (pos == -1) 
            return input;
        return input.substring(0, pos);
    }

    // expand Huffman-encoded input from standard input and write to standard output
    public static void expandH(File f) throws IOException {

        if (!f.exists())
            throw new RuntimeException("file: " + f.getAbsolutePath() + " does not exist.");

    	if (readFile(f.getAbsolutePath()).equals("")) {
            File temp = null;
            if (getExtension(f).equals("zh")) {
                temp = new File(f.getAbsolutePath());
            }
            else {
                temp = new File(getFilename(f));
            }
            temp.createNewFile();
            return;
        }

        BinaryIn bin = null;
        BinaryOut bout = null;
        bin = new BinaryIn(f.getAbsolutePath());
        if (getExtension(f).equals("zh")) {
            bout = new BinaryOut(f.getAbsolutePath());
        }
        else {
            bout = new BinaryOut(getFilename(f));
        }

        // read in Huffman trie from input stream
        Node root = readTrie(bin); 

        // number of bytes to write
        int length = bin.readInt();

        // decode using the Huffman trie
        for (int i = 0; i < length; i++) {
            Node x = root;
            while (!x.isLeaf()) {
                boolean bit = bin.readBoolean();
                if (bit)
                    x = x.right;
                else
                    x = x.left;
            }
            bout.write(x.ch);
        }
        bout.close();
        bin.close();
    }


    private static Node readTrie(BinaryIn bin) {
        boolean isLeaf = bin.readBoolean();
        if (isLeaf) {
	    char x = bin.readChar();
            return new Node(x, -1, null, null);
        }
        else {
            return new Node('\0', -1, readTrie(bin), readTrie(bin));
        }
    }

    public static void expandL(File f) throws IOException {

        if (!f.exists())
            throw new RuntimeException("file: " + f.getAbsolutePath() + " does not exist.");

        if (readFile(f.getAbsolutePath()).equals("")) {
            File temp = null;
            if (getExtension(f).equals("zl")) {
                temp = new File(f.getAbsolutePath());
            }
            else {
                temp = new File(getFilename(f));
            }
            temp.createNewFile();
            return;
        }

        String[] st = new String[L];
        int i;

        for (i = 0; i < R; i++) {
            st[i] = "" + (char) i;
        }
        st[i++] = "";

        BinaryIn bin = null;
        bin = new BinaryIn(f.getAbsolutePath());

        int codeword = 0;
        if (!bin.isEmpty()) {
            codeword = bin.readInt(W);
        }
        String val = st[codeword];

        BinaryOut bout = null;
        if (getExtension(f).equals("zl")) {
            bout = new BinaryOut(f.getAbsolutePath());
        }
        else {
            bout = new BinaryOut(getFilename(f));
        }

        while (true) {
            bout.write(val);
            if (!bin.isEmpty()) {
                codeword = bin.readInt(W);
            }
            if (codeword == R) {
                break;
            }
            String s = st[codeword];
            if (i == codeword) {
                s = val + val.charAt(0);
            }
            if (i < L) {
                st[i++] = val + s.charAt(0);
            }
            val = s;
        }
        bin.close();
        bout.close();
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0)
            throw new RuntimeException("pass in the name of an existing file that ends in hh or ll.");

        File args0 = new File(args[0]);

        if (getExtension(args0).equals("ll")) {
            expandL(args0);
        }
        else if (getExtension(args0).equals("hh")) {
            expandH(args0);
        }
        else if (getExtension(args0).equals("zl")) {
            expandL(args0);
            Untars.main(new String[] {args0.getAbsolutePath()});
        }
        else if (getExtension(args0).equals("zh")) {
            expandH(args0);
            Untars.main(new String[] {args0.getAbsolutePath()});
        }
        else {
            throw new RuntimeException("This file type not supported");
        }
    }
}
