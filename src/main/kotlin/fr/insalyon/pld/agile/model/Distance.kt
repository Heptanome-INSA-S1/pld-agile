package fr.insalyon.pld.agile.model

class Distance constructor (
       val value: Long,
       val distanceUnit: DistanceUnit
){

    enum class DistanceUnit(val coef: Double) {
        M(1.0),
        DAM(10.0),
        KM(1000.0)
    }

    fun to(distanceUnit: Distance.DistanceUnit): Distance {

        val coef = (this.distanceUnit.coef /distanceUnit.coef)

        return Distance((value * coef).toLong(), distanceUnit)
    }

}

public val Long.km: Distance get() = Distance(this, Distance.DistanceUnit.KM)
public val Long.dam: Distance get() = Distance(this, Distance.DistanceUnit.DAM)
public val Long.m: Distance get() = Distance(this, Distance.DistanceUnit.M)
