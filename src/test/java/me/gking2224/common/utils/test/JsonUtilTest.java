package me.gking2224.common.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.gking2224.common.utils.JsonUtil;

public class JsonUtilTest {

    JsonUtil u = new JsonUtil();
    
    @Test
    public void test() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper m = new ObjectMapper();
        Map<?,?> mp = m.readValue("{\"str\":\"string\", \"itg\":\"123\"}", Map.class);
        assertEquals("string", mp.get("str"));
        
    }

    
    @Test
    public void testJpathFilterMatchLength() throws UnsupportedEncodingException {
        String location = "/model/admin/type/100";
        String json = "[{\"id\":4,\"name\":\"Demonstration\",\"enabled\":true,\"location\":\"/model/admin/type/4\"},{\"id\":100,\"name\":\"Model Type\",\"enabled\":true,\"location\":\""+location+"\"}]";
        
        Object o = u.getFilterValue(json, "$.[?(@.name=='Model Type')].location");
        assertEquals(location, o);
        
    }

    
    @Test
    public void testJpathFilterMatchLength1() {
        String json = "[{\"id\":4,\"name\":\"Demonstration\",\"enabled\":true,\"location\":\"/model/admin/type/4\"},{\"id\":100,\"name\":\"Model Type\",\"enabled\":true,\"location\":\"/model/admin/type/100\"}]";
        Object o = u.getValue(json, "$.[?(@.name=='Model Type')]");
        assertNotNull(o);
        
    }
}
