package org.fijimf.chart


import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Composite
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Paint
import java.awt.RenderingHints
import java.awt.Shape
import java.awt.Stroke
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.URL
import java.util.ArrayList
import java.util.Arrays
import java.util.Iterator
import java.util.List
import java.util.ResourceBundle
import javax.swing.ImageIcon
import javax.swing.UIManager
import javax.swing.event.EventListenerList
import org.jfree.JCommon
import org.jfree.chart.block.BlockParams
import org.jfree.chart.block.EntityBlockResult
import org.jfree.chart.block.LengthConstraintType
import org.jfree.chart.block.LineBorder
import org.jfree.chart.block.RectangleConstraint
import org.jfree.chart.entity.EntityCollection
import org.jfree.chart.entity.JFreeChartEntity
import org.jfree.chart.event.ChartChangeEvent
import org.jfree.chart.event.ChartChangeListener
import org.jfree.chart.event.ChartProgressEvent
import org.jfree.chart.event.ChartProgressListener
import org.jfree.chart.event.PlotChangeEvent
import org.jfree.chart.event.PlotChangeListener
import org.jfree.chart.event.TitleChangeEvent
import org.jfree.chart.event.TitleChangeListener
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.Plot
import org.jfree.chart.plot.PlotRenderingInfo
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.title.LegendTitle
import org.jfree.chart.title.TextTitle
import org.jfree.chart.title.Title
import org.jfree.chart.util.ResourceBundleWrapper
import org.jfree.data.Range
import org.jfree.io.SerialUtilities
import org.jfree.ui.Align
import org.jfree.ui.Drawable
import org.jfree.ui.HorizontalAlignment
import org.jfree.ui.RectangleEdge
import org.jfree.ui.RectangleInsets
import org.jfree.ui.Size2D
import org.jfree.ui.VerticalAlignment
import org.jfree.ui.about.Contributor
import org.jfree.ui.about.Licences
import org.jfree.ui.about.ProjectInfo
import org.jfree.util.ObjectUtilities
import org.jfree.util.PaintUtilities


object JFreeChart {
  val DEFAULT_BACKGROUND_PAINT: Paint = UIManager.getColor("Panel.background")

  val DEFAULT_BACKGROUND_IMAGE_ALIGNMENT: Int = Align.FIT

  val DEFAULT_TITLE_FONT: Font = new Font("SansSerif", Font.BOLD, 18)

  val INFO: ProjectInfo = new JFreeChartInfo

  def main(args: Array[String]): Unit = {
    System.out.println(SFreeChart.INFO.toString)
  }

  val DEFAULT_BACKGROUND_IMAGE: Option[Image] = None

  val DEFAULT_BACKGROUND_IMAGE_ALPHA: Float = 0.5f
}
class SFreeChart extends Drawable with TitleChangeListener with PlotChangeListener with Serializable with Cloneable {
  /**The alpha transparency for the background image. */
  private var backgroundImageAlpha: Float = 0.5f

  /**
   * Returns the paint used for the chart background.
   *
   * @return The paint (possibly <code>null</code>).
   *
   * @see # setBackgroundPaint ( Paint )
   */
  def getBackgroundPaint: Paint = {
    return this.backgroundPaint
  }

  /**
   * Sets a flag that controls whether or not listeners receive
   * { @link ChartChangeEvent } notifications.
   *
   * @param notify a boolean.
   *
   * @see # isNotify ( )
   */
  def setNotify(notify: Boolean): Unit = {
    this.notify = notify
    if (notify) {
      notifyListeners(new ChartChangeEvent(this))
    }
  }

  /**
   * Returns the legend for the chart, if there is one.  Note that a chart
   * can have more than one legend - this method returns the first.
   *
   * @return The legend (possibly <code>null</code>).
   *
   * @see # getLegend ( int )
   */
  def getLegend: LegendTitle = {
    return getLegend(0)
  }

  /**The stroke used to draw the chart border (if visible). */
  @transient
  private var borderStroke: Stroke = null

  /**
   * Sets the title list for the chart (completely replaces any existing
   * titles) and sends a  { @link ChartChangeEvent } to all registered
   * listeners.
   *
   * @param subtitles the new list of subtitles (<code>null</code> not
   *                   permitted).
   *
   * @see # getSubtitles ( )
   */
  def setSubtitles(subtitles: List[_]): Unit = {
    if (subtitles == null) {
      throw new NullPointerException("Null 'subtitles' argument.")
    }
    setNotify(false)
    clearSubtitles
    var iterator: Iterator[_] = subtitles.iterator
    while (iterator.hasNext) {
      var t: Title = iterator.next.asInstanceOf[Title]
      if (t != null) {
        addSubtitle(t)
      }
    }
    setNotify(true)
  }

  /**
   * Returns the list of subtitles for the chart.
   *
   * @return The subtitle list (possibly empty, but never <code>null</code>).
   *
   * @see # setSubtitles ( List )
   */
  def getSubtitles: List[_] = {
    return new ArrayList[_](this.subtitles)
  }

  /**
   * Sets the stroke used to draw the chart border (if visible).
   *
   * @param stroke the stroke.
   *
   * @see # getBorderStroke ( )
   */
  def setBorderStroke(stroke: Stroke): Unit = {
    this.borderStroke = stroke
    fireChartChanged
  }

  /**
   * Registers an object for notification of progress events relating to the
   * chart.
   *
   * @param listener the object being registered.
   *
   * @see # removeProgressListener ( ChartProgressListener )
   */
  def addProgressListener(listener: ChartProgressListener): Unit = {
    this.progressListeners.add(classOf[ChartProgressListener], listener)
  }

  /**
   * Receives notification that a chart title has changed, and passes this
   * on to registered listeners.
   *
   * @param event information about the chart title change.
   */
  def titleChanged(event: TitleChangeEvent): Unit = {
    event.setChart(this)
    notifyListeners(event)
  }

  /**An optional background image for the chart. */
  @transient
  private var backgroundImage: Image = null

  /**
   * Creates a new chart with the given title and plot.  The
   * <code>createLegend</code> argument specifies whether or not a legend
   * should be added to the chart.
   * <br><br>
   * Note that the   { @link ChartFactory } class contains a range
   * of static methods that will return ready-made charts, and often this
   * is a more convenient way to create charts than using this constructor.
   *
   * @param title the chart title (<code>null</code> permitted).
   * @param titleFont the font for displaying the chart title
   *                   (<code>null</code> permitted).
   * @param plot controller of the visual representation of the data
   *              (<code>null</code> not permitted).
   * @param createLegend a flag indicating whether or not a legend should
   *                      be created for the chart.
   */
  def this(title: String, titleFont: Font, plot: Plot, createLegend: Boolean) {
    this ()
    if (plot == null) {
      throw new NullPointerException("Null 'plot' argument.")
    }
    this.progressListeners = new EventListenerList
    this.changeListeners = new EventListenerList
    this.notify = true
    this.renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    this.borderVisible = false
    this.borderStroke = new BasicStroke(1.0f)
    this.borderPaint = Color.black
    this.padding = RectangleInsets.ZERO_INSETS
    this.plot = plot
    plot.addChangeListener(this)
    this.subtitles = new ArrayList[_]
    if (createLegend) {
      var legend: LegendTitle = new LegendTitle(this.plot)
      legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0))
      legend.setFrame(new LineBorder)
      legend.setBackgroundPaint(Color.white)
      legend.setPosition(RectangleEdge.BOTTOM)
      this.subtitles.add(legend)
      legend.addChangeListener(this)
    }
    if (title != null) {
      if (titleFont == null) {
        titleFont = DEFAULT_TITLE_FONT
      }
      this.title = new TextTitle(title, titleFont)
      this.title.addChangeListener(this)
    }
    this.backgroundPaint = DEFAULT_BACKGROUND_PAINT
    this.backgroundImage = DEFAULT_BACKGROUND_IMAGE
    this.backgroundImageAlignment = DEFAULT_BACKGROUND_IMAGE_ALIGNMENT
    this.backgroundImageAlpha = DEFAULT_BACKGROUND_IMAGE_ALPHA

    /**
     * Returns the plot for the chart.  The plot is a class responsible for
     * coordinating the visual representation of the data, including the axes
     * (if any).
     *
     * @return The plot.
     */
    def getPlot: Plot = {
      return this.plot
    }

    /**
     * Clears all subtitles from the chart and sends a  { @link ChartChangeEvent }
     * to all registered listeners.
     *
     * @see # addSubtitle ( Title )
     */
    def clearSubtitles: Unit = {
      var iterator: Iterator[_] = this.subtitles.iterator
      while (iterator.hasNext) {
        var t: Title = iterator.next.asInstanceOf[Title]
        t.removeChangeListener(this)
      }
      this.subtitles.clear
      fireChartChanged
    }

    /**
     * Draws the chart on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * This method is the focus of the entire SFreeChart library.
     *
     * @param g2 the graphics device.
     * @param area the area within which the chart should be drawn.
     */
    def draw(g2: Graphics2D, area: Rectangle2D): Unit = {
      draw(g2, area, null, null)
    }

    /**
     * Sends a  { @link ChartChangeEvent } to all registered listeners.
     *
     * @param event information about the event that triggered the
     *               notification.
     */
    protected def notifyListeners(event: ChartChangeEvent): Unit = {
  if (this.notify) {
    var listeners: Array[AnyRef] = this.changeListeners.getListenerList

    {
      var i: Int = listeners.length - 2
      while (i >= 0) {
        {
          if (listeners(i) == classOf[ChartChangeListener]) {
            (listeners(i + 1).asInstanceOf[ChartChangeListener]).chartChanged(event)
          }
        }
        i -= 2
      }
    }
  }
}

/**
 * Removes the specified subtitle and sends a  { @link ChartChangeEvent } to
 * all registered listeners.
 *
 * @param title the title.
 *
 * @see # addSubtitle ( Title )
 */
def removeSubtitle (title: Title): Unit = {
this.subtitles.remove (title)
fireChartChanged
}

/**Paint used to draw the background of the chart. */
@transient
private var backgroundPaint: Paint = null

/**
 * Receives notification that the plot has changed, and passes this on to
 * registered listeners.
 *
 * @param event information about the plot change.
 */
def plotChanged (event: PlotChangeEvent): Unit = {
event.setChart (this)
notifyListeners (event)
}

/**
 * The chart subtitles (zero, one or many).  This field should never be
 * <code>null</code>.
 */
private var subtitles: List[_] = null

/**
 * Sets the main title for the chart and sends a  { @link ChartChangeEvent }
 * to all registered listeners.  If you do not want a title for the
 * chart, set it to <code>null</code>.  If you want more than one title on
 * a chart, use the  { @link # addSubtitle ( Title ) } method.
 *
 * @param title the title (<code>null</code> permitted).
 *
 * @see # getTitle ( )
 */
def setTitle (title: TextTitle): Unit = {
if (this.title != null) {
this.title.removeChangeListener (this)
}
this.title = title
if (title != null) {
title.addChangeListener (this)
}
fireChartChanged
}

/**
 * Adds a chart subtitle, and notifies registered listeners that the chart
 * has been modified.
 *
 * @param subtitle the subtitle (<code>null</code> not permitted).
 *
 * @see # getSubtitle ( int )
 */
def addSubtitle (subtitle: Title): Unit = {
if (subtitle == null) {
throw new IllegalArgumentException ("Null 'subtitle' argument.")
}
this.subtitles.add (subtitle)
subtitle.addChangeListener (this)
fireChartChanged
}

/**
 * Creates and returns a buffered image into which the chart has been drawn.
 *
 * @param width the width.
 * @param height the height.
 *
 * @return A buffered image.
 */
def createBufferedImage (width: Int, height: Int): BufferedImage = {
return createBufferedImage (width, height, null)
}

/**Storage for registered change listeners. */
@transient
private var changeListeners: EventListenerList = null

/**
 * Provides serialization support.
 *
 * @param stream the input stream.
 *
 * @throws IOException if there is an I/O error.
 * @throws ClassNotFoundException if there is a classpath problem.
 */
private def readObject (stream: ObjectInputStream): Unit = {
stream.defaultReadObject
this.borderStroke = SerialUtilities.readStroke (stream)
this.borderPaint = SerialUtilities.readPaint (stream)
this.backgroundPaint = SerialUtilities.readPaint (stream)
this.progressListeners = new EventListenerList
this.changeListeners = new EventListenerList
this.renderingHints = new RenderingHints (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
if (this.title != null) {
this.title.addChangeListener (this)
}

{
var i: Int = 0
while (i < getSubtitleCount) {
{
getSubtitle (i).addChangeListener (this)
}
( {
i += 1;
i
})
}
}
this.plot.addChangeListener (this)
}

/**Draws the visual representation of the data. */
private var plot: Plot = null

/**
 * Returns a flag that controls whether or not change events are sent to
 * registered listeners.
 *
 * @return A boolean.
 *
 * @see # setNotify ( boolean )
 */
def isNotify: Boolean = {
return this.notify
}

/**
 * Creates a rectangle that is aligned to the frame.
 *
 * @param dimensions the dimensions for the rectangle.
 * @param frame the frame to align to.
 * @param hAlign the horizontal alignment.
 * @param vAlign the vertical alignment.
 *
 * @return A rectangle.
 */
private def createAlignedRectangle2D (dimensions: Size2D, frame: Rectangle2D, hAlign: HorizontalAlignment, vAlign: VerticalAlignment): Rectangle2D = {
var x: Double = Double.NaN
var y: Double = Double.NaN
if (hAlign == HorizontalAlignment.LEFT) {
x = frame.getX
}
else if (hAlign == HorizontalAlignment.CENTER) {
x = frame.getCenterX - (dimensions.width / 2.0)
}
else if (hAlign == HorizontalAlignment.RIGHT) {
x = frame.getMaxX - dimensions.width
}
if (vAlign == VerticalAlignment.TOP) {
y = frame.getY
}
else if (vAlign == VerticalAlignment.CENTER) {
y = frame.getCenterY - (dimensions.height / 2.0)
}
else if (vAlign == VerticalAlignment.BOTTOM) {
y = frame.getMaxY - dimensions.height
}
return new Double (x, y, dimensions.width, dimensions.height)
}

/**
 * Returns a flag that indicates whether or not anti-aliasing is used when
 * the chart is drawn.
 *
 * @return The flag.
 *
 * @see # setAntiAlias ( boolean )
 */
def getAntiAlias: Boolean = {
var val: AnyRef = this.renderingHints.get (RenderingHints.KEY_ANTIALIASING)
return RenderingHints.VALUE_ANTIALIAS_ON.equals (`val`)
}

/**
 * Registers an object for notification of changes to the chart.
 *
 * @param listener the listener (<code>null</code> not permitted).
 *
 * @see # removeChangeListener ( ChartChangeListener )
 */
def addChangeListener (listener: ChartChangeListener): Unit = {
if (listener == null) {
throw new IllegalArgumentException ("Null 'listener' argument.")
}
this.changeListeners.add (classOf[ChartChangeListener], listener)
}

/**
 * Returns the number of titles for the chart.
 *
 * @return The number of titles for the chart.
 *
 * @see # getSubtitles ( )
 */
def getSubtitleCount: Int = {
return this.subtitles.size
}

/**
 * Creates a new chart based on the supplied plot.  The chart will have
 * a legend added automatically, but no title (although you can easily add
 * one later).
 * <br><br>
 * Note that the   { @link ChartFactory } class contains a range
 * of static methods that will return ready-made charts, and often this
 * is a more convenient way to create charts than using this constructor.
 *
 * @param plot the plot (<code>null</code> not permitted).
 */
def this (plot: Plot) {
this ()
`this` (null, null, plot, true)

/**
 * Provides serialization support.
 *
 * @param stream the output stream.
 *
 * @throws IOException if there is an I/O error.
 */
private def writeObject (stream: ObjectOutputStream): Unit = {
stream.defaultWriteObject
SerialUtilities.writeStroke (this.borderStroke, stream)
SerialUtilities.writePaint (this.borderPaint, stream)
SerialUtilities.writePaint (this.backgroundPaint, stream)
}

/**Storage for registered progress listeners. */
@transient
private var progressListeners: EventListenerList = null

/**
 * Sets the paint used to draw the chart border (if visible).
 *
 * @param paint the paint.
 *
 * @see # getBorderPaint ( )
 */
def setBorderPaint (paint: Paint): Unit = {
this.borderPaint = paint
fireChartChanged
}

/**
 * Returns the alpha-transparency for the chart's background image.
 *
 * @return The alpha-transparency.
 *
 * @see # setBackgroundImageAlpha ( float )
 */
def getBackgroundImageAlpha: Float = {
return this.backgroundImageAlpha
}

/**
 * Adds a subtitle at a particular position in the subtitle list, and sends
 * a  { @link ChartChangeEvent } to all registered listeners.
 *
 * @param index the index (in the range 0 to  { @link # getSubtitleCount ( ) } ).
 * @param subtitle the subtitle to add (<code>null</code> not permitted).
 *
 * @since 1.0.6
 */
def addSubtitle (index: Int, subtitle: Title): Unit = {
if (index < 0 || index > getSubtitleCount) {
throw new IllegalArgumentException ("The 'index' argument is out of range.")
}
if (subtitle == null) {
throw new IllegalArgumentException ("Null 'subtitle' argument.")
}
this.subtitles.add (index, subtitle)
subtitle.addChangeListener (this)
fireChartChanged
}

/**The alignment for the background image. */
private var backgroundImageAlignment: Int = Align.FIT

/**
 * Returns the paint used to draw the chart border (if visible).
 *
 * @return The border paint.
 *
 * @see # setBorderPaint ( Paint )
 */
def getBorderPaint: Paint = {
return this.borderPaint
}

/**
 * Returns the current value stored in the rendering hints table for
 * { @link RenderingHints # KEY_TEXT_ANTIALIASING }.
 *
 * @return The hint value (possibly <code>null</code>).
 *
 * @since 1.0.5
 *
 * @see # setTextAntiAlias ( Object )
 */
def getTextAntiAlias: AnyRef = {
return this.renderingHints.get (RenderingHints.KEY_TEXT_ANTIALIASING)
}

/**
 * Returns the main chart title.  Very often a chart will have just one
 * title, so we make this case simple by providing accessor methods for
 * the main title.  However, multiple titles are supported - see the
 * { @link # addSubtitle ( Title ) } method.
 *
 * @return The chart title (possibly <code>null</code>).
 *
 * @see # setTitle ( TextTitle )
 */
def getTitle: TextTitle = {
return this.title
}

/**The paint used to draw the chart border (if visible). */
@transient
private var borderPaint: Paint = null

/**
 * Sets the paint used to fill the chart background and sends a
 * { @link ChartChangeEvent } to all registered listeners.
 *
 * @param paint the paint (<code>null</code> permitted).
 *
 * @see # getBackgroundPaint ( )
 */
def setBackgroundPaint (paint: Paint): Unit = {
if (this.backgroundPaint != null) {
if (! this.backgroundPaint.equals (paint) ) {
this.backgroundPaint = paint
fireChartChanged
}
}
else {
if (paint != null) {
this.backgroundPaint = paint
fireChartChanged
}
}
}

/**
 * Sends a default  { @link ChartChangeEvent } to all registered listeners.
 * <P>
 * This method is for convenience only.
 */
def fireChartChanged: Unit = {
var event: ChartChangeEvent = new ChartChangeEvent (this)
notifyListeners (event)
}

/**The chart title (optional). */
private var title: TextTitle = null

/**
 * Sets the value in the rendering hints table for
 * { @link RenderingHints # KEY_TEXT_ANTIALIASING } to either
 * { @link RenderingHints # VALUE_TEXT_ANTIALIAS_ON } or
 * { @link RenderingHints # VALUE_TEXT_ANTIALIAS_OFF }, then sends a
 * { @link ChartChangeEvent } to all registered listeners.
 *
 * @param flag the new value of the flag.
 *
 * @since 1.0.5
 *
 * @see # getTextAntiAlias ( )
 * @see # setTextAntiAlias ( Object )
 */
def setTextAntiAlias (flag: Boolean): Unit = {
if (flag) {
setTextAntiAlias (RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
}
else {
setTextAntiAlias (RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
}
}

/**
 * A flag that can be used to enable/disable notification of chart change
 * events.
 */
private var notify: Boolean = false

/**
 * Rendering hints that will be used for chart drawing.  This should never
 * be <code>null</code>.
 */
@transient
private var renderingHints: RenderingHints = null

/**
 * Returns a flag that controls whether or not a border is drawn around the
 * outside of the chart.
 *
 * @return A boolean.
 *
 * @see # setBorderVisible ( boolean )
 */
def isBorderVisible: Boolean = {
return this.borderVisible
}

/**
 * Deregisters an object for notification of changes to the chart.
 *
 * @param listener the listener (<code>null</code> not permitted)
 *
 * @see # addChangeListener ( ChartChangeListener )
 */
def removeChangeListener (listener: ChartChangeListener): Unit = {
if (listener == null) {
throw new IllegalArgumentException ("Null 'listener' argument.")
}
this.changeListeners.remove (classOf[ChartChangeListener], listener)
}

/**
 * Sets the value in the rendering hints table for
 * { @link RenderingHints # KEY_TEXT_ANTIALIASING } and sends a
 * { @link ChartChangeEvent } to all registered listeners.
 *
 * @param val the new value (<code>null</code> permitted).
 *
 * @since 1.0.5
 *
 * @see # getTextAntiAlias ( )
 * @see # setTextAntiAlias ( boolean )
 */
def setTextAntiAlias (val: AnyRef): Unit = {
this.renderingHints.put (RenderingHints.KEY_TEXT_ANTIALIASING, `val`)
notifyListeners (new ChartChangeEvent (this) )
}

/**
 * Sets the padding between the chart border and the chart drawing area,
 * and sends a  { @link ChartChangeEvent } to all registered listeners.
 *
 * @param padding the padding (<code>null</code> not permitted).
 *
 * @see # getPadding ( )
 */
def setPadding (padding: RectangleInsets): Unit = {
if (padding == null) {
throw new IllegalArgumentException ("Null 'padding' argument.")
}
this.padding = padding
notifyListeners (new ChartChangeEvent (this) )
}

/**
 * Returns the padding between the chart border and the chart drawing area.
 *
 * @return The padding (never <code>null</code>).
 *
 * @see # setPadding ( RectangleInsets )
 */
def getPadding: RectangleInsets = {
return this.padding
}

/**
 * Handles a 'click' on the chart.  SFreeChart is not a UI component, so
 * some other object (for example,  { @link ChartPanel } ) needs to capture
 * the click event and pass it onto the SFreeChart object.
 * If you are not using SFreeChart in a client application, then this
 * method is not required.
 *
 * @param x x-coordinate of the click (in Java2D space).
 * @param y y-coordinate of the click (in Java2D space).
 * @param info contains chart dimension and entity information
 *              (<code>null</code> not permitted).
 */
def handleClick (x: Int, y: Int, info: ChartRenderingInfo): Unit = {
this.plot.handleClick (x, y, info.getPlotInfo)
}

/**
 * Sets a flag that controls whether or not a border is drawn around the
 * outside of the chart.
 *
 * @param visible the flag.
 *
 * @see # isBorderVisible ( )
 */
def setBorderVisible (visible: Boolean): Unit = {
this.borderVisible = visible
fireChartChanged
}

/**
 * Returns the background image for the chart, or <code>null</code> if
 * there is no image.
 *
 * @return The image (possibly <code>null</code>).
 *
 * @see # setBackgroundImage ( Image )
 */
def getBackgroundImage: Image = {
return this.backgroundImage
}

/**
 * Deregisters an object for notification of changes to the chart.
 *
 * @param listener the object being deregistered.
 *
 * @see # addProgressListener ( ChartProgressListener )
 */
def removeProgressListener (listener: ChartProgressListener): Unit = {
this.progressListeners.remove (classOf[ChartProgressListener], listener)
}

/**
 * Clones the object, and takes care of listeners.
 * Note: caller shall register its own listeners on cloned graph.
 *
 * @return A clone.
 *
 * @throws CloneNotSupportedException if the chart is not cloneable.
 */
override def clone: AnyRef = {
var chart: SFreeChart = super.clone.asInstanceOf[SFreeChart]
chart.renderingHints = this.renderingHints.clone.asInstanceOf[RenderingHints]
if (this.title != null) {
chart.title = this.title.clone.asInstanceOf[TextTitle]
chart.title.addChangeListener (chart)
}
chart.subtitles = new ArrayList[_]

{
var i: Int = 0
while (i < getSubtitleCount) {
{
var subtitle: Title = getSubtitle (i).clone.asInstanceOf[Title]
chart.subtitles.add (subtitle)
subtitle.addChangeListener (chart)
}
( {
i += 1;
i
})
}
}
if (this.plot != null) {
chart.plot = this.plot.clone.asInstanceOf[Plot]
chart.plot.addChangeListener (chart)
}
chart.progressListeners = new EventListenerList
chart.changeListeners = new EventListenerList
return chart
}

/**
 * Returns the background image alignment. Alignment constants are defined
 * in the <code>org.jfree.ui.Align</code> class in the JCommon class
 * library.
 *
 * @return The alignment.
 *
 * @see # setBackgroundImageAlignment ( int )
 */
def getBackgroundImageAlignment: Int = {
return this.backgroundImageAlignment
}

/**
 * Sets the alpha-transparency for the chart's background image.
 * Registered listeners are notified that the chart has been changed.
 *
 * @param alpha the alpha value.
 *
 * @see # getBackgroundImageAlpha ( )
 */
def setBackgroundImageAlpha (alpha: Float): Unit = {
if (this.backgroundImageAlpha != alpha) {
this.backgroundImageAlpha = alpha
fireChartChanged
}
}

/**
 * Returns the plot cast as a  { @link CategoryPlot }.
 * <p>
 * NOTE: if the plot is not an instance of  { @link CategoryPlot }, then a
 * <code>ClassCastException</code> is thrown.
 *
 * @return The plot.
 *
 * @see # getPlot ( )
 */
def getCategoryPlot: CategoryPlot = {
return this.plot.asInstanceOf[CategoryPlot]
}

/**
 * Creates a new chart with the given title and plot.  A default font
 * ( { @link # DEFAULT_TITLE_FONT } ) is used for the title, and the chart will
 * have a legend added automatically.
 * <br><br>
 * Note that the  { @link ChartFactory } class contains a range
 * of static methods that will return ready-made charts, and often this
 * is a more convenient way to create charts than using this constructor.
 *
 * @param title the chart title (<code>null</code> permitted).
 * @param plot the plot (<code>null</code> not permitted).
 */
def this (title: String, plot: Plot) {
this ()
`this` (title, SFreeChart.DEFAULT_TITLE_FONT, plot, true)

/**
 * Sets the background image for the chart and sends a
 * { @link ChartChangeEvent } to all registered listeners.
 *
 * @param image the image (<code>null</code> permitted).
 *
 * @see # getBackgroundImage ( )
 */
def setBackgroundImage (image: Image): Unit = {
if (this.backgroundImage != null) {
if (! this.backgroundImage.equals (image) ) {
this.backgroundImage = image
fireChartChanged
}
}
else {
if (image != null) {
this.backgroundImage = image
fireChartChanged
}
}
}

/**
 * Sets the rendering hints for the chart.  These will be added (using the
 * Graphics2D.addRenderingHints() method) near the start of the
 * SFreeChart.draw() method.
 *
 * @param renderingHints the rendering hints (<code>null</code> not
 *                        permitted).
 *
 * @see # getRenderingHints ( )
 */
def setRenderingHints (renderingHints: RenderingHints): Unit = {
if (renderingHints == null) {
throw new NullPointerException ("RenderingHints given are null")
}
this.renderingHints = renderingHints
fireChartChanged
}

/**
 * Creates and returns a buffered image into which the chart has been drawn.
 *
 * @param width the width.
 * @param height the height.
 * @param info carries back chart state information (<code>null</code>
 *              permitted).
 *
 * @return A buffered image.
 */
def createBufferedImage (width: Int, height: Int, info: ChartRenderingInfo): BufferedImage = {
return createBufferedImage (width, height, BufferedImage.TYPE_INT_ARGB, info)
}

/**
 * Sends a  { @link ChartProgressEvent } to all registered listeners.
 *
 * @param event information about the event that triggered the
 *               notification.
 */
protected def notifyListeners (event: ChartProgressEvent): Unit = {
var listeners: Array[AnyRef] = this.progressListeners.getListenerList

{
var i: Int = listeners.length - 2
while (i >= 0) {
{
if (listeners (i) == classOf[ChartProgressListener] ) {
(listeners (i + 1).asInstanceOf[ChartProgressListener] ).chartProgress (event)
}
}
i -= 2
}
}
}

/**
 * Returns the stroke used to draw the chart border (if visible).
 *
 * @return The border stroke.
 *
 * @see # setBorderStroke ( Stroke )
 */
def getBorderStroke: Stroke = {
return this.borderStroke
}

/**
 * Sets a flag that indicates whether or not anti-aliasing is used when the
 * chart is drawn.
 * <P>
 * Anti-aliasing usually improves the appearance of charts, but is slower.
 *
 * @param flag the new value of the flag.
 *
 * @see # getAntiAlias ( )
 */
def setAntiAlias (flag: Boolean): Unit = {
var val: AnyRef = this.renderingHints.get (RenderingHints.KEY_ANTIALIASING)
if (`val` == null) {
`val` = RenderingHints.VALUE_ANTIALIAS_DEFAULT
}
if (! flag && RenderingHints.VALUE_ANTIALIAS_OFF.equals (`val`) || flag && RenderingHints.VALUE_ANTIALIAS_ON.equals (`val`) ) {
return
}
if (flag) {
this.renderingHints.put (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
}
else {
this.renderingHints.put (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
}
fireChartChanged
}

/**
 * Sets the chart title and sends a  { @link ChartChangeEvent } to all
 * registered listeners.  This is a convenience method that ends up calling
 * the  { @link # setTitle ( TextTitle ) } method.  If there is an existing title,
 * its text is updated, otherwise a new title using the default font is
 * added to the chart.  If <code>text</code> is <code>null</code> the chart
 * title is set to <code>null</code>.
 *
 * @param text the title text (<code>null</code> permitted).
 *
 * @see # getTitle ( )
 */
def setTitle (text: String): Unit = {
if (text != null) {
if (this.title == null) {
setTitle (new TextTitle (text, SFreeChart.DEFAULT_TITLE_FONT) )
}
else {
this.title.setText (text)
}
}
else {
setTitle (null.asInstanceOf[TextTitle] )
}
}

/**
 * Returns a chart subtitle.
 *
 * @param index the index of the chart subtitle (zero based).
 *
 * @return A chart subtitle.
 *
 * @see # addSubtitle ( Title )
 */
def getSubtitle (index: Int): Title = {
if ((index < 0) || (index >= getSubtitleCount) ) {
throw new IllegalArgumentException ("Index out of range.")
}
return this.subtitles.get (index).asInstanceOf[Title]
}

/**
 * Removes the first legend in the chart and sends a
 * { @link ChartChangeEvent } to all registered listeners.
 *
 * @see # getLegend ( )
 */
def removeLegend: Unit = {
removeSubtitle (getLegend)
}

/**
 * Returns the collection of rendering hints for the chart.
 *
 * @return The rendering hints for the chart (never <code>null</code>).
 *
 * @see # setRenderingHints ( RenderingHints )
 */
def getRenderingHints: RenderingHints = {
return this.renderingHints
}

/**
 * Creates and returns a buffered image into which the chart has been drawn.
 *
 * @param width the width.
 * @param height the height.
 * @param imageType the image type.
 * @param info carries back chart state information (<code>null</code>
 *              permitted).
 *
 * @return A buffered image.
 */
def createBufferedImage (width: Int, height: Int, imageType: Int, info: ChartRenderingInfo): BufferedImage = {
var image: BufferedImage = new BufferedImage (width, height, imageType)
var g2: Graphics2D = image.createGraphics
draw (g2, new Double (0, 0, width, height), null, info)
g2.dispose
return image
}

/**
 * Returns the nth legend for a chart, or <code>null</code>.
 *
 * @param index the legend index (zero-based).
 *
 * @return The legend (possibly <code>null</code>).
 *
 * @see # addLegend ( LegendTitle )
 */
def getLegend (index: Int): LegendTitle = {
var seen: Int = 0
var iterator: Iterator[_] = this.subtitles.iterator
while (iterator.hasNext) {
var subtitle: Title = iterator.next.asInstanceOf[Title]
if (subtitle.isInstanceOf[LegendTitle] ) {
if (seen == index) {
return subtitle.asInstanceOf[LegendTitle]
}
else {
( {
seen += 1;
seen
})
}
}
}
return null
}

/**
 * Tests this chart for equality with another object.
 *
 * @param obj the object (<code>null</code> permitted).
 *
 * @return A boolean.
 */
override def equals (obj: AnyRef): Boolean = {
if (obj == this) {
return true
}
if (! (obj.isInstanceOf[SFreeChart] ) ) {
return false
}
var that: SFreeChart = obj.asInstanceOf[SFreeChart]
if (! this.renderingHints.equals (that.renderingHints) ) {
return false
}
if (this.borderVisible != that.borderVisible) {
return false
}
if (! ObjectUtilities.equal (this.borderStroke, that.borderStroke) ) {
return false
}
if (! PaintUtilities.equal (this.borderPaint, that.borderPaint) ) {
return false
}
if (! this.padding.equals (that.padding) ) {
return false
}
if (! ObjectUtilities.equal (this.title, that.title) ) {
return false
}
if (! ObjectUtilities.equal (this.subtitles, that.subtitles) ) {
return false
}
if (! ObjectUtilities.equal (this.plot, that.plot) ) {
return false
}
if (! PaintUtilities.equal (this.backgroundPaint, that.backgroundPaint) ) {
return false
}
if (! ObjectUtilities.equal (this.backgroundImage, that.backgroundImage) ) {
return false
}
if (this.backgroundImageAlignment != that.backgroundImageAlignment) {
return false
}
if (this.backgroundImageAlpha != that.backgroundImageAlpha) {
return false
}
if (this.notify != that.notify) {
return false
}
return true
}

/**
 * Draws the chart on a Java 2D graphics device (such as the screen or a
 * printer).  This method is the focus of the entire SFreeChart library.
 *
 * @param g2 the graphics device.
 * @param area the area within which the chart should be drawn.
 * @param info records info about the drawing (null means collect no info).
 */
def draw (g2: Graphics2D, area: Rectangle2D, info: ChartRenderingInfo): Unit = {
draw (g2, area, null, info)
}

/**A flag that controls whether or not the chart border is drawn. */
private var borderVisible: Boolean = false

/**
 * Sets the background alignment.  Alignment options are defined by the
 * { @link org.jfree.ui.Align } class.
 *
 * @param alignment the alignment.
 *
 * @see # getBackgroundImageAlignment ( )
 */
def setBackgroundImageAlignment (alignment: Int): Unit = {
if (this.backgroundImageAlignment != alignment) {
this.backgroundImageAlignment = alignment
fireChartChanged
}
}

/**
 * Draws a title.  The title should be drawn at the top, bottom, left or
 * right of the specified area, and the area should be updated to reflect
 * the amount of space used by the title.
 *
 * @param t the title (<code>null</code> not permitted).
 * @param g2 the graphics device (<code>null</code> not permitted).
 * @param area the chart area, excluding any existing titles
 *              (<code>null</code> not permitted).
 * @param entities a flag that controls whether or not an entity
 *                  collection is returned for the title.
 *
 * @return An entity collection for the title (possibly <code>null</code>).
 */
protected def drawTitle (t: Title, g2: Graphics2D, area: Rectangle2D, entities: Boolean): EntityCollection = {
if (t == null) {
throw new IllegalArgumentException ("Null 't' argument.")
}
if (area == null) {
throw new IllegalArgumentException ("Null 'area' argument.")
}
var titleArea: Rectangle2D = new Double
var position: RectangleEdge = t.getPosition
var ww: Double = area.getWidth
if (ww <= 0.0) {
return null
}
var hh: Double = area.getHeight
if (hh <= 0.0) {
return null
}
var constraint: RectangleConstraint = new RectangleConstraint (ww, new Range (0.0, ww), LengthConstraintType.RANGE, hh, new Range (0.0, hh), LengthConstraintType.RANGE)
var retValue: AnyRef = null
var p: BlockParams = new BlockParams
p.setGenerateEntities (entities)
if (position == RectangleEdge.TOP) {
var size: Size2D = t.arrange (g2, constraint)
titleArea = createAlignedRectangle2D (size, area, t.getHorizontalAlignment, VerticalAlignment.TOP)
retValue = t.draw (g2, titleArea, p)
area.setRect (area.getX, Math.min (area.getY + size.height, area.getMaxY), area.getWidth, Math.max (area.getHeight - size.height, 0) )
}
else if (position == RectangleEdge.BOTTOM) {
var size: Size2D = t.arrange (g2, constraint)
titleArea = createAlignedRectangle2D (size, area, t.getHorizontalAlignment, VerticalAlignment.BOTTOM)
retValue = t.draw (g2, titleArea, p)
area.setRect (area.getX, area.getY, area.getWidth, area.getHeight - size.height)
}
else if (position == RectangleEdge.RIGHT) {
var size: Size2D = t.arrange (g2, constraint)
titleArea = createAlignedRectangle2D (size, area, HorizontalAlignment.RIGHT, t.getVerticalAlignment)
retValue = t.draw (g2, titleArea, p)
area.setRect (area.getX, area.getY, area.getWidth - size.width, area.getHeight)
}
else if (position == RectangleEdge.LEFT) {
var size: Size2D = t.arrange (g2, constraint)
titleArea = createAlignedRectangle2D (size, area, HorizontalAlignment.LEFT, t.getVerticalAlignment)
retValue = t.draw (g2, titleArea, p)
area.setRect (area.getX + size.width, area.getY, area.getWidth - size.width, area.getHeight)
}
else {
throw new RuntimeException ("Unrecognised title position.")
}
var result: EntityCollection = null
if (retValue.isInstanceOf[EntityBlockResult] ) {
var ebr: EntityBlockResult = retValue.asInstanceOf[EntityBlockResult]
result = ebr.getEntityCollection
}
return result
}

/**
 * Returns the plot cast as an  { @link XYPlot }.
 * <p>
 * NOTE: if the plot is not an instance of  { @link XYPlot }, then a
 * <code>ClassCastException</code> is thrown.
 *
 * @return The plot.
 *
 * @see # getPlot ( )
 */
def getXYPlot: XYPlot = {
return this.plot.asInstanceOf[XYPlot]
}

/**
 * Creates and returns a buffered image into which the chart has been drawn.
 *
 * @param imageWidth the image width.
 * @param imageHeight the image height.
 * @param drawWidth the width for drawing the chart (will be scaled to
 *                   fit image).
 * @param drawHeight the height for drawing the chart (will be scaled to
 *                    fit image).
 * @param info optional object for collection chart dimension and entity
 *              information.
 *
 * @return A buffered image.
 */
def createBufferedImage (imageWidth: Int, imageHeight: Int, drawWidth: Double, drawHeight: Double, info: ChartRenderingInfo): BufferedImage = {
var image: BufferedImage = new BufferedImage (imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
var g2: Graphics2D = image.createGraphics
var scaleX: Double = imageWidth / drawWidth
var scaleY: Double = imageHeight / drawHeight
var st: AffineTransform = AffineTransform.getScaleInstance (scaleX, scaleY)
g2.transform (st)
draw (g2, new Double (0, 0, drawWidth, drawHeight), null, info)
g2.dispose
return image
}

/**
 * Draws the chart on a Java 2D graphics device (such as the screen or a
 * printer).
 * <P>
 * This method is the focus of the entire SFreeChart library.
 *
 * @param g2 the graphics device.
 * @param chartArea the area within which the chart should be drawn.
 * @param anchor the anchor point (in Java2D space) for the chart
 *                (<code>null</code> permitted).
 * @param info records info about the drawing (null means collect no info).
 */
def draw (g2: Graphics2D, chartArea: Rectangle2D, anchor: Point2D, info: ChartRenderingInfo): Unit = {
notifyListeners (new ChartProgressEvent (this, this, ChartProgressEvent.DRAWING_STARTED, 0) )
var entities: EntityCollection = null
if (info != null) {
info.clear
info.setChartArea (chartArea)
entities = info.getEntityCollection
}
if (entities != null) {
entities.add (new SFreeChartEntity (chartArea.clone.asInstanceOf[Rectangle2D], this) )
}
var savedClip: Shape = g2.getClip
g2.clip (chartArea)
g2.addRenderingHints (this.renderingHints)
if (this.backgroundPaint != null) {
g2.setPaint (this.backgroundPaint)
g2.fill (chartArea)
}
if (this.backgroundImage != null) {
var originalComposite: Composite = g2.getComposite
g2.setComposite (AlphaComposite.getInstance (AlphaComposite.SRC_OVER, this.backgroundImageAlpha) )
var dest: Rectangle2D = new Double (0.0, 0.0, this.backgroundImage.getWidth (null), this.backgroundImage.getHeight (null) )
Align.align (dest, chartArea, this.backgroundImageAlignment)
g2.drawImage (this.backgroundImage, dest.getX.asInstanceOf[Int], dest.getY.asInstanceOf[Int], dest.getWidth.asInstanceOf[Int], dest.getHeight.asInstanceOf[Int], null)
g2.setComposite (originalComposite)
}
if (isBorderVisible) {
var paint: Paint = getBorderPaint
var stroke: Stroke = getBorderStroke
if (paint != null && stroke != null) {
var borderArea: Rectangle2D = new Double (chartArea.getX, chartArea.getY, chartArea.getWidth - 1.0, chartArea.getHeight - 1.0)
g2.setPaint (paint)
g2.setStroke (stroke)
g2.draw (borderArea)
}
}
var nonTitleArea: Rectangle2D = new Double
nonTitleArea.setRect (chartArea)
this.padding.trim (nonTitleArea)
if (this.title != null) {
var e: EntityCollection = drawTitle (this.title, g2, nonTitleArea, (entities != null) )
if (e != null) {
entities.addAll (e)
}
}
var iterator: Iterator[_] = this.subtitles.iterator
while (iterator.hasNext) {
var currentTitle: Title = iterator.next.asInstanceOf[Title]
if (currentTitle.isVisible) {
var e: EntityCollection = drawTitle (currentTitle, g2, nonTitleArea, (entities != null) )
if (e != null) {
entities.addAll (e)
}
}
}
var plotArea: Rectangle2D = nonTitleArea
var plotInfo: PlotRenderingInfo = null
if (info != null) {
plotInfo = info.getPlotInfo
}
this.plot.draw (g2, plotArea, anchor, null, plotInfo)
g2.setClip (savedClip)
notifyListeners (new ChartProgressEvent (this, this, ChartProgressEvent.DRAWING_FINISHED, 100) )
}

/**
 * Adds a legend to the plot and sends a  { @link ChartChangeEvent } to all
 * registered listeners.
 *
 * @param legend the legend (<code>null</code> not permitted).
 *
 * @see # removeLegend ( )
 */
def addLegend (legend: LegendTitle): Unit = {
addSubtitle (legend)
}

/**The padding between the chart border and the chart drawing area. */
private var padding: RectangleInsets = null
}


/**
 * Information about the SFreeChart project.  One instance of this class is
 * assigned to <code>SFreeChart.INFO<code>.
 */

class SFreeChartInfo extends ProjectInfo {
  /**
   * Default constructor.
   */
  def this() {
    this ()
    var baseResourceClass: String = "org.jfree.chart.resources.SFreeChartResources"
    var resources: ResourceBundle = ResourceBundleWrapper.getBundle(baseResourceClass)
    setName(resources.getString("project.name"))
    setVersion(resources.getString("project.version"))
    setInfo(resources.getString("project.info"))
    setCopyright(resources.getString("project.copyright"))
    setLogo(null)
    setLicenceName("LGPL")
    setLicenceText(Licences.getInstance.getLGPL)
    setContributors(Arrays.asList(Array[Contributor](new Contributor("Eric Alexander", "-"), new Contributor("Richard Atkinson", "richard_c_atkinson@ntlworld.com"), new Contributor("David Basten", "-"), new Contributor("David Berry", "-"), new Contributor("Chris Boek", "-"), new Contributor("Zoheb Borbora", "-"), new Contributor("Anthony Boulestreau", "-"), new Contributor("Jeremy Bowman", "-"), new Contributor("Nicolas Brodu", "-"), new Contributor("Jody Brownell", "-"), new Contributor("David Browning", "-"), new Contributor("Soren Caspersen", "-"), new Contributor("Chuanhao Chiu", "-"), new Contributor("Brian Cole", "-"), new Contributor("Pascal Collet", "-"), new Contributor("Martin Cordova", "-"), new Contributor("Paolo Cova", "-"), new Contributor("Greg Darke", "-"), new Contributor("Mike Duffy", "-"), new Contributor("Don Elliott", "-"), new Contributor("David Forslund", "-"), new Contributor("Jonathan Gabbai", "-"), new Contributor("David Gilbert", "david.gilbert@object-refinery.com"), new Contributor("Serge V. Grachov", "-"), new Contributor("Daniel Gredler", "-"), new Contributor("Hans-Jurgen Greiner", "-"), new Contributor("Joao Guilherme Del Valle", "-"), new Contributor("Aiman Han", "-"), new Contributor("Cameron Hayne", "-"), new Contributor("Martin Hoeller", "-"), new Contributor("Jon Iles", "-"), new Contributor("Wolfgang Irler", "-"), new Contributor("Sergei Ivanov", "-"), new Contributor("Adriaan Joubert", "-"), new Contributor("Darren Jung", "-"), new Contributor("Xun Kang", "-"), new Contributor("Bill Kelemen", "-"), new Contributor("Norbert Kiesel", "-"), new Contributor("Peter Kolb", "-"), new Contributor("Gideon Krause", "-"), new Contributor("Pierre-Marie Le Biot", "-"), new Contributor("Arnaud Lelievre", "-"), new Contributor("Wolfgang Lenhard", "-"), new Contributor("David Li", "-"), new Contributor("Yan Liu", "-"), new Contributor("Tin Luu", "-"), new Contributor("Craig MacFarlane", "-"), new Contributor("Achilleus Mantzios", "-"), new Contributor("Thomas Meier", "-"), new Contributor("Jim Moore", "-"), new Contributor("Jonathan Nash", "-"), new Contributor("Barak Naveh", "-"), new Contributor("David M. O'Donnell", "-"), new Contributor("Krzysztof Paz", "-"), new Contributor("Eric Penfold", "-"), new Contributor("Tomer Peretz", "-"), new Contributor("Diego Pierangeli", "-"), new Contributor("Xavier Poinsard", "-"), new Contributor("Andrzej Porebski", "-"), new Contributor("Viktor Rajewski", "-"), new Contributor("Eduardo Ramalho", "-"), new Contributor("Michael Rauch", "-"), new Contributor("Cameron Riley", "-"), new Contributor("Klaus Rheinwald", "-"), new Contributor("Dan Rivett", "d.rivett@ukonline.co.uk"), new Contributor("Scott Sams", "-"), new Contributor("Michel Santos", "-"), new Contributor("Thierry Saura", "-"), new Contributor("Andreas Schneider", "-"), new Contributor("Jean-Luc SCHWAB", "-"), new Contributor("Bryan Scott", "-"), new Contributor("Tobias Selb", "-"), new Contributor("Darshan Shah", "-"), new Contributor("Mofeed Shahin", "-"), new Contributor("Michael Siemer", "-"), new Contributor("Pady Srinivasan", "-"), new Contributor("Greg Steckman", "-"), new Contributor("Gerald Struck", "-"), new Contributor("Roger Studner", "-"), new Contributor("Irv Thomae", "-"), new Contributor("Eric Thomas", "-"), new Contributor("Rich Unger", "-"), new Contributor("Daniel van Enckevort", "-"), new Contributor("Laurence Vanhelsuwe", "-"), new Contributor("Sylvain Vieujot", "-"), new Contributor("Ulrich Voigt", "-"), new Contributor("Jelai Wang", "-"), new Contributor("Mark Watson", "www.markwatson.com"), new Contributor("Alex Weber", "-"), new Contributor("Matthew Wright", "-"), new Contributor("Benoit Xhenseval", "-"), new Contributor("Christian W. Zuckschwerdt", "Christian.Zuckschwerdt@Informatik.Uni-Oldenburg.de"), new Contributor("Hari", "-"), new Contributor("Sam (oldman)", "-"))))
    addLibrary(JCommon.INFO)

    /**
     * Returns the SFreeChart logo (a picture of a gorilla).
     *
     * @return The SFreeChart logo.
     */
    override def getLogo: Image = {
  var logo: Image = super.getLogo
  if (logo == null) {
    var imageURL: URL = this.getClass.getClassLoader.getResource("org/jfree/chart/gorilla.jpg")
    if (imageURL != null) {
      var temp: ImageIcon = new ImageIcon(imageURL)
      logo = temp.getImage
      setLogo(logo)
    }
  }
  return logo
}
}

