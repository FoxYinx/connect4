package fr.dwightstudio.connect4.game;

import org.jetbrains.annotations.NotNull;

public record MoveScore(long move, int score) implements Comparable<MoveScore> {
    @Override
    public int compareTo(@NotNull MoveScore o) {
        return -Integer.compare(score, o.score);
    }
}