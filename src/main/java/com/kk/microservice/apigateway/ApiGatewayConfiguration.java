package com.kk.microservice.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {
	
	@Bean
	public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
		return builder.routes()
				.route( p -> p.path("/get")
						.filters(f -> f
								.addRequestHeader("MyHeader", "MyHeaderValue") //To add custom request header
								.addRequestParameter("MyReqParam", "MyReqParamValue")) //To add custom request paramerter
						.uri("http://httpbin.org:80") ) // "/get" request will be redirected to this uri.
				.route( p -> p.path("/currency-exchange/**") // Here "/currency-exchange/" is the url path and ** means anything ahead of it.
						.uri("lb://currency-exchange-service/") ) // To find "currency-exchange" application name in load balancer
				.route( p -> p.path("/currency-conversion/**")
						.uri("lb://currency-conversion-service") )
				.route( p -> p.path("/currency-conversion-feign/**")
						.uri("lb://currency-conversion-service") )
				.route( p -> p.path("/currency-conversion-new/**") //setting up new url
						.filters(f -> f
								.rewritePath("/currency-conversion-new/(?<segment>.*)" //new url 
										,"/currency-conversion/${segment}")) //actual url
						.uri("lb://currency-conversion-service") )
				.build();
	}
	
}
