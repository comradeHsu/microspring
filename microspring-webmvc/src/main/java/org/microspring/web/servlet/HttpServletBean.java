package org.microspring.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.microspring.beans.*;
import org.microspring.context.EnvironmentAware;
import org.microspring.core.env.EnvironmentCapable;
import org.microspring.core.io.Resource;
import org.microspring.core.io.ResourceEditor;
import org.microspring.core.io.ResourceLoader;
import org.microspring.util.CollectionUtils;
import org.microspring.util.StringUtils;



import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public abstract class HttpServletBean extends HttpServlet implements EnvironmentCapable, EnvironmentAware {

    /** Logger available to subclasses. */
    protected final Log logger = LogFactory.getLog(getClass());

    private final Set<String> requiredProperties = new HashSet<>(4);



    @Override
    public final void init() throws ServletException {

        // Set bean properties from init parameters.
        PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
//        if (!pvs.isEmpty()) {
//            try {
//                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
//                ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
//                bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, getEnvironment()));
//                initBeanWrapper(bw);
//                bw.setPropertyValues(pvs, true);
//            }
//            catch (BeansException ex) {
//                if (logger.isErrorEnabled()) {
//                    logger.error("Failed to set bean properties on servlet '" + getServletName() + "'", ex);
//                }
//                throw ex;
//            }
//        }

        // Let subclasses do whatever initialization they like.
        initServletBean();
    }

    /**
     * Subclasses may override this to perform custom initialization.
     * All bean properties of this servlet will have been set before this
     * method is invoked.
     * <p>This default implementation is empty.
     * @throws ServletException if subclass initialization fails
     */
    protected void initServletBean() throws ServletException {
    }

    /**
     * PropertyValues implementation created from ServletConfig init parameters.
     */
    private static class ServletConfigPropertyValues extends MutablePropertyValues {

        /**
         * Create new ServletConfigPropertyValues.
         * @param config the ServletConfig we'll use to take PropertyValues from
         * @param requiredProperties set of property names we need, where
         * we can't accept default values
         * @throws ServletException if any required properties are missing
         */
        public ServletConfigPropertyValues(ServletConfig config, Set<String> requiredProperties)
                throws ServletException {

            Set<String> missingProps = (!CollectionUtils.isEmpty(requiredProperties) ?
                    new HashSet<>(requiredProperties) : null);

            Enumeration<String> paramNames = config.getInitParameterNames();
            while (paramNames.hasMoreElements()) {
                String property = paramNames.nextElement();
                Object value = config.getInitParameter(property);
                addPropertyValue(new PropertyValue(property, value));
                if (missingProps != null) {
                    missingProps.remove(property);
                }
            }

            // Fail if we are still missing properties.
            if (!CollectionUtils.isEmpty(missingProps)) {
                throw new ServletException(
                        "Initialization from ServletConfig for servlet '" + config.getServletName() +
                                "' failed; the following required properties were missing: " +
                                StringUtils.collectionToDelimitedString(missingProps, ", "));
            }
        }
    }
}
