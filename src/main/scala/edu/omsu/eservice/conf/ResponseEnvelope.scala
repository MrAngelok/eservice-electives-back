package edu.omsu.eservice.conf

import edu.omsu.eservice.exception.ExceptionMessage

/**
  * Created by voroshilovvv on 25.06.2018.
  */
case class ResponseEnvelope(success: Boolean = true, message: String = ExceptionMessage.success.toString, data: AnyRef)
