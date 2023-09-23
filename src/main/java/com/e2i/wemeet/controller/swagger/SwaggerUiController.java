package com.e2i.wemeet.controller.swagger;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Profile("!prod")
@Controller
public class SwaggerUiController {

    @GetMapping("/api-docs")
    public String swaggerUi() {
        return "redirect:/static/dist/swagger-ui.html";
    }

}
