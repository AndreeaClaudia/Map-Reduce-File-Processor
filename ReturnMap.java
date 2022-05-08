import java.util.ArrayList;
import java.util.HashMap;

public class ReturnMap {
    public  ArrayList< HashMap<Integer,Integer>> dictionaries = new ArrayList<HashMap<Integer, Integer>>();
    public ArrayList<String> lists = new ArrayList<>();

    public ReturnMap(ArrayList< HashMap<Integer,Integer>> dictonary, ArrayList<String> list)
    {
        dictionaries = dictonary;
        lists = list;
    }

    public ArrayList<HashMap<Integer, Integer>> getDictionaries() {
        return dictionaries;
    }
}
