package fr.insalyon.pld.agile.model

class Speed constructor(
    val value: Int,
    val distanceUnit: DistanceUnit,
    val durationUnit: DurationUnit
){
    enum class DistanceUnit(val coef: Double) {
      M(1.0),
      DAM(10.0),
      KM(1000.0)
    }
    
    enum class DurationUnit(val coef: Double) {
      H(3600.0),
      M(60.0),
      S(1.0)
    }

  fun to(distanceUnit: DistanceUnit, durationUnit: DurationUnit): Speed {

    val coef = (this.distanceUnit.coef / distanceUnit.coef) * (this.durationUnit.coef / durationUnit.coef)

    return Speed((value * coef).toInt(), distanceUnit, durationUnit)

  }

  
}

public val Int.km_h: Speed get() = Speed(this, Speed.DistanceUnit.KM, Speed.DurationUnit.H)