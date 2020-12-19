package models

/*import java.time.LocalDateTime
import java.util.UUID

import scala.collection.mutable

case class Session(token: String, username: String, expiration: LocalDateTime)

object Session {

  // Map token -> Session
  private val sessions= mutable.Map.empty[String, Session]

  def getSession(token: String): Option[Session] = {
    sessions.get(token)
  }

  def generateToken(username: String): String = {
    // we use UUID to make sure randomness and uniqueness on tokens
    val token = s"$username-token-${UUID.randomUUID().toString}"
    sessions.put(token, Session(token, username, LocalDateTime.now().plusHours(6)))

    token
  }

} */