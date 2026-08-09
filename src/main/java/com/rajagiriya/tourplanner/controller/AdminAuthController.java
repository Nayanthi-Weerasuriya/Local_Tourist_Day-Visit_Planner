package com.rajagiriya.tourplanner.controller;

import com.rajagiriya.tourplanner.dto.ApiMessageResponse;
import com.rajagiriya.tourplanner.dto.LoginRequest;
import com.rajagiriya.tourplanner.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;

    public AdminAuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername().trim(), request.getPassword())
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);

        return new LoginResponse(
                authentication.getName(),
                authentication.getAuthorities().stream().findFirst().map(Object::toString).orElse("ROLE_ADMIN"),
                "Login successful."
        );
    }

    @GetMapping("/me")
    public LoginResponse me(Authentication authentication) {
        return new LoginResponse(
                authentication.getName(),
                authentication.getAuthorities().stream().findFirst().map(Object::toString).orElse("ROLE_ADMIN"),
                "Authenticated."
        );
    }

    @PostMapping("/logout")
    public ApiMessageResponse logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        response.setHeader("Clear-Site-Data", "\"cookies\"");
        return new ApiMessageResponse(authentication != null ? "Logged out successfully." : "Session cleared.");
    }
}
