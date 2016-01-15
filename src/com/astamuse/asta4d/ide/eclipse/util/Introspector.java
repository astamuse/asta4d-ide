package com.astamuse.asta4d.ide.eclipse.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Copied from Spring IDE and cut redundant logics off
 * 
 * @author e-ryu
 *
 */
public class Introspector {
    public static IMethod findMethod(IType type, String methodName) throws JavaModelException {

        for (IType itrType = type; itrType != null; itrType = getSuperType(itrType)) {
            IMethod method = findMethodOnType(itrType, methodName);
            if (method != null) {
                return method;
            }
        }
        for (IType interfaceType : getAllImplementedInterfaces(type)) {
            IMethod method = findMethod(interfaceType, methodName);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    private static IMethod findMethodOnType(IType type, String methodName) throws JavaModelException {
        for (IMethod method : getMethods(type)) {
            if (JdtUtils.getMethodName(method).equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    public static IMethod[] getMethods(IType type) throws JavaModelException {
        if (type == null) {
            return new IMethod[0];
        }
        if (type.isStructureKnown()) {
            IMethod[] methods = type.getMethods();

            if (JdtUtils.isAjdtProject(type.getResource())) {
                Set<IMethod> itdMethods = AjdtUtils.getDeclaredMethods(type);
                if (itdMethods.size() > 0) {
                    int i = methods.length;
                    IMethod[] allMethods = new IMethod[methods.length + itdMethods.size()];
                    System.arraycopy(methods, 0, allMethods, 0, methods.length);
                    for (IMethod method : itdMethods) {
                        allMethods[i++] = method;
                    }

                    methods = allMethods;
                }
            }

            return methods;
        }
        return new IMethod[0];
    }

    public static Set<IType> getAllImplementedInterfaces(IType type) {
        Set<IType> allInterfaces = new HashSet<IType>();
        try {
            while (type != null) {
                String[] interfaces = type.getSuperInterfaceTypeSignatures();
                if (interfaces != null) {
                    for (String iface : interfaces) {
                        String fqin = JdtUtils.resolveClassNameBySignature(iface, type);
                        IType interfaceType = type.getJavaProject().findType(fqin);
                        if (interfaceType != null) {
                            allInterfaces.add(interfaceType);
                        }

                    }
                }
                type = getSuperType(type);
            }
        } catch (JavaModelException e) {
            // BeansCorePlugin.log(e);
        }
        return allInterfaces;
    }

    /**
     * Returns the super type of the given type. This is using the type hierarchy engine that is passed as parameter, if not null
     */
    public static IType getSuperType(IType type) throws JavaModelException {
        if (type == null) {
            return null;
        }
        String name = type.getSuperclassName();
        if (name == null && !type.getFullyQualifiedName().equals(Object.class.getName())) {
            name = Object.class.getName();
        }
        if (name != null) {
            if (type.isBinary()) {
                return type.getJavaProject().findType(name);
            } else {
                String resolvedName = JdtUtils.resolveClassName(name, type);
                if (resolvedName != null) {
                    return type.getJavaProject().findType(resolvedName);
                }
            }
        }
        return null;
    }
}
