package com.github.igalshilman.reqreply;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

class ProtobufUtils {
    private ProtobufUtils() {
    }

    static <M extends Message> M unpackAny(Any any, Class<M> messageType) {
        try {
            return any.unpack(messageType);
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException("Unable to unpack into " + messageType.getName() + ", typeUrl is " + any.getTypeUrl(), e);
        }
    }

    static <M extends Message> M parseMessageFromBytes(Parser<M> messageParser, ByteString bytes) {
        try {
            return messageParser.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
