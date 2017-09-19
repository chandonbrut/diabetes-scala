package actors

import akka.actor.Actor
import models.Exame
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{MongoClient, MongoDatabase}
import play.api.libs.json.{JsObject, Json}
import scala.concurrent.ExecutionContext.Implicits.global

class ExameActor(mongoUrl:String) extends Actor {



  override def receive = {
    case exame:Exame => {

      val mongoClient = MongoClient(mongoUrl)
      val database: MongoDatabase = mongoClient.getDatabase("Exames")
      val pdvsCollection = database.getCollection("exames")

      val document = Document(Json.toJson(exame).as[JsObject].toString)
      pdvsCollection.insertOne(document)

      mongoClient.close()


    }
    case pdvs:Array[Exame] => {

      val mongoClient = MongoClient(mongoUrl)
      val database: MongoDatabase = mongoClient.getDatabase("Exames")
      val pdvsCollection = database.getCollection("exames")

      val documents = pdvs.map(p => Document(Json.toJson(p).as[JsObject].toString))
      val fut = pdvsCollection.insertMany(documents).toFuture

      fut.onComplete( _ => mongoClient.close() )

    }

//    case query:QueryById => {
//
//      val mongoClient = MongoClient("mongodb://localhost")
//      val database: MongoDatabase = mongoClient.getDatabase("challenge")
//      val pdvsCollection = database.getCollection("pdvs")
//
//      val futurePDV = pdvsCollection.find(equal("id",query.id)).toFuture
//      val mySender = sender
//      futurePDV.map(
//        documents => {
//          if (documents.isEmpty) mySender ! None
//          else mySender ! (pdvReader.reads(Json.parse(documents.head.toJson())).get)
//
//          mongoClient.close()
//        }
//      )
//    }
//
//    case query:QueryAll => {
//
//      val mongoClient = MongoClient("mongodb://localhost")
//      val database: MongoDatabase = mongoClient.getDatabase("challenge")
//      val pdvsCollection = database.getCollection("pdvs")
//
//      val futurePDV = pdvsCollection.find().toFuture
//      val mySender = sender
//      futurePDV.map(
//        documents => {
//          if (documents.isEmpty) mySender ! None
//          else {
//            val docs = documents.map(doc => pdvReader.reads(Json.parse(doc.toJson())).get)
//            mySender ! docs
//          }
//
//          mongoClient.close()
//        }
//      )
//    }
//    case qq:QueryNearest => {
//      val mySender = sender
//
//      val coordinates = qq.coordinates
//
//      val pt = BsonArray(coordinates.lng, coordinates.lat)
//      val query = Document("address" ->
//        Document("$nearSphere" ->
//          Document("$geometry" ->
//            Document("type" -> "Point",
//              "coordinates" -> pt
//            )
//          )
//        ),
//        "coverageArea" -> Document("$geoIntersects" ->
//          Document("$geometry" ->
//            Document("type" -> "Point",
//              "coordinates" -> pt
//            )
//          )
//        ))
//
//      val mongoClient = MongoClient("mongodb://localhost")
//      val database: MongoDatabase = mongoClient.getDatabase("challenge")
//      val pdvsCollection = database.getCollection("pdvs")
//
//      val futurePDVs = pdvsCollection.find(query).toFuture
//      futurePDVs.map {
//        pdv => {
//          if (pdv.isEmpty) mySender ! None
//          else mySender ! (pdvReader.reads(Json.parse(pdv.head.toJson())).get)
//
//          mongoClient.close()
//        }
//      }
//
//    }
    case _ => {}
  }

}
