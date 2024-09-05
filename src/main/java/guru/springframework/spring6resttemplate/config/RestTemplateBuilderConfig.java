package guru.springframework.spring6resttemplate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateBuilderConfig {

    @Value("${rest.template.rootUrl}")
    String rootUrl;

//    This repository contains the registration details of the OAuth clients
//    (e.g., client ID, client secret, scopes).
//    It’s a central place for storing the metadata of each OAuth client.
    private final ClientRegistrationRepository clientRegistrationRepository;

    //This service manages authorized clients and their tokens. It stores the actual tokens
    // (like access tokens, refresh tokens) that are issued to the clients.
    // It also knows how to retrieve them when needed and persist them (possibly in-memory or in a database,
    // depending on the implementation).
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public RestTemplateBuilderConfig(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService oAuth2AuthorizedClientService
    ) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    }

    @Bean
    OAuth2AuthorizedClientManager auth2AuthorizedClientManager() {
//        Handling Authentication: This component is responsible for handling the actual authentication
//        and token acquisition process. In this case, it is configured to use the Client Credentials Grant flow.
//        This means it knows how to interact with an OAuth 2.0
        var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        ///Think of authorizedClientManager as the "manager" that keeps track of which clients are
        // registered and keeps the tokens that they’ve obtained.
        // It makes sure the right clients can access the right tokens when they need to.

//        Managing Authorized Clients: This component handles
//        the lifecycle of the OAuth2 clients and their associated tokens.
//        It ensures that the OAuth2 client gets an access token when required and knows when to refresh tokens
//        if necessary.
        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager
                (clientRegistrationRepository, oAuth2AuthorizedClientService);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer) {
        assert rootUrl != null;

        return configurer.configure(new RestTemplateBuilder())
                .uriTemplateHandler(new DefaultUriBuilderFactory(rootUrl));
    }
}
