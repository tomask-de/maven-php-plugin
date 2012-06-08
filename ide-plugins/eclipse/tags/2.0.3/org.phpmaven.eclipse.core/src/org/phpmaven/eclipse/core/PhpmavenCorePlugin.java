/*******************************************************************************
 * Copyright (c) 2011 PHP-Maven.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     PHP-Maven.org
 *******************************************************************************/

package org.phpmaven.eclipse.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.phpmaven.eclipse.core.archetype.IArchetypeRegistry;
import org.phpmaven.eclipse.core.archetype.IArchetypeRegistrySettings;
import org.phpmaven.eclipse.core.internal.archetype.ArchetypeRegistry;
import org.phpmaven.eclipse.core.internal.archetype.ArchetypeRegistrySettings;
import org.phpmaven.eclipse.core.internal.mvn.MvnLoggingAppender;
import org.phpmaven.eclipse.core.internal.mvn.PhpmavenLauncher;
import org.phpmaven.eclipse.core.internal.mvn.PhpmavenLocator;
import org.phpmaven.eclipse.core.internal.mvn.PhpmavenTestExecution;
import org.phpmaven.eclipse.core.internal.phpunit.Phpunit355;
import org.phpmaven.eclipse.core.mvn.ILauncher;
import org.phpmaven.eclipse.core.mvn.ISourceLocator;
import org.phpmaven.eclipse.core.mvn.ITestExecutionTooling;
import org.phpmaven.eclipse.core.phpunit.IPhpUnit;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;

/**
 * The Core plugin.
 * 
 * @author Martin Eisengardt
 */
public class PhpmavenCorePlugin extends AbstractUIPlugin {
    
    /** the plugin id */
    public static final String PLUGIN_ID = "org.phpmaven.eclipse.core"; //$NON-NLS-1$
    
    /** the maven buildpath container */
    public static final String BUILDPATH_CONTAINER_ID = PhpmavenCorePlugin.PLUGIN_ID + ".MAVEN_INCLUSIONS"; //$NON-NLS-1$
    
    /** ID of this project nature */
    public static final String PHPMAVEN_NATURE_ID = PhpmavenCorePlugin.PLUGIN_ID + ".phpMavenNature"; //$NON-NLS-1$
    
    /** ID of the phpmaven builder */
    public static final String PHPMAVEN_BUILDER_ID = PhpmavenCorePlugin.PLUGIN_ID + ".mavenBuilder"; //$NON-NLS-1$
    
    /** The shared instance */
    private static PhpmavenCorePlugin plugin;
    
    /**
     * The phpunit instance for version 3.5.5 that will match the most common
     * phpunit versions
     */
    private static IPhpUnit PHPUNIT_3_5_5 = new Phpunit355();
    
    /** maven locator */
    private static ISourceLocator MVN_LOCATOR = new PhpmavenLocator();
    
    /** maven test execution */
    private static ITestExecutionTooling MVN_TEST_EXECUTION_TOOLING = new PhpmavenTestExecution();
    
    /**
     * The archetype registry
     */
    private IArchetypeRegistry registry;
    
    /**
     * The archetype registry settings and cache.
     */
    private IArchetypeRegistrySettings registrySettings;
    
    /**
     * The constructor.
     */
    public PhpmavenCorePlugin() {
    }
    
    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        PhpmavenCorePlugin.plugin = this;
        final ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        final Logger logger = factory.getLogger("org.eclipse.m2e.core.internal.embedder.EclipseLogger"); //$NON-NLS-1$
        if (logger instanceof AppenderAttachable) {
            final AppenderBase<ILoggingEvent> appender = new MvnLoggingAppender();
            ((AppenderAttachable) logger).addAppender(appender);
            appender.start();
        }
    }
    
    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        PhpmavenCorePlugin.plugin = null;
        super.stop(context);
    }
    
    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static PhpmavenCorePlugin getDefault() {
        return PhpmavenCorePlugin.plugin;
    }
    
    /**
     * Returns the phpunit support for given version
     * 
     * @param version
     *            version number
     * @return phpunit tooling support
     */
    public static final IPhpUnit getPhpUnit(final String version) {
        // TODO respect the version number
        // let plugins install custom versions via extensions and properties
        return PhpmavenCorePlugin.PHPUNIT_3_5_5;
    }
    
    /**
     * Returns the phpunit support for given project
     * 
     * @param project
     *            the project
     * @return phpunit tooling support
     */
    public static final IPhpUnit getPhpUnit(final IProject project) {
        // TODO respect the version number
        // let plugins install custom versions via extensions and properties
        return PhpmavenCorePlugin.PHPUNIT_3_5_5;
    }
    
    /**
     * Returns the source locator for given project
     * 
     * @param project
     *            project
     * @return source locator
     */
    public static final ISourceLocator getSourceLocator(final IProject project) {
        // TODO configure
        return PhpmavenCorePlugin.MVN_LOCATOR;
    }
    
    /**
     * Returns a launcher helping to execute php
     * 
     * @param project
     *            depending project
     * @return launcher for php execution
     */
    public static ILauncher getLauncher(final IProject project) {
        // TODO configure
        return new PhpmavenLauncher(project);
    }

    /**
     * Returns the test execution tooling for given project
     * 
     * @param project project
     * @return test execution tooling
     */
    public static ITestExecutionTooling getTestExecutionTooling(IProject project) {
        // TODO configure
        return PhpmavenCorePlugin.MVN_TEST_EXECUTION_TOOLING;
    }
    
    /**
     * Logs the given message with severity info
     * 
     * @param message
     */
    public static void logInfo(final String message) {
        PhpmavenCorePlugin.getDefault().getLog().log(new Status(IStatus.INFO, PhpmavenCorePlugin.PLUGIN_ID, message, null));
    }
    
    /**
     * Logs the given message with severity warning
     * 
     * @param message
     */
    public static void logWarn(final String message) {
        PhpmavenCorePlugin.getDefault().getLog().log(new Status(IStatus.WARNING, PhpmavenCorePlugin.PLUGIN_ID, message, null));
    }
    
    /**
     * Logs the given message with severity warning
     * 
     * @param message
     */
    public static void logError(final String message) {
        PhpmavenCorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, message, null));
    }
    
    /**
     * Logs the given message with severity info
     * 
     * @param message
     * @param t
     */
    public static void logInfo(final String message, final Throwable t) {
        PhpmavenCorePlugin.getDefault().getLog().log(new Status(IStatus.INFO, PhpmavenCorePlugin.PLUGIN_ID, message, t));
    }
    
    /**
     * Logs the given message with severity warning
     * 
     * @param message
     * @param t
     */
    public static void logWarn(final String message, final Throwable t) {
        PhpmavenCorePlugin.getDefault().getLog().log(new Status(IStatus.WARNING, PhpmavenCorePlugin.PLUGIN_ID, message, t));
    }
    
    /**
     * Logs the given message with severity warning
     * 
     * @param message
     * @param t
     */
    public static void logError(final String message, final Throwable t) {
        PhpmavenCorePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, PhpmavenCorePlugin.PLUGIN_ID, message, t));
    }
    
    /**
     * Returns the archetype registry.
     * @return archetype registry.
     */
    public static IArchetypeRegistry getArchetypeRegistry() {
        if (PhpmavenCorePlugin.getDefault().registry == null) {
            PhpmavenCorePlugin.getDefault().registry = new ArchetypeRegistry();
        }
        return PhpmavenCorePlugin.getDefault().registry;
    }
    
    /**
     * Returns the archetype registry settings and cache.
     * @return archetype registry settings and cache.
     */
    public static IArchetypeRegistrySettings getArchetypeRegistrySettings() {
        if (PhpmavenCorePlugin.getDefault().registrySettings == null) {
            PhpmavenCorePlugin.getDefault().registrySettings = new ArchetypeRegistrySettings();
        }
        return PhpmavenCorePlugin.getDefault().registrySettings;
    }
    
}