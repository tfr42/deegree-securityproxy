package org.deegree.securityproxy.filter;

import org.deegree.securityproxy.authorization.logging.AuthorizationReport;
import org.deegree.securityproxy.request.OwsRequest;
import org.deegree.securityproxy.request.UnsupportedRequestTypeException;
import org.deegree.securityproxy.request.parser.RequestParsingException;
import org.deegree.securityproxy.responsefilter.ResponseFilterException;
import org.deegree.securityproxy.responsefilter.logging.ResponseFilterReport;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * A {@link ServiceManager} encapsulates one service type. A {@link ServiceManager} contains all information to filter a
 * specific service type.
 * 
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="stenger@lat-lon.de">Dirk Stenger</a>
 * @author last edited by: $Author: stenger $
 * @version $Revision: $, $Date: $
 */
public interface ServiceManager {

    /**
     * Parse a given request.
     * 
     * @param httpRequest
     *            never <code>null</code>.
     * @return
     * @throws UnsupportedRequestTypeException
     *             when the given request does not have the correct service type.
     * @throws RequestParsingException
     */
    OwsRequest parse( HttpServletRequest httpRequest )
                    throws UnsupportedRequestTypeException, RequestParsingException;

    /**
     * Authorize a given request.
     * 
     * @param authentication
     *            the user authentication to authorize, may be <code>null</code>.
     * @param owsRequest
     *            the secured request, never <code>null</code>.
     * @return
     */
    AuthorizationReport authorize( Authentication authentication, OwsRequest owsRequest );

    /**
     * Check if a response filter is enabled for a given request.
     * 
     * @param owsRequest
     *            never <code>null</code>.
     * @return
     */
    boolean isResponseFilterEnabled( OwsRequest owsRequest );

    /**
     * Filter a response.
     * 
     * @param wrappedResponse
     *            the response to filter, the response may be adjusted during filtering, never <code>null</code>.
     * @param authentication
     *            may be <code>null</code>.
     * @param owsRequest
     *            parsed request, never <code>null</code>.
     * @return never <code>null</code>, returns empty report if no response filter manager is found.
     * @throws ResponseFilterException
     *             if an error occurred during writing in the real output stream.
     */
    ResponseFilterReport filterResponse( StatusCodeResponseBodyWrapper wrappedResponse, Authentication authentication,
                                         OwsRequest owsRequest )
                    throws ResponseFilterException;

    /**
     * Check if a given request is supported by the {@link ServiceManager}.
     *
     * @param serviceType
     *            may be <code>null</code>. If <code>null</code>, request parameter is used to determine if service type
     *            is supported.
     * @param request
     *            never <code>null</code>. If serviceType parameter is <code>null</code>, this parameter is used to
     *            determine if service type is supported.
     * @return <code>true</code> if this {@link ServiceManager} can handle the requested {@link HttpServletRequest},
     *         <code>false</code> otherwise.
     */
    boolean isServiceTypeSupported( String serviceType, HttpServletRequest request );

}