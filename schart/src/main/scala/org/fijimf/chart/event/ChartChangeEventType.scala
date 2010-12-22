package org.fijimf.chart.event

sealed trait ChartChangeEventType {
  case object NewDataset extends ChartChangeEventType
  case object General extends ChartChangeEventType
  case object DatasetUpdated extends ChartChangeEventType
}

