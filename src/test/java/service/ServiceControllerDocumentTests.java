package service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import service.controller.TikaServiceConfig;
import tika.DocumentProcessorTests;
import tika.legacy.LegacyPdfProcessorConfig;
import tika.model.TikaProcessingResult;
import tika.processor.CompositeTikaProcessorConfig;

import static org.junit.Assert.*;


/**
 * Implements document processing tests for the Service Controller, extending the set of available tests
 * present in DocumentProcessorTests
 */
@SpringBootTest(classes = TikaServiceApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TikaServiceConfig.class, LegacyPdfProcessorConfig.class, CompositeTikaProcessorConfig.class})
public abstract class ServiceControllerDocumentTests extends DocumentProcessorTests  {

    @Autowired
    TikaServiceConfig serviceConfig;

    protected abstract TikaProcessingResult sendProcessingRequest(final String docPath, HttpStatus expectedStatus) throws Exception;

    @Override
    protected TikaProcessingResult processDocument(final String docPath) throws Exception {
        return sendProcessingRequest(docPath, HttpStatus.OK);
    }

    /**
     * The actual tests start from here
     *
     *
     */

    @Override
    public void testExtractPdfEx1Encrypted() throws Exception {
        final String docPath = "pdf/ex1_enc.pdf";

        TikaProcessingResult result = sendProcessingRequest(docPath, HttpStatus.BAD_REQUEST);

        // extraction from encrypted PDF will fail with the proper error message
        assertFalse(result.getSuccess());
        assertTrue(result.getError().contains("document is encrypted"));
    }

    @Test
    public void testExtractEmptyPdfFile() throws Exception {
        final String docPath = "invalid/pdf_empty.pdf";

        assertFalse(serviceConfig.isFailOnEmptyFiles());

        // extraction should pass but with error
        TikaProcessingResult result = sendProcessingRequest(docPath, HttpStatus.OK);
        assertFalse(result.getSuccess());
        assertTrue(result.getError().contains("Empty"));
    }
}
