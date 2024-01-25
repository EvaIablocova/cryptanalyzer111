package com.javarush.cryptanalyzerGirls.iablocova.view;

import com.javarush.cryptanalyzerGirls.iablocova.entity.Result;

import java.util.Random;
import java.util.Scanner;

import static com.javarush.cryptanalyzerGirls.iablocova.constants.ApplicationCompletionConstants.*;
import static com.javarush.cryptanalyzerGirls.iablocova.constants.CryptoAlphabet.lengthOfAlphabet;
import static com.javarush.cryptanalyzerGirls.iablocova.constants.CryptoAlphabet.lengthOfAlphabetForStatisticalAnalysis;
import static com.javarush.cryptanalyzerGirls.iablocova.constants.Paths.*;


public class ConsoleView implements View {

    @Override
    public String[] getParameters() {
        int k = 4;
        String[] parameters = new String[k];
        boolean t = true;
        Scanner scanner = new Scanner(System.in);
        while (t) {
            System.out.println("Выберите режим, который вы ходите использовать");
            System.out.println("1 - зашифровать файл");
            System.out.println("2 - расшифровать файл");
            System.out.println("3 - BruteForce");
            System.out.println("4 - EncodeForStatisticalAnalysis");
            System.out.println("5 - Статистический анализ");
            System.out.println("6 - Bigram Method");
            parameters[0] = scanner.nextLine();

            switch (parameters[0]) {
                case "1", "4":
                    t = false;
                    System.out.println("Введите путь файла, который вы хотите зашифровать или нажмите Enter для выбора файла по умолчанию (input.txt)");
                    parameters[1] = scanner.nextLine();
                    if (parameters[1].isEmpty()) parameters[1] = input;
                    break;
                case "2", "3", "6", "5":
                    t = false;
                    System.out.println("Введите путь файла, который вы хотите расшифровать или нажмите Enter для выбора файла по умолчанию (encoded.txt)");
                    parameters[1] = scanner.nextLine();
                    if (parameters[1].isEmpty()) parameters[1] = encoded;
                    break;
                default:
                    System.out.println("Нет такого режима. Попробуйте ещё раз");
            }
        }

        t = true;
        while (t) {
            try {
                switch (parameters[0]) {
                    case "1": //1 - зашифровать файл
                        System.out.println("Введите ключ для шифровки или нажмите Enter для случайного генерирования ключа");
                        parameters[2] = scanner.nextLine();

                        if (parameters[2].isEmpty()) {
                            Random random = new Random();
                            parameters[2] = String.valueOf(random.nextInt(lengthOfAlphabet - 1 - 0 + 1) + 0);
                            System.out.println(KEY + parameters[2]);
                        }

                        System.out.println("Выберите в какой файл записать результат или нажмите Enter для выбора файла по умолчанию (encoded.txt)");
                        parameters[3] = scanner.nextLine();
                        if (parameters[3].isEmpty()) parameters[3] = encoded;
                        break;
                    case "2"://2 - расшифровать файл
                        System.out.println("Введите ключ для расшифровки");
                        parameters[2] = scanner.nextLine();
                        System.out.println("Выберите в какой файл записать результат или нажмите Enter для выбора файла по умолчанию (output.txt)");
                        parameters[3] = scanner.nextLine();
                        if (parameters[3].isEmpty()) parameters[3] = output;
                        break;
                    case "3"://3 - BruteForce
                        parameters[2] = "0";
                        System.out.println("Выберите в какой файл записать результат или нажмите Enter для выбора файла по умолчанию (output.txt)");
                        parameters[3] = scanner.nextLine();
                        if (parameters[3].isEmpty()) parameters[3] = output;
                        break;
                    case "4": //4 - EncodeForStatisticalalysis
                        System.out.println("Введите ключ для шифровки или нажмите Enter для случайного генерирования ключа");
                        parameters[2] = scanner.nextLine();

                        if (parameters[2].isEmpty()) {
                            Random random = new Random();
                            parameters[2] = String.valueOf(random.nextInt(lengthOfAlphabetForStatisticalAnalysis - 1 - 0 + 1) + 0);
                            System.out.println(KEY + parameters[2]);
                        }
                        System.out.println("Выберите в какой файл записать результат или нажмите Enter для выбора файла по умолчанию (encoded.txt)");
                        parameters[3] = scanner.nextLine();
                        if (parameters[3].isEmpty()) parameters[3] = encoded;
                        break;
                    case "5"://5 - Статистический анализ
                        System.out.println("Укажите путь файла словаря или нажмите Enter для выбора файла по умолчанию (dictionary.txt)");
                        parameters[2] = scanner.nextLine();
                        if (parameters[2].isEmpty()) parameters[2] = dictionary;
                        System.out.println("Выберите в какой файл записать результат или нажмите Enter для выбора файла по умолчанию (output.txt)");
                        parameters[3] = scanner.nextLine();
                        if (parameters[3].isEmpty()) parameters[3] = output;
                        break;
                    case "6"://6 - Bigram Method
                        parameters[2] = bigrams;
                        System.out.println("Выберите в какой файл записать результат или нажмите Enter для выбора файла по умолчанию (output.txt)");
                        parameters[3] = scanner.nextLine();
                        if (parameters[3].isEmpty()) parameters[3] = output;
                        break;
                    default:
                        break;
                }
                t = false; //пока не введут число не закончится цикл
            } catch (NumberFormatException e) {
            }
        }
        return parameters;
    }

    @Override
    public void printResult(Result result) {
        switch (result.getResultCode()) {
            case OK -> System.out.println(SUCCESS);
            case ERROR -> System.out.println(EXCEPTION + result.getApplicationException().getMessage());
        }
    }

    @Override
    public boolean repeat() {
        boolean t = true;
        boolean re = false;
        while (t) {
            System.out.println("Ходите продолжить выполнение программы?");
            System.out.println("1 - продолжить");
            System.out.println("2 - остановить");

            int k = 0;
            Scanner scanner1 = new Scanner(System.in);
            if (scanner1.hasNextInt()) {
                k = scanner1.nextInt();
                // t1 = false;
            } else {
                System.out.println("Введено не целое число.");
            }

            switch (k) {
                case 1:
                    t = false;
                    re = true;
                    break;
                case 2:
                    t = false;
                    re = false;
                    break;
                default:
                    t = true;
            }
        }

        return re;//никогда не выполниться
    }
}