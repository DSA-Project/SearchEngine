import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JsonObject;
import org.json.simple.parser.JSONParser;
import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Engine{
    private String Path; //The path of the 3 files in your pc
    private File file;
    private File[] dir;
        HashMap<Integer, HashMap<String, ArrayList<Integer>>> forwardIndex=new HashMap<>();//HashMap , FileName->(Word,Count)
        HashMap<File,Integer> docID=new HashMap<>();
        HashMap<String,Integer> wordID=new HashMap<>();
        Integer wordIndex=1;
        Integer docIndex=1;
        Engine(String Path)//Constructor to Initialise the path
        {
        this.Path=Path;
        }
    public void createForwardIndex(){
            String str = "D:\\DocID.json";
        FileReader reader;
            FileWriter fileblah;
            JSONObject dumb;
            try
            {
            File doc = new File(str);
            if(!doc.exists())
                fileblah = new FileWriter(str);
            int count=0;
            file = new File(Path);//All the files on the path
            File subDir[] = file.listFiles();//Lists them
        for (int s = 1; s <2; s++) {//Number of files
            //File[] blogs = subDir[s].listFiles();//All the blog files in each of the directory
            File[] folders = file.listFiles();
            File[] blogs = folders[0].listFiles();
            for (File g : blogs) {//Iterates through
                reader = new FileReader(doc);
                if (doc.length() != 0 ) {
                    JSONParser parser = new JSONParser();
                    dumb = (JSONObject) parser.parse(reader);
                    if (dumb.containsKey(g.toString())) {
                        System.out.println(g + "" + docIndex++);
                        continue;
                    }//reader.close();
                }
                Words words = new Words();
                JSONObject jsonObject = (JSONObject) readJson(g);
                System.out.println(g);//Reads json file
                String text = (String) jsonObject.get("text"); //extracting the text object
                text = processWords(text);//Processes each word
                Matcher m = Pattern.compile("[a-zA-Z0-9]+").matcher(text);//Seperates words based on pattern
                while (m.find()) {//This will iterate as long as there are words inside Matcher
                    wordID.put(m.group(), wordIndex);
                    if (!wordID.containsValue(m.group())) {
                        wordIndex++;
                    }
                    words.setHash(m.group());//Method of words object/Class
                }                docID.put(g, docIndex);
                forwardIndex.put(docIndex, words.getHash());
                if (docIndex >=1) {//Only do for 10 files, Remove this to do for all files, DO IT AT YOUR OWN RISK.
                    break;
                }
                docIndex++;
            }
        }
        saveDocID(docID);
        saveWordID(wordID);
        saveForwardIndex(forwardIndex);//Prints map into a file
        //reader.close();
        }
        catch(Exception e)
        {
        e.printStackTrace();
        }
        }
public void createReverseIndex() throws IOException {//Creates ReverseIndex
        HashMap<String,List<Integer>> invertedIndex=new HashMap<>();
        List<Integer> docID=new ArrayList<>();//Initialise and Declare an ArrayList
        for(Map.Entry<Integer,HashMap<String,ArrayList<Integer>>> entry:forwardIndex.entrySet()){//First for Loop to iterate in forward Index
        for(Map.Entry<String, ArrayList<Integer>> entry1:entry.getValue().entrySet()) {//Second for Loop to iterate in wordList HashMap
        if (invertedIndex.containsKey(entry1.getKey())) {//Checks if invertedIndex contains the word, if it does then it'll simply add the document's name
        docID = invertedIndex.get(entry1.getKey());//Gets the list present at that word's value
        docID.add(entry.getKey());//Adds the new document name to the word's list of doc's
        invertedIndex.put(entry1.getKey(),docID);
        }
        else{
        docID=new ArrayList<Integer>();
        docID.add(entry.getKey());
        invertedIndex.put(entry1.getKey(),docID);
        }

        }}
        saveReverseIndex(invertedIndex);
        }

public void saveReverseIndex(HashMap<String, List<Integer>> reverse) throws IOException {//Save Reverse Index into file
        BufferedWriter bw= new BufferedWriter(new FileWriter("reverseIndex.json"));
        JSONObject obj=new JSONObject();
        JSONArray arr=new JSONArray();
        reverse.forEach((Key,Value)->{
        obj.put(Key,Value);
        });
        bw.write(obj.toJSONString());
        }


private Object readJson(File fileName) throws Exception{//Read and Return JSON object
        FileReader reader=new FileReader(fileName);
        JSONParser jsonParser=new JSONParser();
        //System.out.println("PARSED");
        return jsonParser.parse(reader);
        }


public static String processWords(String text){//Process Words
        text= text.replaceAll("(['])", "");  //replace apostrophe with nothing lel
        text = text.replaceAll("([^a-zA-Z0-9\\s])", "");  //replacing punctuations with space
        text = text.toLowerCase();   //all lower case
        return text;
        }


public void saveForwardIndex(HashMap<Integer, HashMap<String, ArrayList<Integer>>> map){//Save forwardIndex HashMap into File.
        JSONObject json=new JSONObject();
        JSONArray ja1=new JSONArray();
        try{
        BufferedWriter bw=new BufferedWriter(new FileWriter("D:/ForwardIndex.json"));//True means it will not over write into file but write into existing
        map.forEach((Key,Value)->{//Iterate through first HashMap
        Value.forEach((Key1,Value1)->{//Iterates through the hashMap that is inside the hashmap
        Map m=new LinkedHashMap(1);
        m.put(Key1,Value1);
        ja1.add(m);

        });
        //ja1.clear();
        });
        bw.write(ja1.toJSONString());
        bw.flush();
        } catch (IOException e) {
        e.printStackTrace();
        }
        }
public void saveDocID(HashMap<File,Integer> docsID) throws IOException {//Write DOCID into DocId.json
        BufferedWriter bw= new BufferedWriter(new FileWriter("D:/DocID.json"));

        JSONObject obj=new JSONObject();
        docsID.forEach((Key,Value)->{
        obj.put(Key,Value);
        });
        bw.write(obj.toJSONString());
        bw.flush();
        }
public void saveWordID(HashMap<String,Integer> wordID) throws IOException{//Write WORDID into lexicons.json
        BufferedWriter bw= new BufferedWriter(new FileWriter("D:/Lexicons.json"));
        JSONObject obj=new JSONObject();
        wordID.forEach((Key,Value)->{
        obj.put(Key,Value);
        });
        bw.write(obj.toJSONString());
        bw.flush();
        }
        }