package uk.ac.cam.spv28.oop.tick5;

import java.awt.Color;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

    private World world = null;


    @Override
    protected void paintComponent(java.awt.Graphics g) {
        // Paint the background white
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        if(world!= null) {






            int numPixY = world.getHeight();
            int numPixX = world.getWidth();
            int pixWid =  this.getWidth() / numPixX ;
            int pixHei =  this.getHeight() / numPixY ;

            int pixSize;
            int gridWidth;
            int gridHeight;

            if(pixWid < pixHei){
                pixSize = pixWid;
                gridWidth = this.getWidth();
                gridHeight = (int)Math.floor(gridWidth * ((float)numPixY/numPixX));
            }
            else{
                pixSize = pixHei;
                gridHeight = this.getHeight();
                gridWidth = (int)Math.floor(gridHeight * ((float)numPixX/numPixY));
            }
            int extraX = gridWidth - (pixSize*numPixX);
            int extraY = gridHeight - (pixSize*numPixY);



            //System.out.println("- " + world.getGenerationCount());
            float errorY = 0;
            int ConeY = 0;//consumed extra Y pixels, to keep the future rows incremented as well
            for (int row = 0; row < numPixY; row++) {
                float errorX = 0;
                int ConeX = 0;//consumed extra X pixels, to keep the future coluumns incremented as well
                errorY+= (float)extraY/numPixY;
                int eY = 0;
                if(errorY >=1){
                    eY=1;
                    errorY-=1;
                }
                for (int col = 0; col < numPixX; col++) {
                    errorX+= (float)extraX/numPixX;
                    int eX = 0;
                    if(errorX >= 1){
                        eX=1;
                        errorX-=1;
                    }

                    if (world.getCell(col, row)) {
                        g.setColor(Color.BLACK);
                        g.fillRect(col * pixSize + ConeX, row * pixSize + ConeY, pixSize + eX, pixSize+eY);
                    }
                    g.setColor(java.awt.Color.LIGHT_GRAY);
                    g.drawRect(col * pixSize + ConeX, row * pixSize + ConeY, pixSize + eX, pixSize+eY);

                    ConeX += eX;
                }
                ConeY += eY;
            }


            g.setColor(java.awt.Color.BLACK);
            g.drawString("Generation: " + world.getGenerationCount(), 10, this.getHeight() - 20);
        }

        // Sample drawing statements
//        g.setColor(Color.BLACK);
//        g.drawRect(200, 200, 30, 30);
//        g.setColor(Color.LIGHT_GRAY);
//        g.fillRect(140, 140, 30, 30);
//        g.fillRect(260, 140, 30, 30);
//        g.setColor(Color.BLACK);
//        g.drawLine(150, 300, 280, 300);
//        g.drawString("@@@", 135,120);
//        g.drawString("@@@", 255,120);
    }

    public void display(World w) {
        world = w;
        repaint();
    }
}