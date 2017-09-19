package models

import java.util.Date

import play.api.libs.json.{Json}

case class Exame(tipo:String, laboratorio:String, valor:Float, unidade:String, data:Date)

object Exame {

  implicit val exameReader = Json.reads[Exame]
  implicit val exameWriter = Json.writes[Exame]

}
