package com.efimchick.ifmo.web.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.sql.ResultSet;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        //throw new UnsupportedOperationException();

        SetMapper<Set<Employee>> resultMap = new SetMapper<Set<Employee>>() {

            @Override

            public Set<Employee> mapSet(ResultSet resultSet){
                try {
                    Set<Employee> empList = new HashSet<>();
                    while (resultSet.next()) {
                        empList.add(makeEmployee(resultSet));
                    }
                    return empList;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        return resultMap;
    }
    private Employee makeEmployee(ResultSet resSet) {
        try {
            BigInteger id = new BigInteger(resSet.getString("ID"));
            FullName fullName = new FullName(resSet.getString("FIRSTNAME"), resSet.getString("LASTNAME"), resSet.getString("MIDDLENAME"));
            Position position = Position.valueOf(resSet.getString("POSITION"));
            LocalDate hireDate = LocalDate.parse(resSet.getString("HIREDATE"));
            BigDecimal salary = resSet.getBigDecimal("SALARY");
            findManager(resSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Employee(id, fullName, position, hireDate, salary, manager);
    }
    private Employee findManager(ResultSet resSet){
        try {
            Employee manager = null;
            int manID = resSet.getInt("MANAGER");
            int cur = resSet.getRow();
            resSet.beforeFirst();
            while (resSet.next())
                if (resSet.getInt("ID") == manID)
                    manager = makeEmployee(resSet);
            resSet.absolute(cur);
            return manager;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
