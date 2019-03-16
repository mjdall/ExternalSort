package MakeRuns;

import Heap.Comparers.StringComparer;
import Heap.Heap;
import Heap.Comparer;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class MakeRuns {
    final static private String usage = "Usage: java MakeRuns <memory_size> <input_file> <output_file>";

    private static String getUsage () { return usage; }

    private static void printAndExit (String toPrint) {
        System.out.println(toPrint);
        System.exit(1);
    }

    private static int getRunSize (String arg) {
        int memorySize = 0;
        try { memorySize = Integer.parseInt(arg); }
        catch (NumberFormatException e) {
            printAndExit(String.format("could not parse `memory_size` argument `%s`", arg));
        }
        if (memorySize <= 0) { printAndExit("memory size has to be greater than zero"); }
        return  memorySize;
    }

    private static BufferedWriter openAndClearFile (File file) throws IOException {
        return Files.newBufferedWriter(Paths.get(file.toURI()), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    private static BufferedReader openReadFile(File file) throws IOException {
        return Files.newBufferedReader(Paths.get(file.toURI()));
    }

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

    private static String tryReadLine (BufferedReader input) {
        try {
            return input.readLine();
        } catch (java.io.IOException e) {
            printAndExit("rip");
            return null;
        }
    }

    private static void tryWriteLine (String toWrite, BufferedWriter output) {
        try {
            output.write(toWrite + "\n");
        } catch (java.io.IOException e) {
            printAndExit("rip");
        }
    }

    private static String[] readInInitial (int runSize, BufferedReader input) {
        String[] finalArray = new String[runSize];
        for (int i = 0; i < runSize; i++) {
            finalArray[i] = tryReadLine(input);
        }
        return finalArray;
    }

    private static void writeFinalQueue (Heap<String> priorityQueue, BufferedWriter oStream) {
        tryWriteLine("", oStream);
        priorityQueue.resetHeap();
        String popped;
        while ((popped = priorityQueue.remove()) != null) {
            tryWriteLine(popped, oStream);
        }
        tryWriteLine("", oStream);
        System.out.println("Complete");
        System.exit(0);
    }

    private static void runMakeRuns (int runSize, BufferedReader iStream, BufferedWriter oStream) {
        try {
            Comparer comparer = new StringComparer();
            String[] initialHeapArray = readInInitial(runSize, iStream);
            Heap<String> priorityQueue = new Heap<String>(initialHeapArray, comparer);

            String lastOut = null;
            while (iStream.ready() || priorityQueue.getHeapSize() > 0) {
                String top = priorityQueue.check();
                if(lastOut == null || comparer.compare(top, lastOut) >= 0) {
                    lastOut = top;
                    tryWriteLine(top, oStream);
                    if(iStream.ready())
                        priorityQueue.replace(tryReadLine(iStream));
                    else
                        priorityQueue.remove();
                } else {
                    priorityQueue.remove();
                    if(priorityQueue.getHeapSize() == 0 && iStream.ready()) {
                        lastOut = null;
                        tryWriteLine("", oStream);
                        priorityQueue.resetHeap();
                    }
                }
            }

            oStream.flush();
            iStream.close();
            oStream.close();
        } catch (IOException e) {
            System.out.println("Error occured while generating runs\n\n" + e.getMessage());
            System.exit(1);
        }
    }

    private static void popAll (Heap<String> heap) {
        String curr;
        while ((curr = heap.remove()) != null) {
            System.out.println(curr);
        }
        System.exit(1);
    }

    public static void main (String[] args) {
        if (args.length != 3) { printAndExit(getUsage()); }
        int runSize = getRunSize(args[0]);
        String inputFilename = args[1];
        String outputFilename = "./" + args[2];
        BufferedReader inStream = getInStream(inputFilename);
        BufferedWriter outStream = getOutStream(outputFilename);
        runMakeRuns(runSize, inStream, outStream);
    }
}
