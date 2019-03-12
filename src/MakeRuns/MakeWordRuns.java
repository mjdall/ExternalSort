//package MakeRuns;
//
//import Heap.Comparers.StringComparer;
//import Heap.Heap;
//import Heap.Comparer;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.StandardOpenOption;
//import java.util.Arrays;
//
//public class MakeWordRuns {
//    final static private String usage = "Usage: java MakeRuns <memory_size> <input_file> <output_file>";
//
//    private static String getUsage () { return usage; }
//
//    private static void printAndExit (String toPrint) {
//        System.out.println(toPrint);
//        System.exit(1);
//    }
//
//    private static int getRunSize (String arg) {
//        int memorySize = 0;
//        try { memorySize = Integer.parseInt(arg); }
//        catch (NumberFormatException e) {
//            printAndExit(String.format("could not parse `memory_size` argument `%s`", arg));
//        }
//        if (memorySize <= 0) { printAndExit("memory size has to be greater than zero"); }
//        return  memorySize;
//    }
//
//    public static <T> T[] concat(T[] first, T[] second) {
//        T[] result = Arrays.copyOf(first, first.length + second.length);
//        System.arraycopy(second, 0, result, first.length, second.length);
//        return result;
//    }
//
//    private static BufferedWriter openAndClearFile (File file) throws IOException {
//        return Files.newBufferedWriter(Paths.get(file.toURI()), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
//    }
//
//    private static BufferedReader openReadFile(File file) throws IOException {
//        return Files.newBufferedReader(Paths.get(file.toURI()));
//    }
//
//    private static BufferedWriter getOutStream (String outputFilename) {
//        try {
//            File outFile = new File(outputFilename);
//            return openAndClearFile(outFile);
//        } catch (java.io.IOException e) {
//            printAndExit(String.format("Couldn't create output file `%s`", outputFilename));
//            // won't actually hit this but linter
//            return null;
//        }
//    }
//
//    private static BufferedReader getInStream (String inputFilename) {
//        try {
//            File inFile = new File(inputFilename);
//            return openReadFile(inFile);
//        } catch (java.io.IOException e) {
//            printAndExit(String.format("Couldn't get input file `%s`", inputFilename));
//            // won't actually hit this but linter
//            return null;
//        }
//    }
//
//    private static String[] tryReadLine (BufferedReader input) {
//        try {
//            String line;
//            while (((line = input.readLine()) != null) && ("".equals(line))) { }
//            if (line == null) { return null; }
//            return line.split("\\s+");
//        } catch (java.io.IOException e) {
//            printAndExit("rip");
//            return null;
//        }
//    }
//
//    private static void tryWriteLine (String toWrite, BufferedWriter output) {
//        try {
//            output.write(toWrite + "\n");
//        } catch (java.io.IOException e) {
//            printAndExit("rip");
//        }
//    }
//
//    private static String[] readInInitial (int runSize, BufferedReader input) {
//        int readInWords = 0;
//        String[] finalArray = new String[0];
//        while (readInWords < runSize) {
//            String[] readIn = tryReadLine(input);
//            finalArray = concat(finalArray, readIn);
//            readInWords += readIn.length;
//        }
//        return finalArray;
//    }
//
//    // cuurently debugging this method
//    // i think hide + resetting isn't working properly
//    // the method is stuck in a while loop forever atm
//    private static void doRun (String[] readFrom, Heap<String> priorityQueue, BufferedWriter oStream, Comparer comparer) {
//        String previous = priorityQueue.remove();
//        tryWriteLine(previous, oStream);
//        String writeString = priorityQueue.check();
//        if (writeString == null) {
//            for (int i = 0; i < readFrom.length; i++) {
//                System.out.println(readFrom[i]);
//            }
//            String[] heap = priorityQueue.getHeap();
//            System.out.println(heap.length + " " + heap[heap.length - 1]);
////            for (int i = 0; i < heap.length; i++) {
////                System.out.println(i + " " + heap[i]);
////            }
//        }
//        int readIndex = 0;
//        while (readIndex < readFrom.length) {
//            if (comparer.compare(previous, writeString) > 0) {
//                priorityQueue.hide();
//            } else {
//                tryWriteLine(writeString, oStream);
//                priorityQueue.replace(readFrom[readIndex]);
//                previous = writeString;
//                readIndex++;
//            }
//            writeString = priorityQueue.check();
//            if (writeString == null) {
//                tryWriteLine("", oStream);
//                priorityQueue.resetHeap();
//                previous = priorityQueue.remove();
//                tryWriteLine(previous, oStream);
//                writeString = priorityQueue.check();
//            }
//        }
//    }
//
//    private static void runMakeRuns (int runSize, BufferedReader iStream, BufferedWriter oStream) {
//        Comparer comparer = new StringComparer();
//        String[] replacementArray = readInInitial(runSize, iStream);
//        String[] initialHeapArray = Arrays.copyOfRange(replacementArray, 0, runSize);
//        replacementArray = Arrays.copyOfRange(replacementArray, runSize, replacementArray.length);
//        Heap<String> priorityQueue = new Heap<String>(initialHeapArray, comparer);
//        doRun(replacementArray, priorityQueue, oStream, comparer);
//        int count = 0;
//        while ((replacementArray = tryReadLine(iStream)) != null) {
//            doRun(replacementArray, priorityQueue, oStream, comparer);
//            count++;
////             System.out.println(count);
//        }
//    }
//
//    private static void popAll (Heap<String> heap) {
//        String curr;
//        while ((curr = heap.remove()) != null) {
//            System.out.println(curr);
//        }
//        System.exit(1);
//    }
//
//    public static void main (String[] args) {
//        if (args.length != 3) { printAndExit(getUsage()); }
//        int runSize = getRunSize(args[0]);
//        String inputFilename = args[1];
//        String outputFilename = "./" + args[2];
//        BufferedReader inStream = getInStream(inputFilename);
//        BufferedWriter outStream = getOutStream(outputFilename);
//        runMakeRuns(runSize, inStream, outStream);
//    }
//}
