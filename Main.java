package com.company;

public class Main {

    public static void main(String[] args) {
        Engine SearchEngine=new Engine("C:\\Users\\hamda\\IdeaProjects\\Parse2\\LOL\\");//Add the path here
        SearchEngine.createForwardIndex();//Calling method 
        System.out.println("Ended");
    }
}

