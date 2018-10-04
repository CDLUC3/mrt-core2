package org.cdlib.mrt.core;

import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.cdlib.mrt.core.ManifestRowBatch.cols;
import static org.cdlib.mrt.core.ManifestRowBatch.profiles;
import static org.cdlib.mrt.core.ManifestRowCheckmAbs.getHeaders;
import static org.cdlib.mrt.utility.LoggerInf.LogLevel.DEBUG_LOW;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CheckmTest {

    // ------------------------------------------------------------
    // Fixture

    private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();
    private static final ZoneOffset SYSTEM_ZONE_OFFSET = SYSTEM_ZONE_ID.getRules().getOffset(Instant.now());

    private static final ZoneOffset OTHER_ZONE_OFFSET;

    static {
        int systemOffsetSeconds = SYSTEM_ZONE_OFFSET.getTotalSeconds();
        int otherOffsetSeconds = systemOffsetSeconds + 3600;
        if (otherOffsetSeconds < ZoneOffset.MAX.getTotalSeconds()) {
            OTHER_ZONE_OFFSET = ZoneOffset.ofTotalSeconds(otherOffsetSeconds);
        } else {
            OTHER_ZONE_OFFSET = ZoneOffset.ofTotalSeconds(systemOffsetSeconds - 3600);
        }
    }

    private Checkm checkm;

    @Before
    public void setUp() throws TException {
        TFileLogger logger = new TFileLogger(getClass().getSimpleName(), DEBUG_LOW, DEBUG_LOW);
        checkm = new Checkm(logger, getHeaders(profiles, cols));
    }

    // ------------------------------------------------------------
    // Helper methods

    private ZonedDateTime nowZdt(ZoneOffset offset) {
        // .withNano(0) is a hack to get DateTimeFormatter not to output fractional seconds
        return ZonedDateTime.now(offset).withNano(0);
    }

    private ZonedDateTime nowSystemZdt() {
        return nowZdt(SYSTEM_ZONE_OFFSET);
    }

    private ZonedDateTime nowUtcZdt() {
        return nowZdt(ZoneOffset.UTC);
    }

    private ZonedDateTime nowOtherZdt() {
        return nowZdt(OTHER_ZONE_OFFSET);
    }

    private String toIso8601(ZonedDateTime zdt) {
        return zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    // ------------------------------------------------------------
    // Tests

    @Test
    public void getLinePreservesSystemTime() throws TException {
        String expected = toIso8601(nowSystemZdt());
        FileComponent fc = new FileComponent();
        fc.setCreated(new DateState(expected));

        String line = checkm.getLine(fc);
        assertTrue(
                String.format("Expected timestamp '%s' not found in line '%s'", expected, line),
                line.contains(expected)
        );
    }

    @Test
    public void getLinePreservesUtcTime() throws TException {
        String expected = toIso8601(nowUtcZdt());
        FileComponent fc = new FileComponent();
        fc.setCreated(new DateState(expected));

        String line = checkm.getLine(fc);
        assertTrue(
                String.format("Expected timestamp '%s' not found in line '%s'", expected, line),
                line.contains(expected)
        );
    }

    @Test
    public void getLinePreservesOtherTime() throws TException {
        String expected = toIso8601(nowOtherZdt());
        FileComponent fc = new FileComponent();
        fc.setCreated(new DateState(expected));

        String line = checkm.getLine(fc);
        assertTrue(
                String.format("Expected timestamp '%s' not found in line '%s'", expected, line),
                line.contains(expected)
        );
    }
}
