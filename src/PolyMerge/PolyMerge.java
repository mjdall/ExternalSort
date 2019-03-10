package PolyMerge;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.nio.file.StandardOpenOption;

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

    /**
     * Opens the input file for reading
     * @return BufferedReader for reading input file with
     */
    private static BufferedReader getInputFile() {
        File file = new File("MakeRunsOutput.txt");

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
            if(line.equals("")) {
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
    private static int loadInputFiles(List<File> outputFiles) {
        try {
            // Get input file
            BufferedReader inputFile = getInputFile();

            // Check input file is not null
            if(inputFile == null) {
                System.err.println("Could not open input file for reading.");
                System.exit(1);
            }

            // Calculate number of runs
            int runs = getNumRuns();

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
                if(line.equals("") && --distribution[outputIndex] == 0) {
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

            return runs;
        } catch (IOException e) {
            System.err.println("Error while loading input files.\n\n" + e.getMessage());
            System.exit(1);
            return -1;
        }
    }

    private static int runMergeIteration(List<File> allFiles, List<File> activeFiles, int runs) {



        return runs;
    }

    public static void main (String[] args) {
        if(args.length != 1) {
            System.err.println("Usage: java PolyMerge <number of files>");
            System.exit(1);
        }

        int numFiles = 0;
        try {
            numFiles = Integer.parseInt(args[0]);
        }catch (NumberFormatException e) {
            System.err.println("Argument must be a valid integer");
            System.exit(1);
        }

        if(numFiles <= 2) {
            System.err.println("Argument must be greater than 2");
            System.exit(1);
        }

        // Create temporary files
        List<File> files = getTemporaryOutputFiles( numFiles );
        // Load input into output files
        int runs = loadInputFiles( files );

        // List of currently active files
        List<File> activeFiles = files.subList(0, files.size() - 1);
//        while( runs > 0 ) {
//            runs = runMergeIteration( files, activeFiles, runs );
//        }
    }
}