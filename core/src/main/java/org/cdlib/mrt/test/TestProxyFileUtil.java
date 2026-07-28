/******************************************************************************
Copyright (c) 2005-2012, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 *
- Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
- Neither the name of the University of California nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************/
package org.cdlib.mrt.test;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.http.HttpResponse;
import java.util.HashMap;
import static org.cdlib.mrt.test.TestProxyHttp.DEFAULT_TIMEOUT;
import static org.cdlib.mrt.test.TestProxyHttp.main_test_proxy;
import org.cdlib.mrt.utility.Checksums;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.HTTPGetUtil;
import org.cdlib.mrt.utility.HttpGetNew;

public class TestProxyFileUtil {
    protected static final int DEFAULT_TIMEOUT = 3600000;
    public static void main(String[] args) {
        
        //main_test_1(args);
        //main_test_length(args);
        //main_test_file(args);
        //main_test_urlS("http://example.com");
        //main_test_urlS("http://example.com", "/home/loy/tmp/http/example.txt");
        //main_test_noProxy("ark:/13030/m5tn9hg8", 1, "system/mrt-ingest.txt", "/home/loy/tmp/http/npExample.txt");
       // main_test_noProxy("ark:/99999/fk46d7qj8h",1, "system/mrt-ingest.txt", "/home/loy/tmp/http/npExample.txt");
        //main_test_noProxy(9502, "ark:/99999/fk46d7qj8h",1, "system/mrt-ingest.txt", "/home/loy/tmp/http/npExample.txt");
        //main_test_noProxy("ark:/99999/fk46d7qj8h",1, "system/mrt-ingest.txt", "/home/loy/tmp/http/npExample.txt");
        try {
            
            if (true) url2FileP("http://example.com", "/home/loy/tmp/http/example.txt");
            
            if (false) url2FileNoP( // 6k
                9501,
                "ark:/13030/m50g3hfc", 1, "producer/20111019/lib/errors.txt",
                "/home/loy/tmp/http/npExample.txt",
                0,
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
            );
            
            if (false) url2FileNoP( // 6k
                2001,
                "ark:/13030/c8028pn1", 1, "producer/c8028pn1.mets.xml",
                "/home/loy/tmp/http/npExample.txt",
                6030,
                "6985f62f1aa5724609d1c88ac457ce77257d717efae698f4c70bfd1c4797b91e"
            );
           
            //=====
            
            if (false) url2FileNoP( // 60k
                9501,
                "ark:/13030/c8930r9g", 1, "producer/FILEID-1.189.43.jpg",
                "/home/loy/tmp/http/npExample.txt",
                60037,
                "ece925059442c7cd0952b3c6caa0b5c5300db1533d4bf00d6bcd8f1209fbc083"
            );
            if (false) return;
            
            //=====
            
            
            
               
            if (false) url2FileNoP( // 608k
                9501,
                "ark:/13030/m53b60zk", 1, "producer/Twyford_ucsb_0035D_11046.pdf",
                "/home/loy/tmp/http/npExample.txt",
                608746,
                "2d43c5c30e877903c88bccaed9f7559db95a0fc3cd25210ae037c4d9f1998255"
            );
            
            //=====
            
            if (false) url2FileNoP(
                2001,
                "ark:/13030/m50k26vp", 1, "producer/781292960.pdf",
                "/home/loy/tmp/http/keepExample.txt",
                6043096,
                "1775471a380d5b17c1dd2d5fe32af95eb8c2aeb711ffe6ae19f0739dcddc3c79"
            );
            
            if (false) url2FileNoP(
                9501,
                "ark:/13030/m50k26vp", 1, "producer/781292960.pdf",
                "/home/loy/tmp/http/npExample.txt",
                6043096,
                "1775471a380d5b17c1dd2d5fe32af95eb8c2aeb711ffe6ae19f0739dcddc3c79"
            );
            
            //=========
                 

            
            if (false) url2FileNoP(
                9501,
                "ark:/13030/c8416v4h", 1, "producer/cstr_140_side001.tif",
                "/home/loy/tmp/http/npExample.txt",
                60562250,
                "cffdcef022f46cba09534341e842353305fe0109760f0c68d3fb4583555b2cc8"
            );
            
            //==========
            
            
            if (false) url2FileNoP(
                9501,
                "ark:/13030/m5sr4jc5", 1, "producer/b118058782_C113909826_028.tif",
                "/home/loy/tmp/http/npExample.txt",
                200047122,
                "ebd3226fbf35a0b087850e8efb2a8899d7b0cccd135596f9876a79c67884f6bc"
            );
            
            //=========
            
            
            if (false) url2FileNoP( // 602M
                9501,
                "ark:/13030/m50g3hdx", 1, "producer/LCD11008_02.aif",
                "/home/loy/tmp/http/npExample.txt",
                602722980,
                "85ad096033e03cfeef7ab22f3333601c55fe91a31ab9c242924542d96c21e58b"
            );
            
            //==========
            
            
            if (false) url2FileNoP(
                2001,
                "ark:/13030/m5dz0c7f",  5, "producer/AS136-014-M.mp4",
                "/home/loy/tmp/http/npExample.txt",
                6053716977L,
                "8728c4b7f0b0452591a2a4e8fd6af0fb80172f1ed2905bc7b14311d61f60260a"
            );
            
            if (false) url2FileNoP(
                2001,
                "ark:/13030/m5kw7jv3",	1, "producer/Deliverables/cum/preservation_files/cum_00004_prsv.mov",
                "/home/loy/tmp/http/npBigBad.txt",
                40604807408L,
                "53ecab8a6f5d78847d0cbb04ab0303aa766babbc4deb27b7f3da6b9cad1c74cb"
            );
            
    // 40604807408	53ecab8a6f5d78847d0cbb04ab0303aa766babbc4deb27b7f3da6b9cad1c74cb	ark:/13030/m5kw7jv3	1	producer/Deliverables/cum/preservation_files/cum_00004_prsv.mov        
        
        
            
            if (false) url2FileNoP(
                2001,
                "ark:/13030/m5g46t43",	1,  "producer/Deliverables/cum/preservation_files/cum_00005_prsv.mov",
                "/home/loy/tmp/http/npBigBigBad.txt",
                63729458206L,
                "09525977fc4672c4397956d94b78c84a549404c737b2619c4ed200b1daf50a3c"
            );
   //   63729458206	09525977fc4672c4397956d94b78c84a549404c737b2619c4ed200b1daf50a3c	ark:/13030/m5g46t43	1	producer/Deliverables/cum/preservation_files/cum_00005_prsv.mov

        } catch (Exception e) {
            System.out.println("Exception:" + e);
            e.printStackTrace();
        }
    }
    
    public static void url2FileP(String urlS, String fileS) 
    {
        try {
            // 1. Define your local proxy details (e.g., localhost on port 8080)\
            URI urlI = new URI(urlS);
            URL url = urlI.toURL();
            String proxyHost = "uc3-mrtingest-stg02.cdlib.org";
            int proxyPort = 65002;
            File outFile = new File("/home/loy/tmp/http/example.txt");
            deleteFile(outFile);
            
            HashMap<String, String> headers = new HashMap<>();
            HTTPGetUtil getUtil = HTTPGetUtil.build(proxyHost, proxyPort, headers);
            long startTime = System.currentTimeMillis();
            FileUtil.url2File(urlS, outFile, getUtil);
            
            long durTime = System.currentTimeMillis() - startTime;
            System.out.println("TIME - NOTLEN:" + durTime);
            String out = FileUtil.file2String(outFile);
            System.out.println("File:" + outFile.getCanonicalPath() + " - >>>\n"
                    + out + "<<<\n"
            );
            
        } catch (Exception e) {
            System.out.println("Exception:" + e);
            e.printStackTrace();
        }
    }
            
    public static void url2FileNoP(int node, String ark, int versionNum, String pathname, String fileS, long len, String digest) {
        try {
            System.out.println("\n++++++++++++++++++++++++++++++ node:" + node + " - len=" + len + " +++++++++++++++++++++++++++++\n");
            // 1. Define your local proxy details (e.g., localhost on port 8080)\
            String arkEnc = URLEncoder.encode(ark, StandardCharsets.UTF_8);
            String pathnameE  = URLEncoder.encode(pathname, StandardCharsets.UTF_8);
            
            String query = arkEnc + "/" + versionNum + "/" + pathnameE;
            
            String urlS = "http://localhost:35121/storage/content/" + node + "/" + query; 
            URI urlI = new URI(urlS);
            URL url = urlI.toURL();
            System.out.println("url:" + url.toString());
            String proxyHost = null;
            int proxyPort = 65002;
            File outFile = new File(fileS);
            if (outFile.exists()) {
                outFile.delete();
                System.out.println("delete:" + outFile.getAbsolutePath());
            }
            System.out.println("file:" + outFile.getCanonicalPath());
            if (false) return;
            
            HashMap<String, String> headers = new HashMap<>();
            HTTPGetUtil getUtil = HTTPGetUtil.build(DEFAULT_TIMEOUT, proxyHost, proxyPort, headers);
            //FileUtil.url2File(urlS, outFile, len, getUtil);
            
            long startTime = System.currentTimeMillis();
            FileUtil.url2File(urlS, outFile, getUtil);
            long durTime = System.currentTimeMillis() - startTime;
            System.out.println("TIME - NOTLEN:" + durTime);
            validate("NOTLEN", outFile, len, digest);
            deleteFile(outFile);
            System.out.println("\n************************\n");
            startTime = System.currentTimeMillis();
            FileUtil.url2File(urlS, outFile, len, getUtil);
            durTime = System.currentTimeMillis() - startTime;
            System.out.println("TIME LEN:" + durTime);
            validate("LEN", outFile, len, digest);
            deleteFile(outFile);
            
            if (false) {
                String out = FileUtil.file2String(outFile);
                System.out.println("File:" + outFile.getCanonicalPath() + "\n>>>\n"
                        + out + "\n<<<\n"
                );
            }
            //HttpGetNew.getFile(url, outFile, getUtil);
            //System.out.println("Filelen:" + outFile.length());
            
            Long getLen = getUtil.getContentLength(url.toString());
            if (getLen == null) {
                System.out.println("content-length header not available");
            } else {
                System.out.println("content-length = " + getLen);
            }
            
        } catch (Exception e) {
            System.out.println("Exception:" + e);
            e.printStackTrace();
        }
    }
    
    protected static void deleteFile(File delFile)
    {
        if (delFile.exists()) {
                delFile.delete();
                System.out.println("delete:" + delFile.getAbsolutePath());
        } else {
            System.out.println("File does not exist:" + delFile.getAbsolutePath());
        }
        
    }
    
    protected static boolean validate(String header, File testFile, long testLen, String testSha256)
    {
        boolean match = true;
        System.out.println("*** validate: " + header);
        try {
            if ((testFile == null) || !testFile.exists()) {
                System.out.println("File does not exist");
                return false;
            }
            if (testFile.length() != testLen) {
                System.out.println("MISMATCH Length - file:" + testFile.length() + "passed length:" + testLen);
                return false;
            }
            
            String [] types = {"sha256"};
            
            Checksums checksums = Checksums.getChecksums(types, testFile);
            String fileChecksum = checksums.getChecksum("sha256");
            if ((fileChecksum == null) || fileChecksum.isEmpty()) {
                System.out.println("Checksum does not exist");
                return false;
            }
            
            if (!fileChecksum.equals(testSha256)) {
                System.out.println("sha256Fails:\n"
                        + "  File sha256:" + fileChecksum + "\n"
                        + "  Test sha256:" + testSha256 + "\n"
                );
                return false;
            }
                 
                
            System.out.println("VALIDATE MATCH: len:" + testFile.length()
                    + " - sha256:" + fileChecksum
                    );
            return true;
            
        } catch (Exception ex) {
            System.out.println("validate Exception:" + ex);
            return false;
        }
        
    }
    
    public static void main_test_proxy(String ark, int versionNum, String pathname, String fileS) {
        try {
            // 1. Define your local proxy details (e.g., localhost on port 8080)\
            String arkEnc = URLEncoder.encode(ark, StandardCharsets.UTF_8);
            String pathnameE  = URLEncoder.encode(pathname, StandardCharsets.UTF_8);
            
            String query = arkEnc + "/" + versionNum + "/" + pathnameE;
            
            String urlS = "http://uc3-docker-dev03.cdlib.org:35121/storage/content/9502/" + query; 
            URI urlI = new URI(urlS);
            URL url = urlI.toURL();
            System.out.println("url:" + url.toString());
            String proxyHost = "uc3-mrtingest-stg02.cdlib.org";
            int proxyPort = 65002;
            File outFile = new File(fileS);
            if (outFile.exists()) {
                outFile.delete();
                System.out.println("delete:" + outFile.getAbsolutePath());
            }
            System.out.println("file:" + outFile.getCanonicalPath());
            if (false) return;
            HashMap<String, String> headers = new HashMap<>();
            HTTPGetUtil getUtil = HTTPGetUtil.build(DEFAULT_TIMEOUT, proxyHost, proxyPort, headers);
            HttpGetNew.getFile(url, outFile, getUtil);
            System.out.println("Filelen:" + outFile.length());
            if (false) {
                String out = FileUtil.file2String(outFile);
                System.out.println("File:" + outFile.getCanonicalPath() + "\n>>>\n"
                        + out + "\n<<<\n"
                );
            }
            HttpGetNew.getFile(url, outFile, getUtil);
            System.out.println("Filelen:" + outFile.length());
            
            Long getLen = getUtil.getContentLength(url.toString());
            if (getLen == null) {
                System.out.println("content-length header not available");
            } else {
                System.out.println("content-length = " + getLen);
            }
            
        } catch (Exception e) {
            System.out.println("Exception:" + e);
            e.printStackTrace();
        }
    }
    
    public static void main_original(String[] args) {
        try {
            // 1. Define your local proxy details (e.g., localhost on port 8080)
            String proxyHost = "uc3-mrtingest-stg02.cdlib.org";
            int proxyPort = 65002;
            
            // 2. Create the Proxy object (Proxy.Type.HTTP is typically used for both HTTP and HTTPS)
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

            // 3. Set up the target URL
            URI urlI = new URI("http://example.com");
            URL url = urlI.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
            
            // 4. Configure the request
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            // 5. Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                System.out.println("LINE:" + inputLine);
            }
            
            // Close streams
            in.close();
            connection.disconnect();
            
            System.out.println("Response: " + content.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}