import java.io.*;

public class Encryption {
   private static String passkey = null;
   
   private static String encrypt(String text, String key) {
      StringBuilder cipher = new StringBuilder();
      
      for (int i = 0; i < text.length(); i++) {
         char letter = text.charAt(i);
         char shiftLetter = (char) (((letter + key.charAt(i % key.length())) - 2 * 'A') % 26);
         shiftLetter += 'A';
         cipher.append(shiftLetter);
      }
      return cipher.toString();
   }
   
   private static String decrypt(String cipher, String key) {
      StringBuilder text = new StringBuilder();
      for (int i = 0; i < cipher.length(); i++) {
         char letter = cipher.charAt(i);
         char shiftLetter = (char) ((letter - key.charAt(i % key.length()) + 26) % 26);
         shiftLetter += 'A';
         text.append(shiftLetter);
      }
      return text.toString();
   }
   public static void main(String[] args) {
   
   BufferedReader reader = null;
   BufferedWriter writer = null;
   
   try {
      reader = new BufferedReader(new InputStreamReader(System.in));
      writer = new BufferedWriter(new OutputStreamWriter(System.out)); 
      
      String command;
      while((command = reader.readLine()) != null) {
         String[] line = command.split(" ", 2);
         String action = (line[0]).toUpperCase();
         String argument = (line.length > 1) ? line[1] : "";
      
      switch(action) {
         case "PASSKEY":
            passkey = argument;
            writer.write("RESULT\n");
            break;
         case "ENCRYPT":
            if (passkey == null) {
               writer.write("ERROR Password not set.\n");
            } else {
               writer.write("RESULT " + encrypt(argument, passkey) + "\n");
            }
            break;
          case "DECRYPT":
            if (passkey == null) {
               writer.write("ERROR Password not set.\n");
            } else {
               writer.write("RESULT " + decrypt(argument, passkey) + "\n");
            }
            break;
          case "QUIT":
            return;
          default:
            writer.write("ERROR\n");
            break;
      }
      writer.flush();
    }
   } catch (Exception e) {
      e.printStackTrace();
   }
 }

}