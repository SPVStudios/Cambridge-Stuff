package uk.ac.cam.spv28.oop.tick5;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.LayoutStyle;
import javax.swing.JLayer;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.DefaultListModel;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.BoxLayout;
import uk.ac.cam.spv28.oop.tick5.GamePanel;

public class GUILife extends JFrame {

    private World world;
    private PatternStore store;
    public ArrayList<World> cachedWorlds;
    private GamePanel gamePanel;

    private JButton playButton;
    private java.util.Timer timer;
    private boolean playing;

    public GUILife(PatternStore ps) {
        super("Game of Life");
        store=ps;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024,768);
        playing = false;
        try {
            world = new ArrayWorld(store.getPatternsNameSorted().get(2));
            cachedWorlds = new ArrayList<World>();
            cachedWorlds.add(copyWorld(false));
        }
        catch(PatternFormatException pfe){
            world = null;
        }
        add(createPatternsPanel(),BorderLayout.WEST);
        add(createControlPanel(),BorderLayout.SOUTH);
        gamePanel = (GamePanel)createGamePanel();
        add(gamePanel,BorderLayout.CENTER);

    }

    private void addBorder(JComponent component, String title) {
        Border etch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border tb = BorderFactory.createTitledBorder(etch,title);
        component.setBorder(tb);
    }

    private JPanel createGamePanel()
    {
        GamePanel gPanel = new GamePanel();
        addBorder(gPanel,"Game Panel");
        gPanel.display(world);
        return gPanel;
    }

    private JPanel createPatternsPanel() {
        JPanel patt = new JPanel();
        patt.setLayout(new BorderLayout());
        addBorder(patt,"Patterns");
        List<Pattern> temp = store.getPatternsNameSorted();
        Pattern[] arrayP = temp.toArray(new Pattern[0]);
        JList<Pattern> patternList = new JList<Pattern>(arrayP);
        patternList.addListSelectionListener(e -> LoadNewPattern(
                ((JList<Pattern>) e.getSource()).getSelectedValue()));
        JScrollPane sp = new JScrollPane(patternList);
        patt.add(sp);

        return patt;
    }

    private JPanel createControlPanel() {
        JPanel ctrl =  new JPanel();
        addBorder(ctrl,"Controls");
        GridLayout lineLayout = new GridLayout(1,3);
        ctrl.setLayout(lineLayout);
        // < button
        JButton b = new JButton("< Back");
        b.addActionListener(e -> moveBack());
        ctrl.add(b);
        // The Play Button
        JButton play = new JButton("Play");
        play.addActionListener(e -> runOrPause());
        playButton = play;
        ctrl.add(play);

        //The Increment Forward Button
        JButton f = new JButton("Forward >");
        f.addActionListener(e -> moveForward());
        ctrl.add(f);
        return ctrl;
    }

    private World copyWorld(boolean useCloning) {
        if(useCloning == false){
            //use a copy constructor
            if(world instanceof ArrayWorld){
                ArrayWorld copy = new ArrayWorld((ArrayWorld)world);
                return copy;
            }
            else if(world instanceof PackedWorld){
                PackedWorld copy = new PackedWorld((PackedWorld)world);
                return copy;
            }
        }
	else{
		if(world instanceof PackedWorld){
			PackedWorld clo = PackedWorld.clone();
		}
		else{
			ArrayWorld clo = Arrayworld.clone();}
			return clo;
		}
		
	}
        return null;
    }

    private void moveBack(){
        if(world.getGenerationCount() > 0){
            world = cachedWorlds.get(world.getGenerationCount()-1);
            gamePanel.display(world);
        }
    }

    private void runOrPause() {
        if (playing) {
            timer.cancel();
            playing=false;
            playButton.setText("Play");
        }
        else {
            playing=true;
            playButton.setText("Stop");
            timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    moveForward();
                }
            }, 0, 500);
        }
    }

    private void LoadNewPattern(Pattern p){

        World newOne;
        // Based on size, create either a PackedWorld or ArrayWorld
        try {
            if (p.getWidth() * p.getHeight() <= 64) {
                //load a packedWorld
                newOne = new PackedWorld(p);
            } else {
                //load an ArrayWorld
                newOne = new ArrayWorld(p);
            }
            // Clear the cache, set world and put it into
            cachedWorlds = new ArrayList<World>();
            world = newOne;
            cachedWorlds.add(copyWorld(false));
            gamePanel.display(world);
        }
        catch(IOException | PatternFormatException e){
            System.out.println(" Error SV01: Pattern"+p.getName()+ " has caused an error in creating a new instance thereof.");
        }
        timer.cancel();
        playing=false;
        playButton.setText("Play");

    }

    private void moveForward() {
        if (world != null) {
            int gen = world.getGenerationCount();
            if (cachedWorlds.size() > gen + 1) {
                //this next world has already been generated... and this current one has already been saved
                world = cachedWorlds.get(gen+1);//this is OK as it will be changed again through the
                // copy below rather than be altered when we stop running through familiar territory
            } else {
                //generate new iteration and store copy of old one.

                world.nextGeneration();
                cachedWorlds.add(copyWorld(false));
            }
            gamePanel.display(world);
        }
    }

    public static void main (String[] args) throws IOException
        {
            PatternStore thisPS = new PatternStore("https://www.cl.cam.ac.uk/teaching/1819/OOProg/ticks/life.txt");
        GUILife gui = new GUILife(thisPS);
        gui.setVisible(true);
    }

}
/*


    public void play() throws java.io.IOException,PatternFormatException {


        while (!response.equals("q")) {

            } else if (response.startsWith("p")) {
                List<Pattern> names = store.getPatternsNameSorted();
                // Extract the integer after the p in response
                Pattern patternToPlay;
                String[] words = response.split(" ");
                int patternNum = Integer.parseInt(words[1]);//the second item, after p and " ".
                // Get the associated pattern
                if(patternNum >= 0 && patternNum < names.size()){//check the pattern number is in range
                    try{ patternToPlay = store.getPatternByName(names.get(patternNum).getName());}
                    catch(PatternNotFound e){continue;}//this is OK because given the number range filter above, a pattern
                    //can be guaranteed to be found.
                }
                else{
                    System.out.println("Please enter a valid number.");
                    continue;
                }
                //  Initialise world using PackedWorld or ArrayWorld base
                // on pattern world size
                if(patternToPlay != null) {
                    if (patternToPlay.getHeight() * patternToPlay.getWidth() <= 64) {
                        world = new PackedWorld(patternToPlay);
                    } else {
                        world = new ArrayWorld(patternToPlay);
                    }
                }
                print();
            }
            else if(response == "b"){

            }
        }
    }

    public static void main(String args[]) throws IOException, PatternFormatException {
        if (args.length != 1) {
            System.out.println("Usage: java GameOfLife <path/url to store>");
            return;
        }

        try {
            PatternStore ps = new PatternStore(args[0]);
            GameOfLife gol = new GameOfLife(ps);
            gol.play();
        } catch (IOException ioe) {
            System.out.println("Failed to load pattern store");
        }
    }

}*/