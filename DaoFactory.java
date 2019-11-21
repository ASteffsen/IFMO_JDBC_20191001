package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.sql.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class DaoFactory {
    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getAll() {
                try {
                    ResultSet res = exeReq(
                            "SELECT * FROM employee");
                    List<Employee> resList = new ArrayList<>();
                    while (res.next()) {
                        resList.add(makeEmployee(res));
                    }
                    return resList;
                } catch (SQLException e) {
                    return null;
                }
            }
            
            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    ResultSet res = exeReq(
                            "SELECT * FROM employee WHERE id = " + Id.toString());

                    if (res.next())
                        return Optional.of(makeEmployee(res));
                    else
                        return Optional.empty();
                } catch (SQLException e) {
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getByDepartment(Department department) {
                try {
                    ResultSet res = exeReq(
                            "SELECT * FROM employee WHERE department = " + department.getId());
                    List<Employee> resList = new ArrayList<>();
                    while (res.next()) {
                        resList.add(makeEmployee(res));
                    }
                    return resList;
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                try {
                    ResultSet res = exeReq(
                            "SELECT * FROM employee WHERE manager = " + employee.getId());
                    List<Employee> resList = new ArrayList<>();
                    while (res.next()) {
                        resList.add(makeEmployee(res));
                    }

                    return resList;
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public Employee save(Employee emp) {
                try {
                    PreparedStatement pSt = ConnectionSource.instance().createConnection().prepareStatement("INSERT INTO employee VALUES (?,?,?,?,?,?,?,?,?)");

                    pSt.setInt(1, emp.getId().intValue());
                    pSt.setString(2, emp.getFullName().getFirstName());
                    pSt.setString(3, emp.getFullName().getLastName());
                    pSt.setString(4, emp.getFullName().getMiddleName());
                    pSt.setString(5, emp.getPosition().toString());
                    pSt.setInt(6, emp.getManagerId().intValue());
                    pSt.setDate(7, Date.valueOf(emp.getHired()));
                    pSt.setDouble(8,
                            emp.getSalary().doubleValue());
                    pSt.setInt(9, emp.getDepartmentId().intValue());

                    pSt.executeUpdate();
                    return emp;
                } catch (SQLException e) {
                    return null;
                }
            }

            @Override
            public void delete(Employee emp) {
                try {
                    ConnectionSource.instance().createConnection().createStatement().execute(
                            "DELETE FROM employee WHERE ID = " + emp.getId().toString());
                } catch (SQLException e) {
                    System.out.println("ERROR IN DELETE");
                }
            }
        };
    }

    public DepartmentDao departmentDAO() {
       //throw new UnsupportedOperationException();
        return new DepartmentDao(){
            @Override
            public List<Department> getAll() {
                try {
                    ResultSet res = exeReq(
                            "SELECT * FROM department");

                    List<Department> resList = new ArrayList<>();
                    while (res.next()) {
                        resList.add(makeDep(res));
                    }

                    return resList;
                }
                catch (SQLException e) {
                    return null;
                }
            }
            @Override
            public Optional<Department> getById(BigInteger Id) {
                try {
                    ResultSet res = exeReq(
                            "SELECT * FROM department WHERE id = "
                                    + Id.toString());

                    if (res.next())
                        return Optional.of(makeDep(res));
                    else
                        return Optional.empty();
                }
                catch (SQLException e) {
                    return Optional.empty();
                }
            }
            @Override
            public Department save(Department dep) {
                try {
                    PreparedStatement pSt;
                    if (getById(dep.getId()).equals(Optional.empty())) {
                        pSt = ConnectionSource.instance().createConnection().prepareStatement(
                                "INSERT INTO department VALUES (?,?,?)");
                        pSt.setInt(1, dep.getId().intValue());
                        pSt.setString(2, dep.getName());
                        pSt.setString(3, dep.getLocation());
                    }
                    else {
                        pSt = ConnectionSource.instance().createConnection().prepareStatement(
                                "UPDATE department SET NAME = ?, LOCATION = ? WHERE ID = ?");

                        pSt.setString(1, dep.getName());
                        pSt.setString(2, dep.getLocation());
                        pSt.setInt(3, dep.getId().intValue());
                    }

                    pSt.executeUpdate();
                    return dep;
                }
                catch (SQLException e) {
                    return null;
                }
            }
            @Override
            public void delete(Department dep) {
                try {
                    ConnectionSource.instance().createConnection().createStatement().execute(
                            "DELETE FROM department WHERE ID = " + dep.getId().toString());
                }
                catch (SQLException e) {
                    //wow
                }
            }

        };
    }
    private Employee makeEmployee(ResultSet res){
        try {
            BigInteger id = new BigInteger(res.getString("ID"));
            FullName fullName = new FullName(res.getString("FIRSTNAME"), res.getString("LASTNAME"), res.getString("MIDDLENAME"));
            Position position = Position.valueOf(res.getString("POSITION"));
            LocalDate hireDate = LocalDate.parse(res.getString("HIREDATE"));
            BigDecimal salary = res.getBigDecimal("SALARY");
            BigInteger managerId = BigInteger.valueOf(res.getInt("MANAGER"));
            BigInteger departmentId = BigInteger.valueOf(res.getInt("DEPARTMENT"));

            return new Employee(id, fullName, position, hireDate, salary, managerId, departmentId);
        }
        catch (SQLException e) {
            return null;
        }
    }
    private Department makeDep(ResultSet res) {
        try {
            BigInteger id = new BigInteger(res.getString("ID"));
            String name = res.getString("NAME");
            String location = res.getString("LOCATION");

            return new Department(id, name, location);
        }
        catch (SQLException e) {
            return null;
        }
    }

    private ResultSet exeReq(String request){
        try {
            return ConnectionSource.instance().createConnection().createStatement().executeQuery(request);
        }
        catch (SQLException e){
            return null;
        }
    }
}
