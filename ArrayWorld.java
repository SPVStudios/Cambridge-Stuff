package uk.ac.cam.spv28.oop.tick5;
import java.io.*;

public class ArrayWorld extends World implements Cloneable{

    private boolean[][] world;

    private boolean[] deadRow;//a row of false values to save space in memory(we don't need loads of these)

    public ArrayWorld(String serial)  throws PatternFormatException {
        super(serial);
        // initialise world
        world = new boolean[getHeight()][getWidth()];
        deadRow = new boolean[getWidth()];//auto-initialised to FALSE
        getPattern().initialise(this);
    }

    public ArrayWorld(Pattern p) throws  PatternFormatException {
        super(p);
        world = new boolean[getPattern().getHeight()][getPattern().getWidth()];
        deadRow = new boolean[getPattern().getWidth()];//auto-initialised to FALSE
        getPattern().initialise(this);
    }

    @Override
    public Object clone(){
        ArrayWorld c = (ArrayWorld)super.clone();
        c.deadRow = this.deadRow;

        for(int R = 0; R < getHeight(); R++) {
            boolean allFalse = true;
            for (int col = 0; col < getWidth(); col++) {
                if (world[R][col]) {
                    allFalse = false;
                    break;
                }
            }
            if (allFalse) {
                c.world[R] = this.deadRow;
            }
            else {
                for (int C = 0; C < getWidth(); C++) {
                    c.setCell(C,R, this.world[R][C]);
                }
            }
        }
        //check
        for(int x = 0; x < getHeight();x++){
            for(int y = 0; y < getWidth(); y++){
                if(c.getCell(x,y) != this.getCell(x,y)){
                    System.out.println("nope");
                }
            }
        }
        return c;
    }


    //copy constructor
    public ArrayWorld(ArrayWorld w) {
        super((World)w);
        this.deadRow = w.getDeadRow();
        this.world = new boolean[w.getHeight()][w.getWidth()];
        for(int R = 0; R < w.getHeight(); R++){
            boolean allFalse = true;
            for(int col = 0; col < w.getWidth(); col++){
                if(w.getCell(col,R)){
                    allFalse = false;
                    break;
                }
            }
            if(allFalse){
                this.world[R] = this.deadRow;
            }
            else{
                for(int C = 0; C < getWidth(); C++){
                    this.world[R][C] = w.getCell(C,R);
                }
            }
        }
    }

    public boolean[] getDeadRow(){
        return deadRow;
    }

    @Override
    protected void nextGenerationImpl()
    {
        int width = getPattern().getWidth();
        int height = getPattern().getHeight();
        boolean[][] next = new boolean[height][width];
        //use ComputeCell on a loop to process all of the cells in the screen.

        for(int r = 0; r < height; r++){
            for(int c = 0; c < width; c++){
                next[r][c] = computeCell(c, r);
            }
        }
        world = next;
    }

    @Override
    public  boolean getCell(int col, int row)
    {
        if (row < 0 || row > getPattern().getHeight() - 1) return false;
        if (col < 0 || col > getPattern().getWidth() - 1) return false;
        return world[row][col];
    }

    @Override
    public void setCell(int col, int row, boolean val)
    {
        world[row][col] = val;
    }
}