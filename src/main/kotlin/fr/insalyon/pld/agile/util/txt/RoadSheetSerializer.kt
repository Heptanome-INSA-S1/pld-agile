package fr.insalyon.pld.agile.util.txt

import fr.insalyon.pld.agile.getResource
import fr.insalyon.pld.agile.model.*
import java.io.File

class RoadSheetSerializer(){
    lateinit var roadSheet: File

    private fun append(text: String){
        roadSheet.appendText(text )//printWriter().use { out -> out.println(text) }
    }

    private fun getStartTime(sT: Instant?):String{
        if(sT!=null)return "Start Time = ${sT}"
        return "n/a"
    }

    private fun getEndTime(eT: Instant?): String{
        if(eT!=null) return "End Time = ${eT}"
        return "n/a"
    }

    fun serialize(round: Round) {
        if(File(getResource("RoadSheet.txt").path).exists()) File(getResource("RoadSheet.txt").path).delete()
        roadSheet = File(getResource("RoadSheet.txt").path)

        append("Beginning of the round ! \n")
        var warehouseDescription: String = "You have to leave the warehouse (x:${round.warehouse.address.x}, y:${round.warehouse.address.y}) at ${round.warehouse.departureHour} "
        append(warehouseDescription+"\r\n________________________________________________________________________\r\n")

        //round.deliveries.forEach { d -> append("Delivery : x = ${d.address.x} y = ${d.address.y} Start Time = ${d.startTime} End Time = ${d.endTime} Duration = ${d.duration.length}") }
        var j=0
        for(i in 0 until round.deliveries.size){
            append("You have to : \n\n")
            while(j<round.path.size && round.deliveries.elementAt(i).address != round.path.elementAt(j).nodes[0]) {
                round.path.elementAt(j).edges.forEach { e -> append("\t|Take ${e.name} during ${e.length}\r\n")}
                j++
            }
            append("\r\n\tIt's now time to do the delivery $i (x:${round.deliveries.elementAt(i).address.x}, y:${round.deliveries.elementAt(i).address.y}) - Details:")
            append("\n\t\tStart at : ${getStartTime(round.deliveries.elementAt(i).startTime)}")
            append("\n\t\tFinish at : ${getEndTime(round.deliveries.elementAt(i).endTime)}")
            append("\n\t\tDuration = ${round.deliveries.elementAt(i).duration.length}")
            append("\r\n________________________________________________________________________\r\n")
        }
        append("It's nearly finished, to come back at the warehouse : \n\n")
        round.path.elementAt(j).edges.forEach { e -> append("\t|Take ${e.name} during ${e.length}\r\n")}
    }
}