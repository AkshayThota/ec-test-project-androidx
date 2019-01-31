package in.juspay.ectestproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import org.json.JSONObject;

import in.juspay.godel.PaymentActivity;

/**
 * Created by kiran.puppala on 8/7/18.
 */


public class PaymentPageActivity extends AppCompatActivity {
    View createWalletContainer,cardTxn;
    EditText walletForCW,walletId,outputText,payWithApp,delinkWalletName,delinkWalletId,refreshWalletName,refreshWalletId,emiBank,emiTenure,cardtoken_deletecard;
    EditText nameOnCard,cardNumber,cvv,expMonth,expYear,cardType ;
    CheckBox saveForFuture,upiSdkPresent,isEmi;
    View linkWalletContainer;
    EditText enterOtp,bankCode,sellerId,custVpa,cardToken,authType;
    EditText walletNameForDebit,walletDirToken,sdkPresent;
    Bundle bundle;
    Button card_layout_action;
    final static int REQUEST_CODE = 88;
    Boolean saveCard = false,isUpiSdkPresent=false,isEmiTxn=false;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent() != null)
        bundle = getIntent().getExtras();
        setContentView(R.layout.activity_payment_page);
         createWalletContainer = findViewById(R.id.createWalletContainer);
         cardTxn = findViewById(R.id.cardTxn);
         nameOnCard = cardTxn.findViewById(R.id.nameOnCard);
         walletDirToken = findViewById(R.id.walletDirToken);
         sdkPresent = findViewById(R.id.sdkPresent);
         delinkWalletName = findViewById(R.id.delinkWalletName);
         delinkWalletId = findViewById(R.id.delinkWalletId);
         refreshWalletId = findViewById(R.id.refreshWalletId);
         refreshWalletName = findViewById(R.id.refreshWalletName);
         cardNumber = cardTxn.findViewById(R.id.cardNumber);
         cardToken = findViewById(R.id.cardToken);
         cardtoken_deletecard = findViewById(R.id.cardtoken_deletecard);
         authType = findViewById(R.id.authType);
         cvv = cardTxn.findViewById(R.id.cvv);
         expMonth = cardTxn.findViewById(R.id.expMonth);
         expYear = cardTxn.findViewById(R.id.expYear);
         cardType = cardTxn.findViewById(R.id.cardType);
         emiBank = cardTxn.findViewById(R.id.emiBank);
         emiTenure = cardTxn.findViewById(R.id.emiTenure);
         outputText = findViewById(R.id.outputText);
         custVpa = findViewById(R.id.custVpa);
         upiSdkPresent = findViewById(R.id.upiSdkPresent);
         upiSdkPresent.setOnCheckedChangeListener(listener);
         payWithApp = findViewById(R.id.payWithApp);
         saveForFuture = cardTxn.findViewById(R.id.saveForFuture);
         isEmi=cardTxn.findViewById(R.id.isEmi);
         isEmi.setOnCheckedChangeListener(listener);
         card_layout_action = cardTxn.findViewById(R.id.card_layout_action);
         saveForFuture.setOnCheckedChangeListener(listener);

         card_layout_action.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 cardTxnClick();
             }
         });

         walletForCW = findViewById(R.id.walletForCW);
         linkWalletContainer = findViewById(R.id.linkWalletContainer);
         enterOtp = findViewById(R.id.enterOtp);
         walletId = findViewById(R.id.walletId);
         bankCode = findViewById(R.id.bankCode);
         sellerId = findViewById(R.id.sellerId);
         walletNameForDebit = findViewById(R.id.walletNameForDebit);
         createPD();
    }

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
            switch(buttonView.getId()){
                case R.id.saveForFuture: saveCard=isChecked;break;
                case R.id.upiSdkPresent: isUpiSdkPresent = isChecked;break;
                case R.id.isEmi: isEmiTxn = isChecked;break;
            }
        }
    };


    public void nbClick (View view){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName","nbTxn");
            jsonObject.put("paymentMethodType","NB");
            jsonObject.put("paymentMethod",bankCode.getText().toString());
            jsonObject.put("redirectAfterPayment",true);
            jsonObject.put("format","json");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload",jsonObject.toString());
            startEC(newBundle);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createWalletClick (View view){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName","createWallet");
            jsonObject.put("walletName",walletForCW.getText().toString());
            jsonObject.put("command","authenticate");
            jsonObject.put("sdkWalletIdentifier",sellerId.getText().toString());
            jsonObject.put("otpPage",false);
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload",jsonObject.toString());
            startEC(newBundle);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void refreshBalancesClick (View view){
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName","refreshBalances");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload",jsonObject.toString());
            startEC(newBundle);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteCard (View view){
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName","deleteCard");
            jsonObject.put("cardToken",cardtoken_deletecard.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload",jsonObject.toString());
            startEC(newBundle);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void linkWalletClick (View view){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName","linkWallet");
            jsonObject.put("walletId",walletId.getText().toString());
            jsonObject.put("otp",enterOtp.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload",jsonObject.toString());
            startEC(newBundle);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void walletDirClick (View view){

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName","walletTxn");
            jsonObject.put("paymentMethodType","Wallet");
            jsonObject.put("paymentMethod",walletNameForDebit.getText().toString());
            jsonObject.put("directWalletToken",walletDirToken.getText().toString());
            jsonObject.put("sdkPresent",sdkPresent.getText().toString());
            jsonObject.put("redirectAfterPayment",true);
            jsonObject.put("format","json");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload",jsonObject.toString());
            startEC(newBundle);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void upiClick (View view){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName","upiTxn");
            jsonObject.put("paymentMethodType","UPI");
            jsonObject.put("paymentMethod","UPI");
            jsonObject.put("displayNote","");
            jsonObject.put("currency","INR");
            jsonObject.put("custVpa",custVpa.getText().toString());
            jsonObject.put("upiSdkPresent",isUpiSdkPresent);
            jsonObject.put("payWithApp",payWithApp.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload",jsonObject.toString());
            startEC(newBundle);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void cardTxnClick (){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName","cardTxn");
            jsonObject.put("paymentMethodType","CARD");
            jsonObject.put("paymentMethod",cardType.getText().toString());
            jsonObject.put("cardToken",cardToken.getText().toString());
            jsonObject.put("cardNumber",cardNumber.getText().toString());
            jsonObject.put("cardExpMonth",expMonth.getText().toString());
            jsonObject.put("cardExpYear",expYear.getText().toString());
            jsonObject.put("nameOnCard",nameOnCard.getText().toString());
            jsonObject.put("cardSecurityCode",cvv.getText().toString());
            jsonObject.put("saveToLocker",saveCard);
            jsonObject.put("authType",authType.getText().toString());
            jsonObject.put("redirectAfterPayment",true);
            jsonObject.put("format","json");
            jsonObject.put("isEmi",isEmiTxn);
            jsonObject.put("emiBank",emiBank.getText().toString());
            jsonObject.put("emiTenure",emiTenure.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload",jsonObject.toString());
            startEC(newBundle);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void delinkWallet (View view){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName","delinkWallet");
            jsonObject.put("walletId",delinkWalletId.getText().toString());
            jsonObject.put("walletName",delinkWalletName.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload",jsonObject.toString());
            startEC(newBundle);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void refreshWallet (View view){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName","refreshWallet");
            jsonObject.put("walletId",refreshWalletId.getText().toString());
            jsonObject.put("walletName",refreshWalletName.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload",jsonObject.toString());
            startEC(newBundle);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void googlePayReady(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "googlePayReady");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void payWithGooglePay(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "googlePayTxn");

            String mobileNumber = ((EditText) findViewById(R.id.gpay_mobile_number)).getEditableText().toString();
            if(!mobileNumber.equals("")) {
                if(!mobileNumber.contains("+91") || !mobileNumber.contains("0091")) {
                    mobileNumber = "+91" + mobileNumber;
                }
                jsonObject.put("mobileNumber", mobileNumber);
            }

            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {

        }
    }

    public void getPaymentMethods (View view){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName","getPaymentMethods");
            jsonObject.put("refresh","true");
            jsonObject.put("offers","true");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload",jsonObject.toString());
            startEC(newBundle);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void startEC(Bundle bundle){
        if(pd==null) createPD();
        pd.show();
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtras(bundle);
        Log.d("REQUEST PARAMS",bundle.toString());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(pd!=null) pd.hide();
        if(data.getStringExtra("payload") != null){
            Log.e("PAYLOADDDD",data.getStringExtra("payload"));
            outputText.setText(data.getStringExtra("payload"));
        }

    }

    private void createPD(){
        pd = new ProgressDialog(this);
        pd.setMessage("Processing...");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(pd != null) {
            pd.hide();
            pd.dismiss();
        }
        pd = null;
    }
}
