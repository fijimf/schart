package org.fijimf.chart.block


import org.jfree.data.Range
import org.jfree.ui.Size2D

case class RectangleConstraint(width: LengthConstraint = Unconstrained, height: LengthConstraint) {
  def toFixedWidth(w: Double): RectangleConstraint = copy(width = FixedLength(w))

  def toRangeWidth(r: Range): RectangleConstraint = copy(width = RangeLength(r))

  def toUnconstrainedWidth: RectangleConstraint = copy(width = Unconstrained)

  def toFixedHeight(h: Double): RectangleConstraint = copy(height = FixedLength(h))

  def toRangeHeight(r: Range): RectangleConstraint = copy(height = RangeLength(r))

  def toUnconstrainedHeight: RectangleConstraint = copy(height = Unconstrained)

  def constrainDimension(c: LengthConstraint, d: Double): Double = {
    c match {
      case Unconstrained => d
      case FixedLength(x) => x
      case RangeLength(r) => r.constrain(d)
    }
  }

  def calculateConstrainedSize(base: Size2D): Size2D = {
    new Size2D(constrainDimension(width, base.getWidth), constraintDimension(height, base.getHeight))
  }
}
