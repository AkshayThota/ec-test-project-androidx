package in.juspay.ectestproject;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import in.juspay.godel.PaymentActivity;
import in.juspay.godel.core.PaymentConstants;

public class MainActivity extends AppCompatActivity {

    String customerId = "9963169929";
    String customerEmail = "9963169929";
    String customerMobile = "9963169929";
    String merchantId = "foodpanda";
    String clientId = "foodpanda_android";

    String orderId =  "R" + (long) (Math.random() * 10000000000L);
    ArrayList<String> endUrls = new ArrayList<>(Arrays.asList("https://www.reload.in/recharge/", ".*www.reload.in/payment/f.*", ".*sandbox.juspay.in\\/thankyou.*", ".*wallet.juspay.dev:8080/recharge/payment.*", ".*www.foodstag.in\\/payment-gateway\\/handle-payment.*", ".*sandbox.juspay.in\\/end.*", ".*www.voonik.org\\/checkout\\/juspay_callback", ".*localhost.*"));




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PaymentActivity.preFetch(this, "foodpanda_android");



        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try{
            final JuspayHTTPResponse[] response = NetbankingUtils.generateOrder(orderId, customerMobile, customerEmail, MainActivity.this);
            JSONObject order = new JSONObject(response[0].responsePayload);
            JSONObject auth = order.optJSONObject("juspay");
            Bundle intentBundle = new Bundle();

            ///Basic Params
            intentBundle.putString(PaymentConstants.MERCHANT_ID, merchantId);
            intentBundle.putString(PaymentConstants.CLIENT_ID, clientId);
            intentBundle.putString(PaymentConstants.ORDER_ID, order.getString("order_id"));
            intentBundle.putString(PaymentConstants.AMOUNT, NetbankingUtils.amount);
            intentBundle.putString(PaymentConstants.CUSTOMER_ID, customerId);
            intentBundle.putString(PaymentConstants.CUSTOMER_EMAIL, customerEmail);
            intentBundle.putString(PaymentConstants.CUSTOMER_MOBILE, customerMobile);
            intentBundle.putString(PaymentConstants.ENV, PaymentConstants.ENVIRONMENT.SANDBOX);

            ///Godel Params
            intentBundle.putStringArrayList(PaymentConstants.END_URLS, endUrls);
            intentBundle.putBoolean("clearCookies", false);

            //EC Params
            intentBundle.putString(PaymentConstants.SERVICE, "in.juspay.ec");
            intentBundle.putString(PaymentConstants.CLIENT_AUTH_TOKEN, auth.optString("client_auth_token", ""));

            Intent intent = new Intent(this,PaymentPageActivity.class);
            intent.putExtras(intentBundle);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public Bundle jsonObjArraytoBundle (JSONArray jsonArray) throws Exception{
            Bundle bundle = new Bundle();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                JSONArray objKeys = obj.names();
                bundle.putString((objKeys.getString(0)),obj.getString(objKeys.getString(0)));
            }
            return bundle;
    }

    public Bundle jsonToBundle (JSONObject jsonObject) throws Exception {
        Bundle bundle = new Bundle();
        JSONArray keys = jsonObject.names();

        for (int i = 0; i < keys.length (); ++i) {
            String key = keys.getString (i);
            Object value = jsonObject.getString (key);

            if(value instanceof JSONArray){
                JSONArray jsonArray = (JSONArray)value;
                String[] stringArray = null;
                if (jsonArray != null) {
                    stringArray = new String[jsonArray.length()];
                    for (int j = 0; j < jsonArray.length(); j += 1) {
                        Object jsonElement = jsonArray.opt(j);
                        if(jsonElement instanceof String)
                         stringArray[j] = (String)jsonElement;
                    }
                }
                ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(stringArray));
                bundle.putStringArrayList(key,arrayList);
            }else{
                bundle.putString(key,(String)value);
            }
        }

        return bundle;
    }

    public void pluginPay(View view) {
        Bundle intentBundle = new Bundle();

        JSONObject baseParams = new JSONObject();
        JSONObject serviceParams = new JSONObject();
        JSONArray customParams = new JSONArray();

        try{
            baseParams.put("merchantId","foodpanda");
            baseParams.put("clientId","foodpanda_android");
            baseParams.put("transactionId","TDSOJFIOSD");
            baseParams.put("orderId","ORDER_123");
            baseParams.put("amount","1.00");
            baseParams.put("customerId","9963169929");
            baseParams.put("customerEmail","kiranpuppala.96@gmail.com");
            baseParams.put("customerMobile","9963169929");
            baseParams.put("environment","sandbox");

            intentBundle = jsonToBundle(baseParams);


            serviceParams.put("service","in.juspay.ec");
            serviceParams.put("clientAuthToken","");
            JSONArray endUrls = new JSONArray();
            endUrls.put("url1");
            serviceParams.put("endUrls",endUrls);
            serviceParams.put("payload","");

            intentBundle.putAll(jsonToBundle(serviceParams));

            JSONObject param1 = new JSONObject();
            param1.put("udf_operator","vodaphone");
            JSONObject param2 = new JSONObject();
            param2.put("udf_circle","ap");

            customParams.put(param1);
            customParams.put(param2);

            intentBundle.putAll(jsonObjArraytoBundle(customParams));

            Bundle bunddd = intentBundle;

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
