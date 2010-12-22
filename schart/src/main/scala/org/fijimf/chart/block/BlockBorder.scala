package org.fijimf.chart.block

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.geom.Rectangle2D
import org.jfree.ui.RectangleInsets

object BlockBorder {
  val NONE: BlockBorder = new BlockBorder(RectangleInsets.ZERO_INSETS, Color.white)

  implicit def tuple2Insets(t: Double, l: Double, b: Double, r: Double) = new RectangleInsets(t, l, b, r)
}

case class BlockBorder(insets: RectangleInsets = new RectangleInsets(1, 1, 1, 1), paint: Paint = Color.Black) extends BlockFrame {
  def draw(g2: Graphics2D, area: Rectangle2D): Unit = {
    var t: Double = insets.calculateTopInset(area.getHeight)
    var b: Double = insets.calculateBottomInset(area.getHeight)
    var l: Double = insets.calculateLeftInset(area.getWidth)
    var r: Double = insets.calculateRightInset(area.getWidth)
    var x: Double = area.getX
    var y: Double = area.getY
    var w: Double = area.getWidth
    var h: Double = area.getHeight
    g2.setPaint(this.paint)
    var rect: Rectangle2D = new Double
    if (t > 0.0) {
      rect.setRect(x, y, w, t)
      g2.fill(rect)
    }
    if (b > 0.0) {
      rect.setRect(x, y + h - b, w, b)
      g2.fill(rect)
    }
    if (l > 0.0) {
      rect.setRect(x, y, l, h)
      g2.fill(rect)
    }
    if (r > 0.0) {
      rect.setRect(x + w - r, y, r, h)
      g2.fill(rect)
    }
  }
}

