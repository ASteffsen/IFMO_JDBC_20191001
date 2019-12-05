package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DaoFactory {
    private Employee makeEmp(ResultSet resultSet) {
        try {
            BigInteger id = new BigInteger(resultSet.getString("ID"));
            FullName fullName = new FullName(resultSet.getString("FIRSTNAME"), resultSet.getString("LASTNAME"), resultSet.getString("MIDDLENAME"));
            Position position = Position.valueOf(resultSet.getString("POSITION"));
            LocalDate hireDate = LocalDate.parse(resultSet.getString("HIREDATE"));
            BigDecimal salary = resultSet.getBigDecimal("SALARY");
            BigInteger managerId = BigInteger.valueOf(resultSet.getInt("MANAGER"));
            BigInteger departmentId = BigInteger.valueOf(resultSet.getInt("DEPARTMENT"));

            Employee emp = new Employee(id, fullName, position, hireDate, salary, managerId, departmentId);
            return emp;
        }
        catch (SQLException e) {
            return null;
        }
    }

    private Department makeDep(ResultSet resultSet) {
        try {
            BigInteger id = new BigInteger(resultSet.getString("ID"));
            String name = resultSet.getString("NAME");
            String location = resultSet.getString("LOCATION");
            Department dep = new Department(id, name, location);
            return dep;
        }
        catch (SQLException e) {
            return null;
        }
    }

    private ResultSet exeSQL(String request) {
        try {
            return ConnectionSource.instance().createConnection().createStatement().executeQuery(request);
        }
        catch (SQLException e){
            return null;
        }
    }

    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                try {
                    ResultSet resultSet = exeSQL("SELECT * FROM employee WHERE department = " + department.getId());

                    List<Employee> result = new ArrayList<>();
                    while (resultSet.next()) {
                        result.add(makeEmp(resultSet));
                    }

                    return result;
                }
                catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                try {
                    ResultSet resultSet = exeSQL("SELECT * FROM employee WHERE manager = " + employee.getId());

                    List<Employee> result = new ArrayList<>();
                    while (resultSet.next()) {
                        result.add(makeEmp(resultSet));
                    }

                    return result;
                }
                catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = exeSQL("SELECT * FROM employee WHERE id = " + Id.toString());

                    if (resultSet.next())
                        return Optional.of(makeEmp(resultSet));
                    else
                        return Optional.empty();
                }
                catch (SQLException e) {
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getAll() {
                try {
                    ResultSet resultSet = exeSQL("SELECT * FROM employee");

                    List<Employee> result = new ArrayList<>();
                    while (resultSet.next()) {
                        result.add(makeEmp(resultSet));
                    }

                    return result;
                }
                catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public Employee save(Employee employee) {
                try {
                    exeSQL(
                            "INSERT INTO employee VALUES ('"
                                    + employee.getId() + "', '"
                                    + employee.getFullName().getFirstName() + "', '"
                                    + employee.getFullName().getLastName() + "', '"
                                    + employee.getFullName().getMiddleName() + "', '"
                                    + employee.getPosition() + "', '"
                                    + employee.getManagerId() + "', '"
                                    + Date.valueOf(employee.getHired()) + "', '"
                                    + employee.getSalary() + "', '"
                                    + employee.getDepartmentId() + "')"
                    );
                    return employee;
                }
                catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public void delete(Employee employee) {
                try {
                    ConnectionSource.instance().createConnection().createStatement().execute("DELETE FROM employee WHERE ID = " + employee.getId().toString());
                }
                catch (SQLException e) {
                    System.out.println("delete error");
                }
            }
        };
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                try {
                    ResultSet resultSet = exeSQL("SELECT * FROM department WHERE id = " + Id.toString());

                    if (resultSet.next())
                        return Optional.of(makeDep(resultSet));
                    else
                        return Optional.empty();
                }
                catch (SQLException e) {
                    return Optional.empty();
                }

            }

            @Override
            public List<Department> getAll() {
                try {
                    ResultSet resultSet = exeSQL("SELECT * FROM department");

                    List<Department> result = new ArrayList<>();
                    while (resultSet.next()) {
                        result.add(makeDep(resultSet));
                    }

                    return result;
                }
                catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public Department save(Department department) {
                try {
                    if (getById(department.getId()).equals(Optional.empty())) {
                        exeSQL(
                                "INSERT INTO department VALUES ('" +
                                        department.getId()       + "', '" +
                                        department.getName()     + "', '" +
                                        department.getLocation() + "')"
                        );
                    } else {
                        exeSQL(
                                "UPDATE department SET " +
                                        "NAME = '"     + department.getName()     + "', " +
                                        "LOCATION = '" + department.getLocation() + "' " +
                                        "WHERE ID = '" + department.getId()       + "'"
                        );
                    }
                    return department;
                }
                catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public void delete(Department department) {
                try {
                    ConnectionSource.instance().createConnection().createStatement().execute("DELETE FROM department WHERE ID = " + department.getId().toString());
                }
                catch (SQLException e) {}
            }
        };
    }
}