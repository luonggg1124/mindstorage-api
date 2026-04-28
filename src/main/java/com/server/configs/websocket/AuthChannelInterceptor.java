package com.server.configs.websocket;

import java.util.Collections;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.server.exceptions.UnauthorizedException;
import com.server.models.entities.User;
import com.server.services.auth.AuthService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {
    private final AuthService authService;


    public Message<?> preSend(
        Message<?> message,
        MessageChannel channel
    ){
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())){
            String authorization = accessor.getFirstNativeHeader("Authorization");
            if(authorization == null || !authorization.startsWith("Bearer ")){
                throw new UnauthorizedException("UNAUTHORIZED");
            }
            String token = authorization.substring(7);
            User user = authService.userFromToken(token);
            if(user == null){
                throw new UnauthorizedException("UNAUTHORIZED");
            }
            // Use userId as Principal name so /user destinations match convertAndSendToUser(userId.toString(), ...)
            String principalName = user.getId().toString();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principalName, null, Collections.emptyList());
            authentication.setDetails(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            accessor.setUser(authentication);
        }
        return message;
    }
}
