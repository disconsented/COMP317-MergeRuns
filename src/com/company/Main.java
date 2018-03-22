package com.company;

import java.io.*;
//import java.nio.file.Files;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Main {

    public static final String tempFileName = "tempFile";

    public static void main(String[] args) {

        Comparator<File> lengthComparator = new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                if (file.length() > t1.length()) {
                    return 1;
                } else if (file.length() < t1.length()) {
                    return -1;
                } else {
                    return 0;
                }

            }
        };

        PriorityQueue<File> files = new PriorityQueue<File>(lengthComparator);
        if (args.length == 2) {
            File infile = new File(args[0]);
            if (!infile.canRead()) {
                System.out.println("Please input a readable file");
                return;
            }


            int k = Integer.parseInt(args[1]);
            //Find how
            System.out.println(infile.length());
//            int bytesPerK = (int) infile.length() / k;
            int bytesPerK = (int) infile.length();

            WrappedArray wrappedArray = new WrappedArray(bytesPerK);
            try {


                BufferedReader in = new BufferedReader(new FileReader(infile));
                String line;
                File splitFile = File.createTempFile(tempFileName, ".tmp");
                OutputStream os = new BufferedOutputStream(new FileOutputStream(splitFile));

                //Splits into runs based on predetermined runs or the length, whatever comes first. Using EOT for delimiter between runs


                while ((line = in.readLine()) != null) {
                    //If we reach a new run or
                    if (line.contains("\4") || wrappedArray.wouldOverflow(line.length())) {//Delimited

                        wrappedArray.quickSort();

                        //Write to file
                        for (String s : wrappedArray.toInner()) {
                            os.write(s.getBytes(), 0, s.length());
                        }

                        os.flush();
                        os.close();
                        //Reset writer
                        files.add(splitFile);
                        splitFile = File.createTempFile(tempFileName, ".tmp");
                        os = new BufferedOutputStream(new FileOutputStream(splitFile));
                        wrappedArray = new WrappedArray(bytesPerK);
                    }
                    wrappedArray.add(line + "\r\n");

                }
                //Sort and output the last one
                wrappedArray.quickSort();
                for (String s : wrappedArray.toInner()) {
                    os.write(s.getBytes(), 0, s.length());
                }
                files.add(splitFile);
                //We're done
                os.flush();
                os.close();
                in.close();


//                //Open 2, merge
//                int runs = files.size();
//                File lastFile = new File("NOFILEWASCREATED");
//
//                if(files.size() > k){
//
//                }
//
//                while (!files.isEmpty()){
//                    //Poll to make sure its sorted in the right order
//                    File f1 = files.poll();
//                    File f2 = files.poll();
//
//                    //If any of those return null then we're at the end
//                    if(f1 == null || f2 == null)
//                        break;
//                    runs++;
//                    lastFile = File.createTempFile(tempFileName,  ".runs");
//                    files.add(lastFile);
//                    PriorityQueue<String> lineHeap = new PriorityQueue<String>();
//
//                    BufferedReader in1 =  new BufferedReader(new FileReader(f1));
//                    BufferedReader in2 =  new BufferedReader(new FileReader(f2));
//                    String output;
//                    try{
//                        while (in1.ready()){
//                            lineHeap.add(in1.readLine());
//                        }
//                    } catch (Exception e){
//                        //Java's error model is dumb
//                    }
//
//                    try{
//                        while (in2.ready()){
//                            lineHeap.add(in2.readLine());
//                        }
//                    } catch (Exception e) {
//                        //Java's error model is dumb
//                    }
//
//
//
//                    BufferedOutputStream outputStream =  new BufferedOutputStream(new FileOutputStream(splitFile));
//                    while (lineHeap.size() > 0){
//                        outputStream.write(lineHeap.poll().getBytes());
//                    }
//                    outputStream.flush();
//                    outputStream.close();
//                    in1.close();
//                    in2.close();
//                }
                mergeRuns(files, k);
                System.out.println("done");
//                String finalName = args[0].substring(0, args[0].lastIndexOf("."))+"sorted";
//                System.out.println(lastFile.renameTo(new File(finalName)));
//                System.out.println(lastFile.getAbsolutePath());


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(files.size());
        } else {
            System.out.println("Please input the right parameters ([string fileName] [int runs])");
        }

    }

    public static void mergeRuns(PriorityQueue<File> files, int k) {
        int runs = 0;
        while (files.size() > 1) {
            runs++;
            ArrayList<PriorityQueue<String>> mergedRuns = new ArrayList<>();
            int toMerge = k > files.size() ? files.size() : k;
            for (int i = 0; i <= toMerge; i++) {
                File infile = files.poll();
                PriorityQueue<String> minHeap = new PriorityQueue<String>();
                try {
                    BufferedReader in = new BufferedReader(new FileReader(infile));
                    while (in.ready()) {
                        String line = in.readLine();
                        if (!line.equals("\4"))
                            minHeap.add(line);
                    }

//                    in.lines().forEach(s -> minHeap.add(s));
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Just incase we get an empty file
                if (minHeap.size() > 0)
                    mergedRuns.add(minHeap);
            }
            try {
                //New merged file needs to be thrown back in the queue for later
                File splitFile = File.createTempFile(tempFileName + "Part", ".runs");
                files.add(splitFile);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(splitFile));
                //Whilst we still have files left to merge
                String smallest = null;
                while (mergedRuns.size() > 0) {
                    //Find the 'smallest' file
                    //Scan though once to find the smallest string
                    PriorityQueue<String> lastRun = null;
                    for (PriorityQueue<String> run : mergedRuns) {
                        if (smallest == null || run.peek().compareTo(smallest) < 0) {
                            smallest = run.peek();
                            lastRun = run;
                        }
                    }
                    //Cache to poll the string out then remove from list if needed
                    smallest = lastRun.poll();

                    if (lastRun.size() == 0)
                        mergedRuns.remove(lastRun);

                    //If there is nothing left remove it

                    if (smallest != null)
                        os.write((smallest + "\r\n").getBytes());
                    smallest = null;
                }
                os.flush();
                os.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //Rename the final file
        File finalFile = files.poll();
        String finalName = finalFile.getName().substring(0, finalFile.getName().lastIndexOf(".")) + ".sorted";
        try {
            Files.move(finalFile.toPath(), finalFile.toPath().resolveSibling(finalName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO: Print to debug
        System.out.println("Runs: " + runs);
    }
}
