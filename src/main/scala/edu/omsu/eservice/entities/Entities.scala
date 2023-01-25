package edu.omsu.eservice.entities

case class SearchParams(
                         studentName: Option[String],
                         numberGroup: Option[String]
                       )

case class StudentElectives(
                             groups: List[Group]
                           )

case class Group(
                  groupNumber: String,
                  courses: List[Course]
                )

case class Course(
                   course: Long,
                   semesters: List[Semester]
                 )

case class Semester(
                     semester: Long,
                     autumn: Long,
                     spring: Long,
                     disciplines: List[Discipline]
                   )

case class Discipline(
                       cdId: Long,
                       comId: Long,
                       cdOrder: Int,
                       comOrder: Int,
                       numberCycle: String,
                       electives: List[ElectiveInfo]
                     )

case class ElectiveInfo(
                         sspId2: Long,
                         splId: Long,
                         index: String,
                         name: String,
                         control: String,
                         numberDiscipline: String,
                         choice: Long,
                         canChoice: Long,
                         countOfChoice: Option[Long],
                         totalStud: Long
                       )

case class Elective(
                     groupNumber: String,
                     sspId2: Long,
                     splId: Long,
                     index: String,
                     name: String,
                     cdOrder: Int,
                     comOrder: Int,
                     semester: Long,
                     control: String,
                     cdId: Long,
                     comId: Long,
                     numberCycle: String,
                     numberDiscipline: String,
                     course: Long,
                     spring: Long,
                     autumn: Long,
                     choice: Long,
                     year: Long,
                     countOfChoice: Option[Long],
                     totalStud: Long
                   )

case class ElectiveWithCanChoice(
                                  groupNumber: String,
                                  sspId2: Long,
                                  splId: Long,
                                  index: String,
                                  name: String,
                                  cdOrder: Int,
                                  comOrder: Int,
                                  semester: Long,
                                  control: String,
                                  cdId: Long,
                                  comId: Long,
                                  numberCycle: String,
                                  numberDiscipline: String,
                                  course: Long,
                                  spring: Long,
                                  autumn: Long,
                                  choice: Long,
                                  year: Long,
                                  canChoice: Long,
                                  countOfChoice: Option[Long],
                                  totalStud: Long
                                )

case class ChoiceData(
                       sspId2: Long,
                       splId: Long,
                       cdId: Long,
                       comId: Long,
                       numberCycle: String,
                       choice: Long
                     )

case class StudentDataForElPdf(
                                Fio: String,
                                group: String,
                                sspId2: Long
                              )

case class ElectivesForPdf(
                            name: String,
                            semester: Long,
                            course: Long,
                            yearBegin: Long,
                            control: String
                          )

case class ElectivesForPdfBySem(
                                 semester: Long,
                                 disciplines: List[ElectivesForPdf]
                               )
