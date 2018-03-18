package com.company;

public class WrappedArray{
    private byte[] buffer;
    private int count;
    public WrappedArray(int size){
        buffer = new byte[size];
    }
    public void quickSort(){

    }
    public void add(byte b){
        if(count == buffer.length){
            throw new IndexOutOfBoundsException();
        } else {

            buffer[count] = b;
            count++;
        }
    }
    public byte[] toInner(){
        return buffer;
    }

    public boolean isFull(){
        return count == buffer.length;
    }

    public int size(){
        return count;
    }
}