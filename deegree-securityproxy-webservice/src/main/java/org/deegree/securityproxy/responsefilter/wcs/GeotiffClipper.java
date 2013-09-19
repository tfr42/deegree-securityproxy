//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.securityproxy.responsefilter.wcs;

import java.io.OutputStream;

import org.deegree.securityproxy.request.WcsRequest;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Concrete implementation to clip geotiffs.
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeotiffClipper implements ImageClipper {

    @Override
    public OutputStream calculateClippedImage( OutputStream imageToClip, WcsRequest wcsRequest,
                                               Geometry clippingGeometry )
                            throws IllegalArgumentException {
        checkRequiredParameters( imageToClip, wcsRequest, clippingGeometry );
        // TODO
        return imageToClip;
    }

    private void checkRequiredParameters( OutputStream imageToClip, WcsRequest wcsRequest, Geometry clippingGeometry )
                            throws IllegalArgumentException {
        if ( imageToClip == null )
            throw new IllegalArgumentException( "Image to clip must not be null!" );
        if ( wcsRequest == null )
            throw new IllegalArgumentException( "Wcs request must not be null!" );
        if ( clippingGeometry == null )
            throw new IllegalArgumentException( "Clipping geometry must not be null!" );
    }

}