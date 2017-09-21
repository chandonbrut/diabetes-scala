package controllers

import actors.ExameActor
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import com.google.inject.Inject
import play.api.mvc.InjectedController
import akka.util.Timeout
import models.{Exame, QueryAll}
import play.api.Configuration
import play.api.libs.json.{JsError, Json}
import akka.pattern.ask;

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._

class ExameService @Inject() (implicit system:ActorSystem, materializer:Materializer,configuration:Configuration) extends InjectedController {

  implicit val timeout = Timeout(30 seconds)

  val mongoUrl:String = configuration.getString("diabetes.mongourl").get


  def getActor():ActorRef = {
    system.actorOf(Props(new ExameActor(mongoUrl)))

  }

  def all = Action.async {

    request => {
      val actorReply = getActor() ? QueryAll()
      actorReply.map(_ match {
        case reply:List[Exame] => Ok(Json.toJson(reply))
        case _ => BadRequest(Json.obj("error" -> "Sem exames"))
      })
    }
   }

  def insertExame = Action(parse.json) {
    request => {
      val exameRequest = request.body.validate[Exame]
      exameRequest.fold(
        errors => BadRequest(Json.obj("error" -> JsError.toJson(errors))),
        exame => {
          getActor() ! exame
          Ok(Json.obj("status" -> ("ok")))
        }
      )
    }
  }

  def bulkInsertExame = Action(parse.json) {
    request => {
      val pdvRequest = request.body.validate[Array[Exame]]

      pdvRequest.fold(
        errors => BadRequest(Json.obj("error" -> JsError.toJson(errors))),
        exames => {
          getActor() ! exames
          Ok(Json.obj("status" -> ("ok")))
        }
      )
    }
  }

  def show = Action {
    Ok(views.html.view())
  }


}
