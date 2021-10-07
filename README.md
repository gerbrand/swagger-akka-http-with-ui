# swagger-akka-http-with-ui
Support for generating Swagger REST API documentation along with UI for Akka-Http based services.

This module extends [swagger-akka-http](https://github.com/swagger-akka-http/swagger-akka-http) with the [Swagger-UI](https://github.com/swagger-api/swagger-ui) as [webjar](https://www.webjars.org).

## SwaggerHttpWithUiService

The `SwaggerHttpWithUiService` is a trait extending `SwaggerHttpService` which in turn is extending Akka-Http. It will generate the appropriate Swagger json schema based on a set of inputs declaring your Api and the types you want to expose.

The `SwaggerHttpWithUiService` contains a `routes` property you can concatenate along with your existing akka-http routes. This will expose an endpoint at `<baseUrl>/<specPath>/<resourcePath>` with the specified `apiVersion`, `swaggerVersion` and resource listing along with the [Swagger-UI](https://github.com/swagger-api/swagger-ui).

The service requires a set of `apiTypes` and `modelTypes` you want to expose via Swagger. These types include the appropriate Swagger annotations for describing your api. The `SwaggerHttpService` will inspect these annotations and build the appropriate Swagger response.

Here's an example `SwaggerHttpWithUiService` snippet which exposes [Swagger's PetStore](http://petstore.swagger.io/) resources, `Pet`, `User` and `Store`. The routes property can be concatenated to your other route definitions:

```scala
object SwaggerDocService extends SwaggerHttpWithUiService {
  override val apiClasses: Set[Class[_]] = Set(classOf[PetService], classOf[UserService], classOf[StoreService])
  override val host = "localhost:8080" //the url of your api, not swagger's json endpoint
  override val apiDocsPath = "api-docs" //where you want the the swagger-docs and swagger-json endpoint exposed
  override val info = Info() //provides license and other description details
}.routes
```

For more information, see README of [swagger-akka-http](https://github.com/swagger-akka-http/swagger-akka-http).
