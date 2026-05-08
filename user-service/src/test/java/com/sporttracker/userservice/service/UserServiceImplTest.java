package com.sporttracker.userservice.service;

import com.sporttracker.userservice.cache.UserCacheService;
import com.sporttracker.userservice.dto.LoginRequest;
import com.sporttracker.userservice.dto.LoginResponse;
import com.sporttracker.userservice.dto.RegisterRequest;
import com.sporttracker.userservice.model.User;
import com.sporttracker.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserCacheService userCacheService;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User storedUser;

    @BeforeEach
    void setUp() {
        storedUser = User.builder()
                .id("u1")
                .email("emir@test.com")
                .password("hashed123")
                .build();
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register: şifre encode edilerek kaydedilmeli")
    void register_shouldEncodePasswordBeforeSaving() {
        RegisterRequest request = new RegisterRequest("emirhan", "emir@test.com", "raw123");

        when(passwordEncoder.encode("raw123")).thenReturn("hashed123");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("u1");
            return u;
        });

        userService.register(request);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User captured = userCaptor.getValue();
        assertThat(captured.getPassword()).isEqualTo("hashed123");
        assertThat(captured.getEmail()).isEqualTo("emir@test.com");
        verify(passwordEncoder, times(1)).encode("raw123");
    }

    @Test
    @DisplayName("register: username ve email doğru şekilde atanmalı")
    void register_shouldMapUsernameAndEmail() {
        RegisterRequest request = new RegisterRequest("emirhan", "emir@test.com", "pass");

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.register(request);

        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("emir@test.com");
    }

    @Test
    @DisplayName("register: ham şifre asla kaydedilmemeli")
    void register_shouldNeverSaveRawPassword() {
        RegisterRequest request = new RegisterRequest("user1", "user1@test.com", "secret");

        when(passwordEncoder.encode("secret")).thenReturn("$2a$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        userService.register(request);

        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isNotEqualTo("secret");
        assertThat(userCaptor.getValue().getPassword()).startsWith("$2a$");
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login: geçerli email+şifre ile JWT token dönmeli")
    void login_shouldReturnTokenOnValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("emir@test.com");
        request.setPassword("raw123");

        when(userCacheService.findByEmail("emir@test.com")).thenReturn(storedUser);
        when(passwordEncoder.matches("raw123", "hashed123")).thenReturn(true);
        when(jwtService.generateToken(storedUser)).thenReturn("jwt.token.here");

        LoginResponse response = userService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt.token.here");
        assertThat(response.getEmail()).isEqualTo("emir@test.com");
        verify(jwtService, times(1)).generateToken(storedUser);
    }

    @Test
    @DisplayName("login: kullanıcı bulunamazsa exception fırlatmalı")
    void login_shouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@test.com");
        request.setPassword("pass");

        when(userCacheService.findByEmail("ghost@test.com")).thenReturn(null);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ghost@test.com");

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("login: yanlış şifre ile exception fırlatmalı")
    void login_shouldThrowWhenPasswordInvalid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("emir@test.com");
        request.setPassword("wrongpass");

        when(userCacheService.findByEmail("emir@test.com")).thenReturn(storedUser);
        when(passwordEncoder.matches("wrongpass", "hashed123")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Şifre hatalı");
    }

    @Test
    @DisplayName("login: şifre yanlışsa JWT üretilmemeli")
    void login_shouldNotGenerateTokenOnInvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("emir@test.com");
        request.setPassword("wrong");

        when(userCacheService.findByEmail("emir@test.com")).thenReturn(storedUser);
        when(passwordEncoder.matches("wrong", "hashed123")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(request)).isInstanceOf(RuntimeException.class);

        verify(jwtService, never()).generateToken(any());
    }
}
