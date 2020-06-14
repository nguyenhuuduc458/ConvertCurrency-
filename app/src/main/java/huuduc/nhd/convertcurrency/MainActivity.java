package huuduc.nhd.convertcurrency;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import huuduc.nhd.convertcurrency.Entity.Country;
import huuduc.nhd.convertcurrency.Entity.Currency;
import huuduc.nhd.convertcurrency.Process.XMLPullParserHandler;
import huuduc.nhd.convertcurrency.Services.CountryServices;
import huuduc.nhd.convertcurrency.Services.CurrencyServices;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private List<Currency> mListCurrency;
    private List<Country>  mListCountry;
    private EditText       mEditTextOriginal, mEditTextResult;
    private Spinner        mSpinnerOriginal, mSpinnerDestination;
    private Button         mButtonConvert;

    private String destinationCountryName;
    private String originalCurrencyCode;

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
    }

    protected void maping(){
        mSpinnerDestination = findViewById(R.id.spinnerDestinationCurrency);
        mEditTextOriginal   = findViewById(R.id.editTexOriginalCurrency);
        mSpinnerOriginal    = findViewById(R.id.spinnerOriginalCurrency);
        mEditTextResult     = findViewById(R.id.editTextTextResult);
        mButtonConvert      = findViewById(R.id.buttonConvert);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(adapterView.getId()){
            case R.id.spinnerOriginalCurrency:{
                originalCurrencyCode    = new CountryServices(mListCountry).getCountryCode(adapterView.getItemAtPosition(i).toString());
                break;
            }
            case R.id.spinnerDestinationCurrency:{
                destinationCountryName = adapterView.getItemAtPosition(i).toString().trim();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonConvert:{
                new AsynTaskConvertCurrency().execute();
            }
            default: {
                break;
            }
        }
    }

    protected class AsynTaskLoadCountry extends AsyncTask<Void,List<Country>,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String url = "https://aud.fxexchangerate.com/rss.xml";
                InputStream inputStream = new URL(url).openConnection().getInputStream();
                mListCountry            = new XMLPullParserHandler().parseCountry(inputStream);
                publishProgress(mListCountry);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(List<Country>... values) {
            List<String> countryNames = new CountryServices(values[0]).getAllCountryName();

            // spinner original currency
            ArrayAdapter<String> adapterOriginal    = new ArrayAdapter<>(MainActivity.this,R.layout.spinner_item,countryNames);
            adapterOriginal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerOriginal.setAdapter(adapterOriginal);

            // spinner destination currency
            ArrayAdapter<String> adapterDestination = new ArrayAdapter<>(MainActivity.this,R.layout.spinner_item,countryNames);
            adapterDestination.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerDestination.setAdapter(adapterDestination);
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
            CurrencyServices services = new CurrencyServices(mListCurrency);
            Double exchangeRate       = services.getCurrency(destinationCountryName);
            Double result             = services.convertCurrency(money,exchangeRate);
            mEditTextResult.setText(String.valueOf(result));

        }

    }


    protected void loadItems(){
        new AsynTaskLoadCountry().execute();
    }

}