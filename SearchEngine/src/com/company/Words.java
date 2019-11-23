package com.company;

import java.util.HashMap;

public class Words {
    HashMap<String,Integer> wordList=new HashMap<>();//HashMap for Word,Count
    int frequency;
    Words(){
        this.frequency=1;
    }
    public void setHash(String words){


        if(!wordList.containsKey(words)){//If hashmap doesn't contain word , it will add into hashmap
            wordList.put(words,frequency);
        }
        else{
            wordList.put(words,wordList.get(words)+1);//If hashmap already contains then it will simply increment the frequency
        }
    }
    public HashMap<String,Integer> getHash(){//Returns the hashmap
        return wordList;
    }

}
