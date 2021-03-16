package com.viettel.etc.repositories.impl;

import com.viettel.etc.dto.AttachmentFileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AttachmentFileRepositoryImplTest {

    private AttachmentFileRepositoryImpl attachmentFileRepositoryImplUnderTest;

    @BeforeEach
    void setUp() {
        attachmentFileRepositoryImplUnderTest = new AttachmentFileRepositoryImpl();
    }

    @Test
    void testGetFileByAttachId() {
        // Setup
        final List<AttachmentFileDTO> expectedResult = Arrays.asList(new AttachmentFileDTO(0L, 0L, 0L, "documentName", "documentPath", "description", "createUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "updateUser", new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(), "status", "id", 0, 0, false, "fileBase64"));

        // Run the test
        final List<AttachmentFileDTO> result = attachmentFileRepositoryImplUnderTest.getFileByAttachId(0L);

        // Verify the results
        assertNull(result);
    }
}
