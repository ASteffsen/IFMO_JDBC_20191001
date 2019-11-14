package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {

        SetMapper<Set<Employee>> res = new SetMapper<Set<Employee>>() {

            @Override
            public Set<Employee> mapSet(ResultSet resSet) {
                try {
                    Set<Employee> empList = new HashSet<>();
                    while (resSet.next()) {
                        empList.add(makeEmployee(resSet));
                    }
                    return empList;
                }
                catch (SQLException e) {
                    return null;
                }
            }
        };
        return res;
    }

    public Employee makeEmployee(ResultSet resSet) {

        try {
            BigInteger id = new BigInteger(resSet.getString("ID"));
            FullName fullName = new FullName(resSet.getString("FIRSTNAME"), resSet.getString("LASTNAME"), resSet.getString("MIDDLENAME"));
            Position position = Position.valueOf(resSet.getString("POSITION"));
            LocalDate hireDate = LocalDate.parse(resSet.getString("HIREDATE"));
            BigDecimal salary = resSet.getBigDecimal("SALARY");
            Employee manager = null;

            if (resSet.getString("MANAGER") != null) {
                int manId = resSet.getInt("MANAGER");
                int cur = resSet.getRow();
                resSet.beforeFirst();
                while (resSet.next()) {
                    if (Integer.parseInt(resSet.getString("ID")) == manId) {
                        manager = makeEmployee(resSet);
                    }
                }
                resSet.absolute(cur);

            }
            return new Employee(id, fullName, position, hireDate, salary, manager);
        } catch (SQLException e) {
            return null;
        }
    }
}