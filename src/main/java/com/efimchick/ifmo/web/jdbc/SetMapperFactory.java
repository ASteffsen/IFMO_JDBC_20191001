package com.efimchick.ifmo.web.jdbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
//        throw new UnsupportedOperationException();
        SetMapper<Set<Employee>> resultMap = new SetMapper<Set<Employee>>(){
            @Override
            public Set<Employee> mapSet(ResultSet resultSet){
                try {
                    Set<Employee> employeesSet = new HashSet<>();
                    while (resultSet.next()){
                        employeesSet.add(makeEmployee(resultSet));
                    }
                    return employeesSet;
                } catch (SQLException e){
                    return null;
                }
            }
        };
        return resultMap;
    }
    private Employee makeEmployee(ResultSet resultSet) throws SQLException {
        BigInteger id = BigInteger.valueOf(resultSet.getInt("id"));
        FullName fullname = new FullName(
                resultSet.getString("firstname"),
                resultSet.getString("lastname"),
                resultSet.getString("middlename")
        );
        Position position = Position.valueOf(resultSet.getString("position"));
        LocalDate hired = LocalDate.parse(resultSet.getString("hiredate"));
        BigDecimal salary = new BigDecimal(resultSet.getString("salary"));

        Employee manager = null;

        if (resultSet.getString("MANAGER") != null) {
            int managerId = resultSet.getInt("MANAGER");
            int cuRow = resultSet.getRow();
            resultSet.absolute(0);
            while (resultSet.next()) {
                if (managerId == resultSet.getInt("ID"))
                    break;
            }
            manager = makeEmployee(resultSet);
            resultSet.absolute(cuRow);
        }
        return new Employee(id, fullname, position, hired, salary, manager);
    }
}
