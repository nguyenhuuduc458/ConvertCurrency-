package huuduc.nhd.convertcurrency.Services;

import java.util.ArrayList;
import java.util.List;

import huuduc.nhd.convertcurrency.Entity.Country;

public class CountryServices {
    private List<Country> mListCountry;

    public CountryServices(List<Country> mListCountry){
        this.mListCountry = mListCountry;
    }

    public List<String> getAllCountryName(){
        List<String> names = new ArrayList<>();
        for(Country item: mListCountry){
            names.add(item.getName());
        }
        return names;
    }

    public String getCountryCode(String countryName){
        for(Country item: mListCountry){
            if(item.getName().equals(countryName)){
                return item.getCode();
            }
        }
        return null;
    }

}
