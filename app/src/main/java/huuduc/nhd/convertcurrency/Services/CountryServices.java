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
            names.add(item.getCode() + "-" + item.getName());
        }
        return names;
    }

    public String getCurrencyCode(String countryNameAndCode){
        String[] tempt = countryNameAndCode.split("-");
        String countryName = tempt[1];
        for(Country item: mListCountry){
            if(item.getName().equals(countryName)){
                return item.getCode();
            }
        }
        return null;
    }

}
