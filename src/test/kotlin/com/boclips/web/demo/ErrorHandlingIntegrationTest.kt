package com.boclips.web.demo

import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotNull

@RestController
class DummyController {

    @PostMapping("/validate")
    fun posting(@RequestBody @Valid validatingGarbage: ValidatingGarbage) {}
}

data class ValidatingGarbage(
        @get:NotNull
        val mandatoryProperty: String? = null
)

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
class ErrorHandlingIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `enables error handling`() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/validate").contentType(MediaType.APPLICATION_JSON)
                        .content("""{}""")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].timestamp", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].status", Matchers.equalTo(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].path", Matchers.equalTo("/validate")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].error", Matchers.equalTo("Invalid field: mandatoryProperty")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", Matchers.equalTo("must not be null")))
    }
}