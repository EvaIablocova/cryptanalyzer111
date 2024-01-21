package com.javarush.cryptanalyzerGirls.iablocova;

//package com.javarush.cryptanalyzer.iablocova;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class Swing {
//
//    public static void main (String[] args){
//
//        JFrame frame = new JFrame("FrameName");
//        JButton button = new JButton("ButtonName");
//        JTextField textField = new JTextField("TextInsideField");
//        //ScrollBar
//        //panel
//        //list
//        //label - подписать
//        //comboBox - like type of list
//        //GUI form
//
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.setLocationRelativeTo(null);
//
//        button.setBounds(40, 90, 85, 20);
//        textField.setBounds(50, 100, 200, 30);
//
//        button.setBackground(Color.ORANGE);
//
//        frame.add(button);
//        frame.add(textField);
//        frame.setSize(300,300);
//        frame.setLayout(null);
//        frame.setVisible(true);
//    }
//}

import javax.swing.*;
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
import com.javarush.cryptanalyzerGirls.iablocova.view.GUIView;
import com.javarush.cryptanalyzerGirls.iablocova.services.Encode;
import com.javarush.cryptanalyzerGirls.iablocova.services.Decode;
import com.javarush.cryptanalyzerGirls.iablocova.services.BruteForce;
import com.javarush.cryptanalyzerGirls.iablocova.services.BigramMethod;
import com.javarush.cryptanalyzerGirls.iablocova.services.StatisticalAnalysis;
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
    public static void Swing() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                GUIView guiView = new GUIView(encryptField, keyField, statusField, dictionaryField,frame);
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
    // Создание экземпляра GUIView
    GUIView guiView = new GUIView(encryptField, keyField, statusField, dictionaryField,frame);

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
                encryptField.setText("input.txt"); // использовать путь по умолчанию, если файл не выбран
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
                decryptField.setText("encoded.txt"); // использовать путь по умолчанию, если файл не выбран
            }
        });

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
        dictionaryChooseButton.setToolTipText("Выберите файл словаря, либо воспользуйтесь файлом по умолчанию (для режима анализа)");
        dictionaryPanel.add(dictionaryChooseButton);
        dictionaryPanel.setBackground(Color.darkGray);
        dictionaryChooseButton.setBackground(Color.darkGray);

        // Слушатель для кнопки выбора словаря
        dictionaryChooseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                dictionaryField.setText(selectedFile.getAbsolutePath());
            }
        });

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

    private static JPanel createButtonPanel(JFrame frame) {

        // Панель с кнопками
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.darkGray);

        JButton encodeButton = new JButton("Encode");
        encodeButton.addActionListener(e -> {
            performEncryption(frame);
        });
        buttonPanel.add(encodeButton);

        JButton decodeButton = new JButton("Decode");
        decodeButton.addActionListener(e -> {
            performDecryption(frame);
        });
        buttonPanel.add(decodeButton);

        JButton bfButton =new JButton("BruteForce");
        bfButton.addActionListener(e -> {
            performBruteForce(frame);
        });
        buttonPanel.add(bfButton);

        JButton BimButton = new JButton("BigramMethod");
        BimButton.addActionListener(e -> {
            performBigram(frame);
        });
        buttonPanel.add(BimButton);


        JButton buttonAnalyze = new JButton("Статистический Анализ");


        buttonPanel.add(buttonAnalyze);
        // Устанавливаем равномерное распределение пространства
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.darkGray);
        buttonAnalyze.addActionListener(e -> performLetterSubstitution(frame));
        buttonPanel.add(buttonAnalyze);

        return buttonPanel;
    }
    // Метод для шифрования файла
    private static void performEncryption(JFrame frame) {
        // Получаем путь к файлу из глобальной переменной
        String filePath = encryptField.getText();

        if (filePath == null || filePath.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Файл для шифрования не выбран!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Подтверждаем выбранный файл или предлагаем использовать файл по умолчанию
        int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                "Путь файла для шифрования: " + filePath + "\nИспользовать этот файл?(No- файл по умолчанию)",
                "Подтверждение файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmFileChoice != JOptionPane.YES_OPTION) {
            // Если пользователь не подтвердил файл, используем файл по умолчанию
            filePath = "input.txt"; // Путь по умолчанию для файла шифрования
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
            targetFilePath = "encoded.txt"; // Путь по умолчанию
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
        //openTextFile(filePath);
    }

    // Метод для дешифрования файла
    private static void performDecryption(JFrame frame) {
        //String defaultFilePath = loadDefaultFilePath();
        String filePath = decryptField.getText();

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
            targetFilePath = "output.txt"; // Путь по умолчанию
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
            targetFilePath = "output.txt"; // Путь по умолчанию
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
        String filePath = decryptField.getText();

        if (filePath == null || filePath.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Файл для BigramMethod не выбран!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                "Путь файла для дешифрования (BigramMethod): " + filePath + "\nИспользовать этот файл?(No- файл по умолчанию)",
                "Подтверждение файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmFileChoice != JOptionPane.YES_OPTION) {
            // Если пользователь не подтвердил файл, используем файл по умолчанию
            filePath = "encoded.txt"; // Путь по умолчанию для файла шифрования
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
            targetFilePath = "output.txt"; // Путь по умолчанию
        }
        BigramMethod bigramMethodService = new BigramMethod();
        String[] parameters = {null, filePath, key, targetFilePath};
        Result result = bigramMethodService.execute(parameters);

        if (result != null) {
            if (result.getResultCode() == ResultCode.OK) {
                currentFilePath = targetFilePath;
                statusField.setText("Файл дешифрован (Birgam). Сохранен в: " + targetFilePath);
            } else {
                statusField.setText("Ошибка при Bigram анализе: " + result.getErrorMessage());
            }
        } else {
            statusField.setText("Ошибка: результат выполнения Bigram не получен (результат null).");
        }


        // Ask the user if they want to view the decrypted file
        //openTextFile(filePath);
    }

    private static void performLetterSubstitution(JFrame frame) {
        String filePath = decryptField.getText();

        if (filePath == null || filePath.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Файл для Статистического анализа не выбран!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirmFileChoice = JOptionPane.showConfirmDialog(frame,
                "Путь файла для дешифрования (Статистический анализ): " + filePath + "\nИспользовать этот файл?(No- файл по умолчанию)",
                "Подтверждение файла",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmFileChoice != JOptionPane.YES_OPTION) {
            // Если пользователь не подтвердил файл, используем файл по умолчанию
            filePath = "encoded.txt"; // Путь по умолчанию для файла шифрования
        }
// Получаем ключ, введенный пользователем
        String dir = dictionaryField.getText();
        if (dir.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Введите словарь!", "Ошибка", JOptionPane.ERROR_MESSAGE);
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
            targetFilePath = "output.txt"; // Путь по умолчанию
        }

        StatisticalAnalysis statistocalanalysisService = new StatisticalAnalysis();
        String[] parameters = {null, filePath, dir, targetFilePath};
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
        statusField = new JTextField("Ожидание операции...", 30);
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

        // Добавляем обратную визуальную связь для кнопок
        //openFileButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Файл открыт"));


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
