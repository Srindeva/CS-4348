package project1;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class Logger {
   public static void main(String[] args) {
      if(args.length != 1)
         return;
 
   
   String fileName = args[0];
   File logFile = new File(fileName);
   BufferedWriter writer = null;
   
   try { 
      
      Scanner reader = new Scanner(System.in);
      writer = new BufferedWriter(new FileWriter(logFile, false)); 
       
       String logMessage;
       while (true) {
         logMessage = reader.nextLine().trim();
         if((logMessage.trim()).equals("QUIT")) {
            break;
         }
         
         String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
         String[] line = logMessage.split(" ", 2);
         String action = line[0];
         String message = (line.length > 1) ? line[1] : "";
         
         writer.write(String.format("%s [%s] %s%n", timeStamp, action, message));
         writer.flush();
      }
      writer.close();
      reader.close();
   } catch (IOException e) {
      e.printStackTrace();
   }
 }
}