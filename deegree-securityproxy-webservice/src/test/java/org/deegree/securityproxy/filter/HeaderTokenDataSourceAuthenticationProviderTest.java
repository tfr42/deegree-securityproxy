package org.deegree.securityproxy.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.deegree.securityproxy.authentication.HeaderAuthenticationToken;
import org.deegree.securityproxy.authentication.HeaderTokenDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href="goltz@lat-lon.de">Lyn Goltz</a>
 * @author <a href="erben@lat-lon.de">Alexander Erben</a>
 * @author last edited by: $Author: erben $
 * 
 * @version $Revision: $, $Date: $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/HeaderTokenDataSourceAuthenticationProviderTestContext.xml" })
public class HeaderTokenDataSourceAuthenticationProviderTest {

    private static final String VALID_TOKEN = "VALID";

    private static final String VALID_USERNAME = "USER";

    private static final String VALID_PASSWORD = "PASSWD";

    private static final String INVALID_TOKEN = "INVALID";

    @Autowired
    private AuthenticationProvider provider;

    @Autowired
    private HeaderTokenDataSource source;

    @SuppressWarnings("unchecked")
    @Before
    public void setupDatasource() {
        Mockito.reset( source );
        when( source.loadUserDetailsFromDataSource( INVALID_TOKEN ) ).thenReturn( null );
        User validUser = new User( VALID_USERNAME, VALID_PASSWORD, Collections.<GrantedAuthority> emptyList() );
        when( source.loadUserDetailsFromDataSource( VALID_TOKEN ) ).thenReturn( validUser );
        when( source.loadUserDetailsFromDataSource( null ) ).thenThrow( IllegalArgumentException.class );
    }

    @Test
    public void testAuthenticateValidToken() {
        Authentication authenticate = provider.authenticate( createHeaderAuthenticationTokenWithValidHeader() );
        assertThat( authenticate.isAuthenticated(), is( Boolean.TRUE ) );
        assertThat( (String) authenticate.getCredentials(), is( VALID_TOKEN ) );
        UserDetails details = (UserDetails) authenticate.getPrincipal();
        assertThat( details.getUsername(), is( VALID_USERNAME ) );
        assertThat( details.getPassword(), is( VALID_PASSWORD ) );
    }

    @Test
    public void testAuthenticateInvalidToken() {
        Authentication authenticate = provider.authenticate( createHeaderAuthenticationTokenWithInvalidHeader() );
        assertThat( authenticate.isAuthenticated(), is( false ) );
        assertThat( (String) authenticate.getCredentials(), nullValue() );
    }

    @Test
    public void testAuthenticateMissingToken() {
        Authentication authenticate = provider.authenticate( createHeaderAuthenticationTokenWithNoHeader() );
        assertThat( authenticate.isAuthenticated(), is( false ) );
        assertThat( (String) authenticate.getCredentials(), nullValue() );
    }

    @Test
    public void testAuthenticateTokenIsStoredInContext() {
        Authentication authenticationProvided = provider.authenticate( createHeaderAuthenticationTokenWithValidHeader() );
        Authentication authenticationFromContext = SecurityContextHolder.getContext().getAuthentication();
        assertThat( authenticationFromContext, is( authenticationProvided ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthenticateShouldThrowExceptionOnNullParameter() {
        provider.authenticate( null );
    }

    @Test
    public void testSupportsHeaderTokenShouldReturnTrue() {
        assertThat( provider.supports( HeaderAuthenticationToken.class ), is( true ) );
    }

    @Test
    public void testSupportsAllAuthenticationsShouldReturnFalse() {
        assertThat( provider.supports( Authentication.class ), is( false ) );
    }

    private Authentication createHeaderAuthenticationTokenWithNoHeader() {
        return new HeaderAuthenticationToken( null );
    }

    private Authentication createHeaderAuthenticationTokenWithInvalidHeader() {
        return new HeaderAuthenticationToken( INVALID_TOKEN );

    }

    private Authentication createHeaderAuthenticationTokenWithValidHeader() {
        return new HeaderAuthenticationToken( VALID_TOKEN );

    }

}
