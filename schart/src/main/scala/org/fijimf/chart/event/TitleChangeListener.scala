package org.jfree.chart.event

import java.util.EventListener
trait TitleChangeListener extends EventListener {
  def titleChanged(event: TitleChangeEvent): Unit
}

