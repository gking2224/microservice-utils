package me.gking2224.common.batch.generic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ContextResolvingHandler implements InvocationHandler {

    private Object delegate;
    private Map<String, Supplier<Boolean>> interceptors =
            new HashMap<String, Supplier<Boolean>>();

    public ContextResolvingHandler(Object delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        if (doIntercept(method.getName())) {
            try {
                return method.invoke(this.delegate, args);
            }
            catch(InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        else return null;
    }

    private Boolean doIntercept(final String method) {
        Supplier<Boolean> interceptor = interceptors.get(method);
        if (interceptor != null) {
            return interceptor.get();
        }
        else return Boolean.TRUE;
    }

    public ContextResolvingHandler intercept(String methodName, Supplier<Boolean> supplier) {
        this.interceptors.put(methodName, supplier);
        return this;
    }
}
