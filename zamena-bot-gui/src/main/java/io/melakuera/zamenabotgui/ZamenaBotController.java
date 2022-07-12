package io.melakuera.zamenabotgui;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ZamenaBotController {

    private Stage stage;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    private static final String BASE_URL = "http://localhost:8432/zamena";
    private static final Logger logger = Logger.getLogger(ZamenaBotController.class.getName());

    private File file;

    @FXML
    private Label mainLabel;
    @FXML
    private Button fileInput;
    @FXML
    private Button acceptBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Label outputLog;

    @FXML
    protected void fileInput() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите PDF-документ");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Portable Document Format", "*.pdf"));

        File pickedFile = fileChooser.showOpenDialog(stage);

        if (pickedFile != null) {

            mainLabel.setText(pickedFile.getName());

            fileInput.setVisible(false);
            acceptBtn.setVisible(true);
            cancelBtn.setVisible(true);
            
            file = pickedFile;
        }
    }

    @FXML
    protected void onClickAcceptBtn() {

    	try (CloseableHttpClient client = HttpClients.createDefault()) {
    		
    		HttpPost post = new HttpPost(BASE_URL);
    	    HttpEntity entity = MultipartEntityBuilder.create()
    	    		.addPart("zamena", new FileBody(file)).build();
    	    post.setEntity(entity);

    	    try (CloseableHttpResponse response = client.execute(post)) {

                String responseData = EntityUtils.toString(
                        response.getEntity(), "UTF-8");

                handleResponse(responseData);

            } catch (Exception e) {
                logger.log(Level.WARNING, "Ошибка");
                mainLabel.setText("Ошибка");
            }

    	} catch (Exception e) {
            logger.log(Level.WARNING, "Ошибка");
            mainLabel.setText("Ошибка");
        }
    }

    private void handleResponse(String responseData) throws ParseException {

        JSONParser jsonParser = new JSONParser();
        JSONObject responseJson = (JSONObject) jsonParser.parse(responseData);

        if (responseJson.get("status").equals("ok")) {
            mainLabel.setText("PDF-Документ успешно загружен");
        } else {
            mainLabel.setText("Ошибка");
        }
    }

    @FXML
    protected void onClickCancelBtn() {

        mainLabel.setText("");

        fileInput.setVisible(true);
        acceptBtn.setVisible(false);
        cancelBtn.setVisible(false);
        
        file = null;
    }
}