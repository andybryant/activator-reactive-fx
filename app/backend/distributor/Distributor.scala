package backend.distributor

import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.UpgradeToWebsocket
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, Uri}
import akka.stream._
import backend.shared.CodecStage
import com.typesafe.scalalogging.StrictLogging

import scala.language.postfixOps
import scala.util.{Failure, Success}

object Distributor {
  def start()(implicit sys: ActorSystem) = sys.actorOf(Props[Distributor], "distributor")
}

private class Distributor extends Actor with StrictLogging {

  implicit val system = context.system
  implicit val executor = context.dispatcher


  var connectionCounter = 0

  val decider: Supervision.Decider = {
    case x =>
      logger.error("Stream terminated", x)
      Supervision.Stop
  }

  implicit val mat = ActorMaterializer(
    ActorMaterializerSettings(context.system)
      .withDebugLogging(enable = false)
      .withSupervisionStrategy(decider))


  val requestHandler: HttpRequest => HttpResponse = {
    case req@HttpRequest(HttpMethods.GET, Uri.Path("/"), _, _, _) =>
      req.header[UpgradeToWebsocket] match {
        case Some(upgrade) =>
          connectionCounter += 1
          upgrade.handleMessages(buildFlow(connectionCounter))
        case None => HttpResponse(400, entity = "Not a valid websocket request!")
      }
    case _: HttpRequest => HttpResponse(404, entity = "Unknown resource!")
  }

  val port = context.system.settings.config.getInt("distributor.port")
  val host = context.system.settings.config.getString("distributor.host")
  Http().bindAndHandleSync(handler = requestHandler, interface = host, port = port) onComplete {
    case Success(binding) => self ! SuccessfulBinding(binding)
    case Failure(x) => self ! BindingFailed(x)
  }

  def buildFlow(connId: Int) =
    WebsocketFrameStage() atop CodecStage() atop MetricsStage(connId) atop ShapingStage(1000) join DistributorEndpointStage(connId, StreamRegistry.selection)

  private case class SuccessfulBinding(binding: Http.ServerBinding)

  private case class BindingFailed(x: Throwable)

  override def receive: Receive = {
    case SuccessfulBinding(binding) =>
      logger.info(s"Websocket server listening at $host:$port")
    case BindingFailed(x) =>
      logger.error(s"Websocket server failed to bind to $host:$port", x)
  }


}

