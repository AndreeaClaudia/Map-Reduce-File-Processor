import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class MyReduceTask implements Runnable {
    private String path;
    private ReturnMap values;
    private final FileWriter outPath;

    public MyReduceTask(String path, ReturnMap values, FileWriter outPath) {
        this.path = path;
        this.values = values;
        this.outPath = outPath;
    }

    @Override
    public void run() {
        //we get the results of the Map tasks
        ArrayList<HashMap<Integer, Integer>> dictionaries;
        synchronized (Tema2.resultsMap) {
             dictionaries = Tema2.resultsMap.get(path).getDictionaries();
        }

        int numberWords = 0;
        int longestWords = 0;
        int maxLength = 0;
        int rang = 0;

        //we first iterate through all the dictionaries
        for(HashMap<Integer,Integer> dictionar : dictionaries)
        {
            //and get the lengths
            Integer[] lungimi = dictionar.keySet().toArray(new Integer[0]);

            for(int l : lungimi)
            {
                //we compute the number of words, the ranks and the maximum length
                numberWords += dictionar.get(l);
                rang += fib(l + 1) * dictionar.get(l);
                if(maxLength < l)
                {
                    maxLength = l;
                }
            }
        }
        //and now we compute the number of words of maximul length
        for(HashMap<Integer,Integer> dictionar : dictionaries)
        {
            Integer[] lungimi = dictionar.keySet().toArray(new Integer[0]);
            for(int l : lungimi)
            {
                if(l == maxLength)
                {
                    longestWords += dictionar.get(l);
                }
            }
        }

        //we compute the rank and add the final results in the ArrayList
        float rangf = (float) rang / numberWords;
        synchronized (Tema2.restultsReduce)
        {
            Tema2.restultsReduce.add(new ReturnReduce(path, rangf, maxLength, longestWords));
        }
    }

    //the fibonacci function
    static int fib(int n)
    {
        if (n <= 1)
            return n;
        return fib(n-1) + fib(n-2);
    }
}

