package org.knowhowlab.osgi.testing.bnd.junit;

import org.junit.Assert;
import org.junit.rules.ExternalResource;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

public class ServiceTrackerRule<T> extends ExternalResource {

    private final BundleContext context;

    private ServiceTracker serviceTracker;
    private ServiceReference serviceReference;
    private T resource;

    private long timeout;

    private final Class<T> type;

    public ServiceTrackerRule(BundleContext context, Class<T> type) {
        this.context = context;
        this.type = type;
        this.timeout = 2000;
    }

    public ServiceTrackerRule(BundleContext context, Class<T> type, long timeout) {
        this.context = context;
        this.type = type;
        this.timeout = timeout;
    }

    @Override
    protected void before() throws InterruptedException, InvalidSyntaxException {
//        log.debug("Before: {}", type);
        serviceTracker = new ServiceTracker(context,
                FrameworkUtil.createFilter("(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")"), null);

        serviceTracker.open();
        Assert.assertNotNull(serviceTracker.waitForService(timeout));
        serviceReference = serviceTracker.getServiceReference();
        if (serviceReference != null) {
            resource = (T) context.getService(serviceReference);
        } else {
            resource = null;
        }
    }

    @Override
    protected void after() {
        context.ungetService(serviceReference);
        serviceTracker.close();
//        log.debug("After: {}", type);
    }

    public T getResource() {
        return resource;
    }
}
