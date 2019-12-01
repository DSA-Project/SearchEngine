import java.util.ArrayList;
import java.util.HashMap;

public class Words {

    private HashMap<String, ArrayList<Integer>> wordList = new HashMap<String, ArrayList<Integer>>();//HashMap for Word,Count
    int frequency;
    int position = 0;
    ArrayList<Integer> list = new ArrayList<Integer>();

    Words() {
        this.frequency = 1;
    }

    public void setHash(String words) {
        position++;
        if (!wordList.containsKey(words))//If hashmap doesn't contain word , it will add into hashmap
        {
            list = new ArrayList<Integer>();
            list.add(position);
            wordList.put(words, list);
        } else {

            list = wordList.get(words);
            list.add(position);
            wordList.put(words, list);//If hashmap already contains then it will simply increment the frequency
        }
    }

    public HashMap<String, ArrayList<Integer>> getHash() {//Returns the hashmap
        return wordList;
    }
}