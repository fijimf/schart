package org.fijimf.chart.event

import java.util.EventObject

class ChartChangeEvent(val source:AnyRef, val chart:Option[SFreeChart], val eventType:Option[ChartChangeEventType]) extends EventObject 
