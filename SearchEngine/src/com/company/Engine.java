import com.fasterxml.jackson.databind.ObjectMapper;
import opennlp.tools.stemmer.PorterStemmer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private HashMap<Integer,HashMap<Integer,ArrayList<Integer>>> invertedIndex=new HashMap<>();
    private Integer wordIndex=1;
    private Integer docIndex=1;
    ObjectMapper mapper=new ObjectMapper();
    Engine(String Path)//Constructor to Initialise the path
    {
        this.Path=Path;
    }
    public void createForwardIndex(){//Creates Forward Index
        try {
            file = new File(Path);//All the files on the path
            File subDir[] = file.listFiles();//Lists them
            for (int s = 0; s <1; s++) {//Number of files
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

                    docIndex++;
                    //if (docIndex==15)
                      // break;
                }
                saveDocID(docID);
                System.out.println("DocID created");
                saveWordID(wordID);
                System.out.println("Lexicon created");
                saveForwardIndex(forwardIndex);//Prints map into a file
                System.out.println("Forward index created");

            }}
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void createReverseIndex() throws IOException {//Creates ReverseIndex

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
        System.out.println("Reverse index created");
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

    public void saveForwardIndex(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> map) throws IOException {//Save forwardIndex HashMap into File.

        mapper.writeValue(new File("ForwardIndex.json"), forwardIndex);

    }

    public void saveDocID(HashMap<Integer,File> docsID) throws IOException
    {
        mapper.writeValue(new File("DocID.json"), docsID);
    }

    public void saveWordID(HashMap<String,Integer> wordID) throws IOException{//Write WORDID into lexicons.json
        mapper.writeValue(new File("Lexicons.json"), wordID);
    }
    public void saveReverseIndex(HashMap<Integer, HashMap<Integer,ArrayList<Integer>>> reverse) throws IOException {//Save Reverse Index into file

        mapper.writeValue(new File("reverseIndex.json"), invertedIndex);

    }

}