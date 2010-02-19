package org.pentaho.di.repository;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.exception.KettleException;

/**
 * This is the Admin API for Action based security. Bind logical role(s) to runtime role
 * 
 * @author rmansoor
 */

public interface IAbsSecurityManager extends IAbsSecurityProvider{


  /**
  * Initialize the service and get the role binding struct
  * 
  * @param locale 
  * @throws KettleException
  */
  public void initialize(String locale)  throws KettleException;
  /**
  * Sets the bindings for the given runtime role. All other bindings for this runtime role are removed.
  * 
  * @param runtimeRoleName runtime role name
  * @param logicalRoleNames list of logical role names
  * @throws KettleException
  */
  public void setLogicalRoles(String rolename , List<String> logicalRoles)  throws KettleException;
  /**
  * Get all the logical role names for the given runtime role. 
  * 
  * @param runtimeRole 
  * @return list of logical roles
  * @throws KettleException
  */
  public List<String> getLogicalRoles(String runtimeRole)  throws KettleException;

  /**
  * Get the localized logical role names for the given locale. 
  * 
  * @param locale 
  * @return map of localized logical roles
  * @throws KettleException
  */
  public Map<String, String> getAllLogicalRoles(String locale)  throws KettleException;
}