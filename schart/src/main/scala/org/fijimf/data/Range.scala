package org.fijimf.data

object Range {
  def expandToInclude(range: Range, value: Double): Range =  Range(Math.min(range.lower,value), Math.max(range.upper,value))

  private def shiftWithNoZeroCrossing(value: Double, delta: Double): Double = {
    if (value > 0.0) {
      Math.max(value + delta, 0.0)
    }
    else if (value < 0.0) {
      Math.min(value + delta, 0.0)
    }
    else {
      value + delta
    }
  }

  def combine(range1: Range, range2: Range): Range = Range(Math.min(range1.lower, range2.lower), Math.max(range1.upper, range2.upper))

  def shift(base: Range, delta: Double, allowZeroCrossing: Boolean): Range = {
    if (allowZeroCrossing) {
      Range(base.getLowerBound + delta, base.upper + delta)
    }
    else {
      Range(shiftWithNoZeroCrossing(base.getLowerBound, delta), shiftWithNoZeroCrossing(base.upper, delta))
    }
  }

  def scale(base: Range, factor: Double): Range = {
    require(factor > 0)
    Range(base.getLowerBound * factor, base.upper * factor)
  }

  def shift(base: Range, delta: Double): Range = shift(base, delta, false)

  def expand(range: Range, lowerMargin: Double, upperMargin: Double): Range = {
    var lower = range.lower - range.length * lowerMargin
    var upper = range.upper + range.length * upperMargin
    if (lower > upper) {
      lower = lower / 2.0 + upper / 2.0
      upper = lower
    }
    Range(lower, upper)
  }
}

case class Range(lower: Double, upper: Double) {
  require(lower <= upper)
  def centralValue: Double = lower / 2.0 + upper / 2.0

  def length: Double = upper - lower

  def intersects(range: Range): Boolean = intersects(range.lower, range.upper)

  def constrain(value: Double): Double = {
    if (value > upper) {
      upper
    } else if (value < lower) {
      lower
    } else {
      value
    }
  }

  def intersects(low: Double, up: Double): Boolean = {
    require(low < up)
    contains(low) || contains(up) || (low < lower && up > upper)
  }

  def contains(value: Double): Boolean = {
    return (value >= this.lower && value <= this.upper)
  }

}