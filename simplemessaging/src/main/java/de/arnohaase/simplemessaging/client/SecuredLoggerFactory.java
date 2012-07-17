package de.arnohaase.simplemessaging.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.logging.LogFactory;


class SecuredLoggerFactory {
    public static MessagingClientLogger createSecured (final MessagingClientLogger logger) {
        return (MessagingClientLogger) Proxy.newProxyInstance (SecuredLoggerFactory.class.getClassLoader (), new Class[] {MessagingClientLogger.class}, new InvocationHandler() {
            public Object invoke (Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    return method.invoke (logger, args);
                }
                catch (Throwable th) {
                    while (th instanceof InvocationTargetException)
                        th = ((InvocationTargetException) th).getTargetException ();
                    
                    LogFactory.getLog (SecuredLoggerFactory.class).error ("Logger implementation threw an exception - that is not permitted, and it was intercepted.", th);
                    return null;
                }
            }
        });
    }
}
