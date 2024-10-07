import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui(){
        super ("Weather App");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650 );
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        addGuiComponents();
    }
    private void addGuiComponents(){
       //Search Field
        JTextField searchTextField = new JTextField();
       searchTextField.setBounds(15, 15, 351, 45);
       searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        //Weather Image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        //Temperature Text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //Weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //Humidity Image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        //Humidity Text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55 );
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //WindSpeed Image
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        //WindSpeed Text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55 );
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        //Search Button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Get Location from user
                String userInput = searchTextField.getText();

                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                //Retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                //Update gui

                //Update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                //Update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "C");

                //Update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                //Update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                //Update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");

            }
        });
        add(searchButton);

    }

    private ImageIcon loadImage(String resourcePath){
        try{
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }


}
