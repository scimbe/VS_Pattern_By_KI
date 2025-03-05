package de.becke.vs.pattern.callback;

import de.becke.vs.pattern.callback.common.Callback;
import de.becke.vs.pattern.callback.simple.SimpleAsyncService;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.Assert.*;

public class SimpleAsyncServiceTest {

    private SimpleAsyncService service;

    @Before
    public void setUp() {
        service = new SimpleAsyncService();
    }

    @Test
    public void testProcessSynchronouslySuccess() {
        String input = "test";
        Function<String, String> processor = String::toUpperCase;
        Callback<String> callback = new Callback<>() {
            @Override
            public void onSuccess(String result) {
                assertEquals("TEST", result);
            }

            @Override
            public void onError(Throwable exception) {
                fail("Should not have thrown an exception");
            }
        };

        service.processSynchronously(input, processor, callback);
    }

    @Test
    public void testProcessSynchronouslyError() {
        String input = "test";
        Function<String, String> processor = s -> {
            throw new RuntimeException("Test exception");
        };
        Callback<String> callback = new Callback<>() {
            @Override
            public void onSuccess(String result) {
                fail("Should not have succeeded");
            }

            @Override
            public void onError(Throwable exception) {
                assertEquals("Test exception", exception.getMessage());
            }
        };

        service.processSynchronously(input, processor, callback);
    }

    @Test
    public void testProcessAsynchronouslySuccess() throws InterruptedException {
        String input = "test";
        Function<String, String> processor = String::toUpperCase;
        CountDownLatch latch = new CountDownLatch(1);
        Callback<String> callback = new Callback<>() {
            @Override
            public void onSuccess(String result) {
                assertEquals("TEST", result);
                latch.countDown();
            }

            @Override
            public void onError(Throwable exception) {
                fail("Should not have thrown an exception");
            }
        };

        service.processAsynchronously(input, processor, callback);
        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    @Test
    public void testProcessAsynchronouslyError() throws InterruptedException {
        String input = "test";
        Function<String, String> processor = s -> {
            throw new RuntimeException("Test exception");
        };
        CountDownLatch latch = new CountDownLatch(1);
        Callback<String> callback = new Callback<>() {
            @Override
            public void onSuccess(String result) {
                fail("Should not have succeeded");
            }

            @Override
            public void onError(Throwable exception) {
                assertEquals("Test exception", exception.getMessage());
                latch.countDown();
            }
        };

        service.processAsynchronously(input, processor, callback);
        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    @Test
    public void testProcessWithDelay() throws InterruptedException {
        String input = "test";
        CountDownLatch latch = new CountDownLatch(1);
        Callback<String> callback = new Callback<>() {
            @Override
            public void onSuccess(String result) {
                assertEquals("Verarbeitetes Ergebnis: TEST", result);
                latch.countDown();
            }

            @Override
            public void onError(Throwable exception) {
                fail("Should not have thrown an exception");
            }
        };

        service.processAsynchronously(input, SimpleAsyncService::processWithDelay, callback);
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}
