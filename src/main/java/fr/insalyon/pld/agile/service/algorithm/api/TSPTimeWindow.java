package fr.insalyon.pld.agile.service.algorithm.api;

public interface TSPTimeWindow {

    /**
     * @return true si chercheSolution() s'est terminee parce que la limite de temps avait ete atteinte, avant d'avoir pu explorer tout l'espace de recherche,
     */
    public Boolean getTempsLimiteAtteint();

    /**
     * Cherche un circuit de duree minimale passant par chaque sommet (compris entre 0 et nbSommets-1)
     * @param tpsLimite : limite (en millisecondes) sur le temps d'execution de chercheSolution
     * @param nbSommets : nombre de sommets du graphe
     * @param cout cout[i][j]
     * @param duree duree[i]
     * @param timeWindows timeWindows
     * @param departureTime departureTime
     */
    public void chercheSolution(int tpsLimite, int nbSommets, int[][] cout, int[] duree, int[][] timeWindows, int departureTime);

    /**
     * @param i sommet i
     * @return le sommet visite en i-eme position dans la solution calculee par chercheSolution
     */
    public Integer getMeilleureSolution(int i);

    /**
     * @return la duree de la solution calculee par chercheSolution
     */
    public int getCoutMeilleureSolution();

    /**
     *
     * @return si il y a une meilleure solution
     */
    public Boolean getAsBestSolution();
}
