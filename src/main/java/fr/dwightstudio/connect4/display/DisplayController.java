package fr.dwightstudio.connect4.display;

import fr.dwightstudio.connect4.game.GameState;

public abstract class DisplayController {

    /**
     * Fait le rendu de l'état actuel du jeu
     *
     * @param gameState l'état actuel du jeu
     */
    public abstract void render(GameState gameState);

    /**
     * Fait le rendu de l'état actuel du jeu et demande au joueur d'effectuer une action
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
     * Met à jour l'indice de confiance de l'intelligence artificielle
     *
     * @param confidence l'indice de confiance
     */
    public abstract void updateConfidence(int confidence);

    /**
     * Nettoie l'affichage
     */
    public abstract void clear();

}
