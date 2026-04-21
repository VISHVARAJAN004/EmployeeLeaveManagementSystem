package com.example.EmployeeLeaveManagement.service.test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static io.restassured.RestAssured.given;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EmployeeApiTest {

    private static final Logger log = LoggerFactory.getLogger(EmployeeApiTest.class);

    private int port =8085;

    @Test
    void createEmployeeTest() throws InterruptedException {

        given().port(port)
                .auth().preemptive().basic("employee", "123")
                .contentType("application/json")
                .body("""
                        {
                          "name":"Tester1",
                          "email":"tester1@gmail.com",
                          "dateOfBirth":"2004-06-01"
                        }
                        """)
                .when().post("/api/employees")
                .then().log().all().statusCode(201);

        log.info("Employee Tester1 successfully created.");
    }

    @Test
    void createEmployee_negativeTesting() {
        int status=
        given().port(port)
                .auth().preemptive().basic("employee", "123")
                .contentType("application/json")
                .body("""
                        {
                          "name":"Tester1"
                        }
                        """)
                .when().post("/api/employees")
                .then().log().all().extract().statusCode();
                if(status==400)
                    log.info("createEmployee_negativeTesting passed received expected status: "+status +"{missing required fields email and dateOfBirth}");
                else
                    log.error("createEmployee_negativeTesting failed expected 400 but received: "+status);
    }

    @Test
    void getEmployeeTest() throws InterruptedException {
        given().port(port)
                .auth().preemptive().basic("employee", "123")
                .contentType("application/json")
                .body("""
                    {
                      "name":"Tester2",
                      "email":"tester2@gmail.com",
                      "dateOfBirth":"2002-04-05"
                    }
                    """)
                .when().post("/api/employees")
                .then().statusCode(201);

        given().port(port)
                .auth().preemptive().basic("employee","123")
                .when().get("/api/employees")
                .then().log().all().statusCode(200);

        log.info("All employees fetched successfully");
       //Thread.sleep(20000);
    }

    @Test
    void getEmployee_negativeTesting(){
        int status=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .when().get("/api/employee")
                .then().log().all().extract().statusCode();
        if(status==403)
            log.info("getEmployee_negativeTesting passed received expected status: "+status+"{Invalid endpoint}");
        else
            log.error("getEmployee_negativeTesting failed expected 403 but received: "+status);
    }

    @Test
    void getEmployeeByIdTest(){
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "name":"Tester3",
                           "email":"tester3@gmail.com",
                           "dateOfBirth":"2003-11-21"
                        }
                       """)
                .when().post("/api/employees")
                .then().statusCode(201);

        given().port(port)
                .auth().preemptive().basic("employee","123")
                .when().get("/api/employees/1")
                .then().log().all().statusCode(200);
        log.info("Employee fetched successfully by ID");
    }

    @Test
    void getEmployeeById_negativeTesting(){
        int status=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .when().get("/api/employees/999")
                .then().log().all().extract().statusCode();
        if(status==400)
            log.info("getEmployeeById_negativeTesting passed received expected status: "+status+"{employee ID 999 not found}");
        else
            log.error("getEmployeeById_negativeTesting failed expected 400 but received: "+status);
    }

    @Test
    void createLeaveRequestTest() throws InterruptedException {
        int employeeId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "name":"Tester4",
                           "email":"tester4@gmail.com",
                           "dateOfBirth":"2000-09-04"
                        }
                        """)
                .when().post("/api/employees")
                .then().statusCode(201)
                .extract().path("id");

        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "employeeId":"%s",
                           "leaveTypeName": "Casual",
                           "startDate": "2026-08-25",
                           "endDate": "2026-08-25",
                           "leaveNote": "Function"
                        }
                        """.formatted(employeeId))
                .when().post("/api/leaves/apply")
                .then().log().all().statusCode(201);
        log.info("Leave Request created successfully");
       // Thread.sleep(20000);
    }

    @Test
    void createLeaveRequestTest_negativeTesting(){
        int status=
        given().port(port).auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                          "employeeId":"1"
                        }
                        """)
                .when().post("/api/leaves/apply")
                .then().log().all().extract().statusCode();
        if(status==400)
            log.info("createLeaveRequestTest_negativeTesting passed received expected status: "+status+"{missing required fields leaveTypeName, startDate, endDate}");
        else
            log.error("createLeaveRequestTest_negativeTesting failed expected 400 but received: "+status);
    }

    @Test
    void getPendingLeaves() throws InterruptedException {
        int employeeId=
        given().port(port).auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "name":"Tester5",
                           "email":"tester5@gmail.com",
                           "dateOfBirth":"2000-06-04"
                        }
                        """)
                .when().post("/api/employees")
                .then().statusCode(201)
                .extract().path("id");
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "employeeId":"%s",
                           "leaveTypeName": "Casual",
                           "startDate": "2026-09-28",
                           "endDate": "2026-09-28",
                           "leaveNote": "Function"
                        }
                        """.formatted(employeeId))
                .when().post("/api/leaves/apply")
                .then().log().all().statusCode(201);
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .when().get("/api/leaves/pending")
                .then().log().all().statusCode(200);
        log.info("Pending leaves fetched successfully");
       // Thread.sleep(20000);
    }

    @Test
    void getPendingLeaves_negativeTesting(){
        int status=
        given().port(port)
                .when().get("/api/leaves/history")
                .then().log().all().extract().statusCode();
        if(status==401)
            log.info("getPendingLeaves_negativeTesting passed received expected status: "+ status + "{employee role not authorized to access leave history endpoint}");
        else
            log.error("getPendingLeaves_negativeTesting failed expected 401 but received: "+status);
    }

    @Test
    void getLeaveHistoryById() throws InterruptedException {
        int employeeId=
        given().port(port).auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "name":"Tester6",
                           "email":"tester6@gmail.com",
                           "dateOfBirth":"2000-09-04"
                        }
                        """)
                .when().post("/api/employees")
                .then().log().all().statusCode(201)
                .extract().path("id");
        int leaveId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "employeeId":%s,
                           "leaveTypeName": "Casual",
                           "startDate": "2026-08-22",
                           "endDate": "2026-08-23",
                           "leaveNote": "Function"
                        }
                        """.formatted(employeeId))
                .when().post("/api/leaves/apply")
                .then().log().all().statusCode(201)
                .extract().path("leaveId");
        given().port(port).auth().preemptive().basic("employee","123")
                .when().get("/api/leaves/history/"+employeeId)
                .then().log().all().statusCode(200);
        log.info("Fetched Leave history by employee ID successfully");
    }

    @Test
    void getLeaveHistoryById_negativeTesting(){
        int status=
        given().port(port).auth().preemptive().basic("employee","123")
                .when().get("/api/leaves/history/999")
                .then().log().all().extract().statusCode();
        if(status ==400)
            log.info("getLeaveHistoryById_negativeTesting passed received expected status: "+ status +"{employee ID 999 not found}");
        else
            log.error("getPendingLeaves_negativeTesting failed expected 400 but received: "+status);
    }

    @Test
    void rejectLeaveTesting() throws InterruptedException {
        int employeeId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "name":"Tester7",
                           "email":"tester7@gmail.com",
                           "dateOfBirth":"2001-07-08"
                        }
                        """)
                .when().post("/api/employees")
                .then().statusCode(201)
                .extract().path("id");
        int leaveId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "employeeId":"%s",
                           "leaveTypeName":"Casual",
                           "startDate": "2026-08-22",
                           "endDate": "2026-08-23",
                           "leaveNote": "Function"
                        }
                        """.formatted(employeeId))
                .when().post("/api/leaves/apply")
                .then().log().all().statusCode(201)
                .extract().path("leaveId");
        given().port(port)
                .auth().preemptive().basic("manager","123")
                .when().patch("/api/manager/reject/"+leaveId)
                .then().log().all().statusCode(200);
        log.info("Leave got rejected by manager");
        //Thread.sleep(20000);
    }

    @Test
    void rejectLeaveTesting_negativeTesting(){
        int employeeId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "name":"Tester8",
                           "email":"tester8@gmail.com",
                           "dateOfBirth":"2000-09-04"
                        }
                        """)
                .when().post("/api/employees")
                .then().log().all().statusCode(201)
                .extract().path("id");
        int leaveId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "employeeId":"%s",
                           "leaveTypeName":"Casual",
                           "startDate": "2026-07-25",
                           "endDate": "2026-07-26",
                           "leaveNote": "Function"
                        }
                        """.formatted(employeeId))
                .when().post("/api/leaves/apply")
                .then().log().all().statusCode(201)
                .extract().path("leaveId");
        int status=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .when().patch("/api/manager/reject/"+employeeId)
                .then().log().all().extract().statusCode();
        if(status==403)
            log.info("rejectLeaveTesting_negativeTesting passed received expected status: "+status+"{employee role not authorized to access manager reject endpoint}");
        else
            log.error("getPendingLeaves_negativeTesting failed expected 400 but received: "+status);
    }

    @Test
    void approveLeaveTesting() throws InterruptedException {
        int employeeId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "name":"Tester9",
                           "email":"tester9@gmail.com",
                           "dateOfBirth":"2000-12-12"
                        }
                        """)
                .when().post("/api/employees")
                .then().log().all().statusCode(201)
                .extract().path("id");
        int leaveId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "employeeId":"%s",
                           "leaveTypeName": "Casual",
                           "startDate": "2026-07-23",
                           "endDate": "2026-07-25",
                           "leaveNote": "Function"
                        }
                        """.formatted(employeeId))
                .when().post("/api/leaves/apply")
                .then().log().all().statusCode(201)
                .extract().path("leaveId");
        given().port(port)
                .auth().preemptive().basic("manager","123")
                .when().patch("/api/manager/approve/"+leaveId)
                .then().log().all().statusCode(200);
        log.info("Leave approved by manager");
        //Thread.sleep(20000);
    }

    @Test
    void approveLeave_negativeTesting(){
        int employeeId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                          "name":"Tester10",
                          "email":"tester10@gmail.com",
                          "dateOfBirth":"2000-09-04"
                        }
                        """)
                .when().post("/api/employees")
                .then().log().all().statusCode(201)
                .extract().path("id");
        int leaveId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                          {
                          "employeeId":"%s",
                          "leaveTypeName": "Casual",
                          "startDate": "2026-08-21",
                          "endDate": "2026-08-22",
                          "leaveNote": "Function"
                          }
                        """.formatted(employeeId))
                .when().post("/api/leaves/apply")
                .then().log().all().statusCode(201)
                .extract().path("leaveId");
        int status=
        given().port(port)
                .auth().preemptive().basic("manager","123")
                .when().patch("/api/manager/approve/999")
                .then().log().all().extract().statusCode();
        if(status==400)
            log.info("approveLeave_negativeTesting passed received expected status: "+status+"{leave ID 999 not found}");
        else
            log.error("approveLeave_negativeTesting failed expected 400 but received: "+status);
    }

    @Test
    void getPendingLeavesByManager() throws InterruptedException {
        int employeeId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "name":"Tester11",
                           "email":"tester11@gmail.com",
                           "dateOfBirth":"2000-09-04"
                        }
                        """)
                .when().post("/api/employees")
                .then().statusCode(201)
                .extract().path("id");
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                           "employeeId":"%s",
                           "leaveTypeName": "Casual",
                           "startDate": "2026-07-25",
                           "endDate": "2026-07-25",
                           "leaveNote": "Function"
                        }
                        """.formatted(employeeId))
                .when().post("/api/leaves/apply")
                .then().log().all().statusCode(201);
        given().port(port)
                .auth().preemptive().basic("manager","123")
                .when().get("/api/manager/pending")
                .then().log().all().statusCode(200);
        log.info("Pending leaves fetched by manager successfully");
        //Thread.sleep(20000);
    }

    @Test
    void getPendingLeavesByManager_negativeTesting(){
        int status=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .when().get("/api/manager/pending")
                .then().log().all().extract().statusCode();
        if(status==403)
            log.info("getPendingLeavesByManager_negativeTesting passed received expected status: "+status +"{employee role not authorized to access manager pending leaves endpoint}");
        else
            log.error("getPendingLeavesByManager_negativeTesting failed expected 403 but received: "+status);
    }

    @Test
    void updateEmployeeTesting() throws InterruptedException {
        int employeeId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                          "name":"Tester12",
                          "email":"tester12@gmail.com",
                          "dateOfBirth":"2000-12-21"
                        }
                        """)
                .when().post("/api/employees")
                .then().log().all().statusCode(201)
                .extract().path("id");
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                          "name":"Tester12Updated",
                          "email":"tester12updated@gmail.com",
                          "dateOfBirth":"2000-12-22"
                        }
                        """)
                .when().put("/api/employees/%s".formatted(employeeId))
                .then().log().all().statusCode(200);
        log.info("Employee successfully updated using PUT method");
        //Thread.sleep(20000);
    }

    @Test
    void updateEmployee_negativeTesting(){
        int status=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                          "name":"Tester12",
                          "email":"tester12@gmail.com",
                          "dateOfBirth":"2000-12-22"
                        }
                        """)
                .when().put("/api/employees/999")
                .then().log().all().extract().statusCode();
        if(status==400)
            log.info("updateEmployee_negativeTesting passed expected received status: "+status+"{employee ID 999 not found}");
        else
            log.error("updateEmployee_negativeTesting failed expected 400 but received: "+status);
    }

    @Test
    void deleteEmployeeTesting(){
        int employeeId=
        given().port(port)
                .auth().preemptive().basic("employee","123")
                .contentType("application/json")
                .body("""
                        {
                          "name":"DeleteTest",
                          "email":"deleteTest@gmail.com",
                          "dateOfBirth":"2000-01-01"
                        }
                        """)
                .when().post("/api/employees")
                .then().statusCode(201)
                .extract().path("id");
        given().port(port)
                .auth().preemptive().basic("manager","123")
                .when().delete("/api/manager/delete/employee/%s".formatted(employeeId))
                .then().log().all().statusCode(200);
        log.info("Employee deleted successfully with ID "+employeeId);
    }

    @Test
    void deleteEmployee_negativeTesting(){
        int status=
         given().port(port)
                .auth().preemptive().basic("manager","123")
                .when().delete("/api/manager/delete/employee/999")
                .then().log().all().extract().statusCode();
         if(status==400)
             log.info("deleteEmployee_negativeTesting passed expected received status: "+status +"{employee ID 999 not found}");
         else
             log.error("deleteEmployee_negativeTesting failed expected 400 but received: "+status);
    }
}