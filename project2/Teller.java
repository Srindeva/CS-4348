// import java.util.Random;
// 
// class Teller extends Thread {
//     private final int id;
//     
//     public Teller(int id) {
//         this.id = id;
//     }
//     
//     public int getID() {
//         return id;
//     }
//     
//     @Override
//     public void run() {
// 
//         Bank.print("Teller " + id + " is ready to serve.");
//         Bank.tellersReady.release();
//         Bank.print("Teller " + id + " is waiting for a customer.");
// 
//         while (true) {
//             Customer customer = null;
//             
//             try {
//                 Bank.customersReady.acquire();
//                 Bank.queueLock.acquire();
//                 
//                 if (Bank.servedCustomers == Bank.NUM_CUSTOMERS) {
//                     Bank.queueLock.release();
//                     break;
//                 }
//                 
//                 if (!Bank.line.isEmpty()) {
//                     customer = Bank.line.remove(0);
//                     serveCustomer(customer);
//                 } else {
//                     Thread.sleep(100);
//                 }
//                 
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//                 break;
//             } finally {
//                 Bank.queueLock.release();
//             }
//         }
//         Bank.print("Teller " + id + " is leaving for the day.");
//     }
//     public void serveCustomer(Customer customer) {
//         Bank.print("Customer " + customer.getID() + " introduces itself to Teller " + id + ".");
//         Bank.print("Teller " + id + " is serving Customer " + customer.getID() + ".");
//         String transaction = customer.getTransaction();
//         Bank.print("Customer " + customer.getID() + " asks for a " + customer.getTransaction() + ".");
// 
//         if (transaction.equals("withdrawal")) {
//             Bank.print("Teller " + id + " requesting manager permission.");
//             try {
//                 Bank.manager.acquire();  
//                 Bank.print("Teller " + id + " got the manager's permission.");
//                 //Thread.sleep(new Random().nextInt(25) + 5);  
//                 Bank.manager.release();  
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//             }
//         }
// 
//         Bank.print("Teller " + id + " is going to the safe.");
//         try {
//             Bank.safe.acquire();  
//             Bank.print("Teller " + id + " is in the safe.");
//             //Thread.sleep(new Random().nextInt(40) + 10);  
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//         } finally {
//             Bank.safe.release();  
//             Bank.print("Teller " + id + " is leaving the safe.");
//         }
//         
//         customer.setHandledBy(this.id);
//         Bank.print("Teller " + id + " finishes Customer " + customer.getID() + "'s " + customer.getTransaction() + " request.");
//         Bank.print("Customer " + customer.getID() + " thanks Teller " + id + " and leaves.");
//         Bank.print("Teller " + id + " is waiting for a customer.");
//         
//         try {
//             Bank.servedCountLock.acquire();
//             Bank.servedCustomers++;  
//             Bank.servedCountLock.release();
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//         }
//     }
// }
package project2;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Teller extends Thread {
    private int tellerId;
    private Random rand = new Random();
    private boolean isBusy = false;

    // Semaphore to notify that the teller is ready to serve
    private static Semaphore tellersReady = new Semaphore(0);
    private static Semaphore customersReady = new Semaphore(0);

    public Teller(int id) {
        this.tellerId = id;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public int getTellerId() {
        return tellerId;
    }

    @Override
    public void run() {
        try {
            Bank.tellersReady.release();  // Notify that this teller is ready to serve
            while (true) {
                // Wait for a customer to approach
                customersReady.acquire();
                if (isInterrupted()) break;  // Stop the teller when interrupted

                // Start serving a customer
                isBusy = true;
                Customer customer = Bank.line.remove(0); // Get the first customer from the line
                Bank.print("Teller " + tellerId + " is serving Customer " + customer.getCustomerId());
                
                // Customer transaction process
                String transactionType = customer.getTransaction();
                Bank.print("Customer " + customer.getCustomerId() + " asks for a " + transactionType + " transaction.");
                Bank.print("Teller " + tellerId + " is handling the " + transactionType + " transaction.");

                if (transactionType.equals("withdrawal")) {
                    Bank.print("Teller " + tellerId + " is going to the manager.");
                    Bank.print("Teller " + tellerId + " is getting the manager's permission.");
                    Thread.sleep(rand.nextInt(26) + 5);  // Random sleep between 5 and 30 ms
                    Bank.print("Teller " + tellerId + " got the manager's permission.");
                }

                Bank.print("Teller " + tellerId + " is going to the safe.");
                // Random sleep between 10 and 50 ms to simulate performing the transaction
                Thread.sleep(rand.nextInt(41) + 10); 
                Bank.print("Teller " + tellerId + " is in the safe.");
                Bank.print("Teller " + tellerId + " is done with the transaction.");

                // Notify that the customer transaction is completed
                isBusy = false;
                Bank.print("Customer " + customer.getCustomerId() + " transaction is done.");
                customersReady.release();  // Let the customer leave the bank
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
