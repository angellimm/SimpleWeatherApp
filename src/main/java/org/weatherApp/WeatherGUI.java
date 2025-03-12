package org.weatherApp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.json.JSONObject;

public class WeatherGUI extends JFrame {

    private JLabel temperatureText;
    private JLabel weatherDesc;
    private JLabel humidText;
    private JLabel windText;
    private JLabel weatherConditionImage;

    public WeatherGUI() {

        //настраиваем гууи, название
        super("Weather App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        //запускать гууи по центру экрана
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        addGuiComponents();
    }

    private void addGuiComponents() {

        JLabel searchText = new JLabel("Search");
        searchText.setBounds(15, 10, 450, 54);
        searchText.setFont(new Font("Fairfax", Font.PLAIN, 30));
        Color custom2 = new Color(33, 85, 95);
        searchText.setForeground(custom2);
        add(searchText);

        //поисковое поле
        JTextField search = new JTextField();
        search.setBounds(15, 55, 340, 40);
        search.setFont(new Font("Fairfax", Font.PLAIN, 24));
        add(search);

        // кнопка поиска
        JButton searchButt = new JButton(loadImage("assets/search2.png", 47,45));
        searchButt.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButt.setBounds(365, 55, 47, 45);
        searchButt.addActionListener(e -> {
            String city = search.getText();
            if (!city.isEmpty()) {
                updateWeather(city);
            }
        });
        add(searchButt);

        //погода кратинки
        weatherConditionImage = new JLabel(loadImage("assets/cloudy.png",  245, 217));
        weatherConditionImage.setBounds(0, 125,  450, 217);
        weatherConditionImage.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionImage);

        //темература
        temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Fairfax", Font.PLAIN, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //погодные условия
        weatherDesc = new JLabel("CLOUDY");
        weatherDesc.setBounds(0, 405,450, 54);
        weatherDesc.setFont(new Font("Fairfax", Font.PLAIN, 32));
        weatherDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherDesc);

        //влажность
        JLabel humidityImage = new JLabel(loadImage("assets/humid1.png",120, 120 ));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        humidText = new JLabel("<html><b>Humidity </b> 100%</html>");
        humidText.setBounds(90, 500, 85, 55);
        humidText.setFont(new Font("Fairfax", Font.PLAIN, 19));
        Color custom1 = new Color(70, 130, 180);
        humidText.setForeground(custom1);
        add(humidText);

        // ветер
        JLabel windImage = new JLabel(loadImage("assets/windspeed.png", 74,66));
        windImage.setBounds(220, 500, 74, 66);
        add(windImage);

        windText = new JLabel("<html><b>Wind  </b> <br>15 km/h</html>");
        windText.setBounds(310, 500, 100, 65);
        windText.setFont(new Font("Fairfax", Font.PLAIN, 22));
        windText.setForeground(custom1);
        add(windText);



    }

    private void updateWeather(String city) {
        // SwingWorker для выполнения запроса в фоновом потоке
        new SwingWorker <String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return WeatherApp.getWeatherData(city);
            }

            @Override
            protected void done() {
            try {
                String weatherData = get();
                if (weatherData != null){
                    JSONObject json = new JSONObject(weatherData);
                    JSONObject current = json.getJSONObject("current");
                    double temperature = current.getDouble("temp_c");
                    temperatureText.setText(temperature + " °C");

                    // обновляем
                    String condition = current.getJSONObject("condition").getString("text");
                    weatherDesc.setText(condition.toUpperCase());

                    int humidity = current.getInt("humidity");
                    humidText.setText("<html><b>Humidity </b>" + humidity + "%</html>");

                    double windSpeed = current.getDouble("wind_kph");
                    windText.setText("<html><b>Wind </b> " + windSpeed + "km/h</html>");

                    weatherConditionImage.setIcon(loadImage(getWeatherIconPath(condition), 245, 217));
                } else {
                    temperatureText.setText("No data");
                    weatherDesc.setText("ERROR");
                    humidText.setText("<html><b>Humidity </b>N/A</html>");
                    windText.setText("<html><b>Wind </b>N/A</html>");
                }
            } catch (Exception e) {
                e.printStackTrace();
                temperatureText.setText("ERROR");
                weatherDesc.setText("ERROR");
                humidText.setText("<html><b>Humidity </b>N/A</html>");
                windText.setText("<html><b>Wind </b>N/A</html>");
            }
            }
        }.execute();
    }

    private String getWeatherIconPath(String condition) {
        if (condition.toLowerCase().contains("cloud")) {
            return "assets/cloudy.png";
        } else if (condition.toLowerCase().contains("sun")) {
            return "assets/clear.png";
        } else if (condition.toLowerCase().contains("clear")) {
                return "assets/clear.png";
        } else if (condition.toLowerCase().contains("rain")) {
            return "assets/rain.png";
        } else if (condition.toLowerCase().contains("snow")) {
            return "assets/snow.png";
        } else {
            return "assets/cloudy.png";
        }
    }

    private ImageIcon loadImage(String resourcePath, int width, int height) {
        try {
            // Загружаем изображение
            BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource(resourcePath));
            if (image == null) {
                System.out.println("Изображение не найдено: " + resourcePath);
                return null;
            }

            // масштабируем изображение
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}


