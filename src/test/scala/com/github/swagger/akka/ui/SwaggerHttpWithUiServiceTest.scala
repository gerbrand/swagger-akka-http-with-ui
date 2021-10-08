package com.github.swagger.akka.ui

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.swagger.akka.model._
import com.github.swagger.akka.samples._
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme.In
import io.swagger.v3.oas.models.servers.Server
import org.json4s._
import org.json4s.native.JsonMethods._
import org.jsoup.Jsoup

import scala.xml.XML
import org.scalatest.BeforeAndAfterAll

import scala.collection.JavaConverters._
import scala.collection.immutable.ListMap
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * Testing the UI. Based on SwaggerHttpServiceTest, but testing the UI part here.
 */
class SwaggerHttpWithUiServiceTest
  extends AnyWordSpec with Matchers with BeforeAndAfterAll with ScalatestRouteTest {

  override def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }

  def swaggerService = new SwaggerHttpWithUiService {
    override val apiClasses: Set[Class[_]] = Set(classOf[PetHttpService], classOf[UserHttpService])
    override val basePath = "api"
    override val apiDocsPath = "api-doc"
    override val host = "some.domain.com:12345"
    override val info = Info(description = "desc1",
      version = "v1.0",
      title = "title1",
      termsOfService = "Free and Open",
      contact = Some(Contact(name = "Alice Smith", url = "http://com.example.com/alicesmith", email = "alice.smith@example.com")),
      license = Some(License(name = "MIT", url = "https://opensource.org/licenses/MIT")))
  }

  implicit val formats = org.json4s.DefaultFormats

  "The SwaggerHttpWithUiService" when {
    "accessing the root doc path" should {
      "return the basic set of api info" in {
        Get(s"/${swaggerService.apiDocsPath}/swagger.json") ~> swaggerService.routes ~> check {
          handled shouldBe true
          contentType shouldBe ContentTypes.`application/json`
          val str = responseAs[String]
          val response = parse(str)
          (response \ "openapi").extract[String] shouldEqual "3.0.1"
          (response \ "info" \ "description").extract[String] shouldEqual swaggerService.info.description
          (response \ "info" \ "title").extract[String] shouldEqual swaggerService.info.title
          (response \ "info" \ "termsOfService").extract[String] shouldEqual swaggerService.info.termsOfService
          (response \ "info" \ "version").extract[String] shouldEqual swaggerService.info.version
          (response \ "info" \ "contact").extract[Option[Contact]] shouldEqual swaggerService.info.contact
          (response \ "info" \ "license").extract[Option[License]] shouldEqual swaggerService.info.license
          val servers = (response \ "servers").extract[JArray]
          servers.arr should have size 1
          (servers.arr.head \ "url").extract[String] shouldEqual "http://some.domain.com:12345/api/"
        }
      }
    }

    "defining a derived service" should {
      "set the basePath" in {
        swaggerService.basePath should equal ("api")
      }
      "set the apiDocsPath" in {
        swaggerService.apiDocsPath should equal ("api-doc")
      }
      "redirect when no / is added" in {
        Get(s"/${swaggerService.apiDocsPath}") ~> swaggerService.routes ~> check {
          handled shouldBe true
          contentType shouldBe ContentTypes.`text/html(UTF-8)`
          response.status shouldBe StatusCodes.PermanentRedirect
        }
      }
      "return index with updated swagger.json path" in {
        Get(s"/${swaggerService.apiDocsPath}/") ~> swaggerService.routes ~> check {
          handled shouldBe true
          contentType shouldBe ContentTypes.`text/html(UTF-8)`
          val str = responseAs[String]
          val response = Jsoup.parse(str)
          val scriptTags = response.getElementsByTag("script")
          scriptTags.get(2).toString should include(s"""url: "swagger.json"""")
        }

      }


    }
  }
}
