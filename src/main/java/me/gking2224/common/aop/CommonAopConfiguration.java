package me.gking2224.common.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(proxyTargetClass=true)
@ComponentScan("me.gking2224.common.aop")
public class CommonAopConfiguration {
}
