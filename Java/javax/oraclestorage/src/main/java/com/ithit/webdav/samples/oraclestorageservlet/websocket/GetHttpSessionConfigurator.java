package com.ithit.webdav.samples.oraclestorageservlet.websocket;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import static com.ithit.webdav.samples.oraclestorageservlet.websocket.WebSocketServer.INSTANCE_HEADER_NAME;

public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig config,
                                HandshakeRequest request,
                                HandshakeResponse response) {
        config.getUserProperties().put(INSTANCE_HEADER_NAME, request.getHeaders()
                .entrySet()
                .stream()
                .filter(x -> x.getKey().equalsIgnoreCase(INSTANCE_HEADER_NAME))
                .findFirst().map(x -> {
                    if (!x.getValue().isEmpty()) {
                        return x.getValue().get(0);
                    }
                    return "";
                })
                .orElse(""));
    }
}