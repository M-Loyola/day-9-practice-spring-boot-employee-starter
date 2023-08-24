package com.afs.restapi.service;

import com.afs.restapi.entity.Employee;
import com.afs.restapi.exception.EmployeeCreateException;
import com.afs.restapi.exception.EmployeeNotFoundException;
import com.afs.restapi.exception.EmployeeUpdateException;
import com.afs.restapi.repository.EmployeeJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeJpaRepository employeeJpaRepository;

    public EmployeeService(EmployeeJpaRepository employeeJpsRepository) {
        this.employeeJpaRepository = employeeJpsRepository;
    }

    public List<Employee> findAll() {
        return employeeJpaRepository.findAll();
    }

    public Employee findById(Long id) {
        return employeeJpaRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);
    }

    public Employee update(Long id, Employee employee) {
        Employee toBeUpdatedEmployee = findById(id);
        if (Boolean.TRUE.equals(toBeUpdatedEmployee.isInactive())) {
            throw new EmployeeUpdateException();
        }
        updateEmployeeFields(toBeUpdatedEmployee, employee);
        employeeJpaRepository.save(toBeUpdatedEmployee);
        return toBeUpdatedEmployee;
    }

    private void updateEmployeeFields(Employee toBeUpdatedEmployee, Employee employee) {
        if (employee.getSalary() != null) {
            toBeUpdatedEmployee.setSalary(employee.getSalary());
        }
        if (employee.getAge() != null) {
            toBeUpdatedEmployee.setAge(employee.getAge());
        }
    }

    public List<Employee> findAllByGender(String gender) {
        return employeeJpaRepository.findAllByGender(gender);
    }

    public Employee create(Employee employee) {
        if (employee.hasInvalidAge()) {
            throw new EmployeeCreateException();
        }
        return employeeJpaRepository.save(employee);
    }

    public List<Employee> findByPage(Integer pageNumber, Integer pageSize) {
        return employeeJpaRepository.findAll(PageRequest.of(pageNumber - 1, pageSize)).toList();
    }

    public void delete(Long id) {
        employeeJpaRepository.deleteById(id);
    }
}
