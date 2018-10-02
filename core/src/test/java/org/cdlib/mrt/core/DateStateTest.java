package org.cdlib.mrt.core;

import org.junit.Ignore;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class DateStateTest {

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

    // ------------------------------------------------------------
    // Helper methods

    private static long nowMillis() {
        ZonedDateTime zdt = Instant.now().atZone(SYSTEM_ZONE_ID);
        return toMillis(zdt);
    }

    private Date nowDate() {
        long nowMillis = nowMillis();
        return new Date(nowMillis);
    }

    private static long toMillis(ZonedDateTime zdt) {
        long nowSeconds = zdt.toEpochSecond();
        return TimeUnit.SECONDS.toMillis(nowSeconds);
    }

    private String formatIso8601(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        format.setTimeZone(TimeZone.getTimeZone(SYSTEM_ZONE_ID));
        return format.format(date);
    }

    private String formatIso8601Zulu(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        format.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC.getId()));
        return format.format(date);
    }

    // ------------------------------------------------------------
    // Tests

    // ------------------------------
    // Constructors

    @Test
    public void constructNoArgs() {
        DateState state = new DateState();
        Date stateDate = state.getDate();
        long nowMillis = nowMillis();
        long diff = Math.abs(nowMillis - stateDate.getTime());
        assertTrue(diff < 1000L);
    }

    @Test
    public void constructWithDate() {
        Date nowDate = nowDate();
        DateState state = new DateState(nowDate);
        assertEquals(nowDate, state.getDate());
    }

    @Test
    public void constructWithDateAcceptsNull() {
        DateState state = new DateState((Date) null);
        assertNull(state.getDate());
    }

    @Test
    public void constructWithMillis() {
        long nowMillis = nowMillis();
        Date nowDate = new Date(nowMillis);
        DateState state = new DateState(nowMillis);
        assertEquals(nowDate, state.getDate());
    }

    @Test
    public void constructWithString() {
        Date nowDate = nowDate();
        String nowIso8601 = formatIso8601(nowDate);
        DateState state = new DateState(nowIso8601);
        assertEquals(nowDate, state.getDate());
    }

    @Test
    public void constructWithStringSupportsUTC() {
        // .withNano(0) is a hack to get DateTimeFormatter not to output fractional seconds
        ZonedDateTime nowUtcZDT = ZonedDateTime.now(ZoneOffset.UTC).withNano(0);
        String nowUtcIso8601 = nowUtcZDT.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        DateState state = new DateState(nowUtcIso8601);
        long expected = toMillis(nowUtcZDT);
        assertEquals(expected, state.getTimeLong());
    }

    @Test
    public void constructWithStringSupportsOtherTimeZones() {
        // .withNano(0) is a hack to get DateTimeFormatter not to output fractional seconds
        ZonedDateTime nowOtherZdt = ZonedDateTime.now(OTHER_ZONE_OFFSET).withNano(0);
        String nowOtherIso8601 = nowOtherZdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        DateState state = new DateState(nowOtherIso8601);
        long expected = toMillis(nowOtherZdt);
        assertEquals(expected, state.getTimeLong());
    }

    // ------------------------------
    // Accessors

    @Test
    public void setDate() {
        DateState state = new DateState((Date) null);
        Date nowDate = nowDate();
        state.setDate(nowDate);
        assertEquals(nowDate, state.getDate());
    }

    @Test
    public void getIsoDate() {
        long nowMillis = nowMillis();
        Date nowDate = new Date(nowMillis);
        DateState state = new DateState(nowMillis);
        String expected = formatIso8601(nowDate);
        assertEquals(expected, state.getIsoDate());
    }

    @Test
    public void getIsoDateReturnsNullForNull() {
        DateState state = new DateState((Date) null);
        assertNull(state.getIsoDate());
    }

    @Test
    public void getIsoZDate() {
        long nowMillis = nowMillis();
        Date nowDate = new Date(nowMillis);
        DateState state = new DateState(nowMillis);
        String expected = formatIso8601Zulu(nowDate);
        assertEquals(expected, state.getIsoZDate());
    }

    @Test
    public void getIsoZDateReturnsNullForNull() {
        DateState state = new DateState((Date) null);
        assertNull(state.getIsoZDate());
    }

    @Test
    public void toStringReturnsIsoDate() {
        long nowMillis = nowMillis();
        Date nowDate = new Date(nowMillis);
        DateState state = new DateState(nowMillis);
        String expected = formatIso8601(nowDate);
        assertEquals(expected, state.toString());
    }

    @Ignore
    @Test
    public void toStringPreservesLocalTime() {
        // TODO: test this directly in Checkm.getLine()
        fail("not implemented");
    }

    @Ignore
    @Test
    public void toStringPreservesUTCTime() {
        // TODO: test this directly in Checkm.getLine()
        fail("not implemented");
    }

    @Ignore
    @Test
    public void toStringPreservesOtherTimeZone() {
        // TODO: test this directly in Checkm.getLine()
        fail("not implemented");
    }

    @Test
    public void toStringReturnsEmptyForNull() {
        DateState state = new DateState((Date) null);
        assertEquals("empty", state.toString());
    }

    @Test
    public void getTimeLong() {
        long nowMillis = nowMillis();
        Date nowDate = new Date(nowMillis);
        DateState state = new DateState(nowDate);
        assertEquals(nowMillis, state.getTimeLong());
    }

    @Test
    public void getTimeLongReturnsZeroForNUll() {
        DateState state = new DateState((Date) null);
        assertEquals(0L, state.getTimeLong());
    }

    @Test
    public void maxTime() {
        long start = nowMillis();
        DateState lesser = new DateState(start);
        DateState greater = new DateState(start + 1000L);
        assertEquals(greater, lesser.maxTime(greater));
        assertEquals(greater, greater.maxTime(lesser));
    }
}
