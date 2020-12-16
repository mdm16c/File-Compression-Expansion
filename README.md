## File-Compression-Expansion
Java program to compress and expand files using Huffman, LZW, and Tar methods

## Motivation
This project was initially created to fulfill a semester project, but has been adapted to grade these projects instead as I am now the Teaching Assisstant for the class. Along with this, additional functionality has been added in order to keep it up to date with the class.

## Code style
[![js-standard-style](https://img.shields.io/badge/code%20style-standard-brightgreen.svg?style=flat)](https://github.com/feross/standard)

## Framework Used
[Apache Maven](https://maven.apache.org/)

## Features
This project allows the user to compress, expand, and archive any file type. The most notable feature is that the user can decide which compression method is used for files to better suit their needs as there are advantages to each algorithm.

## Installation
1. Be sure that you have Maven and Java installed locally
2. Dowload or clone the Repo into an empty directory
3. Compile all files in the src/main/java folder (Javac *.java)
4. Run the program using any of the commands shown below

## Code Example
To run a test class, use any of the following commands while in the src directory:
```
mvn test -Dtest=FileCompressionTestCHuffman
mvn test -Dtest=FileCompressionTestCLZW
mvn test -Dtest=FileCompressionTestB
mvn test -Dtest=FileCompressionTestAHuffman
mvn test -Dtest=FileCompressionTestALZW
```
To use Huffman compression use any of the following commands:
```
java SchubsH file1.txt
java SchubsH file1.txt file2.txt file3.txt ...
java SchubsH *.txt
```
To use LZW compression use any of the following commands:
```
java SchubsL file1.txt
java SchubsL file1.txt file2.txt file3.txt ...
java SchubsL *.txt
```
To create a Tar archive of Huffman compressed files use any of the following commands:
```
java SchubsArc <Name of a non-existing archive>.zh file1.txt
java SchubsArc <Name of a non-existing archive>.zh file1.txt file2.txt file3.txt ...
java SchubsArc <Name of a non-existing archive>.zh *.txt
```
To create a Tar archive of LZW compressed files use any of the following commands:
```
java SchubsArc <Name of a non-existing archive>.zl file1.txt
java SchubsArc <Name of a non-existing archive>.zl file1.txt file2.txt file3.txt ...
java SchubsArc <Name of a non-existing archive>.zl *.txt
```
To expand a compressed file or archive use any of the following commands:
```
java Deschubs file.hh
java Deschubs file.ll
java Deschubs file.zh
java Deschubs file.zl
```

## Tests
There are five different test classes in order to grade projects more efficiently.

-**FileCompressionTestCHuffman** tests the compression and expansion of files using the Huffman method.

-**FileCompressionTestCLZW** tests the compression and expansion of files using the LZW method.

-**FileCompressionTestB** tests the compression and expansion of files using the Huffman and LZW methods.

-**FileCompressionTestAHuffman** tests the compression and expansion of files using the Huffman and LZW methods along with the compression and expansion of Tar archives containing Huffman compressed files.

-**FileCompressionTestALZW** tests the compression and expansion of files using the Huffman and LZW methods along with the compression and expansion of Tar archives containing LZW compressed files.

MIT © [mdm16c](https://github.com/mdm16c)
