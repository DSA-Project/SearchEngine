package com.company;

public class Main {

    public static void main(String[] args) {
        System.out.println("Begin");
        Engine SearchEngine=new Engine("C:\\Users\\hamda\\IdeaProjects\\Parse2\\LOL\\");//Add the path here
        SearchEngine.createForwardIndex();//Calling method
        System.out.println("Ended");

    }
}

