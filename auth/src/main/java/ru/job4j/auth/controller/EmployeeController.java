package ru.job4j.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.auth.domain.Employee;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.EmployeeRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private RestTemplate rest;

    private static final String acc_API = "http://localhost:8080/person/";

    private static final String acc_API_ID = "http://localhost:8080/person/{id}";

    private final EmployeeRepository employees;

    public EmployeeController(EmployeeRepository employees) {
        this.employees = employees;
    }

    @GetMapping("/")
    public List<Employee> findAll() {
        return StreamSupport.stream(this.employees.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById(@PathVariable int id) {
        var employee = employees.findById(id);
        return new ResponseEntity<Employee>(employee.orElse(new Employee()),
                employee.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Set<Person>> employeeAccounts(@PathVariable int id) {
        var employee = this.employees.findById(id);
        Set<Person> rsl = employee.isPresent() ? employee.get().getAccounts() : new HashSet<>();
        return new ResponseEntity<>(rsl, employee.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<Person> accountsById(@PathVariable int id) {
        Person person;
        try {
            person = this.rest.getForObject(acc_API_ID, Person.class, id);
        } catch (Exception e) {
            person = new Person();
        }
        return new ResponseEntity<>(person, person.getId() != 0 ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping("/")
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return new ResponseEntity<Employee>(employees.save(employee), HttpStatus.CREATED);
    }

    @PostMapping("/account/")
    public ResponseEntity<Person> addAccount(@RequestBody Person person) {
        Person rsl = rest.postForObject(acc_API, person, Person.class);
        return new ResponseEntity<>(rsl, HttpStatus.CREATED);
    }

    @PutMapping("/account/")
    public ResponseEntity<Void> updateAccount(@RequestBody Person person) {
        rest.put(acc_API, person);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Employee employee) {
        employees.save(employee);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Employee employee = new Employee();
        employee.setId(id);
        employees.delete(employee);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/account/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable int id) {
        rest.delete(acc_API, id);
        return ResponseEntity.ok().build();
    }
}
