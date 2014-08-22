/*!
* Copyright 2010 - 2013 Pentaho Corporation.  All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package com.pentaho.di.trans.dataservice;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.sql.ServiceCacheMethod;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.metastore.persist.MetaStoreAttribute;
import org.pentaho.metastore.persist.MetaStoreElementType;
import org.pentaho.metastore.util.PentahoDefaults;

/**
 * This describes a (transformation) data service to the outside world.
 * It defines the name, picks the step to read from (or to write to), the caching method etc.
 *
 * @author matt
 */
@MetaStoreElementType(
  name = PentahoDefaults.KETTLE_DATA_SERVICE_ELEMENT_TYPE_NAME,
  description = PentahoDefaults.KETTLE_DATA_SERVICE_ELEMENT_TYPE_DESCRIPTION )
public class DataServiceMeta {

  public static String XML_TAG = "data-service";

  public static final String DATA_SERVICE_NAME = "name";
  public static final String DATA_SERVICE_STEPNAME = "stepname";
  public static final String DATA_SERVICE_TRANSFORMATION_FILENAME = "transformation_filename";
  public static final String DATA_SERVICE_TRANSFORMATION_REP_PATH = "transformation_rep_path";
  public static final String DATA_SERVICE_TRANSFORMATION_REP_OBJECT_ID = "transformation_rep_object_id";
  public static final String DATA_SERVICE_CACHE_METHOD = "cache_method";
  public static final String DATA_SERVICE_CACHE_MAX_AGE_MINUTES = "cache_max_age_minutes";

  protected String name;

  @MetaStoreAttribute
  protected String stepname;

  @MetaStoreAttribute( key = DATA_SERVICE_TRANSFORMATION_REP_OBJECT_ID )
  protected String transObjectId; // rep: by reference (1st priority)

  @MetaStoreAttribute( key = DATA_SERVICE_TRANSFORMATION_REP_PATH )
  protected String transRepositoryPath; // rep: by name (2nd priority)

  @MetaStoreAttribute( key = DATA_SERVICE_TRANSFORMATION_FILENAME )
  protected String transFilename; // file (3rd priority)

  protected String id;

  @MetaStoreAttribute( key = DATA_SERVICE_CACHE_METHOD )
  protected ServiceCacheMethod cacheMethod;

  @MetaStoreAttribute( key = DATA_SERVICE_CACHE_MAX_AGE_MINUTES )
  protected int cacheMaxAgeMinutes;

  public DataServiceMeta() {
    this( null, null, true, false, ServiceCacheMethod.None );
  }

  /**
   * @param name
   * @param stepname
   * @param output
   * @param optimisationAllowed
   */
  public DataServiceMeta( String name, String stepname, boolean output, boolean optimisationAllowed,
                          ServiceCacheMethod cacheMethod ) {
    this.name = name;
    this.stepname = stepname;
    this.cacheMethod = cacheMethod;
  }

  public boolean isDefined() {
    return !Const.isEmpty( name ) && !Const.isEmpty( stepname );
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * @return the stepname
   */
  public String getStepname() {
    return stepname;
  }

  /**
   * @param stepname the stepname to set
   */
  public void setStepname( String stepname ) {
    this.stepname = stepname;
  }

  /**
   * @return the (metastore) id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId( String id ) {
    this.id = id;
  }

  /**
   * @return the cacheMethod
   */
  public ServiceCacheMethod getCacheMethod() {
    return cacheMethod;
  }

  /**
   * @param cacheMethod the cacheMethod to set
   */
  public void setCacheMethod( ServiceCacheMethod cacheMethod ) {
    this.cacheMethod = cacheMethod;
  }

  public String getTransObjectId() {
    return transObjectId;
  }

  public void setTransObjectId( String transObjectId ) {
    this.transObjectId = transObjectId;
  }

  public String getTransRepositoryPath() {
    return transRepositoryPath;
  }

  public void setTransRepositoryPath( String transRepositoryPath ) {
    this.transRepositoryPath = transRepositoryPath;
  }

  public String getTransFilename() {
    return transFilename;
  }

  public void setTransFilename( String transFilename ) {
    this.transFilename = transFilename;
  }

  public int getCacheMaxAgeMinutes() {
    return cacheMaxAgeMinutes;
  }

  public void setCacheMaxAgeMinutes( int cacheMaxAgeMinutes ) {
    this.cacheMaxAgeMinutes = cacheMaxAgeMinutes;
  }

  /**
   * Try to look up the transObjectId for transformation which are referenced by path
   * and set transRepositoryPath if found.
   * Caller is responsible for determining whether an oid was actually set.
   * @param repository The repository to use.
   * @throws KettleException
   */
  public void lookupTransObjectId( Repository repository ) throws KettleException {
    if ( repository == null ) {
      return;
    }

    if ( Const.isEmpty( transFilename ) && transObjectId == null && !Const.isEmpty( transRepositoryPath ) ) {
      // see if there is a path specified to a repository name
      //
      String path = "/";
      String name = transRepositoryPath;
      int lastSlashIndex = name.lastIndexOf( '/' );
      if ( lastSlashIndex >= 0 ) {
        path = transRepositoryPath.substring( 0, lastSlashIndex + 1 );
        name = transRepositoryPath.substring( lastSlashIndex + 1 );
      }
      RepositoryDirectoryInterface tree = repository.loadRepositoryDirectoryTree();
      RepositoryDirectoryInterface rd = tree.findDirectory( path );
      if ( rd == null ) {
        rd = tree; // root
      }
      ObjectId oid = repository.getTransformationID( name, rd );
      if ( oid != null ) {
        transObjectId = oid.getId();
      }
    }
  }


}
