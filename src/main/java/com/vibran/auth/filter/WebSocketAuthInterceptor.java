package com.vibran.auth.filter;

import com.vibran.auth.service.JwtService;
import com.vibran.auth.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);

        // Only validate on CONNECT — not on every message frame
        if (accessor != null &&
                StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("WebSocket CONNECT rejected: no JWT token");
                throw new IllegalArgumentException(
                        "WebSocket connection requires Authorization header");
            }

            String token = authHeader.substring(7);

            try {
                String email = jwtService.extractEmail(token);
                UserDetails userDetails = userDetailsService
                        .loadUserByUsername(email);

                if (!jwtService.isTokenValid(token, userDetails)) {
                    throw new IllegalArgumentException("Invalid or expired JWT");
                }

                // Set authenticated user on the WebSocket session
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                accessor.setUser(authToken);
                log.debug("WebSocket authenticated: user={}", email);

            } catch (Exception e) {
                log.warn("WebSocket CONNECT rejected: {}", e.getMessage());
                throw new IllegalArgumentException("WebSocket auth failed: "
                        + e.getMessage());
            }
        }

        return message;
    }

}
