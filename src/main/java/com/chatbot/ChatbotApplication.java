package com.chatbot;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChatbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatbotApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI() {
		final String securitySchemeName = "ApiKeyAuth";

		ApiResponses healthResponses = new ApiResponses()
				.addApiResponse("200", new ApiResponse().description("Service is UP"));

		Operation healthOperation = new Operation()
				.summary("Health Check")
				.description("Spring Boot Actuator Health Endpoint")
				.responses(healthResponses);

		PathItem healthPath = new PathItem().get(healthOperation);

		Paths paths = new Paths().addPathItem("/actuator/health", healthPath);


		return new OpenAPI()
				.paths(paths)
				.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
				.components(new Components().addSecuritySchemes(securitySchemeName,
						new SecurityScheme()
								.name("X-API-KEY")
								.type(SecurityScheme.Type.APIKEY)
								.in(SecurityScheme.In.HEADER)));
	}

}
