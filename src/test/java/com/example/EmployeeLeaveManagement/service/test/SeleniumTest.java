package com.example.EmployeeLeaveManagement.service.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class SeleniumTest {

        public static void main(String[] args) {

            WebDriver driver = new ChromeDriver();

            // 1. Open Swagger UI
            driver.get("http://localhost:8080/swagger-ui/index.html");
            driver.manage().window().maximize();

            // 2. Click on Employees API section
            WebElement employeesApi = driver.findElement(By.xpath("//span[text()='employee-controller']"));
            employeesApi.click();

            // 3. Click POST /api/employees
            WebElement postApi = driver.findElement(By.xpath("//span[contains(text(),'POST /api/employees')]"));
            postApi.click();

            // 4. Click "Try it out"
            WebElement tryItOut = driver.findElement(By.xpath("//button[text()='Try it out']"));
            tryItOut.click();

            // 5. Enter JSON data
            WebElement textArea = driver.findElement(By.xpath("//textarea"));
            textArea.clear();
            textArea.sendKeys("{\n" +
                    "  \"name\": \"John\",\n" +
                    "  \"email\": \"john@test.com\",\n" +
                    "  \"dateOfBirth\": \"1995-05-10\"\n" +
                    "}");

            // 6. Click Execute
            WebElement executeBtn = driver.findElement(By.xpath("//button[text()='Execute']"));
            executeBtn.click();

            // 7. Just print response (basic check)
            try {
                Thread.sleep(3000); // simple wait (just for demo)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<WebElement> responses = driver.findElements(By.xpath("//pre"));
            for (WebElement res : responses) {
                System.out.println(res.getText());
            }

            // Close browser
            driver.quit();
        }
    }

