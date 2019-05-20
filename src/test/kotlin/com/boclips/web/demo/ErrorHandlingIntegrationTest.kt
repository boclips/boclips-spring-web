package com.boclips.web.demo

import com.boclips.web.exceptions.ExceptionDetails
import com.boclips.web.exceptions.InvalidRequestApiException
import com.boclips.web.exceptions.ResourceNotFoundApiException
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.management.relation.RelationNotFoundException
import javax.validation.Valid
import javax.validation.constraints.NotNull

@RestController
class DummyController {

    @PostMapping("/validate")
    fun posting(@RequestBody @Valid validatingGarbage: ValidatingGarbage) {}

    @GetMapping("/not-found")
    fun notFound(): Nothing = throw ResourceNotFoundApiException("this thing", "cannot be found dude")

    @GetMapping("/invalid")
    fun invalid(): Nothing = throw InvalidRequestApiException(ExceptionDetails("error", "message", HttpStatus.CONFLICT))
}

data class ValidatingGarbage(
        @get:NotNull
        val mandatoryProperty: String? = null,
        @get:NotNull
        val otherMandatoryProperty: String? = null
)

@SpringBootTest
@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
class ErrorHandlingIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `enables error handling for javax validation`() {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/validate").contentType(MediaType.APPLICATION_JSON)
                        .content("""{}""")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.equalTo(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", Matchers.equalTo("/validate")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.equalTo("Invalid field/s")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.allOf(
                        Matchers.containsString("- mandatoryProperty must not be null"),
                        Matchers.containsString("- otherMandatoryProperty must not be null")
                )))
    }

    @Test
    fun `enables error handling for resource not found exceptions`() {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/not-found")
        )
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.equalTo(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", Matchers.equalTo("/not-found")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.equalTo("this thing")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo("cannot be found dude")))
    }

    @Test
    fun `enables error handling for invalid request exception`() {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/invalid")
        )
                .andExpect(MockMvcResultMatchers.status().isConflict)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.equalTo(409)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", Matchers.equalTo("/invalid")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.equalTo("error")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo("message")))
    }
}