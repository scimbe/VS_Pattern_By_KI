package de.becke.vs.pattern.facade;

import de.becke.vs.pattern.facade.remote.RemoteServiceFacade;
import de.becke.vs.pattern.facade.remote.OrderResult;
import de.becke.vs.pattern.facade.remote.UserProfile;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RemoteServiceFacadeTest {

    private RemoteServiceFacade facade;

    @Before
    public void setUp() {
        facade = new RemoteServiceFacade();
    }

    @Test
    public void testProcessOrderSuccess() {
        String userId = "user_123";
        String[] productIds = {"product_1", "product_2"};
        int[] quantities = {2, 1};

        OrderResult result = facade.processOrder(userId, productIds, quantities);

        assertTrue(result.isSuccess());
        assertNotNull(result.getOrderId());
        assertEquals("Bestellung erfolgreich", result.getMessage());
    }

    @Test
    public void testProcessOrderInsufficientStock() {
        String userId = "user_123";
        String[] productIds = {"product_3"};
        int[] quantities = {10};

        OrderResult result = facade.processOrder(userId, productIds, quantities);

        assertFalse(result.isSuccess());
        assertNull(result.getOrderId());
        assertEquals("Unzureichender Bestand für Produkt: product_3", result.getMessage());
    }

    @Test
    public void testGetUserProfile() {
        String userId = "user_123";

        UserProfile profile = facade.getUserProfile(userId);

        assertNotNull(profile);
        assertEquals(userId, profile.getUserId());
        assertEquals("Benutzer user_123", profile.getName());
        assertEquals("user_123@example.com", profile.getEmail());
        assertArrayEquals(new String[]{"order_1001", "order_1002", "order_1003"}, profile.getOrderHistory());
    }

    @Test
    public void testCheckProductAvailability() {
        String[] productIds = {"product_1", "product_2", "product_3"};

        int[] availabilities = facade.checkProductAvailability(productIds);

        assertArrayEquals(new int[]{10, 5, 0}, availabilities);
    }

    @Test
    public void testCancelOrderSuccess() {
        String orderId = "order_1001";

        boolean result = facade.cancelOrder(orderId);

        assertTrue(result);
    }

    @Test
    public void testCancelOrderNotFound() {
        String orderId = "invalid_order";

        boolean result = facade.cancelOrder(orderId);

        assertFalse(result);
    }
}
