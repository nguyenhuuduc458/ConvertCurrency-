package huuduc.nhd.convertcurrency.Services;

import android.util.Log;

import java.util.List;

import huuduc.nhd.convertcurrency.Entity.Currency;

public class CurrencyServices {
    private List<Currency> mListCurrency;

    public CurrencyServices(List<Currency> mListCurrency){
        this.mListCurrency = mListCurrency;
    }

    public Double getCurrency(String countryName){
       for(int i = 0 ;i < mListCurrency.size(); i++){
           if(mListCurrency.get(i).getCountryName().equalsIgnoreCase(countryName)){
               return mListCurrency.get(i).getCurrency();
           }
       }
       return 0.0;
    }

    public Double convertCurrency(Double currency,Double exchangeRate){
        return currency * exchangeRate;
    }
}
