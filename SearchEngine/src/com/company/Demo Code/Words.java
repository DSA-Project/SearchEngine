

import java.util.ArrayList;
import java.util.HashMap;

public class Words {

    private HashMap<Integer,Integer> wordList=new HashMap<Integer,Integer>();//HashMap for Word,Count
    private int frequency,total;

    int position=0;//Position of word In document
    ArrayList<Integer> list=new ArrayList<Integer>();// ArrayList to store position and frequency

    Words(Integer shares,Integer perf)
    {
        this.total=shares+perf;
    }
/*
Our page rank algorithm is pagerank=frequency*5 + shares/100+performancerank*10
and depending on position of word the pagerank increases
for 50 to 100 pagerank+=4
for 100 to 150 pagerank+=2
for 150 to 250 pagerank+=1
 */

    public void setHash(Integer words){
        position++;
        int total=this.total;
        if(!wordList.containsKey(words))//If hashmap doesn't contain word , it will add into hashmap
        {
            total+=5;
            if(position<5){
                total+=100;
            }
           else if (position<50 && position>5)
                total+=5;
            else if (position >50 && position<100)
                    total+=4;
                else if (position>100 && position<250)
                    total+=2;
                else if (position>250 && position<500)
                    total+=1;
            wordList.put(words,total);

        }
        else
        {
            int total1=wordList.get(words);
            total1+=5;
            if(position<5){
                total1+=100;
            }
            else if (position<50 && position>5)
                total1+=5;
            else if (position >50 && position<100)
                total1+=4;
            else if (position>100 && position<250)
                total1+=2;
            else if (position>250 && position<500)
                total1+=1;

            wordList.put(words,total1);//If hashmap already contains then it will simply increment the frequency
        }
    }
    public HashMap<Integer, Integer> getHash(){//Returns the hashmap
        return wordList;
    }
}
