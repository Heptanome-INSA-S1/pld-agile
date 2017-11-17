package fr.insalyon.pld.agile.model

data class Intersection(
    val id: Long,
    val x: Int = 0,
    val y: Int = 0
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Intersection

    if (id != other.id) return false

    return true
  }

  override fun hashCode(): Int = id.hashCode()
}