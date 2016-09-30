package me.gking2224.common.utils;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

public class DurationFormatterTest {
    Function<String, Duration> durationFormatter = DurationFormatter.getInstance();
    @Test
    public void test() {

        List<String> s = Arrays.asList(new String[] {"3h", "3m", "3s", "20", null, ""});
        List<Long> expected = Arrays.asList(new Long[] { 10800000L, 180000L, 3000L, 20L, 0L, 0L });
        
        List<Long> actual = s.stream().map(durationFormatter).map(d -> (d == null)?null:d.toMillis()).collect(Collectors.toList());
        
        assertEquals(expected, actual);
        
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIllegalArgument() {

        List<String> s = Arrays.asList(new String[] {"abc"});
        
        s.stream().map(durationFormatter).collect(Collectors.toList());
        
        
    }

    @Test(expected=NumberFormatException.class)
    public void testNumberFormat() {

        List<String> s = Arrays.asList(new String[] {"xh"});
        
        s.stream().map(durationFormatter).collect(Collectors.toList());
        
        
    }
    
}
