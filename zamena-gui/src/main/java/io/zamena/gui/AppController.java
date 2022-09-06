package io.zamena.gui;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.hc.client5.http.HttpHostConnectException;
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
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AppController {

    private Stage stage;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private static final String BASE_URL = "https://67f5-94-143-198-244.eu.ngrok.io/zamena";

    private File file;
    
    private JSONParser jsonParser = new JSONParser();

    @FXML
    private Label mainLabel;
    @FXML
    private Button fileInput;
    @FXML
    private Button acceptBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private TextArea outputLog;

    @FXML
    public void fileInput() {

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
    public void onClickAcceptBtn() {

    	try (CloseableHttpClient client = HttpClients.createDefault()) {
    		
    		HttpPost post = new HttpPost(BASE_URL);
    	    HttpEntity entity = MultipartEntityBuilder.create()
    	    		.addPart("zamena", new FileBody(file)).build();
    	    post.setEntity(entity);

    	    CloseableHttpResponse response = client.execute(post);

            handleResponse(response);
            
            clearScene();

    	} catch (HttpHostConnectException e) {
    		e.printStackTrace();
            mainLabel.setText("Приложение на данный момент недоступен");
            setOutputLog(e);
            
            clearScene();
    	} catch (IllegalArgumentException e) {
    		e.printStackTrace();
			mainLabel.setText(e.getMessage());
			setOutputLog(e);
            
			clearScene();
		} catch (IOException |
				org.apache.hc.core5.http.ParseException | 
				ParseException e) {
    		e.printStackTrace();
    		
    		setOutputLog(e);
		} 
    }
    
    @FXML
    public void onClickCancelBtn() {

        mainLabel.setText("");

        clearScene();
    }

    private void handleResponse(CloseableHttpResponse response) 
    		throws ParseException,
    		org.apache.hc.core5.http.ParseException, 
    		IllegalArgumentException,
    		IOException {

    	String responseData = EntityUtils.toString(
                response.getEntity(), "UTF-8");
    	
        JSONObject responseJson = (JSONObject) jsonParser.parse(responseData);
        
        if (responseJson.get("status").equals("fail")) {
            throw new IllegalArgumentException((String) responseJson.get("message"));
        }
        System.out.println(responseJson.toJSONString());
        mainLabel.setText((String) responseJson.get("message"));
    }
    
    private void clearScene() {
    	fileInput.setVisible(true);
        acceptBtn.setVisible(false);
        cancelBtn.setVisible(false);
        
        file = null;
    }
    
    private void setOutputLog(Exception e) {
    	
    	StringWriter errors = new StringWriter();
    	e.printStackTrace(new PrintWriter(errors));
    	
    	outputLog.setText(errors.toString());
    }
}