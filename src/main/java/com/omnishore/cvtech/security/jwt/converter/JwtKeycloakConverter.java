package com.omnishore.cvtech.security.jwt.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JwtKeycloakConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractRoles(jwt);
        return Stream.concat(authorities.stream(), authorities.stream()).collect(Collectors.toSet());
    }

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {
        Map<String, Object> resourceAccess  = jwt.getClaim("resource_access");

        if(resourceAccess != null && resourceAccess.containsKey("omnishore-cv")
                && resourceAccess.get("omnishore-cv") instanceof Map<?, ?> clientRolesObj
                && clientRolesObj.get("roles") instanceof List<?> rolesList) {
            return rolesList.stream()
                    .filter(String.class::isInstance)
                    .map(role -> ((String) role).toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return List.of();

    }
}
