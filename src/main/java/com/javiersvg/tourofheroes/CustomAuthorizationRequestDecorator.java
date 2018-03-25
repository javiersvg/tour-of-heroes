package com.javiersvg.tourofheroes;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthorizationRequestDecorator implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private static final String DEFAULT_AUTHORIZATION_REQUEST_BASE_URI = "/login/oauth2/code";
    private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";
    private final AntPathRequestMatcher authorizationRequestMatcher;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> repository;

    CustomAuthorizationRequestDecorator(AuthorizationRequestRepository<OAuth2AuthorizationRequest> repository,
                                        ClientRegistrationRepository clientRegistrationRepository) {
        this.repository = repository;
        Assert.hasText(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI, "authorizationRequestBaseUri cannot be empty");
        Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
        this.authorizationRequestMatcher = new AntPathRequestMatcher(
                DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        OAuth2AuthorizationRequest oAuth2AuthorizationRequest = this.repository.loadAuthorizationRequest(request);
        if (oAuth2AuthorizationRequest == null && request.getParameter(OAuth2ParameterNames.STATE).equals(OAuth2ParameterNames.STATE)) {
            oAuth2AuthorizationRequest = sendRedirectForAuthorization(request);
        }
        return oAuth2AuthorizationRequest;
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        this.repository.saveAuthorizationRequest(authorizationRequest, request, response);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.repository.removeAuthorizationRequest(request);
    }

    private OAuth2AuthorizationRequest sendRedirectForAuthorization(HttpServletRequest request) {

        String registrationId = this.authorizationRequestMatcher
                .extractUriTemplateVariables(request).get(REGISTRATION_ID_URI_VARIABLE_NAME);
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new IllegalArgumentException("Invalid Client Registration with Id: " + registrationId);
        }

        String redirectUriStr = this.expandRedirectUri(request, clientRegistration);

        Map<String, Object> additionalParameters = new HashMap<>();
        additionalParameters.put(OAuth2ParameterNames.REGISTRATION_ID, clientRegistration.getRegistrationId());

        OAuth2AuthorizationRequest.Builder builder;
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(clientRegistration.getAuthorizationGrantType())) {
            builder = OAuth2AuthorizationRequest.authorizationCode();
        } else if (AuthorizationGrantType.IMPLICIT.equals(clientRegistration.getAuthorizationGrantType())) {
            builder = OAuth2AuthorizationRequest.implicit();
        } else {
            throw new IllegalArgumentException("Invalid Authorization Grant Type for Client Registration (" +
                    clientRegistration.getRegistrationId() + "): " + clientRegistration.getAuthorizationGrantType());
        }

        return builder
                .clientId(clientRegistration.getClientId())
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .redirectUri(request.getParameter(OAuth2ParameterNames.REDIRECT_URI))
                .scopes(clientRegistration.getScopes())
                .state(request.getParameter(OAuth2ParameterNames.STATE))
                .additionalParameters(additionalParameters)
                .build();
    }

    private String expandRedirectUri(HttpServletRequest request, ClientRegistration clientRegistration) {
        int port = request.getServerPort();
        if (("http".equals(request.getScheme()) && port == 80) || ("https".equals(request.getScheme()) && port == 443)) {
            port = -1;		// Removes the port in UriComponentsBuilder
        }

        String baseUrl = UriComponentsBuilder.newInstance()
                .scheme(request.getScheme())
                .host(request.getServerName())
                .port(port)
                .path(request.getContextPath())
                .build()
                .toUriString();

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("baseUrl", baseUrl);
        uriVariables.put(REGISTRATION_ID_URI_VARIABLE_NAME, clientRegistration.getRegistrationId());

        return UriComponentsBuilder.fromUriString(clientRegistration.getRedirectUriTemplate())
                .buildAndExpand(uriVariables)
                .toUriString();
    }
}
