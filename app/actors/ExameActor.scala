package actors

import java.util.Date

import akka.actor.Actor
import models.{Exame, QueryAll}
import org.mongodb.scala.bson.{BsonDouble, BsonInt32, BsonInt64, BsonString}
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{MongoClient, MongoDatabase, documentToUntypedDocument}
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.ExecutionContext.Implicits.global

class ExameActor(mongoUrl:String) extends Actor {



  override def receive = {
    case exame:Exame => {

      val mongoClient = MongoClient(mongoUrl)
      val database: MongoDatabase = mongoClient.getDatabase("Exames")
      val examesCollection = database.getCollection("exames")

      val document = Document(Json.toJson(exame).as[JsObject].toString)
      examesCollection.insertOne(document)

      mongoClient.close()


    }
    case exames:Array[Exame] => {

      val mongoClient = MongoClient(mongoUrl)
      val database: MongoDatabase = mongoClient.getDatabase("Exames")
      val examesCollection = database.getCollection("exames")

      val documents = exames.map(p => Document(Json.toJson(p).as[JsObject].toString))
      val fut = examesCollection.insertMany(documents).toFuture

      fut.onComplete( _ => mongoClient.close() )

    }

    case query:QueryAll => {

      val mongoClient = MongoClient(mongoUrl)
      val database: MongoDatabase = mongoClient.getDatabase("Exames")
      val examesCollection = database.getCollection("exames")


      val futureExames = examesCollection.find().toFuture

      val mySender = sender
      futureExames.map(
        documents => {

          if (documents.isEmpty) mySender ! None
          else {



            val docs = documents.map(doc => {

              val value:Float = doc.get("valor").get match {
                case v:BsonInt32 => v.getValue + .0f
                case v:BsonString => (v.getValue + "").toFloat
                case v:BsonDouble => v.getValue.toFloat
                case _ => 0.0f
              }

              val dv = doc.get("data").get match {
                case v:BsonInt64 => v.getValue
              }



              Exame(
                  doc.getString("tipo"),
                  doc.getString("laboratorio"),
                  value,
                  doc.getString("unidade"),
                  new Date(dv)
                )
              }
            )

            mySender ! docs

          }

          mongoClient.close()
        }
      )
    }
    case _ => {}
  }

}
