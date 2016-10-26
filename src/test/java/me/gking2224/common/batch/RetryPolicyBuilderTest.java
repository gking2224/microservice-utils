package me.gking2224.common.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

import me.gking2224.common.utils.NestedProperties;

public class RetryPolicyBuilderTest {

    @Test
    public void testSimple() {
        Properties properties = new Properties();
        properties.setProperty("batch.retryPolicy.type", "SIMPLE");
        properties.setProperty("batch.retryPolicy.simple.attempts", "3");
        properties.setProperty("batch.retryPolicy.simple.retryOn", "java.lang.RuntimeException");
        
        Properties batchProperties = new NestedProperties("batch", properties);
        
        RetryPolicy iPolicy = new RetryPolicyBuilder().properties(batchProperties).build();
        assertNotNull(iPolicy);
        assertTrue(SimpleRetryPolicy.class.isAssignableFrom(iPolicy.getClass()));
        
        SimpleRetryPolicy policy = (SimpleRetryPolicy)iPolicy;
        assertEquals(3, policy.getMaxAttempts());
    }

    @Test
    public void testAlways() {
        Properties properties = new Properties();
        properties.setProperty("batch.retryPolicy.type", "ALWAYS");
        
        Properties batchProperties = new NestedProperties("batch", properties);
        
        RetryPolicy iPolicy = new RetryPolicyBuilder().properties(batchProperties).build();
        assertNotNull(iPolicy);
        assertTrue(AlwaysRetryPolicy.class.isAssignableFrom(iPolicy.getClass()));
    }

    @Test
    public void testFixedTime() {
        Properties properties = new Properties();
        properties.setProperty("batch.retryPolicy.type", "FIXED_TIME");
        properties.setProperty("batch.retryPolicy.fixedTime.time", "18:00");
        properties.setProperty("batch.retryPolicy.fixedTime.timezone", "GMT");
        
        Properties batchProperties = new NestedProperties("batch", properties);
        
        RetryPolicy iPolicy = new RetryPolicyBuilder().properties(batchProperties).build();
        assertNotNull(iPolicy);
    }

}
