package es.upm.oeg.librairy.nlp.utils;

import java.io.*;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class ReaderUtils {

    public static BufferedReader from(String path) throws IOException {

        InputStream is = path.startsWith("http")? new URL(path).openStream() : new FileInputStream(path);

        InputStream dis = path.endsWith("gz") ? new GZIPInputStream(is) : is;

        InputStreamReader inputStreamReader = new InputStreamReader(dis);

        return new BufferedReader(inputStreamReader);
    }


}
