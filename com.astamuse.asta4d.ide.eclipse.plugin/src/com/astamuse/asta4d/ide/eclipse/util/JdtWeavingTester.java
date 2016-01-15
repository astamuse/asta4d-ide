/*******************************************************************************
 * Copyright (c) 2009 Spring IDE Developers
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Spring IDE Developers - initial API and implementation
 *******************************************************************************/
package com.astamuse.asta4d.ide.eclipse.util;

import org.eclipse.contribution.jdt.IsWovenTester;

/**
 * (Copied from Spring IDE and cut redundant logics off)
 * 
 * Utility class to encapsulate the access to the Jdt weaving plugin.
 * 
 * @author e-ryu
 *
 */
class JdtWeavingTester {

    static boolean isJdtWeavingActive() {
        return IsWovenTester.isWeavingActive();
    }

}