package com.company;

import java.io.*;
//import java.nio.file.Files;
import java.util.ArrayList;

public class Main {

    public static final String tempFileName = "tempFile";
    public static void main(String[] args) {
        int runCount = 0;
        ArrayList<File> files;
        if(args.length == 2){
            File infile  = new File(args[0]);
            if(!infile.canRead()){
                System.out.println("Please input a readable file");
                return;
            }


            int k = Integer.parseInt(args[1]);
             files = new ArrayList<File>(k);
            WrappedArray wrappedArray = new WrappedArray(k);
//            Files.re
            try {

               FileInputStream in =  new FileInputStream(infile);
                int chr;
                File tempFile = File.createTempFile(tempFileName,  ".runs");
                files.add(tempFile);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));

                int bufferIndex = 0;
                while ((chr = in.read())  != -1){
                    if(chr == '\4' || wrappedArray.isFull()){//Delimited
                        wrappedArray.quickSort();

                        //Write to file
                        os.write(wrappedArray.toInner(),0, wrappedArray.size());
                        os.flush();
                        os.close();
                        //Iterate counter
                        runCount++;
                        //Reset writer
                        tempFile = File.createTempFile(tempFileName,  ".runs");
                        files.add(tempFile);
                        os = new BufferedOutputStream(new FileOutputStream(tempFile));
                        wrappedArray = new WrappedArray(k);
                        continue;
                    }
                    //Read until limit
                    wrappedArray.add((byte)chr);
                    //Sort TODO: Quicksort
                    //Output
                }
                //We're done
                in.close();





//                File tempFile = ;
//
//                for (int i = 0; i < k; i++) {
//                    int chr = in.read();
//                    if(chr == -1){
//                        //EOF
//                        os.close();
//                        break;
//                    } else if(chr == '\4'){//EOT/Delimiter
//                        os.close();
//                        runCount++;
//                        tempFile = File.createTempFile(tempFileName+runCount,  "runs");
//                        os = new BufferedOutputStream(new FileOutputStream(tempFile));
//                    } else {
//                        runList.add(chr);
//                    }
//                }
//
//
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            String property = "java.io.tmpdir";
//            String tempDir = System.getProperty(property);

//            File.createTempFile()
        } else {
            System.out.println("Please input the right parameters ([string fileName] [int runs])");
        }

        System.out.println(runCount);
    }
}
