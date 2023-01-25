package edu.omsu.eservice.controllers

import edu.omsu.eservice.exception.ExceptionMessage

class AppException(val message: String, val data: AnyRef) extends RuntimeException(message) {
  def this(exceptionMessage: String) {
    this(exceptionMessage.toString, null)
  }

  def this(exceptionMessage: ExceptionMessage.Value, data: AnyRef = null) {
    this(exceptionMessage.toString, data)
  }
}