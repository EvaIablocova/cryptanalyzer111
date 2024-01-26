package com.javarush.cryptanalyzerGirls.iablocova;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
public class LoginWindow extends JDialog{
    public LoginWindow(JFrame parent) {
        super(parent, "Login", true);
        setSize(800, 600); // Увеличиваем размер
        setLayout(new BorderLayout());

        // Устанавливаем темную тему
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.DARK_GRAY);



        ImageIcon imageIcon = new ImageIcon("src/image/aoao.jpg");
        Image originalImage = imageIcon.getImage();
        int newWidth = 800; // Новая ширина изображения
        int newHeight = 600; // Новая высота изображения
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage);

        // Создаем JLabel для отображения изображения
        JLabel imageLabel = new JLabel(scaledImageIcon);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);

        // Добавляем JLabel на панель
        panel.add(imageLabel, BorderLayout.CENTER);

        // Стилизованный шрифт для кнопок
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        // Кнопка входа
        JButton loginButton = new JButton("Login");
        loginButton.setFont(buttonFont);
        loginButton.setForeground(Color.BLACK); // Устанавливаем цвет текста кнопки
        loginButton.setBackground(Color.DARK_GRAY); // Устанавливаем цвет фона кнопки
        loginButton.setBorderPainted(false); // Убираем рамку
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Закрываем окно
            }
        });

        // Кнопка отмены
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(buttonFont);
        cancelButton.setForeground(Color.BLACK); // Устанавливаем цвет текста кнопки
        cancelButton.setBackground(Color.DARK_GRAY); // Устанавливаем цвет фона кнопки
        loginButton.setBorderPainted(false); // Убираем рамку
        loginButton.setFocusPainted(false);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Закрываем окно
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Закрываем всю программу
            }
        });

        // Панель для кнопок
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(loginButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.setBackground(Color.DARK_GRAY);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.PAGE_END);

        setResizable(false);
        setLocationRelativeTo(parent);
    }
}
