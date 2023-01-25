package edu.omsu.eservice.controllers

import java.text.SimpleDateFormat

import edu.omsu.eservice.conf.ResponseEnvelope
import org.json4s.jackson.Serialization
import org.json4s.{DefaultFormats, Formats}
import spark.ResponseTransformer

class JsonResponseTransformer extends ResponseTransformer {
  implicit val formats: Formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("dd.MM.yyyy")
  }

  override def render(model: scala.AnyRef): String = {
    val envelopeString = model match {
      case re: ResponseEnvelope => re
      case None => ResponseEnvelope(data = null)
      case _ => ResponseEnvelope(data = model)

    }
    Serialization.write(envelopeString)
  }
}
