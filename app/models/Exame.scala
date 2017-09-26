package models

import java.util.Date
import javax.inject.Inject

import play.api.db.Database
import play.api.libs.json.{JsObject, Json}

case class Exame(tipo:String, laboratorio:String, valor:Float, unidade:String, data:Date)

case class QueryAll()

object Exame {
  implicit val exameReader = Json.reads[Exame]
  implicit val exameWriter = Json.writes[Exame]
}

class ExameRepository @Inject() (db:Database) {

  def createTable: Unit = {
    db.withConnection {
      conn =>
        val statement = conn.prepareStatement(
          """
          CREATE TABLE exames (
            id SERIAL PRIMARY KEY,
            data jsonb
          );
          """
        )

        statement.execute()
    }
  }

  def insert(exame:Exame): Unit = {
    db.withConnection {
      conn =>
        val statement = conn.prepareStatement(
          """
            INSERT INTO exames(data) VALUES (to_json(?::jsonb));
          """
        )

        val document = Json.toJson(exame).as[JsObject].toString;

        statement.setString(1,document);

        statement.executeUpdate
    }
  }

  def findAll(): List[Exame] = {
    db.withConnection {
      conn =>
        val statement = conn.prepareStatement(
          """
            SELECT data FROM exames;
          """
        )

        val rs = statement.executeQuery()

        val it = new Iterator[Exame] {
          def hasNext = rs.next()
          def next() = Exame.exameReader.reads(Json.parse(rs.getString(1))).get
        }.toStream

        val exames = for (ss <- it) yield ss

        exames.toList
    }
  }

}