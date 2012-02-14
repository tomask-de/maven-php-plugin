package org.phpmaven.eclipse.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class PhpmavenUiPlugin extends AbstractUIPlugin {
    
    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.phpmaven.eclipse.ui"; //$NON-NLS-1$
    
    /** The shared instance */
    private static PhpmavenUiPlugin plugin;
    
    /**
     * The constructor
     */
    public PhpmavenUiPlugin() {
    }
    
    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        PhpmavenUiPlugin.plugin = this;
    }
    
    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        PhpmavenUiPlugin.plugin = null;
        super.stop(context);
    }
    
    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static PhpmavenUiPlugin getDefault() {
        return PhpmavenUiPlugin.plugin;
    }
    
    /**
     * Logs the given message with severity info
     * 
     * @param message
     */
    public static void logInfo(final String message) {
        PhpmavenUiPlugin.getDefault().getLog().log(new Status(IStatus.INFO, PhpmavenUiPlugin.PLUGIN_ID, message, null));
    }
    
    /**
     * Logs the given message with severity warning
     * 
     * @param message
     */
    public static void logWarn(final String message) {
        PhpmavenUiPlugin.getDefault().getLog().log(new Status(IStatus.WARNING, PhpmavenUiPlugin.PLUGIN_ID, message, null));
    }
    
    /**
     * Logs the given message with severity warning
     * 
     * @param message
     */
    public static void logError(final String message) {
        PhpmavenUiPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, PhpmavenUiPlugin.PLUGIN_ID, message, null));
    }
    
    /**
     * Logs the given message with severity info
     * 
     * @param message
     * @param t
     */
    public static void logInfo(final String message, final Throwable t) {
        PhpmavenUiPlugin.getDefault().getLog().log(new Status(IStatus.INFO, PhpmavenUiPlugin.PLUGIN_ID, message, t));
    }
    
    /**
     * Logs the given message with severity warning
     * 
     * @param message
     * @param t
     */
    public static void logWarn(final String message, final Throwable t) {
        PhpmavenUiPlugin.getDefault().getLog().log(new Status(IStatus.WARNING, PhpmavenUiPlugin.PLUGIN_ID, message, t));
    }
    
    /**
     * Logs the given message with severity warning
     * 
     * @param message
     * @param t
     */
    public static void logError(final String message, final Throwable t) {
        PhpmavenUiPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, PhpmavenUiPlugin.PLUGIN_ID, message, t));
    }
    
    /**
     * the descriptor cache
     */
    private static Map<String, ImageDescriptor> descriptorCache = new HashMap<String, ImageDescriptor>();
    
    /**
     * the image cache
     */
    private static Map<String, Image> imageCache = new HashMap<String, Image>();
    
    /**
     * Returns the image descriptor for given image name
     * 
     * @param name
     *            name of the image
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(final String name) {
        ImageDescriptor descriptor = PhpmavenUiPlugin.descriptorCache.get(name);
        if (descriptor == null) {
            try {
                final String path = "icons/" + name + ".png"; //$NON-NLS-1$ //$NON-NLS-2$
                final URL installURL = PhpmavenUiPlugin.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
                final URL url = new URL(installURL, path);
                descriptor = ImageDescriptor.createFromURL(url);
                PhpmavenUiPlugin.descriptorCache.put(name, descriptor);
            } catch (final MalformedURLException ex) {
                // TODO better error handling
                throw new IllegalArgumentException(ex);
            }
        }
        return descriptor;
    }
    
    /**
     * Returns the image for given image name
     * 
     * @param name
     *            name of the image
     * @return the image object
     */
    public static Image getImage(final String name) {
        Image image = PhpmavenUiPlugin.imageCache.get(name);
        if (image == null) {
            image = PhpmavenUiPlugin.getImageDescriptor(name).createImage();
            PhpmavenUiPlugin.imageCache.put(name, image);
        }
        return image;
    }
    
}
