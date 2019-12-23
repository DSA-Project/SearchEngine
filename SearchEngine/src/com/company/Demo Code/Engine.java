

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import opennlp.tools.stemmer.PorterStemmer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Engine {

    private String Path; //The path of the 3 files in your pc
    private PorterStemmer stemmer=new PorterStemmer();
    private File file;
    private File[] dir;
    private HashMap<Integer, HashMap<Integer, Integer>> forwardIndex=new HashMap<>();//HashMap , FileName->(Word,Count)
    private HashMap<Integer,String> docID=new HashMap<>();//HashMap for DOCID->DOCUMENT PATH
    HashMap<Integer,HashMap<Integer,Integer>> invertedIndex=new HashMap<>();
    private HashMap<String,Integer> wordID=new HashMap<>();//HashMap for Word->WordID
    private Integer wordIndex=1;  //count for word id's
    private HashMap<String,Integer> revDocID=new HashMap<>();
    private Integer docIndex=0;     //count for doc id's
    ObjectMapper objectMapper = new ObjectMapper();

    Engine(String Path)//Constructor to Initialise the path
    {
        this.Path=Path;
    }
    Engine(){

    }

    public void loadIndex(){

        File docs,forward,Lexicons;

        try {
            docs= new File("A:\\Index\\DocID.json");
            forward=new File("A:\\Index\\ForwardIndex.json");
            Lexicons=new File("A:\\Index\\Lexicons.json");
            try {
                this.docID = objectMapper.readValue(docs, new TypeReference<HashMap<Integer, String>>() {});
                this.forwardIndex = objectMapper.readValue(forward, new TypeReference<HashMap<Integer, HashMap<Integer, Integer>>>() {});
                this.wordID=objectMapper.readValue(Lexicons,new TypeReference<HashMap<String,Integer>>(){});
                for(Map.Entry<Integer,String> docu:docID.entrySet()) {
                    revDocID.put(docu.getValue(),docu.getKey());
                }

            }catch(Exception e){
                System.out.println("No index files found!");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void append(){
/* Checks all the documents present

 */
        file=new File(Path);
        dir=file.listFiles();
        docIndex=docID.size()+1;
        wordIndex=wordID.size()+1;
        for(int i=0;i<dir.length;i++){
            File[] blogs=dir[i].listFiles();
            for(File g:blogs){
                if(!revDocID.containsKey(g.toString())){
                    createForwardIndex(g.toString(),docIndex);
                    docIndex++;
                }
            }
        }
    }

    public void createForwardIndex(String fileName,Integer docI){//Creates Forward Index
        try {
            int total_shares=0;

            JSONObject jsonObject = (JSONObject) readJson(new File(fileName));//Reads json file
            String text = (String) jsonObject.get("text"); //extracting the text object

            JSONObject thread= (JSONObject) jsonObject.get("thread");
            long perf=(long)thread.get("performance_score");
            JSONObject social= (JSONObject) thread.get("social");
            for(Iterator keys=social.keySet().iterator();keys.hasNext();){
                JSONObject key= (JSONObject) social.get(keys.next());
                Long share= (Long) key.get("shares");
                Integer shares=share.intValue();
                total_shares+=shares;
            }
            total_shares/=100;//Shares/100 to decrease shares weighing in result
            String title= (String) thread.get("title");
            text=title+text;//Concatenate Title into text to make it searchable
            text = processWords(text);//Processes each word
            Words words = new Words(total_shares, (int) perf);//Peformance score is aslso passed
            Matcher m = Pattern.compile("[a-zA-Z0-9]+").matcher(text);//Seperates words based on pattern
            while (m.find()) {//This will iterate as long as there are words inside Matcher
                 String stem = stemmer.stem(m.group());
                if (!wordID.containsKey(stem))  //checks for the key
                {
                    wordID.put(stem, wordIndex);   //adding stemmed word with word index to wordID
                    words.setHash(wordIndex);  //setting word hashmap
                    wordIndex++;
                } else {
                    words.setHash(wordID.get(stem));   //if stemmed word exists just get the stemmed word and update hash
                }
            }

            docID.put(docI, fileName);
            forwardIndex.put(docI, words.getHash());//create forward index

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void createReverseIndex() throws IOException {//Creates ReverseIndex

        HashMap<Integer,Integer> docMap;  //map for storing the docs
        for(Map.Entry<Integer,HashMap<Integer,Integer>> entry:forwardIndex.entrySet()){//First for Loop to iterate in forward Index
            for(Map.Entry<Integer, Integer> wordMap:entry.getValue().entrySet()) {//Second for Loop to iterate in wordList HashMap
                if (invertedIndex.containsKey(wordMap.getKey())) {//Checks if invertedIndex contains the word, if it does then it'll simply add the document's name
                    docMap = invertedIndex.get(wordMap.getKey());
                    docMap.put(entry.getKey(),wordMap.getValue());//Adds the new document name to the word's list of doc's
                    invertedIndex.put(wordMap.getKey(),docMap);
                }
                else{//If new word
                    docMap=new HashMap<>();//Make a new HashMap
                    docMap.put(entry.getKey(),wordMap.getValue());// Update it with docs and words
                    invertedIndex.put(wordMap.getKey(),docMap);
                }
            }}
        saveFiles("A:\\Index\\InvertedIndex.json",invertedIndex);

    }
    private static Object readJson(File fileName) throws Exception{//Read and Return JSON object
        FileReader reader=new FileReader(fileName);
        JSONParser jsonParser=new JSONParser();    //json parsing
        return jsonParser.parse(reader);
    }
/* This function will process each word and remove every punctuation*/
    public static String processWords(String text){//Process Words
        text= text.replaceAll("(['])", "");  //replace apostrophe with nothing lel
        text= text.replaceAll("([-])", " ");
        text= text.replaceAll("([/])", " ");
        text= text.replaceAll("([:])", " ");
        text = text.replaceAll("([^a-zA-Z0-9\\s])", "");  //replacing punctuations with space
        text = text.toLowerCase();   //all lower case
        return text;
    }

    public  void saveFiles(String fileName,Map map) throws IOException {
        objectMapper.writeValue(new File(fileName),map);//Saves the hashmap
    }
    /*Getters for All HashMap*/
    public HashMap<String,Integer> getWordId(){
        return wordID;
    }
    public HashMap<Integer,HashMap<Integer,Integer>> getforward(){
        return forwardIndex;
    }
    public HashMap<Integer,String> getdocID(){
        return docID;
    }
    public HashMap<Integer,HashMap<Integer,Integer>> getInvert(){
        return invertedIndex;
    }
}

