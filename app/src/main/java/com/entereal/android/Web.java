package com.entereal.android;

import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.webkit.SslErrorHandler;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

// 2020.01.29 https 변경
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;

import org.apache.http.message.BasicNameValuePair;

/**
 * Created by Administrator on 2017-01-02.
 */
public class Web {
    private static final String LOG_TAG = "NMapViewer";
    private static final String USER_AGENT = "Mozilla/5.0";

    //public static final String URL = "http://service.innoparkinglot.kr:8080";

    public static final String URL = "https://smartguardzone.kr/";   // smartguardzone  // 2020.01.29 https 변경

    //public static final String URL = "http://14.38.161.51"; // php
    //public static final String URL = "http://192.168.1.252:8080"; // jsp
    //public static final String URL = "http://192.168.0.101";

    public static class VO {
        public String url = "";
        public String parameters = "";

        public VO(String url) {
            this.url = url;
        }

        public VO AddParam(String var1, String var2) {
            if(!parameters.equals("")) {
                parameters += "&";
            }
            parameters += var1 + "=" + var2;
            return this;
        }
    }
    public interface Callback {
        void callback(JSONObject obj);
    }

    private  static Callback callback;

        // HTTP GET request
    public static void sendGet() throws Exception {

        String url = "http://www.google.com/search?q=mkyong";

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        Log.i(LOG_TAG, response.toString());
    }

    // HTTP POST request
    public static void sendPost(String url, String parameters) throws Exception {

        Log.i(LOG_TAG, "1");
        URL obj = new URL(url);
        Log.i(LOG_TAG, "1-1");
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        Log.i(LOG_TAG, "1-2");

        Log.i(LOG_TAG, "2");
        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3\"");

        //String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        Log.i(LOG_TAG, "3");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        if(!parameters.equals("")) {
            wr.writeBytes(parameters);
        }
        wr.flush();
        wr.close();

        Log.i(LOG_TAG, "4");
        int responseCode = con.getResponseCode();
        /*System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + parameters);
        System.out.println("Response Code : " + responseCode);*/

        Log.i(LOG_TAG, "\nSending 'POST' request to URL : " + url);
        Log.i(LOG_TAG, "Post parameters : " + parameters);
        Log.i(LOG_TAG, "Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        //System.out.println(response.toString());
        Log.i(LOG_TAG, response.toString());
    }

    public static String request(String urlStr) {
        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                int resCode = conn.getResponseCode();
                if (resCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
                    String line = null;
                    while(true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        output.append(line + "\n");
                    }

                    reader.close();
                    conn.disconnect();
                }
            }
        } catch(Exception ex) {
            Log.i(LOG_TAG, "Exception in processing response.", ex);
            //Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }

        return output.toString();
    }

    // https 는 따로 작업해야 한다. ->  2020.01.29 HTTPS code 변경
    // (HttpsURLConnection)이걸로 형 변환만 해주면 자동으로 될려나...?
    public static void send(VO vo, Callback call)
    {
        callback = call;
        new ProcessSendTask().execute(vo, null, null);
    }

    //AsyncTask<Params,Progress,Result>
    private static class ProcessSendTask extends AsyncTask<VO, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(LOG_TAG, "onPreExecute");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(VO... params) {
            try
            {
                VO vo = params[0];
                URL obj = new URL(vo.url);

                // 2020.01.29 HTTPS code 변경
                trustAllHosts();

                HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
//                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }

                });

                HttpURLConnection connection = con;

                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Accept-Language", "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                OutputStream outputStream = connection.getOutputStream();
                if(!(vo.parameters == null || vo.parameters.equals(""))) {
                    outputStream.write(vo.parameters.getBytes("UTF-8"));
                    //Log.i(LOG_TAG, "param:" + vo.parameters);
                }

                outputStream.flush();
                outputStream.close();

                connection.connect();

                int responseCode = connection.getResponseCode();

//                Log.i(LOG_TAG, "Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                //Log.i(LOG_TAG, "response:" + response.toString());
                JSONObject jsonObj = (JSONObject)JSONValue.parse(response.toString());
                if(callback != null) {
                    callback.callback(jsonObj);
                }
                callback = null;
            }
            catch (Exception e)
            {
                Log.i(LOG_TAG, "Web send Exception");
                e.printStackTrace();
            }
            return null;
        }

        private static void trustAllHosts() {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                    // TODO Auto-generated method stub
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                    // TODO Auto-generated method stub
                }
            }};

            // Install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(LOG_TAG, "onPostExecute");
            super.onPostExecute(aVoid);
        }

        /*@Override
        protected void onPostExecute(String result) {
            Log.i(LOG_TAG, "onPostExecute");
            super.onPostExecute();

            if(result != null) {
                Log.i(LOG_TAG, "ASYNC result:" + result);
            }
        }*/

        @Override
        protected void onCancelled() {
            Log.i(LOG_TAG, "onCancelled");
            super.onCancelled();
        }


    }

    private static String getURLQuery(List<BasicNameValuePair> params){
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;

        for (BasicNameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                stringBuilder.append("&");

            try {
                stringBuilder.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }
}


