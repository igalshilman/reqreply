/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.igalshilman.reqreply;

import com.github.igalshilman.reqreply.generated.GreetRequest;
import org.apache.flink.statefun.flink.core.httpfn.HttpFunctionProvider;
import org.apache.flink.statefun.flink.core.httpfn.HttpFunctionSpec;
import org.apache.flink.statefun.flink.core.httpfn.StateSpec;
import org.apache.flink.statefun.sdk.spi.StatefulFunctionModule;

import java.net.URI;
import java.util.Map;

import static com.github.igalshilman.reqreply.Constants.SCALA_FUNCTION_TYPE;
import static java.util.Collections.singletonMap;

public final class Module implements StatefulFunctionModule {

    @Override
    public void configure(Map<String, String> globalConfiguration, Binder binder) {
        // bind a function provider that represents our remote scala function, that listens at
        // http://localhost:5000/statefun
        // and has the state "seen_count" defined.
        binder.bindFunctionProvider(SCALA_FUNCTION_TYPE, createScalaRemoteFunctionHttpProvider());

        binder.bindIngressRouter(Constants.IN, (any, downstream) -> {
            // route messages coming off the Ingress represented by IN, into a remote function
            // with the type SCALA_FUNCTION and the id of recipient of the greet.
            GreetRequest request = ProtobufUtils.unpackAny(any, GreetRequest.class);
            downstream.forward(SCALA_FUNCTION_TYPE, request.getWho(), any);
        });
    }

    private HttpFunctionProvider createScalaRemoteFunctionHttpProvider() {
        HttpFunctionSpec scalaSpec = HttpFunctionSpec.builder(SCALA_FUNCTION_TYPE, URI.create("http://localhost:5000/statefun"))
                .withState(new StateSpec("seen_count"))
                .build();

        return new HttpFunctionProvider(singletonMap(SCALA_FUNCTION_TYPE, scalaSpec));
    }
}
