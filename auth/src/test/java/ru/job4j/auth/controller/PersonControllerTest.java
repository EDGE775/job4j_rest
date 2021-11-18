package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.auth.AuthApplication;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthApplication.class)
@AutoConfigureMockMvc
public class PersonControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenGetAllPersonsThenStatus200() throws Exception {
        Person person1 = new Person("Login1", "Password1");
        Person person2 = new Person("Login2", "Password2");
        Mockito.when(repository.findAll()).thenReturn(List.of(person1, person2));
        mockMvc.perform(
                get("/person/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value("2"));
    }

    @Test
    public void whenGetPeronByIdThenStatus200() throws Exception {
        Person person = new Person("Login", "Password");
        Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.of(person));
        mockMvc.perform(
                get("/person/{id}", 0))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("Login"))
                .andExpect(jsonPath("$.password").value("Password"));
    }

    @Test
    public void whenGetPeronByIdThenStatus404() throws Exception {
        Mockito.when(repository.findById(Mockito.anyInt())).thenReturn(Optional.empty());
        mockMvc.perform(
                get("/person/{id}", 0))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenCreatePersonThenStatus201() throws Exception {
        Person person = new Person("Login", "Password");
        this.mockMvc.perform(
                post("/person/")
                        .content(objectMapper.writeValueAsString(person))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(repository).save(argument.capture());
        assertThat(argument.getValue().getLogin(), is("Login"));
        assertThat(argument.getValue().getPassword(), is("Password"));
    }

    @Test
    public void whenUpdatePersonThenStatus200() throws Exception {
        Person person = new Person("Login", "Password");
        person.setId(1);
        this.mockMvc.perform(
                put("/person/")
                        .content(objectMapper.writeValueAsString(person))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(repository).save(argument.capture());
        assertThat(argument.getValue().getId(), is(1));
        assertThat(argument.getValue().getLogin(), is("Login"));
        assertThat(argument.getValue().getPassword(), is("Password"));
    }

    @Test
    public void whenDeletePersonThenStatus200() throws Exception {
        mockMvc.perform(
                delete("/person/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk());
    }
}