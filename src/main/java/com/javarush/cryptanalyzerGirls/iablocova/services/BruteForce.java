package com.javarush.cryptanalyzerGirls.iablocova.services;

import com.javarush.cryptanalyzerGirls.iablocova.entity.Result;
import com.javarush.cryptanalyzerGirls.iablocova.repository.ResultCode;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.javarush.cryptanalyzerGirls.iablocova.constants.CryptoAlphabet.lengthOfAlphabet;
public class BruteForce implements Function {


    @Override
    public Result execute(String[] parameters) {
//        long startTime = System.currentTimeMillis();

        //перебираем ключи, сравниваем по количеству совпавших регулярок - сохраняем вариант, где нашли совпадение с регуляркой

        int key = decodeAllVariants(parameters);
        parameters[2] = Integer.toString(key);
        new Decode().execute(parameters);

        System.out.println("Ключ шифрования = " + key);

//        long endTime = System.currentTimeMillis();
//        long elapsedTime = endTime - startTime;
//        System.out.println("Время работы алгоритма: "+ elapsedTime);

        return new Result(ResultCode.OK);
    }

    private int decodeAllVariants (String[] parameters){
        Decode decoder = new Decode();
        try{
            File fileOutput = new File (parameters[3]);
            if (!fileOutput.exists()){ fileOutput.createNewFile();}

            for (int shift = 0; shift < lengthOfAlphabet; shift++) {
                parameters [2] = Integer.toString(shift);
                decoder.execute(parameters);

                String decryptedText = readTextFromFile(fileOutput);
                if (isMatching(decryptedText)) return shift;
            }} catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean isMatching (String stringForMatches){

//        int maxLen =0; //максимальная длина совпадений

//        Pattern pattern = Pattern.compile("^[А-ЯЁ]{1}(,|[а-яё])*(\\s)*( [а-яё]+(, )?)*([\\.\\?!])$", Pattern.MULTILINE);
        Pattern pattern = Pattern.compile("^[А-ЯЁ]{1}(,|[а-яё])*(\\\\s)*( [а-яё]+,?)*([\\\\.\\\\?!])$", Pattern.MULTILINE);
        //^([А-ЯЁ]{1}(,|[а-яё]))*(\\s)*( [а-яё]+)([.?!])?$
        Matcher matcher = pattern.matcher(stringForMatches);

        if (matcher.find()) {
            return true;
        }

//        Pattern aPattern = Pattern.compile("^[А-ЯЁ]{1}(,|[а-яё])*(\\s)*( [а-яё]+,?)*([\\.\\?!])$", Pattern.MULTILINE);
//        Matcher aMatcher = aPattern.matcher(cryptString);
//
//        if (aMatcher.find()) return cryptString;

        return false;
    }

}
