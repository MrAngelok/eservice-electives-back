package edu.omsu.eservice.pdf

import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.layout.Style
import com.itextpdf.layout.property.{ TextAlignment, VerticalAlignment}
import com.itextpdf.layout.border.Border
import edu.omsu.eservice.entities.{ElectivesForPdfBySem, StudentDataForElPdf}
import edu.omsu.eservice.pdf.Styles.{_class, _}

import java.io.OutputStream
import java.util.Calendar

object statementElectivesPdf {
  def doReport(personId: Long, studInfo: StudentDataForElPdf, electivesList: List[ElectivesForPdfBySem], outputStream: OutputStream): Unit = {
    val input = this.getClass.getResourceAsStream("/Times_New_Roman.ttf")
    val arr: Array[Byte] = Stream.continually(input.read).takeWhile(_ != -1).map(_.toByte).toArray
    val times = PdfFontFactory.createFont(arr, PdfEncodings.IDENTITY_H, true, true)

    val times8 = new Style().setFont(times).setFontSize(8).setMarginLeft(30f).setMarginRight(15f)
    val times11 = new Style().setFont(times).setFontSize(11).setMarginLeft(30f).setMarginRight(15f)
    val times11tab = new Style().setFont(times).setFontSize(11).setMarginLeft(28f).setMarginRight(15f)
    val times11bold = new Style().setFont(times).setFontSize(11).setBold().setMarginLeft(30f).setMarginRight(15f)
    val times12bold = new Style().setFont(times).setFontSize(12).setBold().setMarginLeft(30f).setMarginRight(15f)
    val times11list = new Style().setFont(times).setFontSize(11).setMarginLeft(50f).setMarginRight(15f)

    implicit val styling: StyleDef = StyleDef(
      defaultFont = times,
      defaultStyle = times11,
      defaultLeading = 12.5f,
      style = Map(
        _class("bold11") ~> Map(_style ~> times11bold),
        _class("bold12") ~> Map(_style ~> times12bold),
        _class("right") ~> Map(_align ~> TextAlignment.RIGHT),
        _class("center") ~> Map(_align ~> TextAlignment.CENTER),
        _class("middle") ~> Map(_valign ~> VerticalAlignment.MIDDLE),
        _class("table") ~> Map(_margin_ratio ~> 10),
        _class("vert") ~> Map(_rotate ~> Math.toRadians(90)),
        _class("underline-bold") ~> Map(_style ~> times11bold),
        _class("normal") ~> Map(_style ~> times8),
        _class("fixed-td") ~> Map(_height ~> 15f),
        _class("leading") ~> Map(_leading ~> 1.5f),
        _class("noborder1") ~> Map(_border ~> Border.NO_BORDER, _style ~> times11tab),
        _class("numList") ~> Map(_style ~> times11list)
      )
    )


    val cal = Calendar.getInstance()
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val month = cal.get(Calendar.MONTH)
    val year = cal.get(Calendar.YEAR)
    val monthNames = Array("Января", "Февраля", "марта", "Апреля", "Мая", "Июня",
    "Июля", "Августа", "Сентября", "октября", "Ноября", "Декабря")

    ItextDSL.writeDoc(outputStream, DocConf(pageSize = PageSize.A4), { implicit self =>
      P("Заявление на изучение элективных дисциплин (модулей)", _class = "bold12 center")
      P(" ")

      Table(cols = Seq(310, 200), _class = "table", init = { implicit self =>
        Tr( row1=> {
          Td("Я     " + studInfo.Fio, _class = "noborder1")(styling, row1)
          Td("группа " + studInfo.group,_class = "noborder1")(styling, row1)
        })
      })

      P("выбираю для изучения следующие элективные дисциплины (модули):")
      P(" ")

      var num = 1
      var course: Long = 1
      var yearBegin: Long = 0
      var studyYear: Long = 0

      electivesList.foreach(f=>{
        course = f.disciplines.map(_.course).head
        yearBegin = f.disciplines.map(_.yearBegin).head
        studyYear = yearBegin + course
        P(f.semester + " cеместр " + (studyYear-1) + "/" + studyYear + " учебный год", _class = "bold11")
        f.disciplines.foreach(x=>{
          P(num+". " + x.name + ".\n Форма контроля: " + x.control, _class="numList")
          num+=1
        })
        num = 1
      })

      P(" ")
      P("Я предупрежден(на), что выбранная элективная дисциплина (модуль) становится обязательной " +
        "для изучения. В случае, если выбранная дисциплина не будет реализовываться, то обязуюсь выбрать " +
        "реализуемую альтернативу, либо предоставить выбор деканату.", init={self => self.p.setFirstLineIndent(30f)})
      P(" ")
      // \u00A0 - No-Break Space
      P("С Положением о выборе и реализации элективных и факультативных дисциплин (модулей) по основным " +
        "образовательным программам в «Омском государственном университете им.\u00A0Ф.М.\u00A0Достоевского» ознакомлен(на).",
        init={self => self.p.setFirstLineIndent(30f)})
      P(" ")
      P(" ")
      P(" ")
      P(" ")

      Table(cols = Seq(200, 300), _class = "table", init = { implicit self =>
        Tr( row1=> {
          Td("« " + day + " » « " + monthNames(month) + " » « " + year + " »", _class = "noborder1")(styling, row1)
          Td(studInfo.Fio + " _______________",_class = "noborder1")(styling, row1)
        })
      })

    })
  }
}
