package org.jfree.chart.plot

case class PieLabelDistributor(labels: List[PieLabelRecord]) extends AbstractPieLabelDistributor {
  private val minGap: Double = 4.0

  private def isOverlap: Boolean = {
    if (labels.size < 2)
      false
    else
      labels.sliding(2).exists((rs: List[PieLabelRecord]) => rs(0).upperY > rs(1).lowerY)
  }

  protected def adjustUpwards(minY: Double, height: Double): PieLabelDistributor = {
    PieLabelDistributor(labels.head :: labels.sliding(2).map((rs: List[PieLabelRecord]) => {
      var record0 = rs(1)
      var record1 = rs(0)
      if (record1.upperY > record0.lowerY) {
        record1.copy(allocatedY = (Math.max(minY + record1.labelHeight / 2.0, record0.lowerY - minGap - record1.labelHeight / 2.0)))
      } else {
        record1
      }
    }).toList)
  }

  protected def adjustDownwards(minY: Double, height: Double): PieLabelDistributor = {
    PieLabelDistributor(labels.head :: labels.sliding(2).map((rs: List[PieLabelRecord]) => {
      var record0 = rs(1)
      var record1 = rs(0)
      if (record1.upperY > record0.lowerY) {
        record1.copy(allocatedY = (Math.min(minY + height - record1.labelHeight / 2.0, record0.upperY + this.minGap + record1.labelHeight / 2.0)))
      } else {
        record1
      }
    }).toList)
  }

  protected def spreadEvenly(minY: Double, height: Double): PieLabelDistributor = {
    val y = minY
    val sumOfLabelHeights: Double = labels.foldLeft[Double](0)(_ + _.labelHeight)
    val gap = if (labels.size > 1) {
      height - sumOfLabelHeights
    } else {
      (height - sumOfLabelHeights) / (labels.size - 1)
    }

    val z = Pair(minY, List.empty[PieLabelRecord])
    PieLabelDistributor(labels.foldLeft[Pair[Double, List[PieLabelRecord]]](z)((p: Pair[Double, List[PieLabelRecord]], plr: PieLabelRecord) =>
      {
        (p._1 + plr.labelHeight + gap, plr.copy(allocatedY = (p._1 + plr.labelHeight / 2.0)) :: p._2)
      })._2)

  }

  def addPieLabelRecord(record: PieLabelRecord): AbstractPieLabelDistributor = {
    PieLabelDistributor(record :: labels)
  }

  /**
   * Adjusts the y-coordinate for the labels in towards the center in an
   * attempt to fix overlapping.
   */
  //NOt used in the Java library
  protected def adjustInwards: Unit = {
    //var lower: Int = 0
    //var upper: Int = this.labels.size - 1
    //while (upper > lower) {
    //if (lower < upper - 1) {
    //var r0: PieLabelRecord = getPieLabelRecord (lower)
    //var r1: PieLabelRecord = getPieLabelRecord (lower + 1)
    //if (r1.getLowerY < r0.getUpperY) {
    //var adjust: Double = r0.getUpperY - r1.getLowerY + this.minGap
    //r1.setAllocatedY (r1.getAllocatedY + adjust)
    //}
    //}
    //var r2: PieLabelRecord = getPieLabelRecord (upper - 1)
    //var r3: PieLabelRecord = getPieLabelRecord (upper)
    //if (r2.getUpperY > r3.getLowerY) {
    //var adjust: Double = (r2.getUpperY - r3.getLowerY) + this.minGap
    //r3.setAllocatedY (r3.getAllocatedY + adjust)
    //}
    //( {
    //lower += 1;
    //lower
    //})
    //( {
    //upper -= 1;
    //upper
    //})
    //}
  }

  /**
   * Returns a string containing a description of the object for
   * debugging purposes.
   *
   * @return A string.
   */
  override def toString: String = {
    labels.mkString("\n")
  }

  def distributeLabels(minY: Double, height: Double): PieLabelDistributor = {
    if (isOverlap) {
      val d = adjustDownwards(minY, height)
      if (d.isOverlap) {
        val u = adjustUpwards(minY, height)
        if (u.isOverlap) {
          u.spreadEvenly(minY, height)
        } else {
          u
        }
      } else {
        d
      }

    } else {
      this
    }
  }

}
