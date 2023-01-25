package edu.omsu.eservice.services

import edu.omsu.eservice.entities._

import java.util.{Calendar, Date}


object ElectiveService {
  /**
   * Grouping electives for frontend
   * @param el - raw electives data from data base
   * @return StudentElectives - grouped electives
   */
  def grouping(el: List[ElectiveWithCanChoice]): StudentElectives =
    StudentElectives(groups = toGroupList(el))

  private def toGroupList(el: List[ElectiveWithCanChoice]): List[Group] = {
    el.groupBy(f => f.groupNumber)
      .map { case (key, value) => Group(groupNumber = key, courses = toCourseList(value)) }
      .toList
  }

  private def toCourseList(el: List[ElectiveWithCanChoice]): List[Course] = {
    el.groupBy(f => f.course)
      .map { case (key, value) => Course(course = key, semesters = toSemesterList(value)) }
      .toList
  }

  private def toSemesterList(el: List[ElectiveWithCanChoice]): List[Semester] = {
    el.groupBy(f => (f.semester, f.autumn, f.spring))
      .map { case (key, value) => Semester(semester = key._1, autumn = key._2, spring = key._3, disciplines = toDisciplineList(value)) }
      .toList
  }

  private def toDisciplineList(el: List[ElectiveWithCanChoice]): List[Discipline] = {
    el.groupBy(f => (f.cdOrder,f.comOrder,f.numberCycle,f.cdId,f.comId))
      .map { case (key, value) => Discipline(cdId = key._4, comId = key._5, cdOrder = key._1, comOrder = key._2, numberCycle = key._3, electives = toElectiveList(value)) }
      .toList
  }

  private def toElectiveList(el: List[ElectiveWithCanChoice]): List[ElectiveInfo] = {
    el.map { f =>
      ElectiveInfo(
        sspId2 = f.sspId2,
        splId = f.splId,
        index = f.index,
        name = f.name,
        control = f.control,
        numberDiscipline = f.numberDiscipline,
        choice = f.choice,
        canChoice = f.canChoice,
        countOfChoice = f.countOfChoice,
        totalStud = f.totalStud
      )
    }
  }

  def boolToLong(x: Boolean): Long = if (x) 1 else 0

  /**
   * Grouping electives for pdf
   * @param el - electives data needed for generating pdf
   * @return grouped electives for pdf
   */
  def groupingForPdf(el: List[ElectivesForPdf]): List[ElectivesForPdfBySem] = {
    el.groupBy(f => f.semester)
      .map { x =>
        ElectivesForPdfBySem(
          semester = x._1,
          disciplines = x._2)
      }.toList.sortBy(_.semester)
  }

  /**
   * Limit date to select electives
   * Changed manually
   * @return limit - Last date to select electives
   */
  def limitDate(): Date = {
    val format = new java.text.SimpleDateFormat("dd.MM.yyyy")
    val limit = format.parse("12.11.2023")
    limit
  }

  /**
   * Checking for the possibility of choosing electives
   * @param el - electives data
   * @return electives - data with a selection field
   */
  def canChoice(el: List[Elective]): List[ElectiveWithCanChoice] = {
    val cal = Calendar.getInstance()
    val month = cal.get(Calendar.MONTH)
    val yearCheck = cal.get(Calendar.YEAR)
    val checkMonth = 12 - limitDate().getMonth
    var checkSpring = 0
    if (checkMonth >= 6) checkSpring = 1

    val nowDate = Calendar.getInstance().getTime
    //      val format = new java.text.SimpleDateFormat("dd.MM.yyyy")
    //      val nowDate = format.parse("04.05.2022")
    var nowSem: Long = 0
    var chetSem = 0

    if (month > 5 && month < 12) {
      chetSem = 1
    }

    val electives: List[ElectiveWithCanChoice] = el.map { f => {
      nowSem = (yearCheck - f.year) * 2 + chetSem
      var canChoice = 1
      if (nowDate.compareTo(ElectiveService.limitDate()) > 0) {
        canChoice = 0
      } else if ((f.year + (f.semester / 2)) < yearCheck) {
        canChoice = 0
      } else if ((f.year + (f.semester / 2)) == yearCheck && f.semester <= 6) {
        canChoice = 0
      } else if(nowSem % 2 == chetSem){
        canChoice = 0
      }
      ElectiveWithCanChoice(
        groupNumber = f.groupNumber,
        sspId2 = f.sspId2,
        splId = f.splId,
        index = f.index,
        name = f.name,
        cdOrder = f.cdOrder,
        comOrder = f.comOrder,
        semester = f.semester,
        control = f.control,
        cdId = f.cdId,
        comId = f.comId,
        numberCycle = f.numberCycle,
        numberDiscipline = f.numberDiscipline,
        course = f.course,
        spring = f.spring,
        autumn = f.autumn,
        choice = f.choice,
        year = f.year,
        canChoice = canChoice,
        countOfChoice = f.countOfChoice,
        totalStud = f.totalStud
      )
    }
    }

    electives
  }
}