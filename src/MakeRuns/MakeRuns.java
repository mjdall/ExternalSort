package MakeRuns;

import Heap.Comparers.StringComparer;
import Heap.Heap;
import Heap.Comparer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class MakeRuns {
    final static private String usage = "Usage: java MakeRuns <memory_size> <input_file> <output_file>";

    /**
     * Get usage string
     * @return Usage string
     */
    private static String getUsage () { return usage; }

    /**
     * Print error and exit
     * @param toPrint Error message to print
     */
    private static void printAndExit (String toPrint) {
        System.out.println(toPrint);
        System.exit(1);
    }

    /**
     * Get the size of the heap to use from the command line argument
     * @param arg The argument to parse
     * @return The parsed value
     */
    private static int getHeapSize(String arg) {
        int memorySize = 0;
        try { memorySize = Integer.parseInt(arg); }
        catch (NumberFormatException e) {
            printAndExit(String.format("Could not parse `memory_size` argument `%s`", arg));
        }
        if (memorySize <= 0) { printAndExit("Memory size has to be greater than zero"); }
        return  memorySize;
    }

    /**
     * Open and clear the contents of the file
     * @param file File to open
     * @return BufferedWriter to the file
     * @throws IOException
     */
    private static BufferedWriter openAndClearFile (File file) throws IOException {
        return Files.newBufferedWriter(Paths.get(file.toURI()), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    /**
     * Opens file for reading
     * @param file The file to open for reading
     * @return BufferedReader for the file
     * @throws IOException
     */
    private static BufferedReader openReadFile(File file) throws IOException {
        return Files.newBufferedReader(Paths.get(file.toURI()));
    }

    /**
     * Get output stream for specified file
     * @param outputFilename Path to the file
     * @return BufferedWriter for the file
     */
    private static BufferedWriter getOutStream (String outputFilename) {
        try {
            File outFile = new File(outputFilename);
            return openAndClearFile(outFile);
        } catch (java.io.IOException e) {
            printAndExit(String.format("Couldn't create output file `%s`", outputFilename));
            // won't actually hit this but linter
            return null;
        }
    }

    /**
     * Get input stream for specified file
     * @param inputFilename Path to the file
     * @return BufferedReader for the file
     */
    private static BufferedReader getInStream (String inputFilename) {
        try {
            File inFile = new File(inputFilename);
            return openReadFile(inFile);
        } catch (java.io.IOException e) {
            printAndExit(String.format("Couldn't get input file `%s`", inputFilename));
            // won't actually hit this but linter
            return null;
        }
    }

    /**
     * Read line from input
     * @param input BufferedReader to read with
     * @return Line, or null if input is finished
     */
    private static String tryReadLine (BufferedReader input) {
        try {
            return input.readLine();
        } catch (java.io.IOException e) {
            printAndExit("Error reading from file\n\n" + e.getMessage());
            return null;
        }
    }

    /**
     * Write line to output
     * @param toWrite Line to write
     * @param output BufferedWriter to write with
     */
    private static void tryWriteLine (String toWrite, BufferedWriter output) {
        try {
            output.write(toWrite + "\n");
        } catch (java.io.IOException e) {
            printAndExit("Error writing to file\n\n" + e.getMessage());
        }
    }

    /**
     * Read in the initial runs into the heap
     * @param runSize The size of the heap
     * @param input The input stream to read from
     * @return Array containing the elements
     */
    private static String[] readInInitial (int runSize, BufferedReader input) {
        String[] finalArray = new String[runSize];
        for (int i = 0; i < runSize; i++) {
            finalArray[i] = tryReadLine(input);
        }
        return finalArray;
    }

    /**
     * Runs the replacement selection strategy algorithm to generate runs
     * @param runSize Size of the heap to use
     * @param iStream BufferedReader to get input from
     * @param oStream BufferedWriter to write runs to
     */
    private static void runMakeRuns (int runSize, BufferedReader iStream, BufferedWriter oStream) {
        try {
            // Read in initial values and create heap
            Comparer comparer = new StringComparer();
            String[] initialHeapArray = readInInitial(runSize, iStream);
            Heap<String> priorityQueue = new Heap<String>(initialHeapArray, comparer);

            String lastOut = null;
            // Loop while there are values to process
            while (iStream.ready() || priorityQueue.getHeapSize() > 0) {
                // Get next value
                String top = priorityQueue.peek();
                // Check if value can be written to output stream
                if(lastOut == null || comparer.compare(top, lastOut) >= 0) {
                    // Write value out
                    lastOut = top;
                    tryWriteLine(top, oStream);
                    // Replace written element with next
                    if(iStream.ready())
                        priorityQueue.replace(tryReadLine(iStream));
                    // No next element - remove from the heap permanantly
                    else
                        priorityQueue.remove(false);
                } else {
                    // Remove from the heap, saving the value at the end of the array
                    priorityQueue.remove(true);
                    // Check if we have processed all the elements
                    if(priorityQueue.getHeapSize() == 0) {
                        // Start next run
                        lastOut = null;
                        tryWriteLine("", oStream);
                        // Reset the heap
                        priorityQueue.resetHeap();
                    }
                }
            }

            // Close files
            oStream.flush();
            iStream.close();
            oStream.close();
        } catch (IOException e) {
            System.out.println("Error occured while generating runs\n\n" + e.getMessage());
            System.exit(1);
        }
    }

    public static void main (String[] args) {
        // Check args
        if (args.length != 3) { printAndExit(getUsage()); }
        // Get size of heap to use for runs
        int runSize = getHeapSize(args[0]);
        // Get input/output files
        String inputFilename = args[1];
        String outputFilename = "./" + args[2];
        // Open files for reading and writing
        BufferedReader inStream = getInStream(inputFilename);
        BufferedWriter outStream = getOutStream(outputFilename);
        // Run replacement selection algorithm to generate runs
        runMakeRuns(runSize, inStream, outStream);
    }
}
