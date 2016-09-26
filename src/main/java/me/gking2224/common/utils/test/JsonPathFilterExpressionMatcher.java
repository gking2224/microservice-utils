package me.gking2224.common.utils.test;

import java.io.UnsupportedEncodingException;

import org.hamcrest.Matcher;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;

// extension to handle filter expression result checks gracefully
public class JsonPathFilterExpressionMatcher extends JsonPathResultMatchers {

    private ExtendedJsonPathExpectationsHelper xJsonPathHelper;

    public JsonPathFilterExpressionMatcher(String expression, Object... args) {
        super(expression, args);
        this.xJsonPathHelper = new ExtendedJsonPathExpectationsHelper(expression, args);
    }
    public <T> ResultMatcher singleValue(final Matcher<T> matcher) {
        return new ResultMatcher() {
            @Override
            public void match(MvcResult result) throws Exception {
                String content = getContent(result);
                xJsonPathHelper.assertValue(content, matcher);
            }
        };
    }
    public <T> ResultMatcher arrayValue(final Matcher<T> matcher) {
        return new ResultMatcher() {
            @Override
            public void match(MvcResult result) throws Exception {
                String content = getContent(result);
                xJsonPathHelper.assertArrayValue(content, matcher);
            }
        };
    }

    private String getContent(MvcResult result) throws UnsupportedEncodingException {
        String content = result.getResponse().getContentAsString();
        return content;
    }
}