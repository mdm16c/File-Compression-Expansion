/*
 * Software Engineering II
 * Fall 2019
 * Matthew McCracken
 *
 * compress and archive any number of files with LZW compression
 * java SchubsArc <archive name> <file> <file>...
 */

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;

public class SchubsArc {

    private static String getExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else 
            return "";
    }

    public static void main(String[] args) throws IOException {

        if (args.length < 1)
            throw new RuntimeException("pass in the name of an archive, followed by files to be included");

        File archive = new File(args[0]);

        if (args.length == 1) {
            archive.createNewFile();
            return;
        }

        ArrayList<String> filesForArchive = new ArrayList<String>();
        filesForArchive.add(archive.getAbsolutePath());
        for (int i = 1; i < args.length; i++) {
            filesForArchive.add(args[i]);
        }

        Tarsn.main(filesForArchive.toArray(new String[filesForArchive.size()]));

        if (getExtension(archive).equals("zl")) {
            SchubsL.main(new String[] {archive.getAbsolutePath()});
        }
        else if (getExtension(archive).equals("zh")) {
            SchubsH.main(new String[] {archive.getAbsolutePath()});
        }
        else {
            throw new RuntimeException("invalid archive file extension");
        }

        filesForArchive.clear();
        filesForArchive = null;
    }
}