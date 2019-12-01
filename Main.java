import java.io.IOException;

public class Main {

        public static void main(String[] args) throws IOException {
            System.out.println("Begin");
            Engine SearchEngine=new Engine("C:\\Users\\Tariq Farooq\\Desktop\\SEMESTER 3\\Data Structures and Algorithms\\PROJET FILS\\");//Add the path here
            SearchEngine.createForwardIndex();//Calling method
            SearchEngine.createReverseIndex();//Calling method
            System.out.println("Ended");

        }
    }