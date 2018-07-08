package socialMedia.backend



trait DataStore {
  def getMessage: String
  def getAttributes: Map[String, String]
  def getTopic: String
}

abstract class Item extends DataStore

class User(val name: String, val age: Int) extends Item {
  override def getMessage: String = name
  override def getAttributes: Map[String, String] = Map("age" -> age.toString)
  override def getTopic: String = "user"
}

class Post(val text: String, val fromUser: String) extends Item {
  override def getMessage: String = text
  override def getAttributes: Map[String, String] = Map("user" -> fromUser)
  override def getTopic: String = "post"
}

class SocialMessage(val fromUser: String, val toUser: String, val text: String) extends Item {
  override def getMessage: String = text
  override def getAttributes: Map[String, String] = Map("fromUser" -> fromUser, "toUser" -> toUser)
  override def getTopic: String = "message"
}
