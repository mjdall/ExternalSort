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
            String line;
            /*
            read in a line, check it's not null,
            if not null, remove multiple whitespaces
             */
            while (((line = input.readLine()) != null)
                     && ((line = line.trim().replaceAll(" +", " ")) != "")
                     && ("".equals(line)))
            if (line == null) { return null; }
            return line;
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
        Comparer comparer = new StringComparer();
        String[] initialHeapArray = readInInitial(runSize, iStream);
        Heap<String> priorityQueue = new Heap<String>(initialHeapArray, comparer);

        String nextReplacement = tryReadLine(iStream);
        String previous = priorityQueue.replace(nextReplacement);
        String checkNext = priorityQueue.check();
        while (true) {
            int runCount = 0;
            // while the previous had more priority than the next in the queue
            runLoop:
            while (comparer.compare(previous, checkNext) <= 0) {
                // get the item that had less priority and write it
                previous = priorityQueue.replace(nextReplacement);
                tryWriteLine(previous, oStream);

                // get the next item in the queue, this will never be null as we used replace before this
                checkNext = priorityQueue.check();

                // while the item we just found has more priority than the previous written item
                while (comparer.compare(previous, checkNext) < 0) {
                    priorityQueue.remove();
                    checkNext = priorityQueue.check();

                    // the queue has no more items in it
                    if (checkNext == null) { break runLoop; }
                }

                // actually remove the item we were checking from the queue and replace it
                priorityQueue.replace(nextReplacement);

                // read in the next replacement line, if null, the stream has ended
                if ((nextReplacement = tryReadLine(iStream)) == null) { writeFinalQueue(priorityQueue, oStream); }
                runCount++;
            }
            //System.out.println("prev: " + previous.length() + " next: " + checkNext.length());
//            System.out.println("run size: " + runCount);

            // end of run - write new line to file
            tryWriteLine("", oStream);

            // reset the queue
            priorityQueue.resetHeap();

            // set our starting variables back up
            previous = priorityQueue.replace(nextReplacement);
            if ((nextReplacement = tryReadLine(iStream)) == null) { writeFinalQueue(priorityQueue, oStream); }
            checkNext = priorityQueue.check();
            tryWriteLine(previous, oStream);
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
