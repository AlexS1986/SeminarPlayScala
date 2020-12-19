package user

import java.time.{LocalDateTime, ZoneOffset}

import models.{User}
import play.api.mvc._
import services.AuthService

import scala.concurrent.{ExecutionContext, Future}
/*
class UserRequest[A](val user: Option[User], request: Request[A]) extends WrappedRequest[A](request)

class UserAction (val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent]
    with ActionTransformer[Request, UserRequest] {

  def transform[A](request: Request[A]) = Future.successful {

    val sessionTokenOpt = request.session.get("sessionToken")

    val user = sessionTokenOpt
      .flatMap(token => Session.getSession(token))
      .filter(_.expiration.isAfter(LocalDateTime.now(ZoneOffset.UTC)))
      .map(_.username)
      .flatMap(UserDAO.getUser)

    new UserRequest(user, request)
  }
} */

case class UserAuthRequest[A](user: User, request: Request[A]) extends WrappedRequest[A](request)

class UserAuthAction(authService: AuthService, ec: ExecutionContext, playBodyParsers: PlayBodyParsers)
  extends ActionBuilder[UserAuthRequest, AnyContent] {
  override val executionContext = ec
  override def parser = playBodyParsers.defaultBodyParser
  def invokeBlock[A](request: Request[A],
                     block: (UserAuthRequest[A]) => Future[Result]): Future[Result] ={
    val maybeUser = authService.checkCookie(request)
    maybeUser match {
      case None => Future.successful(Results.Redirect("/login_page"))
      case Some(user) => block(UserAuthRequest(user, request))
    }
  }
}