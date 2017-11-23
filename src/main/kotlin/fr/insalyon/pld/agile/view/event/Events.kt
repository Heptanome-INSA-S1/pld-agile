package fr.insalyon.pld.agile.view.event

import tornadofx.FXEvent


class HighlightLocationEvent(var id: String, var isWarehouse: Boolean) : FXEvent()