package edu.omsu.eservice.services

import edu.omsu.eservice.entities.{ElectiveWithCanChoice, StudentElectives}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, Outcome, fixture}

class ElectiveServiceSpec extends fixture.FlatSpec with ScalaFutures with Matchers {
  override protected def withFixture(test: OneArgTest): Outcome = {
    val electives: List[ElectiveWithCanChoice] = List(
      ElectiveWithCanChoice(groupNumber = "ТАМ-031-З-01", sspId2 = 6940299L, splId = 687066L, index = "БЛОК1.В.01/1", name = "Адаптивное физическое воспитание детей дошкольного возраста", cdOrder = 1, comOrder = 3, semester = 4, control = "Зачет", cdId = 58, comId = 3, numberCycle = "01", numberDiscipline = "1", course = 2, spring = 1, autumn = 0, choice = 1, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ТАМ-031-З-01", sspId2 = 6940299L, splId = 687067L, index = "БЛОК1.В.01/2", name = "Адаптивное физическое воспитание в дошкольных образовательных организациях", cdOrder = 1, comOrder = 3, semester = 4, control = "Зачет", cdId = 58, comId = 3, numberCycle = "01", numberDiscipline = "2", course = 2, spring = 1, autumn = 0, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ТАМ-031-З-01", sspId2 = 6940299L, splId = 687068L, index = "БЛОК1.В.02/1", name = "Адаптивное физическое воспитание лиц с нарушением интеллектуального развития", cdOrder = 1, comOrder = 3, semester = 4, control = "Зачет", cdId = 58, comId = 3, numberCycle = "02", numberDiscipline = "1", course = 2, spring = 1, autumn = 0, choice = 0, year = 2020, canChoice = 0, countOfChoice = null, 36),
      ElectiveWithCanChoice(groupNumber = "ТАМ-031-З-01", sspId2 = 6940299L, splId = 687056L, index = "БЛОК1.В.02/1", name = "Адаптивное физическое воспитание лиц с сенсорными нарушениями", cdOrder = 1, comOrder = 3, semester = 4, control = "Зачет", cdId = 58, comId = 3, numberCycle = "02", numberDiscipline = "2", course = 2, spring = 1, autumn = 0, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683385L, index = "М2.В.01/1", name = "Административно-процессуальное регулирование общественных отношений", cdOrder = 2, comOrder = 3, semester = 3, control = "Зачет", cdId = 39, comId = 3, numberCycle = "01", numberDiscipline = "1", course = 2, spring = 0, autumn = 1, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683386L, index = "М2.В.01/2", name = "Теоретические проблемы государственного управления экономическими социальными процессами в РФ", cdOrder = 2, comOrder = 3, semester = 3, control = "Зачет", cdId = 39, comId = 3, numberCycle = "01", numberDiscipline = "2", course = 2, spring = 0, autumn = 1, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683387L, index = "М2.В.02/1", name = "Программно-целевой метод управления", cdOrder = 2, comOrder = 3, semester = 3, control = "Зачет", cdId = 39, comId = 3, numberCycle = "02", numberDiscipline = "1", course = 2, spring = 0, autumn = 1, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683405L, index = "М2.В.02/2", name = "Налоговое право Европейского Союза", cdOrder = 2, comOrder = 3, semester = 3, control = "Зачет", cdId = 39, comId = 3, numberCycle = "02", numberDiscipline = "2", course = 2, spring = 0, autumn = 1, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683406L, index = "М2.В.03/1", name = "Налоговые споры", cdOrder = 2, comOrder = 3, semester = 3, control = "Зачет", cdId = 39, comId = 3, numberCycle = "03", numberDiscipline = "1", course = 2, spring = 0, autumn = 1, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683407L, index = "М2.В.03/2", name = "Правовое обеспечение налоговой безопасности государства и бизнеса", cdOrder = 2, comOrder = 3, semester = 3, control = "Зачет", cdId = 39, comId = 3, numberCycle = "03", numberDiscipline = "2", course = 2, spring = 0, autumn = 1, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683379L, index = "М2.В.04/1", name = "Модели местного управления и самоуправления (отечественный и мировой опыт)", cdOrder = 2, comOrder = 3, semester = 4, control = "Зачет", cdId = 39, comId = 3, numberCycle = "04", numberDiscipline = "1", course = 2, spring = 1, autumn = 0, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683380L, index = "М2.В.04/2", name = "Противодействие коррупции", cdOrder = 2, comOrder = 3, semester = 4, control = "Зачет", cdId = 39, comId = 3, numberCycle = "04", numberDiscipline = "2", course = 2, spring = 1, autumn = 0, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683381L, index = "М2.В.05/1", name = "Бюджетный процесс", cdOrder = 2, comOrder = 3, semester = 4, control = "Зачет", cdId = 39, comId = 3, numberCycle = "05", numberDiscipline = "1", course = 2, spring = 1, autumn = 0, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683382L, index = "М2.В.05/2", name = "Правовые основы противодействия легализации (отмыванию) доходов,полученных преступным путем, и финансированию терроризма", cdOrder = 2, comOrder = 3, semester = 4, control = "Зачет", cdId = 39, comId = 3, numberCycle = "05", numberDiscipline = "2", course = 2, spring = 1, autumn = 0, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683394L, index = "М2.В.06/1", name = "Административные реформы", cdOrder = 2, comOrder = 3, semester = 4, control = "Зачет", cdId = 39, comId = 3, numberCycle = "06", numberDiscipline = "1", course = 2, spring = 1, autumn = 0, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683395L, index = "М2.В.06/2", name = "Координация в системе государственного и муниципального управления", cdOrder = 2, comOrder = 3, semester = 4, control = "Зачет", cdId = 39, comId = 3, numberCycle = "06", numberDiscipline = "2", course = 2, spring = 1, autumn = 0, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683396L, index = "М2.В.07/1", name = "Научная организация управленческого труда", cdOrder = 2, comOrder = 3, semester = 1, control = "Зачет", cdId = 39, comId = 3, numberCycle = "07", numberDiscipline = "1", course = 1, spring = 0, autumn = 1, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36),
      ElectiveWithCanChoice(groupNumber = "ЮЮМ-001-О-12", sspId2 = 7042040L, splId = 683397L, index = "М2.В.07/2", name = "Правовые основы социального управления", cdOrder = 2, comOrder = 3, semester = 1, control = "Зачет", cdId = 39, comId = 3, numberCycle = "07", numberDiscipline = "2", course = 1, spring = 0, autumn = 1, choice = 0, year = 2020, canChoice = 0, countOfChoice = Option(1), 36)
    )

    val studentElectives = ElectiveService.grouping(electives)
    test(studentElectives)
  }

  override type FixtureParam = StudentElectives

  it should "has proper grouping" in { studentElectives =>

    studentElectives.groups.size shouldBe 2

    val group1 = studentElectives.groups.head
    group1.groupNumber shouldBe "ЮЮМ-001-О-12"
    group1.courses.size shouldBe 2

    group1.courses.head.course shouldBe 2
    group1.courses.head.semesters.size shouldBe 2

    group1.courses.head.semesters.head.semester shouldBe 4
    group1.courses.head.semesters.head.spring shouldBe 1
    group1.courses.head.semesters.head.autumn shouldBe 0
    group1.courses.head.semesters.head.disciplines.size shouldBe 3

    group1.courses.head.semesters.head.disciplines(0).numberCycle shouldBe "06"
    group1.courses.head.semesters.head.disciplines(0).electives.size shouldBe 2
    group1.courses.head.semesters.head.disciplines(0).electives.head.name shouldBe "Административные реформы"
    group1.courses.head.semesters.head.disciplines(0).electives(1).name shouldBe "Координация в системе государственного и муниципального управления"
    group1.courses.head.semesters.head.disciplines(1).numberCycle shouldBe "05"
    group1.courses.head.semesters.head.disciplines(1).electives.size shouldBe 2
    group1.courses.head.semesters.head.disciplines(1).electives.head.name shouldBe "Бюджетный процесс"
    group1.courses.head.semesters.head.disciplines(1).electives(1).name shouldBe "Правовые основы противодействия легализации (отмыванию) доходов,полученных преступным путем, и финансированию терроризма"
    group1.courses.head.semesters.head.disciplines(2).numberCycle shouldBe "04"
    group1.courses.head.semesters.head.disciplines(2).electives.size shouldBe 2
    group1.courses.head.semesters.head.disciplines(2).electives.head.name shouldBe "Модели местного управления и самоуправления (отечественный и мировой опыт)"
    group1.courses.head.semesters.head.disciplines(2).electives(1).name shouldBe "Противодействие коррупции"

    group1.courses.head.semesters(1).semester shouldBe 3
    group1.courses.head.semesters(1).spring shouldBe 0
    group1.courses.head.semesters(1).autumn shouldBe 1
    group1.courses.head.semesters(1).disciplines.size shouldBe 3

    group1.courses.head.semesters(1).disciplines.head.numberCycle shouldBe "03"
    group1.courses.head.semesters(1).disciplines.head.electives.size shouldBe 2
    group1.courses.head.semesters(1).disciplines.head.electives.head.name shouldBe "Налоговые споры"
    group1.courses.head.semesters(1).disciplines.head.electives(1).name shouldBe "Правовое обеспечение налоговой безопасности государства и бизнеса"
    group1.courses.head.semesters(1).disciplines(1).numberCycle shouldBe "02"
    group1.courses.head.semesters(1).disciplines(1).electives.size shouldBe 2
    group1.courses.head.semesters(1).disciplines(1).electives.head.name shouldBe "Программно-целевой метод управления"
    group1.courses.head.semesters(1).disciplines(1).electives(1).name shouldBe "Налоговое право Европейского Союза"
    group1.courses.head.semesters(1).disciplines(2).numberCycle shouldBe "01"
    group1.courses.head.semesters(1).disciplines(2).electives.size shouldBe 2
    group1.courses.head.semesters(1).disciplines(2).electives.head.name shouldBe "Административно-процессуальное регулирование общественных отношений"
    group1.courses.head.semesters(1).disciplines(2).electives(1).name shouldBe "Теоретические проблемы государственного управления экономическими социальными процессами в РФ"

    group1.courses(1).course shouldBe 1
    group1.courses(1).semesters.size shouldBe 1

    group1.courses(1).semesters.head.semester shouldBe 1
    group1.courses(1).semesters.head.spring shouldBe 0
    group1.courses(1).semesters.head.autumn shouldBe 1
    group1.courses(1).semesters.head.disciplines.size shouldBe 1

    group1.courses(1).semesters.head.disciplines.head.numberCycle shouldBe "07"
    group1.courses(1).semesters.head.disciplines.head.electives.size shouldBe 2

    group1.courses(1).semesters.head.disciplines.head.electives.head.name shouldBe "Научная организация управленческого труда"
    group1.courses(1).semesters.head.disciplines.head.electives(1).name shouldBe "Правовые основы социального управления"

    val group2 = studentElectives.groups(1)
    group2.groupNumber shouldBe "ТАМ-031-З-01"
    group2.courses.size shouldBe 1

    group2.courses.head.course shouldBe 2
    group2.courses.head.semesters.size shouldBe 1

    group2.courses.head.semesters.head.semester shouldBe 4
    group2.courses.head.semesters.head.spring shouldBe 1
    group2.courses.head.semesters.head.autumn shouldBe 0
    group2.courses.head.semesters.head.disciplines.size shouldBe 2
    group2.courses.head.semesters.head.disciplines(0).numberCycle shouldBe "01"
    group2.courses.head.semesters.head.disciplines(0).electives.size shouldBe 2
    group2.courses.head.semesters.head.disciplines(0).electives.head.name shouldBe "Адаптивное физическое воспитание детей дошкольного возраста"
    group2.courses.head.semesters.head.disciplines(0).electives(1).name shouldBe "Адаптивное физическое воспитание в дошкольных образовательных организациях"
    group2.courses.head.semesters.head.disciplines(1).numberCycle shouldBe "02"
    group2.courses.head.semesters.head.disciplines(1).electives.size shouldBe 2
    group2.courses.head.semesters.head.disciplines(1).electives.head.name shouldBe "Адаптивное физическое воспитание лиц с нарушением интеллектуального развития"
    group2.courses.head.semesters.head.disciplines(1).electives(1).name shouldBe "Адаптивное физическое воспитание лиц с сенсорными нарушениями"

  }
}
