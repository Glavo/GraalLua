package org.glavo.lua.nodes;


import com.oracle.truffle.api.dsl.ImplicitCast;
import com.oracle.truffle.api.dsl.TypeCast;
import com.oracle.truffle.api.dsl.TypeCheck;
import com.oracle.truffle.api.dsl.TypeSystem;
import org.glavo.lua.runtime.LuaNil;

/**
 * ## Type System of Lua.
 *
 * References
 * - [Types and Values](https://www.lua.org/pil/2.html)
 * - [Type Coercion in Lua](https://developer.roblox.com/en-us/articles/Type-Coercion-in-Lua)
 * - [Coercion](http://www.lua.org/manual/5.1/manual.html#2.2.1)
 */


@TypeSystem({String.class, long.class, double.class, boolean.class})
public class LuaTypes {
    @TypeCheck(LuaNil.class)
    public static boolean isLuaNil(Object value) {
        return value == LuaNil.Nil;
    }

    @TypeCast(LuaNil.class)
    public static LuaNil asLuaNil(Object value) {
        assert isLuaNil(value);
        return LuaNil.Nil;
    }

    /**
     * Arithmetic Casting of String
     * Notice that Inequality Comparison should not coerce the types
     */
    @ImplicitCast
    public static long castLong(String value) {
        return Long.decode(value);
    }

    @ImplicitCast
    public static double castDouble(String value){
        return Double.parseDouble(value);
    }

    // Numbers to Strings
    @ImplicitCast
    public static String castString(long value){
        // Lua 5 seems to keep all format in decimal
        return Long.toString(value);
    }

    @ImplicitCast
    public static String castString(double value){
        return Double.toString(value);
    }

    @ImplicitCast
    public static String castString(boolean value) {
        return Boolean.toString(value);
    }

    // Truth values
    // This should not be implicit,
    // because Lua's short circuit operation returns
    // the original value rather than the truth value
    public static boolean truthValue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean)value;
        } else {
            return value != LuaNil.Nil;
        }
    }

}
