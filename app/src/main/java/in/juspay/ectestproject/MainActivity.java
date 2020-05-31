package in.juspay.ectestproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.webkit.WebView;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import in.juspay.godel.PaymentActivity;
import in.juspay.godel.core.PaymentConstants;
import in.juspay.godel.ui.PaymentFragment;

public class MainActivity extends AppCompatActivity {

    String customerId = "9963169929";
    String customerEmail = "9963169929";
    String customerMobile = "9963169929";
    String merchantId;
    String clientId;

    String orderId =  "R" + (long) (Math.random() * 10000000000L);
    ArrayList<String> endUrls = new ArrayList<>(Arrays.asList("https://www.reload.in/recharge/", ".*www.reload.in/payment/f.*", ".*sandbox.juspay.in\\/thankyou.*", ".*wallet.juspay.dev:8080/recharge/payment.*", ".*www.foodstag.in\\/payment-gateway\\/handle-payment.*", ".*sandbox.juspay.in\\/end.*", ".*www.voonik.org\\/checkout\\/juspay_callback", ".*localhost.*", ".*api.juspay.in\\/end.*"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        merchantId = getApplicationContext().getResources().getString(R.string.mid);
        clientId  = merchantId+"_android";;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        PaymentActivity.preFetch(MainActivity.this, clientId);


        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getDetails();


    }


    public void getDetails(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your mobile number");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Let's Test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customerMobile =  input.getText().toString().equals("") ? customerMobile : input.getText().toString() ;
                pay();
            }
        });
        builder.setNegativeButton("No! I'm Tired", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finishAffinity();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    public void pay(){
        try{
            final JuspayHTTPResponse[] response = NetbankingUtils.generateOrder(orderId, customerMobile, customerEmail, MainActivity.this);
            JSONObject order = new JSONObject(response[0].responsePayload);
            JSONObject auth = order.optJSONObject("juspay");
            Bundle intentBundle = new Bundle();

            ///Basic Params
            intentBundle.putString(PaymentConstants.MERCHANT_ID, merchantId);
            intentBundle.putString(PaymentConstants.CLIENT_ID, clientId);
            intentBundle.putString(PaymentConstants.ORDER_ID, order.getString("order_id"));
            intentBundle.putString(PaymentConstants.AMOUNT,"1");
            intentBundle.putString(PaymentConstants.CUSTOMER_ID, customerId);
            intentBundle.putString(PaymentConstants.CLIENT_EMAIL, customerEmail);
            intentBundle.putString(PaymentConstants.CLIENT_MOBILE_NO, customerMobile);
            intentBundle.putString(PaymentConstants.ENV, PaymentConstants.ENVIRONMENT.SANDBOX);

            ///Godel Params
            intentBundle.putStringArrayList(PaymentConstants.END_URLS, endUrls);
//            intentBundle.putBoolean("clearCookies", false);

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


}
