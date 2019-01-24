package uk.ac.cam.spv28.oop.tick5;

public abstract class World implements Cloneable{

    private int generation;

    private Pattern pattern;

    public World(String patterninput)throws PatternFormatException{
        pattern = new Pattern(patterninput);
        generation = 0;
    }

    //copy constructor
    public World(World w){
        this.pattern = w.pattern;
        this.generation = w.generation;
    }

    public World(Pattern patterninput) throws PatternFormatException{
       this.pattern = patterninput;
       generation = 0;
    }

    public int getWidth(){
        return pattern.getWidth();
    }

    public int getHeight()
    {
        return pattern.getHeight();
    }

    public int getGenerationCount(){
        return generation;
    }

    protected void incrementGenerationCount(){
        generation++;
    }

    protected Pattern getPattern(){
        return pattern;
    }

    public void nextGeneration()
    {
        nextGenerationImpl();
        incrementGenerationCount();
    };

    @Override
    public Object clone (){
        try{return super.clone();}
        catch(CloneNotSupportedException e){return null;}
    }

    protected abstract void nextGenerationImpl();

    public abstract boolean getCell(int col, int row);

    public abstract void setCell(int col, int row, boolean val);

    protected int countNeighbours(int col, int row){
        //counts the number of neighbours to the given cell and returns it
        int Total = 0;
        Total = getCell(col-1,row-1)? ++Total : Total;
        Total = getCell(col,row-1)? ++Total : Total;
        Total = getCell(col+1,row-1)? ++Total : Total;
        Total = getCell(col-1,row)? ++Total : Total;
        Total = getCell(col+1,row)? ++Total : Total;
        Total = getCell(col-1,row+1)? ++Total : Total;
        Total = getCell(col,row+1)? ++Total : Total;
        Total = getCell(col+1,row+1)? ++Total : Total;
        return Total;
    }

    protected boolean computeCell( int col, int row){
        // liveCell is true if the cell at position (col,row) in world is live
        boolean liveCell = getCell(col, row);

        // neighbours is the number of live neighbours to cell (col,row)
        int neighbours = countNeighbours(col, row);
        //System.out.println(neighbours);
        boolean nextCell =false;
        //System.out.println(neighbours);
        //A live cell with less than two neighbours dies (underpopulation)
        if (liveCell && neighbours < 2) {//A live cell with two or three neighbours lives (a balanced population)
            nextCell = false;				//2 stays alive if alive already... see 3 below
        }
        else if(liveCell & neighbours == 2){
            nextCell = true;
        }
        //A live cell with with more than three neighbours dies (overcrowding)
        else if(neighbours > 3){
            nextCell = false;
        }
        //A dead cell with exactly three live neighbours comes alive
        // 3 live neighbours guarantees
        else if(neighbours == 3){
            nextCell = true;
        }
        return nextCell;
    }
}