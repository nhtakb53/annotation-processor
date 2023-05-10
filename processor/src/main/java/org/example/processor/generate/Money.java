package org.example.processor.generate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // 애노테이션을 어디에 붙일 수 있는지 정의
@Retention(RetentionPolicy.SOURCE) // 애노테이션을 언제까지 유지할 것인지 정의
public @interface Money {
}
