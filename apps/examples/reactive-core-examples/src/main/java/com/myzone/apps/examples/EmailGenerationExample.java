package com.myzone.apps.examples;

import com.myzone.reactive.event.ReferenceChangeEvent;
import com.myzone.reactive.reference.ConcurrentObservableReference;
import com.myzone.reactive.reference.ObservableReadonlyReference;
import com.myzone.reactive.reference.ObservableReference;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Function;
import java.util.regex.Pattern;

import static com.myzone.reactive.reference.ObservableReferences.filter;
import static com.myzone.reactive.reference.ObservableReferences.map;
import static com.myzone.reactive.reference.ObservableReferences.reduce;
import static java.lang.String.format;
import static javax.swing.SwingUtilities.invokeLater;

/**
 * @author myzone
 * @date 04.03.14
 */
public class EmailGenerationExample {

    private static final String EMAIL_DOMAIN = "mysite.com";
    private static final Pattern EMAIL_MATCHING_PATTERN = Pattern.compile(format("^%s\\.%s@%s$", "([A-Za-z0-9._-]+)", "([A-Za-z0-9._-]+)", EMAIL_DOMAIN));

    public static void main(String[] args) {
        // creating text fields
        JTextField firstNameTextField = new JTextField();
        firstNameTextField.setSize(400, 20);
        firstNameTextField.setFont(new Font("Impact", Font.PLAIN, 20));

        JTextField secondNameTextField = new JTextField();
        secondNameTextField.setSize(400, 20);
        secondNameTextField.setFont(new Font("Impact", Font.PLAIN, 20));

        JTextField emailTextField = new JTextField();
        emailTextField.setSize(400, 20);
        emailTextField.setEditable(false);
        emailTextField.setFont(new Font("Impact", Font.PLAIN, 20));

        JButton okButton = new JButton("Ok");
        okButton.setSize(400, 20);
        okButton.setFont(new Font("Impact", Font.PLAIN, 20));

        // creating its view-model
        ObservableReference<String, ReferenceChangeEvent<String>> firstNameProperty = new ConcurrentObservableReference<>(firstNameTextField.getText());
        ObservableReference<String, ReferenceChangeEvent<String>> secondNameProperty = new ConcurrentObservableReference<>(secondNameTextField.getText());

        ObservableReadonlyReference<String, ReferenceChangeEvent<String>> trimmedFirstNameProperty = map(firstNameProperty, String::trim);
        ObservableReadonlyReference<String, ReferenceChangeEvent<String>> trimmedSecondNameProperty = map(secondNameProperty, String::trim);

        ObservableReadonlyReference<String, ReferenceChangeEvent<String>> emailNameProperty = filter(reduce(
                trimmedFirstNameProperty,
                trimmedSecondNameProperty,
                (firstName, secondName) -> format("%s.%s@%s", normalize(firstName), normalize(secondName), EMAIL_DOMAIN)
        ), EMAIL_MATCHING_PATTERN.asPredicate(), "");
        ObservableReadonlyReference<Boolean, ReferenceChangeEvent<Boolean>> statusProperty = reduce(
                map(trimmedFirstNameProperty, EmailGenerationExample::validate),
                map(trimmedSecondNameProperty, EmailGenerationExample::validate),
                Boolean::logicalAnd
        );

        // binding UI with view-model
        firstNameTextField.addKeyListener(new KeyAdapter() {
            public
            @Override
            void keyReleased(KeyEvent e) {
                firstNameProperty.set(firstNameTextField.getText());
            }
        });

        secondNameTextField.addKeyListener(new KeyAdapter() {
            public
            @Override
            void keyReleased(KeyEvent e) {
                secondNameProperty.set(secondNameTextField.getText());
            }
        });

        emailNameProperty.addListener((source, event) -> {
            System.out.println("emailNameProperty - got event: " + event);

            invokeLater(() -> {
                emailTextField.setText(event.getNew());
            });
        });
        emailTextField.setText(emailNameProperty.get());

        statusProperty.addListener((source, event) -> {
            System.out.println("statusProperty - got event: " + event);

            invokeLater(() -> {
                okButton.setEnabled(event.getNew());
            });
        });
        okButton.setEnabled(statusProperty.get());

        // some swing stuff just to display all
        Box verticalBox = Box.createVerticalBox();

        verticalBox.add(firstNameTextField);
        verticalBox.add(secondNameTextField);
        verticalBox.add(emailTextField);
        verticalBox.add(okButton);

        JFrame frame = new JFrame();
        frame.add(verticalBox);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static String normalize(String s) {
        return s.toLowerCase();
    }

    private static boolean validate(String s) {
        return s.matches("^[A-Za-z0-9._-]+$");
    }


}
