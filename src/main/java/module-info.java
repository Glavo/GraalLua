module org.glavo.lua {
    requires org.graalvm.truffle;

    provides com.oracle.truffle.api.TruffleLanguage.Provider
            with org.glavo.lua.LuaLanguageProvider;

    exports org.glavo.lua;
    exports org.glavo.lua.runtime;
}