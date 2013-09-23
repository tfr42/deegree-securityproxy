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

import static javax.imageio.ImageIO.read;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.geotools.data.DataSourceException;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeotiffClipperTest {

    private GeotiffClipper geotiffClipper = new GeotiffClipper();

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateClippedImageWithNullImageStreamShouldFail()
                            throws Exception {
        geotiffClipper.calculateClippedImage( null, mockClippingGeometry(), mockOutputStream() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateClippedImageWithNullClippingGeometryShouldFail()
                            throws Exception {
        geotiffClipper.calculateClippedImage( mockInputStream(), null, mockOutputStream() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateClippedImageWithNullOutputStreamShouldFail()
                            throws Exception {
        geotiffClipper.calculateClippedImage( mockInputStream(), mockClippingGeometry(), null );
    }

    @Test
    public void testCalculateClippedImageInsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        OutputStream outputStream = createOutputStreamFrom( destinationFile );
        InputStream inputStream = createInputStreamFrom( sourceFile );

        geotiffClipper.calculateClippedImage( inputStream, createWholeWorldVisibleGeometry(), outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        // TODO: pixels should be the same!
    }

    @Test
    public void testCalculateClippedImageInsideVisibleAreaAndOutsideVisibleArea()
                            throws Exception {
        File originalFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        File newFile = createNewTempFile();

        geotiffClipper.calculateClippedImage( createInputStreamFrom( originalFile ),
                                              createGeometryWithImageInsideAndOutside(),
                                              createOutputStreamFrom( newFile ) );

        // Should have the same dimension! But with 'no data' areas!
        int heightOriginalImage = ImageIO.read( originalFile ).getHeight();
        int widthOriginalImage = ImageIO.read( originalFile ).getWidth();
        int heightNewImage = ImageIO.read( newFile ).getHeight();
        int widthNewImage = ImageIO.read( newFile ).getWidth();

        assertThat( heightNewImage, not( heightOriginalImage ) );
        assertThat( widthNewImage, not( widthOriginalImage ) );
    }

    @Test
    public void testCalculateClippedImageOutsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        File destinationFile = createNewTempFile();

        InputStream inputStream = createInputStreamFrom( sourceFile );
        OutputStream outputStream = createOutputStreamFrom( destinationFile );

        geotiffClipper.calculateClippedImage( inputStream, createGeometryWithImageOutside(), outputStream );

        inputStream.close();
        outputStream.close();

        assertThat( destinationFile, hasSameDimension( sourceFile ) );
        // Should have the same dimension! But all pixels are 'no data'!
    }

    @Test
    public void testIsClippingRequiredWhenImageIsInsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );
        boolean isClippingRequired = geotiffClipper.isClippingRequired( geoTiffReader,
                                                                        createWholeImageVisibleGeometryInImageCrs() );

        assertThat( isClippingRequired, is( true ) );
    }

    @Test
    public void testIsClippingRequiredWhenImageIsOutsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );
        boolean isClippingRequired = geotiffClipper.isClippingRequired( geoTiffReader,
                                                                        createWholeImageInvisibleGeometryInImageCrs() );

        assertThat( isClippingRequired, is( true ) );
    }

    @Test
    public void testIsClippingRequiredWhenImageIsInAndOutsideVisibleArea()
                            throws Exception {
        File sourceFile = createNewFile( "dem30_geotiff_tiled.tiff" );
        GeoTiffReader geoTiffReader = createGeoTiffReader( sourceFile );
        boolean isClippingRequired = geotiffClipper.isClippingRequired( geoTiffReader,
                                                                        createImageVisibleAndInvisibleGeometryInImageCrs() );

        assertThat( isClippingRequired, is( true ) );
    }

    private GeoTiffReader createGeoTiffReader( File tiff )
                            throws DataSourceException {
        return new GeoTiffReader( tiff );
    }

    private File createNewFile( String resourceName ) {
        return new File( GeotiffClipperTest.class.getResource( resourceName ).getPath() );
    }

    private File createNewTempFile()
                            throws IOException {
        return File.createTempFile( GeotiffClipperTest.class.getSimpleName(), "tif" );
    }

    private InputStream createInputStreamFrom( File file )
                            throws Exception {
        return new FileInputStream( file );
    }

    private OutputStream createOutputStreamFrom( File file )
                            throws Exception {
        return new FileOutputStream( file );
    }

    private InputStream mockInputStream() {
        return mock( InputStream.class );
    }

    private OutputStream mockOutputStream() {
        return mock( OutputStream.class );
    }

    private Geometry mockClippingGeometry() {
        return mock( Geometry.class );
    }

    private Geometry createWholeImageVisibleGeometryInImageCrs() {
        Envelope wholeWorld = new Envelope( 446580.945, 457351.945, 4427805.000, 4441915.000 );
        return new GeometryFactory().toGeometry( wholeWorld );
    }

    private Geometry createWholeImageInvisibleGeometryInImageCrs() {
        Envelope wholeWorld = new Envelope( 446580.945, 446581.945, 4427805.000, 4427806.000 );
        return new GeometryFactory().toGeometry( wholeWorld );
    }

    private Geometry createImageVisibleAndInvisibleGeometryInImageCrs() {
        Envelope wholeWorld = new Envelope( 446580.945, 457351.945, 4437805.000, 4441915.000 );
        return new GeometryFactory().toGeometry( wholeWorld );
    }

    private Geometry createWholeWorldVisibleGeometry() {
        Envelope wholeWorld = new Envelope( -180, 180, -90, 90 );
        return new GeometryFactory().toGeometry( wholeWorld );
    }

    private Geometry createGeometryWithImageInsideAndOutside() {
        Envelope smallEnvelope = new Envelope( 40, 40.1, -111.57, -111.53 );
        return new GeometryFactory().toGeometry( smallEnvelope );
    }

    private Geometry createGeometryWithImageOutside() {
        Envelope smallEnvelope = new Envelope( 5, 5.1, 48.57, 48.93 );
        return new GeometryFactory().toGeometry( smallEnvelope );
    }

    private Matcher<File> hasSameDimension( File sourceFile )
                            throws IOException {

        BufferedImage sourceImage = read( sourceFile );
        final int heightSource = sourceImage.getHeight();
        final int widthSource = sourceImage.getWidth();

        return new BaseMatcher<File>() {

            @Override
            public boolean matches( Object item ) {
                BufferedImage destinationImage;
                try {
                    destinationImage = read( (File) item );
                    int heightDestination = destinationImage.getHeight();
                    int widthDestination = destinationImage.getWidth();

                    return heightDestination == heightSource && widthDestination == widthSource;
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void describeTo( Description description ) {
                description.appendText( "Should have the same width and heigth as the source ( " + widthSource + " * "
                                        + heightSource + ")!" );
            }
        };
    }

    // private int[] getPixels( File file )
    // throws InterruptedException, IOException {
    // BufferedImage image = read( file );
    //
    // int imageWidth = image.getWidth() ;
    // int imageHeight = image.getHeight();
    //
    // int[] pix = new int[imageWidth * imageHeight];
    // PixelGrabber pixelGrabber = new PixelGrabber( image, 0, 0, imageHeight, imageHeight, pix, 0, imageWidth);
    // int[] pixels = null;
    // if ( pixelGrabber.grabPixels() ) {
    // int width = pixelGrabber.getWidth();
    // int height = pixelGrabber.getHeight();
    // pixels = new int[width * height];
    // pixels = (int[]) pixelGrabber.getPixels();
    // }
    // return pixels;
    // }

}