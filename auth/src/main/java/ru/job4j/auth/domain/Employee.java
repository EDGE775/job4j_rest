package ru.job4j.auth.domain;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Table(name = "employee")
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    private int inn;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date(System.currentTimeMillis());

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_person",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    List<Person> persons;

    public void addPerson(Person person) {
        persons.add(person);
    }

    public static Employee of(String firstName, String secondName, int inn) {
        Employee employee = new Employee();
        employee.firstName = firstName;
        employee.secondName = secondName;
        employee.inn = inn;
        return employee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public int getInn() {
        return inn;
    }

    public void setInn(int inn) {
        this.inn = inn;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
