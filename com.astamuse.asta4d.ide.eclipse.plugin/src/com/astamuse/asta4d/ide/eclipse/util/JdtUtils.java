/*******************************************************************************
 * Copyright (c) 2005, 2009 Spring IDE Developers
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Spring IDE Developers - initial API and implementation
 *******************************************************************************/
package com.astamuse.asta4d.ide.eclipse.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * Copied from Spring IDE and cut redundant logics off
 * 
 * @author e-ryu
 *
 */
public class JdtUtils {

    private static final String AJDT_NATURE = "org.eclipse.ajdt.ui.ajnature";

    private static final boolean IS_AJDT_PRESENT = isAjdtPresent();

    /**
     * Returns the corresponding Java project or <code>null</code> a for given project.
     * 
     * @param project
     *            the project the Java project is requested for
     * @return the requested Java project or <code>null</code> if the Java project is not defined or the project is not accessible
     */
    public static IJavaProject getJavaProject(IProject project) {
        if (project.isAccessible()) {
            try {
                if (project.hasNature(JavaCore.NATURE_ID)) {
                    return (IJavaProject) project.getNature(JavaCore.NATURE_ID);
                }
            } catch (CoreException e) {
                // SpringCore.log("Error getting Java project for project '" + project.getName() + "'", e);
            }
        }
        return null;
    }

    /**
     * Returns the corresponding Java type for given full-qualified class name.
     * 
     * @param project
     *            the JDT project the class belongs to
     * @param className
     *            the full qualified class name of the requested Java type
     * @return the requested Java type or null if the class is not defined or the project is not accessible
     */
    public static IType getJavaType(IProject project, String className) {
        IJavaProject javaProject = JdtUtils.getJavaProject(project);

        if (className != null) {

            // For inner classes replace '$' by '.'
            String unchangedClassName = null;
            int pos = className.lastIndexOf('$');
            if (pos > 0) {
                unchangedClassName = className;
                className = className.replace('$', '.');
            }

            try {
                IType type = null;
                // First look for the type in the Java project
                if (javaProject != null) {
                    type = javaProject.findType(className, new NullProgressMonitor());

                    if (type == null && unchangedClassName != null) {
                        type = javaProject.findType(unchangedClassName, new NullProgressMonitor());
                    }

                    if (type != null) {
                        return type;
                    }
                }

                // Then look for the type in the referenced Java projects
                for (IProject refProject : project.getReferencedProjects()) {
                    IJavaProject refJavaProject = JdtUtils.getJavaProject(refProject);
                    if (refJavaProject != null) {
                        type = refJavaProject.findType(className);

                        if (type == null && unchangedClassName != null) {
                            type = javaProject.findType(unchangedClassName, new NullProgressMonitor());
                        }

                        if (type != null) {
                            return type;
                        }
                    }
                }

                // fall back and try to locate the class using AJDT
                return getAjdtType(project, className);
            } catch (CoreException e) {
                // SpringCore.log("Error getting Java type '" + className + "'", e);
            }
        }

        return null;
    }

    public static IType getAjdtType(IProject project, String className) {
        IJavaProject javaProject = getJavaProject(project);
        if (IS_AJDT_PRESENT && javaProject != null && className != null) {

            try {
                IType type = null;

                // First look for the type in the project
                if (isAjdtProject(project)) {
                    type = AjdtUtils.getAjdtType(project, className);
                    if (type != null) {
                        return type;
                    }
                }

                // Then look for the type in the referenced Java projects
                for (IProject refProject : project.getReferencedProjects()) {
                    if (isAjdtProject(refProject)) {
                        type = AjdtUtils.getAjdtType(refProject, className);
                        if (type != null) {
                            return type;
                        }
                    }
                }
            } catch (CoreException e) {
                // SpringCore.log("Error getting Java type '" + className + "'", e);
            }
        }
        return null;
    }

    public static boolean isAjdtPresent() {
        return Platform.getBundle("org.eclipse.ajdt.core") != null;
    }

    /**
     * Returns true if given resource's project is a ADJT project.
     */
    public static boolean isAjdtProject(IResource resource) {
        if (resource != null && resource.isAccessible()) {
            IProject project = resource.getProject();
            if (project != null) {
                try {
                    return project.hasNature(AJDT_NATURE);
                } catch (CoreException e) {
                    // SpringCore.log(e);
                }
            }
        }
        return false;
    }

    public static String getMethodName(IMethod method) {
        // Special support Ajdt intertype declarations
        String methodName = method.getElementName();
        int index = methodName.lastIndexOf('.');
        if (index > 0) {
            methodName = methodName.substring(index + 1);
        }
        return methodName;
    }

    public static String resolveClassNameBySignature(String className, IType type) {
        // in case the type is already resolved
        if (className != null && className.length() > 0 && className.charAt(0) == Signature.C_RESOLVED) {
            return Signature.toString(className).replace('$', '.');
        }
        // otherwise do the resolving
        else {
            className = Signature.toString(className).replace('$', '.');
            return resolveClassName(className, type);
        }
    }

    public static String resolveClassName(String className, IType type) {
        if (className == null || type == null) {
            return className;
        }
        // replace binary $ inner class name syntax with . for source level
        className = className.replace('$', '.');
        String dotClassName = new StringBuilder().append('.').append(className).toString();

        IProject project = type.getJavaProject().getProject();

        try {
            // Special handling for some well-know classes
            if (className.startsWith("java.lang") && getJavaType(project, className) != null) {
                return className;
            }

            // Check if the class is imported
            if (!type.isBinary()) {

                // Strip className to first segment to support ReflectionUtils.MethodCallback
                int ix = className.lastIndexOf('.');
                String firstClassNameSegment = className;
                if (ix > 0) {
                    firstClassNameSegment = className.substring(0, ix);
                }

                // Iterate the imports
                for (IImportDeclaration importDeclaration : type.getCompilationUnit().getImports()) {
                    String importName = importDeclaration.getElementName();
                    // Wildcard imports -> check if the package + className is a valid type
                    if (importDeclaration.isOnDemand()) {
                        String newClassName = new StringBuilder(importName.substring(0, importName.length() - 1)).append(className)
                                .toString();
                        if (getJavaType(project, newClassName) != null) {
                            return newClassName;
                        }
                    }
                    // Concrete import matching .className at the end -> check if type exists
                    else if (importName.endsWith(dotClassName) && getJavaType(project, importName) != null) {
                        return importName;
                    }
                    // Check if className is multi segmented (ReflectionUtils.MethodCallback)
                    // -> check if the first segment
                    else if (!className.equals(firstClassNameSegment)) {
                        if (importName.endsWith(firstClassNameSegment)) {
                            String newClassName = new StringBuilder(importName.substring(0, importName.lastIndexOf('.') + 1))
                                    .append(className).toString();
                            if (getJavaType(project, newClassName) != null) {
                                return newClassName;
                            }
                        }
                    }
                }
            }

            // Check if the class is in the same package as the type
            String packageName = type.getPackageFragment().getElementName();
            String newClassName = new StringBuilder(packageName).append(dotClassName).toString();
            if (getJavaType(project, newClassName) != null) {
                return newClassName;
            }

            // Check if the className is sufficient (already fully-qualified)
            if (getJavaType(project, className) != null) {
                return className;
            }

            // Check if the class is coming from the java.lang
            newClassName = new StringBuilder("java.lang").append(dotClassName).toString();
            if (getJavaType(project, newClassName) != null) {
                return newClassName;
            }

            // Fall back to full blown resolution
            String[][] fullInter = type.resolveType(className);
            if (fullInter != null && fullInter.length > 0) {
                return fullInter[0][0] + "." + fullInter[0][1];
            }
        } catch (JavaModelException e) {
            // SpringCore.log(e);
        }

        return className;
    }
}
