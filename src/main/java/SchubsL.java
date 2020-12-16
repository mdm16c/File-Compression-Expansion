/*
 * Software Engineering II
 * Fall 2019
 * Matthew McCracken
 *
 * compress and number of files with LZW compression
 * java SchubsL <file> <file>...
 */

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;

public class SchubsL {
    private static final int R = 256;        // number of input chars
    private static final int L = 4096;       // number of codewords = 2^W
    private static final int W = 12;         // codeword width

    private static String extension = "";

    private static String readFile(String filePath) throws IOException {
        String content = "";
        content = new String(Files.readAllBytes(Paths.get(filePath)));
        return content;
    }

    private static String getExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else 
            return "";
    }

    public static void compress(File f) throws IOException {

        if (!f.exists())
            throw new RuntimeException("file: " + f.getAbsolutePath() + " does not exist.");

        String input = readFile(f.getAbsolutePath());

        if (input.equals("")) {
            File temp = new File(f.getAbsolutePath() + extension);
            temp.createNewFile();
            return;
        }

        if (input.length() > 0) {

            TST<Integer> st = new TST<Integer>();

            for (int i = 0; i < R; i++)
                st.put("" + (char) i, i);
            int code = R+1;

            BinaryOut bout = null;
            bout = new BinaryOut(f.getAbsolutePath() + extension);

            while (input.length() > 0) {
                String s = st.longestPrefixOf(input);
                bout.write(st.get(s), W);
                int t = s.length();
                if (t < input.length() && code < L)
                    st.put(input.substring(0, t + 1), code++);
                input = input.substring(t);
            }

            bout.write(R, W);
            bout.close();
        }
    } 

    public static void main(String[] args) throws IOException {

        if (args.length == 0)
            throw new RuntimeException("pass in the name of an archive, file, or glob of files.");

        if (getExtension(new File(args[0])).equals("zl")) {
            extension = "";
        }
        else {
            extension = ".ll";
        }

        for (int i = 0; i < args.length; i++) {
            compress(new File(args[i]));
        }
    }
}