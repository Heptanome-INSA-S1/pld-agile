package fr.insalyon.pld.agile.service.algorithm.implementation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class TSP3 extends TemplateTSP2 {

    @Override
    protected Iterator<Integer> iterator(Integer sommetCrt, ArrayList<Integer> nonVus, int[][] cout, int[] duree, int[][] timeWindows, int coutVus, int departureTime) {
        nonVus.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                int waitingTime1 = 0;
                int waitingTime2 = 0;

                if (timeWindows[o1][0] != -1) {
                    int nextIntersectionArrivalTime = departureTime + coutVus + cout[sommetCrt][o1];
                    if (nextIntersectionArrivalTime < timeWindows[o1][0]) {
                        waitingTime1 = timeWindows[o1][0] - nextIntersectionArrivalTime;
                    }
                }
                if (timeWindows[o2][0] != -1) {
                    int nextIntersectionArrivalTime = departureTime + coutVus + cout[sommetCrt][o2];
                    if (nextIntersectionArrivalTime < timeWindows[o2][0]) {
                        waitingTime2 = timeWindows[o2][0] - nextIntersectionArrivalTime;
                    }
                }

                return Integer.compare((cout[sommetCrt][o2] + waitingTime2), (cout[sommetCrt][o1] + waitingTime1));
            }
        });
        return new IteratorSeq(nonVus, sommetCrt);
    }

    @Override
    protected int bound(Integer sommetCourant, ArrayList<Integer> nonVus, int[][] cout, int[] duree, int coutVus,
                        int[][] timeWindows, int departureTime) {

        int minimumToNextIntersection = Integer.MAX_VALUE;

        for (Integer prochainSommet : nonVus) {
            int waitingTime = 0;
            if (timeWindows[prochainSommet][0] != -1) {
                int nextIntersectionArrivalTime = departureTime + coutVus + cout[sommetCourant][prochainSommet];
                int nextIntersectionDepartureTime = nextIntersectionArrivalTime  + duree[prochainSommet];
                if (nextIntersectionArrivalTime < timeWindows[prochainSommet][0]) {
                    waitingTime = timeWindows[prochainSommet][0] - nextIntersectionArrivalTime;
                }else if (nextIntersectionDepartureTime > timeWindows[prochainSommet][1]) {
                    return (24 * 3600);
                }
            }
            if ((waitingTime + cout[sommetCourant][prochainSommet]) < minimumToNextIntersection) {
                minimumToNextIntersection = (waitingTime + cout[sommetCourant][prochainSommet]);
            }
        }

        int minmumsSum = 0;

        for (Integer val : nonVus) {
            int minimum = Integer.MAX_VALUE;
            for (Integer val2 : nonVus) {
                if (val2 != val) {
                    if ((cout[val][val2]) < minimum) {
                        minimum = cout[val][val2];
                    }
                }
            }
            if (cout[val][0] < minimum) {
                minimum = cout[val][0];
            }
            minmumsSum += minimum + duree[val];
        }

        return (minimumToNextIntersection + minmumsSum);
    }

}