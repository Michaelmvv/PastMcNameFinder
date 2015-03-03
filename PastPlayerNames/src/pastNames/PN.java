package pastNames;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

public class PN implements ActionListener, KeyListener {

    JFrame frame = new JFrame("Past user names");
    JTabbedPane tabbedPane = new JTabbedPane();
    JPanel input = new JPanel();
    JTextPane output = new JTextPane();

    JTextField textField = new JTextField();
    JButton goButton = new JButton("Find User");

    public static void main(String[] args) {
        new PN().gui();
    }

    public void gui() {

        textField.setPreferredSize(new Dimension(150, 30));
        frame.add(tabbedPane);
        tabbedPane.add("Input", input);
        tabbedPane.add("Output", output);
        input.add(textField);
        input.add(goButton);
        goButton.addActionListener(this);
        textField.addKeyListener(this);
        frame.pack();
        frame.setVisible(true);

    }

    public void getInfoOnPlayer() {

        String input = textField.getText();
        try {
            String webData = getInfoFromMc("https://api.mojang.com/users/profiles/minecraft/" + input);
            Gson gson = new Gson();
            JsonObject uuidData = gson.fromJson(webData, JsonObject.class);
            String uuid = "";
            if (uuidData != null) {
                uuid = uuidData.get("id").getAsString();
            }

            if (!uuid.equals("")) {

                StringBuilder builder = new StringBuilder();

                String namesData = getInfoFromMc("https://api.mojang.com/user/profiles/" + uuid + "/names");

                JsonArray pastNames = gson.fromJson(namesData, JsonArray.class);
                builder.append("Player UUID: " + uuid + " \n");
                builder.append("Past names of " + input + " are: \n");

                int i = 0;
                Iterator<JsonElement> iterator = pastNames.iterator();
                while (iterator.hasNext()) {

                    i++;
                    JsonElement element = gson.fromJson(iterator.next(), JsonElement.class);
                    JsonObject nameObj = element.getAsJsonObject();
                    String name = nameObj.get("name").getAsString();
                    builder.append(i + ". " + name + "\n");

                }
                output.setText(builder.toString());
                tabbedPane.setSelectedIndex(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getInfoFromMc(String urlString) throws Exception {

        BufferedReader reader = null;

        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);
            return buffer.toString();

        } finally {
            if (reader != null)
                reader.close();
        }
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        JButton buttonPressed = (JButton) arg0.getSource();

        if (buttonPressed == goButton) {
            getInfoOnPlayer();
            frame.pack();
        }

    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
            getInfoOnPlayer();
            frame.pack();
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // do not need
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // do not need
    }
}
