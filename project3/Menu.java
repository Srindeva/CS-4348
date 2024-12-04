package project3;

import java.util.*;
import java.io.*;

public class Menu {
    private IDXFile idxFile;
    
    public Menu() {
    }

    public void start() {
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
                    this.handleOpen();
                case "insert":
                    this.handleInsert();
                case "search":
                    this.handleSearch();
                case "print":
                    this.handlePrint();
                case "extract":
                    this.handleExtract();
                case "quit":    
                    running = false;
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }
        scanner.close();
    }

    public void handleCreate(Scanner scanner) {
        System.out.println("Enter file path: ");
        String pathname = scanner.nextLine();
        
        try {
            this.idxFile = new IDXFile(pathname, scanner, true);
            System.out.println("Index file opened.");
        } catch (IOException e) {
            System.out.println("Error opening file: " + e.getMessage());
        }

    }
    public void handleOpen() {

    }
    public void handleInsert() {

    }
    public void handleSearch() {
        
    }
    public void handlePrint() {
        
    }
    public void handleExtract() {
        
    }
    public void display() {
        System.out.println("\n┏------------------ MENU ------------------┓");
        System.out.println("  create  - Create a new index file");
        System.out.println("  open    - Open an existing index file");
        System.out.println("  insert  - Insert a key-value pair");
        System.out.println("  search  - Search for a key");
        System.out.println("  print   - Print all key-value pairs");
        System.out.println("  extract - Save key-value pairs to a file");
        System.out.println("  quit    - Exit the program");
        System.out.println("┗------------------------------------------┛");
        System.out.print("Enter command: ");
    }
    public static void main(String args[]) {
        Menu menu = new Menu();
        menu.start();
    }
}