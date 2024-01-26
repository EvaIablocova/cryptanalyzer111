package com.javarush.cryptanalyzerGirls.iablocova;

import javax.swing.*;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FilenameFilter;
import static com.javarush.cryptanalyzerGirls.iablocova.constants.Paths.input;
import static com.javarush.cryptanalyzerGirls.iablocova.constants.Paths.encoded;
import static com.javarush.cryptanalyzerGirls.iablocova.constants.Paths.output;
import static com.javarush.cryptanalyzerGirls.iablocova.constants.Paths.dictionary;
import com.javarush.cryptanalyzerGirls.iablocova.LoginWindow;
import com.javarush.cryptanalyzerGirls.iablocova.view.GUIView;
import com.javarush.cryptanalyzerGirls.iablocova.services.Encode;
import com.javarush.cryptanalyzerGirls.iablocova.services.Decode;
import com.javarush.cryptanalyzerGirls.iablocova.services.BruteForce;
import com.javarush.cryptanalyzerGirls.iablocova.services.BigramMethod;
import com.javarush.cryptanalyzerGirls.iablocova.services.StatisticalAnalysis;
import com.javarush.cryptanalyzerGirls.iablocova.services.EncodeForStatisticalAnalysis;
import com.javarush.cryptanalyzerGirls.iablocova.entity.Result;
import com.javarush.cryptanalyzerGirls.iablocova.repository.ResultCode;
import java.util.Random;
import java.io.PrintWriter;

public class Swing {
    // Объявляем переменные на уровне класса
    private static JTextField encryptField;
    private static JTextField decryptField;
    private static JTextField keyField;
    private static JTextField statusField;
    private static JTextField dictionaryField;
    private static String currentFilePath;
    private static JFrame frame;

    public Swing () {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
// Создание экземпляра GUIView
        GUIView guiView = new GUIView(encryptField, decryptField, keyField, statusField, dictionaryField, frame);
        // Инициализация компонентов UI
        encryptField = new JTextField(30);
        keyField = new JTextField(30);
        statusField = new JTextField("Ожидание операции...", 30);
        statusField.setEditable(false);
        // Основное окно приложения
        JFrame frame = new JFrame("Crypto");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 400)); // Устанавливаем предпочтительный размер
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GUIView guiView = new GUIView(encryptField, decryptField, keyField, statusField, dictionaryField, frame);
                if (guiView.repeat()) {
                    // Не закрываем окно, если пользователь хочет повторить операцию
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    // Пользователь хочет повторить операцию
                    // Можно вызвать метод для повтора операции или просто ничего не делать
                } else {
                    // Пользователь хочет закрыть приложение
                    //System.exit(0); // Закрываем приложение
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });
        //String defaultFilePath = loadDefaultFilePath();
        // if (!defaultFilePath.isEmpty()) {
        //encryptField.setText(defaultFilePath);
        // }
        // Отображение окна входа перед основным окном
        LoginWindow loginWindow = new LoginWindow(frame);
        loginWindow.setVisible(true);
        // Основная панель
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1)); // Используем GridLayout для упорядочивания элементов
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Добавляем отступы
        panel.setBackground(Color.DARK_GRAY);


        // Панели для разделения функций
        JPanel filePanel = createFileEncryptionPanel();
        JPanel dfilePanel = createFileDecryptionPanel();
        JPanel dictionaryPanel = createDictionaryPanel();
        JPanel keyPanel = createKeyPanel();
        JPanel buttonPanel = createButtonPanel(frame);
        JPanel statusPanel = createStatusPanel();

        // Добавляем панели к основной панели
        panel.add(filePanel);
        panel.add(dfilePanel);
        panel.add(dictionaryPanel);
        panel.add(keyPanel);
        panel.add(buttonPanel);
        panel.add(statusPanel);

        // Добавляем основную панель к фрейму
        frame.add(panel);
        frame.pack(); // Упаковываем и устанавливаем все размеры
        frame.setLocationRelativeTo(null); // Центрируем окно
        frame.setVisible(true);
    }


    private static JPanel createFileEncryptionPanel() {
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.add(new JLabel("Путь файла для шифрования:"));

        encryptField = new JTextField(30);
        filePanel.add(encryptField);

        JButton fileChooseButton = new JButton("Выбрать файл");
        filePanel.add(fileChooseButton);

        filePanel.setBackground(Color.darkGray);
        fileChooseButton.setBackground(Color.lightGray);

        // Обработчик для кнопки выбора файла
        fileChooseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(".")); // открыть диалог в текущей директории
            int result = fileChooser.showOpenDialog(null); // показать диалог выбора файла
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                encryptField.setText(selectedFile.getAbsolutePath());
            } else {
                encryptField.setText(input); // использовать путь по умолчанию, если файл не выбран
            }
        });


        return filePanel;
    }

    private static JPanel createFileDecryptionPanel() {
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.add(new JLabel("Путь Файла для дешифрования:"));

        decryptField = new JTextField(30);
        filePanel.add(decryptField);

        JButton fileChooseButton = new JButton("Выбрать файл");
        filePanel.add(fileChooseButton);

        filePanel.setBackground(Color.darkGray);
        fileChooseButton.setBackground(Color.lightGray);

        // Обработчик для кнопки выбора файла
        fileChooseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(".")); // открыть диалог в текущей директории
            int result = fileChooser.showOpenDialog(null); // показать диалог выбора файла
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                decryptField.setText(selectedFile.getAbsolutePath());
            } else {
                decryptField.setText(input); // использовать путь по умолчанию, если файл не выбран
            }
        });
        File currentDirectory = new File(".");
        System.out.println("Текущий путь: " + currentDirectory.getAbsolutePath());

        File inputFile = new File(currentDirectory, encoded);
        if (inputFile.exists() && !inputFile.isDirectory()) {
            System.out.println("Файл encoded.txt найден.");
        } else {
            System.out.println("Файл encoded.txt не найден в текущей директории.");
        }

        return filePanel;
    }

    private static JPanel createDictionaryPanel() {
        // Панель для выбора файла словаря
        JPanel dictionaryPanel = new JPanel();
        dictionaryPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        dictionaryPanel.add(new JLabel("Файл словаря:"));
        dictionaryField = new JTextField(30);
        dictionaryPanel.add(dictionaryField);

        JButton dictionaryChooseButton = new JButton("Выбрать файл");
        dictionaryChooseButton.setToolTipText("Выберите файл словаря или используйте файл по умолчанию (dictionary.txt)");
        dictionaryPanel.add(dictionaryChooseButton);

        dictionaryPanel.setBackground(Color.darkGray);
        dictionaryChooseButton.setBackground(Color.darkGray);

        // Слушатель для кнопки выбора словаря
        dictionaryChooseButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Выбрать файл из папки или использовать файл по умолчанию (dictionary.txt)?",
                    "Выбор файла словаря",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("."));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    dictionaryField.setText(selectedFile.getAbsolutePath());
                }
            } else {
                dictionaryField.setText("dictionary.txt"); // Устанавливаем значение по умолчанию
            }
        });
        File currentDirectory = new File(".");
        System.out.println("Текущий путь: " + currentDirectory.getAbsolutePath());

        File inputFile = new File(currentDirectory, "dictionary.txt");
        if (inputFile.exists() && !inputFile.isDirectory()) {
            System.out.println("Файл dictionary найден.");
        } else {
            System.out.println("Файл dictionary не найден в текущей директории.");
        }
        return dictionaryPanel;
    }

    private static JPanel createKeyPanel() {
        JPanel keyPanel = new JPanel();
        keyPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        keyPanel.add(new JLabel("Ключ:"));

        keyField = new JTextField(30);
        keyPanel.add(keyField);

        JButton setKeyButton = new JButton("Введите ключ");
        setKeyButton.addActionListener(e -> {
            Object[] options = {"Ввести вручную", "Сгенерировать случайный"};
            int choice = JOptionPane.showOptionDialog(null,
                    "Как вы хотите установить ключ?",
                    "Выбор ключа",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == JOptionPane.YES_OPTION) {
                // Ввести вручную
                String userInput = JOptionPane.showInputDialog("Введите ключ:");
                if (userInput != null && !userInput.trim().isEmpty()) {
                    keyField.setText(userInput.trim());
                }
            } else if (choice == JOptionPane.NO_OPTION) {
                // Сгенерировать случайный
                Random random = new Random();
                int randomKey = random.nextInt(50); // Генерация случайного числа
                keyField.setText(String.valueOf(randomKey));
            }
        });

        keyPanel.add(setKeyButton);
        keyPanel.setBackground(Color.darkGray);
        setKeyButton.setBackground(Color.lightGray);

        return keyPanel;
    }

    private static String chooseFile(JFrame frame, String defaultFilePath) {
        Object[] options = {"Использовать файл по умолчанию", "Выбрать другой файл"};
        int choice = JOptionPane.showOptionDialog(frame,
                "Выберите файл для дешифрования:",
                "Выбор файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.YES_OPTION) {
            // Пользователь выбрал файл по умолчанию
            return defaultFilePath;
        } else if (choice == JOptionPane.NO_OPTION) {
            // Пользователь выбрал другой файл
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                return selectedFile.getAbsolutePath();
            }
        }
        return null; // Если пользователь закрыл окно или выбор не был сделан
    }

    private static String currentMode = "none";

    private static JPanel createButtonPanel(JFrame frame) {

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.darkGray);

        JButton encodeButton = new JButton("Encode");
        encodeButton.addActionListener(e -> {
            currentMode = "1"; // Устанавливаем режим "шифрование"
            performEncryption(frame);
        });
        buttonPanel.add(encodeButton);

        JButton decodeButton = new JButton("Decode");
        decodeButton.addActionListener(e -> {
            currentMode = "2"; // Устанавливаем режим "дешифрование"
            performDecryption(frame);
        });
        buttonPanel.add(decodeButton);

        JButton bfButton = new JButton("BruteForce");
        bfButton.addActionListener(e -> {
            currentMode = "3"; // Устанавливаем режим "BruteForce"
            performBruteForce(frame);
        });
        buttonPanel.add(bfButton);

        JButton BimButton = new JButton("BigramMethod");
        BimButton.addActionListener(e -> {
            currentMode = "6"; // Устанавливаем режим "BigramMethod"
            performBigram(frame);
        });

        buttonPanel.add(BimButton);

        JButton EnSButton = new JButton("EncodeForStatisticalAnalysis ");
        EnSButton.addActionListener(e -> {
            currentMode = "4"; // Устанавливаем режим "BigramMethod"
            performEncodeStatic(frame);
        });
        buttonPanel.add(EnSButton);

        JButton buttonAnalyze = new JButton("StatisticalAnalysis");


        buttonPanel.add(buttonAnalyze);
        // Устанавливаем равномерное распределение пространства
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.darkGray);
        buttonAnalyze.addActionListener(e -> {
            currentMode = "5"; // Устанавливаем режим "Статистический Анализ"
            performLetterSubstitution(frame);
        });
        buttonPanel.add(buttonAnalyze);

        return buttonPanel;
    }

    // Метод для шифрования файла
    private static void performEncryption(JFrame frame) {
        // Получаем путь к файлу из глобальной переменной
        String filePath = encryptField.getText();

        // Подтверждаем выбранный файл или предлагаем использовать файл по умолчанию

        if (filePath == null || filePath.isEmpty()) {
            int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                    "Путь файла для шифрования не выбран! Использовать файл по умолчанию?",
                    "Ошибка",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            if (confirmFileChoice == JOptionPane.NO_OPTION) {
                // Обработка случая, когда выбрано "No" для использования файла по умолчанию
                // Ваш код здесь
            } else {
                filePath = input;
            }
        } else {
            int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                    "Путь файла для шифрования: " + filePath + "\nИспользовать этот файл?(No- файл по умолчанию)",
                    "Подтверждение файла",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmFileChoice == JOptionPane.NO_OPTION) {
                // Обработка случая, когда выбрано "No" для использования файла по умолчанию
                filePath = input;
            } else {
                // Обработка случая, когда выбрано "Yes" для использования выбранного файла
            }
        }





        // Запрашиваем пользователя о пути к файлу для сохранения результата
        String key = keyField.getText(); // Получаем ключ
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Введите ключ!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Object[] options = {"Выбрать файл из папки", "Использовать путь по умолчанию (encoded.txt)"};
        int saveChoice = JOptionPane.showOptionDialog(frame,
                "Как сохранить зашифрованный файл?",
                "Сохранение файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        String targetFilePath;
        if (saveChoice == JOptionPane.YES_OPTION) {
            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setCurrentDirectory(new File("."));
            fileSaver.setDialogTitle("Выберите место для сохранения файла");
            int userSelection = fileSaver.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileSaver.getSelectedFile();
                targetFilePath = fileToSave.getAbsolutePath();
            } else {
                // Если пользователь отменил выбор, прекращаем операцию
                statusField.setText("Операция отменена пользователем.");
                return;
            }
        } else {
            targetFilePath = encoded; // Путь по умолчанию
        }


        // Создаем массив параметров для передачи в метод execute класса Encode
        String[] parameters = {null, filePath, key, targetFilePath};

        // Создаем экземпляр класса Encode
        Encode encodeService = new Encode();
        // Вызываем метод execute и получаем результат
        Result result = encodeService.execute(parameters);

        // Выводим результат в текстовое поле статуса
        if (result.getResultCode() == ResultCode.OK) {
            currentFilePath = targetFilePath;
            statusField.setText("Файл успешно зашифрован. Сохранен в: " + targetFilePath);
            //saveDefaultFilePath(filePath); // Сохраняем путь к файлу шифрования по умолчанию
        } else {
            statusField.setText("Ошибка при шифровании: " + result.getErrorMessage());
        }
//        openTextFile(filePath);
    }

    // Метод для дешифрования файла
    private static void performDecryption(JFrame frame) {
        //String defaultFilePath = loadDefaultFilePath();
        String filePath = decryptField.getText();
        /*
        if (filePath == null || filePath.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Файл для дешифрования не выбран!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
// Подтверждаем выбранный файл или предлагаем использовать файл по умолчанию
        int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                "Путь файла для дешифрования: " + filePath + "\nИспользовать этот файл?(No- файл по умолчанию)",
                "Подтверждение файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmFileChoice != JOptionPane.YES_OPTION) {
            // Если пользователь не подтвердил файл, используем файл по умолчанию
            filePath = "encoded.txt"; // Путь по умолчанию для файла шифрования
        }
*/
        if (filePath == null || filePath.isEmpty()) {
            int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                    "Путь файла для дешифрования не выбран! Использовать файл по умолчанию?",
                    "Ошибка",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            if (confirmFileChoice == JOptionPane.NO_OPTION) {
                // Обработка случая, когда выбрано "No" для использования файла по умолчанию
                // Ваш код здесь
            } else {
                filePath = encoded;
            }
        } else {
            int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                    "Путь файла для дешифрования: " + filePath + "\nИспользовать этот файл?(No- файл по умолчанию)",
                    "Подтверждение файла",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmFileChoice == JOptionPane.NO_OPTION) {
                // Обработка случая, когда выбрано "No" для использования файла по умолчанию
                filePath = encoded;
            } else {
                // Обработка случая, когда выбрано "Yes" для использования выбранного файла
            }
        }

        // Получаем ключ, введенный пользователем
        String key = keyField.getText();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Введите ключ!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Object[] options = {"Выбрать файл из папки", "Использовать путь по умолчанию (output.txt)"};
        int saveChoice = JOptionPane.showOptionDialog(frame,
                "Как сохранить зашифрованный файл?",
                "Сохранение файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        String targetFilePath;
        if (saveChoice == JOptionPane.YES_OPTION) {
            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setCurrentDirectory(new File("."));
            fileSaver.setDialogTitle("Выберите место для сохранения файла");
            int userSelection = fileSaver.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileSaver.getSelectedFile();
                targetFilePath = fileToSave.getAbsolutePath();
            } else {
                // Если пользователь отменил выбор, прекращаем операцию
                statusField.setText("Операция отменена пользователем.");
                return;
            }
        } else {
            targetFilePath = output; // Путь по умолчанию
        }

        Decode decodeService = new Decode();
        String[] parameters = {null, filePath, key, targetFilePath};
        Result result = decodeService.execute(parameters);

        if (result.getResultCode() == ResultCode.OK) {
            currentFilePath = targetFilePath;
            statusField.setText("Файл успешно дешифрован. Сохранен в: " + targetFilePath);
        } else {
            statusField.setText("Ошибка при дешифровании: " + result.getErrorMessage());
        }

        // Спрашиваем пользователя,         хочет ли он открыть дешифрованный файл
        //openTextFile(filePath);
    }

    // Метод для brute_force файла
    private static void performBruteForce(JFrame frame) {
        String filePath = decryptField.getText();
/*
        if (filePath == null || filePath.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Файл для brute force анализа не выбран!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                "Путь файла для дешифрования (BruteForce): " + filePath + "\nИспользовать этот файл?(No- файл по умолчанию)",
                "Подтверждение файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmFileChoice != JOptionPane.YES_OPTION) {
            // Если пользователь не подтвердил файл, используем файл по умолчанию
            filePath = "encoded.txt"; // Путь по умолчанию для файла шифрования
        }
*/
        if (filePath == null || filePath.isEmpty()) {
            int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                    "Путь файла для дешифрования (BruteForce) не выбран! Использовать файл по умолчанию?",
                    "Ошибка",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            if (confirmFileChoice == JOptionPane.NO_OPTION) {
                // Обработка случая, когда выбрано "No" для использования файла по умолчанию
                // Ваш код здесь
            } else {
                filePath = encoded;
            }
        } else {
            int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                    "Путь файла для дешифрования (BruteForce): " + filePath + "\nИспользовать этот файл?(No- файл по умолчанию)",
                    "Подтверждение файла",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmFileChoice == JOptionPane.NO_OPTION) {
                // Обработка случая, когда выбрано "No" для использования файла по умолчанию
                filePath = encoded;
            } else {
                // Обработка случая, когда выбрано "Yes" для использования выбранного файла
            }
        }
        // Получаем ключ, введенный пользователем
        String key = keyField.getText();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Введите ключ!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Object[] options = {"Выбрать файл из папки", "Использовать путь по умолчанию (output.txt)"};
        int saveChoice = JOptionPane.showOptionDialog(frame,
                "Как сохранить зашифрованный файл?",
                "Сохранение файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        String targetFilePath;
        if (saveChoice == JOptionPane.YES_OPTION) {
            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setCurrentDirectory(new File("."));
            fileSaver.setDialogTitle("Выберите место для сохранения файла");
            int userSelection = fileSaver.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileSaver.getSelectedFile();
                targetFilePath = fileToSave.getAbsolutePath();
            } else {
                // Если пользователь отменил выбор, прекращаем операцию
                statusField.setText("Операция отменена пользователем.");
                return;
            }
        } else {
            targetFilePath = output; // Путь по умолчанию
        }
        BruteForce bruteForceService = new BruteForce();
        String[] parameters = {null, filePath, key, targetFilePath};
        Result result = bruteForceService.execute(parameters);

        if (result != null) {
            if (result.getResultCode() == ResultCode.OK) {
                currentFilePath = targetFilePath;
                statusField.setText("Файл дешифрован (BruteForce). Сохранен в: " + targetFilePath);
            } else {
                statusField.setText("Ошибка при BruteForce анализе: " + result.getErrorMessage());
            }
        } else {
            statusField.setText("Ошибка: результат выполнения BruteForce не получен (результат null).");
        }

        // Ask the user if they want to view the decrypted file
        //openTextFile(filePath);
    }



    private static void performBigram(JFrame frame) {
        String filePath = decryptField.getText().isEmpty() ? encoded : decryptField.getText();
        //String filePath = decryptField.getText();
        if (filePath == null || filePath.isEmpty()) {

            int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                    "Путь файла для дешифрования (BigramMethod): " + filePath + "\nИспользовать этот файл?(No- файл по умолчанию)",
                    "Подтверждение файла",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmFileChoice == JOptionPane.NO_OPTION) {
                // Обработка случая, когда выбрано "No" для использования файла по умолчанию
                filePath = encoded;
            } else {
                // Обработка случая, когда выбрано "Yes" для использования выбранного файла
            }
        }

        // Получаем ключ, введенный пользователем
        String key = keyField.getText();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Введите ключ!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Object[] options = {"Выбрать файл из папки", "Использовать путь по умолчанию (output.txt)"};
        int saveChoice = JOptionPane.showOptionDialog(frame,
                "Как сохранить зашифрованный файл?",
                "Сохранение файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        String targetFilePath;
        if (saveChoice == JOptionPane.YES_OPTION) {
            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setCurrentDirectory(new File("."));
            fileSaver.setDialogTitle("Выберите место для сохранения файла");
            int userSelection = fileSaver.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileSaver.getSelectedFile();
                targetFilePath = fileToSave.getAbsolutePath();
            } else {
                // Если пользователь отменил выбор, прекращаем операцию
                statusField.setText("Операция отменена пользователем.");
                return;
            }
        } else {
            targetFilePath = output; // Путь по умолчанию
        }
        String bigrammetPath = askForBigrammetPath(frame);
        if (bigrammetPath == null || bigrammetPath.isEmpty()) {
            statusField.setText("Операция отменена: не указан путь для сохранения всех вариантов дешифровки.");
            return;
        }

        String[] parameters = {key, filePath, bigrammetPath, targetFilePath};
        List<String> decryptedTexts = new BigramMethod().decodeAllVariants(parameters);

        if (decryptedTexts.isEmpty()) {
            statusField.setText("Дешифровка не удалась");
            return;
        }
        String  keyVariant = displayDecryptionVariants(frame, decryptedTexts);
        // Вызов метода decodeTextWithKey для дальнейшей обработки расшифрованного текста
        Result result = decodeTextWithKey(filePath, targetFilePath, keyVariant);
        if (result != null) {
            if (result.getResultCode() == ResultCode.OK) {
                currentFilePath = targetFilePath;
                statusField.setText("Файл дешифрован (BigramMethod). Сохранен в: " + targetFilePath);
            } else {
                statusField.setText("Ошибка при BigramMethod анализе: " + result.getErrorMessage());
            }
        } else {
            statusField.setText("Ошибка: результат выполнения BigramMethod не получен (результат null).");
        }
    }

    private static String displayDecryptionVariants(JFrame frame, List<String> decryptedTexts) {
        final String[] chosenVariantNumber = {null}; // Массив для хранения номера выбранного варианта

        // Создаем диалоговое окно
        JDialog dialog = new JDialog(frame, "Выбор варианта дешифровки", true);
        dialog.setLayout(new BorderLayout());


        // Создаем текстовую область для отображения всех вариантов дешифровки
        JTextArea textArea = new JTextArea();
        for (int i = 0; i < decryptedTexts.size(); i++) {
            textArea.append("Вариант " + i + ":\n" + decryptedTexts.get(i) + "\n\n");
        }
        textArea.setEditable(false); // Делаем текстовую область только для чтения
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Создаем текстовое поле для ввода номера выбранного варианта
        JTextField variantField = new JTextField(10);

        // Создаем кнопку для подтверждения выбора
        JButton okButton = new JButton("OK");

        // Изменяем обработчик кнопки OK
        okButton.addActionListener(e -> {
            int selectedVariant;
            try {
                selectedVariant = Integer.parseInt(variantField.getText());
                if (selectedVariant >= 0 && selectedVariant < decryptedTexts.size()) {
                    chosenVariantNumber[0] = Integer.toString(selectedVariant);
                    dialog.dispose(); // Закрываем диалоговое окно
                } else {
                    JOptionPane.showMessageDialog(dialog, "Введите корректный номер варианта!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Введите корректный номер варианта!");
            }
        });

        // Добавляем компоненты к диалоговому окну
        dialog.add(scrollPane, BorderLayout.CENTER);
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Введите номер варианта:"));
        inputPanel.add(variantField);
        inputPanel.add(okButton);
        dialog.add(inputPanel, BorderLayout.SOUTH);

        // Настройка и отображение диалогового окна
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(frame);

        dialog.setVisible(true);
        return chosenVariantNumber[0];
    }


    private static String askForBigrammetPath(JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setDialogTitle("Выберите место для сохранения всех вариантов дешифровки");
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            return fileToSave.getAbsolutePath();
        } else {
            return null; // Пользователь отменил выбор
        }
    }
    private static void displayDecryptionOptions(JFrame frame, String bigrammetPath, String outputFilePath) {
        // Читаем текст из файла bigrammet
        String bigrammetText = readFile(bigrammetPath);
        if (bigrammetText == null || bigrammetText.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Не удалось прочитать файл с вариантами дешифровки.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Создаем текстовую область для отображения текста
        JTextArea textArea = new JTextArea(bigrammetText);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Создаем и добавляем кнопку для выбора варианта дешифровки
        JButton selectButton = new JButton("Выбрать и сохранить");
        selectButton.addActionListener(e -> {
            String selectedText = textArea.getSelectedText();
            if (selectedText != null && !selectedText.isEmpty()) {
                saveToFile(selectedText, outputFilePath);
                JOptionPane.showMessageDialog(frame, "Выбранный вариант сохранен в " + outputFilePath);
            } else {
                JOptionPane.showMessageDialog(frame, "Пожалуйста, выберите текст для сохранения.", "Предупреждение", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Добавляем текстовую область и кнопку на панель
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(selectButton, BorderLayout.SOUTH);

        // Добавляем панель в окно
        frame.getContentPane().removeAll();
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private static void saveToFile(String text, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static Result decodeTextWithKey(String filePath, String outputFilePath, String key) {
        BigramMethod bigramMethodService = new BigramMethod();
        String[] parameters = {"6", filePath, key,  outputFilePath};

        return new Decode().execute(parameters);
    }




    private static void performEncodeStatic(JFrame frame) {
        // Получаем путь к файлу из глобальной переменной
        String filePath = encryptField.getText();
        if (filePath == null || filePath.isEmpty()) {
            int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                    "Путь файла для шифрования (StaticalAnalysis) не выбран! Использовать файл по умолчанию?",
                    "Ошибка",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);

            if (confirmFileChoice == JOptionPane.NO_OPTION) {
                // Обработка случая, когда выбрано "No" для использования файла по умолчанию
                // Ваш код здесь
            } else {
                filePath = input;
            }
        } else {
            int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                    "Путь файла для шифрования (StaticalAnalysis) : " + filePath + "\nИспользовать этот файл?(No- файл по умолчанию)",
                    "Подтверждение файла",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmFileChoice == JOptionPane.NO_OPTION) {
                // Обработка случая, когда выбрано "No" для использования файла по умолчанию
                filePath = input;
            } else {
                // Обработка случая, когда выбрано "Yes" для использования выбранного файла
            }
        }

        String key = keyField.getText(); // Получаем ключ
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Введите ключ!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object[] options = {"Выбрать файл из папки", "Использовать путь по умолчанию (encoded.txt)"};
        int saveChoice = JOptionPane.showOptionDialog(frame,
                "Как сохранить зашифрованный файл?",
                "Сохранение файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        String targetFilePath;
        if (saveChoice == JOptionPane.YES_OPTION) {
            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setCurrentDirectory(new File("."));
            fileSaver.setDialogTitle("Выберите место для сохранения файла");
            int userSelection = fileSaver.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileSaver.getSelectedFile();
                targetFilePath = fileToSave.getAbsolutePath();
            } else {
                // Если пользователь отменил выбор, прекращаем операцию
                statusField.setText("Операция отменена пользователем.");
                return;
            }
        } else {
            targetFilePath = encoded; // Путь по умолчанию
        }


        // Создаем массив параметров для передачи в метод execute класса Encode
        String[] parameters = {null, filePath, key, targetFilePath};

        // Создаем экземпляр класса Encode
        EncodeForStatisticalAnalysis encodeService = new EncodeForStatisticalAnalysis();
        // Вызываем метод execute и получаем результат
        Result result = encodeService.execute(parameters);

        // Выводим результат в текстовое поле статуса
        if (result.getResultCode() == ResultCode.OK) {
            currentFilePath = targetFilePath;
            statusField.setText("Файл успешно зашифрован (StaticalAnalysis) . Сохранен в: " + targetFilePath);
            //saveDefaultFilePath(filePath); // Сохраняем путь к файлу шифрования по умолчанию
        } else {
            statusField.setText("Ошибка при шифровании (StaticalAnalysis) : " + result.getErrorMessage());
        }
        //openTextFile(filePath);

    }
    private static void performLetterSubstitution(JFrame frame) {
        String filePath = encoded;

// Получаем ключ, введенный пользователем
        String dir = dictionaryField.getText();
        String dictionaryFilePath;

        if (dir.isEmpty()) {
            int confirmDictionaryChoice = JOptionPane.showConfirmDialog(frame,
                    "Путь к словарю не указан. Использовать словарь по умолчанию?",
                    "Выбор словаря",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmDictionaryChoice == JOptionPane.NO_OPTION) {
                // Обработка случая, когда выбрано "No" для использования словаря по умолчанию
                JOptionPane.showMessageDialog(frame, "Введите путь к словарю!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                dictionaryFilePath = dictionary; // Путь к словарю по умолчанию
            }
        } else {
            dictionaryFilePath = dir; // Используем указанный путь к словарю
        }
        Object[] options = {"Выбрать файл из папки", "Использовать путь по умолчанию (output.txt)"};
        int saveChoice = JOptionPane.showOptionDialog(frame,
                "Как сохранить зашифрованный файл?",
                "Сохранение файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        String targetFilePath;
        if (saveChoice == JOptionPane.YES_OPTION) {
            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setCurrentDirectory(new File("."));
            fileSaver.setDialogTitle("Выберите место для сохранения файла");
            int userSelection = fileSaver.showSaveDialog(frame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileSaver.getSelectedFile();
                targetFilePath = fileToSave.getAbsolutePath();
            } else {
                // Если пользователь отменил выбор, прекращаем операцию
                statusField.setText("Операция отменена пользователем.");
                return;
            }
        } else {
            targetFilePath = output; // Путь по умолчанию
        }


        StatisticalAnalysis statistocalanalysisService = new StatisticalAnalysis();
        String[] parameters = {"5", filePath, dictionaryFilePath, targetFilePath};
        Result result = statistocalanalysisService.execute(parameters);

        if (result != null) {
            if (result.getResultCode() == ResultCode.OK) {
                currentFilePath = targetFilePath;
                statusField.setText("Файл дешифрован (StatisticalAnalysis). Сохранен в: " + targetFilePath);
            } else {
                statusField.setText("Ошибка при StatisticalAnalysis: " + result.getErrorMessage());
            }
        } else {
            statusField.setText("Ошибка: результат выполнения StatisticalAnalysis не получен (результат null).");
        }
        if (result != null && result.getResultCode() == ResultCode.OK) {
            // Чтение дешифрованного текста из файла
            final String decryptedText = readFile(targetFilePath); // Используем final

            // Создаем текстовую область для отображения и редактирования текста
            JTextArea textArea = new JTextArea(decryptedText);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            // Создаем текстовые поля для ввода заменяемой и заменяющей букв
            JTextField sourceLetterField = new JTextField(10);
            JTextField targetLetterField = new JTextField(10);

            // Создаем кнопку для применения замен
            JButton applyChangesButton = new JButton("Применить изменения");
            applyChangesButton.addActionListener(e -> {
                String sourceLetter = sourceLetterField.getText();
                String targetLetter = targetLetterField.getText();
                if (sourceLetter.length() == 1 && targetLetter.length() == 1) {
                    // Заменяем буквы в тексте
                    String updatedText = textArea.getText().replace(sourceLetter.charAt(0), targetLetter.charAt(0));
                    textArea.setText(updatedText);
                } else {
                    JOptionPane.showMessageDialog(frame, "Введите одну букву для замены и одну букву для заменителя.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });
// Создаем панель для размещения элементов управления.
            JPanel controlPanel = new JPanel();
            controlPanel.add(new JLabel("Заменить:"));
            controlPanel.add(sourceLetterField);
            controlPanel.add(new JLabel("На:"));
            controlPanel.add(targetLetterField);
            controlPanel.add(applyChangesButton);

            JButton viewOriginalFileButton = new JButton("Посмотреть изначальный файл");
            controlPanel.add(viewOriginalFileButton);

            viewOriginalFileButton.addActionListener(e -> {
                // Чтение текста из файла "input.txt"
                String originalText = readFile(input); // Убедитесь, что метод readFile корректно реализован

                // Создаем текстовую область для отображения оригинального текста
                JTextArea originalTextArea = new JTextArea(originalText);
                originalTextArea.setLineWrap(true);
                originalTextArea.setWrapStyleWord(true);
                originalTextArea.setEditable(false);

                // Создаем панель прокрутки для текстовой области
                JScrollPane originalScrollPane = new JScrollPane(originalTextArea);

                // Создаем и отображаем диалоговое окно с текстовой областью
                JDialog originalTextDialog = new JDialog(frame, "Изначальный текст", true);
                originalTextDialog.setLayout(new BorderLayout());
                originalTextDialog.add(originalScrollPane, BorderLayout.CENTER);
                originalTextDialog.setSize(600, 400);
                originalTextDialog.setLocationRelativeTo(frame);
                originalTextDialog.setVisible(true);
            });

// Создаем панель прокрутки и добавляем в нее текстовую область.
            JScrollPane scrollPane = new JScrollPane(textArea);

// Создаем и отображаем диалоговое окно с текстовой областью и панелью управления.
            JDialog dialog = new JDialog(frame, "Редактирование текста", true);
            dialog.setLayout(new BorderLayout());
            dialog.add(scrollPane, BorderLayout.CENTER);
            dialog.add(controlPanel, BorderLayout.SOUTH);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);

// Сохраняем измененный текст в файл после закрытия диалога.
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFilePath))) {
                writer.write(textArea.getText());
            } catch (IOException ex) {
                ex.printStackTrace();
            }




        }

    }







    // Дополнительный метод для чтения содержимого файла
    private static String readFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            return contentBuilder.toString();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка при чтении файла: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }



    // Функция для открытия  файла
    private static void openTextFile(String filePath) {
        // Создаем окно для отображения содержимого файла
        JFrame fileFrame = new JFrame("File Contents");
        fileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fileFrame.setPreferredSize(new Dimension(500, 300));
        fileFrame.setLayout(new BorderLayout());

        // Создаем компонент для отображения текста
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false); // делаем текстовую область только для чтения
        textArea.setLineWrap(true); // Включаем перенос строк
        textArea.setWrapStyleWord(true); // Включаем перенос слов
        // Загружаем содержимое файла в текстовую область
        try {
            textArea.read(new FileReader(filePath), null);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка при открытии файла: " + e.getMessage());
            return;
        }

        // Создаем JScrollPane с вертикальной полосой прокрутки
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Включаем вертикальную полосу прокрутки
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Отключаем горизонтальную полосу прокрутки

        fileFrame.add(scrollPane, BorderLayout.CENTER);

        // Показываем окно
        fileFrame.pack();
        fileFrame.setLocationRelativeTo(null); // для центрирования окна
        fileFrame.setVisible(true);
    }



    private static JPanel createStatusPanel() {
        // Панель статуса операции
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Статус операции:"));
        statusPanel.setBackground(Color.darkGray);
        statusField = new JTextField("Ожидание операции...", 45);
        statusField.setEditable(false);
        statusPanel.add(statusField);
        //statusField.setBackground(Color.darkGray);

        JButton openFileButton = new JButton("Открыть файл");


        statusPanel.add(openFileButton);
        openFileButton.addActionListener(e -> {
            if (currentFilePath != null && !currentFilePath.isEmpty()) {
                // Открываем файл с использованием пути, сохраненного в currentFilePath
                openTextFile(currentFilePath);
            } else {
                JOptionPane.showMessageDialog(frame, "Файл для открытия не указан!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });



        return statusPanel;


    }
    /*
    private static void saveDefaultFilePath(String filePath) {
        try (PrintWriter out = new PrintWriter(new FileWriter(DEFAULT_FILE_PATH))) {
            out.println(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/


}
