package org.jfree.chart.plot

import java.io.Serializable
import org.jfree.text.TextBox

case class PieLabelRecord(key: Comparable[_], angle: Double, allocatedY:Double, baseY: Double, label: TextBox, labelHeight: Double, gap: Double, linkPercent: Double) extends Comparable[PieLabelRecord] with Serializable {

  def lowerY: Double = {
    return allocatedY - labelHeight / 2.0
  }

  def upperY: Double = {
    return allocatedY + labelHeight / 2.0
  }

  def compareTo(plr: PieLabelRecord): Int = {
    baseY.compareTo(plr.baseY)
  }

  override def toString: String = {
    return baseY + ", " + key.toString
  }
}

