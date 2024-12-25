import java.util.*;
import java.io.*;
import java.util.regex.*;

public class Driver {
   private static Process logger;
   private static InputStream loggerInput;
   private static OutputStream loggerOutput;
   private static Scanner fromLogger;
   private static PrintStream toLogger;
   
   private static Process encryption;
   private static InputStream encryptionInput;
   private static OutputStream encryptionOutput;
   private static Scanner fromEncryption;
   private static PrintStream toEncryption;
   
   private static List<String> history = new ArrayList<>();
 
   private static void handlePassword(Scanner userInput) {
      String passkey;
      if(promptHistory(userInput)) {
         passkey = useHistory(userInput);
      } else {
         System.out.print("Enter password: "); 
         passkey = userInput.nextLine().trim();
      }
      toEncryption.println("PASSKEY " + passkey);
      toEncryption.flush();
      toLogger.println("PASSWORD Set passkey.");
      toLogger.flush();
      handleResult();
   }
   
   private static void handleEncrypt(Scanner userInput) {
      String text;
      if (promptHistory(userInput)) {
         text = useHistory(userInput);
      } else {
         System.out.print("Enter text to encrypt: "); 
         text = userInput.nextLine().trim();
      }
      toEncryption.println("ENCRYPT " + text);
      toEncryption.flush();
      toLogger.println("ENCRYPT " + text);
      toLogger.flush();
      handleResult();
   }
   
   private static void handleDecrypt(Scanner userInput) {
      String text;
      if (promptHistory(userInput)) {
         text = useHistory(userInput);
      } else {
         System.out.print("Enter text to decrypt: "); 
         text = userInput.nextLine().trim();
      }
      toEncryption.println("DECRYPT " + text);
      toEncryption.flush();
      toLogger.println("DECRYPT " + text);
      toLogger.flush();
      handleResult();
   }
   
   private static void handleResult() {
      String result = fromEncryption.nextLine();
      System.out.println(result);
      toLogger.println(result);
      toLogger.flush();
   }
   
   private static boolean promptHistory(Scanner userInput) {
      System.out.print("Use history? (Y/N) ");
      String selection = userInput.nextLine().trim();
      return ((selection.equalsIgnoreCase("Y")) ? true : false);
   }
   
   private static void displayHistory() {
      System.out.println("------------------HISTORY---------------------");
      for(int i = 0; i < history.size(); i++) {
         System.out.printf("[%d] %s\n", i, history.get(i));
      }
      System.out.println("----------------------------------------------");
   }
   
   private static void updateHistory(String fileName) {
        history.clear();
        try (RandomAccessFile logFile = new RandomAccessFile(fileName, "r")) {
            logFile.seek(0);
            String line;
            while ((line = logFile.readLine()) != null) {
                history.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } 
   }
   
   private static String useHistory(Scanner userInput) {
      displayHistory();
      if(history.size() == 0) 
            System.out.println("History is empty.");
         System.out.print("Enter the index from history: ");
         int index = Integer.parseInt(userInput.nextLine().trim());
         if(index > history.size())
            return "";
         String line = history.get(index);
         String[] words = line.split(" ");
         String argument = "";
         for(int i = 3; i < words.length; i++) {
            argument += words[i];
         }
         return argument;
   }
   
   public static void main(String[] args) {
   
      if(args.length != 1) {
         System.out.println("Log file not specified.");
         return;
      }  
      String fileName = args[0];
      File file = new File(fileName);
      try {
         logger = Runtime.getRuntime().exec("java Logger " + fileName);
         loggerInput = logger.getInputStream();
         loggerOutput = logger.getOutputStream();
         fromLogger = new Scanner(loggerInput);
         toLogger = new PrintStream(loggerOutput);
         
         
         encryption = Runtime.getRuntime().exec("java Encryption");
         encryptionInput = encryption.getInputStream();
         encryptionOutput = encryption.getOutputStream();
         fromEncryption = new Scanner(encryptionInput);
         toEncryption = new PrintStream(encryptionOutput);

         toLogger.println("START Driver Started.");
         toLogger.flush();
         
         Scanner userInput = new Scanner(System.in);
         String command = "";
         
         history = new ArrayList<>();
         history.clear();
         boolean exit = false;
         
         while (!exit) {
            updateHistory(fileName);
            System.out.println("-------------------MENU-----------------------");
            System.out.println("password - set a password for the cipher");
            System.out.println("encrypt - encrypt text with a vigenere cipher");
            System.out.println("decrypt - decrypt using the passkey");
            System.out.println("history - view executed commands");
            System.out.println("quit - exit the program");
            System.out.println("----------------------------------------------");
            System.out.print("Choose a command: ");
            
            command = userInput.nextLine().trim().toLowerCase();
            switch(command) {
               case "password":
                  handlePassword(userInput);
                  break;
               case "encrypt":
                  handleEncrypt(userInput);
                  break;
               case "decrypt":
                  handleDecrypt(userInput);
                  break;
               case "history":
                  updateHistory(fileName);
                  displayHistory();
                  break;
               case "quit":
                  toEncryption.println("QUIT");
                  toEncryption.flush();

                  toLogger.println("END Driver Exited.");
                  toLogger.flush();

                  toLogger.println("QUIT");
                  toLogger.flush();
               
                  encryption.waitFor();
                  logger.waitFor();
                  updateHistory(fileName);
                  history.clear();
                  exit = true;
                  break;
               default:
                  System.out.println("---------------------------");
                  break;
            }
         }
         
      } catch (IOException | InterruptedException e) {
         e.printStackTrace();
      }
   }
}