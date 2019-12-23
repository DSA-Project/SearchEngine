import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Stage;
import opennlp.tools.stemmer.PorterStemmer;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Search {

private  HashMap<String, Integer> wordID;
private  HashMap<Integer, String> docID;
private  HashMap<Integer, HashMap<Integer, Integer>> invertedIndex;
private ObjectMapper mapper = new ObjectMapper();
public ArrayList<Integer> result = new ArrayList<>();
public NumberFormat formatter = new DecimalFormat("#0.00000");
/*
Constructor used to load the hashmaps in this class aswell so they can be used to search , only the relevant
HashMaps are loaded
 */
    Search(HashMap<String,Integer> wordID,HashMap<Integer,String> docID,HashMap<Integer,HashMap<Integer,Integer>> invertedIndex){
        this.wordID=wordID;
        this.docID=docID;
        this.invertedIndex=invertedIndex;
    }


    public ArrayList<String> Search(String text, Stage primaryStage) throws IOException, InterruptedException {
        /*
        This function takes string , splits them and searches for each word , then gets intersection
        for all the words then unions them with the remaining words one by one
         */
        result.clear();
        long end;
        long start=System.currentTimeMillis();
        PorterStemmer stemmer=new PorterStemmer();
        text = Engine.processWords(text);

        ArrayList<String> words = new ArrayList<>();
        Matcher m = Pattern.compile("[a-zA-Z0-9]+").matcher(text);//Seperates words based on pattern

        while (m.find()) //This will iterate as long as there are words inside Matcher
        {   String stem = stemmer.stem(m.group());
            words.add(stem);
        }
//Doc keys to retrieve every doc containing said word.
                ArrayList<Integer> docKeys;
                docKeys = query(words);
                result.addAll(docKeys);
                end=System.currentTimeMillis();
                System.out.println("Execution time in searching " + formatter.format((end - start) / 1000d) + " seconds\n");
                return RetreiveDoc(result);
    }
/* Functions to
retrieve docs and query the invertedList it is O(1)
 */
    public ArrayList<String> RetreiveDoc(ArrayList<Integer> docs)
    {       ArrayList<String> billi = new ArrayList<String>();
            for(int i=0;i<docs.size();i++) {
                billi.add(docID.get(docs.get(i)));
            }
            return billi;
    }

    public ArrayList<Integer> query(ArrayList<String> words) throws IOException {
        ArrayList<HashMap<Integer,Integer>> temp = new ArrayList<>();

        for (int i = 0; i < words.size(); i++) {

            temp.add(oneWord(words.get(i)));
        }
        HashMap<Integer,Integer> result = new HashMap<>(temp.get(0));
        for(int i=1;i<words.size();i++){
            result.keySet().retainAll(temp.get(i).keySet());
        }
        for (Map.Entry mapElement : result.entrySet()) {
            int sum =0;
            Integer key = (Integer)mapElement.getKey();
            for(int i=0;i<temp.size();i++)
                sum+=temp.get(i).get(key);
            result.replace(key, sum);
        }
        result = sortByValue(result);
        Set<Integer> keySet = result.keySet();
        ArrayList<Integer> listofDoc = new ArrayList<Integer>(keySet);

        return listofDoc;
    }
/*
Functions to query for one word and sort the results by their page rank
 */

    public HashMap<Integer,Integer> oneWord(String word) throws IOException {
        //add condition if word does not exist
        Integer key = wordID.get(word); //load from lexicon file
        HashMap<Integer, Integer> docwithlist;
        docwithlist = invertedIndex.get(key);// hashmap contains doc against list (freq,pos), loaded from file
        return docwithlist;
    }

    public  HashMap<Integer,Integer> sortByValue(HashMap<Integer,Integer> m)
    {
        Map<Integer, Integer> unSortedMap = m;
        LinkedHashMap<Integer, Integer> reverseSortedMap = new LinkedHashMap<>();
        unSortedMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        return  reverseSortedMap;
    }

}
