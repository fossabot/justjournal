package com.justjournal.services;

import com.justjournal.core.UserContext;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Lucas Holt
 */
@Slf4j
@Service
public class PdfFormatService extends AbstractFormatService {

    public void write(final UserContext userContext, final OutputStream outputStream) throws ServiceException {
        try {
            final Document document = new Document();
            final OutputStream out = new BufferedOutputStream(outputStream);
            PdfWriter.getInstance(document, out);
            format(userContext, document);
            document.close();
            out.flush();
            out.close();
        } catch (final DocumentException d) {
            log.error(d.getMessage(), d);
            throw new ServiceException("Could not generate PDF");
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("Could not write PDF");
        }
    }
}
