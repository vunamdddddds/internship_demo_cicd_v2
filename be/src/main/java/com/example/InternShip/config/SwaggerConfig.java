
package com.example.InternShip.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Local Development Server");

        List<Server> servers = new java.util.ArrayList<>();
        servers.add(localServer);

        return new OpenAPI()
                .info(new Info()
                        .title("API Library")
                        .version("1.0")
                        .description("Swagger configuration for Internship project"))
                .servers(servers)
                // 🔒 Thêm security để hiện ô nhập token
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}

// package com.example.InternShip.config;

// import io.swagger.v3.oas.models.Components;
// import io.swagger.v3.oas.models.OpenAPI;
// import io.swagger.v3.oas.models.info.Info;
// import io.swagger.v3.oas.models.security.SecurityRequirement;
// import io.swagger.v3.oas.models.security.SecurityScheme;
// import io.swagger.v3.oas.models.servers.Server;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import java.util.List;

// @Configuration
// public class SwaggerConfig {

//     @Value("${NGROK_URL:}")
//     private String ngrokUrl;

//     @Value("${server.port}")
//     private String serverPort;

//     @Bean
//     public OpenAPI customOpenAPI() {
//         Server localServer = new Server()
//                 .url("http://localhost:" + serverPort)
//                 .description("Local Development Server");

//         List<Server> servers = new java.util.ArrayList<>();
//         servers.add(localServer);

//         OpenAPI openAPI = new OpenAPI()
//                 .info(new Info()
//                         .title("API Library")
//                         .version("1.0")
//                         .description("Swagger configuration for Internship project"))
//                 .servers(servers)
//                 // 🔒 Thêm security để hiện ô nhập token
//                 .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
//                 .components(new Components()
//                         .addSecuritySchemes("bearerAuth",
//                                 new SecurityScheme()
//                                         .type(SecurityScheme.Type.HTTP)
//                                         .scheme("bearer")
//                                         .bearerFormat("JWT")));

//         if (ngrokUrl != null && !ngrokUrl.isBlank()) {
//             Server ngrokServer = new Server()
//                     .url(ngrokUrl)
//                     .description("Ngrok Tunnel Server");
//             openAPI.getServers().add(0, ngrokServer);
//         }

//         return openAPI;
//     }
// }
