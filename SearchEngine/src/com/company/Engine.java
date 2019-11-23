package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {
    private String Path; //The path of the 3 files in your pc
    private File file;
    private File[] dir;
    Engine(String Path)//Constructor to Initialise the path
    {
        this.Path=Path;

    }
    public void createForwardIndex(){
        HashMap<String,HashMap<String,Integer>> forwardIndex=new HashMap<>();//HashMap , FileName->(Word,Count)
        try {
            file = new File(Path);//All the files on the path
            String subDir[] = file.list();//Lists them
            for (int s = 0; s <subDir.length; s++) {//Number of files
                String[] blogs = new File(Path + subDir[s]).list();//All the blog files in each of the directory
                forwardIndex.clear();//Clears the hashmap
                for (String g : blogs) {//Iterates through
                    Words words=new Words();
                    JSONObject jsonObject = (JSONObject) readJson(Path+"\\"+subDir[s]+"\\"+g);//Reads json file
                    String text = (String) jsonObject.get("text"); //extracting the text object
                    text = processWords(text);//Processes each word
                    Matcher m = Pattern.compile("[a-zA-Z0-9]+").matcher(text);//Seperates words based on pattern
                    while (m.find()) {//This will iterate as long as there are words inside Matcher
                        words.setHash(m.group()) ;//Method of words object/Class
                    }
                    g.replaceAll("blogs_","");//replaces the name blogs
                    forwardIndex.put(g,words.getHash());//Add the file name as key and (Word,count) as value
                }
                printMap(forwardIndex);//Prints map into a file
            }}
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public Object readJson(String fileName) throws Exception{
        FileReader reader=new FileReader(fileName);
        JSONParser jsonParser=new JSONParser();
        return jsonParser.parse(reader);
    }

    public static String processWords(String text){
        text= text.replaceAll("(['])", "");  //replace apostrophe with nothing lel
        text = text.replaceAll("([^a-zA-Z0-9\\s])", "");  //replacing punctuations with space
        text = text.toLowerCase();   //all lower case
        return text;
    }

    public void printMap(HashMap<String,HashMap<String,Integer>> map){
        JSONObject json=new JSONObject();
        JSONArray ja1=new JSONArray();
        try{
            BufferedWriter bw=new BufferedWriter(new FileWriter("ForwardIndex.json",true));//True means it will not over write into file but write into existing
            map.forEach((Key,Value)->{//Iterate through first HashMap
                Value.forEach((Key1,Value1)->{//Iterates through the hashMap that is inside the hashmap

                    Map m=new LinkedHashMap(2);
                    m.put("1",Key1);
                    m.put("2",Value1);
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

        } catch (IOException e) {
            e.printStackTrace();
        }


    }}



