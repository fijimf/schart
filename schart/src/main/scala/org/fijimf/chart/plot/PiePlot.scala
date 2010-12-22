package org.jfree.chart.plot


import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Composite
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.Shape
import java.awt.Stroke
import java.awt.geom.Arc2D
import java.awt.geom.CubicCurve2D
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.QuadCurve2D
import java.awt.geom.Rectangle2D
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.ResourceBundle
import java.util.TreeMap
import org.jfree.chart.LegendItem
import org.jfree.chart.LegendItemCollection
import org.jfree.chart.PaintMap
import org.jfree.chart.StrokeMap
import org.jfree.chart.entity.EntityCollection
import org.jfree.chart.entity.PieSectionEntity
import org.jfree.chart.event.PlotChangeEvent
import org.jfree.chart.labels.PieSectionLabelGenerator
import org.jfree.chart.labels.PieToolTipGenerator
import org.jfree.chart.labels.StandardPieSectionLabelGenerator
import org.jfree.chart.urls.PieURLGenerator
import org.jfree.chart.util.ResourceBundleWrapper
import org.jfree.data.DefaultKeyedValues
import org.jfree.data.KeyedValues
import org.jfree.data.general.DatasetChangeEvent
import org.jfree.data.general.DatasetUtilities
import org.jfree.data.general.PieDataset
import org.jfree.io.SerialUtilities
import org.jfree.text.G2TextMeasurer
import org.jfree.text.TextBlock
import org.jfree.text.TextBox
import org.jfree.text.TextUtilities
import org.jfree.ui.RectangleAnchor
import org.jfree.ui.RectangleInsets
import org.jfree.ui.TextAnchor
import org.jfree.util._

object PiePlot {
  val DEFAULT_LABEL_BACKGROUND_PAINT: Paint = new Color(255, 255, 192)
  val DEBUG_DRAW_INTERIOR: Boolean = false
  val DEBUG_DRAW_LINK_AREA: Boolean = false
  val DEFAULT_LABEL_PAINT: Paint = Color.black

  val DEFAULT_LABEL_OUTLINE_PAINT: Paint = Color.black

  val DEFAULT_START_ANGLE: Double = 90.0

  val MAX_INTERIOR_GAP: Double = 0.40

  var localizationResources: ResourceBundle = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle")

  val DEFAULT_LABEL_FONT: Font = new Font("SansSerif", Font.PLAIN, 10)

   val DEFAULT_INTERIOR_GAP: Double = 0.08

   val DEFAULT_LABEL_OUTLINE_STROKE: Stroke = new BasicStroke(0.5f)

   val DEFAULT_LABEL_SHADOW_PAINT: Paint = new Color(151, 151, 151, 128)

   val DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW: Double = 0.00001

  val DEBUG_DRAW_PIE_AREA: Boolean = false
}

class PiePlot extends Plot with Cloneable with Serializable {
  private var sectionPaint: Paint = null
  private var ignoreNullValues: Boolean = false
  private var baseSectionOutlineStroke: Stroke = null
  private var baseSectionPaint: Paint = null
  private var labelPaint: Paint = null
  private var labelPadding: RectangleInsets = null
  /**The tooltip generator. */
  private var toolTipGenerator: PieToolTipGenerator = null

  /**The dataset for the pie chart. */
  private var dataset: PieDataset = null

  private var minimumArcAngleToDraw: Double = .0

  def setLabelLinkMargin(margin: Double): Unit = {
    this.labelLinkMargin = margin
    fireChangeEvent
  }

  def setLegendLabelGenerator(generator: PieSectionLabelGenerator): Unit = {
    if (generator == null) {
      throw new IllegalArgumentException("Null 'generator' argument.")
    }
    this.legendLabelGenerator = generator
    fireChangeEvent
  }


  def getDirection: Rotation = {
    return this.direction
  }

def getLegendLabelToolTipGenerator: PieSectionLabelGenerator = {
return this.legendLabelToolTipGenerator
}

def getLabelLinkMargin: Double = {
return this.labelLinkMargin
}

def setLegendItemShape (shape: Shape): Unit = {
if (shape == null) {
throw new IllegalArgumentException ("Null 'shape' argument.")
}
this.legendItemShape = shape
fireChangeEvent
}

def getLegendLabelURLGenerator: PieURLGenerator = {
return this.legendLabelURLGenerator
}

/**The color used to draw the section labels. */
@transient

/**
 * Returns the flag that controls whether or not the section paint is
 * auto-populated by the  { @link # lookupSectionPaint ( Comparable ) } method.
 *
 * @return A boolean.
 *
 * @since 1.0.11
 */
def getAutoPopulateSectionPaint: Boolean = {
return this.autoPopulateSectionPaint
}

protected def lookupSectionPaint (key: Comparable[_], autoPopulate: Boolean): Paint = {
var result: Paint = getSectionPaint
if (result != null) {
return result
}
result = this.sectionPaintMap.getPaint (key)
if (result != null) {
return result
}
if (autoPopulate) {
var ds: DrawingSupplier = getDrawingSupplier
if (ds != null) {
result = ds.getNextPaint
this.sectionPaintMap.put (key, result)
}
else {
result = this.baseSectionPaint
}
}
else {
result = this.baseSectionPaint
}
return result
}

def getLabelLinkStyle: PieLabelLinkStyle = {
return this.labelLinkStyle
}

private var simpleLabelOffset: RectangleInsets = null

def setShadowXOffset (offset: Double): Unit = {
this.shadowXOffset = offset
fireChangeEvent
}

def setBaseSectionOutlineStroke (stroke: Stroke): Unit = {
if (stroke == null) {
throw new IllegalArgumentException ("Null 'stroke' argument.")
}
this.baseSectionOutlineStroke = stroke
fireChangeEvent
}

private var legendLabelURLGenerator: PieURLGenerator = null

def getLabelOutlineStroke: Stroke = {
return this.labelOutlineStroke
}

def setLabelLinkStroke (stroke: Stroke): Unit = {
if (stroke == null) {
throw new IllegalArgumentException ("Null 'stroke' argument.")
}
this.labelLinkStroke = stroke
fireChangeEvent
}

/**
 * Sets the padding between each label and its outline and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param padding the padding (<code>null</code> not permitted).
 *
 * @since 1.0.7
 *
 * @see # getLabelPadding ( )
 */
def setLabelPadding (padding: RectangleInsets): Unit = {
if (padding == null) {
throw new IllegalArgumentException ("Null 'padding' argument.")
}
this.labelPadding = padding
fireChangeEvent
}

/**
 * Returns the x-offset for the shadow effect.
 *
 * @return The offset (in Java2D units).
 *
 * @see # setShadowXOffset ( double )
 */
def getShadowXOffset: Double = {
return this.shadowXOffset
}

/**
 * Returns a flag indicating whether the pie chart is circular, or
 * stretched into an elliptical shape.
 *
 * @return A flag indicating whether the pie chart is circular.
 *
 * @see # setCircular ( boolean )
 */
def isCircular: Boolean = {
return this.circular
}

def clearSectionOutlineStrokes (notify: Boolean): Unit = {
this.sectionOutlineStrokeMap.clear
if (notify) {
fireChangeEvent
}
}

  def setShadowPaint (paint: Paint): Unit = {
this.shadowPaint = paint
fireChangeEvent
}

/**
 * Sets the legend label URL generator and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param generator the generator (<code>null</code> permitted).
 *
 * @see # getLegendLabelURLGenerator ( )
 *
 * @since 1.0.4
 */
def setLegendLabelURLGenerator (generator: PieURLGenerator): Unit = {
this.legendLabelURLGenerator = generator
fireChangeEvent
}

/**
 * The gap between the labels and the link corner, as a percentage of the
 * plot width.
 */
private var labelGap: Double = 0.025

protected def lookupSectionOutlinePaint (key: Comparable[_], autoPopulate: Boolean): Paint = {
var result: Paint = getSectionOutlinePaint
if (result != null) {
return result
}
result = this.sectionOutlinePaintMap.getPaint (key)
if (result != null) {
return result
}
if (autoPopulate) {
var ds: DrawingSupplier = getDrawingSupplier
if (ds != null) {
result = ds.getNextOutlinePaint
this.sectionOutlinePaintMap.put (key, result)
}
else {
result = this.baseSectionOutlinePaint
}
}
else {
result = this.baseSectionOutlinePaint
}
return result
}

/**
 * Returns the flag that controls whether or not the section outline stroke
 * is auto-populated by the  { @link # lookupSectionOutlinePaint ( Comparable ) }
 * method.
 *
 * @return A boolean.
 *
 * @since 1.0.11
 */
def getAutoPopulateSectionOutlineStroke: Boolean = {
return this.autoPopulateSectionOutlineStroke
}

/**
 * The padding between the labels and the label outlines.  This is not
 * allowed to be <code>null</code>.
 *
 * @since 1.0.7
 */

/**
 * Sets the section label paint and sends a  { @link PlotChangeEvent } to all
 * registered listeners.
 *
 * @param paint the paint (<code>null</code> not permitted).
 *
 * @see # getLabelPaint ( )
 */
def setLabelPaint (paint: Paint): Unit = {
if (paint == null) {
throw new IllegalArgumentException ("Null 'paint' argument.")
}
this.labelPaint = paint
fireChangeEvent
}

/**
 * Returns the flag that controls whether or not the outline is drawn for
 * each pie section.
 *
 * @return The flag that controls whether or not the outline is drawn for
 *         each pie section.
 *
 * @see # setSectionOutlinesVisible ( boolean )
 */
def getSectionOutlinesVisible: Boolean = {
return this.sectionOutlinesVisible
}

/**The base section outline paint (fallback). */
@transient
private var baseSectionOutlinePaint: Paint = null

/**The shadow paint. */
@transient
private var shadowPaint: Paint = Color.gray

/**
 * Clears the section paint settings for this plot and, if requested, sends
 * a  { @link PlotChangeEvent } to all registered listeners.  Be aware that
 * if the <code>autoPopulateSectionPaint</code> flag is set, the section
 * paints may be repopulated using the same colours as before.
 *
 * @param notify notify listeners?
 *
 * @since 1.0.11
 *
 * @see # autoPopulateSectionPaint
 */
def clearSectionPaints (notify: Boolean): Unit = {
this.sectionPaintMap.clear
if (notify) {
fireChangeEvent
}
}

/**
 * Sets the label link style and sends a  { @link PlotChangeEvent } to all
 * registered listeners.
 *
 * @param style the new style (<code>null</code> not permitted).
 *
 * @see # getLabelLinkStyle ( )
 *
 * @since 1.0.10
 */
def setLabelLinkStyle (style: PieLabelLinkStyle): Unit = {
if (style == null) {
throw new IllegalArgumentException ("Null 'style' argument.")
}
this.labelLinkStyle = style
fireChangeEvent
}

/**
 * Sets the section label generator and sends a  { @link PlotChangeEvent } to
 * all registered listeners.
 *
 * @param generator the generator (<code>null</code> permitted).
 *
 * @see # getLabelGenerator ( )
 */
def setLabelGenerator (generator: PieSectionLabelGenerator): Unit = {
this.labelGenerator = generator
fireChangeEvent
}

/**
 * Returns the distance that the end of the label link is embedded into
 * the plot, expressed as a percentage of the plot's radius.
 * <br><br>
 * This method is overridden in the  { @link RingPlot } class to resolve
 * bug 2121818.
 *
 * @return <code>0.10</code>.
 *
 * @since 1.0.12
 */
protected def getLabelLinkDepth: Double = {
return 0.1
}

/**
 * Sets the interior gap and sends a  { @link PlotChangeEvent } to all
 * registered listeners.  This controls the space between the edges of the
 * pie plot and the plot area itself (the region where the section labels
 * appear).
 *
 * @param percent the gap (as a percentage of the available drawing space).
 *
 * @see # getInteriorGap ( )
 */
def setInteriorGap (percent: Double): Unit = {
if ((percent < 0.0) || (percent > MAX_INTERIOR_GAP) ) {
throw new IllegalArgumentException ("Invalid 'percent' (" + percent + ") argument.")
}
if (this.interiorGap != percent) {
this.interiorGap = percent
fireChangeEvent
}
}

/**
 * Returns the shadow paint.
 *
 * @return The paint (possibly <code>null</code>).
 *
 * @see # setShadowPaint ( Paint )
 */
def getShadowPaint: Paint = {
return this.shadowPaint
}

/**
 * Sets the URL generator and sends a  { @link PlotChangeEvent } to all
 * registered listeners.
 *
 * @param generator the generator (<code>null</code> permitted).
 *
 * @see # getURLGenerator ( )
 */
def setURLGenerator (generator: PieURLGenerator): Unit = {
this.urlGenerator = generator
fireChangeEvent
}

/**
 * Returns the flag that controls whether or not the section outline paint
 * is auto-populated by the  { @link # lookupSectionOutlinePaint ( Comparable ) }
 * method.
 *
 * @return A boolean.
 *
 * @since 1.0.11
 */
def getAutoPopulateSectionOutlinePaint: Boolean = {
return this.autoPopulateSectionOutlinePaint
}

/**
 * Draws the right labels.
 *
 * @param keys the keys.
 * @param g2 the graphics device.
 * @param plotArea the plot area.
 * @param linkArea the link area.
 * @param maxLabelWidth the maximum label width.
 * @param state the state.
 */
protected def drawRightLabels (keys: KeyedValues, g2: Graphics2D, plotArea: Rectangle2D, linkArea: Rectangle2D, maxLabelWidth: Float, state: PiePlotState): Unit = {
this.labelDistributor.clear
var lGap: Double = plotArea.getWidth * this.labelGap
var verticalLinkRadius: Double = state.getLinkArea.getHeight / 2.0

{
var i: Int = 0
while (i < keys.getItemCount) {
{
var label: String = this.labelGenerator.generateSectionLabel (this.dataset, keys.getKey (i) )
if (label != null) {
var block: TextBlock = TextUtilities.createTextBlock (label, this.labelFont, this.labelPaint, maxLabelWidth, new G2TextMeasurer (g2) )
var labelBox: TextBox = new TextBox (block)
labelBox.setBackgroundPaint (this.labelBackgroundPaint)
labelBox.setOutlinePaint (this.labelOutlinePaint)
labelBox.setOutlineStroke (this.labelOutlineStroke)
labelBox.setShadowPaint (this.labelShadowPaint)
labelBox.setInteriorGap (this.labelPadding)
var theta: Double = Math.toRadians (keys.getValue (i).doubleValue)
var baseY: Double = state.getPieCenterY - Math.sin (theta) * verticalLinkRadius
var hh: Double = labelBox.getHeight (g2)
this.labelDistributor.addPieLabelRecord (new PieLabelRecord (keys.getKey (i), theta, baseY, labelBox, hh, lGap / 2.0 + lGap / 2.0 * Math.cos (theta), 1.0 - getLabelLinkDepth + getExplodePercent (keys.getKey (i) ) ) )
}
}
( {
i += 1;
i
})
}
}
var hh: Double = plotArea.getHeight
var gap: Double = hh * getInteriorGap
this.labelDistributor.distributeLabels (plotArea.getMinY + gap, hh - 2 * gap)

{
var i: Int = 0
while (i < this.labelDistributor.getItemCount) {
{
drawRightLabel (g2, state, this.labelDistributor.getPieLabelRecord (i) )
}
( {
i += 1;
i
})
}
}
}

/**The URL generator. */
private var urlGenerator: PieURLGenerator = null

/**
 * A flag that controls whether zero values are ignored.
 */
private var ignoreZeroValues: Boolean = false

/**
 * Sets the amount that a pie section should be exploded and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param key the section key (<code>null</code> not permitted).
 * @param percent the explode percentage (0.30 = 30 percent).
 *
 * @since 1.0.3
 *
 * @see # getExplodePercent ( Comparable )
 */
def setExplodePercent (key: Comparable[_], percent: Double): Unit = {
if (key == null) {
throw new IllegalArgumentException ("Null 'key' argument.")
}
if (this.explodePercentages == null) {
this.explodePercentages = new TreeMap[_, _]
}
this.explodePercentages.put (key, new Double (percent) )
fireChangeEvent
}

/**
 * Returns the pie index (this is used by the  { @link MultiplePiePlot } class
 * to track subplots).
 *
 * @return The pie index.
 *
 * @see # setPieIndex ( int )
 */
def getPieIndex: Int = {
return this.pieIndex
}

/**
 * Sets the section label outline paint and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param paint the paint (<code>null</code> permitted).
 *
 * @see # getLabelOutlinePaint ( )
 */
def setLabelOutlinePaint (paint: Paint): Unit = {
this.labelOutlinePaint = paint
fireChangeEvent
}

/**The paint used for the label linking lines. */
@transient
private var labelLinkPaint: Paint = Color.black

/**
 * Draws the left labels.
 *
 * @param leftKeys a collection of keys and angles (to the middle of the
 *         section, in degrees) for the sections on the left side of the
 *         plot.
 * @param g2 the graphics device.
 * @param plotArea the plot area.
 * @param linkArea the link area.
 * @param maxLabelWidth the maximum label width.
 * @param state the state.
 */
protected def drawLeftLabels (leftKeys: KeyedValues, g2: Graphics2D, plotArea: Rectangle2D, linkArea: Rectangle2D, maxLabelWidth: Float, state: PiePlotState): Unit = {
this.labelDistributor.clear
var lGap: Double = plotArea.getWidth * this.labelGap
var verticalLinkRadius: Double = state.getLinkArea.getHeight / 2.0

{
var i: Int = 0
while (i < leftKeys.getItemCount) {
{
var label: String = this.labelGenerator.generateSectionLabel (this.dataset, leftKeys.getKey (i) )
if (label != null) {
var block: TextBlock = TextUtilities.createTextBlock (label, this.labelFont, this.labelPaint, maxLabelWidth, new G2TextMeasurer (g2) )
var labelBox: TextBox = new TextBox (block)
labelBox.setBackgroundPaint (this.labelBackgroundPaint)
labelBox.setOutlinePaint (this.labelOutlinePaint)
labelBox.setOutlineStroke (this.labelOutlineStroke)
labelBox.setShadowPaint (this.labelShadowPaint)
labelBox.setInteriorGap (this.labelPadding)
var theta: Double = Math.toRadians (leftKeys.getValue (i).doubleValue)
var baseY: Double = state.getPieCenterY - Math.sin (theta) * verticalLinkRadius
var hh: Double = labelBox.getHeight (g2)
this.labelDistributor.addPieLabelRecord (new PieLabelRecord (leftKeys.getKey (i), theta, baseY, labelBox, hh, lGap / 2.0 + lGap / 2.0 * - Math.cos (theta), 1.0 - getLabelLinkDepth + getExplodePercent (leftKeys.getKey (i) ) ) )
}
}
( {
i += 1;
i
})
}
}
var hh: Double = plotArea.getHeight
var gap: Double = hh * getInteriorGap
this.labelDistributor.distributeLabels (plotArea.getMinY + gap, hh - 2 * gap)

{
var i: Int = 0
while (i < this.labelDistributor.getItemCount) {
{
drawLeftLabel (g2, state, this.labelDistributor.getPieLabelRecord (i) )
}
( {
i += 1;
i
})
}
}
}

/**
 * Returns the object responsible for the vertical layout of the pie
 * section labels.
 *
 * @return The label distributor (never <code>null</code>).
 *
 * @since 1.0.6
 */
def getLabelDistributor: AbstractPieLabelDistributor = {
return this.labelDistributor
}

/**
 * Returns a clone of the plot.
 *
 * @return A clone.
 *
 * @throws CloneNotSupportedException if some component of the plot does
 *         not support cloning.
 */
override def clone: AnyRef = {
var clone: PiePlot = super.clone.asInstanceOf[PiePlot]
if (clone.dataset != null) {
clone.dataset.addChangeListener (clone)
}
if (this.urlGenerator.isInstanceOf[PublicCloneable] ) {
clone.urlGenerator = ObjectUtilities.clone (this.urlGenerator).asInstanceOf[PieURLGenerator]
}
clone.legendItemShape = ShapeUtilities.clone (this.legendItemShape)
if (this.legendLabelGenerator != null) {
clone.legendLabelGenerator = ObjectUtilities.clone (this.legendLabelGenerator).asInstanceOf[PieSectionLabelGenerator]
}
if (this.legendLabelToolTipGenerator != null) {
clone.legendLabelToolTipGenerator = ObjectUtilities.clone (this.legendLabelToolTipGenerator).asInstanceOf[PieSectionLabelGenerator]
}
if (this.legendLabelURLGenerator.isInstanceOf[PublicCloneable] ) {
clone.legendLabelURLGenerator = ObjectUtilities.clone (this.legendLabelURLGenerator).asInstanceOf[PieURLGenerator]
}
return clone
}

/**
 * Provides serialization support.
 *
 * @param stream the output stream.
 *
 * @throws IOException if there is an I/O error.
 */
private def writeObject (stream: ObjectOutputStream): Unit = {
stream.defaultWriteObject
SerialUtilities.writePaint (this.sectionPaint, stream)
SerialUtilities.writePaint (this.baseSectionPaint, stream)
SerialUtilities.writePaint (this.sectionOutlinePaint, stream)
SerialUtilities.writePaint (this.baseSectionOutlinePaint, stream)
SerialUtilities.writeStroke (this.sectionOutlineStroke, stream)
SerialUtilities.writeStroke (this.baseSectionOutlineStroke, stream)
SerialUtilities.writePaint (this.shadowPaint, stream)
SerialUtilities.writePaint (this.labelPaint, stream)
SerialUtilities.writePaint (this.labelBackgroundPaint, stream)
SerialUtilities.writePaint (this.labelOutlinePaint, stream)
SerialUtilities.writeStroke (this.labelOutlineStroke, stream)
SerialUtilities.writePaint (this.labelShadowPaint, stream)
SerialUtilities.writePaint (this.labelLinkPaint, stream)
SerialUtilities.writeStroke (this.labelLinkStroke, stream)
SerialUtilities.writeShape (this.legendItemShape, stream)
}

/**
 * Sets the amount that a pie section should be exploded and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param section the section index.
 * @param percent the explode percentage (0.30 = 30 percent).
 *
 * @deprecated Use { @link # setExplodePercent ( Comparable, double ) } instead.
 */
def setExplodePercent (section: Int, percent: Double): Unit = {
var key: Comparable[_] = getSectionKey (section)
setExplodePercent (key, percent)
}

/**
 * Sets the direction in which the pie sections are drawn and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param direction the direction (<code>null</code> not permitted).
 *
 * @see # getDirection ( )
 */
def setDirection (direction: Rotation): Unit = {
if (direction == null) {
throw new IllegalArgumentException ("Null 'direction' argument.")
}
this.direction = direction
fireChangeEvent
}

/**The direction for the pie segments. */
private var direction: Rotation = null

/**
 * The outline paint for ALL sections (overrides list).
 *
 * @deprecated This field is redundant, it is sufficient to use
 *     sectionOutlinePaintMap and baseSectionOutlinePaint.  Deprecated as
 *     of version 1.0.6.
 */
@transient
private var sectionOutlinePaint: Paint = null

/**
 * Sets the stroke used to fill a section of the pie and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param section the section index (zero-based).
 * @param stroke the stroke (<code>null</code> permitted).
 *
 * @deprecated Use { @link # setSectionOutlineStroke ( Comparable, Stroke ) }
 *     instead.
 */
def setSectionOutlineStroke (section: Int, stroke: Stroke): Unit = {
var key: Comparable[_] = getSectionKey (section)
setSectionOutlineStroke (key, stroke)
}

/**The section outline stroke map. */
private var sectionOutlineStrokeMap: StrokeMap = null

/**
 * A flag that controls whether or not the section paint is auto-populated
 * from the drawing supplier.
 *
 * @since 1.0.11
 */
private var autoPopulateSectionPaint: Boolean = false

/**
 * Sets the base section paint.
 *
 * @param paint the paint (<code>null</code> not permitted).
 *
 * @see # getBaseSectionOutlinePaint ( )
 */
def setBaseSectionOutlinePaint (paint: Paint): Unit = {
if (paint == null) {
throw new IllegalArgumentException ("Null 'paint' argument.")
}
this.baseSectionOutlinePaint = paint
fireChangeEvent
}

/**The pie index (used by the  { @link MultiplePiePlot } class). */
private var pieIndex: Int = 0

/**
 * Returns the paint used for the lines that connect pie sections to their
 * corresponding labels.
 *
 * @return The paint (never <code>null</code>).
 *
 * @see # setLabelLinkPaint ( Paint )
 */
def getLabelLinkPaint: Paint = {
return this.labelLinkPaint
}

/**The starting angle. */
private var startAngle: Double = .0

/**
 * Sets the paint used for the lines that connect pie sections to their
 * corresponding labels, and sends a  { @link PlotChangeEvent } to all
 * registered listeners.
 *
 * @param paint the paint (<code>null</code> not permitted).
 *
 * @see # getLabelLinkPaint ( )
 */
def setLabelLinkPaint (paint: Paint): Unit = {
if (paint == null) {
throw new IllegalArgumentException ("Null 'paint' argument.")
}
this.labelLinkPaint = paint
fireChangeEvent
}

/**
 * Sets the section label outline stroke and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param stroke the stroke (<code>null</code> permitted).
 *
 * @see # getLabelOutlineStroke ( )
 */
def setLabelOutlineStroke (stroke: Stroke): Unit = {
this.labelOutlineStroke = stroke
fireChangeEvent
}

/**
 * Returns the flag that controls whether <code>null</code> values in the
 * dataset are ignored.
 *
 * @return A boolean.
 *
 * @see # setIgnoreNullValues ( boolean )
 */
def getIgnoreNullValues: Boolean = {
return this.ignoreNullValues
}

/**
 * Returns the maximum explode percent.
 *
 * @return The percent.
 */
def getMaximumExplodePercent: Double = {
if (this.dataset == null) {
return 0.0
}
var result: Double = 0.0
var iterator: Iterator[_] = this.dataset.getKeys.iterator
while (iterator.hasNext) {
var key: Comparable[_] = iterator.next.asInstanceOf[Comparable[_]]
var explode: Number = this.explodePercentages.get (key).asInstanceOf[Number]
if (explode != null) {
result = Math.max (result, explode.doubleValue)
}
}
return result
}

/**
 * Returns the offset used for the simple labels, if they are displayed.
 *
 * @return The offset (never <code>null</code>).
 *
 * @since 1.0.7
 *
 * @see # setSimpleLabelOffset ( RectangleInsets )
 */
def getSimpleLabelOffset: RectangleInsets = {
return this.simpleLabelOffset
}

/**
 * Sets the starting angle and sends a  { @link PlotChangeEvent } to all
 * registered listeners.  The initial default value is 90 degrees, which
 * corresponds to 12 o'clock.  A value of zero corresponds to 3 o'clock...
 * this is the encoding used by Java's Arc2D class.
 *
 * @param angle the angle (in degrees).
 *
 * @see # getStartAngle ( )
 */
def setStartAngle (angle: Double): Unit = {
this.startAngle = angle
fireChangeEvent
}

/**
 * Sets a flag that controls whether zero values are ignored,
 * and sends a  { @link PlotChangeEvent } to all registered listeners.  This
 * only affects whether or not a label appears for the non-visible
 * pie section.
 *
 * @param flag the flag.
 *
 * @see # getIgnoreZeroValues ( )
 * @see # setIgnoreNullValues ( boolean )
 */
def setIgnoreZeroValues (flag: Boolean): Unit = {
this.ignoreZeroValues = flag
fireChangeEvent
}

/**
 * Returns the paint for ALL sections in the plot.
 *
 * @return The paint (possibly <code>null</code>).
 *
 * @see # setSectionPaint ( Paint )
 *
 * @deprecated Use { @link # getSectionPaint ( Comparable ) } and
 * { @link # getBaseSectionPaint ( ) }.  Deprecated as of version 1.0.6.
 */
def getSectionPaint: Paint = {
return this.sectionPaint
}

/**
 * Sets the flag that controls whether or not label linking lines are
 * visible and sends a  { @link PlotChangeEvent } to all registered listeners.
 * Please take care when hiding the linking lines - depending on the data
 * values, the labels can be displayed some distance away from the
 * corresponding pie section.
 *
 * @param visible the flag.
 *
 * @see # getLabelLinksVisible ( )
 */
def setLabelLinksVisible (visible: Boolean): Unit = {
this.labelLinksVisible = visible
fireChangeEvent
}

/**
 * Returns the outline paint associated with the specified key, or
 * <code>null</code> if there is no paint associated with the key.
 *
 * @param key the key (<code>null</code> not permitted).
 *
 * @return The paint associated with the specified key, or
 *     <code>null</code>.
 *
 * @throws IllegalArgumentException if <code>key</code> is
 *     <code>null</code>.
 *
 * @see # setSectionOutlinePaint ( Comparable, Paint )
 *
 * @since 1.0.3
 */
def getSectionOutlinePaint (key: Comparable[_] ): Paint = {
return this.sectionOutlinePaintMap.getPaint (key)
}

/**
 * Sets the label distributor and sends a  { @link PlotChangeEvent } to all
 * registered listeners.
 *
 * @param distributor the distributor (<code>null</code> not permitted).
 *
 * @since 1.0.6
 */
def setLabelDistributor (distributor: AbstractPieLabelDistributor): Unit = {
if (distributor == null) {
throw new IllegalArgumentException ("Null 'distributor' argument.")
}
this.labelDistributor = distributor
fireChangeEvent
}

/**
 * A flag that controls whether or not the section outline paint is
 * auto-populated from the drawing supplier.
 *
 * @since 1.0.11
 */
private var autoPopulateSectionOutlinePaint: Boolean = false

/**
 * The paint used to draw the outline of the section labels
 * (<code>null</code> permitted).
 */
@transient
private var labelOutlinePaint: Paint = null

/**
 * Returns the flag that controls whether simple or extended labels are
 * displayed on the plot.
 *
 * @return A boolean.
 *
 * @since 1.0.7
 */
def getSimpleLabels: Boolean = {
return this.simpleLabels
}

/**The x-offset for the shadow effect. */
private var shadowXOffset: Double = 4.0f

/**The section paint map. */
private var sectionPaintMap: PaintMap = null

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
this.sectionPaint = SerialUtilities.readPaint (stream)
this.baseSectionPaint = SerialUtilities.readPaint (stream)
this.sectionOutlinePaint = SerialUtilities.readPaint (stream)
this.baseSectionOutlinePaint = SerialUtilities.readPaint (stream)
this.sectionOutlineStroke = SerialUtilities.readStroke (stream)
this.baseSectionOutlineStroke = SerialUtilities.readStroke (stream)
this.shadowPaint = SerialUtilities.readPaint (stream)
this.labelPaint = SerialUtilities.readPaint (stream)
this.labelBackgroundPaint = SerialUtilities.readPaint (stream)
this.labelOutlinePaint = SerialUtilities.readPaint (stream)
this.labelOutlineStroke = SerialUtilities.readStroke (stream)
this.labelShadowPaint = SerialUtilities.readPaint (stream)
this.labelLinkPaint = SerialUtilities.readPaint (stream)
this.labelLinkStroke = SerialUtilities.readStroke (stream)
this.legendItemShape = SerialUtilities.readShape (stream)
}

/**
 * Sets the y-offset for the shadow effect and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param offset the offset (in Java2D units).
 *
 * @see # getShadowYOffset ( )
 */
def setShadowYOffset (offset: Double): Unit = {
this.shadowYOffset = offset
fireChangeEvent
}

/**
 * Draws a single data item.
 *
 * @param g2 the graphics device (<code>null</code> not permitted).
 * @param section the section index.
 * @param dataArea the data plot area.
 * @param state state information for one chart.
 * @param currentPass the current pass index.
 */
protected def drawItem (g2: Graphics2D, section: Int, dataArea: Rectangle2D, state: PiePlotState, currentPass: Int): Unit = {
var n: Number = this.dataset.getValue (section)
if (n == null) {
return
}
var value: Double = n.doubleValue
var angle1: Double = 0.0
var angle2: Double = 0.0
if (this.direction == Rotation.CLOCKWISE) {
angle1 = state.getLatestAngle
angle2 = angle1 - value / state.getTotal * 360.0
}
else if (this.direction == Rotation.ANTICLOCKWISE) {
angle1 = state.getLatestAngle
angle2 = angle1 + value / state.getTotal * 360.0
}
else {
throw new IllegalStateException ("Rotation type not recognised.")
}
var angle: Double = (angle2 - angle1)
if (Math.abs (angle) > getMinimumArcAngleToDraw) {
var ep: Double = 0.0
var mep: Double = getMaximumExplodePercent
if (mep > 0.0) {
ep = getExplodePercent (section) / mep
}
var arcBounds: Rectangle2D = getArcBounds (state.getPieArea, state.getExplodedPieArea, angle1, angle, ep)
var arc: Double = new Double (arcBounds, angle1, angle, Arc2D.PIE)
if (currentPass == 0) {
if (this.shadowPaint != null) {
var shadowArc: Shape = ShapeUtilities.createTranslatedShape (arc, this.shadowXOffset.asInstanceOf[Float], this.shadowYOffset.asInstanceOf[Float] )
g2.setPaint (this.shadowPaint)
g2.fill (shadowArc)
}
}
else if (currentPass == 1) {
var key: Comparable[_] = getSectionKey (section)
var paint: Paint = lookupSectionPaint (key)
g2.setPaint (paint)
g2.fill (arc)
var outlinePaint: Paint = lookupSectionOutlinePaint (key)
var outlineStroke: Stroke = lookupSectionOutlineStroke (key)
if (this.sectionOutlinesVisible) {
g2.setPaint (outlinePaint)
g2.setStroke (outlineStroke)
g2.draw (arc)
}
if (state.getInfo != null) {
var entities: EntityCollection = state.getEntityCollection
if (entities != null) {
var tip: String = null
if (this.toolTipGenerator != null) {
tip = this.toolTipGenerator.generateToolTip (this.dataset, key)
}
var url: String = null
if (this.urlGenerator != null) {
url = this.urlGenerator.generateURL (this.dataset, key, this.pieIndex)
}
var entity: PieSectionEntity = new PieSectionEntity (arc, this.dataset, this.pieIndex, section, key, tip, url)
entities.add (entity)
}
}
}
}
state.setLatestAngle (angle2)
}

/**
 * Returns the outline paint for the specified section.  This is equivalent
 * to <code>lookupSectionPaint(section,
 * getAutoPopulateSectionOutlinePaint())</code>.
 *
 * @param key the section key.
 *
 * @return The paint for the specified section.
 *
 * @since 1.0.3
 *
 * @see # lookupSectionOutlinePaint ( Comparable, boolean )
 */
protected def lookupSectionOutlinePaint (key: Comparable[_] ): Paint = {
return lookupSectionOutlinePaint (key, getAutoPopulateSectionOutlinePaint)
}

/**The stroke used for the label linking lines. */
@transient
private var labelLinkStroke: Stroke = new BasicStroke (0.5f)

/**
 * The outline stroke for ALL sections (overrides list).
 *
 * @deprecated This field is redundant, it is sufficient to use
 *     sectionOutlineStrokeMap and baseSectionOutlineStroke.  Deprecated as
 *     of version 1.0.6.
 */
@transient
private var sectionOutlineStroke: Stroke = null

/**
 * Returns the amount that the section with the specified key should be
 * exploded.
 *
 * @param key the key (<code>null</code> not permitted).
 *
 * @return The amount that the section with the specified key should be
 *     exploded.
 *
 * @throws IllegalArgumentException if <code>key</code> is
 *     <code>null</code>.
 *
 * @since 1.0.3
 *
 * @see # setExplodePercent ( Comparable, double )
 */
def getExplodePercent (key: Comparable[_] ): Double = {
var result: Double = 0.0
if (this.explodePercentages != null) {
var percent: Number = this.explodePercentages.get (key).asInstanceOf[Number]
if (percent != null) {
result = percent.doubleValue
}
}
return result
}

/**
 * Returns the flag that controls whether zero values in the
 * dataset are ignored.
 *
 * @return A boolean.
 *
 * @see # setIgnoreZeroValues ( boolean )
 */
def getIgnoreZeroValues: Boolean = {
return this.ignoreZeroValues
}

/**
 * Returns the section label font.
 *
 * @return The font (never <code>null</code>).
 *
 * @see # setLabelFont ( Font )
 */
def getLabelFont: Font = {
return this.labelFont
}

/**The legend item shape. */
@transient
private var legendItemShape: Shape = null

/**
 * Draws a section label on the left side of the pie chart.
 *
 * @param g2 the graphics device.
 * @param state the state.
 * @param record the label record.
 */
protected def drawLeftLabel (g2: Graphics2D, state: PiePlotState, record: PieLabelRecord): Unit = {
var anchorX: Double = state.getLinkArea.getMinX
var targetX: Double = anchorX - record.getGap
var targetY: Double = record.getAllocatedY
if (this.labelLinksVisible) {
var theta: Double = record.getAngle
var linkX: Double = state.getPieCenterX + Math.cos (theta) * state.getPieWRadius * record.getLinkPercent
var linkY: Double = state.getPieCenterY - Math.sin (theta) * state.getPieHRadius * record.getLinkPercent
var elbowX: Double = state.getPieCenterX + Math.cos (theta) * state.getLinkArea.getWidth / 2.0
var elbowY: Double = state.getPieCenterY - Math.sin (theta) * state.getLinkArea.getHeight / 2.0
var anchorY: Double = elbowY
g2.setPaint (this.labelLinkPaint)
g2.setStroke (this.labelLinkStroke)
var style: PieLabelLinkStyle = getLabelLinkStyle
if (style.equals (PieLabelLinkStyle.STANDARD) ) {
g2.draw (new Double (linkX, linkY, elbowX, elbowY) )
g2.draw (new Double (anchorX, anchorY, elbowX, elbowY) )
g2.draw (new Double (anchorX, anchorY, targetX, targetY) )
}
else if (style.equals (PieLabelLinkStyle.QUAD_CURVE) ) {
var q: QuadCurve2D = new Float
q.setCurve (targetX, targetY, anchorX, anchorY, elbowX, elbowY)
g2.draw (q)
g2.draw (new Double (elbowX, elbowY, linkX, linkY) )
}
else if (style.equals (PieLabelLinkStyle.CUBIC_CURVE) ) {
var c: CubicCurve2D = new Float
c.setCurve (targetX, targetY, anchorX, anchorY, elbowX, elbowY, linkX, linkY)
g2.draw (c)
}
}
var tb: TextBox = record.getLabel
tb.draw (g2, targetX.asInstanceOf[Float], targetY.asInstanceOf[Float], RectangleAnchor.RIGHT)
}

/**
 * Draws the pie.
 *
 * @param g2 the graphics device.
 * @param plotArea the plot area.
 * @param info chart rendering info.
 */
protected def drawPie (g2: Graphics2D, plotArea: Rectangle2D, info: PlotRenderingInfo): Unit = {
var state: PiePlotState = initialise (g2, plotArea, this, null, info)
var labelReserve: Double = 0.0
if (this.labelGenerator != null && ! this.simpleLabels) {
labelReserve = this.labelGap + this.maximumLabelWidth
}
var gapHorizontal: Double = plotArea.getWidth * (this.interiorGap + labelReserve) * 2.0
var gapVertical: Double = plotArea.getHeight * this.interiorGap * 2.0
if (DEBUG_DRAW_INTERIOR) {
var hGap: Double = plotArea.getWidth * this.interiorGap
var vGap: Double = plotArea.getHeight * this.interiorGap
var igx1: Double = plotArea.getX + hGap
var igx2: Double = plotArea.getMaxX - hGap
var igy1: Double = plotArea.getY + vGap
var igy2: Double = plotArea.getMaxY - vGap
g2.setPaint (Color.gray)
g2.draw (new Double (igx1, igy1, igx2 - igx1, igy2 - igy1) )
}
var linkX: Double = plotArea.getX + gapHorizontal / 2
var linkY: Double = plotArea.getY + gapVertical / 2
var linkW: Double = plotArea.getWidth - gapHorizontal
var linkH: Double = plotArea.getHeight - gapVertical
if (this.circular) {
var min: Double = Math.min (linkW, linkH) / 2
linkX = (linkX + linkX + linkW) / 2 - min
linkY = (linkY + linkY + linkH) / 2 - min
linkW = 2 * min
linkH = 2 * min
}
var linkArea: Rectangle2D = new Double (linkX, linkY, linkW, linkH)
state.setLinkArea (linkArea)
if (DEBUG_DRAW_LINK_AREA) {
g2.setPaint (Color.blue)
g2.draw (linkArea)
g2.setPaint (Color.yellow)
g2.draw (new Double (linkArea.getX, linkArea.getY, linkArea.getWidth, linkArea.getHeight) )
}
var lm: Double = 0.0
if (! this.simpleLabels) {
lm = this.labelLinkMargin
}
var hh: Double = linkArea.getWidth * lm * 2.0
var vv: Double = linkArea.getHeight * lm * 2.0
var explodeArea: Rectangle2D = new Double (linkX + hh / 2.0, linkY + vv / 2.0, linkW - hh, linkH - vv)
state.setExplodedPieArea (explodeArea)
var maximumExplodePercent: Double = getMaximumExplodePercent
var percent: Double = maximumExplodePercent / (1.0 + maximumExplodePercent)
var h1: Double = explodeArea.getWidth * percent
var v1: Double = explodeArea.getHeight * percent
var pieArea: Rectangle2D = new Double (explodeArea.getX + h1 / 2.0, explodeArea.getY + v1 / 2.0, explodeArea.getWidth - h1, explodeArea.getHeight - v1)
if (DEBUG_DRAW_PIE_AREA) {
g2.setPaint (Color.green)
g2.draw (pieArea)
}
state.setPieArea (pieArea)
state.setPieCenterX (pieArea.getCenterX)
state.setPieCenterY (pieArea.getCenterY)
state.setPieWRadius (pieArea.getWidth / 2.0)
state.setPieHRadius (pieArea.getHeight / 2.0)
if ((this.dataset != null) && (this.dataset.getKeys.size > 0) ) {
var keys: List[_] = this.dataset.getKeys
var totalValue: Double = DatasetUtilities.calculatePieDatasetTotal (this.dataset)
var passesRequired: Int = state.getPassesRequired

{
var pass: Int = 0
while (pass < passesRequired) {
{
var runningTotal: Double = 0.0

{
var section: Int = 0
while (section < keys.size) {
{
var n: Number = this.dataset.getValue (section)
if (n != null) {
var value: Double = n.doubleValue
if (value > 0.0) {
runningTotal += value
drawItem (g2, section, explodeArea, state, pass)
}
}
}
( {
section += 1;
section
})
}
}
}
( {
pass += 1;
pass
})
}
}
if (this.simpleLabels) {
drawSimpleLabels (g2, keys, totalValue, plotArea, linkArea, state)
}
else {
drawLabels (g2, keys, totalValue, plotArea, linkArea, state)
}
}
else {
drawNoDataMessage (g2, plotArea)
}
}

/**
 * Returns the paint associated with the specified key, or
 * <code>null</code> if there is no paint associated with the key.
 *
 * @param key the key (<code>null</code> not permitted).
 *
 * @return The paint associated with the specified key, or
 *     <code>null</code>.
 *
 * @throws IllegalArgumentException if <code>key</code> is
 *     <code>null</code>.
 *
 * @see # setSectionPaint ( Comparable, Paint )
 *
 * @since 1.0.3
 */
def getSectionPaint (key: Comparable[_] ): Paint = {
return this.sectionPaintMap.getPaint (key)
}

/**
 * Sets the base section paint and sends a  { @link PlotChangeEvent } to all
 * registered listeners.
 *
 * @param paint the paint (<code>null</code> not permitted).
 *
 * @see # getBaseSectionPaint ( )
 */
def setBaseSectionPaint (paint: Paint): Unit = {
if (paint == null) {
throw new IllegalArgumentException ("Null 'paint' argument.")
}
this.baseSectionPaint = paint
fireChangeEvent
}

/**
 * Returns the URL generator.
 *
 * @return The generator (possibly <code>null</code>).
 *
 * @see # setURLGenerator ( PieURLGenerator )
 */
def getURLGenerator: PieURLGenerator = {
return this.urlGenerator
}

/**
 * Returns a key for the specified section.  If there is no such section
 * in the dataset, we generate a key.  This is to provide some backward
 * compatibility for the (now deprecated) methods that get/set attributes
 * based on section indices.  The preferred way of doing this now is to
 * link the attributes directly to the section key (there are new methods
 * for this, starting from version 1.0.3).
 *
 * @param section the section index.
 *
 * @return The key.
 *
 * @since 1.0.3
 */
protected def getSectionKey (section: Int): Comparable[_] = {
var key: Comparable[_] = null
if (this.dataset != null) {
if (section >= 0 && section < this.dataset.getItemCount) {
key = this.dataset.getKey (section)
}
}
if (key == null) {
key = new Integer (section)
}
return key
}

/**
 * A flag that controls whether simple or extended labels are used.
 *
 * @since 1.0.7
 */
private var simpleLabels: Boolean = true

/**The link margin. */
private var labelLinkMargin: Double = 0.025

/**
 * Sets the minimum arc angle that will be drawn.  Pie sections for an
 * angle smaller than this are not drawn, to avoid a JDK bug.  See this
 * link for details:
 * <br><br>
 * <a href="http://www.jfree.org/phpBB2/viewtopic.php?t=2707">
 * http://www.jfree.org/phpBB2/viewtopic.php?t=2707</a>
 * <br><br>
 * ...and this bug report in the Java Bug Parade:
 * <br><br>
 * <a href=
 * "http://developer.java.sun.com/developer/bugParade/bugs/4836495.html">
 * http://developer.java.sun.com/developer/bugParade/bugs/4836495.html</a>
 *
 * @param angle the minimum angle.
 *
 * @see # getMinimumArcAngleToDraw ( )
 */
def setMinimumArcAngleToDraw (angle: Double): Unit = {
this.minimumArcAngleToDraw = angle
}

/**
 * Returns a collection of legend items for the pie chart.
 *
 * @return The legend items (never <code>null</code>).
 */
override def getLegendItems: LegendItemCollection = {
var result: LegendItemCollection = new LegendItemCollection
if (this.dataset == null) {
return result
}
var keys: List[_] = this.dataset.getKeys
var section: Int = 0
var shape: Shape = getLegendItemShape
var iterator: Iterator[_] = keys.iterator
while (iterator.hasNext) {
var key: Comparable[_] = iterator.next.asInstanceOf[Comparable[_]]
var n: Number = this.dataset.getValue (key)
var include: Boolean = true
if (n == null) {
include = ! this.ignoreNullValues
}
else {
var v: Double = n.doubleValue
if (v == 0.0) {
include = ! this.ignoreZeroValues
}
else {
include = v > 0.0
}
}
if (include) {
var label: String = this.legendLabelGenerator.generateSectionLabel (this.dataset, key)
if (label != null) {
var description: String = label
var toolTipText: String = null
if (this.legendLabelToolTipGenerator != null) {
toolTipText = this.legendLabelToolTipGenerator.generateSectionLabel (this.dataset, key)
}
var urlText: String = null
if (this.legendLabelURLGenerator != null) {
urlText = this.legendLabelURLGenerator.generateURL (this.dataset, key, this.pieIndex)
}
var paint: Paint = lookupSectionPaint (key)
var outlinePaint: Paint = lookupSectionOutlinePaint (key)
var outlineStroke: Stroke = lookupSectionOutlineStroke (key)
var item: LegendItem = new LegendItem (label, description, toolTipText, urlText, true, shape, true, paint, true, outlinePaint, outlineStroke, false, new Float, new BasicStroke, Color.black)
item.setDataset (getDataset)
item.setSeriesIndex (this.dataset.getIndex (key) )
item.setSeriesKey (key)
result.add (item)
}
( {
section += 1;
section
})
}
else {
( {
section += 1;
section
})
}
}
return result
}

/**
 * Returns the paint for the specified section.
 *
 * @param section the section index (zero-based).
 *
 * @return The paint (never <code>null</code>).
 *
 * @deprecated Use { @link # getSectionPaint ( Comparable ) } instead.
 */
def getSectionPaint (section: Int): Paint = {
var key: Comparable[_] = getSectionKey (section)
return getSectionPaint (key)
}

/**The y-offset for the shadow effect. */
private var shadowYOffset: Double = 4.0f

/**
 * Sets the section label shadow paint and sends a  { @link PlotChangeEvent }
 * to all registered listeners.
 *
 * @param paint the paint (<code>null</code> permitted).
 *
 * @see # getLabelShadowPaint ( )
 */
def setLabelShadowPaint (paint: Paint): Unit = {
this.labelShadowPaint = paint
fireChangeEvent
}

/**
 * Returns the shape used for legend items.
 *
 * @return The shape (never <code>null</code>).
 *
 * @see # setLegendItemShape ( Shape )
 */
def getLegendItemShape: Shape = {
return this.legendItemShape
}

/**A flag that controls whether or not the label links are drawn. */
private var labelLinksVisible: Boolean = false

/**
 * Draws the labels for the pie sections.
 *
 * @param g2 the graphics device.
 * @param keys the keys.
 * @param totalValue the total value.
 * @param plotArea the plot area.
 * @param linkArea the link area.
 * @param state the state.
 */
protected def drawLabels (g2: Graphics2D, keys: List[_], totalValue: Double, plotArea: Rectangle2D, linkArea: Rectangle2D, state: PiePlotState): Unit = {
var originalComposite: Composite = g2.getComposite
g2.setComposite (AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 1.0f) )
var leftKeys: DefaultKeyedValues = new DefaultKeyedValues
var rightKeys: DefaultKeyedValues = new DefaultKeyedValues
var runningTotal: Double = 0.0
var iterator: Iterator[_] = keys.iterator
while (iterator.hasNext) {
var key: Comparable[_] = iterator.next.asInstanceOf[Comparable[_]]
var include: Boolean = true
var v: Double = 0.0
var n: Number = this.dataset.getValue (key)
if (n == null) {
include = ! this.ignoreNullValues
}
else {
v = n.doubleValue
include = if (this.ignoreZeroValues) v > 0.0 else v >= 0.0
}
if (include) {
runningTotal = runningTotal + v
var mid: Double = this.startAngle + (this.direction.getFactor * ((runningTotal - v / 2.0) * 360) / totalValue)
if (Math.cos (Math.toRadians (mid) ) < 0.0) {
leftKeys.addValue (key, new Double (mid) )
}
else {
rightKeys.addValue (key, new Double (mid) )
}
}
}
g2.setFont (getLabelFont)
var marginX: Double = plotArea.getX + this.interiorGap * plotArea.getWidth
var gap: Double = plotArea.getWidth * this.labelGap
var ww: Double = linkArea.getX - gap - marginX
var labelWidth: Float = this.labelPadding.trimWidth (ww).asInstanceOf[Float]
if (this.labelGenerator != null) {
drawLeftLabels (leftKeys, g2, plotArea, linkArea, labelWidth, state)
drawRightLabels (rightKeys, g2, plotArea, linkArea, labelWidth, state)
}
g2.setComposite (originalComposite)
}

/**
 * Sets the paint associated with the specified key, and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param key the key (<code>null</code> not permitted).
 * @param paint the paint.
 *
 * @throws IllegalArgumentException if <code>key</code> is
 *     <code>null</code>.
 *
 * @see # getSectionPaint ( Comparable )
 *
 * @since 1.0.3
 */
def setSectionPaint (key: Comparable[_], paint: Paint): Unit = {
this.sectionPaintMap.put (key, paint)
fireChangeEvent
}

/**
 * Sets the pie index (this is used by the  { @link MultiplePiePlot } class to
 * track subplots).
 *
 * @param index the index.
 *
 * @see # getPieIndex ( )
 */
def setPieIndex (index: Int): Unit = {
this.pieIndex = index
}

/**
 * The paint used to draw the shadow for the section labels
 * (<code>null</code> permitted).
 */
@transient
private var labelShadowPaint: Paint = null

/**
 * Sets the circular attribute and, if requested, sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param circular the new value of the flag.
 * @param notify notify listeners?
 *
 * @see # isCircular ( )
 */
def setCircular (circular: Boolean, notify: Boolean): Unit = {
this.circular = circular
if (notify) {
fireChangeEvent
}
}

/**
 * Returns the base section paint.  This is used when no other paint is
 * defined, which is rare.  The default value is <code>Color.gray</code>.
 *
 * @return The paint (never <code>null</code>).
 *
 * @see # setBaseSectionPaint ( Paint )
 */
def getBaseSectionPaint: Paint = {
return this.baseSectionPaint
}

/**
 * Returns the outline stroke for the specified section.  This is
 * equivalent to <code>lookupSectionOutlineStroke(section,
 * getAutoPopulateSectionOutlineStroke())</code>.
 *
 * @param key the section key.
 *
 * @return The stroke for the specified section.
 *
 * @since 1.0.3
 *
 * @see # lookupSectionOutlineStroke ( Comparable, boolean )
 */
protected def lookupSectionOutlineStroke (key: Comparable[_] ): Stroke = {
return lookupSectionOutlineStroke (key, getAutoPopulateSectionOutlineStroke)
}

/**
 * Returns the outline stroke for the specified section.  The lookup
 * involves these steps:
 * <ul>
 * <li>if  { @link # getSectionOutlineStroke ( ) } is non-<code>null</code>,
 *         return it;</li>
 * <li>otherwise, if  { @link # getSectionOutlineStroke ( int ) } is
 *         non-<code>null</code> return it;</li>
 * <li>if  { @link # getSectionOutlineStroke ( int ) } is <code>null</code> but
 *         <code>autoPopulate</code> is <code>true</code>, attempt to fetch
 *         a new outline stroke from the drawing supplier
 *         ( { @link # getDrawingSupplier ( ) } );
 * <li>if all else fails, return  { @link # getBaseSectionOutlineStroke ( ) }.
 * </ul>
 *
 * @param key the section key.
 * @param autoPopulate a flag that controls whether the drawing supplier
 *     is used to auto-populate the section outline stroke settings.
 *
 * @return The stroke.
 *
 * @since 1.0.3
 */
protected def lookupSectionOutlineStroke (key: Comparable[_], autoPopulate: Boolean): Stroke = {
var result: Stroke = getSectionOutlineStroke
if (result != null) {
return result
}
result = this.sectionOutlineStrokeMap.getStroke (key)
if (result != null) {
return result
}
if (autoPopulate) {
var ds: DrawingSupplier = getDrawingSupplier
if (ds != null) {
result = ds.getNextOutlineStroke
this.sectionOutlineStrokeMap.put (key, result)
}
else {
result = this.baseSectionOutlineStroke
}
}
else {
result = this.baseSectionOutlineStroke
}
return result
}

/**
 * Sets the legend label tool tip generator and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param generator the generator (<code>null</code> permitted).
 *
 * @see # getLegendLabelToolTipGenerator ( )
 */
def setLegendLabelToolTipGenerator (generator: PieSectionLabelGenerator): Unit = {
this.legendLabelToolTipGenerator = generator
fireChangeEvent
}

/**Flag determining whether to draw an ellipse or a perfect circle. */
private var circular: Boolean = false

/**
 * Sets the paint for ALL sections in the plot.  If this is set to
 * </code>null</code>, then a list of paints is used instead (to allow
 * different colors to be used for each section).
 *
 * @param paint the paint (<code>null</code> permitted).
 *
 * @see # getSectionPaint ( )
 *
 * @deprecated Use { @link # setSectionPaint ( Comparable, Paint ) } and
 * { @link # setBaseSectionPaint ( Paint ) }.  Deprecated as of version 1.0.6.
 */
def setSectionPaint (paint: Paint): Unit = {
this.sectionPaint = paint
fireChangeEvent
}

/**
 * Returns the tool tip generator, an object that is responsible for
 * generating the text items used for tool tips by the plot.  If the
 * generator is <code>null</code>, no tool tips will be created.
 *
 * @return The generator (possibly <code>null</code>).
 *
 * @see # setToolTipGenerator ( PieToolTipGenerator )
 */
def getToolTipGenerator: PieToolTipGenerator = {
return this.toolTipGenerator
}

/**
 * Returns the section label paint.
 *
 * @return The paint (never <code>null</code>).
 *
 * @see # setLabelPaint ( Paint )
 */
def getLabelPaint: Paint = {
return this.labelPaint
}

/**
 * Clears the section outline paint settings for this plot and, if
 * requested, sends a  { @link PlotChangeEvent } to all registered listeners.
 * Be aware that if the <code>autoPopulateSectionPaint</code> flag is set,
 * the section paints may be repopulated using the same colours as before.
 *
 * @param notify notify listeners?
 *
 * @since 1.0.11
 *
 * @see # autoPopulateSectionOutlinePaint
 */
def clearSectionOutlinePaints (notify: Boolean): Unit = {
this.sectionOutlinePaintMap.clear
if (notify) {
fireChangeEvent
}
}

/**
 * Returns the stroke used for the label linking lines.
 *
 * @return The stroke.
 *
 * @see # setLabelLinkStroke ( Stroke )
 */
def getLabelLinkStroke: Stroke = {
return this.labelLinkStroke
}

/**
 * Sets the flag that controls whether or not the section paint is
 * auto-populated by the  { @link # lookupSectionPaint ( Comparable ) } method,
 * and sends a  { @link PlotChangeEvent } to all registered listeners.
 *
 * @param auto auto-populate?
 *
 * @since 1.0.11
 */
def setAutoPopulateSectionPaint (auto: Boolean): Unit = {
this.autoPopulateSectionPaint = auto
fireChangeEvent
}

/**
 * A flag that controls whether or not the section outline stroke is
 * auto-populated from the drawing supplier.
 *
 * @since 1.0.11
 */
private var autoPopulateSectionOutlineStroke: Boolean = false

/**
 * Returns the maximum label width as a percentage of the plot width.
 *
 * @return The width (a percentage, where 0.20 = 20 percent).
 *
 * @see # setMaximumLabelWidth ( double )
 */
def getMaximumLabelWidth: Double = {
return this.maximumLabelWidth
}

/**
 * Sets the gap between the edge of the pie and the labels (expressed as a
 * percentage of the plot width) and sends a  { @link PlotChangeEvent } to all
 * registered listeners.
 *
 * @param gap the gap (a percentage, where 0.05 = five percent).
 *
 * @see # getLabelGap ( )
 */
def setLabelGap (gap: Double): Unit = {
this.labelGap = gap
fireChangeEvent
}

/**
 * The pie section label distributor.
 *
 * @since 1.0.6
 */
private var labelDistributor: AbstractPieLabelDistributor = null

/**
 * Sets the flag that controls whether or not the outline is drawn for
 * each pie section, and sends a  { @link PlotChangeEvent } to all registered
 * listeners.
 *
 * @param visible the flag.
 *
 * @see # getSectionOutlinesVisible ( )
 */
def setSectionOutlinesVisible (visible: Boolean): Unit = {
this.sectionOutlinesVisible = visible
fireChangeEvent
}

/**
 * Draws the pie section labels in the simple form.
 *
 * @param g2 the graphics device.
 * @param keys the section keys.
 * @param totalValue the total value for all sections in the pie.
 * @param plotArea the plot area.
 * @param pieArea the area containing the pie.
 * @param state the plot state.
 *
 * @since 1.0.7
 */
protected def drawSimpleLabels (g2: Graphics2D, keys: List[_], totalValue: Double, plotArea: Rectangle2D, pieArea: Rectangle2D, state: PiePlotState): Unit = {
var originalComposite: Composite = g2.getComposite
g2.setComposite (AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 1.0f) )
var labelInsets: RectangleInsets = new RectangleInsets (UnitType.RELATIVE, 0.18, 0.18, 0.18, 0.18)
var labelsArea: Rectangle2D = labelInsets.createInsetRectangle (pieArea)
var runningTotal: Double = 0.0
var iterator: Iterator[_] = keys.iterator
while (iterator.hasNext) {
var key: Comparable[_] = iterator.next.asInstanceOf[Comparable[_]]
var include: Boolean = true
var v: Double = 0.0
var n: Number = getDataset.getValue (key)
if (n == null) {
include = ! getIgnoreNullValues
}
else {
v = n.doubleValue
include = if (getIgnoreZeroValues) v > 0.0 else v >= 0.0
}
if (include) {
runningTotal = runningTotal + v
var mid: Double = getStartAngle + (getDirection.getFactor * ((runningTotal - v / 2.0) * 360) / totalValue)
var arc: Arc2D = new Double (labelsArea, getStartAngle, mid - getStartAngle, Arc2D.OPEN)
var x: Int = arc.getEndPoint.getX.asInstanceOf[Int]
var y: Int = arc.getEndPoint.getY.asInstanceOf[Int]
var labelGenerator: PieSectionLabelGenerator = getLabelGenerator
if (labelGenerator == null) {
continue //todo: continue is not supported
}
var label: String = labelGenerator.generateSectionLabel (this.dataset, key)
if (label == null) {
continue //todo: continue is not supported
}
g2.setFont (this.labelFont)
var fm: FontMetrics = g2.getFontMetrics
var bounds: Rectangle2D = TextUtilities.getTextBounds (label, g2, fm)
var out: Rectangle2D = this.labelPadding.createOutsetRectangle (bounds)
var bg: Shape = ShapeUtilities.createTranslatedShape (out, x - bounds.getCenterX, y - bounds.getCenterY)
if (this.labelShadowPaint != null) {
var shadow: Shape = ShapeUtilities.createTranslatedShape (bg, this.shadowXOffset, this.shadowYOffset)
g2.setPaint (this.labelShadowPaint)
g2.fill (shadow)
}
if (this.labelBackgroundPaint != null) {
g2.setPaint (this.labelBackgroundPaint)
g2.fill (bg)
}
if (this.labelOutlinePaint != null && this.labelOutlineStroke != null) {
g2.setPaint (this.labelOutlinePaint)
g2.setStroke (this.labelOutlineStroke)
g2.draw (bg)
}
g2.setPaint (this.labelPaint)
g2.setFont (this.labelFont)
TextUtilities.drawAlignedString (getLabelGenerator.generateSectionLabel (getDataset, key), g2, x, y, TextAnchor.CENTER)
}
}
g2.setComposite (originalComposite)
}

/**
 * Draws a section label on the right side of the pie chart.
 *
 * @param g2 the graphics device.
 * @param state the state.
 * @param record the label record.
 */
protected def drawRightLabel (g2: Graphics2D, state: PiePlotState, record: PieLabelRecord): Unit = {
var anchorX: Double = state.getLinkArea.getMaxX
var targetX: Double = anchorX + record.getGap
var targetY: Double = record.getAllocatedY
if (this.labelLinksVisible) {
var theta: Double = record.getAngle
var linkX: Double = state.getPieCenterX + Math.cos (theta) * state.getPieWRadius * record.getLinkPercent
var linkY: Double = state.getPieCenterY - Math.sin (theta) * state.getPieHRadius * record.getLinkPercent
var elbowX: Double = state.getPieCenterX + Math.cos (theta) * state.getLinkArea.getWidth / 2.0
var elbowY: Double = state.getPieCenterY - Math.sin (theta) * state.getLinkArea.getHeight / 2.0
var anchorY: Double = elbowY
g2.setPaint (this.labelLinkPaint)
g2.setStroke (this.labelLinkStroke)
var style: PieLabelLinkStyle = getLabelLinkStyle
if (style.equals (PieLabelLinkStyle.STANDARD) ) {
g2.draw (new Double (linkX, linkY, elbowX, elbowY) )
g2.draw (new Double (anchorX, anchorY, elbowX, elbowY) )
g2.draw (new Double (anchorX, anchorY, targetX, targetY) )
}
else if (style.equals (PieLabelLinkStyle.QUAD_CURVE) ) {
var q: QuadCurve2D = new Float
q.setCurve (targetX, targetY, anchorX, anchorY, elbowX, elbowY)
g2.draw (q)
g2.draw (new Double (elbowX, elbowY, linkX, linkY) )
}
else if (style.equals (PieLabelLinkStyle.CUBIC_CURVE) ) {
var c: CubicCurve2D = new Float
c.setCurve (targetX, targetY, anchorX, anchorY, elbowX, elbowY, linkX, linkY)
g2.draw (c)
}
}
var tb: TextBox = record.getLabel
tb.draw (g2, targetX.asInstanceOf[Float], targetY.asInstanceOf[Float], RectangleAnchor.LEFT)
}

/**
 * Sets the paint used to fill a section of the pie and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param section the section index (zero-based).
 * @param paint the paint (<code>null</code> permitted).
 *
 * @deprecated Use { @link # setSectionPaint ( Comparable, Paint ) } instead.
 */
def setSectionPaint (section: Int, paint: Paint): Unit = {
var key: Comparable[_] = getSectionKey (section)
setSectionPaint (key, paint)
}

/**
 * Sets the flag that controls whether simple or extended labels are
 * displayed on the plot, and sends a  { @link PlotChangeEvent } to all
 * registered listeners.
 *
 * @param simple the new flag value.
 *
 * @since 1.0.7
 */
def setSimpleLabels (simple: Boolean): Unit = {
this.simpleLabels = simple
fireChangeEvent
}

/**
 * Creates a plot that will draw a pie chart for the specified dataset.
 *
 * @param dataset the dataset (<code>null</code> permitted).
 */
def this (dataset: PieDataset) {
this ()
`super`
this.dataset = dataset
if (dataset != null) {
dataset.addChangeListener (this)
}
this.pieIndex = 0
this.interiorGap = DEFAULT_INTERIOR_GAP
this.circular = true
this.startAngle = DEFAULT_START_ANGLE
this.direction = Rotation.CLOCKWISE
this.minimumArcAngleToDraw = DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW
this.sectionPaint = null
this.sectionPaintMap = new PaintMap
this.baseSectionPaint = Color.gray
this.autoPopulateSectionPaint = true
this.sectionOutlinesVisible = true
this.sectionOutlinePaint = null
this.sectionOutlinePaintMap = new PaintMap
this.baseSectionOutlinePaint = DEFAULT_OUTLINE_PAINT
this.autoPopulateSectionOutlinePaint = false
this.sectionOutlineStroke = null
this.sectionOutlineStrokeMap = new StrokeMap
this.baseSectionOutlineStroke = DEFAULT_OUTLINE_STROKE
this.autoPopulateSectionOutlineStroke = false
this.explodePercentages = new TreeMap[_, _]
this.labelGenerator = new StandardPieSectionLabelGenerator
this.labelFont = DEFAULT_LABEL_FONT
this.labelPaint = DEFAULT_LABEL_PAINT
this.labelBackgroundPaint = DEFAULT_LABEL_BACKGROUND_PAINT
this.labelOutlinePaint = DEFAULT_LABEL_OUTLINE_PAINT
this.labelOutlineStroke = DEFAULT_LABEL_OUTLINE_STROKE
this.labelShadowPaint = DEFAULT_LABEL_SHADOW_PAINT
this.labelLinksVisible = true
this.labelDistributor = new PieLabelDistributor (0)
this.simpleLabels = false
this.simpleLabelOffset = new RectangleInsets (UnitType.RELATIVE, 0.18, 0.18, 0.18, 0.18)
this.labelPadding = new RectangleInsets (2, 2, 2, 2)
this.toolTipGenerator = null
this.urlGenerator = null
this.legendLabelGenerator = new StandardPieSectionLabelGenerator
this.legendLabelToolTipGenerator = null
this.legendLabelURLGenerator = null
this.legendItemShape = Plot.DEFAULT_LEGEND_ITEM_CIRCLE
this.ignoreNullValues = false
this.ignoreZeroValues = false

/**
 * Sets the outline paint associated with the specified key, and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param key the key (<code>null</code> not permitted).
 * @param paint the paint.
 *
 * @throws IllegalArgumentException if <code>key</code> is
 *     <code>null</code>.
 *
 * @see # getSectionOutlinePaint ( Comparable )
 *
 * @since 1.0.3
 */
def setSectionOutlinePaint (key: Comparable[_], paint: Paint): Unit = {
this.sectionOutlinePaintMap.put (key, paint)
fireChangeEvent
}

/**
 * Returns the section label shadow paint.
 *
 * @return The paint (possibly <code>null</code>).
 *
 * @see # setLabelShadowPaint ( Paint )
 */
def getLabelShadowPaint: Paint = {
return this.labelShadowPaint
}

/**
 * Sets the flag that controls whether or not the section outline paint is
 * auto-populated by the  { @link # lookupSectionOutlinePaint ( Comparable ) }
 * method, and sends a  { @link PlotChangeEvent } to all registered listeners.
 *
 * @param auto auto-populate?
 *
 * @since 1.0.11
 */
def setAutoPopulateSectionOutlinePaint (auto: Boolean): Unit = {
this.autoPopulateSectionOutlinePaint = auto
fireChangeEvent
}

/**
 * Returns a rectangle that can be used to create a pie section (taking
 * into account the amount by which the pie section is 'exploded').
 *
 * @param unexploded the area inside which the unexploded pie sections are
 *                    drawn.
 * @param exploded the area inside which the exploded pie sections are
 *                  drawn.
 * @param angle the start angle.
 * @param extent the extent of the arc.
 * @param explodePercent the amount by which the pie section is exploded.
 *
 * @return A rectangle that can be used to create a pie section.
 */
protected def getArcBounds (unexploded: Rectangle2D, exploded: Rectangle2D, angle: Double, extent: Double, explodePercent: Double): Rectangle2D = {
if (explodePercent == 0.0) {
return unexploded
}
else {
var arc1: Arc2D = new Double (unexploded, angle, extent / 2, Arc2D.OPEN)
var point1: Point2D = arc1.getEndPoint
var arc2: Double = new Double (exploded, angle, extent / 2, Arc2D.OPEN)
var point2: Point2D = arc2.getEndPoint
var deltaX: Double = (point1.getX - point2.getX) * explodePercent
var deltaY: Double = (point1.getY - point2.getY) * explodePercent
return new Double (unexploded.getX - deltaX, unexploded.getY - deltaY, unexploded.getWidth, unexploded.getHeight)
}
}

/**
 * The label link style.
 *
 * @since 1.0.10
 */
private var labelLinkStyle: PieLabelLinkStyle = PieLabelLinkStyle.STANDARD

/**
 * Returns the base section stroke.  This is used when no other stroke is
 * available.
 *
 * @return The stroke (never <code>null</code>).
 *
 * @see # setBaseSectionOutlineStroke ( Stroke )
 */
def getBaseSectionOutlineStroke: Stroke = {
return this.baseSectionOutlineStroke
}

/**
 * Sets the section label background paint and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param paint the paint (<code>null</code> permitted).
 *
 * @see # getLabelBackgroundPaint ( )
 */
def setLabelBackgroundPaint (paint: Paint): Unit = {
this.labelBackgroundPaint = paint
fireChangeEvent
}

/**
 * Initialises the drawing procedure.  This method will be called before
 * the first item is rendered, giving the plot an opportunity to initialise
 * any state information it wants to maintain.
 *
 * @param g2 the graphics device.
 * @param plotArea the plot area (<code>null</code> not permitted).
 * @param plot the plot.
 * @param index the secondary index (<code>null</code> for primary
 *               renderer).
 * @param info collects chart rendering information for return to caller.
 *
 * @return A state object (maintains state information relevant to one
 *         chart drawing).
 */
def initialise (g2: Graphics2D, plotArea: Rectangle2D, plot: PiePlot, index: Integer, info: PlotRenderingInfo): PiePlotState = {
var state: PiePlotState = new PiePlotState (info)
state.setPassesRequired (2)
if (this.dataset != null) {
state.setTotal (DatasetUtilities.calculatePieDatasetTotal (plot.getDataset) )
}
state.setLatestAngle (plot.getStartAngle)
return state
}

/**
 * Returns the gap between the edge of the pie and the labels, expressed as
 * a percentage of the plot width.
 *
 * @return The gap (a percentage, where 0.05 = five percent).
 *
 * @see # setLabelGap ( double )
 */
def getLabelGap: Double = {
return this.labelGap
}

/**The section label generator. */
private var labelGenerator: PieSectionLabelGenerator = null

/**
 * Returns the outline stroke associated with the specified key, or
 * <code>null</code> if there is no stroke associated with the key.
 *
 * @param key the key (<code>null</code> not permitted).
 *
 * @return The stroke associated with the specified key, or
 *     <code>null</code>.
 *
 * @throws IllegalArgumentException if <code>key</code> is
 *     <code>null</code>.
 *
 * @see # setSectionOutlineStroke ( Comparable, Stroke )
 *
 * @since 1.0.3
 */
def getSectionOutlineStroke (key: Comparable[_] ): Stroke = {
return this.sectionOutlineStrokeMap.getStroke (key)
}

/**The legend label generator. */
private var legendLabelGenerator: PieSectionLabelGenerator = null

/**
 * Returns the amount that a section should be 'exploded'.
 *
 * @param section the section number.
 *
 * @return The amount that a section should be 'exploded'.
 *
 * @deprecated Use { @link # getExplodePercent ( Comparable ) } instead.
 */
def getExplodePercent (section: Int): Double = {
var key: Comparable[_] = getSectionKey (section)
return getExplodePercent (key)
}

/**
 * Returns the paint for the specified section.  This is equivalent to
 * <code>lookupSectionPaint(section, getAutoPopulateSectionPaint())</code>.
 *
 * @param key the section key.
 *
 * @return The paint for the specified section.
 *
 * @since 1.0.3
 *
 * @see # lookupSectionPaint ( Comparable, boolean )
 */
protected def lookupSectionPaint (key: Comparable[_] ): Paint = {
return lookupSectionPaint (key, getAutoPopulateSectionPaint)
}

/**
 * Returns the base section paint.  This is used when no other paint is
 * available.
 *
 * @return The paint (never <code>null</code>).
 *
 * @see # setBaseSectionOutlinePaint ( Paint )
 */
def getBaseSectionOutlinePaint: Paint = {
return this.baseSectionOutlinePaint
}

/**
 * Sets the section label font and sends a  { @link PlotChangeEvent } to all
 * registered listeners.
 *
 * @param font the font (<code>null</code> not permitted).
 *
 * @see # getLabelFont ( )
 */
def setLabelFont (font: Font): Unit = {
if (font == null) {
throw new IllegalArgumentException ("Null 'font' argument.")
}
this.labelFont = font
fireChangeEvent
}

/**
 * Returns the section label background paint.
 *
 * @return The paint (possibly <code>null</code>).
 *
 * @see # setLabelBackgroundPaint ( Paint )
 */
def getLabelBackgroundPaint: Paint = {
return this.labelBackgroundPaint
}

/**
 * Returns the label padding.
 *
 * @return The label padding (never <code>null</code>).
 *
 * @since 1.0.7
 *
 * @see # setLabelPadding ( RectangleInsets )
 */
def getLabelPadding: RectangleInsets = {
return this.labelPadding
}

/**
 * Returns the start angle for the first pie section.  This is measured in
 * degrees starting from 3 o'clock and measuring anti-clockwise.
 *
 * @return The start angle.
 *
 * @see # setStartAngle ( double )
 */
def getStartAngle: Double = {
return this.startAngle
}

/**
 * A flag indicating whether the pie chart is circular, or stretched into
 * an elliptical shape.
 *
 * @param flag the new value.
 *
 * @see # isCircular ( )
 */
def setCircular (flag: Boolean): Unit = {
setCircular (flag, true)
}

/**The font used to display the section labels. */
private var labelFont: Font = null

/**
 * A flag that controls whether or not an outline is drawn for each
 * section in the plot.
 */
private var sectionOutlinesVisible: Boolean = false

/**
 * Sets the outline stroke associated with the specified key, and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param key the key (<code>null</code> not permitted).
 * @param stroke the stroke.
 *
 * @throws IllegalArgumentException if <code>key</code> is
 *     <code>null</code>.
 *
 * @see # getSectionOutlineStroke ( Comparable )
 *
 * @since 1.0.3
 */
def setSectionOutlineStroke (key: Comparable[_], stroke: Stroke): Unit = {
this.sectionOutlineStrokeMap.put (key, stroke)
fireChangeEvent
}

/**
 * Returns the dataset.
 *
 * @return The dataset (possibly <code>null</code>).
 *
 * @see # setDataset ( PieDataset )
 */
def getDataset: PieDataset = {
return this.dataset
}

/**
 * Returns a short string describing the type of plot.
 *
 * @return The plot type.
 */
def getPlotType: String = {
return localizationResources.getString ("Pie_Plot")
}

/**
 * Sets the paint used to fill a section of the pie and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param section the section index (zero-based).
 * @param paint the paint (<code>null</code> permitted).
 *
 * @deprecated Use { @link # setSectionOutlinePaint ( Comparable, Paint ) }
 *     instead.
 */
def setSectionOutlinePaint (section: Int, paint: Paint): Unit = {
var key: Comparable[_] = getSectionKey (section)
setSectionOutlinePaint (key, paint)
}

/**
 * Draws the plot on a Java 2D graphics device (such as the screen or a
 * printer).
 *
 * @param g2 the graphics device.
 * @param area the area within which the plot should be drawn.
 * @param anchor the anchor point (<code>null</code> permitted).
 * @param parentState the state from the parent plot, if there is one.
 * @param info collects info about the drawing
 *              (<code>null</code> permitted).
 */
def draw (g2: Graphics2D, area: Rectangle2D, anchor: Point2D, parentState: PlotState, info: PlotRenderingInfo): Unit = {
var insets: RectangleInsets = getInsets
insets.trim (area)
if (info != null) {
info.setPlotArea (area)
info.setDataArea (area)
}
drawBackground (g2, area)
drawOutline (g2, area)
var savedClip: Shape = g2.getClip
g2.clip (area)
var originalComposite: Composite = g2.getComposite
g2.setComposite (AlphaComposite.getInstance (AlphaComposite.SRC_OVER, getForegroundAlpha) )
if (! DatasetUtilities.isEmptyOrNull (this.dataset) ) {
drawPie (g2, area, info)
}
else {
drawNoDataMessage (g2, area)
}
g2.setClip (savedClip)
g2.setComposite (originalComposite)
drawOutline (g2, area)
}

/**
 * Sets the maximum label width as a percentage of the plot width and sends
 * a  { @link PlotChangeEvent } to all registered listeners.
 *
 * @param width the width (a percentage, where 0.20 = 20 percent).
 *
 * @see # getMaximumLabelWidth ( )
 */
def setMaximumLabelWidth (width: Double): Unit = {
this.maximumLabelWidth = width
fireChangeEvent
}

/**
 * Returns the flag that controls whether or not label linking lines are
 * visible.
 *
 * @return A boolean.
 *
 * @see # setLabelLinksVisible ( boolean )
 */
def getLabelLinksVisible: Boolean = {
return this.labelLinksVisible
}

/**
 * Sets the tool tip generator and sends a  { @link PlotChangeEvent } to all
 * registered listeners.  Set the generator to <code>null</code> if you
 * don't want any tool tips.
 *
 * @param generator the generator (<code>null</code> permitted).
 *
 * @see # getToolTipGenerator ( )
 */
def setToolTipGenerator (generator: PieToolTipGenerator): Unit = {
this.toolTipGenerator = generator
fireChangeEvent
}

/**
 * Returns the section label outline paint.
 *
 * @return The paint (possibly <code>null</code>).
 *
 * @see # setLabelOutlinePaint ( Paint )
 */
def getLabelOutlinePaint: Paint = {
return this.labelOutlinePaint
}

/**The percentage amount to explode each pie section. */
private var explodePercentages: Map[_, _] = null

/**
 * The amount of space left around the outside of the pie plot, expressed
 * as a percentage of the plot area width and height.
 */
private var interiorGap: Double = .0

/**
 * Sets a flag that controls whether <code>null</code> values are ignored,
 * and sends a  { @link PlotChangeEvent } to all registered listeners.  At
 * present, this only affects whether or not the key is presented in the
 * legend.
 *
 * @param flag the flag.
 *
 * @see # getIgnoreNullValues ( )
 * @see # setIgnoreZeroValues ( boolean )
 */
def setIgnoreNullValues (flag: Boolean): Unit = {
this.ignoreNullValues = flag
fireChangeEvent
}

/**
 * Returns the outline stroke for ALL sections in the plot.
 *
 * @return The stroke (possibly <code>null</code>).
 *
 * @see # setSectionOutlineStroke ( Stroke )
 *
 * @deprecated Use { @link # getSectionOutlineStroke ( Comparable ) } and
 * { @link # getBaseSectionOutlineStroke ( ) }.  Deprecated as of version
 *     1.0.6.
 */
def getSectionOutlineStroke: Stroke = {
return this.sectionOutlineStroke
}

/**
 * Returns the interior gap, measured as a percentage of the available
 * drawing space.
 *
 * @return The gap (as a percentage of the available drawing space).
 *
 * @see # setInteriorGap ( double )
 */
def getInteriorGap: Double = {
return this.interiorGap
}

/**
 * Sets the dataset and sends a  { @link DatasetChangeEvent } to 'this'.
 *
 * @param dataset the dataset (<code>null</code> permitted).
 *
 * @see # getDataset ( )
 */
def setDataset (dataset: PieDataset): Unit = {
var existing: PieDataset = this.dataset
if (existing != null) {
existing.removeChangeListener (this)
}
this.dataset = dataset
if (dataset != null) {
setDatasetGroup (dataset.getGroup)
dataset.addChangeListener (this)
}
var event: DatasetChangeEvent = new DatasetChangeEvent (this, dataset)
datasetChanged (event)
}

/**The maximum label width as a percentage of the plot width. */
private var maximumLabelWidth: Double = 0.14

/**
 * Returns the y-offset for the shadow effect.
 *
 * @return The offset (in Java2D units).
 *
 * @see # setShadowYOffset ( double )
 */
def getShadowYOffset: Double = {
return this.shadowYOffset
}

/**
 * Sets the flag that controls whether or not the section outline stroke is
 * auto-populated by the  { @link # lookupSectionOutlineStroke ( Comparable ) }
 * method, and sends a  { @link PlotChangeEvent } to all registered listeners.
 *
 * @param auto auto-populate?
 *
 * @since 1.0.11
 */
def setAutoPopulateSectionOutlineStroke (auto: Boolean): Unit = {
this.autoPopulateSectionOutlineStroke = auto
fireChangeEvent
}

/**
 * The stroke used to draw the outline of the section labels
 * (<code>null</code> permitted).
 */
@transient
private var labelOutlineStroke: Stroke = null

/**
 * Sets the offset for the simple labels and sends a
 * { @link PlotChangeEvent } to all registered listeners.
 *
 * @param offset the offset (<code>null</code> not permitted).
 *
 * @since 1.0.7
 *
 * @see # getSimpleLabelOffset ( )
 */
def setSimpleLabelOffset (offset: RectangleInsets): Unit = {
if (offset == null) {
throw new IllegalArgumentException ("Null 'offset' argument.")
}
this.simpleLabelOffset = offset
fireChangeEvent
}

/**
 * Returns the paint for the specified section.
 *
 * @param section the section index (zero-based).
 *
 * @return The paint (possibly <code>null</code>).
 *
 * @deprecated Use { @link # getSectionOutlinePaint ( Comparable ) } instead.
 */
def getSectionOutlinePaint (section: Int): Paint = {
var key: Comparable[_] = getSectionKey (section)
return getSectionOutlinePaint (key)
}

/**
 * Returns the legend label generator.
 *
 * @return The legend label generator (never <code>null</code>).
 *
 * @see # setLegendLabelGenerator ( PieSectionLabelGenerator )
 */
def getLegendLabelGenerator: PieSectionLabelGenerator = {
return this.legendLabelGenerator
}

/**
 * Returns the stroke for the specified section.
 *
 * @param section the section index (zero-based).
 *
 * @return The stroke (possibly <code>null</code>).
 *
 * @deprecated Use { @link # getSectionOutlineStroke ( Comparable ) } instead.
 */
def getSectionOutlineStroke (section: Int): Stroke = {
var key: Comparable[_] = getSectionKey (section)
return getSectionOutlineStroke (key)
}

/**
 * Returns the outline paint for ALL sections in the plot.
 *
 * @return The paint (possibly <code>null</code>).
 *
 * @see # setSectionOutlinePaint ( Paint )
 *
 * @deprecated Use { @link # getSectionOutlinePaint ( Comparable ) } and
 * { @link # getBaseSectionOutlinePaint ( ) }.  Deprecated as of version
 *     1.0.6.
 */
def getSectionOutlinePaint: Paint = {
return this.sectionOutlinePaint
}

/**A tool tip generator for the legend. */
private var legendLabelToolTipGenerator: PieSectionLabelGenerator = null

/**
 * Sets the outline stroke for ALL sections in the plot.  If this is set to
 * </code>null</code>, then a list of paints is used instead (to allow
 * different colors to be used for each section).
 *
 * @param stroke the stroke (<code>null</code> permitted).
 *
 * @see # getSectionOutlineStroke ( )
 *
 * @deprecated Use { @link # setSectionOutlineStroke ( Comparable, Stroke ) } and
 * { @link # setBaseSectionOutlineStroke ( Stroke ) }.  Deprecated as of
 *     version 1.0.6.
 */
def setSectionOutlineStroke (stroke: Stroke): Unit = {
this.sectionOutlineStroke = stroke
fireChangeEvent
}

/**The section outline paint map. */
private var sectionOutlinePaintMap: PaintMap = null

/**
 * Returns the minimum arc angle that will be drawn.  Pie sections for an
 * angle smaller than this are not drawn, to avoid a JDK bug.
 *
 * @return The minimum angle.
 *
 * @see # setMinimumArcAngleToDraw ( double )
 */
def getMinimumArcAngleToDraw: Double = {
return this.minimumArcAngleToDraw
}

/**
 * The color used to draw the background of the section labels.  If this
 * is <code>null</code>, the background is not filled.
 */
@transient
private var labelBackgroundPaint: Paint = null

/**
 * Returns the section label generator.
 *
 * @return The generator (possibly <code>null</code>).
 *
 * @see # setLabelGenerator ( PieSectionLabelGenerator )
 */
def getLabelGenerator: PieSectionLabelGenerator = {
return this.labelGenerator
}

/**
 * Sets the outline paint for ALL sections in the plot.  If this is set to
 * </code>null</code>, then a list of paints is used instead (to allow
 * different colors to be used for each section).
 *
 * @param paint the paint (<code>null</code> permitted).
 *
 * @see # getSectionOutlinePaint ( )
 *
 * @deprecated Use { @link # setSectionOutlinePaint ( Comparable, Paint ) } and
 * { @link # setBaseSectionOutlinePaint ( Paint ) }.  Deprecated as of
 *     version 1.0.6.
 */
def setSectionOutlinePaint (paint: Paint): Unit = {
this.sectionOutlinePaint = paint
fireChangeEvent
}

}

