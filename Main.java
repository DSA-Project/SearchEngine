
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("Begin");
        Engine SearchEngine = new Engine("C:\\Users\\Tariq Farooq\\Desktop\\SEMESTER 3\\Data Structures and Algorithms\\PROJET FILS\\");//Add the path here
        System.out.println("Creating Forward Indexing");
        long start = System.currentTimeMillis();
        SearchEngine.createForwardIndex();
        long end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.out.println("Execution time is " + formatter.format((end - start) / 1000d) + " seconds");
        SearchEngine.createReverseIndex();
        System.out.println("Creating Reverse Indexing");//Calling method
        start = System.currentTimeMillis();
        end = System.currentTimeMillis();
        System.out.print("Execution time is " + formatter.format((end - start) / 1000d) + " seconds");
        System.out.println("Ended");
    }
}



