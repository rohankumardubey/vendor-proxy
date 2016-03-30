package controllers

import play.api.Logger
import play.api.Play._
import play.api.libs.ws.WS
import play.api.mvc.Controller
import security.AsConsumer
import utils.RequestHeaders
import utils.Environment._

import scala.concurrent.ExecutionContext.Implicits.global

object Proxy extends Controller with RequestHeaders {

  def execute(service: String) = AsConsumer(parse.json) { (request, consumerName) =>
    Logger.info(s"Proxying $service on behalf of $consumerName")
    WS.url(apiUrl(service))
      .withHeaders(tokenHeader(service), consumerHeader(consumerName))
      .withMethod(request.method)
      .withBody(request.body)
      .execute()
      .map(response => Status(response.status)(response.body))
  }
}
