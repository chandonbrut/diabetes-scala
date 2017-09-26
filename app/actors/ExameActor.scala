package actors

import akka.actor.Actor
import models.{Exame, ExameRepository, QueryAll}
import scala.concurrent.ExecutionContext.Implicits.global

class ExameActor(repo:ExameRepository) extends Actor {

  override def receive = {
    case exame:Exame => {
      repo.insert(exame)
    }
    case exames:Array[Exame] => {
      exames.map(repo.insert(_))
    }

    case query:QueryAll => {
      sender ! repo.findAll();
    }
    case _ => {}
  }

}
