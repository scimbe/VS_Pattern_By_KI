package de.becke.vs.pattern.pipeline;

import de.becke.vs.pattern.pipeline.async.AsyncPipeline;
import de.becke.vs.pattern.pipeline.async.AsyncPipelineStage;
import de.becke.vs.pattern.pipeline.core.PipelineContext;
import de.becke.vs.pattern.pipeline.core.PipelineException;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

public class AsyncPipelineTest {

    @Test
    public void testExecuteWithSingleStage() throws PipelineException {
        AsyncPipeline<String, String> pipeline = new AsyncPipeline<>("TestPipeline");
        pipeline.addStage(new UpperCaseStage());

        String result = pipeline.execute("test");
        assertEquals("TEST", result);
    }

    @Test
    public void testExecuteWithMultipleStages() throws PipelineException {
        AsyncPipeline<String, String> pipeline = new AsyncPipeline<>("TestPipeline");
        pipeline.addStage(new UpperCaseStage())
                .addStage(new AppendStage("123"));

        String result = pipeline.execute("test");
        assertEquals("TEST123", result);
    }

    @Test
    public void testExecuteWithErrorHandling() {
        AsyncPipeline<String, String> pipeline = new AsyncPipeline<>("TestPipeline");
        pipeline.addStage(new ErrorStage());

        try {
            pipeline.execute("test");
            fail("PipelineException expected");
        } catch (PipelineException e) {
            assertEquals("Fehler in Stage 'ErrorStage': Intentional error", e.getMessage());
        }
    }

    @Test
    public void testExecuteAsync() throws Exception {
        AsyncPipeline<String, String> pipeline = new AsyncPipeline<>("TestPipeline");
        pipeline.addStage(new UpperCaseStage());

        CompletableFuture<String> future = pipeline.executeAsync("test");
        String result = future.get();
        assertEquals("TEST", result);
    }

    private static class UpperCaseStage implements AsyncPipelineStage<String, String> {
        @Override
        public CompletableFuture<String> processAsync(String input, PipelineContext context) {
            return CompletableFuture.completedFuture(input.toUpperCase());
        }

        @Override
        public String getStageName() {
            return "UpperCaseStage";
        }
    }

    private static class AppendStage implements AsyncPipelineStage<String, String> {
        private final String suffix;

        public AppendStage(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public CompletableFuture<String> processAsync(String input, PipelineContext context) {
            return CompletableFuture.completedFuture(input + suffix);
        }

        @Override
        public String getStageName() {
            return "AppendStage";
        }
    }

    private static class ErrorStage implements AsyncPipelineStage<String, String> {
        @Override
        public CompletableFuture<String> processAsync(String input, PipelineContext context) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(new PipelineException("Intentional error", null, getStageName()));
            return future;
        }

        @Override
        public String getStageName() {
            return "ErrorStage";
        }
    }
}
