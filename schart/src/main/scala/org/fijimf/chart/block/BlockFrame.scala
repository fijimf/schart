package org.fijimf.chart.block

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import org.jfree.ui.RectangleInsets

trait BlockFrame {
  def draw(g2: Graphics2D, area: Rectangle2D): Unit

  def insets: RectangleInsets
}

