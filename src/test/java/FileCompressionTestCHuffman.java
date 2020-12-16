/*
 * Software Engineering II
 * Fall 2019
 * Matthew McCracken
 *
 * test file compression and decompression classes for C using Huffman
 */

import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Random;
import java.util.ArrayList;

import java.lang.StringBuilder;

public class FileCompressionTestCHuffman {

    private final String sep = System.getProperty("file.separator");

    //setup
    @Before
    public void createFilesFolder() throws IOException {
        File resources = new File("src" + sep + "files");
        if (!resources.exists()) {
            resources.mkdir();
        }
    }

    //util functions

    private String getExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else 
            return "";
    }

    private void createDir(File d, int createFiles) throws IOException {
        if (d.exists()) {
            d.delete();
        }
        d.mkdir();

        Random random = new Random();

        for (int i = 0; i < createFiles; i++) {
            File f = new File(d, getRandomFilename(random.nextInt(10)+1)+".txt");
            populateFile(f, getRandomString(random.nextInt(20)));   
        }
    }

    private void fileWalkerDelete(File f) throws IOException {
        //base case
        if (f.listFiles() == null || f.listFiles().length == 0) {
            f.delete();
            return;
        }

        //recursive loop
        for (File file : f.listFiles()) {
            if (file.isDirectory()) {
                fileWalkerDelete(file);
                file.delete();
            }
            else {
                file.delete();
            }
        }
        f.delete();
    }

    private ArrayList<String> allFilesWithExtension = new ArrayList<String>();

    private String[] getVar() {
        String[] s = allFilesWithExtension.toArray(new String[allFilesWithExtension.size()]);
        allFilesWithExtension.clear();
        return s;
    }

    private void getAllWithExtension(File d, String extension) throws IOException {
        if (d.isDirectory()) {
            for (File f : d.listFiles()) {
                if (f.isDirectory()) {
                    getAllWithExtension(f, extension);
                }
                else {
                    if (getExtension(f).equals(extension)) {
                        allFilesWithExtension.add(f.getAbsolutePath());
                    }
                }
            }
        }
        else {
            if (getExtension(d).equals(extension)) {
                allFilesWithExtension.add(d.getAbsolutePath());
            }
        }
    }

    private boolean checkBytes(File f1, File f2) throws IOException {
        FileInputStream in1 = null;
        FileInputStream in2 = null;
        in1 = new FileInputStream(f1.toString());
        in2 = new FileInputStream(f2.toString());

        if (f1.length() != f2.length()) {
            return false;
        }

        int c1 = 0;
        int c2 = 0;
        for (int i = 0; i < f1.length(); i++) {
            c1 = in1.read();
            c2 = in2.read();
            if (c1 != c2) {
                if (in1 != null) {
                    in1.close();
                }
                if (in2 != null) {
                    in2.close();
                }
                return false;
            }
        }
        return true;
    }

    private String getRandomString(int length) throws IOException {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890`~!@#$%^&*()-_=+[{]};:'<,>.?/|";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String getRandomFilename(int length) throws IOException {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void populateFile(File f, String contents) throws IOException {
        //create file
        if (f.exists()) {
            f.delete();
        }
        f.createNewFile();

        //populate file
        FileOutputStream out = null;
        out = new FileOutputStream(f);
        byte[] b = contents.getBytes();
        for (int i = 0; i < b.length; i++) {
            out.write(b[i]);
        }
        if (out != null) {
            out.close();
        }
    }

    private void copy(File src, File dest) throws IOException { 
        File f = new File(dest.getAbsolutePath());
        f.createNewFile();

        //get contents of file
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(f);
            int c;

            //copy everything over between files
            for (long i = 0; i < src.length(); i++) {
                c = in.read();
                out.write(c);
            }
        }
        finally {
            //close file streams
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }

    private String[] schubify(String[] arr, String type) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] += type;
        }
        return arr;
    }

    //tests

    //Huffman normal file
    @Test
    public void testHuffman_normalFile() throws IOException {

        //gets working dir
        File d = new File("src" + sep + "files" + sep + "Test 1");
        if (d.exists()) {
            fileWalkerDelete(d);
        }

        //creates dir with 1 file inside
        createDir(d, 1);
        
        //gets file
        getAllWithExtension(d, "txt");
        String[] s = getVar();

        //copies file
        File temp = new File(s[0]);
        copy(temp, new File(temp.getParentFile(), "COPY" + temp.getName()));

        //compress file
        SchubsH.main(s);

        //get file list
        String[] s2 = new String[s.length];
        s2[0] = s[0];

        //get compressed file list
        schubify(s2, ".hh");

        //decompress file
        Deschubs.main(s2);

        //checks that files are the same
        assertTrue(checkBytes(temp, new File(temp.getParentFile(), "COPY" + temp.getName())));
    }

    //LZW normal glob
    @Test
    public void testHuffman_normalGlob() throws IOException {

        //gets working dir
        File d = new File("src" + sep + "files" + sep + "Test 2");
        if (d.exists()) {
            fileWalkerDelete(d);
        }

        //creates dir with 10 files inside
        createDir(d, 10);
        
        //gets all txt files in dir
        getAllWithExtension(d, "txt");
        String[] s = getVar();

        //makes a copy of all files
        for (int i = 0; i < s.length; i++) {
            File temp = new File(s[i]);
            copy(temp, new File(temp.getParentFile(), "COPY" + temp.getName()));
        }

        //compress all files
        SchubsH.main(s);

        //copy file list array
        String[] s2 = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            s2[i] = s[i];
        }

        //get list of .hh files
        schubify(s2, ".hh");

        //expand all hh files
        for (int i = 0; i < s2.length; i++) {   
            Deschubs.main(new String[] {s2[i]});
        }

        //check to make sure original copies are the same as expanded files 
        for (int i = 0; i < s.length; i++) {
            File temp = new File(s[i]);
            assertTrue(checkBytes(temp, new File(temp.getParentFile(), "COPY" + temp.getName())));
        }
    }

    //Huffman file deleted after compression
    @Test
    public void testHuffman_fileDeletedAfterCompress() throws IOException {

        //gets working dir
        File d = new File("src" + sep + "files" + sep + "Test 3");
        if (d.exists()) {
            fileWalkerDelete(d);
        }

        //creates dir with 1 file inside
        createDir(d, 1);
        
        //gets file
        getAllWithExtension(d, "txt");
        String[] s = getVar();

        //copies file
        File temp = new File(s[0]);
        copy(temp, new File(temp.getParentFile(), "COPY" + temp.getName()));

        //compress file
        SchubsH.main(s);

        //delete file
        temp.delete();

        //get file list
        String[] s2 = new String[s.length];
        s2[0] = s[0];

        //get compressed file list
        schubify(s2, ".hh");

        //decompress file
        Deschubs.main(s2);

        //checks that files are the same
        assertTrue(checkBytes(temp, new File(temp.getParentFile(), "COPY" + temp.getName())));
    }

    //Huffman normal file with spaces and endlines
    @Test
    public void testHuffman_normalFileWithEndlAndSpaces() throws IOException {

        //gets working dir
        File d = new File("src" + sep + "files" + sep + "Test 4");
        if (d.exists()) {
            fileWalkerDelete(d);
        }

        //creates working dir
        createDir(d, 0);

        //manually add file with endlines and spaces
        String e = System.lineSeparator();
        populateFile(new File(d, getRandomFilename(10)+".txt"), getRandomString(25) + "    "+e+e+e+"    " + getRandomString(25) + "  "+e+e+"       "+e+e+e);
        
        //gets file
        getAllWithExtension(d, "txt");
        String[] s = getVar();

        //copies file
        File temp = new File(s[0]);
        copy(temp, new File(temp.getParentFile(), "COPY" + temp.getName()));

        //compress file
        SchubsH.main(s);

        //get file list
        String[] s2 = new String[s.length];
        s2[0] = s[0];

        //get compressed file list
        schubify(s2, ".hh");

        //decompress file
        Deschubs.main(s2);

        //checks that files are the same
        assertTrue(checkBytes(temp, new File(temp.getParentFile(), "COPY" + temp.getName())));
    }

    //Huffman empty file
    @Test
    public void testHuffman_emptyFile() throws IOException {

        //gets working dir
        File d = new File("src" + sep + "files" + sep + "Test 5");
        if (d.exists()) {
            fileWalkerDelete(d);
        }

        //creates working dir
        createDir(d, 0);
        populateFile(new File(d, getRandomFilename(10)+".txt"), "");
        
        //gets file
        getAllWithExtension(d, "txt");
        String[] s = getVar();

        //copies file
        File temp = new File(s[0]);
        copy(temp, new File(temp.getParentFile(), "COPY" + temp.getName()));

        //compress file
        SchubsH.main(s);

        //get file list
        String[] s2 = new String[s.length];
        s2[0] = s[0];

        //get compressed file list
        schubify(s2, ".hh");

        //decompress file
        Deschubs.main(s2);

        //checks that files are the same
        assertTrue(checkBytes(temp, new File(temp.getParentFile(), "COPY" + temp.getName())));
    }

    //Huffman wrong number of args
    @Test
    public void testHuffman_badArgs() throws IOException {

        //gets working dir
        File d = new File("src" + sep + "files" + sep + "Test 6");
        if (d.exists()) {
            fileWalkerDelete(d);
        }

        //creates dir with 1 file inside
        createDir(d, 1);
        
        //gets file
        getAllWithExtension(d, "txt");
        allFilesWithExtension.remove(0);
        String[] s = getVar();

        //if program breaks then the case passes, otherwise case fails
        try {
            SchubsH.main(s);

        } catch (Exception e) {
            getVar();
            assert(true);
            return;
        }
        getVar();
        assert(false);
    }

    //Huffman wrong number of args
    @Test
    public void testHuffman_fileDoesNotExist() throws IOException {

        //gets working dir
        File d = new File("src" + sep + "files" + sep + "Test 7");
        if (d.exists()) {
            fileWalkerDelete(d);
        }

        //creates dir with 1 file inside
        createDir(d, 1);
        
        //gets file
        getAllWithExtension(d, "txt");
        String[] s = getVar();

        //copies file
        File temp = new File(s[0]);
        copy(temp, new File(temp.getParentFile(), "COPY" + temp.getName()));

        //delete file so it does not exist
        temp.delete();

        //if program breaks then the case passes, otherwise case fails
        try {
            SchubsH.main(s);

        } catch (Exception e) {
            getVar();
            assert(true);
            return;
        }
        getVar();
        assert(false);
    }

    //Deschubs bad args
    @Test
    public void testAll_DeschubsBadArgs() throws IOException {

        //gets working dir
        File d = new File("src" + sep + "files" + sep + "Test 8");
        if (d.exists()) {
            fileWalkerDelete(d);
        }

        //if program breaks then the case passes, otherwise case fails
        try {
            Deschubs.main(new String[] {});

        } catch (Exception e) {
            getVar();
            assert(true);
            return;
        }
        getVar();
        assert(false);
    }
    
    //Deschubs wrong filetype
    @Test
    public void testAll_DeschubsWrongFiletype() throws IOException {

        //gets working dir
        File d = new File("src" + sep + "files" + sep + "Test 9");
        if (d.exists()) {
            fileWalkerDelete(d);
        }
        createDir(d, 0);

        File f = new File(d, getRandomFilename(10)+".txt");
        f.createNewFile();

        //if program breaks then the case passes, otherwise case fails
        try {
            Deschubs.main(new String[] {f.getAbsolutePath()});

        } catch (Exception e) {
            getVar();
            assert(true);
            return;
        }
        getVar();
        assert(false);
    }
}