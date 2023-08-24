package com.afs.restapi.service;

import com.afs.restapi.entity.Company;
import com.afs.restapi.entity.Employee;
import com.afs.restapi.repository.CompanyJpaRepository;
import com.afs.restapi.repository.EmployeeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompanyServiceTest {
    private CompanyService companyService;
    private CompanyJpaRepository mockedCompanyRepository;
    private EmployeeJpaRepository mockedEmployeeRepository;
    @BeforeEach
    void setUp() {
        mockedCompanyRepository = mock(CompanyJpaRepository.class);
        mockedEmployeeRepository = mock(EmployeeJpaRepository.class);
        companyService = new CompanyService(mockedCompanyRepository, mockedEmployeeRepository);
    }
    @Test
    void should_return_all_companies_when_get_companies_given_companies_service() {
        // Given
        Company company = new Company("JavaCom");
        List<Company> companies = List.of(company);
        when(mockedCompanyRepository.findAll()).thenReturn(companies);

        // When
        List<Company> allCompanies = companyService.findAll();

        // Then
        assertEquals(allCompanies.get(0).getId(), company.getId());
        assertEquals(allCompanies.get(0).getName(), company.getName());
    }
    @Test
    void should_return_company_when_get_company_given_company_service_and_company_id() {
        // Given
        Company company = new Company("JavaCom");
        when(mockedCompanyRepository.findById(company.getId())).thenReturn(Optional.of(company));

        // When
        Company foundCompany = companyService.findById(company.getId());

        // Then
        assertEquals(company.getId(), foundCompany.getId());
        assertEquals(company.getName(), foundCompany.getName());
    }
    @Test
    void should_return_employees_company_when_get_find_employees_by_company_id_given_company_service() {
        // Given
        Company company = new Company(1L, "JavaCom");
        Employee alice = new Employee(1L, "Sam", 24, "Female", 9000);
        alice.setCompanyId(company.getId());

        List<Employee> employees = List.of(alice);
        when(mockedEmployeeRepository.findByCompanyId(company.getId())).thenReturn(employees);

        // When
        List<Employee> foundEmployees = companyService.findEmployeesByCompanyId(company.getId());

        // Then
        assertEquals(foundEmployees.get(0).getId(), alice.getId());
        assertEquals(foundEmployees.get(0).getName(), alice.getName());
        assertEquals(foundEmployees.get(0).getAge(), alice.getAge());
        assertEquals(foundEmployees.get(0).getGender(), alice.getGender());
        assertEquals(foundEmployees.get(0).getSalary(), alice.getSalary());
    }
    @Test
    void should_return_company_when_create_given_company_service_and_company() {
        // Given
        Company company = new Company("JavaCom");
        Company savedCompany = new Company(1L, "JavaCom");
        when(mockedCompanyRepository.save(company)).thenReturn(savedCompany);

        // When
        Company companyResponse = companyService.create(company);

        // Then
        assertEquals(savedCompany.getId(), companyResponse.getId());
        assertEquals("JavaCom", companyResponse.getName());
    }
    @Test
    void should_return_updated_company_when_update_given_company() {
        // Given
        Company company = new Company(1L, "JavaCom");
        Company updatedCompanyInfo = new Company(1L, "Spring Boot Com");
        when(mockedCompanyRepository.findById(company.getId())).thenReturn(Optional.of(company));
        when(mockedCompanyRepository.save(any(Company.class))).thenReturn(updatedCompanyInfo);

        // When
        Company updatedCompany = companyService.update(company.getId(), updatedCompanyInfo);

        // Then
        assertEquals(updatedCompanyInfo.getId(), updatedCompany.getId());
        assertEquals(updatedCompanyInfo.getName(), updatedCompany.getName());
    }
}
