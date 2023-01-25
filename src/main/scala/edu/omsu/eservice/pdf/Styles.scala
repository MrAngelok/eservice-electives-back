package edu.omsu.eservice.pdf

import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.layout.Style
import com.itextpdf.layout.border.Border
import com.itextpdf.layout.property.{TextAlignment, VerticalAlignment}


object Styles {
  def withClass(_class: String)(f: String => Unit) = f(_class)

  sealed trait StyleValue

  case class LStyle(v: Style) extends StyleValue

  case class Align(v: TextAlignment) extends StyleValue

  case class VertAlign(v: VerticalAlignment) extends StyleValue

  case class MarginRatio(v: Int) extends StyleValue

  case class Rotate(v: Double) extends StyleValue

  case class Width(v: Int) extends StyleValue

  case class Height(v: Float) extends StyleValue

  case class Size(v: Border) extends StyleValue

  case class Leading(v: Float) extends StyleValue

  implicit def itextFontToFont(style: Style) = LStyle(style)

  implicit def itextAlignToAlign(align: TextAlignment) = Align(align)

  implicit def itextAlignToVAlign(align: VerticalAlignment) = VertAlign(align)

  implicit def intToMarginRatio(v: Int) = MarginRatio(v)

  implicit def intToRotate(v: Double) = Rotate(v)

  implicit def intToWidth(v: Int) = Width(v)

  implicit def intToHeight(v: Float) = Height(v)

  implicit def intToSize(v: Border) = Size(v)

  implicit def intToLeading(v: Float) = Leading(v)

  sealed trait StyleLiteral

  case object _style extends StyleLiteral {
    def ~>(v: LStyle) = (this, v)
  }

  case object _align extends StyleLiteral {
    def ~>(v: Align) = (this, v)
  }

  case object _valign extends StyleLiteral {
    def ~>(v: VertAlign) = (this, v)
  }

  case object _margin_ratio extends StyleLiteral {
    def ~>(v: MarginRatio) = (this, v)
  }

  case object _rotate extends StyleLiteral {
    def ~>(v: Rotate) = (this, v)
  }

  case object _width extends StyleLiteral {
    def ~>(v: Width) = (this, v)
  }

  case object _height extends StyleLiteral {
    def ~>(v: Height) = (this, v)
  }

  case object _border extends StyleLiteral {
    def ~>(v: Size) = (this, v)
  }

  case object _leading extends StyleLiteral {
    def ~>(v: Leading) = (this, v)
  }


  case class _class(name: String) {
    def ~>(v: Map[StyleLiteral, StyleValue]) = (this, v)
  }


  case class StyleDef(defaultFont: PdfFont, defaultStyle: Style, defaultLeading: Float, style: Map[_class, Map[StyleLiteral, StyleValue]])

  def query[T <: StyleValue](stdef: StyleDef, classes: String, literal: StyleLiteral): Option[T] = {
    val res = classes.split(" ").map { className => // iterate over classes
      stdef.style.get(_class(className)).flatMap { x => x.get(literal) } //find prop in class
    }.filter(_.isDefined).map(_.get) //remove not found entries
      .headOption
    if (res.isDefined) Option(res.get.asInstanceOf[T])
    else None
  }
}
