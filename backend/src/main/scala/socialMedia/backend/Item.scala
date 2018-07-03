package socialMedia.backend

import org.joda.time.DateTime

abstract class Item
case class User(id: Int, name: String, age: Int)
    extends Item

case class Post(id: Int,
                text: String,
                fromUser: Int,
                date: DateTime)
    extends Item

case class Message(id: Int, fromUser: Int, toUser: Int, date: DateTime) extends Item
