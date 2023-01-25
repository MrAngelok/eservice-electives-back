package edu.omsu.eservice.exception

object ExceptionMessage extends Enumeration {
  type ExceptionMessage = Value
  val success = Value("Операция выполнена успешно.")
  val notChange = Value("Без изменений.")
  val error = Value("Ошибка!")
}