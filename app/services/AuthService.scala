package services

import java.security.MessageDigest
import java.util.{Base64, UUID}
import java.util.concurrent.TimeUnit

import models.User
import org.mindrot.jbcrypt.BCrypt
import play.api.cache.SyncCacheApi
import play.api.mvc.{Cookie, RequestHeader}
import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}

import scala.concurrent.duration.Duration

case class UserLoginData(username: String, password: String)

class AuthService(cacheApi: SyncCacheApi) {
  val mda = MessageDigest.getInstance("SHA-512")
  val cookieHeader = "X-Auth-Token"
  //...


  def login(userCode: String, password: String): Option[Cookie] = {
    for {
      user <- checkUser(userCode,password)
      cookie <- Some(createCookie(user))
    } yield {
      cookie
    }
  }

  def checkCookie(header: RequestHeader) : Option[User] = {
    for {
      cookie <- header.cookies.get(cookieHeader)
      user <- cacheApi.get[User](cookie.value)
    } yield {
      user
    }
  }

  private def createCookie(user: User): Cookie = {
    val randomPart = UUID.randomUUID().toString.toUpperCase
    val userPart = user.userId.toString.toUpperCase
    val key = s"$randomPart|$userPart"
    val token = Base64.getEncoder.encodeToString(mda.digest(key.getBytes))
    val duration = Duration.create(10, TimeUnit.HOURS)
    cacheApi.set(token, user, duration)
    Cookie(cookieHeader, token, maxAge = Some(duration.toSeconds.toInt))
  }

  private def checkUser(username: String, password: String) : Option[User] = DB.readOnly {
    implicit session =>
      val mayBeUser = sql"select * from users where username = $username".
        map(User.fromRS).single().apply()
      mayBeUser.flatMap{
        user =>
          if(BCrypt.checkpw(password, user.password)) {
            Some(user)
          } else {
            None
          }
      }
  }

}