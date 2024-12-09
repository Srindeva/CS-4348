package project3;

import java.util.*;
import java.io.*;

public class Menu {
    private static final String MAGIC_NUMBER = "4337PRJ3";
    private static final int BLOCK_SIZE = 512;

    private RandomAccessFile idxFile;
    private String workingFile;
    private BTree bt;
    private boolean fileOpen; 

    public Menu() {
        this.idxFile = null;
        this.bt = null;
        this.workingFile = "";
        this.fileOpen = false;
    }

    public void start() throws IOException {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while(running) {
            this.display();
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "create":
                    this.handleCreate(scanner);
                    break;
                case "open":
                    this.handleOpen(scanner);
                    break;
                case "insert":
                    this.handleInsert(scanner);
                    break;
                case "search":
                    this.handleSearch(scanner);
                    break;
                case "print":
                    this.handlePrint();
                    break;
                case "extract":
                    this.handleExtract();
                    break;
                case "quit":    
                    running = false;
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }
        scanner.close();
    }

    public void handleCreate(Scanner scanner) throws IOException {
        System.out.print("Enter file path: ");
        String pathname = scanner.nextLine();
        this.workingFile = pathname;
        File file = new File(pathname);


        if(!file.exists()) {
            System.out.println("File exists: Overwrite (y/n)");
            if(!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                System.out.println("Overwrite aborted.");
            }
        }

        closeFile();
        this.idxFile = new RandomAccessFile(file, "rw");
        byte[] header = new byte[BLOCK_SIZE];
        System.arraycopy(MAGIC_NUMBER.getBytes(), 0, header, 0, MAGIC_NUMBER.length());
        bt = new BTree(idxFile);

        this.fileOpen = true;
        System.out.printf("Index file '%s' opened.\n", workingFile);
        
    }
    public void handleOpen(Scanner scanner) throws IOException {
        System.out.print("Enter file path: ");
        String pathname = scanner.nextLine();
        File file = new File(pathname);
        this.workingFile = pathname;

        if (!file.exists()) {
            System.err.println("File does not exist.");
            return;
        }

        closeFile();
        idxFile = new RandomAccessFile(file, "rw");
        byte[] magic = new byte[MAGIC_NUMBER.length()];
        idxFile.read(magic);
        if (!MAGIC_NUMBER.equals(new String(magic))) {
            System.err.println("Invalid file format.");
            closeFile();
            return;
        }
        
        bt = new BTree(idxFile);
        this.fileOpen = true;
        System.out.printf("Index file '%s' opened.\n", workingFile);
        
    }
    public void handleInsert(Scanner scanner) throws IOException {
        if(!isFileOpen())
            return;

        System.out.print("Enter a key: ");
        long key = Long.parseLong(scanner.nextLine().trim());
        System.out.print("Enter a value: ");
        long value = Long.parseLong(scanner.nextLine().trim());
        
        bt.insert(key, value);
    }
    public void handleSearch(Scanner scanner) {
        System.out.print("Enter a key: ");
        long key = Long.parseLong(scanner.nextLine().trim());
        bt.search(key);
    }
    public void handlePrint() {
        bt.printTree();
    }
    public void handleExtract() {
        
    }
    private void closeFile() throws IOException {
        if (idxFile != null) {
            idxFile.close();
            idxFile = null;
        }
        bt = null;
    }
    public boolean isFileOpen() {
        if(idxFile == null) {
            System.out.println("No file is currently open.");
            return false;
        }
        return true;
    }
    public void display() {
        String title;
        if(!fileOpen)
            title = "\n┏------------------------ MENU ------------------------┓\n";
        else 
            title = "\n┏------------------------ (" + workingFile + ") ------------------------┓\n";
        
        System.out.print(title);
        System.out.println("\tcreate  - Create a new index file");
        System.out.println("\topen    - Open an existing index file");
        System.out.println("\tinsert  - Insert a key-value pair");
        System.out.println("\tsearch  - Search for a key");
        System.out.println("\tprint   - Print all key-value pairs");
        System.out.println("\textract - Save key-value pairs to a file");
        System.out.println("\tquit    - Exit the program");
        
        String border = "┗";
        for(int i = 0; i < (title.length() - 4); i++) {
            border += "-";
        }
        border += "┛";
        System.out.println(border);
        System.out.print("Enter command: ");
    }
    public static void main(String args[]) {
        Menu menu = new Menu();
        try {
            menu.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}