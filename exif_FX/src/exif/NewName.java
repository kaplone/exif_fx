package exif;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class NewName
{
    public static String newName(String filename)
    {
    	String fileNameOut = "";
    	
        try
        {
            File jpgFile = new File( filename );
            Metadata metadata = ImageMetadataReader.readMetadata( jpgFile );

            // Read Exif Data
            Directory directory = metadata.getDirectory( ExifDirectory.class );
            if( directory != null )
            {
                // Read the date
                Date date = directory.getDate( ExifDirectory.TAG_DATETIME_ORIGINAL );
                DateFormat df = DateFormat.getDateInstance();
                df.format( date );
                int year = df.getCalendar().get( Calendar.YEAR );
                int day = df.getCalendar().get( Calendar.DAY_OF_MONTH );
                int hour = df.getCalendar().get( Calendar.HOUR_OF_DAY);
                int minute = df.getCalendar().get( Calendar.MINUTE);
                int second = df.getCalendar().get( Calendar.SECOND );
                int milli = df.getCalendar().get( Calendar.MILLISECOND );
                int month = df.getCalendar().get( Calendar.MONTH ) + 1;

                fileNameOut = year + String.format("_%02d", month) + String.format("_%02d", day) + "_" + String.format("%02dh", hour) + String.format("%02dm", minute) + String.format("%02ds.jpg", second);

                }

        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
		return fileNameOut;
                
    }
}