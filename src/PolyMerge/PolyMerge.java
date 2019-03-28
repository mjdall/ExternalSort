/**
 * Polyphase Sort Merge implementation
 *
 * Daniel Stokes - 1331134
 * Morgan Dally - ???????
 */

package PolyMerge;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import java.nio.file.StandardOpenOption;

import Heap.Heap;

class PolyMerge {

    /**
     * Creates temporary files for use during polyphase merge sort
     * @param number Number of temporary files to create
     * @return Pair of lists containing file names along with readers for the files
     */
    private static List<File> getTemporaryOutputFiles(int number) {
        // Create lists
        List<File> files = new ArrayList<>();
        List<BufferedReader> readers = new ArrayList<>();
        // Loop for number
        for(int i = 0; i < number; i++) {
            try {
                File file = new File("./PolyMerge_tmp_" + i + ".txt");
                // Create file and clear contents
                openAndClearFile(file);
                // Set the file to delete itself when program exits
                file.deleteOnExit();
                // Add to lists
                files.add(file);
            } catch (IOException e) {
                System.err.println("Could not create temporary output files for writing");
                System.exit(1);
            }
        }
        // Return
        return files;
    }

    /**
     * Open file, clearing the content if it exists
     * @param file The file to open
     * @return Writer that points at the file
     * @throws IOException
     */
    private static BufferedWriter openAndClearFile(File file) throws IOException {
        return Files.newBufferedWriter(Paths.get(file.toURI()), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE );
    }

    /**
     * Opens file, appending to the end if it exists
     * @param file The file to append to
     * @return Writer that points to the end of the file for writing
     * @throws IOException
     */
    private static BufferedWriter openAppendFile(File file) throws IOException {
        return Files.newBufferedWriter(Paths.get(file.toURI()), StandardOpenOption.APPEND, StandardOpenOption.WRITE, StandardOpenOption.CREATE );
    }

    /**
     * Opens a file for reading
     * @param file File to open for reading
     * @return BufferedReader for file
     * @throws IOException
     */
    private static BufferedReader openReadFile(File file) throws IOException {
        return Files.newBufferedReader(Paths.get(file.toURI()));
    }

    private static String inputFile = "MakeRunsOutput.txt";

    /**
     * Opens the input file for reading
     * @return BufferedReader for reading input file with
     */
    private static BufferedReader getInputFile() {
        File file = new File(inputFile);

        if(!file.exists()) {
            // Didn't find matching file
            System.err.println("Could not find input runs to read.\nDid you run from the correct directory?");
            System.exit(1);
        }

        try {
            // Open file for reading
            return openReadFile(file);
        } catch (IOException e) {
            System.err.println("Could not open input file for reading.");
            System.exit(1);
        }

        return null;
    }

    private static int getNumRuns() throws IOException {
        // Open input file
        BufferedReader inputFile = getInputFile();

        int runs = 0;
        boolean newRun = true;
        // Count the number of runs in the file so we can distribute correctly
        while(inputFile.ready()) {
            newRun = false;
            // Read next line
            String line = inputFile.readLine();
            // Empty line means end of run
            if(line.length() == 0) {
                runs++;
                newRun = true;
            }
        }
        if(!newRun) {
            // File didn't have newline at end
            // Add one for final run
            runs++;
        }

        inputFile.close();

        return runs;
    }

    /**
     * Calculates the distribution for the runs across the temporary files
     * @param numFiles Number of temporary files used
     * @param numRuns Number of runs to distribute
     * @return The distribution that provides optimal performance for the given number of runs
     */
    private static int[] calcDistribution(int numFiles, int numRuns) {
        int inputFiles = numFiles - 1;
        int[] state = new int[inputFiles];

        int sum = inputFiles;
        // Init to ones
        for(int i = 0; i < inputFiles; i++){
            state[ i ] = 1;
        }

        // Generates perfect ditribution of runs
        // See: http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.441.2825&rep=rep1&type=pdf
        while ( sum <= numRuns ) {
            // Get largest value. This will be the smallest value on the prior iteration
            int largest = state[ inputFiles - 1 ];
            sum = largest;
            // Loop for all files except the first
            for(int i = inputFiles - 1; i >= 1; i--) {
                // Calculate the next number for ith input
                state[i] = state[i-1] + largest;
                // Increase sum
                sum += state[i];
            }
            // Set state
            state[0] = largest;
        }

        // Sort array so largest is second to last
        Arrays.sort(state);
        return state;
    }

    /**
     * Loads the input runs into the temporary files in the optimal distribution, leaving the last file empty
     * @param outputFiles The list of temporary files to output to
     */
    private static void loadInputFiles(List<File> outputFiles, int runs) {
        try {
            // Get input file
            BufferedReader inputFile = getInputFile();

            // Check input file is not null
            if(inputFile == null) {
                System.err.println("Could not open input file for reading.");
                System.exit(1);
            }

            if(runs == -1) {
                // Calculate number of runs
                runs = getNumRuns();
                System.out.println("For " + runs + " runs");
            }

            // Get distribution of runs
            int[] distribution = calcDistribution(outputFiles.size(), runs);

            // Start outputting to the first file
            int outputIndex = 0;
            // Get the first temp file
            BufferedWriter writer = openAppendFile(outputFiles.get(outputIndex));
            // Loop while there is still data
            while(inputFile.ready()) {
                // Read next line
                String line = inputFile.readLine();
                // Write line to output file
                writer.write(line + "\n");
                // Empty line means end of run
                // Decrement number of runs needed to distribute
                if(line.length() == 0 && --distribution[outputIndex] == 0) {
                    // Close writer
                    writer.close();
                    // Open next writer
                    writer = openAppendFile(outputFiles.get(++outputIndex));
                }
            }

            // Fill out remaining distributions with empty runs
            while (outputIndex < outputFiles.size() - 1) {
                // Write empty run
                writer.write("\n");
                // Check if enough runs have been processed
                if(--distribution[outputIndex] == 0) {
                    // Close writer
                    writer.close();
                    // Open next writer
                    writer = openAppendFile(outputFiles.get(++outputIndex));
                }
            }

            // Close final writer
            writer.close();
            // Close input file reader
            inputFile.close();

        } catch (IOException e) {
            System.err.println("Error while loading input files.\n\n" + e.getMessage());
            System.exit(1);
        }
    }

    private static class HeapEntry {
        private String string;
        private int fileIndex;

        public String getString() {
            return string;
        }

        public int getFileIndex() {
            return fileIndex;
        }

        public HeapEntry(String _string, int _fileIndex) {
            string = _string;
            fileIndex = _fileIndex;
        }
    }

    /**
     * Loads first entry of next run into memory
     * @param files The temporary files we are using
     * @param inputReaders The input readers for current input arrays
     * @param lastEntryHeap The heap containing the entries
     * @param outputIndex The current output index
     * @throws IOException
     */
    private static void loadNewRun(List<File> files, BufferedReader[] inputReaders, Heap<HeapEntry> lastEntryHeap, int outputIndex) throws IOException {
        if(lastEntryHeap.getHeapSize() != 0 ) {
            System.err.println("Unprocessed items were about to be overwritten");
            System.exit(1);
        }
        for(int i = 0; i < files.size(); i++) {
            if(i == outputIndex) {
                // Close old input if changed
                if(inputReaders[i] != null) {
                    inputReaders[i].close();
                }
                inputReaders[i] = null;
            } else {
                if(inputReaders[i] == null) {
                    // Open file if null
                    inputReaders[i] = openReadFile(files.get(i));
                }
                // Read next entry
                String line = inputReaders[i].readLine();
                if(line.length() > 0) {
                    lastEntryHeap.insert(new HeapEntry(line, i));
                }
            }
        }
    }

    /**
     * Runs the polyphase merge on the specified files
     * @param files The temporary working files
     */
    private static void runPolyphaseMerge(List<File> files) {
        // Last file is first output file
        int numFiles = files.size();
        int outputIndex = numFiles - 1;
        int numIterations = 0;
        Heap<HeapEntry> lastEntryHeap = new Heap<>(new HeapEntry[numFiles], Comparator.comparing(HeapEntry::getString));
        BufferedReader[] inputReaders = new BufferedReader[numFiles];
        try {
            // Get next output run
            // The perfect distribution calculation means that this will always be the next file along
            int nextOutput = (outputIndex + 1) % numFiles;
            // Loop while there are runs remaining to process
            do {
                // Load first run
                loadNewRun(files, inputReaders, lastEntryHeap, outputIndex);
                // Open new file for outputting
                BufferedWriter output = openAndClearFile(files.get(outputIndex));

                // Loop until next file runs out of runs
                while (true) {
                    if(lastEntryHeap.getHeapSize() == 0) {
                        // All files have reached the end of their runs
                        // Write new line to signify run end in output file
                        output.write("\n");
                        if(!inputReaders[nextOutput].ready()) {
                            // Completed all runs for this iteration
                            // Switch output files
                            break;
                        }
                        // Process next run
                        loadNewRun(files, inputReaders, lastEntryHeap, outputIndex);
                    } else {
                        // Load next element in the run
                        HeapEntry smallest = lastEntryHeap.peek();
                        output.write(smallest.getString() + "\n");
                        String nextLine = inputReaders[smallest.getFileIndex()].readLine();
                        if(nextLine.length() == 0) {
                            // Remove element from heap
                            lastEntryHeap.remove(false);
                        } else {
                            // Replace element with nex line from file
                            lastEntryHeap.replace(new HeapEntry(nextLine, smallest.getFileIndex()));
                        }
                    }
                }

                // Number of iterations it took to complete read
                numIterations++;
                outputIndex = nextOutput;
                nextOutput = (outputIndex + 1) % numFiles;
                output.close();
            } while (inputReaders[nextOutput].ready());

            // Merged all runs
            // Get output file
            outputIndex = ( outputIndex + numFiles - 1 ) % numFiles;
            BufferedReader reader = openReadFile(files.get(outputIndex));
            while (reader.ready()) {
                String line = reader.readLine();
                if(line.length() != 0) {
                    System.out.println(line.substring(1));
                }
            }
//            System.out.println("Required: " + numIterations + " polyphase merge iterations to sort output");
            System.err.println(numIterations);
        } catch (IOException e) {
            System.err.println("Error while running polyphase merge.\n\n" + e.getMessage());
            System.exit(1);
        }
    }

    public static void main (String[] args) {
        // Check input args
        if(args.length != 3) {
            System.err.println("Usage: java PolyMerge <number of files> <number of runs> <input file>\n" +
                    "\tNumber of files: The number of temporary files to use\n" +
                    "\tNumber of runs: The number of runs in the input file. -1 if this should be calculated internally\n" +
                    "\tInput file: The input file to use as the input runs");
            System.exit(1);
        }

        int numFiles = 0;
        try {
            // Get number of files to use
            numFiles = Integer.parseInt(args[0]);
        }catch (NumberFormatException e) {
            System.err.println("Number of files must be a valid integer");
            System.exit(1);
        }

        // Check there are sufficient files
        if(numFiles <= 2) {
            System.err.println("Number of files must be greater than 2");
            System.exit(1);
        }

        int numRuns = 0;
        try {
            // Get number of files to use
            numRuns = Integer.parseInt(args[1]);
        }catch (NumberFormatException e) {
            System.err.println("Number of runs must be a valid integer");
            System.exit(1);
        }

        if(numRuns < 1 && numRuns != -1) {
            System.err.println("Number of runs must be >=1 or -1");
            System.exit(1);
        }

        inputFile = args[2];

        // Create temporary files
        List<File> files = getTemporaryOutputFiles( numFiles );
        // Load input into output files
        loadInputFiles( files, numRuns );

        // Merge the files
        runPolyphaseMerge(files);
    }
}