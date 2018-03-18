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
            //Find how
            System.out.println(infile.length());
            int bytesPerK = (int) infile.length() / k;

            //List of fdi
            files = new ArrayList<File>();
            WrappedArray wrappedArray = new WrappedArray(bytesPerK);
            try {

               FileInputStream in =  new FileInputStream(infile);
                int chr;
                File tempFile = File.createTempFile(tempFileName,  ".runs");
                files.add(tempFile);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));

                //Splits into runs based on predetermined runs or the length, whatever comes first. Using EOT for delimiter between runs
                StringBuffer temp = new StringBuffer();
                while ((chr = in.read())  != -1){
                    //If we reach a new run or
                    if(chr == '\4' || wrappedArray.wouldOverflow(temp.length())){//Delimited
                        wrappedArray.quickSort();

                        //Write to file
                        for (String s: wrappedArray.toInner()) {
                            os.write(s.getBytes(), 0, s.length());
                        }
//                        os.write(wrappedArray.toInner(),0, wrappedArray.size());
                        os.flush();
                        os.close();
                        //Iterate counter
                        runCount++;
                        //Reset writer
                        tempFile = File.createTempFile(tempFileName,  ".runs");
                        files.add(tempFile);
                        os = new BufferedOutputStream(new FileOutputStream(tempFile));
                        wrappedArray = new WrappedArray(bytesPerK);
                        //No need to write EOT
//                        continue;
                    }
                    if(chr == 32 || chr == '\r' || chr == '\n'){
                        wrappedArray.add(temp.toString() + (char) chr);
                        temp = new StringBuffer();
                    } else {
                        temp.append((char) chr);
                    }

                }
                //We're done
                os.flush();
                os.close();
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
