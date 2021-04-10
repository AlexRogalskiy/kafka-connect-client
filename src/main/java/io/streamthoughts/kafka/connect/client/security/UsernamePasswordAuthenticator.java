/*
 * Copyright 2019-2021 StreamThoughts.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamthoughts.kafka.connect.client.security;

import java.util.Objects;
import java.util.function.Supplier;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** UsernamePasswordAuthenticator. */
public class UsernamePasswordAuthenticator implements Authenticator {

    private final Supplier<Credential> credentialSupplier;

    /**
     * Creates a new {@link UsernamePasswordAuthenticator} instance.
     *
     * @param credentialSupplier the object supplying the {@link Credential}.
     */
    public UsernamePasswordAuthenticator(final Supplier<Credential> credentialSupplier) {
        this.credentialSupplier =
                Objects.requireNonNull(credentialSupplier, "credentialSupplier cannot be null");
    }

    @Nullable
    @Override
    public Request authenticate(final @Nullable Route route, final @NotNull Response response) {
        final Credential credential = credentialSupplier.get();
        final String password = credential.password();
        final String username = credential.username();

        if (username != null && password != null) {
            final String basic = Credentials.basic(username, password);
            return response.request().newBuilder().header("Authorization", basic).build();
        }

        return response.request().newBuilder().build();
    }

    public static class Credential {

        private final String username;
        private final String password;

        public static Credential empty() {
            return new Credential(null, null);
        }

        public Credential(final String username, final String password) {
            this.username = username;
            this.password = password;
        }

        public String username() {
            return username;
        }

        public String password() {
            return password;
        }
    }
}
