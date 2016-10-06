package me.gking2224.common.test.utils;


public class TestUtils {


    public static JsonPathFilterExpressionMatcher jsonFilterPath(String path) {
        return new JsonPathFilterExpressionMatcher(path);
    }

}
