package models
import java.util.UUID
import scalikejdbc.WrappedResultSet

case class User(userId: UUID, username: String, password: String)


object User {
  def fromRS(rs: WrappedResultSet) : User = {
    User(UUID.fromString(rs.string("user_id")),
      rs.string("username"),
      rs.string("password"))
  }
}
