package org.mdkt.compiler;

import evolution_impl.gpprograms.CompilationException;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.util.Arrays;

/**
 * Created by trung on 5/3/15.
 */
public class InMemoryJavaCompiler {
    static JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    private final DynamicClassLoader cl = new DynamicClassLoader(ClassLoader.getSystemClassLoader());

    public Class<?> compile(String className, String sourceCodeInText) throws Exception {
        SourceCode sourceCode = new SourceCode(className, sourceCodeInText);
        CompiledCode compiledCode = new CompiledCode(className);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sourceCode);
        ExtendedStandardJavaFileManager fileManager = new ExtendedStandardJavaFileManager(javac.getStandardFileManager(null, null, null), compiledCode, cl);
        JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, null, null, null, compilationUnits);
        boolean success = task.call();
        if(!success) {
            throw new CompilationException();
        }
        return cl.loadClass(className);
    }
}
