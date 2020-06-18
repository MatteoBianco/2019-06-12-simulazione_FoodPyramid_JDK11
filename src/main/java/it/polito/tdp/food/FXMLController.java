package it.polito.tdp.food;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.food.model.Condiment;
import it.polito.tdp.food.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField txtCalorie;
    
    @FXML
    private TextArea txtResult;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private ComboBox<Condiment> boxIngrediente;

    @FXML
    private Button btnDietaEquilibrata;

    @FXML
    void doCalcolaDieta(ActionEvent event) {
    	txtResult.clear();
    	Condiment start = this.boxIngrediente.getValue();
    	if(start == null) {
    		txtResult.setText("Errore: selezionare un ingrediente da cui partire per il calcolo della dieta!\n");
    		return;
    	}
    	List<Condiment> dieta = this.model.getIndipendentCondiments(start);
    	if(dieta.isEmpty()) {
    		txtResult.appendText("Nessuna dieta possibile trovata.\n");
    	}
    	txtResult.appendText("Dieta calcolata!\n\n");
    	Double totCalories = 0.0;
    	for(Condiment c : dieta) {
    		txtResult.appendText(c + "\n");
    		totCalories += c.getCondiment_calories();
    	}
    	txtResult.appendText(String.format("\nCalorie totali: %.5f", totCalories));
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	txtResult.clear();
    	this.boxIngrediente.getItems().clear();
    	Double calories;
    	try {
    		calories = Double.parseDouble(txtCalorie.getText());
    	} catch (NumberFormatException e) {
    		txtResult.setText("Errore: inserire un numero come valore delle calorie!");
    		return;
    	}
    	this.model.createGraph(calories);
    	txtResult.appendText(String.format("Grafo creato, con %d vertici e %d archi.\n\n", 
    			this.model.getVerticesSize(), this.model.getEdgesSize()));
    	for(Condiment c : this.model.listCondimentSortedByCalories()) {
    		txtResult.appendText(c + " - Foods: " + this.model.getFoodsForCondiment(c) + "\n");
    	}
    	this.boxIngrediente.getItems().addAll(this.model.listCondimentSortedByCalories());
    	this.btnDietaEquilibrata.setDisable(false);
    	this.boxIngrediente.setDisable(false);
    }
    
    @FXML
    void initialize() {
        assert txtCalorie != null : "fx:id=\"txtCalorie\" was not injected: check your FXML file 'Food.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Food.fxml'.";
        assert boxIngrediente != null : "fx:id=\"boxIngrediente\" was not injected: check your FXML file 'Food.fxml'.";
        assert btnDietaEquilibrata != null : "fx:id=\"btnDietaEquilibrata\" was not injected: check your FXML file 'Food.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		txtResult.setText("Inserire un valore per le calorie massime degli ingredienti. "
				+ "Si consigliano valori compresi tra 1 e 150 per una ricerca sensata.");
		this.boxIngrediente.setDisable(true);
		this.btnDietaEquilibrata.setDisable(true);
	}
}
