package com.myzone.apps.examples;

import com.myzone.reactive.events.ReferenceChangeEvent;
import com.myzone.reactive.reference.ConcurrentObservableReference;
import com.myzone.reactive.reference.ObservableReadonlyReference;
import com.myzone.reactive.reference.ObservableReference;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static com.myzone.reactive.reference.ObservableReferences.reduce;
import static java.lang.String.format;
import static javax.swing.SwingUtilities.invokeLater;

/**
 * @author myzone
 * @date 04.03.14.
 */
public class ObservableReferencesReduceExample {

    public static void main(String[] args) {
        // creating text fields
        JTextField textField1 = new JTextField();
        textField1.setSize(400, 20);
        textField1.setFont(new Font("Impact", Font.PLAIN, 20));

        JTextField textField2 = new JTextField();
        textField2.setSize(400, 20);
        textField2.setFont(new Font("Impact", Font.PLAIN, 20));

        JTextField textField3 = new JTextField();
        textField3.setSize(400, 20);
        textField3.setEditable(false);
        textField3.setFont(new Font("Impact", Font.PLAIN, 20));

        // creating it's view-model
        ObservableReference<String, ReferenceChangeEvent<String>> text1 = new ConcurrentObservableReference<>(textField1.getText());
        ObservableReference<String, ReferenceChangeEvent<String>> text2 = new ConcurrentObservableReference<>(textField2.getText());
        ObservableReadonlyReference<String,ReferenceChangeEvent<String>> text3 = reduce(text1, text2, (s1, s2) -> {
            s1 = s1.replaceAll("\\s+", "");
            s2 = s2.replaceAll("\\s+", "");

            if (s1.isEmpty() || s2.isEmpty()) {
                return "fail!";
            }

            return format("%s | %s", s1, s2);
        });

        // binding UI with view-model
        textField1.addKeyListener(new KeyAdapter() {
            public @Override void keyReleased(KeyEvent e) {
                text1.set(textField1.getText());
            }
        });

        textField2.addKeyListener(new KeyAdapter() {
            public @Override void keyReleased(KeyEvent e) {
                text2.set(textField2.getText());
            }
        });

        text3.addListener((source, event) -> {
            System.out.println("got event: " + event);

            invokeLater(() -> {
                textField3.setText(event.getNew());
            });
        });
        textField3.setText(text3.get());

        // some swing stuff just to display all
        Box verticalBox = Box.createVerticalBox();

        verticalBox.add(textField1);
        verticalBox.add(textField2);
        verticalBox.add(textField3);

        JFrame frame = new JFrame();
        frame.add(verticalBox);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}
