package in.juspay.ectestproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;

import org.json.JSONObject;

import in.juspay.godel.PaymentActivity;
import in.juspay.godel.ui.PaymentFragment;
import lib.android.paypal.com.magnessdk.MagnesSDK;
import lib.android.paypal.com.magnessdk.MagnesSettings;

/**
 * Created by kiran.puppala on 8/7/18.
 */


public class PaymentPageActivity extends AppCompatActivity {
    final static int REQUEST_CODE = 88;
    PaymentFragment fragment;
    View createWalletContainer, cardTxn;
    EditText walletForCW, walletId, sdkName, outputText, payWithApp, delinkWalletName, delinkWalletId, refreshWalletName, refreshWalletId, emiBank, emiTenure, cardtoken_deletecard;
    EditText refreshBalancesSdkIdentifier, refreshWalletSdkIdentifier, shouldLink;
    EditText nameOnCard, cardNumber, cvv, expMonth, expYear, cardType, cfPaymentMethod;
    CheckBox saveForFuture, upiSdkPresent, isEmi;
    View linkWalletContainer;
    EditText enterOtp, bankCode, sellerId, custVpa, cardToken, authType;
    EditText walletNameForDebit, walletDirToken, sdkPresent;
    Bundle bundle;
    Button tokenize_card;
    Button card_layout_action;
    Boolean saveCard = false, isUpiSdkPresent
            = false, isEmiTxn = false;
    ProgressDialog pd;
    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.saveForFuture:
                    saveCard = isChecked;
                    break;
                case R.id.upiSdkPresent:
                    isUpiSdkPresent = isChecked;
                    break;
                case R.id.isEmi:
                    isEmiTxn = isChecked;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null)
            bundle = getIntent().getExtras();

        setContentView(R.layout.activity_payment_page);

        fragment = new PaymentFragment();


        createWalletContainer = findViewById(R.id.createWalletContainer);
        refreshBalancesSdkIdentifier = findViewById(R.id.refreshBalancesSdkIdentifier);
        refreshWalletSdkIdentifier = findViewById(R.id.refreshWalletSdkIdentifier);
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
        shouldLink = findViewById(R.id.shouldLink);
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
        isEmi = cardTxn.findViewById(R.id.isEmi);
        isEmi.setOnCheckedChangeListener(listener);
        card_layout_action = cardTxn.findViewById(R.id.card_layout_action);
        tokenize_card = cardTxn.findViewById(R.id.tokenize_card);
        saveForFuture.setOnCheckedChangeListener(listener);

        card_layout_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardTxnClick();
            }
        });

        tokenize_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tokenizeCard();
            }
        });


        walletForCW = findViewById(R.id.walletForCW);
        linkWalletContainer = findViewById(R.id.linkWalletContainer);
        enterOtp = findViewById(R.id.enterOtp);
        walletId = findViewById(R.id.walletId);
        cfPaymentMethod = findViewById(R.id.cfPaymentMethod);
        bankCode = findViewById(R.id.bankCode);
        sellerId = findViewById(R.id.sellerId);
        walletNameForDebit = findViewById(R.id.walletNameForDebit);
        sdkName = findViewById(R.id.sdkName);

        createPD();

        MagnesSettings magnesSettings = new MagnesSettings.Builder(getBaseContext()).build();
        MagnesSDK.getInstance().setUp(magnesSettings);//Setup Magnes sdk before calling riskId function inside sdk.

    }

    public void nbClick(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "nbTxn");
            jsonObject.put("paymentMethodType", "NB");
            jsonObject.put("paymentMethod", bankCode.getText().toString());
            jsonObject.put("redirectAfterPayment", true);
            jsonObject.put("format", "json");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createWalletClick(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "createWallet");
            jsonObject.put("walletName", walletForCW.getText().toString());
            jsonObject.put("command", "authenticate");
            jsonObject.put("sdkWalletIdentifier", sellerId.getText().toString());
            jsonObject.put("otpPage", false);
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listWalletsClick(View v) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "listWallets");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tokenizeCard() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "tokenizeCard");
            jsonObject.put("cardNumber", cardNumber.getText().toString());
            jsonObject.put("cardExpMonth", expMonth.getText().toString());
            jsonObject.put("cardExpYear", expYear.getText().toString());
            jsonObject.put("nameOnCard", nameOnCard.getText().toString());
            jsonObject.put("cardSecurityCode", cvv.getText().toString());
            jsonObject.put("redirectAfterPayment", true);
            jsonObject.put("format", "json");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshWalletBalancesClick(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "refreshWalletBalances");
            jsonObject.put("sdkWalletIdentifier", refreshBalancesSdkIdentifier.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void cardListClick(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "cardList");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteCard(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "deleteCard");
            jsonObject.put("cardToken", cardtoken_deletecard.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void linkWalletClick(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "linkWallet");
            jsonObject.put("walletId", walletId.getText().toString());
            jsonObject.put("otp", enterOtp.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void walletDirClick(View view) {

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "walletTxn");
            jsonObject.put("paymentMethodType", "Wallet");
            jsonObject.put("paymentMethod", walletNameForDebit.getText().toString());
            jsonObject.put("directWalletToken", walletDirToken.getText().toString());
            jsonObject.put("sdkPresent", sdkPresent.getText().toString());
            jsonObject.put("shouldLink", shouldLink.getText().toString());

            String mobileNumber = ((EditText) findViewById(R.id.gpay_mobile_number)).getEditableText().toString();
            if (!mobileNumber.equals("")) {
                if (!mobileNumber.contains("+91") || !mobileNumber.contains("0091")) {
                    mobileNumber = "+91" + mobileNumber;
                }
                jsonObject.put("walletMobileNumber", mobileNumber);
            }

            jsonObject.put("redirectAfterPayment", true);
            jsonObject.put("format", "json");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upiClick(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "upiTxn");
            jsonObject.put("paymentMethodType", "UPI");
            jsonObject.put("paymentMethod", "UPI");
            jsonObject.put("displayNote", "");
            jsonObject.put("currency", "INR");
            jsonObject.put("custVpa", custVpa.getText().toString());
            jsonObject.put("upiSdkPresent", isUpiSdkPresent);
            jsonObject.put("payWithApp", payWithApp.getText().toString()); //"in.org.npci.upiapp"
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cardTxnClick() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "cardTxn");
            jsonObject.put("paymentMethodType", "CARD");
            jsonObject.put("paymentMethod", cardType.getText().toString());
            jsonObject.put("cardToken", cardToken.getText().toString());
            jsonObject.put("cardNumber", cardNumber.getText().toString());
            jsonObject.put("cardExpMonth", expMonth.getText().toString());
            jsonObject.put("cardExpYear", expYear.getText().toString());
            jsonObject.put("nameOnCard", nameOnCard.getText().toString());
            jsonObject.put("cardSecurityCode", cvv.getText().toString());
            jsonObject.put("saveToLocker", saveCard);
            jsonObject.put("authType", authType.getText().toString());
            jsonObject.put("redirectAfterPayment", true);
            jsonObject.put("format", "json");
            Log.e("ISSEMIIIIIII", isEmiTxn + "");
            jsonObject.put("isEmi", isEmiTxn);
            jsonObject.put("emiBank", emiBank.getText().toString());
            jsonObject.put("emiTenure", emiTenure.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delinkWallet(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "delinkWallet");
//            jsonObject.put("walletId", delinkWalletId.getText().toString());
            jsonObject.put("walletName", delinkWalletName.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshWallet(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "refreshWallet");
            jsonObject.put("walletId", refreshWalletId.getText().toString());
            jsonObject.put("walletName", refreshWalletName.getText().toString());
            jsonObject.put("sdkWalletIdentifier", refreshWalletSdkIdentifier.getText().toString());
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void googlePayReady(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "isDeviceReady");
            jsonObject.put("sdkPresent", sdkName.getText().toString());
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
            if (!mobileNumber.equals("")) {
                if (!mobileNumber.contains("+91") || !mobileNumber.contains("0091")) {
                    mobileNumber = "+91" + mobileNumber;
                }
                jsonObject.put("walletMobileNumber", mobileNumber);
            }

            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {

        }
    }

    public void getPaymentSource(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "getPaymentSource");
            jsonObject.put("refresh", "true");
            jsonObject.put("offers", "true");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPaymentMethods(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "getPaymentMethods");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(fragment != null && fragment.isAdded()) {
            fragment.backPressHandler(true);
        } else {
            super.onBackPressed();
        }
    }

    public void startEC(Bundle bundle) {
        if (pd == null) createPD();
        pd.show();

//        JuspayCallback juspayCallback = new JuspayCallback() {
//            @Override
//            public void onResult(int requestCode, int resultCode, @Nullable Intent data) {
//                Log.d("MerchantActivity", "onResult: " + requestCode + " resultCode " + resultCode);
//
//                if (pd != null) pd.hide();
//                if (data.getStringExtra("payload") != null) {
//                    Log.e("PAYLOADDDD", data.getStringExtra("payload"));
//                    outputText.setText(data.getStringExtra("payload"));
//                }
////                removeFragment(fragment);
////                fragment = null;
//            }
//        };
//        fragment.setJuspayCallback(juspayCallback);
//
//        fragment.setArguments(bundle);
//        fragment.setJuspayCallback(juspayCallback);
//
//        FragmentManager fm = getSupportFragmentManager();
//
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.add(android.R.id.content,fragment,"myFragmentTag");
//        ft.commit();

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtras(bundle);
        Log.d("REQUEST PARAMS", bundle.toString());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (pd != null) pd.hide();
        if (data.getStringExtra("payload") != null) {
            Log.e("PAYLOADDDD", data.getStringExtra("payload"));
            outputText.setText(data.getStringExtra("payload"));
        }

    }

    private void createPD() {
        pd = new ProgressDialog(this);
        pd.setMessage("Processing...");
    }

    private void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void cfClick(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "consumerFinanceTxn");
            jsonObject.put("paymentMethodType", "CONSUMER_FINANCE");
            jsonObject.put("paymentMethod", cfPaymentMethod.getText().toString());
            jsonObject.put("redirectAfterPayment", true);
            jsonObject.put("format", "json");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeCODTxn(View view) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("opName", "codTxn");
            Bundle newBundle = new Bundle(bundle);
            newBundle.putString("payload", jsonObject.toString());
            startEC(newBundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
