package in.juspay.ectestproject;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import in.juspay.android_lib.core.Constants;
import in.juspay.hypersdk.data.JuspayConstants;

/**
 * Created by Veera.Subbiah on 15/03/18.
 */

public class NetbankingUtils {

    private static final String LOG_TAG = "NetbankingUtils";
    protected static final String amount  = "1.00";

    static JuspayHTTPResponse[] generateOrder(String orderId, String mobile, String email, Context context) {
        try {
            String orderUrls[] = new String[]{context.getString(R.string.base_url) + "/order/create"};
            String auths[] = new String[]{Base64.encodeToString((R.string.api_key + ":").getBytes(), Base64.DEFAULT)};
            JuspayHTTPResponse response[] = new JuspayHTTPResponse[orderUrls.length];

            for (int i = 0; i < orderUrls.length; i++) {
                String orderUrl = orderUrls[i];
                HttpsURLConnection connection = (HttpsURLConnection) (new URL(orderUrl).openConnection());

                connection.setSSLSocketFactory(new TLSSocketFactory());
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Basic QjcxNzFCMDEwOTU0RUUxQUIwRDVEMjhGQjAwNDcyOg==");
                connection.setRequestProperty("version", "2018-07-01");
                connection.setDoOutput(true);

                Map<String, String> payload = new HashMap<>();
                payload.put(JuspayConstants.ORDER_ID, orderId);
                payload.put(JuspayConstants.AMOUNT, amount);
                payload.put("customer_id", mobile);
                payload.put("customer_email", email);
                payload.put("customer_phone", mobile);
                payload.put("return_url", context.getString(R.string.end_url));
                payload.put(JuspayConstants.DESCRIPTION, "Test Transaction");
                payload.put("options.get_client_auth_token", "true");
                payload.put("metadata.AXIS_UPI:gateway_reference_id", "1c172215b3004f51a3af982cf1b6c1fc");

                OutputStream stream = connection.getOutputStream();
                stream.write(generateQueryString(payload).getBytes());
                response[i] = new JuspayHTTPResponse(connection);
                Log.d(LOG_TAG, "Order: " + response[i].responsePayload);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static void createCustomer(String mobile, String email, Context context) {
        try {
            String url =context.getString(R.string.base_url) + "/customers";
            String auth = Base64.encodeToString((R.string.api_key + ":").getBytes(), Base64.DEFAULT);
            JuspayHTTPResponse response = null;

            HttpsURLConnection connection = (HttpsURLConnection) (new URL(url).openConnection());
            connection.setRequestMethod("POST");
            connection.setSSLSocketFactory(new TLSSocketFactory());
            connection.setRequestProperty("Authorization", "Basic " + auth);
            connection.setRequestProperty("version", "2018-07-01");
            connection.setDoOutput(true);

            Map<String, String> payload = new HashMap<>();
            payload.put("object_reference_id", mobile);
            payload.put("mobile_number", mobile);
            payload.put("email_address", email);
            payload.put("first_name", "veera");
            payload.put("last_name", "subbiah");
            payload.put(JuspayConstants.DESCRIPTION, "Test Transaction");
            payload.put("options.get_client_auth_token", "true");

            OutputStream stream = connection.getOutputStream();
            stream.write(generateQueryString(payload).getBytes());
            response = new JuspayHTTPResponse(connection);
            Log.d(LOG_TAG, "Customer: " + response.responsePayload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static JuspayHTTPResponse refundOrder(String orderId, Context context) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) (new URL(context.getString(R.string.base_url) + "/orders/" + orderId + "/refunds").openConnection());
            connection.setRequestMethod("POST");
            connection.setSSLSocketFactory(new TLSSocketFactory());
            connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((R.string.api_key + ":").getBytes(), Base64.DEFAULT));
            connection.setDoOutput(true);

            Map<String, String> payload = new HashMap<>();
            payload.put("unique_request_id", orderId);
            payload.put(Constants.AMOUNT, amount);

            OutputStream stream = connection.getOutputStream();
            stream.write(generateQueryString(payload).getBytes());
            JuspayHTTPResponse response = new JuspayHTTPResponse(connection);
            Log.d(LOG_TAG, "Refund Status: " + response.toString());

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static void updateOrder(JSONObject jsonObject, String orderId, Context context) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) (new URL(context.getString(R.string.base_url) + "/orders/" + orderId)).openConnection();
            connection.setRequestMethod("POST");
            connection.setSSLSocketFactory(new TLSSocketFactory());
            connection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((R.string.api_key + ":").getBytes(), Base64.DEFAULT));
            connection.setDoOutput(true);

            Map<String, String> payload = new HashMap<>();
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                payload.put(key, jsonObject.getString(key));
            }

            OutputStream stream = connection.getOutputStream();
            stream.write(generateQueryString(payload).getBytes());
            JuspayHTTPResponse response = new JuspayHTTPResponse(connection);
            Log.d(LOG_TAG, "Order status: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JuspayHTTPResponse getNetbankingURL(String orderId, String bankName) {
        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL("https://api.juspay.in/txns").openConnection());
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            Map<String, String> payload = new HashMap<>();
            payload.put("order_id", orderId);
            payload.put("merchant_id", "juspay_recharge");
            payload.put("payment_method_type", "NB");
            payload.put("payment_method", bankName);
            payload.put("redirect_after_payment", "true");
            payload.put("format", "json");

            OutputStream stream = connection.getOutputStream();
            stream.write(generateQueryString(payload).getBytes());
            JuspayHTTPResponse response = new JuspayHTTPResponse(connection);
            Log.d(LOG_TAG, "getNetbankingURL Response: " + response.toString());

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





    public static String generateQueryString(Map<String, String> queryString) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        Map.Entry e;
        for(Iterator var2 = queryString.entrySet().iterator(); var2.hasNext(); sb.append(URLEncoder.encode((String)e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode((String)e.getValue(), "UTF-8"))) {
            e = (Map.Entry)var2.next();
            if(sb.length() > 0) {
                sb.append('&');
            }
        }

        return sb.toString();
    }

    public static SSLContext initializeSSLContext(Context mContext){
        try {
            SSLContext instance = SSLContext.getInstance("TLSv1.2");
            instance.init(null, null, new java.security.SecureRandom());
            return instance;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

}

class TLSSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory delegate;

    public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        delegate = context.getSocketFactory();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        return enableTLSOnSocket(delegate.createSocket());
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket enableTLSOnSocket(Socket socket) {
        if(socket != null && (socket instanceof SSLSocket)) {
            ((SSLSocket)socket).setEnabledProtocols(new String[] {"TLSv1.1", "TLSv1.2"});
        }
        return socket;
    }

}