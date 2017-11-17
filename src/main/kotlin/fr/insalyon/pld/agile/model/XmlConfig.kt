package fr.insalyon.pld.agile.model

object XmlConfig {
  object Map {
    val TAG = "reseau"
  }

  object Intersection {
    val TAG = "noeud"
    val ID = "id"
    val X = "x"
    val Y = "y"
  }

  object Junction {
    val TAG = "troncon"
    val NAME = "nomRue"
    val FROM = "origine"
    val TO = "destination"
    val LENGTH = "longueur"
  }

  object RoundRequest {
    val TAG = "demandeDeLivraisons"
  }

  object Warehouse {
    val TAG = "entrepot"
    val ADDRESS = "adresse"
    val DEPARTURE_HOUR = "heureDepart"
  }

  object Delivery {
    val TAG = "livraison"
    val ADDRESS = "adresse"
    val START_TIME = "debutPlage"
    val END_TIME = "finPlage"
    val DURATION = "duree"
  }
}