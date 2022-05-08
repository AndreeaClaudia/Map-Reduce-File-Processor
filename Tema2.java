import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Tema2 {

    public static final HashMap<String, ReturnMap> resultsMap = new HashMap<>();
    public static final ArrayList<ReturnReduce> restultsReduce = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }
        int workers = Integer.parseInt(args[0]);
        //we start the executor service for the map tasks
        ExecutorService mapWorkers = Executors.newFixedThreadPool(workers);

        //create the output file and open it for writing
        File output = new File(args[2]);
        FileWriter writer = null;
        try {
            output.createNewFile();
            writer = new FileWriter(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //creating a scanner for reading the input file
        Scanner scanner = new Scanner(new File(args[1]));
        int fragment = scanner.nextInt();
        int document_number = scanner.nextInt();

        //making a list of the document names
        ArrayList<String> document_list = new ArrayList<>();
        for(int i = 0; i < document_number; i++)
        {
            document_list.add(scanner.next());
        }

        //we split the fragments for the workers
        for(String file : document_list)
        {
            int offset = 0;
            File f = new File(file);
            long file_size = f.length();

            while(file_size > file_size % fragment)
            {
                mapWorkers.execute(new MyMapTask(file, offset, fragment));
                file_size -= fragment;
                offset+= fragment;
            }
            mapWorkers.execute(new MyMapTask(file, offset,(int) file_size));
        }

        //we shut down the workers
        mapWorkers.shutdown();

        //we make sure that they are all shut down
        try{
            mapWorkers.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            mapWorkers.shutdownNow();
        }

        //now we open the executor service for the reduce tasks
        ExecutorService reduceWorkers = Executors.newFixedThreadPool(workers);

        for(String s : document_list)
        {
            //we give them the tasks
            reduceWorkers.execute(new MyReduceTask(s, resultsMap.get(s), writer));
        }

        //we shut them down
        reduceWorkers.shutdown();

        //we make sure they are all shut down
        try{
            reduceWorkers.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            reduceWorkers.shutdownNow();
        }

        //we sort the results of the reduce tasks by the rank
        restultsReduce.sort(new Comparator<ReturnReduce>() {
            @Override
            public int compare(ReturnReduce o1, ReturnReduce o2) {
                return Float.compare(o2.rang, o1.rang);
            }
        });

        //we write the results in the output file
        for(ReturnReduce r : restultsReduce)
        {
            File inFile = new File(r.fileName);
            writer.write(inFile.getName() + "," + String.format("%.2f", r.rang) + "," + r.maxLength + "," + r.longestWords);
            writer.write("\n");
        }
        writer.close();
    }
}




