// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.Semaphore;
//
// public class Bank {
//     public static final int NUM_TELLERS = 3;
//     public static final int NUM_CUSTOMERS = 50;
//     public static int servedCustomers = 0;
//     
//     public static Semaphore safe = new Semaphore(2);
//     public static Semaphore manager = new Semaphore(1);
//     public static Semaphore bankOpen = new Semaphore(2);
//     public static Semaphore queueLock = new Semaphore(1);
//     public static Semaphore customerLock = new Semaphore(1);  
//     public static Semaphore customersReady = new Semaphore(0);
//     public static Semaphore tellersReady = new Semaphore(0); 
//     public static Semaphore servedCountLock = new Semaphore(1);
//     
//     public static List<Customer> line = new ArrayList<>();
//     
//     public static void main(String[] args) {
//         print("Bank is open.");
//         Teller[] tellers = new Teller[NUM_TELLERS];
//         
//         for (int i = 0; i < NUM_TELLERS; i++) {
//             tellers[i] = new Teller(i);
//             tellers[i].start();
//             try {
//                 tellersReady.acquire();
//                 Thread.sleep(10);
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//             }
//         }
//         
//         Customer[] customers = new Customer[NUM_CUSTOMERS];
//         
//         for (int i = 0; i < NUM_CUSTOMERS; i++) {
//             customers[i] = new Customer(i);
//             customers[i].start();
//         }
//         
//         for (Thread customer : customers) {
//             try {
//                 customer.join();
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//             }
//         }
//         
//         for (Teller teller : tellers) {
//             teller.interrupt();
//         }
//         
//         for (Teller teller : tellers) {
//             try {
//                 teller.join();
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//             }
//         }
//         
//         print("Bank is closed.");
//     }
//     
//     public static synchronized void print(String message) {
//         System.out.println(message);
//     }
// }
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Bank {
    public static final int NUM_TELLERS = 3;
    public static final int NUM_CUSTOMERS = 50;
    public static int servedCustomers = 0;

    public static Semaphore safe = new Semaphore(2);  // Limit the number of tellers in the safe
    public static Semaphore manager = new Semaphore(1);  // Only one manager at a time
    public static Semaphore bankOpen = new Semaphore(2);  // Only allow 2 customers in at a time
    public static Semaphore queueLock = new Semaphore(1);  // Lock for accessing customer queue
    public static Semaphore customersReady = new Semaphore(0);  // Customers ready to be served
    public static Semaphore tellersReady = new Semaphore(0);  // Tell customers when a teller is ready
    public static Semaphore servedCountLock = new Semaphore(1);  // Lock for the served count
    public static List<Customer> line = new ArrayList<>();
    public static Teller[] tellers = new Teller[NUM_TELLERS]; // Declare tellers array at the class level

    public static void main(String[] args) {
        print("Bank is open.");

        // Create tellers
        for (int i = 0; i < NUM_TELLERS; i++) {
            tellers[i] = new Teller(i);
            tellers[i].start();
            try {
                tellersReady.acquire();
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Customer[] customers = new Customer[NUM_CUSTOMERS];

        // Create customers
        for (int i = 0; i < NUM_CUSTOMERS; i++) {
            customers[i] = new Customer(i);
            customers[i].start();
        }

        // Wait for customers to finish
        for (Thread customer : customers) {
            try {
                customer.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Shut down tellers
        for (Teller teller : tellers) {
            teller.interrupt();
        }

        for (Teller teller : tellers) {
            try {
                teller.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        print("Bank is closed.");
    }

    public static synchronized void print(String message) {
        System.out.println(message);
    }

    public static Teller getNextAvailableTeller() {
        for (Teller teller : tellers) {
            if (!teller.isBusy()) {
                return teller;
            }
        }
        return null;
    }
}
