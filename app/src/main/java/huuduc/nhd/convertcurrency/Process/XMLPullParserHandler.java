package huuduc.nhd.convertcurrency.Process;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import huuduc.nhd.convertcurrency.Entity.Country;
import huuduc.nhd.convertcurrency.Entity.Currency;

public class XMLPullParserHandler {
    private List<Country>  listCountry  = new ArrayList<>();
    private List<Currency> listCurrency = new ArrayList<>();
    private boolean isItem;
    private String  description = "";
    private String  text        = "";
    private String  title       = "";


    public List<Country> parseCountry(InputStream inputStream){
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream,null);

            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG: {
                        if(tagName.equalsIgnoreCase("item")){
                            isItem = true;
                        }
                        break;
                    }
                    case XmlPullParser.TEXT:{
                        text = parser.getText();
                        break;
                    }
                    case XmlPullParser.END_TAG:{
                        if(tagName.equalsIgnoreCase("title")){
                            title = text;
                        }else if(tagName.equalsIgnoreCase("item")){
                            isItem = false;
                        }

                        if(!title.equals("")){
                            if(isItem){
                                String [] country             = title.split("/");
                                String [] countryNameAndCode  = country[1].split("\\(");
                                listCountry.add(new Country(countryNameAndCode[0],countryNameAndCode[1].substring(0,countryNameAndCode[1].length() - 1)));
                            }
                            isItem = false;
                            title  = "";
                        }
                        break;
                    }
                }
                eventType = parser.next();
            }
            return listCountry;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Currency> parseCurrency(InputStream inputStream){
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = parser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:{
                        if(tagName.equalsIgnoreCase("item")){
                            isItem = true;
                        }
                        break;
                    }
                    case XmlPullParser.TEXT:{
                        text = parser.getText();
                        break;
                    }
                    case XmlPullParser.END_TAG:{
                        if(tagName.equalsIgnoreCase("description")){
                            description = text;
                        }else if(tagName.equalsIgnoreCase("item")){
                            isItem = false;
                        }
                        if(!description.equals("")){
                            if(isItem){
                                String [] descriptions = description.split("=");
                                String [] countryNameAndCurrency = descriptions[1].trim().split("\\s", 2);
                                String countryName = countryNameAndCurrency[1];
                                Double currency    = Double.parseDouble(countryNameAndCurrency[0]);
                                listCurrency.add(new Currency(countryName,currency));
                            }
                            isItem      = false;
                            description = "";
                        }
                    }
                }
                eventType = parser.next();
            }
            return listCurrency;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
