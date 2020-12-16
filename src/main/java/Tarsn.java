/*
 * Software Engineering II
 * Fall 2019
 * Matthew McCracken
 *
 * Tars n amount of files
 * java Tarsn <archive name> <file> <file>...
 */
import java.io.IOException;
import java.io.File;

public class Tarsn {
	public static void main(String[] args) throws IOException {

		File in1 = null;
		BinaryIn bin1 = null;
		BinaryOut out = null;
		char separator = (char) 255; //all ones 11111111

		try {
			out = new BinaryOut(args[0]);
			for (int i = 1; i < args.length; i++) {
				//input files start at args[1] not args[0]
				in1 = new File(args[i]);

				if (!in1.exists() || !in1.isFile()) {
					return;
				}

				long filesize = in1.length();
				int filenamesize = args[i].length();

				//archive file is at args[0]
				//layout: file-name-length, separator, filename, file-size, file

				out.write(filenamesize);
				out.write(separator);

				out.write(args[i]);
				out.write(separator);

				out.write(filesize);
				out.write(separator);

				bin1 = new BinaryIn(args[i]);
				while (!bin1.isEmpty()) {
					char x = bin1.readChar();
					out.write(x);
				}
				if (bin1 != null) {
					bin1.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bin1 != null) {
				bin1.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
}