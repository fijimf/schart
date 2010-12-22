package org.jfree.chart.plot
import java.io.Serializable

trait AbstractPieLabelDistributor extends Serializable {
  def labels: List[PieLabelRecord]

  def itemCount: Int = {
    labels.size
  }

  def distributeLabels(minY: Double, height: Double): AbstractPieLabelDistributor

  def addPieLabelRecord(record: PieLabelRecord): AbstractPieLabelDistributor

  def getPieLabelRecord(index: Int): PieLabelRecord = {
    labels(index)
  }
}