package com.company;

import java.util.ArrayList;

public class WrappedArray{
    private ArrayList<String> buffer;
    //Length in bytes
    private int count;
    private int max;
    public WrappedArray(int max){
        this.max = max;
        buffer = new ArrayList<>();
    }

    public void add(String b){
        if(count + b.length() >= max){
            throw new IndexOutOfBoundsException();
        } else {
            buffer.add(b);
            count+= b.length();
        }
    }
    public ArrayList<String> toInner(){
        return buffer;
    }

    public int size(){
        return count;
    }

    public boolean wouldOverflow(int length){
        return size()+length >= max;
    }

    public void quickSort(){
        quickSort(this, 0, buffer.size()-1);
    }

    private WrappedArray quickSort(WrappedArray a, int low, int high){
        if(high > low) {

            int p = a.partition(low, high);

            quickSort(a, low, p);
            quickSort(a, p + 1, high);
        }
        return a;
    }

    private int partition (int low, int high){
        String pivot = buffer.get(low);

        int i = low - 1;
        int j = high + 1;

        while (true){
            do {
                i++;
            } while (buffer.get(i).compareTo(pivot) < 0);

            do{
                j--;
            } while (buffer.get(j).compareTo(pivot) > 0);

            if ( i >= j)
                return j;


            //Swap
            String a = buffer.get(i);
            String b = buffer.get(j);
            buffer.set(i, b);
            buffer.set(j, a);
        }
    }
}