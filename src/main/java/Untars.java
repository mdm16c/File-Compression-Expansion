/*
 * Software Engineering II
 * Fall 2019
 * Matthew McCracken
 *
 * untar a tar file
 * java Untars <archive name>
 */

import java.io.IOException;
import java.io.File;

public class Untars {

	private static void createIfNotExist(File f) throws IOException {
		if (!f.exists()) {
			createIfNotExist(f.getParentFile());
			f.mkdir();
		}
		return;
	}

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			throw new RuntimeException("pass in an archive name");
		}

		BinaryIn in = null;
		BinaryOut out = null;

		char sep = (char) 255;

		//nerf through archive, extracting files
		//int lengthoffilename, sep, filename, sep, lengthoffile, sep, bits
		//add while here for multiple files with condition file not empty
		//will need another out.close() with loop
		try {
			in = new BinaryIn(args[0]);
			while (!in.isEmpty()) {
				int filenamesize = in.readInt();
				sep = in.readChar();
				String filename = "";

				for(int i=0; i<filenamesize; i++) {
					filename += in.readChar();
				}

				sep = in.readChar();
				long filesize = in.readLong();
				sep = in.readChar();

				File temp = new File(filename);
				createIfNotExist(temp.getParentFile());

				out = new BinaryOut(filename);

				for(int i=0; i<filesize; i++) {
					out.write(in.readChar());
				}

				if (out != null) {
					out.close();
				}
			}

		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
}