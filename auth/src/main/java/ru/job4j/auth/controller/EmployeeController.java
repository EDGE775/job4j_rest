package ru.job4j.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.auth.domain.Employee;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.EmployeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private RestTemplate rest;

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    private static final String API = "http://localhost:8080/person/";

    private static final String API_ID = "http://localhost:8080/person/{id}";

    @GetMapping("/")
    public List<Employee> findAll() {
        return StreamSupport.stream(
                this.employeeRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById(@PathVariable int id) {
        Optional<Employee> employee = this.employeeRepository.findById(id);
        return new ResponseEntity<>(
                employee.orElse(new Employee()),
                employee.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/")
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        return new ResponseEntity<>(
                this.employeeRepository.save(employee),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Employee employee) {
        this.employeeRepository.save(employee);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Employee employee = employeeRepository.findById(id).get();
        this.employeeRepository.delete(employee);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/person")
    public ResponseEntity<Person> addPerson(@RequestBody Person person,
                                            @PathVariable int id) {
        Employee employee = this.employeeRepository.findById(id).get();
        Person savedPerson = rest.postForObject(API, person, Person.class);
        employee.addPerson(savedPerson);
        employeeRepository.save(employee);
        return new ResponseEntity<>(
                savedPerson,
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}/person")
    public ResponseEntity<List<Person>> findAllPerson(@PathVariable int id) {
        Optional<Employee> employee = this.employeeRepository.findById(id);
        List<Person> persons = employee.isPresent() ? employee.get().getPersons() : new ArrayList<>();
        return new ResponseEntity<>(persons,
                employee.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{emp-id}/person/{per-id}")
    public ResponseEntity<Void> deletePerson(@PathVariable(name = "per-id") int perId,
                                             @PathVariable(name = "emp-id") int empId) {
        Employee employee = this.employeeRepository.findById(empId).get();
        employee.setPersons(employee.getPersons()
                .stream()
                .filter(per -> per.getId() != perId)
                .collect(Collectors.toList()));
        employeeRepository.save(employee);
        rest.delete(API_ID, perId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{emp-id}/person")
    public ResponseEntity<Void> updatePerson(@RequestBody Person person,
                                             @PathVariable(name = "emp-id") int empId) {
        Employee employee = this.employeeRepository.findById(empId).get();
        if (employee.getPersons()
                .stream().map(x -> x.getId()).toList()
                .contains(person.getId())) {
            rest.put(API, person);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
