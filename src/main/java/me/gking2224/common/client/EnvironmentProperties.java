package me.gking2224.common.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvironmentProperties {

    @AliasFor("properties")
    String value() default "";
    
    @AliasFor("value")
    String properties() default "";
    
    String name() default "";
    
    String prefix() default "";

}
