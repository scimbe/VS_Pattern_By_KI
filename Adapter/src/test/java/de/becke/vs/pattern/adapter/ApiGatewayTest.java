package de.becke.vs.pattern.adapter;

import de.becke.vs.pattern.adapter.api.ApiGateway;
import de.becke.vs.pattern.adapter.api.ApiResponse;
import de.becke.vs.pattern.adapter.api.ProductService;
import de.becke.vs.pattern.adapter.api.UserService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Testklasse für den ApiGateway-Adapter.
 */
public class ApiGatewayTest {

    private ApiGateway apiGateway;

    @Before
    public void setUp() {
        UserService userService = new UserService();
        ProductService productService = new ProductService();
        apiGateway = new ApiGateway(userService, productService);
    }

    @Test
    public void testProcessRequestGetUser() {
        ApiResponse response = apiGateway.processRequest("/users/123", "GET", null);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"id\": \"123\", \"username\": \"user123\", \"email\": \"user123@example.com\", \"role\": \"user\" }", response.getBody());
    }

    @Test
    public void testProcessRequestCreateUser() {
        String requestBody = "{ \"username\": \"newuser\", \"email\": \"new@example.com\", \"role\": \"user\" }";
        ApiResponse response = apiGateway.processRequest("/users", "POST", requestBody);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"status\": \"success\", \"message\": \"Benutzer erstellt\" }", response.getBody());
    }

    @Test
    public void testProcessRequestUpdateUser() {
        String requestBody = "{ \"email\": \"updated@example.com\" }";
        ApiResponse response = apiGateway.processRequest("/users/123", "PUT", requestBody);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"status\": \"success\", \"message\": \"Benutzer aktualisiert\" }", response.getBody());
    }

    @Test
    public void testProcessRequestDeleteUser() {
        ApiResponse response = apiGateway.processRequest("/users/123", "DELETE", null);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"status\": \"success\", \"message\": \"Benutzer gelöscht\" }", response.getBody());
    }

    @Test
    public void testProcessRequestGetProduct() {
        ApiResponse response = apiGateway.processRequest("/products/456", "GET", null);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"id\": \"456\", \"name\": \"Produkt 456\", \"price\": 99.99, \"stock\": 42 }", response.getBody());
    }

    @Test
    public void testProcessRequestCreateProduct() {
        String requestBody = "<product><name>Neues Produkt</name><price>149.99</price><stock>10</stock></product>";
        ApiResponse response = apiGateway.processRequest("/products", "POST", requestBody);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"status\": \"success\", \"message\": \"Produkt erstellt\" }", response.getBody());
    }

    @Test
    public void testProcessRequestUpdateProduct() {
        String requestBody = "<product><price>159.99</price><stock>15</stock></product>";
        ApiResponse response = apiGateway.processRequest("/products/456", "PUT", requestBody);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"status\": \"success\", \"message\": \"Produkt aktualisiert\" }", response.getBody());
    }

    @Test
    public void testProcessRequestDeleteProduct() {
        ApiResponse response = apiGateway.processRequest("/products/456", "DELETE", null);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"status\": \"success\", \"message\": \"Produkt gelöscht\" }", response.getBody());
    }

    @Test
    public void testProcessRequestInvalidPath() {
        ApiResponse response = apiGateway.processRequest("", "GET", null);
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"error\": \"Ungültiger Pfad\", \"status\": 400 }", response.getBody());
    }

    @Test
    public void testProcessRequestUnknownResource() {
        ApiResponse response = apiGateway.processRequest("/unknown/123", "GET", null);
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"error\": \"Ressource nicht gefunden: unknown\", \"status\": 404 }", response.getBody());
    }

    @Test
    public void testProcessRequestInvalidMethod() {
        ApiResponse response = apiGateway.processRequest("/users/123", "PATCH", null);
        assertNotNull(response);
        assertEquals(405, response.getStatusCode());
        assertEquals("application/json", response.getContentType());
        assertEquals("{ \"error\": \"Methode nicht unterstützt: PATCH\", \"status\": 405 }", response.getBody());
    }
}
