
import java.io.IOException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Begin");
        Engine SearchEngine=new Engine("D:\\DSA.project\\blogs");//Add the path here
        SearchEngine.createForwardIndex();//Calling method
        SearchEngine.createReverseIndex();//Calling method
        System.out.println("Ended");
    }
}