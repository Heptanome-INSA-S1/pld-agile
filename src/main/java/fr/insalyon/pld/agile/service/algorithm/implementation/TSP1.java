package fr.insalyon.pld.agile.service.algorithm.implementation;

import java.util.ArrayList;
import java.util.Iterator;

public class TSP1 extends TemplateTSP {

  @Override
  protected Iterator<Integer> iterator(Integer sommetCrt, ArrayList<Integer> nonVus, long[][] cout, long[] duree) {
    return new IteratorSeq(nonVus, sommetCrt);
  }

  @Override
  protected long bound(Integer sommetCourant, ArrayList<Integer> nonVus, long[][] cout, long[] duree) {
    return 0L;
  }
}
