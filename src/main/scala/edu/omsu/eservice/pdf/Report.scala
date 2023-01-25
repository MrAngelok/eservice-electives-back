package edu.omsu.eservice.pdf

import java.io.OutputStream

import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.color.Color
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.layout.Style
import com.itextpdf.layout.property.{TextAlignment, VerticalAlignment}
import edu.omsu.eservice.pdf.Styles.{_class, _}

object Report {
  def doReport(data: String, outputStream: OutputStream) {
    val input = this.getClass.getResourceAsStream("/Times_New_Roman.ttf")
    val arr: Array[Byte] = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte).toArray
    val times = PdfFontFactory.createFont(arr, PdfEncodings.IDENTITY_H, true, true)

    val times8 = new Style().setFont(times).setFontSize(8)
    val times11 = new Style().setFont(times).setFontSize(11)
    val times11bold = new Style().setFont(times).setFontSize(11).setBold()
    val times11boldRed = new Style().setFont(times).setFontSize(11).setBold().setFontColor(Color.RED)
    val times11underline = new Style().setFont(times).setFontSize(11).setUnderline()

    implicit val styling = StyleDef(
      defaultFont = times,
      defaultStyle = times11,
      defaultLeading = 9.5f,
      style = Map(
        _class("bold11") ~> Map(_style ~> times11bold),
        _class("right") ~> Map(_align ~> TextAlignment.RIGHT),
        _class("center") ~> Map(_align ~> TextAlignment.CENTER),
        _class("middle") ~> Map(_valign ~> VerticalAlignment.MIDDLE),
        _class("table") ~> Map(_margin_ratio ~> 10),
        _class("vert") ~> Map(_rotate ~> Math.toRadians(90)),
        _class("underline") ~> Map(_style ~> times11underline),
        _class("underline-bold") ~> Map(_style ~> times11bold),
        _class("normal") ~> Map(_style ~> times8),
        _class("underline-bold-red") ~> Map(_style ~> times11boldRed),
        _class("fixed-td") ~> Map(_height ~> 15f),
        _class("leading") ~> Map(_leading ~> 10.5f)
      )
    )

    ItextDSL.writeDoc(outputStream, DocConf(pageSize = PageSize.A4.rotate()), { implicit self =>
      P("Текст: " + data, _class = "bold center underline-bold")
    })
  }

}