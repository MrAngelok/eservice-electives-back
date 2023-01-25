package edu.omsu.eservice.controllers

import com.itextpdf.io.source.ByteArrayOutputStream
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import edu.omsu.eservice.conf.Auth.Profile
import edu.omsu.eservice.conf.{Api, Auth, ResponseEnvelope}
import edu.omsu.eservice.dao.OracleDao
import edu.omsu.eservice.entities.{ChoiceData, StudentElectives}
import edu.omsu.eservice.pdf.statementElectivesPdf
import edu.omsu.eservice.services.ElectiveService
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Formats}
import org.pac4j.core.profile.ProfileManager
import org.pac4j.sparkjava.SparkWebContext
import org.slf4j.LoggerFactory
import scalikejdbc.{ConnectionPool, DataSourceConnectionPool}
import spark._

import java.text.SimpleDateFormat
import java.util.Calendar
import scala.util.Try

object AppController {
  implicit val formats: Formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("dd.MM.yyyy")
  }

  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger(AppController.getClass)
    val config = ConfigFactory.load()

    val hc = new HikariConfig()
    hc.setDataSourceClassName(config.getString("db.dbConnected.driver"))
    hc.addDataSourceProperty("url", config.getString("db.dbConnected.url"))
    hc.setUsername(config.getString("db.dbConnected.user"))
    hc.setPassword(config.getString("db.dbConnected.password"))
    hc.setMinimumIdle(config.getInt("db.dbConnected.poolInitialSize"))
    hc.setMaximumPoolSize(config.getInt("db.dbConnected.poolMaxSize"))
    hc.setConnectionTimeout(config.getInt("db.dbConnected.connectionTimeoutMillis"))
    hc.setConnectionTestQuery(config.getString("db.dbConnected.poolValidationQuery"))

    val ds = new HikariDataSource(hc)
    ConnectionPool.add('dbConnected, new DataSourceConnectionPool(ds))
    //DBs.setup('dbConnected)

    // Das client
    val client = Auth.makeDasClient(config, OracleDao.findPersonIdByLogin)
    val api = new Api(config, client)

    Spark.exception(classOf[AppException], new ExceptionHandler[AppException] {
      override def handle(exception: AppException, request: Request, response: Response): Unit = {
        val context = new SparkWebContext(request, response)
        val manager = new ProfileManager[Profile](context)
        if (!manager.isAuthenticated) {
          logger.error("[" + request.requestMethod() + " " + request.uri() + "] " + exception.getMessage)
        } else {
          val profile: Profile = manager.get(true).get()
          profile.personId match {
            case Some(personId) => logger.error(profile.username + "(" + personId + ") [" + request.requestMethod() + " " + request.uri() + "] " + exception.getMessage)
            case None => logger.error(profile.username + " [" + request.requestMethod() + " " + request.uri() + "] " + exception.getMessage)
          }
        }
        response.header("Content-type", "application/json; charset=\"UTF-8\"")
        response.status(200)
        val envelope: ResponseEnvelope = ResponseEnvelope(success = false, exception.getMessage, exception.data)
        response.body(api.jsonTransformer.render(envelope))
      }
    })

    logger.info("Application initialized")

    /**
     * Get electives for person
     * @return electives - Grouped electives data
     */
    //Get electives from data base for person
    def getElectives(login: String, personId: Long, req: Request, res: Response): StudentElectives = {
      val el = OracleDao.getElectives(personId)

      if (el.isEmpty) {
        throw new AppException("Нет дисциплин по выбору")
      }

      //проверка на модульность
      if (el.exists(_.index.contains(".М."))) {
        throw new AppException("Выбор модулей в разработке")
      }

      //группировка
      val electives = ElectiveService.grouping(ElectiveService.canChoice(el))

      electives
    }

    /**
     * Insert or update choice of electives
     */
    def postChoice(login: String, personId: Long, req: Request, res: Response): AnyRef = {
      val choiceBody = Try(parse(req.body()).extract[ChoiceData]).toOption.getOrElse {
        throw new AppException("Неверно переданы данные")
      }

      if (choiceBody.choice == 1) {
        throw new AppException("Отправлен уже выбранный пункт")
      }

      val timeOfChoice = formats.dateFormat.format(Calendar.getInstance().getTime)

      //данные о выбранных дисциплинах
      val choiceData = OracleDao.getStudentChoiceMany(choiceBody.sspId2)

      if (choiceData.isEmpty) {
        OracleDao.insertChoice(choiceBody.sspId2, choiceBody.splId, timeOfChoice) //Без листа
      }

      //блок выбранных дисциплин
      val blockOfDisc = OracleDao.getChoiceForCheck(personId, choiceBody.cdId, choiceBody.comId, choiceBody.numberCycle)

      if (!blockOfDisc.map(_.choice).contains(1)) {
        OracleDao.insertChoice(choiceBody.sspId2, choiceBody.splId, timeOfChoice)
      }

      //список выборов дисциплин
      val listSplId = blockOfDisc.map(_.splId)

      var splIdOld: Long = 0

      listSplId.foreach(id => {
        if (id != choiceBody.splId) {
          splIdOld = id
        }
      })

      OracleDao.updateChoice(choiceBody.sspId2, choiceBody.splId, splIdOld)

      null
    }

    /**
     * Generate pdf with choice of electives
     */
    def getPdf(login: String, personId: Long, req: Request, res: Response): AnyRef = {
      val studInfo = OracleDao.getInfoForPdf(personId)

      val bos = new ByteArrayOutputStream()
      val electives = OracleDao.getElectivesForPdf(studInfo.get.sspId2)

      if (electives.isEmpty) {
        throw new AppException("Нет выбранных дисциплин для заявления")
      }

      statementElectivesPdf.doReport(personId, studInfo.get, ElectiveService.groupingForPdf(electives), outputStream = bos)

      res.header("Content-type", "application/pdf")
      val format = new SimpleDateFormat("ddMMY")
      val cal = format.format(Calendar.getInstance().getTime)
      val encodedFileName = MimeUtility.encodeText("electives_" + personId + "_" + studInfo.get.sspId2 + "_" + cal + ".pdf", "UTF-8", "Q")

      res.header("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"")
      res.raw.getOutputStream.write(bos.toByteArray)
      null
    }

    def getForModerator(login: String, personId: Long, req: Request, res: Response): AnyRef = {
      null
    }

    api.get("/person", getElectives)
    api.post("/insertOrUpdateChoice", "application/json", postChoice)
    api.get("/pdf", getPdf)
  }
}