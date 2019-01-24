package uk.ac.cam.spv28.oop.tick5;

import java.io.*;
import java.net.*;
import java.util.*;



public class PatternStore {

    private List<Pattern> patterns = new LinkedList<>();
    private Map<String,List<Pattern>> mapAuths = new HashMap<>();
    private Map<String,Pattern> mapName = new HashMap<>();

    public PatternStore(String source) throws IOException {
        if (source.startsWith("http://") || source.startsWith("https://")) {
            loadFromURL(source);
        }
        else {
            System.out.println(source);//test
            loadFromDisk(source);
        }
    }

    public PatternStore(Reader source) throws IOException {
        load(source);
    }

    private void load(Reader r) throws IOException {
        // read each line from the reader and print it to the screen
        BufferedReader b = new BufferedReader(r);
        String line = b.readLine();
        Pattern currentP;
        while (line != null) {
            //System.out.println(line);
            try {
              currentP = new Pattern(line);

                patterns.add(currentP);
                String currentAuth = currentP.getAuthor();
                String currentName = currentP.getName();

                if(mapAuths.get(currentAuth) != null){
                    List<Pattern> switcheroo= mapAuths.get(currentAuth);
                    switcheroo.add(currentP);

                }
                else {
                    List<Pattern> newList = new ArrayList<>();
                    newList.add(currentP);
                    mapAuths.put(currentAuth, newList);
                }

                mapName.put(currentName, currentP);
            }
            catch(PatternFormatException e){
                System.out.println("WARNING: Pattern Anomaly Detected in : *" +line+"*" );
            }


            line = b.readLine();
        }
    }

    private void loadFromURL(String url) throws IOException {
        // Create a Reader for the URL and then call load on it
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        Reader r = new InputStreamReader(conn.getInputStream());
        load(r);

// Rest as above
    }

    private void loadFromDisk(String filename) throws IOException {
        // Create a Reader for the file and then call load on it
        Reader r = new FileReader(filename);
        load(r);

    }

    public List<Pattern> getPatternsNameSorted() {
        // Get a list of all patterns sorted by name
       List<Pattern> copy = new ArrayList<Pattern>();
       copy.addAll(patterns);
        Collections.sort(copy);
        return copy;
    }

    public List<Pattern> getPatternsAuthorSorted() {
        // Get a list of all patterns sorted by author then name
        List<Pattern> copy = new ArrayList<Pattern>();
        copy.addAll(patterns);
        Collections.sort(copy, new Comparator<Pattern>() {//this is the start of an anonymous comparator
                                public int compare(Pattern p1, Pattern p2)
                                {   int result1 = p1.getAuthor().compareTo(p2.getAuthor());
                                    if (result1 != 0){
                                        return result1;
                                    }
                                    return p1.compareTo(p2);}
        });

        return copy;
    }

    public List<Pattern> getPatternsByAuthor(String author) throws PatternNotFound {
        //  return a list of patterns from a particular author sorted by name
        List<Pattern> authorsPatterns = mapAuths.get(author);
        if(authorsPatterns == null){
            throw new PatternNotFound();
        }
        else{
            List<Pattern> copy = new ArrayList<Pattern>();
            copy.addAll(authorsPatterns);
            Collections.sort(copy);
            return copy;
        }

    }

    public Pattern getPatternByName(String name) throws PatternNotFound {
        // Get a particular pattern by name
        Pattern namedPattern = mapName.get(name);
        if(namedPattern == null){
            throw new PatternNotFound();
        }
        else{
            return namedPattern;//this is OK directly as patterns are immutable
        }
    }

    public List<String> getPatternAuthors() {
        // Get a sorted list of all pattern authors in the store
        Set<String> namesfrommap= mapAuths.keySet();
        List<String> authors = new ArrayList<String>();
        authors.addAll(namesfrommap);
        Collections.sort(authors);
        return authors;
    }

    public List<String> getPatternNames() {
        //  Get a list of all pattern names in the store,
        // sorted by name
        Set<String> namesfrommap= mapName.keySet();
        List<String> names = new ArrayList<String>();
        names.addAll(namesfrommap);
        Collections.sort(names);
        return names;
    }


    public static void main(String args[]) throws IOException,PatternNotFound {
       // String input = args[0].ReplaceAll("?",""); //this is here due to a particular issue I have with the IntelliJ terminal
        PatternStore p = new PatternStore(args[0]);
       //test outputs here?
        List<Pattern> AllPatterns = p.getPatternsByAuthor("achim flammenkamp");
        for(Pattern i : AllPatterns){
            System.out.println(i.getAuthor() + "    "+i.getName());
        }
    }
}