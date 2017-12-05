package fr.insalyon.pld.agile.util.txt

import fr.insalyon.pld.agile.getResource
import fr.insalyon.pld.agile.model.*
import org.apache.commons.io.FileUtils
import java.awt.Desktop
import java.io.File

class RoadSheetSerializer(){
    lateinit var roadSheet: File

    private fun append(text: String){
        roadSheet.appendText(text )
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
        var warehouseDescription: String = "You have to leave the warehouse (x:${round.warehouse.address.x}, y:${round.warehouse.address.y}) at ${round.warehouse.departureHour.toFormattedString()} "
        append(warehouseDescription+"\r\n________________________________________________________________________\r\n")

        var j=0
        for(i in 0 until round.deliveries().size){
            append("You have to : \n\n")
            while(j<round.distancePathInMeters().size && round.deliveries().elementAt(i).address != round.distancePathInMeters().elementAt(j).nodes[0]) {
                round.distancePathInMeters().elementAt(j).edges.forEach { e -> append("\t|Take ${e.name} during ${e.length.dam.to(Distance.DistanceUnit.M).value} m\r\n")}
                j++
            }
            append("\r\n\tIt's now time to do the delivery $i (x:${round.deliveries().elementAt(i).address.x}, y:${round.deliveries().elementAt(i).address.y}) - Details:")
            append("\n\t\tStart at : ${getStartTime(round.deliveries().elementAt(i).startTime)}")
            append("\n\t\tFinish at : ${getEndTime(round.deliveries().elementAt(i).endTime)}")
            append("\n\t\tDuration : ${round.deliveries().elementAt(i).duration.toString()}")
            append("\r\n________________________________________________________________________\r\n")
        }
        append("It's nearly finished, to come back at the warehouse : \n\n")
        round.distancePathInMeters().elementAt(j).edges.forEach { e -> append("\t|Take ${e.name} during e.length.dam.to(Distance.DistanceUnit.M).value} m\r\n")}
    }

    /*
        Method that implement the html roadsheet with the calculated round
     */
    fun serializeHTML(round: Round) {

        //Open the file TemplateFeuilleDeRoute.html and parses it to String
        val htmlTemplateFile = getResource("html/TemplateFeuilleDeRoute.html")
        var htmlString = FileUtils.readFileToString(htmlTemplateFile)

        //Replacing fields in the template by the effective data
        val startTime = "" +round.warehouse.departureHour.toFormattedString()
        val nbDeliveries = "" + round.deliveries().size
        var currentTime = round.warehouse.departureHour
        var rows = "<div class=\"row\">\n" +
            "            <div class=\"time left\">\n" +
            "                timexxx -\n" +
            "            </div>\n" +
            "            <div class=\"img left\">\n" +
            "                <img src=\"./img/index.png\" width=\"36\" height=\"30\">\n" +
            "            </div>\n" +
            "            <div class=\"localisation\">\n" +
            "                Entrepôt numb\n" +
            "            </div>\n" +
            "        </div>"
        rows = rows.replace("timexxx", startTime)


        //Getting all the roads that the delivery man will have to take to get to the first delivery point
        var y=0
        var infoL = "${round.warehouse.address.id}<br><br>"
        while(y<round.distancePathInMeters().size && round.deliveries().elementAt(0).address != round.distancePathInMeters().elementAt(y).nodes[0]) {
            var distance = 0.0
            round.distancePathInMeters().elementAt(y).edges.forEachIndexed { index, it ->
                distance += it.length.toDouble()
                if (!(index + 1 < round.distancePathInMeters().elementAt(y).edges.size && it.name.equals(round.distancePathInMeters().elementAt(y).edges.elementAt(index + 1).name))) {
                    var distances = "${distance} m"
                    if(distance > 999){
                        distance /= 1000.0
                        distances = "${distance} km"
                    }
                    infoL += "\t Prends  ${it.name} sur ${distances}<br>"
                    distance = 0.0
                }
            }
            y++
        }
        rows = rows.replace("numb", infoL)

        //Getting all the roads that the delivery man will have to take to get to each delivery points except the first
        for (i in 0 until round.deliveries().size){
            if(i>0){
                currentTime += round.deliveries().elementAt(i-1).duration
            }
            currentTime += round.durationPathInSeconds().elementAt(i)
            var waitingTime: Duration? = null
            if(round.deliveries().elementAt(i).startTime != null && round.deliveries().elementAt(i).startTime!! > currentTime) {
                waitingTime = round.deliveries().elementAt(i).startTime!! - currentTime
                currentTime = round.deliveries().elementAt(i).startTime!!
            }
            var infoLivraison : String = "Livraison au point("+round.deliveries().elementAt(i).address.x!!+","+round.deliveries().elementAt(i).address.y+") durant ${round.deliveries().elementAt(i).duration}<br><br>"
            var j=i+1
           while(j<round.distancePathInMeters().size && round.deliveries().elementAt(i).address != round.distancePathInMeters().elementAt(j-1).nodes[0]) {
                var distance = 0.0
                round.distancePathInMeters().elementAt(j).edges.forEachIndexed { index, it ->
                    distance += it.length.toDouble()
                    if (!(index + 1 < round.distancePathInMeters().elementAt(j).edges.size && it.name.equals(round.distancePathInMeters().elementAt(j).edges.elementAt(index + 1).name))) {
                        var distances = "${distance} m"
                        if(distance > 999){
                            distance /= 1000.0
                            distances = "${distance} km"
                        }
                        infoLivraison += "\t Prends  ${it.name} sur ${distances}<br>"
                        distance = 0.0
                    }
                }
                j++
            }
            rows +="<div class=\"row\">\n" +
                "            <div class=\"time left\">\n" +
                "                timexxx -\n" +
                "            </div>\n" +
                "            <div class=\"img left\">\n" +
                "                <img src=\"./img/index.png\" width=\"36\" height=\"30\">\n" +
                "            </div>\n" +
                "            <div class=\"localisation\">\n" +
                "                localisationxxx\n" +
                "            </div>\n" +
                "        </div>"
            rows = rows.replace("timexxx", ""+currentTime.toFormattedString())
            rows = rows.replace("localisationxxx", infoLivraison)
        }

        //Getting the time when the delivery man will end his delivery and arrive at the warehouse
        currentTime += round.deliveries().last().duration
        currentTime += round.durationPathInSeconds().last()
        rows+= "<div class=\"row\">\n" +
            "            <div class=\"time left\">\n" +
            "                timexxx -\n" +
            "            </div>\n" +
            "            <div class=\"img left\">\n" +
            "                <img src=\"./img/index.png\" width=\"36\" height=\"30\">\n" +
            "            </div>\n" +
            "            <div class=\"localisation\">\n" +
            "                Entrepôt numb\n" +
            "            </div>\n" +
            "        </div>"
        rows = rows.replace("timexxx", currentTime.toFormattedString())
        rows = rows.replace("numb", ""+round.warehouse.address.id)
        htmlString = htmlString.replace("\$startTime", startTime)
        htmlString = htmlString.replace("\$endTime", currentTime.toFormattedString())
        htmlString = htmlString.replace("\$rows", rows)
        htmlString = htmlString.replace("\$nbDeliveries", nbDeliveries)

        //Writting the file FeuilleDeRoute.html with the implemented String
        val newHtmlFile = File("src/main/dist/FeuilleDeRoute.html")
        FileUtils.writeStringToFile(newHtmlFile, htmlString)

        //Browsing the RoadSheet
        Desktop.getDesktop().browse(newHtmlFile.toURI())
    }
}