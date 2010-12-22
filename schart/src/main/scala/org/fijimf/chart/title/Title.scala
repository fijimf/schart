package org.fijimf.chart.title

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import javax.swing.event.EventListenerList
import org.jfree.chart.block.AbstractBlock
import org.jfree.chart.block.Block
import org.jfree.chart.event.TitleChangeEvent
import org.jfree.chart.event.TitleChangeListener
import org.jfree.ui.HorizontalAlignment
import org.jfree.ui.RectangleEdge
import org.jfree.ui.RectangleInsets
import org.jfree.ui.VerticalAlignment
import org.jfree.util.ObjectUtilities
import org.fijimf.chart.block.Block

case class Title(position: RectangleEdge=RectangleEdge.TOP,
                 horizontalAlignment: HorizontalAlignment=HorizontalAlignment.CENTER,
                 verticalAlignment: VerticalAlignment=VerticalAlignment.CENTER,
                 padding: RectangleInsets= new RectangleInsets(1, 1, 1, 1))   extends AbstractBlock with Block with Cloneable with Serializable {
protected def this () {
this()
if (position == null) {
throw new IllegalArgumentException("Null 'position' argument.")
}
if (horizontalAlignment == null) {
throw new IllegalArgumentException("Null 'horizontalAlignment' argument.")
}
if (verticalAlignment == null) {
throw new IllegalArgumentException("Null 'verticalAlignment' argument.")
}
if (padding == null) {
throw new IllegalArgumentException("Null 'spacer' argument.")
}
this.visible = true
this.position = position
this.horizontalAlignment = horizontalAlignment
this.verticalAlignment = verticalAlignment
setPadding(padding)
this.listenerList = new EventListenerList
this.notify = true


abstract class Title
  private var verticalAlignment: VerticalAlignment = null
  def getNotify: Boolean = {
    return this.notify
  }
  private var horizontalAlignment: HorizontalAlignment = null
  def setHorizontalAlignment(alignment: HorizontalAlignment): Unit = {
    if (alignment == null) {
      throw new IllegalArgumentException("Null 'alignment' argument.")
    }
    if (this.horizontalAlignment != alignment) {
      this.horizontalAlignment = alignment
      notifyListeners(new TitleChangeEvent(this))
    }
  }
  private var notify: Boolean = false
  def isVisible: Boolean = {
    return this.visible
  }
  def setVisible(visible: Boolean): Unit = {
    this.visible = visible
    notifyListeners(new TitleChangeEvent(this))
  }
  def getHorizontalAlignment: HorizontalAlignment = {
    return this.horizontalAlignment
  }
  private def readObject(stream: ObjectInputStream): Unit = {
    stream.defaultReadObject
    this.listenerList = new EventListenerList
  }
  def getPosition: RectangleEdge = {
    return this.position
  }
  def setNotify(flag: Boolean): Unit = {
    this.notify = flag
    if (flag) {
      notifyListeners(new TitleChangeEvent(this))
    }
  }
  def removeChangeListener(listener: TitleChangeListener): Unit = {
    this.listenerList.remove(classOf[TitleChangeListener], listener)
  }
  private var position: RectangleEdge = null

  def addChangeListener(listener: TitleChangeListener): Unit = {
    this.listenerList.add(classOf[TitleChangeListener], listener)
  }
  protected def this() {
    this ()
    `this`(Title.DEFAULT_POSITION, Title.DEFAULT_HORIZONTAL_ALIGNMENT, Title.DEFAULT_VERTICAL_ALIGNMENT, Title.DEFAULT_PADDING)

    /**
     * Creates a new title.
     *
     * @param position the position of the title (<code>null</code> not
     *                  permitted).
     * @param horizontalAlignment the horizontal alignment of the title (LEFT,
     *                             CENTER or RIGHT, <code>null</code> not
     *                             permitted).
     * @param verticalAlignment the vertical alignment of the title (TOP,
     *                           MIDDLE or BOTTOM, <code>null</code> not
     *                           permitted).
     * @param padding the amount of space to leave around the outside of the
     *                 title (<code>null</code> not permitted).
     */
  /**
   * Returns the vertical alignment of the title.
   *
   * @return The vertical alignment (never <code>null</code>).
   */
  def getVerticalAlignment: VerticalAlignment = {
    return this.verticalAlignment
  }

  /**
   * Creates a new title, using default attributes where necessary.
   *
   * @param position the position of the title (<code>null</code> not
   *                  permitted).
   * @param horizontalAlignment the horizontal alignment of the title
   *                             (<code>null</code> not permitted).
   * @param verticalAlignment the vertical alignment of the title
   *                           (<code>null</code> not permitted).
   */
  protected def this(position: RectangleEdge, horizontalAlignment: HorizontalAlignment, verticalAlignment: VerticalAlignment) {
    this ()
    `this`(position, horizontalAlignment, verticalAlignment, Title.DEFAULT_PADDING)

    /**
     * Notifies all registered listeners that the chart title has changed in
     * some way.
     *
     * @param event an object that contains information about the change to
     *               the title.
     */
    protected def notifyListeners(event: TitleChangeEvent): Unit = {
  if (this.notify) {
    var listeners: Array[AnyRef] = this.listenerList.getListenerList

    {
      var i: Int = listeners.length - 2
      while (i >= 0) {
        {
          if (listeners(i) == classOf[TitleChangeListener]) {
            (listeners(i + 1).asInstanceOf[TitleChangeListener]).titleChanged(event)
          }
        }
        i -= 2
      }
    }
  }
}

/**
 * Sets the vertical alignment for the title, and notifies any registered
 * listeners of the change.
 *
 * @param alignment the new vertical alignment (TOP, MIDDLE or BOTTOM,
 *                   <code>null</code> not permitted).
 */
def setVerticalAlignment (alignment: VerticalAlignment): Unit = {
if (alignment == null) {
throw new IllegalArgumentException ("Null 'alignment' argument.")
}
if (this.verticalAlignment != alignment) {
this.verticalAlignment = alignment
notifyListeners (new TitleChangeEvent (this) )
}
}

/**
 * Draws the title on a Java 2D graphics device (such as the screen or a
 * printer).
 *
 * @param g2 the graphics device.
 * @param area the area allocated for the title (subclasses should not
 *              draw outside this area).
 */
def draw (g2: Graphics2D, area: Rectangle2D): Unit

/**
 * Sets the position for the title and sends a  { @link TitleChangeEvent } to
 * all registered listeners.
 *
 * @param position the position (<code>null</code> not permitted).
 */
def setPosition (position: RectangleEdge): Unit = {
if (position == null) {
throw new IllegalArgumentException ("Null 'position' argument.")
}
if (this.position != position) {
this.position = position
notifyListeners (new TitleChangeEvent (this) )
}
}

/**
 * Returns a hashcode for the title.
 *
 * @return The hashcode.
 */
override def hashCode: Int = {
var result: Int = 193
result = 37 * result + ObjectUtilities.hashCode (this.position)
result = 37 * result + ObjectUtilities.hashCode (this.horizontalAlignment)
result = 37 * result + ObjectUtilities.hashCode (this.verticalAlignment)
return result
}

/**Storage for registered change listeners. */
@transient
private var listenerList: EventListenerList = null

/**
 * A flag that controls whether or not the title is visible.
 *
 * @since 1.0.11
 */
var visible: Boolean = false

/**
 * Provides serialization support.
 *
 * @param stream the output stream.
 *
 * @throws IOException if there is an I/O error.
 */
private def writeObject (stream: ObjectOutputStream): Unit = {
stream.defaultWriteObject
}
}

