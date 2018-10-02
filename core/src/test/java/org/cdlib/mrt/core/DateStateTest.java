package org.cdlib.mrt.core;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class DateStateTest {

    // ------------------------------------------------------------
    // Fixture

    private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();

    // ------------------------------------------------------------
    // Helper methods

    private static long nowMillis() {
        long nowSeconds = Instant.now().atZone(SYSTEM_ZONE_ID).toEpochSecond();
        return TimeUnit.SECONDS.toMillis(nowSeconds);
    }

    private Date nowDate() {
        long nowMillis = nowMillis();
        return new Date(nowMillis);
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
