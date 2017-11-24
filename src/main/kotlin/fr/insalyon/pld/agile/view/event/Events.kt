package fr.insalyon.pld.agile.view.event

import javafx.scene.paint.Color
import tornadofx.FXEvent


class HighlightLocationEvent(var id: String, var isWarehouse: Boolean) : FXEvent()

class HighlightLocationInListEvent(var id: String, var color:Color) : FXEvent()