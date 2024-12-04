package project3;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.util.Scanner;

public class IDXFile {
    private static final int BLOCK_SIZE = 512;
    private static final String MAGIC_NUMBER = "4337PRJ3";
    
    private RandomAccessFile file;
    private final String pathname;
    private static Scanner userInput;

    public IDXFile(String pathname, Scanner userInput, boolean createNew) throws IOException {
        this.pathname = pathname;
        this.userInput = userInput;
        try {
            if (createNew) {
                initializeFile();
            } else {

            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            this.file = null;
        }
    }    
    public void initializeFile() throws IOException {
        File workingFile = new File(pathname);
        Scanner scanner = new Scanner(System.in);

        if (workingFile.exists()) {
            // Prompt for overwrite if the file exists
            System.out.print("File already exists. Overwrite? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (!response.equals("yes")) {
                System.out.println("File creation aborted.");
                scanner.close();
                return;
            }
        }
        scanner.close();

        // Create or overwrite the file
        this.file = new RandomAccessFile(workingFile, "rw");
        file.setLength(0); // Truncate the file

        // Write the header
        file.seek(0);
        byte[] magicBytes = MAGIC_NUMBER.getBytes();
        file.write(magicBytes);

        // Write root node ID (0) and next block ID (1)
        file.writeLong(0); // Root node ID
        file.writeLong(1); // Next block ID

        // Fill the rest of the block with unused bytes
        byte[] remaining = new byte[BLOCK_SIZE - 24];
        file.write(remaining);

        System.out.println("File '" + pathname + "' created successfully.");
    }    

    public void checkMagicNumber() throws IOException{
        file.seek(0);
        byte[] magicBytes = new byte[8];
        file.read(magicBytes);
        String magicString = new String(magicBytes);
        if(!MAGIC_NUMBER.equals(magicString)) 
            throw new IOException("Invalid file: Magic number does not match.");
    }
}

