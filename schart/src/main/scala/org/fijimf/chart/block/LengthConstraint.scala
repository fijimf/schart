package org.fijimf.chart.block

import org.fijimf.data.Range

abstract sealed class LengthConstraint     {
  def constrain(y:Double):Double
}
case class FixedLength(x:Double)  extends LengthConstraint{
  def constrain(y: Double):Double = x
}
case object Unconstrained  extends LengthConstraint{
  def constrain(y: Double) = y
}
case class RangeLength(r:Range) extends LengthConstraint{
  def constrain(y: Double) = r.con
}