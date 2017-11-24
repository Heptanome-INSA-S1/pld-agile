package fr.insalyon.pld.agile.service.algorithm.api;

public interface TSP {

  /**
   * @return true si findSolution() s'est terminee parce que la limite de temps avait ete atteinte, avant d'avoir pu explorer tout l'espace de recherche,
   */
  Boolean getLimitTimeReached();

  /**
   * Cherche un circuit de duree minimale passant par chaque sommet (compris entre 0 et nbSommets-1)
   *
   * @param timeLimitInMs : limite (en millisecondes) sur le temps d'execution de findSolution
   * @param numberOfNodes : nombre de sommets du graphe
   * @param coast         : coast[i][j] = duree pour aller de i a j, avec 0 <= i < numberOfNodes et 0 <= j < numberOfNodes
   * @param durations     : durations[i] = durations pour visiter le sommet i, avec 0 <= i < nbSommets
   */
  void findSolution(int timeLimitInMs, int numberOfNodes, long[][] coast, long[] durations);

  /**
   * @param i
   * @return le sommet visite en i-eme position dans la solution calculee par findSolution
   */
  Integer getBestSolution(int i);

  /**
   * @return la duree de la solution calculee par findSolution
   */
  long getBestSolutionCoast();
}
