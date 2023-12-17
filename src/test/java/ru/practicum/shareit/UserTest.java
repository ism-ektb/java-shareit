package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    public static final String path = "/users";

    /**
     * дополнительные тесты к сущности user
     */
    @Test
    void createAndPatchGoodAndBadEmailThenPatchUserWithOtherId() throws Exception {
        // создаем пользователя с id 1
        mockMvc.perform(MockMvcRequestBuilders.post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pathConvertToString("in/good_user.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(pathConvertToString("out/good_user.json")));

        //меняем email юзера 1
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pathConvertToString("in/patch_good_email.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(pathConvertToString("out/patch_good_email.json")));

        //пытаемся заменить емайл на невалидный
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pathConvertToString("in/patch_bad_email.json")))
                .andExpect(MockMvcResultMatchers.status().is(400));

        //создаем юзера с id 2
        mockMvc.perform(MockMvcRequestBuilders.post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pathConvertToString("in/good_user.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(pathConvertToString("out/good_user_2.json")));

        //патчим юзера с id 1 (поле id не пустое и равно 1)
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pathConvertToString("in/patch_user_other_id.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(pathConvertToString("out/next_id1.json")));

        //пытаемся патчить юзера с id 2 (поле id не пустое и равно 1)
        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pathConvertToString("in/patch_good_email.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(pathConvertToString("out/patch_good_email.json")));

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pathConvertToString("in/patch_user_other_id.json")))
                .andExpect(MockMvcResultMatchers.status().is(500));
    }

    private String pathConvertToString(String filename) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + filename).toPath(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
