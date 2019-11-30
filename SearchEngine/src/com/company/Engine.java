package com.company;

import opennlp.tools.stemmer.PorterStemmer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {
    private String Path; //The path of the 3 files in your pc
    private PorterStemmer stemmer=new PorterStemmer();
    private File file;
    private File[] dir;
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> forwardIndex=new HashMap<>();//HashMap , FileName->(Word,Count)
    private HashMap<Integer,File> docID=new HashMap<>();//HashMap for DOCID->DOCUMENT PATH
    private HashMap<String,Integer> wordID=new HashMap<>();//HashMap for Word->WordID
    private Integer wordIndex=1;
    private Integer docIndex=1;
    Engine(String Path)//Constructor to Initialise the path
    {
        this.Path=Path;
    }
    public void createForwardIndex(){//Creates Forward Index
        try {
            file = new File(Path);//All the files on the path
            File subDir[] = file.listFiles();//Lists them
            for (int s = 1; s <2; s++) {//Number of files
                File[] blogs = subDir[s].listFiles();//All the blog files in each of the directory

                for (File g : blogs) {//Iterates through
                    docID.put(docIndex,g);
                    Words words=new Words();
                    JSONObject jsonObject = (JSONObject) readJson(g);//Reads json file
                    String text = (String) jsonObject.get("text"); //extracting the text object
                    text = processWords(text);//Processes each word
                    Matcher m = Pattern.compile("[a-zA-Z0-9]+").matcher(text);//Seperates words based on pattern
                    while (m.find()) {//This will iterate as long as there are words inside Matcher
                        String stem=stemmer.stem(m.group());
                        if(!wordID.containsKey(stem)){
                            wordID.put(stem,wordIndex);
                            words.setHash(wordIndex);
                            wordIndex++;
                        }
                        else{
                           words.setHash(wordID.get(stem));
                        }

                    }

                    forwardIndex.put(docIndex,words.getHash());
                    if(docIndex==10){//Only do for 10 files, Remove this to do for all files, DO IT AT YOUR OWN RISK.
                        break;
                    }
                    docIndex++;
                }
                saveDocID(docID);
                saveWordID(wordID);
                saveForwardIndex(forwardIndex);//Prints map into a file

            }}
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createReverseIndex() throws IOException {//Creates ReverseIndex
        HashMap<Integer,HashMap<Integer,ArrayList<Integer>>> invertedIndex=new HashMap<>();
        HashMap<Integer,ArrayList<Integer>> docMap=new HashMap<>();
        ArrayList<Integer> docID;//Initialise and Declare an ArrayList
            for(Map.Entry<Integer,HashMap<Integer,ArrayList<Integer>>> entry:forwardIndex.entrySet()){//First for Loop to iterate in forward Index
                for(Map.Entry<Integer, ArrayList<Integer>> wordMap:entry.getValue().entrySet()) {//Second for Loop to iterate in wordList HashMap
                if (invertedIndex.containsKey(wordMap.getKey())) {//Checks if invertedIndex contains the word, if it does then it'll simply add the document's name
                    docMap = invertedIndex.get(wordMap.getKey());
                    docMap.put(entry.getKey(),wordMap.getValue());//Adds the new document name to the word's list of doc's
                    invertedIndex.put(wordMap.getKey(),docMap);
                }
                else{
                    docMap=new HashMap<>();
                    docMap.put(entry.getKey(),wordMap.getValue());
                    invertedIndex.put(wordMap.getKey(),docMap);
                }

            }}
            saveReverseIndex(invertedIndex);
    }

    public void saveReverseIndex(HashMap<Integer, HashMap<Integer,ArrayList<Integer>>> reverse) throws IOException {//Save Reverse Index into file
        JSONObject json=new JSONObject();
        JSONArray ja1=new JSONArray();

        BufferedWriter bw=new BufferedWriter(new FileWriter("ReverseIndex.json"));//True means it will not over write into file but write into existing
        reverse.forEach((Key,Value)->{//Iterate through first HashMap
            Value.forEach((Key1,Value1)->{//Iterates through the hashMap that is inside the hashmap

                Map m=new LinkedHashMap(2);
                m.put(Key1,Value1);
                ja1.add(m);

            });
            json.put(Key,ja1);
            try {
                bw.write(json.toJSONString());
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            json.clear();
            ja1.clear();
        });
    }


    private static Object readJson(File fileName) throws Exception{//Read and Return JSON object
        FileReader reader=new FileReader(fileName);
        JSONParser jsonParser=new JSONParser();
        return jsonParser.parse(reader);
    }

    public static String processWords(String text){//Process Words
        text= text.replaceAll("(['])", "");  //replace apostrophe with nothing lel
        text = text.replaceAll("([^a-zA-Z0-9\\s])", "");  //replacing punctuations with space
        text = text.toLowerCase();   //all lower case
        return text;
    }

    public void saveForwardIndex(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> map) throws IOException{//Save forwardIndex HashMap into File.
        JSONObject json=new JSONObject();
        JSONArray ja1=new JSONArray();

            BufferedWriter bw=new BufferedWriter(new FileWriter("ForwardIndex.json"));//True means it will not over write into file but write into existing
            map.forEach((Key,Value)->{//Iterate through first HashMap
                Value.forEach((Key1,Value1)->{//Iterates through the hashMap that is inside the hashmap

                    Map m=new LinkedHashMap(2);
                    m.put(Key1,Value1);
                    ja1.add(m);

                });
                json.put(Key,ja1);
                try {
                    bw.write(json.toJSONString());
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                json.clear();
                ja1.clear();
            });
        }

    public void saveDocID(HashMap<Integer,File> docsID) throws IOException {//Write DOCID into DocId.json
            BufferedWriter bw= new BufferedWriter(new FileWriter("DocID.json"));
            JSONObject obj=new JSONObject();
            docsID.forEach((Key,Value)->{
                obj.put(Key,Value);
            });
            bw.write(obj.toJSONString());
            bw.flush();
    }
    public void saveWordID(HashMap<String,Integer> wordID) throws IOException{//Write WORDID into lexicons.json
        BufferedWriter bw= new BufferedWriter(new FileWriter("Lexicons.json"));
        JSONObject obj=new JSONObject();
        wordID.forEach((Key,Value)->{
            obj.put(Key,Value);
        });
        bw.write(obj.toJSONString());
        bw.flush();
    }

}


