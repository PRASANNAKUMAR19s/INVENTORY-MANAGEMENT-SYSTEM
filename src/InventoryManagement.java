import java.util.Scanner;

/**
 * Inventory Management System
 */
public class InventoryManagement {


    static final int MAX_PRODUCTS = 100;
    static final int LOW_STOCK_THRESHOLD = 10;


    static int[] productIds = new int[MAX_PRODUCTS];
    static String[] productNames = new String[MAX_PRODUCTS];
    static int[] quantities = new int[MAX_PRODUCTS];
    static double[] prices = new double[MAX_PRODUCTS];
    static int productCount = 0;

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        seedDemoData();

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║    INVENTORY MANAGEMENT SYSTEM  v2.0     ║");
        System.out.println("╚══════════════════════════════════════════╝");

        boolean running = true;

        while (running) {

            printMenu();
            int choice = readInt("Enter choice: ");
            System.out.println();

            if (choice == 1) {
                addProduct();

            } else if (choice == 2) {
                displayAllProducts();

            } else if (choice == 3) {
                updateQuantity();

            } else if (choice == 4) {
                searchProduct();

            } else if (choice == 5) {
                showLowStockAlerts();

            } else if (choice == 6) {
                removeProduct();

            } else if (choice == 7) {
                System.out.println("Goodbye! 👋");
                running = false;

            } else {
                System.out.println("⚠  Invalid choice. Please enter 1-7.\n");
            }
        }

        sc.close();
    }

    static void printMenu() {
        System.out.println("+-----------------------------------------+");
        System.out.println("|              MAIN MENU                  |");
        System.out.println("+-----------------------------------------+");
        System.out.println("|  1. Add Product                         |");
        System.out.println("|  2. Display All Products                |");
        System.out.println("|  3. Update Quantity                     |");
        System.out.println("|  4. Search Product                      |");
        System.out.println("|  5. Low Stock Alerts                    |");
        System.out.println("|  6. Remove Product                      |");
        System.out.println("|  7. Exit                                |");
        System.out.println("+-----------------------------------------+");
    }

    static void addProduct() {

        System.out.println("--- Add New Product -----------------------");

        if (productCount >= MAX_PRODUCTS) {
            System.out.println("WARNING: Inventory is full! Max " + MAX_PRODUCTS + " products.\n");
            return;
        }

        int id = readInt("Product ID   : ");

        if (findIndexById(id) != -1) {
            System.out.println("WARNING: Product ID " + id + " already exists!\n");
            return;
        }

        System.out.print("Product Name : ");
        // sc.nextLine();
        String name = sc.nextLine().trim();

        int qty = readInt("Quantity     : ");
        double price = readDouble("Price (Rs.)  : ");

        productIds[productCount] = id;
        productNames[productCount] = name;
        quantities[productCount] = qty;
        prices[productCount] = price;
        productCount++;

        System.out.println("SUCCESS: Product '" + name + "' added!\n");

        checkLowStock(productCount - 1);
    }

    static void displayAllProducts() {

        if (productCount == 0) {
            System.out.println("INFO: No products found in inventory.\n");
            return;
        }

        System.out.println("+--- All Products ----------------------------------------------------------------------+");
        System.out.printf("| %-6s \t|  %-22s \t| %8s |  %10s |%s%n","ID", "Name", "Qty", "Price(Rs)", "Status \t|");
        System.out.println("+---------------------------------------------------------------------------------------+");

        for (int i = 0; i < productCount; i++) {

            String status;

            if (quantities[i] == 0) {
                status = "OUT OF STOCK \t|";

            } else if (quantities[i] <= LOW_STOCK_THRESHOLD) {
                status = "LOW STOCK \t|";

            } else {
                status = "OK \t\t|";
            }

            System.out.printf("| %-6d  \t| %-22s \t| %8d | %10.2f | %s%n",
                    productIds[i], productNames[i],
                    quantities[i], prices[i], status);
        }

        System.out.println("+---------------------------------------------------------------------------------------+");
        System.out.println("Total products: " + productCount + "\n");
    }

    static void updateQuantity() {

        System.out.println("--- Update Quantity -----------------------");

        int id = readInt("Product ID  : ");
        int idx = findIndexById(id);

        if (idx == -1) {
            System.out.println("WARNING: Product ID " + id + " not found!\n");
            return;
        }

        System.out.println("Product     : " + productNames[idx]);
        System.out.println("Current Qty : " + quantities[idx]);
        System.out.println("  [1] Add stock");
        System.out.println("  [2] Remove stock");
        System.out.println("  [3] Set exact value");
        int action = readInt("Choose      : ");

        int amount = readInt("Amount      : ");

        if (amount < 0) {
            System.out.println("WARNING: Please enter a non-negative number.\n");
            return;
        }

        if (action == 1) {
            quantities[idx] += amount;
            System.out.println("SUCCESS: Stock added. New Quantity: " + quantities[idx] + "\n");

        } else if (action == 2) {

            if (amount > quantities[idx]) {
                System.out.println("WARNING: Cannot remove " + amount
                        + ". Only " + quantities[idx] + " in stock!\n");
                return;

            } else {
                quantities[idx] -= amount;
                System.out.println("SUCCESS: Stock removed. New Quantity: " + quantities[idx] + "\n");
            }

        } else if (action == 3) {
            quantities[idx] = amount;
            System.out.println("SUCCESS: Quantity set to " + quantities[idx] + "\n");

        } else {
            System.out.println("WARNING: Invalid action. Choose 1, 2, or 3.\n");
            return;
        }

        checkLowStock(idx);
    }

    static void searchProduct() {

        System.out.println("--- Search Product ------------------------");
        System.out.println("  [1] Search by ID");
        System.out.println("  [2] Search by Name");
        int option = readInt("Choose     : ");

        if (option == 1) {

            int id = readInt("Product ID : ");
            int idx = findIndexById(id);

            if (idx == -1) {
                System.out.println("WARNING: No product found with ID " + id + "\n");
            } else {
                printProductDetail(idx);
            }

        } else if (option == 2) {

            System.out.print("Name keyword: ");
            sc.nextLine();
            String keyword = sc.nextLine().trim().toLowerCase();

            boolean found = false;

            for (int i = 0; i < productCount; i++) {
                if (productNames[i].toLowerCase().contains(keyword)) {
                    printProductDetail(i);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("WARNING: No products found matching '" + keyword + "'\n");
            }

        } else {
            System.out.println("WARNING: Invalid option. Choose 1 or 2.\n");
        }
    }

    static void printProductDetail(int i) {
        System.out.println("+-- Product Details --------------------+");
        System.out.println("|  ID    : " + productIds[i] + "\t\t \t\t|");
        System.out.println("|  Name  : " + productNames[i] + " \t\t|");
        System.out.println("|  Qty   : " + quantities[i] + " \t\t\t\t|");
        System.out.printf("|  Price : Rs. %.2f%n", prices[i]);
        System.out.printf("|  Value : Rs. %.2f%n", prices[i] * quantities[i]);

        if (quantities[i] == 0) {
            System.out.println("|  Status: OUT OF STOCK \t\t|");

        } else if (quantities[i] <= LOW_STOCK_THRESHOLD) {
            System.out.println("|  Status: LOW STOCK \t\t|");

        } else {
            System.out.println("|  Status: OK \t\t|");
        }

        System.out.println("+---------------------------------------+\n");
    }

    static void showLowStockAlerts() {

        System.out.println("--- Low Stock Alerts (threshold <= "
                + LOW_STOCK_THRESHOLD + ") -------");

        boolean anyAlert = false;

        for (int i = 0; i < productCount; i++) {

            if (quantities[i] == 0) {
                System.out.printf("  [OUT OF STOCK] ID %-5d | %-22s | Qty: %d%n",
                        productIds[i], productNames[i], quantities[i]);
                anyAlert = true;

            } else if (quantities[i] <= LOW_STOCK_THRESHOLD) {
                System.out.printf("  [LOW STOCK]    ID %-5d | %-22s | Qty: %d%n",
                        productIds[i], productNames[i], quantities[i]);
                anyAlert = true;
            }
        }

        if (!anyAlert) {
            System.out.println("  All products are sufficiently stocked.");
        }

        System.out.println();
    }

    static void removeProduct() {

        System.out.println("--- Remove Product ------------------------");

        int id = readInt("Product ID : ");
        int idx = findIndexById(id);

        if (idx == -1) {
            System.out.println("WARNING: Product ID " + id + " not found!\n");
            return;
        }

        System.out.println("About to remove: " + productNames[idx]);
        System.out.print("Confirm? (y/n) : ");
        //sc.nextLine();
        String confirm = sc.nextLine().trim().toLowerCase();

        if (confirm.equals("y")) {

            String removedName = productNames[idx];

            for (int i = idx; i < productCount - 1; i++) {
                productIds[i] = productIds[i + 1];
                productNames[i] = productNames[i + 1];
                quantities[i] = quantities[i + 1];
                prices[i] = prices[i + 1];
            }
            productCount--;

            System.out.println("SUCCESS: '" + removedName + "' removed.\n");

        } else {
            System.out.println("INFO: Removal cancelled.\n");
        }
    }

    static int findIndexById(int id) {

        int foundIndex = -1;

        for (int i = 0; i < productCount; i++) {
            if (productIds[i] == id) {
                foundIndex = i;
            }
        }

        return foundIndex;
    }

    static void checkLowStock(int i) {

        if (quantities[i] == 0) {
            System.out.println("*** ALERT: '" + productNames[i]
                    + "' is OUT OF STOCK! ***\n");

        } else if (quantities[i] <= LOW_STOCK_THRESHOLD) {
            System.out.println("*** ALERT: '" + productNames[i]
                    + "' is LOW on stock! Only "
                    + quantities[i] + " left. ***\n");
        }
    }

    static int readInt(String prompt) {

        boolean validInput = false;
        int value = 0;

        while (!validInput) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                value = Integer.parseInt(input);
                validInput = true;

            } catch (NumberFormatException e) {
                System.out.println("  WARNING: Invalid input! Please enter a whole number.");
            }
        }

        return value;
    }

    static double readDouble(String prompt) {

        boolean validInput = false;
        double value = 0.0;

        while (!validInput) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                value = Double.parseDouble(input);
                validInput = true;

            } catch (NumberFormatException e) {
                System.out.println("  WARNING: Invalid input! Please enter a valid number.");
            }
        }

        return value;
    }

    static void seedDemoData() {
        addProductDirect(101, "Laptop - Dell XPS 15", 45, 89999.00);
        addProductDirect(102, "Wireless Mouse", 8, 999.00);
        addProductDirect(103, "USB-C Hub 7-in-1", 22, 2499.00);
        addProductDirect(104, "Mechanical Keyboard", 0, 4999.00);
        addProductDirect(105, "27in Monitor - LG", 15, 24999.00);
        addProductDirect(106, "Webcam 1080p", 5, 3299.00);
        addProductDirect(107, "Laptop Stand - Aluminium", 30, 1799.00);
    }

    static void addProductDirect(int id, String name, int qty, double price) {
        productIds[productCount] = id;
        productNames[productCount] = name;
        quantities[productCount] = qty;
        prices[productCount] = price;
        productCount++;
    }
}