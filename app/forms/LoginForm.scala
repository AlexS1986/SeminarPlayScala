package forms

import play.api.data.Form
import play.api.data.Forms.{mapping, number, text}

case class LoginForm(username: String, password:String)

object LoginForm {
  val form: Form[LoginForm] = Form(
    mapping(
      "username" -> text,
      "password" -> text
    )(LoginForm.apply)(LoginForm.unapply)
  )
}

