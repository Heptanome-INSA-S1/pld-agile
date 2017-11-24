package fr.insalyon.pld.agile.model

class Distance constructor (
       val value: Int,
       val distanceUnit: DistanceUnit
){

    enum class DistanceUnit(val coef: Double) {
        M(1.0),
        DAM(10.0),
        KM(1000.0)
    }

    fun to(distanceUnit: Distance.DistanceUnit): Distance {

        val coef = (this.distanceUnit.coef /distanceUnit.coef)

        return Distance((value * coef).toInt(), distanceUnit)
    }

}

public val Int.km: Distance get() = Distance(this, Distance.DistanceUnit.KM)
public val Int.dam: Distance get() = Distance(this, Distance.DistanceUnit.DAM)
public val Int.m: Distance get() = Distance(this, Distance.DistanceUnit.M)
