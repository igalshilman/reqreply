package com.github.igalshilman.reqreply;

import com.google.protobuf.Any;
import org.apache.flink.statefun.sdk.FunctionType;
import org.apache.flink.statefun.sdk.io.EgressIdentifier;
import org.apache.flink.statefun.sdk.io.IngressIdentifier;

public class Constants {

    static final FunctionType SCALA_FUNCTION_TYPE = new FunctionType("example", "greeter");

    static final IngressIdentifier<Any> IN = new IngressIdentifier<>(Any.class, "example", "in");

    static final EgressIdentifier<Any> OUT = new EgressIdentifier<>("example", "greets", Any.class);
}
