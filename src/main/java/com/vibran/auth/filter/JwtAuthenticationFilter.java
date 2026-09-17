    package com.vibran.auth.filter;

    import com.vibran.auth.service.JwtService;
    import com.vibran.auth.service.UserDetailsServiceImpl;
    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.ExpiredJwtException;
    import io.jsonwebtoken.MalformedJwtException;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.lang.NonNull;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import java.io.IOException;

    @Component
    @RequiredArgsConstructor
    @Slf4j
    public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtService jwtService;
        private final UserDetailsServiceImpl userDetailsService;

        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull FilterChain filterChain)
                throws ServletException, IOException {

            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String token = authHeader.substring(7);

            try {
                final Claims claims = jwtService.extractAllClaims(token);
                final String email = claims.getSubject();

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    if (jwtService.isTokenValid(token, userDetails, claims)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                log.debug("JWT expired for request: {}", request.getRequestURI());
                handleAuthenticationError(response, "Token expired");
                return;
            } catch (MalformedJwtException | IllegalArgumentException e) {
                log.debug("Invalid JWT: {}", e.getMessage());
                handleAuthenticationError(response, "Invalid token");
                return;
            } catch (Exception e) {
                log.error("Authentication error", e);
                handleAuthenticationError(response, "Authentication failed");
                return;
            }

            filterChain.doFilter(request, response);
        }

        private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
        }

    }