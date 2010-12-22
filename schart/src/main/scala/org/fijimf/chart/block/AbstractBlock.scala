package org.fijimf.chart.block

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import org.fijimf.data.Range
import org.jfree.ui.RectangleInsets
import org.jfree.ui.Size2D

case class AbstractBlock(id: String,
                         width: Double = 0.0,
                         height: Double = 0.0,
                         bounds: Double = 0.0,
                         margin: RectangleInsets = RectangleInsets.ZERO_INSETS,
                         frame: BlockBorder = BlockBorder.NONE,
                         padding: RectangleInsets = RectangleInsets.ZERO_INSETS
        ) {
  def getContentXOffset: Double = {
    margin.getLeft + frame.insets.getLeft + padding.getLeft
  }

  protected def trimBorder(area: Rectangle2D): Rectangle2D = {
    frame.getInsets.trim(area)
    area
  }

  protected def calculateTotalHeight(contentHeight: Double): Double = {
    margin.extendHeight(frame.insets.extendHeight(padding.extendHeight(contentHeight)))
  }

  def arrange(g2: Graphics2D, constraint: RectangleConstraint): Size2D = {
    constraint.calculateConstrainedSize(new Size2D(width, height))
  }

  def getContentYOffset: Double = {
    return margin.getTop + frame.insets.getTop + padding.getTop
  }

  def arrange(g2: Graphics2D): Size2D = {
    arrange(g2, RectangleConstraint.NONE)
  }

  protected def drawBorder(g2: Graphics2D, area: Rectangle2D): Unit = {
    frame.draw(g2, area)
  }

  protected def toContentConstraint(c: RectangleConstraint): RectangleConstraint = {
    var w: Double = c.getWidth
    var wr: Range = c.getWidthRange
    var h: Double = c.getHeight
    var hr: Range = c.getHeightRange
    var ww: Double = trimToContentWidth(w)
    var hh: Double = trimToContentHeight(h)
    var wwr: Range = trimToContentWidth(wr)
    var hhr: Range = trimToContentHeight(hr)
    return new RectangleConstraint(ww, wwr, c.getWidthConstraintType, hh, hhr, c.getHeightConstraintType)
  }

  private def trimToContentWidth(r: Range): Range = {
    var lowerBound: Double = 0.0
    var upperBound: Double = Double.POSITIVE_INFINITY
    if (r.lower > 0.0) {
      lowerBound = trimToContentWidth(r.lower)
    }
    if (r.upper < Double.POSITIVE_INFINITY) {
      upperBound = trimToContentWidth(r.upper)
    }
    Range(lowerBound, upperBound)
  }

  protected def trimToContentWidth(fixedWidth: Double): Double = {
    Math.max(padding.trimWidth(frame.insets.trimWidth(margin.trimWidth(fixedWidth))), 0.0)
  }


  protected def trimToContentHeight(fixedHeight: Double): Double = {
    Math.max(padding.trimHeight(frame.insets.trimHeight(margin.trimHeight(fixedHeight))), 0.0)
  }

  private def trimToContentHeight(r: Range): Range = {
    if (r == null) {
      return null
    }
    var lowerBound: Double = 0.0
    var upperBound: Double = Double.POSITIVE_INFINITY
    if (r.getLowerBound > 0.0) {
      lowerBound = trimToContentHeight(r.getLowerBound)
    }
    if (r.getUpperBound < Double.POSITIVE_INFINITY) {
      upperBound = trimToContentHeight(r.getUpperBound)
    }
    return new Range(lowerBound, upperBound)
  }

  protected def trimPadding(area: Rectangle2D): Rectangle2D = {
    this.padding.trim(area)
    return area
  }

  def getBorder: BlockBorder = {
    if (this.frame.isInstanceOf[BlockBorder]) {
      return this.frame.asInstanceOf[BlockBorder]
    }
    else {
      return null
    }
  }

  protected def calculateTotalWidth(contentWidth: Double): Double = {
    margin.extendWidth(frame.insets.extendWidth(padding.extendWidth(contentWidth)))
  }

  protected def trimMargin(area: Rectangle2D): Rectangle2D = {
    margin.trim(area)
    area
  }

}

