module org.glavo.lua {
    requires org.graalvm.truffle;

    provides com.oracle.truffle.api.TruffleLanguage
            with org.glavo.lua.LuaLanguage;

    exports org.glavo.lua;
    exports org.glavo.lua.data;
}