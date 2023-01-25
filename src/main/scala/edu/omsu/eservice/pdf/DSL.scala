package edu.omsu.eservice.pdf

import java.io.{IOException, OutputStream}

import com.itextpdf.io.font.FontConstants
import com.itextpdf.kernel.events.{Event, IEventHandler, PdfDocumentEvent}
import com.itextpdf.kernel.font.{PdfFont, PdfFontFactory}
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.{PdfDocument, PdfWriter}
import com.itextpdf.layout.element.{Cell, Paragraph, Text, Table => ItextTable}
import com.itextpdf.layout.property.VerticalAlignment
import com.itextpdf.layout.{Document => ItextDocument}
import edu.omsu.eservice.pdf.Styles._


trait ItextNode[T] {
  def itextEl: T
}

trait TableContainer {
  def addTable(table: ItextTable)
}

trait SpanContainer {
  def addSpan(span: Text)
}

trait PContainer extends SpanContainer {
  def addP(p: Paragraph)
}

case class Position(x: Int, y: Int)

case class WaterMark(text: String, font: PdfFont, size: Int = 80, position: Position = Position(400, 300))

case class DocConf(val marginTop: Float = 36f,
                   val marginBottom: Float = 18f,
                   val marginLeft: Float = 36f,
                   val marginRight: Float = 36f,
                   val pageSize: PageSize = PageSize.A4,
                   val handlers: Map[String, IEventHandler] = Map())


object ItextDSL {

  def writeDoc(out: OutputStream, conf: DocConf, dsl: Document => Unit): Unit = {

    val writer = new PdfWriter(out)
    val pdfDoc = new PdfDocument(writer)
    val itextDoc: ItextDocument = new ItextDocument(pdfDoc, conf.pageSize)

    itextDoc.setMargins(conf.marginTop, conf.marginRight, conf.marginBottom, conf.marginLeft)
    pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new IEventHandler {
      override def handleEvent(event: Event): Unit = {
        val docEvent = event.asInstanceOf[PdfDocumentEvent]
        val page = docEvent.getPage
        var font: PdfFont = null
        try
          font = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD)
        catch {
          case e: IOException =>
            e.printStackTrace()
        }
      }
    })

    dsl(Document(itextDoc))

    writer.flush()
    pdfDoc.close()

  }
}

case class Document(doc: ItextDocument) extends ItextNode[ItextDocument] with TableContainer with SpanContainer with PContainer {
  override def itextEl: ItextDocument = doc

  override def addTable(table: ItextTable): Unit = doc.add(table)

  override def addSpan(span: Text): Unit = doc.add(new Paragraph().add(span))

  override def addP(p: Paragraph): Unit = doc.add(p)
}

case class Span(text: String, _class: String = "")(implicit val style: StyleDef, implicit val parent: SpanContainer) {
  val p = new Text(text).setFont(style.defaultFont).addStyle(style.defaultStyle)
  query[LStyle](style, _class, _style).map { style => p.addStyle(style.v) }
  parent.addSpan(p)
}

case class P(text: String = "",
             _class: String = "",
             init: P => Unit = { self => })
            (implicit val style: StyleDef, implicit val parent: PContainer) extends ItextNode[Paragraph] with SpanContainer with PContainer {

  val p = new Paragraph(text).setFont(style.defaultFont).setFixedLeading(style.defaultLeading).addStyle(style.defaultStyle)
  query[LStyle](style, _class, _style).map { style => p.addStyle(style.v) }
  query[Align](style, _class, _align).map { align => p.setTextAlignment(align.v) }
  query[Leading](style, _class, _leading).map { leading => p.setFixedLeading(leading.v) }

  override def itextEl: Paragraph = p

  override def addSpan(span: Text): Unit = p.add(span)

  override def addP(p: Paragraph): Unit = this.p.add(p)

  init(this)
  parent.addP(p)
}

case class Table(cols: Seq[Float],
                 absolute: Boolean = false,
                 _class: String = "",
                 init: Table => Unit)
                (implicit val style: StyleDef,
                 implicit val parent: TableContainer) extends ItextNode[ItextTable] {

  sealed trait WidthDef {
    val value: Int
  }

  case class PercentWidth(val value: Int) extends WidthDef

  case class AbsWidth(val value: Int) extends WidthDef

  private var widthDef: Option[WidthDef] = None

  def width(absolute: Boolean = false, value: Int) = widthDef = absolute match {
    case true => Some(AbsWidth(value))
    case false => Some(PercentWidth(value))
  }

  lazy val table = {
    val table =
      if (!absolute) new ItextTable(cols.toArray)
      else {
        val table = new ItextTable(cols.length)
        table
      }

    widthDef match {
      case Some(AbsWidth(value)) => table.setWidth(value)
      case Some(PercentWidth(value)) => table.setWidthPercent(value)
      case _ => table.setWidthPercent(100);
    }
    query[MarginRatio](style, _class, _margin_ratio).map { ratio =>
      table.setSpacingRatio(ratio.v)
    }

    table
  }

  override def itextEl: ItextTable = table

  init(this)

  parent.addTable(table)
}

case class Tr(init: Tr => Unit)(implicit val parent: Table) extends ItextNode[ItextTable] {
  override def itextEl: ItextTable = parent.itextEl

  init(this)
}

case class Td(text: String = "",
              implicit val _class: String = "",
              colspan: Int = 1, rowspan: Int = 1,
              init: Td => Unit = { self => })
             (implicit val style: StyleDef, implicit val parent: Tr) extends ItextNode[Cell] with TableContainer with SpanContainer with PContainer {

  val cell = new Cell(rowspan, colspan).addStyle(style.defaultStyle)

  //  cell.setPadding(0)
  val valign: VerticalAlignment = query[VertAlign](style, _class, _valign).map(_.v).getOrElse(VerticalAlignment.MIDDLE)
  cell.setVerticalAlignment(valign)

  query[Height](style, _class, _height).map { height =>
    cell.setHeight(height.v)
  }

  query[Size](style, _class, _border).map { borderSize =>
    cell.setBorder(borderSize.v)
  }

  val hAlign = query[Align](style, _class, _align)

  if (text != "") {
    val font = query[LStyle](style, _class, _style).map(_.v).getOrElse(style.defaultFont)
    val p = new Text(text).setFont(style.defaultFont)
    query[LStyle](style, _class, _style).map { style => p.addStyle(style.v) }

    val par = new Paragraph(p)
    query[Rotate](style, _class, _rotate).map { rot => par.setRotationAngle(rot.v) }

    hAlign.map { align =>
      par.setTextAlignment(align.v)
    }
    cell.add(par)
  } else
    hAlign.map { align =>
      cell.setTextAlignment(align.v)
    }


  override def itextEl: Cell = cell

  override def addTable(table: ItextTable): Unit = cell.add(table)

  override def addSpan(span: Text): Unit = cell.add(new Paragraph().add(span))

  override def addP(p: Paragraph): Unit = cell.add(p)

  init(this)

  parent.itextEl.addCell(cell)
}