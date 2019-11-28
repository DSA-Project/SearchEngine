package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Begin");
        Engine SearchEngine=new Engine("C:\\Users\\hamda\\IdeaProjects\\Parse2\\LOL\\");//Add the path here
        SearchEngine.createForwardIndex();//Calling method
        SearchEngine.createReverseIndex();//Calling method
        System.out.println("Ended");
    }
}

