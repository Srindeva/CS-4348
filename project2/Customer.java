// import java.util.Random;
// 
// public class Customer extends Thread {
//     private final int id;
//     private final String transaction;
//     private int handledBy;
//     
//     public Customer(int id) {
//         this.id = id;
//         this.transaction = new Random().nextBoolean() ? "deposit" : "withdrawal";
//     }
// 
//     public int getID() {
//         return id;
//     }
// 
//     public String getTransaction() {
//         return transaction;
//     }
//     
//     public int getHandledBy() {
//       return handledBy;
//     }
//     
//     public void setHandledBy(int tellerID) {
//       this.handledBy = tellerID; 
//     }
//     
//     @Override
//     public void run() {
//         Bank.print("Customer " + id + " decides to make a " + transaction + " transaction.");
//         
//         try {
//             Bank.bankOpen.acquire();  
//             Bank.print("Customer " + id + " is going to the bank.");
// 
//             Bank.print("Customer " + id + " is getting in line.");
//             Bank.queueLock.acquire();
//             Bank.line.add(this);  
//             Bank.queueLock.release();
// 
//             Bank.print("Customer " + id + " is selecting a teller.");
//             Bank.customersReady.release();  
// 
//             Thread.sleep(100);  
//             
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//         } finally {
//             Bank.bankOpen.release();  
//         }
//     }
// 
// }
package project2;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Customer extends Thread {
    private int customerId;
    private String transactionType;
    private Random rand = new Random();

    public Customer(int id) {
        this.customerId = id;
        this.transactionType = rand.nextBoolean() ? "withdrawal" : "deposit"; // Random transaction
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getTransaction() {
        return transactionType;
    }

    @Override
    public void run() {
        try {
            // Customer enters the bank, but only two customers are allowed in at a time
            Bank.bankOpen.acquire();
            Bank.print("Customer " + customerId + " is going to the bank.");
            
            // Customer gets in line
            Bank.print("Customer " + customerId + " is getting in line.");
            
            // Wait for a teller to be ready and go to the teller
            Teller teller = null;
            while ((teller = Bank.getNextAvailableTeller()) == null) {
                Thread.sleep(10); // Wait for a teller to become available
            }

            Bank.print("Customer " + customerId + " is selecting a teller.");
            Bank.print("Customer " + customerId + " goes to Teller " + teller.getTellerId());
            Bank.print("Customer " + customerId + " introduces itself to Teller " + teller.getTellerId());
            
            // Notify teller that this customer is ready
            Bank.customersReady.release();
            teller.join();  // Wait for the teller to complete the transaction

            // Customer waits for the teller to ask for the transaction
            Bank.print("Customer " + customerId + " tells the teller the " + transactionType + " transaction.");

            // Wait for the teller to complete the transaction
            Bank.customersReady.acquire();

            Bank.print("Customer " + customerId + " is leaving the bank.");
            Bank.bankOpen.release(); // Allow space for more customers in the bank

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
