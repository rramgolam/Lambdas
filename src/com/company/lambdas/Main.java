package com.company.lambdas;

import javax.security.auth.login.AccountNotFoundException;
import java.util.*;
import java.util.function.*;

public class Main {

    public static void main(String[] args) {

        // Create a runnable lambda     - alternative to writing a class implementing runnable, overriding run() etc...
        new Thread(()-> System.out.println("Hello from a runnable via lambdas.")).start();

        // Runnable via lambda with a code block
        new Thread(()->{
            System.out.println("Line 1");
            System.out.println("Line 2");
            System.out.println("Line 3");
        }).start();


        // Some objects to use
        Employee jim = new Employee("Jimmy Quartz", 44);
        Employee bob = new Employee("Bob Diamond", 12);
        Employee clive = new Employee("Clive Ruby", 33);
        Employee alex = new Employee("Alex Crystal", 27);

        List<Employee> employees = new ArrayList<>();
        employees.add(jim);
        employees.add(bob);
        employees.add(clive);
        employees.add(alex);


        // Standard way to sort by name with anonymous threads
        Collections.sort(employees, new Comparator<Employee>() {
            @Override
            public int compare(Employee employee1, Employee employee2) {
                return employee1.getName().compareTo(employee2.getName());
            }
        });


        // Same as above via Lambdas
        Collections.sort(employees, (employee1, employee2) ->
                employee1.getName().compareTo(employee2.getName()));

        for (Employee employee : employees) {
            System.out.println(employee.getName());
        }


        // Example using interfaces - implementing within an anonymous class
        String moddedName = doStringOperation(new UpperConcat() {
            @Override
            public String upperAndConcat(String s1, String s2) {
                return s1.toUpperCase() + s2.toUpperCase();
            }
        }, "Andy", "Brown");
        System.out.println(moddedName);


        // OR use lambda to implement the interface - passing a lambda to a function to do the work with
        //     the rest of the params - lambdas can be used as variables for later/repeated use
        UpperConcat uc = (s1, s2) -> s1.toUpperCase() + s2.toUpperCase();
        System.out.println(doStringOperation(uc,"Andy","Brown"));


        // Example of local variables within lambda scope - they must be final or non-changing
        AnotherClass ac = new AnotherClass();
        ac.printValue();


        // Enhanced-for way of iterating through collection
        System.out.println("---------------------------------------");
        employees.forEach(employee -> {
            System.out.println(employee.getName());
            System.out.println(employee.getAge());
        });


        // Using predicates with lambdas for conditional expressions
        printEmployeesByAge(employees,"Employees over 30 : ", employee -> employee.getAge() > 30);
        printEmployeesByAge(employees,"Employees under 30 : ", employee -> employee.getAge() < 30);
        printEmployeesByAge(employees,"Employees over 18 : ", employee -> employee.getAge() > 18);


        // Using Specific Predicates    -   chaining possible with .or .isequal .negate for some
        IntPredicate greaterThan15 = i -> i > 15;
        IntPredicate lessThan100 = i -> i < 100;

        System.out.println(greaterThan15.test(20));
        System.out.println(lessThan100.test(99));
        System.out.println(greaterThan15.and(lessThan100).test(44)); // chained


        // Using Suppliers
        Random random = new Random();
        Supplier<Integer> randomSupplier = () -> random.nextInt(1000);
        for (int i = 0; i < 10; i++) {
            System.out.println(randomSupplier.get());   // lists 10 random ints
        }


        // Using lambda functions - to return values (not just boolean)
        Function<Employee, String> getLastName = (Employee employee) -> {
            return employee.getName().substring(employee.getName().indexOf(" ") + 1);
        };
        System.out.println(getLastName.apply(employees.get(0)));    // use function

        Function<Employee, String> getFirstName = (Employee employee) -> {
            return employee.getName().substring(0,employee.getName().indexOf(" "));
        };
        System.out.println(getFirstName.apply(employees.get(0)));    // use function

            // you can use multiple functions and pass the required one to a method depending on task
        getAName(getFirstName, employees.get(0));
        getAName(getLastName, employees.get(0));

        // You can chain together Functions
        Function<Employee, String> upperCase = employee -> employee.getName().toUpperCase();
        Function<String, String> firstName = name -> name.substring(name.indexOf(" ") + 1);
        Function chainedFunction = upperCase.andThen(firstName);
        System.out.println(chainedFunction.apply(employees.get(0)));

        // If you need functions that take two arguments
        BiFunction<String, Employee, String> appendAge = (String name, Employee employee) -> {
            return name.concat(" " + employee.getAge());
        };

        String upperName = upperCase.apply(employees.get(1));
        System.out.println(appendAge.apply(upperName, employees.get(1)));

        // UnaryOperator - one arg one result (same type)
        IntUnaryOperator incBy5 = i -> i + 5;
        System.out.println(incBy5.applyAsInt(10));

        // Consumers can be chained
        Consumer<String> c1 = s -> s.toUpperCase();
        Consumer<String> c2 = s -> System.out.println(s);
        c1.andThen(c2).accept("Hello, World!"); // but the uppercase is lost

    }

    public static void getAName(Function<Employee, String> getName, Employee employee) {
        System.out.println(getName.apply(employee));
    }

    public static String doStringOperation(UpperConcat uc, String s1, String s2) {
        return uc.upperAndConcat(s1, s2);
    }

    public static void printEmployeesByAge(List<Employee> employees,
                                           String ageText,
                                           Predicate<Employee> ageCondition) {

        System.out.println(ageText);
        System.out.println("----------------------------");
        for(Employee employee : employees) {
            if (ageCondition.test(employee)) {
                System.out.println(employee.getName());
                System.out.println(employee.getAge());
                System.out.println();
            }
        }
    }
}



class Employee {
    private String name;
    private int age;

    public Employee(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

interface UpperConcat {
    public String upperAndConcat(String s1, String s2);
}

class AnotherClass {
    public  void printValue() {
        int number = 65;

        Runnable r = () -> {
          try {
              Thread.sleep(8000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
          //number++;   Compiler complains - must be final or non changing
          System.out.println(number);
        };

        new Thread(r).start();
    }
}