package huuduc.nhd.convertcurrency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import huuduc.nhd.convertcurrency.Entity.Country;
import huuduc.nhd.convertcurrency.Entity.Currency;
import huuduc.nhd.convertcurrency.Process.XMLPullParserHandler;
import huuduc.nhd.convertcurrency.Services.CountryServices;
import huuduc.nhd.convertcurrency.Services.CurrencyServices;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String DESTINATION_ITEM_SELECT = "destination item select";
    private static final String ORIGIN_ITEM_SELECT      = "original item select";

    private List<Currency> mListCurrency;
    private List<Country>  mListCountry;
    private ProgressDialog mDialog;
    private LinearLayout   mContainer;
    private ImageView      mButtonReverse;
    private EditText       mEditTextOriginal, mEditTextResult;
    private Spinner        mSpinnerOriginal, mSpinnerDestination;
    private Button         mButtonConvert;

    private String destinationCountryName;
    private String originalCurrencyCode;
    private int    posOriginal;
    private int    posDestination;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        maping();

        // Load item for spinner
        loadItems();

        // Spinner event
        mSpinnerDestination.setOnItemSelectedListener(this);
        mSpinnerOriginal.setOnItemSelectedListener(this);

        // Convert button event
        mButtonConvert.setOnClickListener(this);
        mButtonReverse.setOnClickListener(this);

        // save value when rotate
        if(savedInstanceState != null){
            onRestoreInstanceState(savedInstanceState);
        }

    }

    protected void maping(){
        mSpinnerDestination = findViewById(R.id.spinnerDestinationCurrency);
        mEditTextOriginal   = findViewById(R.id.editTexOriginalCurrency);
        mSpinnerOriginal    = findViewById(R.id.spinnerOriginalCurrency);
        mEditTextResult     = findViewById(R.id.editTextTextResult);
        mButtonReverse      = findViewById(R.id.imageViewReverse);
        mButtonConvert      = findViewById(R.id.buttonConvert);
        mContainer          = findViewById(R.id.container);
    }

    protected void loadItems(){
        if(checkInternetConnection()){
            new AsynTaskLoadCountry().execute();
        }else{
            List<String> items = new ArrayList<>();
            items.add("[Unknown country]");
            setValueForSpinner(items);
            Toast.makeText(this, "Không có kết nối internet" , Toast.LENGTH_SHORT).show();
        }
    }

    protected void reverseItem(){
        mSpinnerOriginal.setSelection(posDestination);
        mSpinnerDestination.setSelection(posOriginal);
    }

    protected void setValueForSpinner(List<String> items){
        // spinner original currency
        ArrayAdapter<String> adapterOriginal    = new ArrayAdapter<>(MainActivity.this,R.layout.spinner_item,items);
        adapterOriginal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerOriginal.setAdapter(adapterOriginal);

        // spinner destination currency
        ArrayAdapter<String> adapterDestination = new ArrayAdapter<>(MainActivity.this,R.layout.spinner_item,items);
        adapterDestination.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerDestination.setAdapter(adapterDestination);
    }
    protected void convertMoney(){
          if(checkInternetConnection() && mSpinnerOriginal.getAdapter().getCount() > 1){
              new AsynTaskConvertCurrency().execute();
          }else if(mSpinnerOriginal.getAdapter().getCount() <=1){
              Toast.makeText(this, "Vui lòng refresh lại trang", Toast.LENGTH_SHORT).show();
          } else{
              Toast.makeText(this, "Không có kết nối internet", Toast.LENGTH_SHORT).show();
          }
    }

    protected boolean checkInternetConnection(){
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo!= null && networkInfo.isAvailable() && networkInfo.isConnected())
            return true;
        else{
            return false;
        }
    }

    protected void showProgressDialog(){
        mDialog = new ProgressDialog(this);
        mDialog .setCancelable(false);
        mDialog .show();
        mDialog .setContentView(R.layout.loading_view);
        mDialog .getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    protected void hideProgressDialog(){
        mDialog.dismiss();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refreshPage:{
                loadItems();
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try{
           switch(adapterView.getId()){
               case R.id.spinnerOriginalCurrency:{
                   originalCurrencyCode   = new CountryServices(mListCountry).getCurrencyCode(adapterView.getItemAtPosition(i).toString());
                   posOriginal = i;
                   break;
               }
               case R.id.spinnerDestinationCurrency:{
                   destinationCountryName = adapterView.getItemAtPosition(i).toString().trim();
                   posDestination = i;
                   break;
               }
               default:
                   break;
           }
       }catch (ArrayIndexOutOfBoundsException e){

       }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonConvert:{
                convertMoney();
                break;
            }
            case R.id.imageViewReverse:{
                reverseItem();
                break;
            }
            default: {
                break;
            }
        }
    }


    protected class AsynTaskLoadCountry extends AsyncTask<Void,List<Country>,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1002);
                String url = "https://aud.fxexchangerate.com/rss.xml";
                InputStream inputStream = new URL(url).openConnection().getInputStream();
                mListCountry            = new XMLPullParserHandler().parseCountry(inputStream);
                publishProgress(mListCountry);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(List<Country>... values) {
            List<String> countryNames = new CountryServices(values[0]).getAllCountryName();

            // set default value for spinner
            setValueForSpinner(countryNames);

            // set value for spinner when rotate screen
            if(posOriginal >=0 && posDestination >=0){
                mSpinnerOriginal.setSelection(posOriginal);
                mSpinnerDestination.setSelection(posDestination);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgressDialog();
        }
    }

    protected class AsynTaskConvertCurrency extends AsyncTask<Void,List<Currency>,Boolean>{
        private String url;
        private Double money;

        @Override
        protected void onPreExecute() {
            try {

                money  = Double.parseDouble(mEditTextOriginal.getText().toString());
                url    = "https://" + originalCurrencyCode + ".fxexchangerate.com/rss.xml";

            } catch (Exception e){
                Toast.makeText(MainActivity.this, "Vui lòng nhập tiền cần đổi", Toast.LENGTH_SHORT).show();
                mEditTextOriginal.requestFocus();
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if(TextUtils.isEmpty(url))
                return false;
            try {
                if(!url.startsWith("https") && !url.startsWith("http"))
                    url = "https://" + url;

                InputStream inputStream = new URL(url).openConnection().getInputStream();
                mListCurrency           = new XMLPullParserHandler().parseCurrency(inputStream);
                publishProgress(mListCurrency);

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(List<Currency>... values) {
            String[] tempt = destinationCountryName.split("-");
            String destinationCountryName = tempt[1].trim();

            CurrencyServices services = new CurrencyServices(values[0]);
            Double exchangeRate       = services.getCurrency(destinationCountryName);
            Double result             = services.convertCurrency(money,exchangeRate);
            mEditTextResult.setText(String.valueOf(result));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MainActivity.ORIGIN_ITEM_SELECT,posOriginal);
        outState.putInt(MainActivity.DESTINATION_ITEM_SELECT,posDestination);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        posOriginal    = savedInstanceState.getInt(MainActivity.ORIGIN_ITEM_SELECT);
        posDestination = savedInstanceState.getInt(MainActivity.DESTINATION_ITEM_SELECT);
    }

}