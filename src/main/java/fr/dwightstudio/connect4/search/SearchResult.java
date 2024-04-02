package fr.dwightstudio.connect4.search;

public record SearchResult(int move, int confidence, float meanConfidence) {
    public SearchResult(int move, int confidence) {
        this(move, confidence, confidence);
    }
}
