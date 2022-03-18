import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        Storehouse storehouse = new Storehouse();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the name of text file with extension like \"text.txt\".");
        System.out.println("If the text file is not in the same folder with src folder, enter full path please.");
        String filename = scanner.nextLine();
        System.out.println("For how many weeks do you want the logs? Warning! Can not enter a number fewer than demanded weeks.");
        int week = scanner.nextInt();
        scanner.nextLine();

        /**
         * Read inputs from file.
         */
        File file = new File(filename);
        Scanner input = new Scanner(file);
        StringBuilder textbuild = new StringBuilder();

        //Add lines unless start with "#", "//" or newline.
        while (input.hasNext()) {
            String line = input.nextLine() + "\n";
            if (line.startsWith("#") || line.startsWith("\n") || line.startsWith("//")) continue;
            textbuild.append(line);
        }
        String text = textbuild.toString().trim();

        /*
         * Start to take products.
         */
        Scanner reader = new Scanner(text);
        while (reader.hasNextLine()) {
            boolean productend = false;
            String firstline = reader.nextLine();


            HashMap<Integer, Integer> lowerItems = new HashMap<Integer, Integer>();   //store lower items with number of needs.
            int itemId = Integer.parseInt(firstline);
            int onHand = Integer.parseInt(reader.nextLine());
            int scheduledReceiptAmount = Integer.parseInt(reader.nextLine());
            int scheduledReceiptPeriod = Integer.parseInt(reader.nextLine());
            int leadTime = Integer.parseInt(reader.nextLine());
            int lotSize = Integer.parseInt(reader.nextLine());

            /*
             Read all lower items.
             */
                while (!productend) {    // read unless type "end".
                    String line = reader.nextLine();
                    if (line.equals("end")) {
                        productend = true;
                        continue;
                    }

                    String[] Line = line.split(" "); // "Split item id's and number of needs"
                    lowerItems.put(Integer.parseInt(Line[0]), Integer.parseInt(Line[1]));
                }
            //End of product.
            //Add product to product list.
            storehouse.getProducts().add(new Product(itemId, onHand, scheduledReceiptAmount, scheduledReceiptPeriod, leadTime, lotSize, lowerItems, week));
        }

        System.out.println("Enter item ID demanded");
        int demandID = scanner.nextInt();
        scanner.nextLine();

        while(true) {
            System.out.println("If you finish this step type \"end\"");
            System.out.println("Enter demanded week and amount. eg:\"3 50\"");
            String demandline = scanner.nextLine();
            if (demandline.startsWith("end"))
                break;
            String[] demandLine = demandline.split(" "); // Split "period" and "amount" then put them to grossReq.
            storehouse.getProductWithId(demandID).getGrossRequirement().put(Integer.parseInt(demandLine[0]), Integer.parseInt(demandLine[1]));

        }


        /**
         * CREATING MRP RECORDS WITH METHOD CALL
         */
        storehouse.createMRPRecords(storehouse.getProductWithId(demandID));


        boolean possible = true;
        for(Product product : storehouse.getProducts()) {
            Set<Integer> periods = product.getTimePhasedNetRequirement().keySet();
            for (Integer period : periods){
                if (period<1) possible=false;
            }
        }

        /**
        Print All Products with Informations
        */
        if(possible) {
            for (Product product : storehouse.getProducts()) {
                int size = product.getGrossRequirement().size();
                int idLength = String.valueOf(product.getItemId()).length();
                System.out.print("id: " + product.getItemId() + (new String(new char[22 - idLength]).replace("\0", " ")) + " | ");
                for (int period = 1; period <= product.getNetRequirement().size(); period++) {
                    System.out.print("Period " + period + " |");
                }
                System.out.print("\n----------------------------------------------------------------------------------------------------------------------------------");

                System.out.print("\nGross Requirement          |");
                for (int period = 1; period <= size; period++) {
                    System.out.print("    ");
                    System.out.format("%-6s", product.getGrossRequirement().get(period));
                }

                System.out.print("\nScheduled Receip           |");
                for (int period = 1; period <= size; period++) {
                    System.out.print("    ");
                    System.out.format("%-6s", product.getScheduledReceipt().get(period));
                }
                System.out.print("\nOn Hand From Prior Period  |");
                for (int period = 1; period <= size; period++) {
                    System.out.print("    ");
                    System.out.format("%-6s", product.getOnHand().get(period));
                }
                System.out.print("\nNet Requirements           |");
                for (int period = 1; period <= size; period++) {
                    System.out.print("    ");
                    System.out.format("%-6s", product.getNetRequirement().get(period));
                }
                System.out.print("\nTime Phased Net Requirement|");
                for (int period = 1; period <= size; period++) {
                    System.out.print("    ");
                    System.out.format("%-6s", product.getTimePhasedNetRequirement().get(period));
                }
                System.out.print("\nPlanned Order release      |");
                for (int period = 1; period <= size; period++) {
                    System.out.print("    ");
                    System.out.format("%-6s", product.getPlannedOrderRelease().get(period));
                }
                System.out.print("\nPlanned Order Delivery     |");
                for (int period = 1; period <= size; period++) {
                    System.out.print("    ");
                    System.out.format("%-6s", product.getPlannedOrderDelivery().get(period));
                }
                System.out.println("\n\n");
            }
        }
        else System.out.println("Product can not be produced on time!!");
    }
}

