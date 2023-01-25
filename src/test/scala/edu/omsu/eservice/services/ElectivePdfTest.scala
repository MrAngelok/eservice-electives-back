package edu.omsu.eservice.services

import edu.omsu.eservice.entities.{ElectivesForPdf, StudentDataForElPdf}

import java.io.FileOutputStream
import edu.omsu.eservice.pdf.statementElectivesPdf
import edu.omsu.eservice.services.ElectiveService.groupingForPdf
import org.scalatest.FunSuite

class ElectivePdfTest extends FunSuite {
  val personId = 143418

  //For testing pdf file
  test("ElectivesPdf should render without errors") {
    val out = new FileOutputStream("target/Elective_pdf1.pdf")
    val parameters = StudentDataForElPdf(Fio = "Язовских Артём Дмитриевич", group = "МИБ-901-О-01", sspId2 = 609700)
    val el1 = ElectivesForPdf("Цифровая обработка сигналов", 6, 3, 2019, "Дифференциальный зачёт")
    val el2 = ElectivesForPdf("Интерфейсы периферийных устройств", 7, 4, 2019, "Дифференциальный зачёт")
    val el3 = ElectivesForPdf("Правовые основы противодействия легализации (отмыванию) доходов,полученных преступным путем, и финансированию терроризма", 7, 4, 2019, "Экзамен")
    val listParams = groupingForPdf(List(el1, el2, el3))
    statementElectivesPdf.doReport(personId, parameters, listParams, out)
  }


}
