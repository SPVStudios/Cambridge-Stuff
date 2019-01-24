package uk.ac.cam.spv28.oop.tick5;

public class Pattern implements Comparable<Pattern> {

    private String name;
    private String author;
    private int width;
    private int height;
    private int startCol;
    private int startRow;
    private String cells;


    public String getName() {
        return name;
    }

    public String getAuthor(){
        return author;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getStartCol(){
        return startCol;
    }

    public int getStartRow(){
        return startRow;
    }

    public String getCells(){
        return cells;
    }

    @Override
    public String toString(){
    return name+" ("+author+")";
    }

    //public static void main (String[] args){
    //    String format = args[0];
    //    Pattern p = new Pattern(format);
//
    //    System.out.printf("Name: %s %nAuthor: %s %nWidth: %d %nHeight: %d %nStartCol: %d %nStartRow: %d %nPattern: %s",
     //           p.getName(),p.getAuthor(),p.getWidth(),p.getHeight(),p.getStartCol(),p.getStartRow(),p.getCells());
    //}



    public Pattern(String format) throws PatternFormatException {

        if(format.equals("")){
        throw new PatternFormatException("Please specify a pattern.");
        }
        String[] params = format.split(":");
        if(params.length != 7)
        {
            throw new PatternFormatException("Invalid pattern format: Incorrect number of fields in pattern (found "+ String.valueOf(params.length)+").");
        }

        name = params[0];
        author = params[1];
        int errorcheckcase = 0;
        try{
            width = Integer.parseInt(params[2]);
            errorcheckcase++;
            height = Integer.parseInt(params[3]);
            errorcheckcase++;
            startCol = Integer.parseInt(params[4]);
            errorcheckcase++;
            startRow = Integer.parseInt(params[5]);
        }
        catch(NumberFormatException e){
            String extraword = "";
            String wrongInput = String.valueOf(params[errorcheckcase+2]);
            switch(errorcheckcase){
                case 0:
                    extraword = "width";
                    break;
                case 1:
                    extraword = "height";
                    break;
                case 2:
                    extraword = "startX";
                    break;
                case 3:
                    extraword = "startY";
                    break;
            }
            throw new PatternFormatException("Invalid pattern format: Could not interpret the "+extraword+" field as a number ('" + wrongInput + "a' given).");
        }
        cells = params[6];
    }

    public void initialise(World world) throws PatternFormatException {
        //pre-emptive error check
        for(char c : cells.toCharArray()){
            if(Character.isDigit(c)){
                int test =  Character.getNumericValue(c);
                if(test == 0 || test == 1){
                    continue;
                }
            }
            else if(c == ' '){
                continue;
            }
            throw new PatternFormatException("Invalid pattern format: Malformed pattern '"+ cells+"'.");
        }


        String[] cellRows = cells.split(" ");

        for(int row = 0;  row < cellRows.length;row++)
        {

            char[] values = cellRows[row].toCharArray();
            for(int col = 0; col < values.length; col++){
                if(Character.getNumericValue(values[col]) == 1){
                    world.setCell(getStartCol() + col, getStartRow() + row, true);

                }
            }
        }
    }

    @Override
    public int compareTo(Pattern o) {
        return name.compareTo(o.getName());
    }

}