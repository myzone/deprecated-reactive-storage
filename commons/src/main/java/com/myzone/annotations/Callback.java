package com.myzone.annotations;

import java.lang.annotation.*;

/**
 * @author myzone
 * @date 11.01.14
 */
@Inherited
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Callback {

}
