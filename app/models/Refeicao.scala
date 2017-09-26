package models

case class GrupoAlimentar(nome:String,alimentos:List[Alimento])
case class Alimento(grupo:GrupoAlimentar,nome:String)
case class Porcao(quantidade:Float,unidade:String,alimento:Alimento)
case class Refeicao(nome:String,ordem:Int,porcoes:List[Porcao])

object Refeicao {

}
