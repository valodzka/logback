/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2011, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.ContextAwareBase;

public class ContextUtil extends ContextAwareBase {

  public ContextUtil(Context context) {
    setContext(context);
  }

  public static String getLocalHostName() throws SocketException, UnknownHostException {
    try {
      final InetAddress addr = InetAddress.getLocalHost();
      return addr.getHostName();
    } catch (final UnknownHostException uhe) {
      final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        final NetworkInterface nic = interfaces.nextElement();
        final Enumeration<InetAddress> addresses = nic.getInetAddresses();
        while (addresses.hasMoreElements()) {
          final InetAddress address = addresses.nextElement();
          if (!address.isLoopbackAddress()) {
            final String hostname = address.getHostName();
            if (hostname != null) {
              return hostname;
            }
          }
        }
      }
      throw uhe;
    }
  }

  /**
   * Add the local host's name as a property
   */
  public void addHostNameAsProperty() {
    try {
      String localhostName =  getLocalHostName();
      context.putProperty(CoreConstants.HOSTNAME_KEY, localhostName);
    } catch (UnknownHostException e) {
      addError("Failed to get local hostname", e);
    } catch (SocketException e) {
      addError("Failed to get local hostname", e);
    } catch (SecurityException e) {
      addError("Failed to get local hostname", e);
    }
  }

   public void addProperties(Properties props) {
    if (props == null) {
      return;
    }
    Iterator i = props.keySet().iterator();
    while (i.hasNext()) {
      String key = (String) i.next();
      context.putProperty(key, props.getProperty(key));
    }
  }


  public void addGroovyPackages(List<String> frameworkPackages) {
    //addFrameworkPackage(frameworkPackages, "groovy.lang");
    addFrameworkPackage(frameworkPackages, "org.codehaus.groovy.runtime");
  }

  public void addFrameworkPackage(List<String> frameworkPackages, String packageName) {
    if(!frameworkPackages.contains(packageName)) {
      frameworkPackages.add(packageName);
    }
  }

}
