package edu.omsu.eservice.dao

import edu.omsu.eservice.entities.{ChoiceData, Elective, ElectivesForPdf, StudentDataForElPdf}


trait Dao {
  def findPersonIdByLogin(username: String): Option[Long]

  def getElectives(personId: Long): List[Elective]

  def insertChoice(sspId2: Long, splId: Long, dateChoice: String): Long

  def getStudentChoiceMany(sspId2: Long): List[ChoiceData]

  def getChoiceForCheck(personId: Long, cdId: Long, comId: Long, numberCycle: String): List[ChoiceData]

  def updateChoice(sspId2: Long, splId_new: Long, splId_old: Long): Long

  def getInfoForPdf(personId: Long): Option[StudentDataForElPdf]

  def getElectivesForPdf(sspId2: Long): List[ElectivesForPdf]
}
