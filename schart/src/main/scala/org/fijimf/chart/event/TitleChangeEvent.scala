package org.fijimf.chart.event

import org.jfree.chart.title.Title
case class TitleChangeEvent(title:Title) extends ChartChangeEvent {
  private var title: Title = null

  def getTitle: Title = {
    return this.title
  }
