package com.society.leagues.domain.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public @interface RequiresAuth {
}
