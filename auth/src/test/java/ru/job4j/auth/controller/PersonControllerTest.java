package ru.job4j.auth.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.job4j.auth.AuthApplication;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@SpringBootTest(classes = AuthApplication.class)
@AutoConfigureMockMvc
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository persons;

    @Test
    public void shouldReturnTwoPersonsInJson() throws Exception {
        Person one = new Person();
        one.setId(1);
        one.setLogin("one");
        one.setPassword("1");
        Person two = new Person();
        two.setId(2);
        two.setLogin("two");
        two.setPassword("2");
        Mockito.when(persons.findAll()).thenReturn(List.of(one, two));
        this.mockMvc.perform(get("/person/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].login", containsInAnyOrder("one", "two")))
                .andExpect(jsonPath("$[*].password", containsInAnyOrder("1", "2")));
    }

    @Test
    public void shouldReturnPersonInJson() throws Exception {
        Person one = new Person();
        one.setId(1);
        one.setLogin("one");
        one.setPassword("1");
        Mockito.when(persons.findById(1)).thenReturn(Optional.of(one));
        this.mockMvc.perform(get("/person/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("one"))
                .andExpect(jsonPath("$.password").value("1"));
    }

    @Test
    public void shouldReturnPersonSavedFromJsonWithGenID() throws Exception {
        Person preone = new Person();
        preone.setId(0);
        preone.setLogin("one");
        preone.setPassword("1");
        Person one = new Person();
        one.setId(1);
        one.setLogin("one");
        one.setPassword("1");
        Mockito.when(persons.save(preone)).thenReturn(one);
        String person = "{\"id\":\"0\",\"login\":\"one\",\"password\":\"1\"}";
        this.mockMvc.perform(post("/person/")
                .contentType(MediaType.APPLICATION_JSON).content(person))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.login").value("one"))
                .andExpect(jsonPath("$.password").value("1"));
    }

    @Test
    public void shouldUpdatePersonFromJson() throws Exception {
        String person = "{\"id\":\"1\",\"login\":\"one\",\"password\":\"1\"}";
        this.mockMvc.perform(MockMvcRequestBuilders.put("/person/")
                .contentType(MediaType.APPLICATION_JSON).content(person))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(persons).save(argument.capture());
        assertThat(argument.getValue().getId(), is(1));
        assertThat(argument.getValue().getLogin(), is("one"));
        assertThat(argument.getValue().getPassword(), is("1"));
    }

    @Test
    public void shouldDeletePersonFromJson() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/person/1"))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
        verify(persons).delete(argument.capture());
        assertThat(argument.getValue().getId(), is(1));
    }



}