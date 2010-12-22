package org.fijimf.chart.block

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import org.jfree.ui.Drawable
import org.jfree.ui.Size2D

trait Block extends Drawable {
  def arrange(g2: Graphics2D): Size2D
  def arrange(g2: Graphics2D, constraint: RectangleConstraint): Size2D
  def bounds: Rectangle2D
  def draw(g2: Graphics2D, area: Rectangle2D, params: AnyRef): AnyRef
  def id: String
}

