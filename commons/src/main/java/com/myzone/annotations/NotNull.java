package com.myzone.annotations;

import java.lang.annotation.*;

/**
 * @author myzone
 * @date 11.01.14
 */
@Inherited
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {

}
