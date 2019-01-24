package uk.ac.cam.spv28.oop.tick5;

import java.io.*;

public class PackedWorld extends World implements Cloneable{

    private long world;

    public PackedWorld(String serial) throws java.io.IOException, PatternFormatException {

        super(serial);
        if (getPattern().getHeight() * getPattern().getWidth() > 64){
            throw new java.io.IOException();
        }

        world = 0;
        getPattern().initialise(this);
    }

    public PackedWorld(Pattern p) throws  java.io.IOException, PatternFormatException{
        super(p);
        if (getPattern().getHeight() * getPattern().getWidth() > 64){
            throw new java.io.IOException();
        }

        world = 0;
        getPattern().initialise(this);
    }

    public PackedWorld(PackedWorld w){
        super(w);
        this.world = w.world;
    }

    @Override
    protected void nextGenerationImpl()
    {
        long next = 0;
        int height = getPattern().getHeight();
        int width = getPattern().getWidth();
        //use ComputeCell on a loop to process all of the cells in the screen.
        for(int r = 0; r < height; r++){
            for(int c = 0; c < width; c++){
                next = setnewLoc(next,r,c,computeCell(c, r));
            }
        }
        world = next;
    }

    @Override
    public boolean getCell(int col, int row) {
        if (row < 0 || row >= getPattern().getHeight() ) return false;
        if (col < 0 || col >= getPattern().getWidth() ) return false;

        int position = ((row * getPattern().getWidth()) + col);
        // set "check" to equal 1 if the "position" bit in "packed" is set to 1
        // you should use bitwise operators (not % or similar)
        long check = (world>>position) & 1;//TODO: complete this statement
        return (check == 1);
    }

    /*
     * Set the nth bit in the packed number to the value given
     * and return the new packed number
     */
    @Override
    public  void setCell(int col, int row, boolean value) {

        int position = ((row * getPattern().getWidth()) + col);
        if (value) {
            // TODO: complete this using bitwise operators
            // update the value "packed" with the bit at "position" set to 1
            long longOne = 1;

            world = (longOne<<position) | world;
        }
        else {
            // TODO: complete this using bitwise operators
            // update the value "packed" with the bit a "position" set to 0
            long longOne = 1;

            world = (~(longOne<<position)) & world;
        }
    }

    private long setnewLoc(long NewWorld, int Row, int Col, boolean value){
        int position = ((Row * getPattern().getWidth()) + Col);
        if (value) {
            // TODO: complete this using bitwise operators
            // update the value "packed" with the bit at "position" set to 1
            long longOne = 1;

            NewWorld = (longOne<<position) | NewWorld;
        }
        else {
            // TODO: complete this using bitwise operators
            // update the value "packed" with the bit a "position" set to 0
            long longOne = 1;

             NewWorld= (~(longOne<<position)) & NewWorld;
        }
        return NewWorld;
    }

    @Override
    public Object clone(){
        return super.clone();
    }
//    private int returnfromposition(int position){
//        long check = (world>>position) & 1;
//        if (check == 1){
//            return 1;
//        }
//        return 0;
//    }

}