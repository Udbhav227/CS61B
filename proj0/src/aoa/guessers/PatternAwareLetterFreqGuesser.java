package aoa.guessers;

import aoa.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatternAwareLetterFreqGuesser implements Guesser {
    private final List<String> words;

    public PatternAwareLetterFreqGuesser(String dictionaryFile) {
        words = FileUtils.readWords(dictionaryFile);
    }

    @Override
    /** Returns the most common letter in the set of valid words based on the current
     *  PATTERN. */
    public char getGuess(String pattern, List<Character> guesses) {
        Map<Character, Integer> fm = getFreqMapThatMatchesPattern(pattern); // fm = frequency map
        int mf = 0; // mf = max frequency
        char mfc = '?'; // mfc = max frequency character
        for (Character key : fm.keySet()) {
            if (!guesses.contains(key)) {
                if (fm.get(key) == mf && mfc > key) {
                    mfc = key;
                }
                else if (fm.get(key) > mf) {
                    mf = fm.get(key);
                    mfc = key;
                }
            }
        }
        return mfc;
    }

    public List<String> keepOnlyWordsThatMatchPattern(String pattern) {
        List<String> wordsThatMatchPattern = new ArrayList<>();
        Map<Integer, Character> wordsMapAfterFilter = new HashMap<>();
        char[] patternArray = pattern.toCharArray();
        char pickedLetter = '-';
        int idx = 0;
        for (var ch : patternArray) {
            if (ch != '-') {
                pickedLetter = ch;
                wordsMapAfterFilter.put(idx, pickedLetter);
            }
            idx++;
        }
        if (wordsMapAfterFilter.isEmpty()) {
            for (var word : words) {
                wordsThatMatchPattern.add(word.length() == pattern.length() ? word : null);
            }
        } else {
            for (var word : words) {
                if (word.length() == pattern.length()) {
                    boolean flag = true;
                    for (var key : wordsMapAfterFilter.keySet()) {
                        if (word.charAt(key) != wordsMapAfterFilter.get(key)) {
                            flag = false;
                        }
                    }
                    if (flag) wordsThatMatchPattern.add(word);
                }
            }
        }
        return wordsThatMatchPattern;
    }

    public Map<Character, Integer> getFreqMapThatMatchesPattern(String pattern){
        Map<Character, Integer> frequencyMap = new HashMap<>();
        List<String> newList =  keepOnlyWordsThatMatchPattern(pattern);
        for (var word : newList) {
            for (var ch : word.toCharArray()) {
                if (frequencyMap.containsKey(ch)) {
                    frequencyMap.put(ch, frequencyMap.get(ch) + 1);
                } else frequencyMap.put(ch, 1);
            }
        }
        return frequencyMap;
    }

    public static void main(String[] args) {
        PatternAwareLetterFreqGuesser palfg = new PatternAwareLetterFreqGuesser("data/example.txt");
        System.out.println(palfg.getGuess("-e--", List.of('e')));
        System.out.println(palfg.keepOnlyWordsThatMatchPattern("-o--a-"));
        System.out.println(palfg.getFreqMapThatMatchesPattern("----"));
    }
}