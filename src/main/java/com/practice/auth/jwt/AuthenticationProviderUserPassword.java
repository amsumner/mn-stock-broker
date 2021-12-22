package com.practice.auth.jwt;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Singleton
@Slf4j
public class AuthenticationProviderUserPassword implements AuthenticationProvider {


    @Override
    public Publisher<AuthenticationResponse> authenticate(
            @Nullable final HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Mono.<AuthenticationResponse>create(emitter -> {
           final Object identity = authenticationRequest.getIdentity();
            Object secret = authenticationRequest.getSecret();
            log.info("User {} tried to login", identity);

            if (identity.equals("my-user") && secret.equals("secret")) {
                //Success
                emitter.success(AuthenticationResponse.success("my-user"));
            } else {
                emitter.error(AuthenticationResponse.exception());
            }
        });
    }
}
