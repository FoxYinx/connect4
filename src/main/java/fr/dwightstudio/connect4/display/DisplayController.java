package fr.dwightstudio.connect4.display;

import fr.dwightstudio.connect4.game.GameState;
import fr.dwightstudio.connect4.search.SearchResult;

public abstract class DisplayController {

    /**
     * Fait le rendu de l'état actuel du jeu
     *
     * @param gameState l'état actuel du jeu
     */
    public abstract void render(GameState gameState);

    /**
     * Demande au joueur d'effectuer une action
     *
     * @param gameState l'état actuel du jeu
     * @return une action décidée par le joueur
     */
    public abstract int play(GameState gameState);

    /**
     * Indique la victoire d'un joueur
     *
     * @param human vrai si c'est l'humain qui a gagné
     */
    public abstract void win(boolean human);

    /**
     * Indique que le jeu s'est terminé sur une égalité
     */
    public abstract void draw();

    /**
     * Met à jour l'indice de confiance de l'intelligence artificielle
     *
     * @param state l'état actuel du jeu
     * @param searchResult les résultats de la recherche
     */
    public abstract void updateConfidence(GameState state, SearchResult searchResult);

    /**
     * Nettoie l'affichage
     */
    public abstract void clear();

}
