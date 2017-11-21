package fr.insalyon.pld.agile.util.txt

import fr.insalyon.pld.agile.model.*
import java.io.File

class RoadSheetSerializer(){
    lateinit var roadSheet: File

    private fun append(text: String){
        roadSheet.appendText(text )//printWriter().use { out -> out.println(text) }
    }

    private fun getStartTime(sT: Instant?):String{
        if(sT!=null)return "Start Time = ${sT}"
        return ""
    }

    private fun getEndTime(eT: Instant?): String{
        if(eT!=null) return "End Time = ${eT}"
        return ""
    }

    fun serialize(round: Round) {
        if(File("RoadSheet.txt").exists()) File("RoadSheet.txt").delete()
        roadSheet = File("RoadSheet.txt")

        var warehouseDescription: String = "Warehouse : ${round.warehouse.address.x} ${round.warehouse.address.y} - Departure Hour = ${round.warehouse.departureHour} "
        append(warehouseDescription+"\r\n____________________________________\r\n")

        //round.deliveries.forEach { d -> append("Delivery : x = ${d.address.x} y = ${d.address.y} Start Time = ${d.startTime} End Time = ${d.endTime} Duration = ${d.duration.length}") }
        var j=0
        for(i in 0 until round.deliveries.size){
            while(j<round.path.size && round.deliveries.elementAt(i).address != round.path.elementAt(j).nodes[0]) {
                round.path.elementAt(j).edges.forEach { e -> append("${e.name} \r\n")}
                j++
            }
            append("Delivery $i : x = ${round.deliveries.elementAt(i).address.x} y = ${round.deliveries.elementAt(i).address.y} ${getStartTime(round.deliveries.elementAt(i).startTime)}${getEndTime(round.deliveries.elementAt(i).endTime)}Duration = ${round.deliveries.elementAt(i).duration.length}")
            append("\r\n____________________________________\r\n")
        }
        round.path.elementAt(j).edges.forEach { e -> append("${e.name} \r\n")}
        append("Warehouse : x = ${round.warehouse.address.x} y = ${round.warehouse.address.y} \r\nFIN TOURNEE")
    }
}