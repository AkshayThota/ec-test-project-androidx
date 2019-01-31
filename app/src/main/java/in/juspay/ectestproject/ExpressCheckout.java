package in.juspay.ectestproject;


import android.content.Intent;
import android.os.Bundle;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import in.juspay.godel.PaymentActivity;
import in.juspay.godel.core.PaymentConstants;


public class ExpressCheckout extends CordovaPlugin {

    private final static String LOG_TAG = "ECHEADLESS_PLUGIN";
    private final static int REQUEST_CODE = 88;
    private static final String PAYMENT = "JuspayStartPayment";
    private static final String TRACK_STATUS = "JuspayTrackStatus";
    public static CordovaInterface cordova = null;
    CallbackContext cordovaCallBack;

    @Override
    public void initialize(CordovaInterface initCordova, CordovaWebView webView) {
        cordova = initCordova;
        Log.d(LOG_TAG,"initialized");
        super.initialize(cordova, webView);
    }


    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.d(LOG_TAG,"executed");
        if (action.equals(PAYMENT)) {
            doPayment(args.optJSONObject(0), callbackContext);
            return true;
        }

        return false;
    }


    private void doPayment(JSONObject params, final CallbackContext callbackContext){
        try{
            this.cordovaCallBack = callbackContext;

            JSONObject baseParams = params.optJSONObject("baseParams");
            JSONObject serviceParams = params.optJSONObject("serviceParams");
            JSONArray customParams = params.optJSONArray("customParams");

            Log.d(LOG_TAG,params.toString());
            Log.d(LOG_TAG,baseParams.toString());
            Log.d(LOG_TAG,serviceParams.toString());
            Log.d(LOG_TAG,customParams.toString());

            Bundle intentBundle = jsonToBundle(baseParams);
            intentBundle.putAll(jsonToBundle(serviceParams));
            intentBundle.putAll(jsonObjArraytoBundle(customParams));

            Intent intent = new Intent(cordova.getActivity(), PaymentActivity.class);
            intent.putExtras(intentBundle);

            cordova.startActivityForResult(this, intent, REQUEST_CODE);

        }catch(Exception e){
            callbackContext.error(e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = data.getStringExtra("payload");
        try{
            JSONObject jsonObj = new JSONObject();
            if(result != null){
                jsonObj.put("response",result);
                this.cordovaCallBack.success(jsonObj);
            }else{
                jsonObj.put("response","null data");
                this.cordovaCallBack.error(jsonObj);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private Bundle jsonObjArraytoBundle (JSONArray jsonArray) throws Exception{
        Bundle bundle = new Bundle();
        for(int i=0;i<jsonArray.length();i++){
            JSONObject obj = jsonArray.getJSONObject(i);
            JSONArray objKeys = obj.names();
            bundle.putString((objKeys.getString(0)),obj.getString(objKeys.getString(0)));
        }
        return bundle;
    }

    private Bundle jsonToBundle (JSONObject jsonObject) throws Exception {
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


}
