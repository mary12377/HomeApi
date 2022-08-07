package tests;


import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import static helpers.AllureRestAssuredFilter.withCustomTemplates;


public class TestBase {
    @BeforeAll
    static void setUp() {
        RestAssured.filters(withCustomTemplates());
    }
}