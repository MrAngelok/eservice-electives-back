package edu.omsu.eservice.dao

import edu.omsu.eservice.connection.DbConnected
import edu.omsu.eservice.entities.{ChoiceData, Elective, ElectivesForPdf, StudentDataForElPdf}
import edu.omsu.eservice.services.ElectiveService
import scalikejdbc._

object OracleDao extends DbConnected with Dao {
  //core queries
  def findPersonIdByLogin(username: String): Option[Long] =
    insideReadOnly { implicit session =>
      sql"""
        select person_id
        from wv_users u
        where u.login=upper($username)
       """
        .map {
          rs => rs.long("person_id")
        }.single().apply()
    }

  def getElectives(personId: Long): List[Elective] = {
    insideReadOnly { implicit session =>
      sql"""
          select
            номер_группы,
            ссп_ид2,
            e.план_ид,
            e.спл_ид,
            e.индекс,
            e.дис_наименование,
            e.цд_порядок,
            e.ком_порядок,
            e.номер_семестра,
            e.контроль,
            e.цд_ид,
            e.ком_ид,
            e.номер_в_цикле,
            e.Номер_Дисциплины_По_Выбору,
            e.курс,
            e.весна,
            e.осень,
            s.spl_id,
            e.год_введения,
            s.cnt,
            (select count(ссп_ид2) from ип8_студенты_ид ст, и_планы пл
            where ст.план_ид=пл.ид and пл.план_ид=a.дуп_ид) total
          from ип8_студ_инфо a
          inner join wv_electives e on nvl(a.уск_дуп_ид, a.дуп_ид) = e.план_ид
          left outer join (select spl_id, count(spl_id) cnt from w_student_choice group by spl_id) s on e.спл_ид=s.spl_id
          where члвк_ид = $personId
          order by e.цд_порядок, e.ком_порядок, e.номер_в_цикле, e.Номер_Дисциплины_По_Выбору
    """.map {
        rs =>
          Elective(
            rs.string("номер_группы"),
            rs.long("ссп_ид2"),
            rs.long("спл_ид"),
            rs.string("индекс"),
            rs.string("дис_наименование"),
            rs.int("цд_порядок"),
            rs.int("ком_порядок"),
            rs.int("номер_семестра"),
            rs.string("контроль"),
            rs.long("цд_ид"),
            rs.long("ком_ид"),
            rs.string("номер_в_цикле"),
            rs.string("Номер_Дисциплины_По_Выбору"),
            rs.long("курс"),
            rs.long("весна"),
            rs.long("осень"),
            ElectiveService.boolToLong(rs.longOpt("spl_id").nonEmpty),
            rs.long("год_введения"),
            rs.longOpt("cnt"),
            rs.long("total")//36
          )
      }.list().apply()
    }
  }

  def getStudentChoiceMany(sspId2: Long): List[ChoiceData] = {
    insideReadOnly { implicit session =>
      sql"""
          select
          s.ssp_id2,
          s.spl_id,
          e.цд_ид,
          e.ком_ид,
          e.номер_в_цикле
          from w_student_choice s
          left outer join wv_electives e on s.spl_id=e.спл_ид
          where ssp_id2 = $sspId2
      """.map {
        rs =>
          ChoiceData(
            rs.long("ssp_id2"),
            rs.long("spl_id"),
            rs.long("цд_ид"),
            rs.long("ком_ид"),
            rs.string("номер_в_цикле"),
            ElectiveService.boolToLong(rs.longOpt("spl_id").nonEmpty)
          )
      }.list().apply()
    }
  }

  def getChoiceForCheck(personId: Long, cdId: Long, comId: Long, numberCycle: String): List[ChoiceData] = {
    insideReadOnly { implicit session =>
      sql"""
          select
            ссп_ид2,
            e.спл_ид,
            e.цд_ид,
            e.ком_ид,
            e.номер_в_цикле,
            spl_id
          from ип8_студ_инфо a
          inner join wv_electives e on nvl(a.уск_дуп_ид, a.дуп_ид) = e.план_ид
          left outer join w_student_choice on e.спл_ид=spl_id
          where (члвк_ид = $personId) and (e.цд_ид = $cdId) and (e.ком_ид = $comId) and (e.номер_в_цикле = $numberCycle)
      """.map {
        rs =>
          ChoiceData(
            rs.long("ссп_ид2"),
            rs.long("спл_ид"),
            rs.long("цд_ид"),
            rs.long("ком_ид"),
            rs.string("номер_в_цикле"),
            ElectiveService.boolToLong(rs.longOpt("spl_id").nonEmpty)
          )
      }.list().apply()
    }
  }

  def insertChoice(sspId2: Long, splId: Long, dateChoice: String): Long = {
    insideLocalTx { implicit session =>
      sql"""
            insert into w_student_choice(ssp_id2, spl_id, date_choice, when_moderation, who_moderation)
            values($sspId2, $splId, $dateChoice, $dateChoice,'student')
      """.update().apply()
    }
  }

  def updateChoice(sspId2: Long, splId_new: Long, splId_old: Long): Long = {
    insideLocalTx { implicit session =>
      sql"""
            update w_student_choice
            set spl_id = $splId_new
            where ssp_id2 = $sspId2 and spl_id = $splId_old
      """.update().apply()
    }
  }

  def getInfoForPdf(personId: Long): Option[StudentDataForElPdf] = {
    insideReadOnly { implicit session =>
      sql"""
           select
            фио,
            номер_группы,
            ссп_ид2
           from ип8_студ_инфо
           where члвк_ид = $personId
         """.map {
        rs =>
          StudentDataForElPdf(
            rs.string("фио"),
            rs.string("номер_группы"),
            rs.long("ссп_ид2")
          )
      }.single().apply()
    }
  }

  def getElectivesForPdf(sspId2: Long): List[ElectivesForPdf] = {
    insideReadOnly { implicit session =>
      sql"""
           select
            ДИС_НАИМЕНОВАНИЕ,
            номер_семестра,
            курс,
            год_введения,
            контроль
           from wv_electives
           where спл_ид in
           (select spl_id from w_student_choice where ssp_Id2 = $sspId2)
           order by номер_семестра
         """.map {
        rs =>
          ElectivesForPdf(
            rs.string("ДИС_НАИМЕНОВАНИЕ"),
            rs.long("номер_семестра"),
            rs.long("курс"),
            rs.long("год_введения"),
            rs.string("контроль")
          )
      }.list().apply()
    }
  }
}

//select 'ДОПЛ' as type, 'exedopl' as typedocl from dual union
//select 'ПОЧС' as type, 'exehour' as typedocl from dual;
