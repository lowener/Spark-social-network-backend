package socialMedia.backend

import org.joda.time.DateTime

abstract class Item
case class User(id: Int, name: String, location: Option[String], age: Int)
    extends Item
case class Post(id: Int,
                text: String,
                fromUser: Int,
                date: DateTime,
                location: Option[String])
    extends Item
case class Message(id: Int, fromUser: Int, toUser: Int, date: DateTime)
