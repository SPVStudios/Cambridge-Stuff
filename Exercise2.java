package uk.ac.cam.spv28.mlrd.task2;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.LinkedList;


import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.DataPreparation1;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.IExercise1;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.IExercise2;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Tokenizer;
import uk.ac.cam.cl.mlrd.utils.DataSplit;


//import uk.ac.cam.spv28.mlrd.task1.*;

public class Exercise2 implements IExercise2 {

    /**
     * probably just 50|50 for this system but can't hurt to confirm.
     */
    public Map<Sentiment, Double> calculateClassProbabilities(Map<Path, Sentiment> trainingSet) throws IOException {

        int numPOS = 0;
        int numNEG = 0;

        for (Path p : trainingSet.keySet()) {
            Sentiment s = trainingSet.get(p);
            if (s == Sentiment.POSITIVE) {
                numPOS++;
            } else {
                numNEG++;
            }
        }

        HashMap<Sentiment,Double> ClassProbs = new HashMap<>();
        double p_POS = (double) numPOS / ((double) numPOS + (double) numNEG);
        double p_NEG = 1 - p_POS;

        ClassProbs.put(Sentiment.POSITIVE, p_POS);
        ClassProbs.put(Sentiment.NEGATIVE,p_NEG);
        return ClassProbs;
    }

    /**
     * For each word and sentiment present in the training set, estimate the
     * unsmoothed log probability of a word to occur in a review with a
     * particular sentiment.
     *
     * @param trainingSet {@link Map}<{@link Path}, {@link Sentiment}> Training review
     *                    paths
     * @return {@link Map}<{@link String}, {@link Map}<{@link Sentiment},
     * {@link Double}>> Estimated log probabilities
     * @throws IOException
     */
    public Map<String, Map<Sentiment, Double>> calculateUnsmoothedLogProbs(Map<Path, Sentiment> trainingSet)
            throws IOException {


        HashMap<String, Integer> WordCountforPOS = new HashMap<String, Integer>();
        HashMap<String, Integer> WordCountforNEG = new HashMap<String, Integer>();

        int totalPOS_Words = 0;
        int totalNEG_Words = 0;
        //also keep track of every single unique word
        List<String> Overall_Vocabulary = new LinkedList<String>();

        for (Path p : trainingSet.keySet()) {
            Sentiment current_Sentiment = trainingSet.get(p);

            List<String> tokens = Tokenizer.tokenize(p);
            for (String s : tokens) {
                if (current_Sentiment == Sentiment.POSITIVE) {
                    if (WordCountforPOS.containsKey(s)) {
                        WordCountforPOS.put(s, WordCountforPOS.get(s) + 1);
                    } else {
                        WordCountforPOS.put(s, 1);
                    }
                    totalPOS_Words++;
                } else {//NEG sentiment:
                    if (WordCountforNEG.containsKey(s)) {
                        WordCountforNEG.put(s, WordCountforNEG.get(s) + 1);
                    } else {
                        WordCountforNEG.put(s, 1);
                    }
                    totalNEG_Words++;
                }
                //add to vocabulary
                if (!Overall_Vocabulary.contains(s)) {
                    Overall_Vocabulary.add(s);
                }
            }
        }
        //words all counted up. now calculate P(Wi|Sentiment) for each possible word
        Map<String, Map<Sentiment, Double>> wordChance = new HashMap<String, Map<Sentiment, Double>>();
        //as required for the tester compared to my previous method, give unknown values a -infinity.
        for (String word : Overall_Vocabulary) {
            HashMap<Sentiment, Double> t = new HashMap<Sentiment, Double>();
            t.put(Sentiment.NEGATIVE, Double.NEGATIVE_INFINITY);
            t.put(Sentiment.POSITIVE, Double.NEGATIVE_INFINITY);
            wordChance.put(word, t);
        }


        //first the positive ones
        for (String word : WordCountforPOS.keySet()) {
            double Wi = (double) WordCountforPOS.get(word) / (double) totalPOS_Words;
            Map<Sentiment, Double> indiChance = wordChance.get(word);
            indiChance.put(Sentiment.POSITIVE, Math.log(Wi));
            wordChance.put(word, indiChance);
        }
        //now the negative ones
        for (String word : WordCountforNEG.keySet()) {
            Map<Sentiment, Double> prevIndiChance = wordChance.get(word);
            double Wi = (double) WordCountforNEG.get(word) / (double) totalNEG_Words;
            prevIndiChance.put(Sentiment.NEGATIVE, Math.log(Wi));
        }
        // return overall Map.
        return wordChance;

    }



    /**
     * For each word and sentiment present in the training set, estimate the
     * smoothed log probability of a word to occur in a review with a particular
     * sentiment. Use the smoothing technique described in the instructions.
     *
     * @param trainingSet
     *            {@link Map}<{@link Path}, {@link Sentiment}> Training review
     *            paths
     * @return {@link Map}<{@link String}, {@link Map}<{@link Sentiment},
     *         {@link Double}>> Estimated log probabilities
     * @throws IOException
     */
    public Map<String, Map<Sentiment, Double>> calculateSmoothedLogProbs(Map<Path, Sentiment> trainingSet)
            throws IOException{

        HashMap<String, Integer> WordCountforPOS = new HashMap<String, Integer>();
        HashMap<String, Integer> WordCountforNEG = new HashMap<String, Integer>();
        //also keep track of every single unique word
        List<String> Overall_Vocabulary = new LinkedList<String>();

        int totalPOS_Words = 0;
        int totalNEG_Words = 0;


        for (Path p : trainingSet.keySet()) {
            Sentiment current_Sentiment = trainingSet.get(p);

            List<String> tokens = Tokenizer.tokenize(p);
            for (String s : tokens) {
                if (current_Sentiment == Sentiment.POSITIVE) {
                    if (WordCountforPOS.containsKey(s)) {
                        WordCountforPOS.put(s, WordCountforPOS.get(s) + 1);
                    } else {
                        WordCountforPOS.put(s, 1);
                    }
                    totalPOS_Words++;
                } else {//NEG sentiment
                    if (WordCountforNEG.containsKey(s)) {
                        WordCountforNEG.put(s, WordCountforNEG.get(s) + 1);
                    } else {
                        WordCountforNEG.put(s, 1);
                    }
                    totalNEG_Words++;
                }
                //add to vocabulary
                if(!Overall_Vocabulary.contains(s)){
                    Overall_Vocabulary.add(s);
                }
            }
        }
        //words all counted up. now calculate P(Wi|Sentiment) for each possible word as modified by the Laplace. Use this as a baseline for all words then replace with data in a minute.
        int V = Overall_Vocabulary.size();
        Map<String,Map<Sentiment,Double>> wordChance = new HashMap<String,Map<Sentiment,Double>>();
        double base_NEG_chance = Math.log(1.0 / ((double)totalNEG_Words + V));
        double base_POS_chance = Math.log(1.0 / ((double)totalPOS_Words + V));

//        //====Testing
//        System.out.println("Base Negative value: "+base_NEG_chance);
//        System.out.println("Base Positive value: "+base_POS_chance);
//
//        //===



        for(String word : Overall_Vocabulary){
            HashMap<Sentiment,Double> t = new HashMap<Sentiment,Double>();
            t.put(Sentiment.POSITIVE, base_POS_chance);
            t.put(Sentiment.NEGATIVE, base_NEG_chance);
            wordChance.put(word,t);
        }
        //now update with realistic values
        //first the positive ones
        for(String word : WordCountforPOS.keySet()) {
            if (wordChance.containsKey(word)) {
                double Wi = ((double) WordCountforPOS.get(word) + 1) / ((double) totalPOS_Words + V);
                Map<Sentiment, Double> indiChance = wordChance.get(word);
                indiChance.put(Sentiment.POSITIVE, Math.log(Wi));
            }
        }
        for(String word : WordCountforNEG.keySet()) {
            if (wordChance.containsKey(word)) {
                double Wi = ((double) WordCountforNEG.get(word) + 1) / ((double) totalNEG_Words + V);
                Map<Sentiment, Double> indiChance = wordChance.get(word);
                indiChance.put(Sentiment.NEGATIVE, Math.log(Wi));
            }
        }
            //else leave as base value
        // return overall Map.
        return wordChance;
    }

    /**
     * Use the estimated log probabilities to predict the sentiment of each
     * review in the test set.
     *
     * @param testSet
     *            {@link Set}<{@link Path}> Test review paths
     * @param tokenLogProbs
     *            {@link Map}<{@link String}, {@link Map}<{@link Sentiment},
     *            {@link Double}>> Log probabilities
     * @return {@link Map}<{@link Path}, {@link Sentiment}> Predicted sentiments
     * @throws IOException
     */
    public Map<Path, Sentiment> naiveBayes(Set<Path> testSet, Map<String, Map<Sentiment, Double>> tokenLogProbs,
                                           Map<Sentiment, Double> classProbabilities) throws IOException{


        HashMap<Path,Sentiment> outputPredic = new HashMap<Path,Sentiment>();
            //check our testSet one at a time
        for(Path p : testSet) {
            double p_POS = Math.log(classProbabilities.get(Sentiment.POSITIVE));
            double p_NEG = Math.log(classProbabilities.get(Sentiment.NEGATIVE));
            //we'll add to these linearly throughout calculation. CAREFUL HERE.
            List<String> tokens  = Tokenizer.tokenize(p);
            for(String w : tokens){

                if(tokenLogProbs.containsKey(w)){
                    Map<Sentiment,Double> wordChance = tokenLogProbs.get(w);
                    p_POS += wordChance.getOrDefault(Sentiment.POSITIVE, Double.NEGATIVE_INFINITY);
                    p_NEG += wordChance.getOrDefault(Sentiment.NEGATIVE, Double.NEGATIVE_INFINITY);
                }
                //else ignore

            }

            if (p_POS >= p_NEG) {
                outputPredic.put(p,Sentiment.POSITIVE);
            }
            else{
                outputPredic.put(p,Sentiment.NEGATIVE);
            }
        }
        return outputPredic;
    }




}