package fr.insalyon.pld.agile.service.algorithm.implementation;

import fr.insalyon.pld.agile.service.algorithm.api.TSP;
import fr.insalyon.pld.agile.service.algorithm.api.TSPTimeWindow;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class TemplateTSP2 implements TSPTimeWindow {

    private Integer[] meilleureSolution;
    private int coutMeilleureSolution = 0;
    private Boolean tempsLimiteAtteint;
    private Boolean asBestSolution;


    public TemplateTSP2() {
        super();
        coutMeilleureSolution = 0;
        tempsLimiteAtteint = false;
        asBestSolution = false;
    }

    @Override
    public Boolean getTempsLimiteAtteint() {
        return tempsLimiteAtteint;
    }

    public Boolean getAsBestSolution() {
        return asBestSolution;
    }

    @Override
    public void chercheSolution(int tpsLimite, int nbSommets, int[][] cout, int[] duree, int[][] timeWindows, int departureTime) {
        tempsLimiteAtteint = false;
        coutMeilleureSolution = (24*3600);
        meilleureSolution = new Integer[nbSommets];
        ArrayList<Integer> nonVus = new ArrayList<>();
        for (int i = 1; i < nbSommets; i++)
            nonVus.add(i);
        ArrayList<Integer> vus = new ArrayList<>(nbSommets);
        vus.add(0); // le premier sommet visite est 0
        branchAndBound(0, nonVus, vus, 0, cout, duree, System.currentTimeMillis(), tpsLimite, timeWindows, departureTime);
    }

    public Integer getMeilleureSolution(int i) {
        if ((meilleureSolution == null) || (i < 0) || (i >= meilleureSolution.length))
            return null;
        return meilleureSolution[i];
    }

    @Override
    public int getCoutMeilleureSolution() {
        return coutMeilleureSolution;
    }

    /**
     * Methode devant etre redefinie par les sous-classes de TemplateTSP
     *
     * @param sommetCourant
     * @param nonVus
     *            : tableau des sommets restant a visiter
     * @param cout
     *            : cout[i][j] = duree pour aller de i a j, avec 0 <= i < nbSommets
     *            et 0 <= j < nbSommets
     * @param duree
     *            : duree[i] = duree pour visiter le sommet i, avec 0 <= i <
     *            nbSommets
     * @param departureTime
     * @param timeWindows
     * @param coutVus
     * @return une borne inferieure du cout des permutations commencant par
     *         sommetCourant, contenant chaque sommet de nonVus exactement une fois
     *         et terminant par le sommet 0
     */
    protected abstract int bound(Integer sommetCourant, ArrayList<Integer> nonVus, int[][] cout, int[] duree, int coutVus, int[][] timeWindows, int departureTime);

    /**
     * Methode devant etre redefinie par les sous-classes de TemplateTSP
     *
     * @param sommetCrt
     * @param nonVus
     *            : tableau des sommets restant a visiter
     * @param cout
     *            : cout[i][j] = duree pour aller de i a j, avec 0 <= i < nbSommets
     *            et 0 <= j < nbSommets
     * @param duree
     *            : duree[i] = duree pour visiter le sommet i, avec 0 <= i <
     *            nbSommets
     * @return un iterateur permettant d'iterer sur tous les sommets de nonVus
     */
    protected abstract Iterator<Integer> iterator(Integer sommetCrt, ArrayList<Integer> nonVus, int[][] cout, int[] duree, int[][] timeWindows, int coutVus, int departureTime);

    /**
     * Methode definissant le patron (template) d'une resolution par separation et
     * evaluation (branch and bound) du TSP
     *
     * @param sommetCrt
     *            le dernier sommet visite
     * @param nonVus
     *            la liste des sommets qui n'ont pas encore ete visites
     * @param vus
     *            la liste des sommets visites (y compris sommetCrt)
     * @param coutVus
     *            la somme des couts des arcs du chemin passant par tous les sommets
     *            de vus + la somme des duree des sommets de vus
     * @param cout
     *            : cout[i][j] = duree pour aller de i a j, avec 0 <= i < nbSommets
     *            et 0 <= j < nbSommets
     * @param duree
     *            : duree[i] = duree pour visiter le sommet i, avec 0 <= i <
     *            nbSommets
     * @param tpsDebut
     *            : moment ou la resolution a commence
     * @param tpsLimite
     *            : limite de temps pour la resolution
     */
    void branchAndBound(int sommetCrt, ArrayList<Integer> nonVus, ArrayList<Integer> vus, int coutVus, int[][] cout,
                        int[] duree, long tpsDebut, int tpsLimite, int[][] timeWindows, int departureTime) {
        if (System.currentTimeMillis() - tpsDebut > tpsLimite) {
            tempsLimiteAtteint = true;
            return;
        }
        if (nonVus.size() == 0) { // tous les sommets ont ete visites
            coutVus += cout[sommetCrt][0];
            if (coutVus < coutMeilleureSolution) { // on a trouve une solution meilleure que meilleureSolution
                vus.toArray(meilleureSolution);
                coutMeilleureSolution = coutVus;
                asBestSolution = true;
            }
        } else if (coutVus + bound(sommetCrt, nonVus, cout, duree, coutVus, timeWindows, departureTime) < coutMeilleureSolution) {
            Iterator<Integer> it = iterator(sommetCrt, nonVus, cout, duree, timeWindows, coutVus, departureTime);
            while (it.hasNext()) {
                Integer prochainSommet = it.next();
                vus.add(prochainSommet);
                nonVus.remove(prochainSommet);

                boolean cut = false;
                int waitingTime = 0;

                if(timeWindows[prochainSommet][0] != -1) {
                    int nextIntersectionArrivalTime = departureTime + coutVus + cout[sommetCrt][prochainSommet];
                    int nextIntersectionDepartureTime = nextIntersectionArrivalTime + duree[prochainSommet];
                    if (nextIntersectionArrivalTime < timeWindows[prochainSommet][0]){
                        waitingTime = timeWindows[prochainSommet][0] - nextIntersectionArrivalTime;
                        nextIntersectionDepartureTime = timeWindows[prochainSommet][0] + duree[prochainSommet];
                    }
                    if (nextIntersectionDepartureTime > timeWindows[prochainSommet][1]) {
                        cut = true;
                    }
                }

                if (!cut) {
                    branchAndBound(prochainSommet, nonVus, vus,
                            coutVus + cout[sommetCrt][prochainSommet] + duree[prochainSommet] + waitingTime, cout, duree, tpsDebut,
                            tpsLimite, timeWindows, departureTime);

                }
                vus.remove(prochainSommet);
                nonVus.add(prochainSommet);
            }
        }
    }
}
