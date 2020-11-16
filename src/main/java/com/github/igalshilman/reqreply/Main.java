package com.github.igalshilman.reqreply;

import com.github.igalshilman.reqreply.generated.GreetRequest;
import com.github.igalshilman.reqreply.generated.GreetResponse;
import com.google.protobuf.Any;
import org.apache.flink.statefun.flink.harness.Harness;
import org.apache.flink.statefun.flink.harness.io.SerializableConsumer;
import org.apache.flink.statefun.flink.harness.io.SerializableSupplier;
import org.apache.flink.statefun.flink.io.generated.KafkaProducerRecord;

public class Main {

    public static void main(String[] args) throws Exception {
        Harness harness = new Harness();
        harness.withSupplyingIngress(Constants.IN, new GreetGenerator());
        harness.withConsumingEgress(Constants.OUT, new KafkaEgressCollector());
        harness.start();
    }

    /**
     * Generate greet requests. This would act as our in memory ingress.
     */
    private static class GreetGenerator implements SerializableSupplier<Any> {
        /**
         * This method will be called repeatedly by the harness.
         */
        @Override
        public Any get() {
            GreetRequest request = GreetRequest.newBuilder().setWho("adil").build();
            Any any = Any.pack(request);
            throttle(1_000);
            return any;
        }

        private static void throttle(@SuppressWarnings("SameParameterValue") long milliseconds) {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Collect Kafka Egress records sent by the remote function.
     */
    private static class KafkaEgressCollector implements SerializableConsumer<Any> {
        @Override
        public void accept(Any any) {
            KafkaProducerRecord kafkaProducerRecord = ProtobufUtils.unpackAny(any, KafkaProducerRecord.class);
            GreetResponse response = ProtobufUtils.parseMessageFromBytes(GreetResponse.parser(), kafkaProducerRecord.getValueBytes());
            System.out.printf("Producing to '%s' with a key '%s' the greeting '%s'\n", kafkaProducerRecord.getTopic(), kafkaProducerRecord.getKey(), response.getGreeting());
        }
    }
}
