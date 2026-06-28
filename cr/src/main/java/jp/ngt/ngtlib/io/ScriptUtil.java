/*
 *
 *  * AppleExtended
 *  *
 *  * Original code (c) 2020 anatawa12 and other contributors.
 *  * Modifications (c) 2026 Applepie.
 *  *
 *  * This file is part of AppleExtended, which is a derivative work of fixRTM.
 *  * Both are licensed under the GNU Lesser General Public License version 3.
 *  * See LICENSE.txt in the mod root for full license text.
 *
 *
 */

package jp.ngt.ngtlib.io;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.util.ResourceLocation;

import javax.script.*;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScriptUtil {
    private static ScriptEngineManager SEM;
    private static final Object GRAAL_ENGINE_LOCK = new Object();
    private static volatile Object SHARED_GRAAL_POLYGLOT_ENGINE;
    private static volatile ClassLoader SHARED_GRAAL_POLYGLOT_ENGINE_LOADER;
    private static final String ECMASCRIPT_VERSION = "2021";
    private static final Pattern IMPORT_PACKAGE_PATTERN = Pattern.compile(
            "(?m)^\\s*importPackage\\s*\\(\\s*(?:Packages\\s*\\.\\s*)?([A-Za-z_$][A-Za-z0-9_$]*(?:\\s*\\.\\s*[A-Za-z_$][A-Za-z0-9_$]*)+)\\s*\\)\\s*;?\\s*(?://.*)?$");
    private static final Pattern IMPORT_CLASS_PATTERN = Pattern.compile(
            "(?m)^\\s*importClass\\s*\\(\\s*(?:Packages\\s*\\.\\s*)?([A-Za-z_$][A-Za-z0-9_$]*(?:\\s*\\.\\s*[A-Za-z_$][A-Za-z0-9_$]*)+)\\s*\\)\\s*;?\\s*(?://.*)?$");
    private static final Pattern IMPORT_PACKAGE_INLINE_PATTERN = Pattern.compile(
            "\\bimportPackage\\s*\\(\\s*(?:Packages\\s*\\.\\s*)?([A-Za-z_$][A-Za-z0-9_$]*(?:\\s*\\.\\s*[A-Za-z_$][A-Za-z0-9_$]*)+)\\s*\\)");
    private static final Pattern IMPORT_CLASS_INLINE_PATTERN = Pattern.compile(
            "\\bimportClass\\s*\\(\\s*(?:Packages\\s*\\.\\s*)?([A-Za-z_$][A-Za-z0-9_$]*(?:\\s*\\.\\s*[A-Za-z_$][A-Za-z0-9_$]*)+)\\s*\\)");
    private static final Pattern PACKAGES_CLASS_ACCESS_PATTERN = Pattern.compile(
            "\\bPackages\\s*\\.\\s*([a-z_$][A-Za-z0-9_$]*(?:\\s*\\.\\s*[a-z_$][A-Za-z0-9_$]*)*\\s*\\.\\s*[A-Z][A-Za-z0-9$]*)");
    private static final Pattern GL11_SCALEF_CALL_PATTERN = Pattern.compile("\\bGL11\\s*\\.\\s*glScalef\\s*\\(");
    private static final Pattern GL11_TRANSLATEF_CALL_PATTERN = Pattern.compile("\\bGL11\\s*\\.\\s*glTranslatef\\s*\\(");
    private static final Pattern GL11_ROTATEF_CALL_PATTERN = Pattern.compile("\\bGL11\\s*\\.\\s*glRotatef\\s*\\(");
    private static final String LEGACY_PACKAGE_GLOBALS_BOOTSTRAP =
            "if (typeof Packages !== 'undefined') {" +
                    "if (typeof jp === 'undefined') { var jp = Packages.jp; }" +
                    "if (typeof net === 'undefined') { var net = Packages.net; }" +
                    "if (typeof cpw === 'undefined') { var cpw = Packages.cpw; }" +
                    "if (typeof mods === 'undefined') { var mods = Packages.mods; }" +
                    "if (typeof scala === 'undefined') { var scala = Packages.scala; }" +
                    "if (typeof kotlin === 'undefined') { var kotlin = Packages.kotlin; }" +
                    "}";
    private static final String GRAAL_EVAL_COMPAT_BOOTSTRAP =
            "(function () {" +
                    "if (typeof globalThis === 'undefined') { return; }" +
                    "if (globalThis.__rtmEvalCompatInstalled === true) { return; }" +
                    "var rawEval = globalThis.eval;" +
                    "if (typeof rawEval !== 'function') { return; }" +
                    "var normalize = function (code) {" +
                    "if (typeof code !== 'string') { return code; }" +
                    "if (code.indexOf('Packages.') < 0 && code.indexOf('importPackage(') < 0 && code.indexOf('importClass(') < 0) { return code; }" +
                    "code = code.replace(/^\\s*importPackage\\s*\\(\\s*(?:Packages\\s*\\.\\s*)?([A-Za-z_$][A-Za-z0-9_$]*(?:\\s*\\.\\s*[A-Za-z_$][A-Za-z0-9_$]*)+)\\s*\\)\\s*;?\\s*(?:\\/\\/.*)?$/gm, function (_, fqn) {" +
                    "var p = String(fqn).replace(/\\s+/g, '');" +
                    "return 'importPackage(\"' + p + '\");';" +
                    "});" +
                    "code = code.replace(/^\\s*importClass\\s*\\(\\s*(?:Packages\\s*\\.\\s*)?([A-Za-z_$][A-Za-z0-9_$]*(?:\\s*\\.\\s*[A-Za-z_$][A-Za-z0-9_$]*)+)\\s*\\)\\s*;?\\s*(?:\\/\\/.*)?$/gm, function (_, fqn) {" +
                    "var c = String(fqn).replace(/\\s+/g, '');" +
                    "return 'importClass(Java.type(\"' + c + '\"));';" +
                    "});" +
                    "code = code.replace(/\\bimportPackage\\s*\\(\\s*(?:Packages\\s*\\.\\s*)?([A-Za-z_$][A-Za-z0-9_$]*(?:\\s*\\.\\s*[A-Za-z_$][A-Za-z0-9_$]*)+)\\s*\\)/g, function (_, fqn) {" +
                    "var p = String(fqn).replace(/\\s+/g, '');" +
                    "return 'importPackage(\"' + p + '\")';" +
                    "});" +
                    "code = code.replace(/\\bimportClass\\s*\\(\\s*(?:Packages\\s*\\.\\s*)?([A-Za-z_$][A-Za-z0-9_$]*(?:\\s*\\.\\s*[A-Za-z_$][A-Za-z0-9_$]*)+)\\s*\\)/g, function (_, fqn) {" +
                    "var c = String(fqn).replace(/\\s+/g, '');" +
                    "return 'importClass(Java.type(\"' + c + '\"))';" +
                    "});" +
                    "code = code.replace(/\\bPackages\\s*\\.\\s*([a-z_$][A-Za-z0-9_$]*(?:\\s*\\.\\s*[a-z_$][A-Za-z0-9_$]*)*\\s*\\.\\s*[A-Z][A-Za-z0-9$]*)/g, function (_, fqn) {" +
                    "var c = String(fqn).replace(/\\s+/g, '');" +
                    "return '(Java.type(\"' + c + '\"))';" +
                    "});" +
                    "code = code.replace(/\\bGL11\\s*\\.\\s*glScalef\\s*\\(/g, '__rtm_glScalef(');" +
                    "code = code.replace(/\\bGL11\\s*\\.\\s*glTranslatef\\s*\\(/g, '__rtm_glTranslatef(');" +
                    "code = code.replace(/\\bGL11\\s*\\.\\s*glRotatef\\s*\\(/g, '__rtm_glRotatef(');" +
                    "return code;" +
                    "};" +
                    "globalThis.eval = function (code) {" +
                    "if (typeof code !== 'string') { return rawEval(code); }" +
                    "return rawEval(normalize(code));" +
                    "};" +
                    "globalThis.__rtmEvalCompatInstalled = true;" +
                    "})();";
    private static final String GRAAL_GL11_ARRAY_COMPAT_BOOTSTRAP =
            "(function () {" +
                    "if (typeof globalThis === 'undefined') { return; }" +
                    "if (globalThis.__rtmGL11ArrayCompatInstalled === true) { return; }" +
                    "var resolveGL11 = function () {" +
                    "if (typeof GL11 !== 'undefined' && GL11 !== null) { return GL11; }" +
                    "if (typeof Java !== 'undefined' && Java && typeof Java.type === 'function') {" +
                    "try { return Java.type('org.lwjgl.opengl.GL11'); } catch (e) {}" +
                    "}" +
                    "return null;" +
                    "};" +
                    "var isMissing = function (v) {" +
                    "try { return v === undefined || v === null || String(v) === 'undefined'; }" +
                    "catch (e) { return v === undefined || v === null; }" +
                    "};" +
                    "var make3 = function (x, y, z) {" +
                    "var n0 = Number(x), n1 = Number(y), n2 = Number(z);" +
                    "if (isNaN(n0) || isNaN(n1) || isNaN(n2)) { return null; }" +
                    "return [n0, n1, n2];" +
                    "};" +
                    "var make4 = function (a0, a1, a2, a3) {" +
                    "var n0 = Number(a0), n1 = Number(a1), n2 = Number(a2), n3 = Number(a3);" +
                    "if (isNaN(n0) || isNaN(n1) || isNaN(n2) || isNaN(n3)) { return null; }" +
                    "return [n0, n1, n2, n3];" +
                    "};" +
                    "var unpack3 = function (v) {" +
                    "if (isMissing(v)) { return null; }" +
                    "try {" +
                    "if (Array.isArray(v)) {" +
                    "if (v.length === 1) { return unpack3(v[0]); }" +
                    "if (v.length >= 3) { var r0 = make3(v[0], v[1], v[2]); if (r0 !== null) { return r0; } }" +
                    "}" +
                    "} catch (e) {}" +
                    "try { var x = v[0], y = v[1], z = v[2]; if (!isMissing(x) && !isMissing(y) && !isMissing(z)) { var r1 = make3(x, y, z); if (r1 !== null) { return r1; } } } catch (e) {}" +
                    "try {" +
                    "if (typeof v.get === 'function' && typeof v.size === 'function') {" +
                    "var sz = v.size();" +
                    "if (sz === 1) { return unpack3(v.get(0)); }" +
                    "if (sz >= 3) { var r2 = make3(v.get(0), v.get(1), v.get(2)); if (r2 !== null) { return r2; } }" +
                    "}" +
                    "} catch (e) {}" +
                    "try {" +
                    "if (typeof Java !== 'undefined' && Java && typeof Java.type === 'function') {" +
                    "var JArray = Java.type('java.lang.reflect.Array');" +
                    "var len = JArray.getLength(v);" +
                    "if (len === 1) { return unpack3(JArray.get(v, 0)); }" +
                    "if (len >= 3) { var r3 = make3(JArray.get(v, 0), JArray.get(v, 1), JArray.get(v, 2)); if (r3 !== null) { return r3; } }" +
                    "}" +
                    "} catch (e) {}" +
                    "return null;" +
                    "};" +
                    "var unpack4 = function (v) {" +
                    "if (isMissing(v)) { return null; }" +
                    "try {" +
                    "if (Array.isArray(v)) {" +
                    "if (v.length === 1) { return unpack4(v[0]); }" +
                    "if (v.length >= 4) { var r0 = make4(v[0], v[1], v[2], v[3]); if (r0 !== null) { return r0; } }" +
                    "}" +
                    "} catch (e) {}" +
                    "try { var a0 = v[0], a1 = v[1], a2 = v[2], a3 = v[3]; if (!isMissing(a0) && !isMissing(a1) && !isMissing(a2) && !isMissing(a3)) { var r1 = make4(a0, a1, a2, a3); if (r1 !== null) { return r1; } } } catch (e) {}" +
                    "try {" +
                    "if (typeof v.get === 'function' && typeof v.size === 'function') {" +
                    "var sz4 = v.size();" +
                    "if (sz4 === 1) { return unpack4(v.get(0)); }" +
                    "if (sz4 >= 4) { var r2 = make4(v.get(0), v.get(1), v.get(2), v.get(3)); if (r2 !== null) { return r2; } }" +
                    "}" +
                    "} catch (e) {}" +
                    "try {" +
                    "if (typeof Java !== 'undefined' && Java && typeof Java.type === 'function') {" +
                    "var JArray4 = Java.type('java.lang.reflect.Array');" +
                    "var len4 = JArray4.getLength(v);" +
                    "if (len4 === 1) { return unpack4(JArray4.get(v, 0)); }" +
                    "if (len4 >= 4) { var r3 = make4(JArray4.get(v, 0), JArray4.get(v, 1), JArray4.get(v, 2), JArray4.get(v, 3)); if (r3 !== null) { return r3; } }" +
                    "}" +
                    "} catch (e) {}" +
                    "return null;" +
                    "};" +
                    "var call3 = function (method, a, b, c) {" +
                    "var gl = resolveGL11();" +
                    "if (gl === null || typeof gl[method] !== 'function') { return null; }" +
                    "if (isMissing(b) && isMissing(c)) {" +
                    "var u3 = unpack3(a);" +
                    "if (u3 !== null) {" +
                    "try { return gl[method](u3[0], u3[1], u3[2]); } catch (e0) { return null; }" +
                    "}" +
                    "if (typeof a === 'number' && !isNaN(a)) {" +
                    "try { return gl[method](a, a, a); } catch (e1) { return null; }" +
                    "}" +
                    "return null;" +
                    "}" +
                    "try { return gl[method](a, b, c); }" +
                    "catch (e2) {" +
                    "var u3b = unpack3([a, b, c]);" +
                    "if (u3b !== null) { try { return gl[method](u3b[0], u3b[1], u3b[2]); } catch (e3) {} }" +
                    "return null;" +
                    "}" +
                    "};" +
                    "var call4 = function (method, a, b, c, d) {" +
                    "var gl = resolveGL11();" +
                    "if (gl === null || typeof gl[method] !== 'function') { return null; }" +
                    "if (isMissing(b) && isMissing(c) && isMissing(d)) {" +
                    "var u4 = unpack4(a);" +
                    "if (u4 !== null) { try { return gl[method](u4[0], u4[1], u4[2], u4[3]); } catch (e0) { return null; } }" +
                    "return null;" +
                    "}" +
                    "try { return gl[method](a, b, c, d); }" +
                    "catch (e1) {" +
                    "var u4b = unpack4([a, b, c, d]);" +
                    "if (u4b !== null) { try { return gl[method](u4b[0], u4b[1], u4b[2], u4b[3]); } catch (e2) {} }" +
                    "return null;" +
                    "}" +
                    "};" +
                    "globalThis.__rtm_glScalef = function (a, b, c) { return call3('glScalef', a, b, c); };" +
                    "globalThis.__rtm_glTranslatef = function (a, b, c) { return call3('glTranslatef', a, b, c); };" +
                    "globalThis.__rtm_glRotatef = function (a, b, c, d) { return call4('glRotatef', a, b, c, d); };" +
                    "globalThis.__rtmGL11ArrayCompatInstalled = true;" +
                    "})();";
    private static final String[] GRAAL_ENGINE_NAMES = new String[]{
            "Graal.js",
            "graal.js"
    };

    private static void init() {
        LaunchClassLoader loader = Launch.classLoader;
        if (loader != null) {
            loader.addClassLoaderExclusion("jdk.nashorn.");
        }

        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");

        ClassLoader classLoader = resolveScriptHostClassLoader();
        SEM = new ScriptEngineManager(classLoader);
    }

    private static void showScripts(ScriptEngineManager mng) {
        NGTLog.debug("SEF");
        for (ScriptEngineFactory factory : mng.getEngineFactories()) {
            System.out.println("Engine name: " + factory.getEngineName());
            System.out.println("Engine version: " + factory.getEngineVersion());
            System.out.println("Language name: " + factory.getLanguageName());
            System.out.println("Language version: " + factory.getLanguageVersion());

            for (String extension : factory.getExtensions()) {
                System.out.println("Extension: " + extension);
            }
            for (String mimeType : factory.getMimeTypes()) {
                System.out.println("MimeType: " + mimeType);
            }
            for (String name : factory.getNames()) {
                System.out.println("Short name: " + name);
            }
            System.out.println();
        }
    }

    public static ScriptEngine doScript(String s) {
        return doScript(s, null);
    }

    public static ScriptEngine doScript(String s, String fileName) {
        if (SEM == null) {
            init();
        }

        com.anatawa12.fixRtm.ngtlib.io.ScriptUtil.INSTANCE.prepareSystemProperty();
        ScriptEngine se = createScriptEngine();
        if (se == null) {
            String engines = SEM.getEngineFactories()
                    .stream()
                    .map(ScriptEngineFactory::getEngineName)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("No JavaScript ScriptEngine found. Available engines: " + engines);
        }

        try {
            applyScriptEngineOptions(se);
            if (!isGraalEngine(se)) {
                throw new IllegalStateException("Only GraalJS is allowed, but got: " + se.getClass().getName());
            }
            if (fileName != null) {
                se.put(com.anatawa12.fixRtm.scripting.FIXScriptUtil.SCRIPT_NAME_PROPERTY, fileName);
            }

            try {
                se.eval("load(\"nashorn:mozilla_compat.js\");");
            } catch (ScriptException ignored) {
                NGTLog.debug("mozilla_compat.js is not available on current ScriptEngine.");
            }

            applyLegacyPackageGlobalsBootstrap(se);
            installGraalEvalCompatibilityHooks(se);
            installGraalGL11ArrayCompatibilityHooks(se);
            se.eval(normalizeLegacyImports(s));
            installGraalGL11ArrayCompatibilityHooks(se);
            return se;
        } catch (ScriptException e) {
            throw new RuntimeException("Script exec error: " + fileName, e);
        }
    }

    private static ScriptEngine createScriptEngine() {
        ScriptEngine graalEngine = createReflectedGraalEngineSafely();
        if (graalEngine != null) {
            return graalEngine;
        }

        ScriptEngine factoryEngine = createFromGraalFactories(SEM);
        if (factoryEngine != null) {
            return factoryEngine;
        }

        for (ClassLoader classLoader : getCandidateClassLoaders()) {
            if (classLoader == null) {
                continue;
            }
            ScriptEngineManager manager = new ScriptEngineManager(classLoader);
            ScriptEngine found = createFromGraalFactories(manager);
            if (found != null) {
                SEM = manager;
                return found;
            }
        }

        if (SEM != null && !SEM.getEngineFactories().isEmpty()) {
            List<String> available = SEM.getEngineFactories()
                    .stream()
                    .map(factory -> factory.getEngineName() + " (" + factory.getClass().getName() + ")")
                    .collect(Collectors.toList());
            NGTLog.debug("No GraalJS ScriptEngine found. Available engines: %s", available);
        }
        return null;
    }

    private static ScriptEngine createFromGraalFactories(ScriptEngineManager manager) {
        if (manager == null) {
            return null;
        }

        for (String name : GRAAL_ENGINE_NAMES) {
            ScriptEngine found = manager.getEngineByName(name);
            if (isGraalEngine(found)) {
                NGTLog.debug("ScriptEngine selected by Graal name: %s (%s)", name, found.getClass().getName());
                return found;
            }
        }

        for (ScriptEngineFactory factory : manager.getEngineFactories()) {
            if (!isGraalFactory(factory)) {
                continue;
            }
            try {
                ScriptEngine found = factory.getScriptEngine();
                if (isGraalEngine(found)) {
                    NGTLog.debug("ScriptEngine selected by Graal factory: %s", factory.getClass().getName());
                    return found;
                }
            } catch (Throwable t) {
                NGTLog.debug("Failed to create GraalJS engine from factory %s: %s", factory.getClass().getName(), t.toString());
            }
        }

        return null;
    }

    private static boolean isGraalFactory(ScriptEngineFactory factory) {
        if (factory == null) {
            return false;
        }

        String className = factory.getClass().getName().toLowerCase(Locale.ROOT);
        if (className.contains("graal") || className.contains("truffle")) {
            return true;
        }

        String engineName = factory.getEngineName();
        return engineName != null && engineName.toLowerCase(Locale.ROOT).contains("graal");
    }

    private static boolean isGraalEngine(ScriptEngine engine) {
        if (engine == null) {
            return false;
        }

        String className = engine.getClass().getName().toLowerCase(Locale.ROOT);
        return className.contains("graal") || className.contains("truffle");
    }

    private static ScriptEngine createReflectedGraalEngineSafely() {
        try {
            return createReflectedGraalEngine();
        } catch (ClassNotFoundException ignored) {
            return null;
        } catch (Throwable t) {
            NGTLog.debug("Failed to initialize reflected GraalJS engine: %s", t.toString());
            return null;
        }
    }

    private static ScriptEngine createReflectedGraalEngine() throws Exception {
        ClassLoader hostClassLoader = resolveScriptHostClassLoader();
        Class<?> engineClass = loadClassForScriptRuntime("org.graalvm.polyglot.Engine", hostClassLoader);
        Object polyglotEngine = getOrCreateSharedGraalPolyglotEngine(engineClass, hostClassLoader);

        Class<?> contextClass = loadClassForScriptRuntime("org.graalvm.polyglot.Context", hostClassLoader);
        Object contextBuilder = contextClass.getMethod("newBuilder", String[].class).invoke(null, (Object) new String[]{"js"});
        Class<?> contextBuilderClass = contextBuilder.getClass();

        contextBuilderClass.getMethod("allowAllAccess", boolean.class).invoke(contextBuilder, true);
        contextBuilderClass.getMethod("allowExperimentalOptions", boolean.class).invoke(contextBuilder, true);
        try {
            contextBuilderClass.getMethod("hostClassLoader", ClassLoader.class).invoke(contextBuilder, hostClassLoader);
        } catch (NoSuchMethodException ignored) {
        }
        try {
            Class<?> predicateClass = Class.forName("java.util.function.Predicate");
            Object allowAllPredicate = Proxy.newProxyInstance(
                    ScriptUtil.class.getClassLoader(),
                    new Class<?>[]{predicateClass},
                    (proxy, method, args) -> {
                        if ("test".equals(method.getName())) return true;
                        if ("toString".equals(method.getName())) return "AllowAllHostClassLookup";
                        if ("hashCode".equals(method.getName())) return System.identityHashCode(proxy);
                        if ("equals".equals(method.getName())) return proxy == args[0];
                        return null;
                    }
            );
            contextBuilderClass.getMethod("allowHostClassLookup", predicateClass).invoke(contextBuilder, allowAllPredicate);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        }
        contextBuilderClass.getMethod("option", String.class, String.class).invoke(contextBuilder, "js.java-package-globals", "true");
        contextBuilderClass.getMethod("option", String.class, String.class).invoke(contextBuilder, "js.nashorn-compat", "true");
        contextBuilderClass.getMethod("option", String.class, String.class).invoke(contextBuilder, "js.ecmascript-version", ECMASCRIPT_VERSION);

        Class<?> graalEngineClass = loadClassForScriptRuntime("com.oracle.truffle.js.scriptengine.GraalJSScriptEngine", hostClassLoader);
        Object scriptEngine = graalEngineClass
                .getMethod("create", engineClass, contextBuilderClass)
                .invoke(null, polyglotEngine, contextBuilder);
        if (scriptEngine instanceof ScriptEngine) {
            NGTLog.debug("ScriptEngine selected: reflected GraalJSScriptEngine.");
            return (ScriptEngine) scriptEngine;
        }
        return null;
    }

    private static Object getOrCreateSharedGraalPolyglotEngine(Class<?> engineClass, ClassLoader hostClassLoader) throws Exception {
        Object existingEngine = SHARED_GRAAL_POLYGLOT_ENGINE;
        ClassLoader existingLoader = SHARED_GRAAL_POLYGLOT_ENGINE_LOADER;
        if (existingEngine != null && existingLoader == hostClassLoader) {
            return existingEngine;
        }

        synchronized (GRAAL_ENGINE_LOCK) {
            existingEngine = SHARED_GRAAL_POLYGLOT_ENGINE;
            existingLoader = SHARED_GRAAL_POLYGLOT_ENGINE_LOADER;
            if (existingEngine != null && existingLoader == hostClassLoader) {
                return existingEngine;
            }

            Object engineBuilder = engineClass.getMethod("newBuilder").invoke(null);
            Class<?> builderClass = engineBuilder.getClass();
            builderClass.getMethod("allowExperimentalOptions", boolean.class).invoke(engineBuilder, true);
            try {
                builderClass.getMethod("option", String.class, String.class)
                        .invoke(engineBuilder, "engine.WarnInterpreterOnly", "false");
            } catch (Throwable ignored) {
            }

            Object newEngine = builderClass.getMethod("build").invoke(engineBuilder);
            SHARED_GRAAL_POLYGLOT_ENGINE = newEngine;
            SHARED_GRAAL_POLYGLOT_ENGINE_LOADER = hostClassLoader;
            return newEngine;
        }
    }

    private static ClassLoader resolveScriptHostClassLoader() {
        if (Launch.classLoader != null) {
            return Launch.classLoader;
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader;
        }
        return ScriptUtil.class.getClassLoader();
    }

    private static List<ClassLoader> getCandidateClassLoaders() {
        Set<ClassLoader> ordered = new LinkedHashSet<>();
        if (Launch.classLoader != null) {
            ordered.add(Launch.classLoader);
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            ordered.add(contextClassLoader);
        }
        ClassLoader scriptUtilLoader = ScriptUtil.class.getClassLoader();
        if (scriptUtilLoader != null) {
            ordered.add(scriptUtilLoader);
        }
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        if (systemClassLoader != null) {
            ordered.add(systemClassLoader);
        }
        return new ArrayList<>(ordered);
    }

    private static Class<?> loadClassForScriptRuntime(String className, ClassLoader preferredLoader) throws ClassNotFoundException {
        Set<ClassLoader> loaders = new LinkedHashSet<>();
        if (preferredLoader != null) {
            loaders.add(preferredLoader);
        }
        loaders.addAll(getCandidateClassLoaders());

        for (ClassLoader loader : loaders) {
            if (loader == null) {
                continue;
            }
            try {
                return Class.forName(className, true, loader);
            } catch (ClassNotFoundException ignored) {
            }
        }

        return Class.forName(className);
    }

    private static void applyScriptEngineOptions(ScriptEngine scriptEngine) {
        if (scriptEngine == null) {
            return;
        }

        try {
            Bindings bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
            if (bindings == null) {
                return;
            }
            bindings.put("polyglot.js.allowAllAccess", true);
            bindings.put("polyglot.js.allowHostAccess", true);
            bindings.put("polyglot.js.allowHostClassLookup", true);
            bindings.put("polyglot.js.nashorn-compat", true);
            bindings.put("polyglot.js.ecmascript-version", ECMASCRIPT_VERSION);
        } catch (Throwable ignored) {
        }
    }

    private static String normalizeLegacyImports(String script) {
        if (script == null || script.isEmpty()) {
            return script;
        }

        String normalized = rewriteLegacyImportStatements(script, IMPORT_PACKAGE_PATTERN, true);
        normalized = rewriteLegacyImportStatements(normalized, IMPORT_CLASS_PATTERN, false);
        normalized = rewriteLegacyImportStatements(normalized, IMPORT_PACKAGE_INLINE_PATTERN, true);
        normalized = rewriteLegacyImportStatements(normalized, IMPORT_CLASS_INLINE_PATTERN, false);
        normalized = rewritePackagesClassAccess(normalized);
        normalized = rewriteGL11CompatibilityCalls(normalized);
        return normalized;
    }

    private static String rewriteLegacyImportStatements(String script, Pattern pattern, boolean packageImport) {
        Matcher matcher = pattern.matcher(script);
        StringBuffer out = new StringBuffer(script.length());
        boolean replaced = false;
        while (matcher.find()) {
            String fqn = matcher.group(1).replaceAll("\\s+", "");
            String replacement = packageImport
                    ? "importPackage(\"" + fqn + "\");"
                    : "importClass(Java.type(\"" + fqn + "\"));";
            matcher.appendReplacement(out, Matcher.quoteReplacement(replacement));
            replaced = true;
        }
        if (!replaced) {
            return script;
        }
        matcher.appendTail(out);
        return out.toString();
    }

    private static String rewritePackagesClassAccess(String script) {
        Matcher matcher = PACKAGES_CLASS_ACCESS_PATTERN.matcher(script);
        StringBuffer out = new StringBuffer(script.length());
        boolean replaced = false;
        while (matcher.find()) {
            String fqn = matcher.group(1).replaceAll("\\s+", "");
            String replacement = "(Java.type(\"" + fqn + "\"))";
            matcher.appendReplacement(out, Matcher.quoteReplacement(replacement));
            replaced = true;
        }
        if (!replaced) {
            return script;
        }
        matcher.appendTail(out);
        return out.toString();
    }

    private static String rewriteGL11CompatibilityCalls(String script) {
        String rewritten = GL11_SCALEF_CALL_PATTERN.matcher(script).replaceAll("__rtm_glScalef(");
        rewritten = GL11_TRANSLATEF_CALL_PATTERN.matcher(rewritten).replaceAll("__rtm_glTranslatef(");
        rewritten = GL11_ROTATEF_CALL_PATTERN.matcher(rewritten).replaceAll("__rtm_glRotatef(");
        return rewritten;
    }

    private static void applyLegacyPackageGlobalsBootstrap(ScriptEngine scriptEngine) {
        if (scriptEngine == null) {
            return;
        }
        if (!isGraalEngine(scriptEngine)) {
            return;
        }

        try {
            scriptEngine.eval(LEGACY_PACKAGE_GLOBALS_BOOTSTRAP);
        } catch (ScriptException e) {
            NGTLog.debug("Failed to apply GraalJS legacy package globals bootstrap: %s", e.toString());
        }
    }

    private static void installGraalEvalCompatibilityHooks(ScriptEngine scriptEngine) {
        if (scriptEngine == null) {
            return;
        }
        if (!isGraalEngine(scriptEngine)) {
            return;
        }

        try {
            scriptEngine.eval(GRAAL_EVAL_COMPAT_BOOTSTRAP);
        } catch (ScriptException e) {
            NGTLog.debug("Failed to install GraalJS eval compatibility hooks: %s", e.toString());
        }
    }

    private static void installGraalGL11ArrayCompatibilityHooks(ScriptEngine scriptEngine) {
        if (scriptEngine == null) {
            return;
        }
        if (!isGraalEngine(scriptEngine)) {
            return;
        }

        try {
            scriptEngine.eval(GRAAL_GL11_ARRAY_COMPAT_BOOTSTRAP);
        } catch (ScriptException e) {
            NGTLog.debug("Failed to install GraalJS GL11 array compatibility hooks: %s", e.toString());
        }
    }

    public static CompiledScript compile(ScriptEngine engine, ResourceLocation resource) {
        try {
            String script = NGTText.getText(resource, true);
            if (!(engine instanceof Compilable)) {
                throw new RuntimeException("Script engine is not compilable: " + engine.getClass().getName());
            }
            CompiledScript compiledScript = ((Compilable) engine).compile(script);
            compiledScript.eval();
            return compiledScript;
        } catch (IOException e) {
            throw new RuntimeException("Script load error : " + resource.getResourcePath(), e);
        } catch (ScriptException e) {
            throw new RuntimeException("Script load error : " + resource.getResourcePath(), e);
        }
    }

    public static Object doScriptFunction(ScriptEngine se, String func, Object... args) {
        try {
            return ((Invocable) se).invokeFunction(func, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Script exec error : " + func + " in file: " + scriptName(se), e);
        } catch (ScriptException e) {
            throw new RuntimeException("Script exec error : " + func + " in file: " + scriptName(se), e);
        }
    }

    private static String scriptName(ScriptEngine engine) {
        try {
            Object name = engine.get(com.anatawa12.fixRtm.scripting.FIXScriptUtil.SCRIPT_NAME_PROPERTY);
            if (name == null) return null;
            return name.toString();
        } catch (Throwable t) {
            return null;
        }
    }

    public static Object doScriptIgnoreError(ScriptEngine se, String func, Object... args) {
        try {
            return doScriptFunction(se, func, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getScriptField(ScriptEngine se, String fieldName) {
        return se.get(fieldName);
    }

}
