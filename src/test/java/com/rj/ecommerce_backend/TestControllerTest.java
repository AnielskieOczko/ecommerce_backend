package com.rj.ecommerce_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
class TestControllerTest {
    @Autowired
    private MockMvc mockMvc; // Mock the web environment

    @Test
    public void helloSpring_shouldReturnExpectedString() throws Exception {
        // Perform a GET request to /test
        String response = mockMvc.perform(get("/test"))
                .andExpect(status().isOk()) // Expect a 200 OK status
                .andExpect(content().string("hello spring")) // Expect the response body to be "hello spring"
                .andReturn().getResponse().getContentAsString();

        // Assert using AssertJ
        assertThat(response).isEqualTo("hello spring");
    }

    @Test
    public void helloSpring_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk());

    }
}