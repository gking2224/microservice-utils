package me.gking2224.common.utils;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

public class NestedPropertiesTest {

    @Test
    public void test() {
        Properties p = new Properties();
        p.setProperty("a.nested", "value");
        
        NestedProperties np = new NestedProperties("a", p);
        
        assertEquals("value", np.get("nested"));
    }

    @Test
    public void testTwoDeep() {
        Properties p = new Properties();
        p.setProperty("a.deep.nested", "value");
        
        NestedProperties np1 = new NestedProperties("a", p);
        
        NestedProperties np2 = new NestedProperties("deep", np1);
        
        assertEquals("value", np2.get("nested"));
    }

    @Test
    public void testFallback1() {
        Properties p = new Properties();
        p.setProperty("value", "global");
        p.setProperty("continent.value", "continent");
        p.setProperty("continent.country.value", "country");
        p.setProperty("continent.country.state.value", "state");
        
        NestedProperties np1 = new NestedProperties("continent", p);
        NestedProperties np2 = new NestedProperties("country", np1);
        NestedProperties np3 = new NestedProperties("state", np2);
        
        assertEquals("state", np3.get("value"));
    }

    @Test
    public void testFallback2() {
        Properties p = new Properties();
        p.setProperty("value", "global");
        p.setProperty("continent.value", "continent");
        p.setProperty("continent.country.value", "country");
        
        NestedProperties np1 = new NestedProperties("continent", p);
        NestedProperties np2 = new NestedProperties("country", np1);
        NestedProperties np3 = new NestedProperties("state", np2);
        
        assertEquals("country", np3.get("value"));
    }

    @Test
    public void testFallback3() {
        Properties p = new Properties();
        p.setProperty("value", "global");
        p.setProperty("continent.value", "continent");
        
        NestedProperties np1 = new NestedProperties("continent", p);
        NestedProperties np2 = new NestedProperties("country", np1);
        NestedProperties np3 = new NestedProperties("state", np2);
        
        assertEquals("continent", np3.get("value"));
    }

    @Test
    public void testFallback4() {
        Properties p = new Properties();
        p.setProperty("value", "global");
        
        NestedProperties np1 = new NestedProperties("continent", p);
        NestedProperties np2 = new NestedProperties("country", np1);
        NestedProperties np3 = new NestedProperties("state", np2);
        
        assertEquals("global", np3.get("value"));
    }

    @Test
    public void testFallback4_getProperty() {
        Properties p = new Properties();
        p.setProperty("value", "global");
        
        NestedProperties np1 = new NestedProperties("continent", p);
        NestedProperties np2 = new NestedProperties("country", np1);
        NestedProperties np3 = new NestedProperties("state", np2);
        
        assertEquals("global", np3.getProperty("value"));
    }

}
