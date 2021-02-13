package org.glavo.lua.parser;

import java.lang.annotation.*;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.SOURCE)
public @interface Token {
    long EOF = 0L;
    long Undefined = -1L;
}
