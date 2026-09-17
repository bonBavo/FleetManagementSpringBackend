package com.vibran.config;

import com.vibran.auth.filter.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    // ── STOMP endpoint ────────────────────────────────────────
    // Flutter connects to: ws://your-server/ws
    // With SockJS fallback: http://your-server/ws
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // restrict in production
                .withSockJS();                  // SockJS fallback for Flutter web
    }

    // ── Message broker ────────────────────────────────────────
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // Prefix for messages FROM Flutter TO server (client sends)
        registry.setApplicationDestinationPrefixes("/app");

        // Prefix for messages FROM server TO Flutter (server pushes)
        // Simple in-memory broker — switch to RabbitMQ/Redis later for scale
        registry.enableSimpleBroker(
                "/topic",   // broadcast — e.g. /topic/vehicle/5/location
                "/queue"    // point-to-point — e.g. /queue/user/3/alerts
        );

        // Prefix for user-specific messages
        registry.setUserDestinationPrefix("/user");
    }

    // ── JWT auth on WebSocket handshake ───────────────────────
    // Every STOMP CONNECT frame carries the JWT — we validate it here
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }
}
