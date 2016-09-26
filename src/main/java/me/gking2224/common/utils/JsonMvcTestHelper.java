package me.gking2224.common.utils;

import static org.junit.Assert.fail;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.util.function.Function;

import org.springframework.http.MediaType;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonMvcTestHelper {

    private static JsonUtil json = new JsonUtil();

    public static ResultActions doGet(MockMvc mockMvc, String url, Function<ResultActions, ResultActions> checks) throws Exception {
        return doMethod(mockMvc, url, checks,  get(url));
    }

    public static ResultActions doPost(MockMvc mockMvc, Object obj, String url, Function<ResultActions, ResultActions> checks)
            throws JsonProcessingException, Exception {
        return doMethod(mockMvc, obj, url, checks,  post(url));
    }

    public static ResultActions doPut(MockMvc mockMvc, Object obj, String url, Function<ResultActions, ResultActions> checks)
            throws JsonProcessingException, Exception {
        return doMethod(mockMvc, obj, url, checks,  put(url));
    }

    public static ResultActions doDelete(MockMvc mockMvc, Object obj, String url, Function<ResultActions, ResultActions> checks)
            throws JsonProcessingException, Exception {
        return doMethod(mockMvc, obj, url, checks,  delete(url));
    }

    public static ResultActions doMethod(
            MockMvc mockMvc, String url, Function<ResultActions, ResultActions> checks, MockHttpServletRequestBuilder builder) throws Exception {
        return doMethod(mockMvc, null, url, checks, builder);
    }

    public static ResultActions doMethod(
            MockMvc mockMvc, Object content, String url, Function<ResultActions, ResultActions> checks, MockHttpServletRequestBuilder builder) throws Exception {
        if (content != null) {
            builder = builder.content(json.objectToJson(content));
        }
        ResultActions result = mockMvc.perform(builder.header(CONTENT_TYPE, APPLICATION_JSON_VALUE).accept(APPLICATION_JSON_VALUE));
        return checks.apply(result);
    }
    
    public static ResultActions expect4xx(ResultActions t) {
        try {
            return t.andExpect(status().is4xxClientError()).andExpect(content().contentType(APPLICATION_JSON_VALUE));
        } catch (Exception e) {
            fail(e.getMessage());
            return null;
        }
    }
    
    public static ResultActions expectOK(ResultActions t) {
        try {
            return t.andExpect(status().isOk()).andExpect(anyOf(content().contentType(APPLICATION_JSON_VALUE), content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)));
        } catch (Exception e) {
            fail(e.getMessage());
            return null;
        }
    }
    
    public static String responseContent(MvcResult m) throws UnsupportedEncodingException {
        return m.getResponse().getContentAsString();
    }
    
    public static ResultMatcher anyOf(final ResultMatcher... matchers){
        return new ResultMatcher() {

            @Override
            public void match(MvcResult result) throws Exception {
                boolean matches = false;
                for (ResultMatcher m : matchers) {
                    try {
                        m.match(result);
                        matches = true;
                    } catch (AssertionError e) {}
                }
                AssertionErrors.assertTrue("None of the given matchers matched", matches);
            }
        };
    }
}
