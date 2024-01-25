package com.javarush.cryptanalyzerGirls.iablocova.services;

import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.Scanner;
import com.javarush.cryptanalyzerGirls.iablocova.entity.Result;
import com.javarush.cryptanalyzerGirls.iablocova.repository.ResultCode;

public class StatisticalAnalysis implements Function {

    @Override
    public Result execute(String[] parameters) {

        try {
            File encodedFile = new File(parameters[1]);
            if (!encodedFile.exists()) {
                encodedFile.createNewFile();
            }

            File fileDictionary = new File(parameters[2]);
            if (!fileDictionary.exists()) {
                fileDictionary.createNewFile();
            }

            File decodedFile = new File(parameters[3]);
            if (!decodedFile.exists()) {
                decodedFile.createNewFile();
            }

            convertFileToLowerCase (fileDictionary, fileDictionary);

            Map<Character, Integer> encodedStatistics = countCharactersInEncodedFile(encodedFile);

            Map<Character, Integer> dictionaryStatistics = countCharactersInDictionaryFile(encodedStatistics, fileDictionary);

            makeMapsEqual(dictionaryStatistics, encodedStatistics);

            replaceCharactersFromStatistics(encodedFile, decodedFile, encodedStatistics, dictionaryStatistics);

            changeCharacters (decodedFile);

        }  catch (IOException e) {
            e.printStackTrace();
        }

        return new Result(ResultCode.OK);
    }

    //в статистическом анализе сначала надо сделать статитстику вхождения каждого символа в текст в заданном файле
    private static Map<Character, Integer> countCharactersInEncodedFile(File file) {
        // Создаем LinkedHashMap, который сохраняет порядок вставки
        Map<Character, Integer> sortedCharCountMap = new LinkedHashMap<>();

        try {
            // Создаем BufferedReader для чтения файла
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int currentChar;
            // Создаем Map для хранения статистики символов
            Map<Character, Integer> charCountMap = new HashMap<>();
            while ((currentChar = reader.read()) != -1) {
                char charValue = (char) currentChar;
                if (charValue != '\n')
                    // Обновляем статистику
                    charCountMap.put(charValue, charCountMap.getOrDefault(charValue, 0) + 1);
            }

            // Закрываем BufferedReader после использования
            reader.close();

            // Конвертируем Map в список
            List<Map.Entry<Character, Integer>> list = new ArrayList<>(charCountMap.entrySet());

            // Сортируем список по убыванию значений
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
            // Добавляем отсортированные элементы в LinkedHashMap
            for (Map.Entry<Character, Integer> entry : list) {
                sortedCharCountMap.put(entry.getKey(), entry.getValue());
            }

            // Возвращаем отсортированный Map
            return sortedCharCountMap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<Character, Integer> countCharactersInDictionaryFile(Map<Character, Integer> StatisticsInEncodedFile, File file) {
        // Создаем LinkedHashMap, который сохраняет порядок вставки
        Map<Character, Integer> sortedCharCountMap = new LinkedHashMap<>();

        try {
            // Создаем BufferedReader для чтения файла
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int currentChar;
            // Создаем Map для хранения статистики символов
            Map<Character, Integer> charCountMap = new HashMap<>();
            while ((currentChar = reader.read()) != -1) {
                char charValue = (char) currentChar;
                if (charValue != '\n' && StatisticsInEncodedFile.containsKey(charValue))
                    // Обновляем статистику
                    charCountMap.put(charValue, charCountMap.getOrDefault(charValue, 0) + 1);
            }

            // Закрываем BufferedReader после использования
            reader.close();

            // Конвертируем Map в список
            List<Map.Entry<Character, Integer>> list = new ArrayList<>(charCountMap.entrySet());

            // Сортируем список по убыванию значений
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
            // Добавляем отсортированные элементы в LinkedHashMap
            for (Map.Entry<Character, Integer> entry : list) {
                sortedCharCountMap.put(entry.getKey(), entry.getValue());
            }
//            // Выводим отсортированный список с номерами
//            for (int i = 0; i < list.size(); i++) {
//                Map.Entry<Character, Integer> entry = list.get(i);
//                System.out.println((i + 1) + ". " + entry.getKey() + " " + entry.getValue());
//            }

            // Возвращаем отсортированный Map
            return sortedCharCountMap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void makeMapsEqual(Map<Character, Integer> dictionaryStatistics, Map<Character, Integer> encodedStatistics) {

        // Создаем копии мапов
        Map<Character, Integer> commonInDictionary = new HashMap<>(dictionaryStatistics);
        Map<Character, Integer> commonInEncoded = new HashMap<>(encodedStatistics);

        // Удаляем из каждой копии элементы, которые отсутствуют в другой мапе
        commonInDictionary.keySet().retainAll(encodedStatistics.keySet());
        commonInEncoded.keySet().retainAll(dictionaryStatistics.keySet());

        // Теперь обновляем исходные мапы
        dictionaryStatistics.keySet().retainAll(commonInDictionary.keySet());
        encodedStatistics.keySet().retainAll(commonInEncoded.keySet());
        // Выводим оба мэпа
        System.out.println("Dictionary Map: ");
        int i = 1;
        for (Map.Entry<Character, Integer> entry : dictionaryStatistics.entrySet()) {
            System.out.println(i + ". " + entry.getKey() + ": " + entry.getValue());
            i++;
        }
        System.out.println("Encoded Map: ");
        int l = 1;
        for (Map.Entry<Character, Integer> entry : encodedStatistics.entrySet()) {
            System.out.println(l + ". " + entry.getKey() + ": " + entry.getValue());
            l++;
        }

    }
    private void replaceCharactersFromStatistics(File encoded, File output, Map<Character, Integer> sourceMap, Map<Character, Integer> targetMap) {
        try {

            // Чтение содержимого файла
            StringBuilder content = new StringBuilder(readTextFromFile(encoded));
            char tmp = 0;
            char sourceChar = sourceMap.keySet().iterator().next();
            char targetChar = targetMap.keySet().iterator().next();
            while (!sourceMap.isEmpty() && !targetMap.isEmpty()) {

                if (tmp == 0) {
                    for (int i = 0; i < content.length(); i++) {
                        if (content.charAt(i) == sourceChar) {
                            content.setCharAt(i, targetChar);
                        } else if (content.charAt(i) == targetChar) {
                            content.setCharAt(i, '$');
                        }
                    }
                    tmp = targetChar;
                    sourceMap.remove(sourceChar);
                    targetMap.remove(targetChar);
                } else if (tmp != 0) {
                    //находим символ tmp в статистике словаря
                    targetChar = findKeyByIndex(targetMap, findIndexByKey(sourceMap, tmp));
                    if (targetChar != 0) {
                        sourceChar = tmp;
                        for (int i = 0; i < content.length(); i++) {
                            if (content.charAt(i) == '$') {
                                content.setCharAt(i, targetChar);
                            } else if (content.charAt(i) == targetChar) {
                                content.setCharAt(i, '$');
                            }
                            tmp = targetChar;
                            sourceMap.remove(sourceChar);
                            targetMap.remove(targetChar);
                        }
                    } else {//если этого символа больше нет в статистике закодированного файла - значит мы можем просто сделать замену, не боясь потерять данные
                        for (int i = 0; i < content.length(); i++) {
                            if (content.charAt(i) == '$') {
                                content.setCharAt(i, tmp);
                            }
                        }
                        sourceMap.remove(sourceChar);
                        targetMap.remove(tmp);
                        sourceChar = sourceMap.keySet().iterator().next();
                        targetChar = targetMap.keySet().iterator().next();
                        tmp = 0;
                    }
                }

            }
            // Запись изменений обратно в файл
            rewriteTextToFile(output, content.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Находит индекс элемента по ключу
    private static int findIndexByKey(Map<Character, Integer> map, char key) {
        Set<Character> keySet = map.keySet();
        int index = 0;
        for (char mapKey : keySet) {
            if (mapKey == key) {
                return index;
            }
            index++;
        }
        return -1; // Возвращаем -1, если ключ не найден
    }

    // Находит ключ по индексу
    private static char findKeyByIndex(Map<Character, Integer> map, int index) {
        Set<Character> keySet = map.keySet();
        int currentIndex = 0;
        for (char key : keySet) {
            if (currentIndex == index) {
                return key;
            }
            currentIndex++;
        }
        return '\0'; // Возвращаем '\0', если индекс недействителен
    }

    private void swapCharactersInFile(File file, char one, char another) {
        try {
            String content = readTextFromFile(file);
            // Чтение содержимого файла
            StringBuilder newContent = new StringBuilder();
            for (char character : content.toCharArray()) {
                if (character == one) {
                    newContent.append(another);
                } else if (character == another) {
                    newContent.append(one);
                } else {
                    newContent.append(character);
                }
            }
            rewriteTextToFile(file, newContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeCharacters (File decodedFile) {
        System.out.println("Enter the characters you want to swap or enter 'exit' to quit:");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Source Character: ");
            String sourceInput = scanner.nextLine();

            if (sourceInput.equalsIgnoreCase("exit")) {
                break;
            }

            if (sourceInput.length() != 1) {
                System.out.println("Invalid input. Please enter a single character.");
                continue;
            }

            char sourceChar = sourceInput.charAt(0);

            System.out.print("Target Character: ");
            String targetInput = scanner.nextLine();

            if (targetInput.equalsIgnoreCase("exit")) {
                break;
            }
            if (targetInput.length() != 1) {
                System.out.println("Invalid input. Please enter a single character.");
                continue;
            }

            char targetChar = targetInput.charAt(0);

            swapCharactersInFile(decodedFile, sourceChar, targetChar);
            System.out.println("Characters swapped successfully.");
        }
    }
}
