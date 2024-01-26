package com.javarush.cryptanalyzerGirls.iablocova.services;
import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;


import com.javarush.cryptanalyzerGirls.iablocova.constants.CryptoAlphabet;
import com.javarush.cryptanalyzerGirls.iablocova.entity.Result;
import com.javarush.cryptanalyzerGirls.iablocova.repository.ResultCode;

import static com.javarush.cryptanalyzerGirls.iablocova.constants.CryptoAlphabet.lengthOfAlphabet;
import static com.javarush.cryptanalyzerGirls.iablocova.constants.Paths.bigramMetPath;
import static com.javarush.cryptanalyzerGirls.iablocova.constants.Paths.bigrams;

public class BigramMethod implements Function{
    @Override
    public Result execute(String[] parameters) {

        try {
            Map<String, Integer> bigramFrequencies = loadBigramFrequencies(parameters[2]);
            File file1 = new File(parameters[1]);
            if (!file1.exists()) {
                file1.createNewFile();
            }
            String encryptedText = readTextFromFile(file1);

            decodeAllVariants (parameters);
            parameters [2] = Integer.toString(chooseShift());
            new Decode().execute(parameters);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Result(ResultCode.OK);
    }


    // Метод для загрузки частот биграмм из файла
    private static Map<String, Integer> loadBigramFrequencies(String filePath) {
        Map<String, Integer> frequencies = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    frequencies.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return frequencies;
    }
    /*
        // Вывод всех возможных вариантов дешифровки
        public List<String> decodeAllVariants (String[] parameters){
            List<String> result = new ArrayList<>(); //ff
            Decode decoder = new Decode();
            String groupOfDecryptedText = "";
            try{
            for (int shift = 0; shift <= lengthOfAlphabet; shift++) {
                parameters [2] = Integer.toString(shift);
                decoder.execute(parameters);

                File fileOutput = new File (parameters[3]);
                if (!fileOutput.exists()){ fileOutput.createNewFile();}

                String decryptedText = readTextFromFile(fileOutput);
                groupOfDecryptedText = appendToString (groupOfDecryptedText, decryptedText, shift);
            }

                File BigramOutput = new File (bigramMetPath);
                if (!BigramOutput.exists()){ BigramOutput.createNewFile();}
                rewriteTextToFile(BigramOutput, groupOfDecryptedText);

            } catch (IOException e) {
            e.printStackTrace();
        }
            try {
                for (int shift = 0; shift <= lengthOfAlphabet; shift++) {
                    parameters[2] = Integer.toString(shift);
                    decoder.execute(parameters);

                    File fileOutput = new File(parameters[3]);
                    if (!fileOutput.exists()) {
                        System.out.println("Файл для записи не существует: " + fileOutput.getAbsolutePath());
                        fileOutput.createNewFile();
                    }

                    String decryptedText = readTextFromFile(fileOutput);
                    System.out.println("Дешифрованный текст для сдвига " + shift + ": " + decryptedText);

                    groupOfDecryptedText = appendToString(groupOfDecryptedText, decryptedText, shift);
                }

                File BigramOutput = new File(bigramMetPath);
                if (!BigramOutput.exists()) {
                    BigramOutput.createNewFile();
                }
                rewriteTextToFile(BigramOutput, groupOfDecryptedText);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;//ff

        }
    */
    public List<String> decodeAllVariants(String[] parameters) {
        List<String> result = new ArrayList<>();
        Decode decoder = new Decode();

        try {
            for (int shift = 0; shift <= lengthOfAlphabet; shift++) {
                parameters[2] = Integer.toString(shift);
                decoder.execute(parameters);

                File fileOutput = new File(parameters[3]);
                if (!fileOutput.exists()) {
                    System.out.println("Файл для записи не существует: " + fileOutput.getAbsolutePath());
                    fileOutput.createNewFile();
                }

                String decryptedText = readTextFromFile(fileOutput);
                System.out.println("Дешифрованный текст для сдвига " + shift + ": " + decryptedText);

                result.add(decryptedText); // Добавляем дешифрованный текст в список
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }



    private static String appendToString (String baseString, String whatAppend, int shift){
        baseString = baseString + "\n"+ "Дешифрованный текст " + shift + ":" + "\n"+ whatAppend;
        return baseString;
    }

    private static int chooseShift (){
        System.out.println("Выберите подходящий вариант расшифровки: ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

}






