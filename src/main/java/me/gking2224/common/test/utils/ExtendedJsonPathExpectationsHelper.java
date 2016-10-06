package me.gking2224.common.test.utils;

import static org.hamcrest.MatcherAssert.assertThat;

import java.text.ParseException;

import org.hamcrest.Matcher;
import org.springframework.test.util.JsonPathExpectationsHelper;

import me.gking2224.common.utils.JsonUtil;

public class ExtendedJsonPathExpectationsHelper extends JsonPathExpectationsHelper {

    private String expression;
    private JsonUtil jsonUtil = new JsonUtil();
    
    public ExtendedJsonPathExpectationsHelper(String expression, Object[] args) {
        super(expression, args);
        this.expression = expression;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> void assertValue(String content, Matcher<T> matcher) throws ParseException {
        T value = (T) jsonUtil.getFilterValue(content, expression);
        assertThat("JSON path \"" + this.expression + "\"", value, matcher);
    }
    
    public <T> void assertArrayValue(String content, Matcher<T> matcher) throws ParseException {
        @SuppressWarnings("unchecked")
        T value = (T) jsonUtil.getArray(content, expression);
        assertThat("JSON path \"" + this.expression + "\"", value, matcher);
    }
    
    public <T> void assertArrayValue(String content, Matcher<T> matcher, int idx) throws ParseException {
        @SuppressWarnings("unchecked")
        T value = (T) jsonUtil.getFilterValue(content, expression, idx);
        assertThat("JSON path \"" + this.expression + "\"", value, matcher);
    }

}
