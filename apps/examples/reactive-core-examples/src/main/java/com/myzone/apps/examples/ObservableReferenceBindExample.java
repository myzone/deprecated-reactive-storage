package com.myzone.apps.examples;

import com.myzone.reactive.events.ReferenceChangeEvent;
import com.myzone.reactive.reference.ConcurrentObservableReference;
import com.myzone.reactive.reference.ObservableReference;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static com.myzone.reactive.reference.ObservableReferences.bind;
import static javax.swing.SwingUtilities.invokeLater;

/**
 * @author myzone
 * @date 04.03.14
 */
public class ObservableReferenceBindExample {

    public static void main(String[] args) {
        // creating text fields
        JTextField textField1 = new JTextField();
        textField1.setSize(400, 20);
        textField1.setFont(new Font("Impact", Font.PLAIN, 20));

        JTextField textField2 = new JTextField();
        textField2.setSize(400, 20);
        textField2.setEditable(false);
        textField2.setFont(new Font("Impact", Font.PLAIN, 20));

        // creating it's view-model
        ObservableReference<String, ReferenceChangeEvent<String>> text1 = new ConcurrentObservableReference<>(textField1.getText());
        ObservableReference<String, ReferenceChangeEvent<String>> text2 = new ConcurrentObservableReference<>();

        // binding UI with view-model
        bind(text2, text1);

        textField1.addKeyListener(new KeyAdapter() {
            public @Override void keyReleased(KeyEvent e) {
                text1.set(textField1.getText());
            }
        });

        text2.addListener((source, event) -> {
            System.out.println("got event: " + event);

            invokeLater(() -> {
                textField2.setText(event.getNew());
            });
        });
        textField2.setText(text2.get());

        // some swing stuff just to display all
        Box verticalBox = Box.createVerticalBox();

        verticalBox.add(textField1);
        verticalBox.add(textField2);

        JFrame frame = new JFrame();
        frame.add(verticalBox);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}
