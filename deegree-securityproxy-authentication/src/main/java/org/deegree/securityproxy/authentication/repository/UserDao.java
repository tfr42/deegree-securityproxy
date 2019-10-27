package org.deegree.securityproxy.authentication.repository;

import javax.sql.DataSource;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Loads {@link UserDetails} from a {@link DataSource}.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
public interface UserDao {

    /**
     * Verify an header value against the encapsulated data source.
     * 
     * @param headerValue
     *            the value of the header token used for authentication, may be <code>null</code> or empty
     * @return the user details that match the given header value, <code>null</code> if the headerValue is
     *         <code>null</code> or empty or if no matching user could be found
     */
    UserDetails retrieveUserById( String headerValue );

    /**
     * Verify an name against the encapsulated data source.
     * 
     * @param name
     *            the name used for authentication, may be <code>null</code> or empty
     * @return the user details that match the given name, <code>null</code> if the name is <code>null</code> or empty
     *         or if no matching name could be found
     */
    UserDetails retrieveUserByName( String name );

}