package com.afs.restapi.service;

import com.afs.restapi.entity.Employee;
import com.afs.restapi.exception.EmployeeCreateException;
import com.afs.restapi.exception.EmployeeUpdateException;
import com.afs.restapi.repository.EmployeeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    private EmployeeService employeeService;
    @Mock
    private EmployeeJpaRepository employeeJpaRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        employeeService = new EmployeeService(employeeJpaRepository);
    }
    @Test
    void should_return_all_employees_when_get_employees_given_employee_service() {
        // Given
        Employee employee = new Employee(1L, "Lucy", 20, "Female", 3000);
        List<Employee> employees = List.of(employee);
        when(employeeJpaRepository.findAll()).thenReturn(employees);

        // When
        List<Employee> allEmployees = employeeService.findAll();

        // Then
        assertEquals(allEmployees.get(0).getId(), employee.getId());
        assertEquals(allEmployees.get(0).getName(), employee.getName());
        assertEquals(allEmployees.get(0).getAge(), employee.getAge());
        assertEquals(allEmployees.get(0).getGender(), employee.getGender());
        assertEquals(allEmployees.get(0).getSalary(), employee.getSalary());
    }

    @Test
    void should_return_the_employee_when_get_employee_given_employee_service_and_an_employee_id() {
        // Given
        Employee employee = new Employee(1L, "Lucy", 20, "Female", 3000);
        when(employeeJpaRepository.findById(employee.getId())).thenReturn(java.util.Optional.of(employee));

        // When
        Employee foundEmployee = employeeService.findById(employee.getId());

        // Then
        assertEquals(employee.getId(), foundEmployee.getId());
        assertEquals(employee.getName(), foundEmployee.getName());
        assertEquals(employee.getAge(), foundEmployee.getAge());
        assertEquals(employee.getGender(), foundEmployee.getGender());
        assertEquals(employee.getSalary(), foundEmployee.getSalary());
    }

    @Test
    void should_return_employees_by_given_gender_when_get_employees_given_employee_service() {
        // Given
        Employee alice = new Employee(null, "Alice", 24, "Female", 9000);
        List<Employee> employees = List.of(alice);
        when(employeeJpaRepository.findAllByGender(anyString())).thenReturn(employees);

        // When
        List<Employee> foundEmployees = employeeService.findAllByGender("Female");

        // Then
        assertEquals(employees.size(), foundEmployees.size());
        assertEquals(alice.getId(), foundEmployees.get(0).getId());
        assertEquals(alice.getName(), foundEmployees.get(0).getName());
        assertEquals(alice.getAge(), foundEmployees.get(0).getAge());
        assertEquals(alice.getGender(), foundEmployees.get(0).getGender());
        assertEquals(alice.getSalary(), foundEmployees.get(0).getSalary());
    }

    @Test
    void should_return_created_active_employee_when_create_given_employee_service_and_employee_with_valid_age() {
        // Given
        Employee employee = new Employee(null, "Lucy", 20, "Female", 3000);
        Employee savedEmployee = new Employee(1L, "Lucy", 20, "Female", 3000);
        when(employeeJpaRepository.save(employee)).thenReturn(savedEmployee);

        // When
        Employee employeeResponse = employeeService.create(employee);

        // Then
        assertEquals(savedEmployee.getId(), employeeResponse.getId());
        assertEquals("Lucy", employeeResponse.getName());
        assertEquals(20, employeeResponse.getAge());
        assertEquals("Female", employeeResponse.getGender());
        assertEquals(3000, employeeResponse.getSalary());
        verify(employeeJpaRepository).save(argThat(tempEmployee -> {
            assertEquals("Lucy", tempEmployee.getName());
            assertEquals(20, tempEmployee.getAge());
            assertEquals("Female", tempEmployee.getGender());
            assertEquals(3000, tempEmployee.getSalary());
            return true;
        }));
    }

    @Test
    void should_throw_exception_when_create_given_employee_service_and_employee_whose_age_is_less_than_18() {
        // Given
        Employee employee = new Employee(null, "Lucy", 16, "Female", 3000);

        // When, Then
        EmployeeCreateException employeeCreateException = assertThrows(EmployeeCreateException.class, () -> {
            employeeService.create(employee);
        });
        assertEquals("Employee must be 18-65 years old", employeeCreateException.getMessage());
    }

    @Test
    void should_return_inactive_employee_when_delete_given_employee_service_and_active_employee() {
        // Given
        Employee employee = new Employee(null, "Lucy", 20, "Female", 3000);
        employee.setActive(Boolean.TRUE);
        when(employeeJpaRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        // When
        employeeService.delete(employee.getId());

        // Then
        verify(employeeJpaRepository).deleteById(employee.getId());
    }

    @Test
    void should_return_updated_employee_when_update_given_employee_age_and_salary() {
        // Given
        Employee employee = new Employee(1L, "Lucy", 20, "Female", 3000);
        employee.setActive(Boolean.TRUE);
        Employee updatedEmployeeInfo = new Employee(null, null, 30, null, 10000);
        when(employeeJpaRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        // When
        Employee updatedEmployee = employeeService.update(employee.getId(), updatedEmployeeInfo);

        // Then
        assertEquals("Lucy", updatedEmployee.getName());
        assertEquals("Female", updatedEmployee.getGender());
        assertEquals(30, updatedEmployee.getAge());
        assertEquals(10000, updatedEmployee.getSalary());
        verify(employeeJpaRepository).save(updatedEmployee);
    }

    @Test
    void should_throw_exception_when_update_given_employee_service_and_inactive_employee_and_age_and_salary() {
        // Given
        Employee employee = new Employee(null, "Lucy", 20, "Female", 3000);
        employee.setActive(Boolean.FALSE);
        Employee updatedEmployeeInfo = new Employee(null, "Lucy", 30, "Female", 10000);
        when(employeeJpaRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        // When, Then
        EmployeeUpdateException employeeUpdateException = assertThrows(EmployeeUpdateException.class, () ->
                employeeService.update(employee.getId(), updatedEmployeeInfo));
        assertEquals("Employee is inactive", employeeUpdateException.getMessage());
    }

//    @Test
//    void should_paged_employees_when_get_employees_by_page_given_employee_service_and_pageNumber_and_pageSize() {
//        // Given
//        Employee alice = new Employee(null, "Alice", 24, "Female", 9000);
//        List<Employee> employees = List.of(alice);
//        when(mockedEmployeeRepository.listEmployeesByPage(anyLong(), anyLong())).thenReturn(employees);
//
//        // When
//        List<Employee> pagedEmployees = employeeService.getEmployeesByPage(1L, 1L);
//
//        // Then
//        assertEquals(pagedEmployees.get(0).getId(), alice.getId());
//        assertEquals(pagedEmployees.get(0).getName(), alice.getName());
//        assertEquals(pagedEmployees.get(0).getAge(), alice.getAge());
//        assertEquals(pagedEmployees.get(0).getGender(), alice.getGender());
//        assertEquals(pagedEmployees.get(0).getSalary(), alice.getSalary());
//    }
}