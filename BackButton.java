/**
* The backButton class allows users to go back and forth between different pages.
* It appears in the bottom left corner of each page.
* @author Faith Tong
* @version 6/14/2023
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BackButton {
  /**
  * Constructor for the back button, defines and styles the button and allows
  * hiding and showing JFrames. The backButton class needs no other methods as
  * the constructor holds all necessary functionality.
  * @param {JFrame} currentFrame - the frame that holds the back button
  * @param {JFrame} prevFrame - the frame the user goes back to when the back button is clicked
  */
  BackButton(JFrame currentFrame, JFrame prevFrame) {
    JButton back = new JButton("Back");
    back.setBounds(20, 520, 80, 30);
    currentFrame.add(back);

    back.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        currentFrame.setVisible(false);
        prevFrame.setVisible(true);
      }
    });
  }
}