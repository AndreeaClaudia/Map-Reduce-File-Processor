import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

public class MyMapTask implements Runnable {
    private final String path;
    private final int offset;
    private final int dimension;

    public MyMapTask(String path, int offset, int dimension) {
        this.path = path;
        this.offset = offset;
        this.dimension = dimension;
    }

    @Override
    public void run() {
        //the separators
        String separators = ";:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"| \t\r\n";
        try {
            int maximLen = 0;
            ArrayList<String> listaCuvLungi = new ArrayList<>();

            //we read the file
            File input = new File(path);
            RandomAccessFile reader = new RandomAccessFile(input,"r");

            //starting from the offset
            reader.seek(offset);

            StringBuffer fragment = new StringBuffer();
            StringBuffer newfragment = new StringBuffer();

            //we read the fragment
            for(int i = 0; i < dimension; i++)
            {
                fragment.append((char) reader.readByte());
            }

            /* we try to search byte by byte going to the left to find
            a separator to check if we start at the middle of a word */
            int newOffsetL = offset;
            while(newOffsetL > 0)
            {
                reader.seek(newOffsetL);
                char ceva = (char) reader.readByte();
                String litera = new String(String.valueOf(ceva));
                if(separators.contains(litera))
                    break;
                newOffsetL--;
            }

            if(newOffsetL < 0)
                newOffsetL = offset;

            /* we try to search byte by byte going to the right to find
            a separator to check if we end the fragment at the middle of a word
            so we can check that one too */
            int newOffsetR = offset + dimension - 1;
            while(newOffsetR < input.length())
            {
                reader.seek(newOffsetR);
                char ceva = (char) reader.readByte();
                String litera = new String(String.valueOf(ceva));
                if(separators.contains(litera))
                    break;
                newOffsetR++;
            }

            //now we read the fragment with the new offsets
            reader.seek(newOffsetL);
            for(int i =0;i < newOffsetR - newOffsetL;i++)
            {
                newfragment.append((char) reader.readByte());
            }

            //we split both fragments into tokens
            String initialfragment = fragment.toString();
            String[] initialTokens = initialfragment.split("[ ;:/?~\\\\.,><`\\[\\]{}()!@#$%^&\\-_+'=*\"|\t\n\r]");
            String laterfragment = newfragment.toString();
            String[] newTokens = laterfragment.split("[ ;:/?~\\\\.,><`\\[\\]{}()!@#$%^&\\-_+'=*\"|\t\n\r]");

            //we store the values in a HashMap
            HashMap<Integer, Integer> dictionar = new HashMap<>();

            /*  we have empty tokens so i check the first words
                in both fragments */
            int p, m;
            for(p = 0; p < initialTokens.length; p++)
            {
                if(initialTokens[p].length() != 0 ) break;
            }
            for(m = 0; m < newTokens.length; m++)
            {
                if(newTokens[m].length() != 0 ) break;
            }

            /*  if the first word of the initial fragment is the same as the
                first word in the fragment in which we check for another word
                that comes right before the fragment then we did not start at the
                middle of the word */
            if(initialTokens[p].equals(newTokens[m]) && newTokens[m].length() != 0)
            {
                /* we take into consideration this first word and we
                   put it into the dictionary */
                if(dictionar.containsKey(newTokens[m].length()))
                {
                    dictionar.put(newTokens[m].length(),dictionar.get(newTokens[m].length()) + 1);
                }
                else
                {
                    dictionar.put(newTokens[m].length(), 1);
                }

                if(newTokens[m].length() > maximLen)
                {
                    maximLen = newTokens[m].length();
                }
            }
            //then we check the other tokens
            m++;
            for (; m < newTokens.length; m++)
            {
                //we only store the non-empty words
                if(newTokens[m].length() != 0 ) {
                    if(dictionar.containsKey(newTokens[m].length()))
                    {
                        dictionar.put(newTokens[m].length(),dictionar.get(newTokens[m].length()) + 1);
                    }
                    else
                    {
                        dictionar.put(newTokens[m].length(), 1);
                    }
                    //we also compute the maximum lenght of the words
                    if(newTokens[m].length() > maximLen)
                    {
                        maximLen = newTokens[m].length();
                    }
                }
            }
            //we add the longest words to the list
            for(String s : newTokens)
            {
                if(s.length() != 0 && s.length() == maximLen)
                {
                    listaCuvLungi.add(s);
                }
            }

            //we put an entry to the results HashMap
            synchronized (Tema2.resultsMap) {
                //we add them by the file name
                if (Tema2.resultsMap.containsKey(path)) {

                    ArrayList<HashMap<Integer, Integer>> dictionare = Tema2.resultsMap.get(path).dictionaries;
                    dictionare.add(dictionar);

                    ArrayList<String> words = Tema2.resultsMap.get(path).lists;
                    words.addAll(listaCuvLungi);

                    Tema2.resultsMap.put(path, new ReturnMap(dictionare, words));
                } else {
                    ArrayList<HashMap<Integer, Integer>> dictionare = new ArrayList<HashMap<Integer, Integer>>();
                    dictionare.add(dictionar);

                    Tema2.resultsMap.put(path, new ReturnMap(dictionare, listaCuvLungi));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}