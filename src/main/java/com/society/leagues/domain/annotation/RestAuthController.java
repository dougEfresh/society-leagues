package com.society.leagues.domain.annotation;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@RequiresAuth
@Component
@RestController
public @interface RestAuthController {
}
