package com.vehiclerental;

import com.vehiclerental.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:auth-flow-test;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.h2.console.enabled=false"
})
class AuthFlowIntegrationTest {

    private static final Pattern CSRF_INPUT_PATTERN = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerThenLogin_withSameCredentials_shouldAuthenticate() throws Exception {
        String email = "flow.user@example.com";
        String rawPassword = "secret123";

        MvcResult registerPageResult = mockMvc.perform(get("/auth/register"))
            .andExpect(status().isOk())
            .andReturn();

        String registerHtml = registerPageResult.getResponse().getContentAsString();
        String registerCsrf = extractCsrfToken(registerHtml);
        MockHttpSession registerSession = (MockHttpSession) registerPageResult.getRequest().getSession(false);
        assertThat(registerSession).isNotNull();

        mockMvc.perform(post("/auth/register")
                .session(Objects.requireNonNull(registerSession))
                .param("_csrf", registerCsrf)
                .param("name", "Flow User")
                .param("email", email)
                .param("password", rawPassword)
                .param("phone", "9999999999")
                .param("role", "CUSTOMER"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/auth/login"));

        var createdUser = userRepository.findByEmail(email).orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getPassword()).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, createdUser.getPassword())).isTrue();

        MvcResult loginPageResult = mockMvc.perform(get("/auth/login"))
            .andExpect(status().isOk())
            .andReturn();

        String loginHtml = loginPageResult.getResponse().getContentAsString();
        String loginCsrf = extractCsrfToken(loginHtml);

        MockHttpSession loginSession = (MockHttpSession) loginPageResult.getRequest().getSession(false);
        assertThat(loginSession).isNotNull();

        MvcResult loginResult = mockMvc.perform(post("/login")
                .session(Objects.requireNonNull(loginSession))
                .param("_csrf", loginCsrf)
                .param("username", email)
                .param("password", rawPassword))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/dashboard"))
            .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertThat(session).isNotNull();
        MockHttpSession authenticatedSession = Objects.requireNonNull(session);

        mockMvc.perform(get("/dashboard").session(authenticatedSession))
            .andExpect(status().isOk());
    }

    private String extractCsrfToken(String html) {
        Matcher matcher = CSRF_INPUT_PATTERN.matcher(html);
        assertThat(matcher.find()).as("CSRF hidden input should be present").isTrue();
        return matcher.group(1);
    }
}
