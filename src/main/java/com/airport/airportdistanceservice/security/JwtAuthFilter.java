package com.airport.airportdistanceservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Sorğunun başlığından (Header) "Authorization" məlumatını oxuyuruq
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Əgər Header boşdursa və ya "Bearer " sözü ilə başlamırsa, deməli token yoxdur.
        // Bu halda sorğunu bloklamırıq, sadəcə zəncirdəki digər filtrlərə (və ya endpointlərə) ötürürük.
        // Qeydiyyat/Login kimi açıq yollar (permitAll) beləcə işləyə bilir.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " sözünü (ilk 7 simvolu) kəsib atırıq ki, əlimizdə yalnız xalis token qalsın
        jwt = authHeader.substring(7);

        // 4. JwtService vasitəsilə tokendən istifadəçinin emailini (subject) çıxarırıq
        userEmail = jwtService.extractEmail(jwt);

        // 5. Əgər email tapıldısa və istifadəçi bu sorğu çərçivəsində hələ təsdiqlənməyibsə (SecurityContext boşdursa)
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Verilənlər bazasından istifadəçinin bütün məlumatlarını (rolu, şifrəsi və s.) gətiririk
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 6. Tokenin riyazi cəhətdən düzgünlüyünü və vaxtının keçib-keçmədiyini yoxlayırıq
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 7. Token təmizdirsə, Spring Security üçün xüsusi təsdiq vərəqəsi (AuthenticationToken) yaradırıq
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Parol burada lazım deyil, çünki tokeni artıq təsdiqləmişik
                        userDetails.getAuthorities()
                );

                // Sorğunun detallarını (IP adresi, session id və s.) əlavə edirik
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 8. Ən vacib addım: Təsdiq vərəqəsini Spring-in Təhlükəsizlik Sisteminə (SecurityContext) qoyuruq.
                // Artıq server bu istifadəçinin kim olduğunu bilir.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Nəhayət, sorğunu filtrdən çıxarıb tələb olunan endpoint-ə (məsələn, Controller-ə) göndəririk
        filterChain.doFilter(request, response);
    }
}