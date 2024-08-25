/**
* The MoodTracking class sets up and displays 3 features: mood tracking, self-reflection, and personalized mood exercise.
* Clicking on each button takes it to the next page.
* Simulating phone apps, the windows have a dimension of 300 x 600.
* @author Faith Tong
* @version 6/14/2023
*/

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet; 
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MoodTracking {
  //Elements needed in wider scopes than just their method are declared and initialized as fields. Includes theme styles, JFrame windows, buttons, and a slider.
  
  //App theme colours and styles
  Color lightBlue = new Color(183, 213, 237);
  Font buttonFont = new Font(Font.SERIF, Font.BOLD, 13);
  SimpleAttributeSet textStyle = new SimpleAttributeSet();
  
  JFrame menu = new JFrame();
  JFrame moodTrackingPage = new JFrame();
  JFrame reflectionPage = new JFrame();
  JFrame exercisePage = new JFrame();

  JButton trackButton = new JButton("Track Mood");
  JButton reflectButton = new JButton("Self-Reflection");
  JButton exerciseButton = new JButton("Mood Exercise");
  BackButton trackBack = new BackButton(moodTrackingPage, menu);
  BackButton reflectionBack = new BackButton(reflectionPage, menu);
  BackButton exerciseBack = new BackButton(exercisePage, menu);

  JSlider moodRange = new JSlider(JSlider.HORIZONTAL, 1, 10, 5);

  /**
  * Automatically sets up the windows and assigns on-click actions to buttons through this constructor.
  */
  MoodTracking() {
    StyleConstants.setForeground(textStyle, Color.black);
    
    menu.setSize(300, 600);
    menu.getContentPane().setBackground(lightBlue);
    menu.setLayout(null);
    menu.setTitle("Main Menu");
    menu.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    menu.setVisible(true);
    moodTrackingPage.setSize(300, 600);
    moodTrackingPage.setLayout(null);
    reflectionPage.setSize(300, 600);
    reflectionPage.setLayout(null);
    exercisePage.setSize(300, 600);
    exercisePage.setLayout(null);

    moodTrackingPage.setVisible(false);
    reflectionPage.setVisible(false);
    exercisePage.setVisible(false);
    
    trackButton.setBounds(85, 70, 130, 40);
    trackButton.setFont(buttonFont);
    reflectButton.setBounds(70, 190, 160, 40);
    reflectButton.setFont(buttonFont);
    exerciseButton.setBounds(70, 320, 160, 40);
    exerciseButton.setFont(buttonFont);

    trackButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        trackMood();
      }
    });

    reflectButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        promptReflection();
      }
    });

    exerciseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        startExercise();
      }
    });

    menu.add(trackButton);
    menu.add(reflectButton);
    menu.add(exerciseButton);
  }

  /**
  * trackMood() holds all the action code for when the Track Mood button is pressed.
  * It hides the menu window, and shows the 'moodTrackingPage' window, with the text, slider, 
  * and button displayed. It also allows mood data to be shown (through reading and writing
  * to file) after clicking the submit button.
  */
  public void trackMood() {
    menu.setVisible(false);
    moodTrackingPage.setVisible(true);

    JTextPane question = new JTextPane();
    question.setCharacterAttributes(textStyle, true);
    question.setText("How are you feeling today? 1 - worst, 10 - best");
    question.setBounds(10, 10, 275, 40);
    question.setEditable(false);
    moodTrackingPage.add(question);

    moodRange.setBounds(10, 60, 250, 50);
    Hashtable moodRangeLabels = new Hashtable();
    for(int i = 1; i < 11; i++) {
      moodRangeLabels.put(i, new JLabel(String.valueOf(i)));
    }
    moodRange.setLabelTable(moodRangeLabels);
    moodRange.setPaintLabels(true);
    moodTrackingPage.add(moodRange);

    JButton submitButton = new JButton("See mood data");
    submitButton.setBounds(10, 120, 150, 40);
    submitButton.setFont(buttonFont);
    moodTrackingPage.add(submitButton);

    submitButton.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        getMoodData();
      }
    });
    
  }

  /**
  * Reads previous mood data from 'data.txt', adds the current mood on JSlider, and displays it.
  * If there is no 'data.txt', a new empty file is created. Data is displayed in the form of
  * a graph to show trend of mood. Writes all data points back to the text file, including the value from
  * the slider. Runs when 'submit mood data' button is clicked.
  */
  public void getMoodData() {
    File savedData = new File("data.txt");
    int[] newData = new int[100];
    int nextEmptyIndex = 0;

    try {
      savedData.createNewFile();
      Scanner input = new Scanner(savedData);
      while(input.hasNextLine()) { 
        int line = Integer.parseInt(input.nextLine());
        newData[nextEmptyIndex] = line;
        nextEmptyIndex++;
      }
    } catch (IOException i) {
      System.out.println(i);
    }

    int currentMood = moodRange.getValue();
    newData[nextEmptyIndex] = currentMood;
    nextEmptyIndex++;

    try {
    FileWriter fileWriter = new FileWriter(savedData);
    for(int i=0; i < nextEmptyIndex; i++) {
      String data = String.valueOf(newData[i]) + "\n";
      fileWriter.write(data);
    }
    fileWriter.close();
    } catch (IOException i) {
      System.out.println(i);
    }

    JTextPane graphTitle = new JTextPane();
    graphTitle.setCharacterAttributes(textStyle, true);
    graphTitle.setText("Your Mood Trends:");
    graphTitle.setBounds(10, 190, 275, 20);
    graphTitle.setEditable(false);
    moodTrackingPage.add(graphTitle);
    createGraph(newData, nextEmptyIndex);
    moodTrackingPage.setVisible(false);
    moodTrackingPage.setVisible(true);
  }

  /**
  * Draws a graph from an array of integers.
  * @author ChatGPT
  * @param {int[]} data - array of integers to be graphed
  * @param {int} dataLength - length of array
  */
  public void createGraph(int[] data, int dataLength) {
        final int[] copiedData = data.clone();
        final int copiedDataLength = dataLength;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);

                        int width = getWidth();
                        int height = getHeight();
                        int maxDataValue = 10;
                        int xScale = width / copiedDataLength;
                        int yScale = height / maxDataValue;

                        g.setColor(Color.BLUE);

                        for (int i = 0; i < copiedDataLength - 1; i++) {
                            int x1 = i * xScale;
                            int y1 = height - copiedData[i] * yScale;
                            int x2 = (i + 1) * xScale;
                            int y2 = height - copiedData[i + 1] * yScale;

                            g.drawLine(x1, y1, x2, y2);
                        }
                    }
                };

                panel.setBounds(10, 220, 300, 100);
                moodTrackingPage.add(panel);
            }
        });
    }

  /**
  * Displays a randomly chosen prompt related to mood regulation and allows the user
  * to input an answer. Called when the reflectButton is clicked.
  */
  public void promptReflection() {
    menu.setVisible(false);
    reflectionPage.setVisible(true);

    String[] prompts = {"What are 3 things you are grateful for and why?", "What is something that you need right now? How can you get it?", "What is something you are looking forward to?", "Is there anything holding you back? What can you do about it?", "What is something you've been telling yourself that might not be true?", "Have you acknowledged and allowed yourself to feel what you feel?", "What has been your dominant emotion recently, and what is causing it?", "Has the way you deal with and express your emotions affected those around you positively or negatively?"};

    Random randomizer = new Random();
    int randomIndex = randomizer.nextInt(7);

    JTextPane chosenPrompt = new JTextPane();
    chosenPrompt.setCharacterAttributes(textStyle, true);
    chosenPrompt.setBounds(10, 10, 275, 60);
    chosenPrompt.setEditable(false);
    chosenPrompt.setText(prompts[randomIndex]);
    reflectionPage.add(chosenPrompt);

    JTextArea response = new JTextArea();
    response.setBounds(10, 100, 275, 200);
    reflectionPage.add(response);
    
  }

  /**
  * Displays a series of questions related to mood which will be used to suggest an emotional regulation exercise for the user.
  * Sets up and styles 3 questions, with radio button options for each answer. Has a submit button that when
  * clicked, analyzes answers and suggests a related exercise.
  */
  public void startExercise() {
    menu.setVisible(false);
    exercisePage.setVisible(true);

    JTextPane firstQuestion = new JTextPane();
    firstQuestion.setCharacterAttributes(textStyle, true);
    firstQuestion.setText("Which word best describes your mood?");
    firstQuestion.setBounds(10, 20, 275, 20);
    firstQuestion.setEditable(false);
    exercisePage.add(firstQuestion);
    JRadioButton happyOption = new JRadioButton("Happy");
    happyOption.setBounds(30, 45, 200, 20);
    happyOption.setActionCommand("H");
    JRadioButton sadOption = new JRadioButton("Sad");
    sadOption.setBounds(30, 65, 200, 20);
    sadOption.setActionCommand("S");
    JRadioButton angryOption = new JRadioButton("Angry");
    angryOption.setBounds(30, 85, 200, 20);
    angryOption.setActionCommand("A");
    JRadioButton worriedOption = new JRadioButton("Worried");
    worriedOption.setBounds(30, 105, 200, 20);
    worriedOption.setActionCommand("W");
    ButtonGroup moodOptions = new ButtonGroup();
    moodOptions.add(happyOption);
    moodOptions.add(sadOption);
    moodOptions.add(angryOption);
    moodOptions.add(worriedOption);
    exercisePage.add(happyOption);
    exercisePage.add(sadOption);
    exercisePage.add(angryOption);
    exercisePage.add(worriedOption);
    
    JTextPane secondQuestion = new JTextPane();
    secondQuestion.setCharacterAttributes(textStyle, true);
    secondQuestion.setText("Do you have some free time now?");
    secondQuestion.setBounds(10, 150, 275, 20);
    secondQuestion.setEditable(false);
    exercisePage.add(secondQuestion);
    JRadioButton timeTrueOption = new JRadioButton("Yes");
    timeTrueOption.setBounds(30, 175, 200, 20);
    timeTrueOption.setActionCommand("Y");
    JRadioButton timeFalseOption = new JRadioButton("No");
    timeFalseOption.setBounds(30, 195, 200, 20);
    timeFalseOption.setActionCommand("N");
    ButtonGroup timeOptions = new ButtonGroup();    
    timeOptions.add(timeTrueOption);
    timeOptions.add(timeFalseOption);
    exercisePage.add(timeTrueOption);
    exercisePage.add(timeFalseOption);

    JTextPane thirdQuestion = new JTextPane();
  thirdQuestion.setCharacterAttributes(textStyle, true);
    thirdQuestion.setText("Do you know what caused that feeling?");
    thirdQuestion.setBounds(10, 240, 275, 20);
    thirdQuestion.setEditable(false);
    exercisePage.add(thirdQuestion);
    JRadioButton causeTrueOption = new JRadioButton("Yes");
    causeTrueOption.setBounds(30, 265, 200, 20);
    causeTrueOption.setActionCommand("Y");
    JRadioButton causeFalseOption = new JRadioButton("No");
    causeFalseOption.setBounds(30, 285, 200, 20);
    causeFalseOption.setActionCommand("N");
    ButtonGroup causeOptions = new ButtonGroup();
    causeOptions.add(causeTrueOption);
    causeOptions.add(causeFalseOption);
    exercisePage.add(causeTrueOption);
    exercisePage.add(causeFalseOption);
    
    JButton submitButton = new JButton("Exercise suggestion");
    submitButton.setBounds(10, 335, 190, 40);
    submitButton.setFont(buttonFont);
    exercisePage.add(submitButton);

    JTextPane exercise = new JTextPane();
    exercise.setCharacterAttributes(textStyle, true);
    exercise.setBounds(10, 385, 275, 50);
    exercise.setEditable(false);

    submitButton.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        //The .getSelection() method will throw a NullPointerException if submit is clicked without selecting anything.
        //If this happens, the user will be prompted to select an option for each question.
        try {
          String selectedMood = moodOptions.getSelection().getActionCommand();
          String selectedTime = timeOptions.getSelection().getActionCommand();
          String selectedCause = causeOptions.getSelection().getActionCommand();
          chooseExercise(exercise, selectedMood, selectedTime, selectedCause);
        } catch (NullPointerException n) {
          exercise.setText("Please select an option for each question.");
        }
      }
    });

    exercisePage.add(exercise);
  }

  /**
  * Takes the selected option of each questions and uses conditional statements to suggest a mood exercise.
  * Called when the submit button on the Exercise Page is clicked.
  * @param {JTextPane} text - The text pane where the message will be shown.
  * @param (String) mood - The character corresponding to the selected option in the first question - 'What word best describes your mood?'
  * @param (String) time - 'Y' or 'N', the selected answer to the question 'Do you have some free time now?'
  * @param {String} cause - 'Y' or 'N', the selected answer to the question 'Do you know what caused that feeling?'
  */
  public void chooseExercise(JTextPane text, String mood, String time, String cause){
    if (mood.equals("H")) {
      if (time.equals("Y")) {
        text.setText("Do something nice for someone else!");
      }
      else if (time.equals("N")) {
        if (cause.equals("Y")) {
          text.setText("Thank whoever made you feel that way!");
        }
        else if (cause.equals("N")) {
          text.setText("Tell someone you love them!");
        }
      }
    }
    else if (mood.equals("S")) {
      if (time.equals("Y")) {
        if (cause.equals("Y")) {
          text.setText("Talk to someone you trust about it.");
        }
        else if (cause.equals("N")) {
          text.setText("Try journaling.");
        }
      }
      else if (time.equals("N")) {
        text.setText("Listen to a comforting song.");
      }
    }
    else if (mood.equals("A")) {
      if (time.equals("Y")) {
        text.setText("Do some exercise.");
      }
      else if (time.equals("N")) {
        if (cause.equals("Y")) {
          text.setText("Try and think about the situation from a different perspective.");
        }
        else if (cause.equals("N")) {
          text.setText("Take a few deep breaths.");
        }
      }      
    }
    else if (mood.equals("W")) {
      if (time.equals("Y")) {
        text.setText("Take a walk in nature.");
      }
      else if (time.equals("N")) {
        if (cause.equals("Y")) {
          text.setText("Think about the things you CAN control in the situation, not the things you can't.");
        }
        else if (cause.equals("N")) {
          text.setText("Take a few deep breaths.");
        }
      }
    }
  }
  
}