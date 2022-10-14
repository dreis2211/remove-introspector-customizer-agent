package com.dreis.agent.introspector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * A {@link ClassFileTransformer} that removes support for looking up
 * {@link java.beans.Customizer} classes for bean infos that in 99% of
 * cases might not exist
 */
public class RemoveIntrospectorCustomizerAgent implements ClassFileTransformer {

	private final ClassPool pool = new ClassPool(true);

	public static void premain(String agentArgs, Instrumentation inst) {
		inst.addTransformer(new RemoveIntrospectorCustomizerAgent(), true);
	}

	public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)  {
		try {
			if (className.equals("java/beans/Introspector")) {
				CtClass klass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
				CtMethod[] methods = klass.getDeclaredMethods();
				for (int i = 0; i < methods.length; i++) {
					if (methods[i].getName().contains("findCustomizerClass")) {
						methods[i].setBody("{ return null; }");
					}
				}
				return klass.toBytecode();
			} else {
				return null;
			}
		} catch (Throwable t) {
			return null;
		}
	}

}
