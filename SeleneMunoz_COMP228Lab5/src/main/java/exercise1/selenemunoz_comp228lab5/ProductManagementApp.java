package exercise1.selenemunoz_comp228lab5;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

@SuppressWarnings("ALL")
public class ProductManagementApp extends Application {

    /*-------- Global Declarations --------*/

    // Label Declaration //
    private Label idLabel;
    private Label nameLabel;
    private Label descriptionLabel;
    private Label priceLabel;
    private Label categoryLabel;
    private Label messageLabel;

    // Text Fields Declaration //
    private TextField idTextField;
    private TextField nameTextField;
    private TextField descriptionTextField;
    private TextField priceTextField;

    // ComboBox Declaration //
    ComboBox<String> categoryComboBox;

    //Table view declaration //
    private TableView<Product> productTableView;
    /*----------------*/

    /*-------- Create connection with database --------*/

    // Connection Object //
    private Connection connection;

    // Method to connect to database //
    private void setConnectionToDatabase(){
        String databaseURL = "jdbc:mysql://localhost:3306/SeleneMunoz_COMP228Lab5Database";
        String username = "root";
        String password = "root";

        try{
            connection = DriverManager.getConnection(databaseURL,username, password);
            System.out.println("Connection made successfully to the database.");
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }
    /*----------------*/

    /*-------- Validation User's Input Methods --------*/

    // User's Input Validation Method // - Checks if user's input is empty or blank
    private Boolean userInputValidation(String inputValue, String textFieldType){
        if(inputValue == null || inputValue.isEmpty() || inputValue.isBlank()){

            switch (textFieldType) {
                case "id":
                    messageLabel.setText("Please enter product id");
                    return false;

                case "name":
                    messageLabel.setText("Please enter product name");
                    return false;

                case "category":
                    messageLabel.setText("Please enter product category");
                    return false;

                case "price":
                    messageLabel.setText("Please enter product price");
                    return false;

                case "description":
                    messageLabel.setText("Please enter description");
                    return false;

                default:
                    System.out.println("Error with arguments during checkInputValidation method");
                    break;
            }
        }
        return true;
    }

    // Record Validation with Product ID // - Check if product's record exists with product ID
    private Boolean checkRecordExist(int id){
        try{
            PreparedStatement selectProductWhereIdStatement = connection.prepareStatement("select * from products where id=?");
            selectProductWhereIdStatement.setInt(1, id);
            ResultSet resultSet = selectProductWhereIdStatement.executeQuery();

            if(resultSet.next()){
                return true;
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }
        System.out.println("Product Record with ID [ " + id + " ] is not available");
        messageLabel.setText("Product Record with ID [ " + id + " ] is not available");
        return false;
    }
    /*----------------*/

    /*-------- Database Interactions Methods --------*/

    // Load Product Information Method // - Loads database information into tableview
    private void loadInformationIntoTableview(){

        // Obeservable List - Store data from database
        ObservableList<Product> productObservableList = FXCollections.observableArrayList();

        try {
            Statement selectProductsStatement = connection.createStatement();

            // Resultset - Store data from query
            ResultSet resultSet = selectProductsStatement.executeQuery("select * from products");

            // Store resultset data into productObservableList
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                String categoryString = resultSet.getString("category");
                Category category = Category.valueOf(categoryString);

                // Create and Add Product object into productObservableList
                productObservableList.add(new Product(id, name, description, price, category));
            }

            // Link productObservableList into productTableView
            productTableView.setItems(productObservableList);
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    // Insert Product Information Method // - Insert product information into database
    private void insertNewProduct(){
        try{
            String id = idTextField.getText();
            String name = nameTextField.getText();
            String price = priceTextField.getText();
            String description = descriptionTextField.getText();
            String category = categoryComboBox.getValue();

            // Input Validation
            if(userInputValidation(id, "id") && userInputValidation(name, "name") && userInputValidation(description, "description")
                    && userInputValidation(price, "price") && userInputValidation(category, "category")) {
                // Check if record exist with given ID
                if (checkRecordExist(Integer.parseInt(id))) {
                    messageLabel.setText("Product with ID [ " + id + " ] already exists.");
                } else {
                    // Insert Statement to Insert data into database
                    PreparedStatement insertStatement = connection.prepareStatement("insert into products (id, name, description, price, category) values(?,?,?,?,?)");
                    insertStatement.setInt(1, Integer.parseInt(id));
                    insertStatement.setString(2, name);
                    insertStatement.setString(3, description);
                    insertStatement.setDouble(4, Double.parseDouble(price));
                    insertStatement.setString(5, category);
                    insertStatement.executeUpdate();
                    System.out.println("Product Record Inserted Successfully");
                    clearAllFields();
                    messageLabel.setText("Product Record Inserted Successfully");
                    loadInformationIntoTableview();
                }
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    // Delete Product Information Method // - Delete product information from the database with given ID
    private void deleteExistingProduct(){
        try{
            // Input Validation
            if(userInputValidation(idTextField.getText(), "id")){
                int id = Integer.parseInt(idTextField.getText());

                // Check if record exist with given ID
                if(checkRecordExist(id)){
                    PreparedStatement deleteProductWhereIdStatement = connection.prepareStatement("delete from products where id=?");
                    deleteProductWhereIdStatement.setInt(1, id);
                    deleteProductWhereIdStatement.executeUpdate();
                    System.out.println("Record Deleted Successfully");
                    clearAllFields();
                    messageLabel.setText("Record Deleted Successfully");
                    loadInformationIntoTableview();
                }
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    // Update Product Information Method // - Update product information from the database with given ID
    private void updateExistingProduct(){
        try{
            String id = idTextField.getText();
            String name = nameTextField.getText();
            String price = priceTextField.getText();
            String description = descriptionTextField.getText();
            String category = categoryComboBox.getValue();

            // Input Validation
            if(userInputValidation(id, "id") && userInputValidation(name, "name") && userInputValidation(description, "description")
                    && userInputValidation(price, "price") && userInputValidation(category, "category"))
            {
                // Check if record exist with given ID
                if(checkRecordExist(Integer.parseInt(id))){
                    PreparedStatement updateProductWhereIdStatement = connection.prepareStatement("update products set name=?,description=?,price=?,category=? where id=?");
                    updateProductWhereIdStatement.setString(1, name);
                    updateProductWhereIdStatement.setString(2, description);
                    updateProductWhereIdStatement.setDouble(3, Double.parseDouble(price));
                    updateProductWhereIdStatement.setString(4, category);
                    updateProductWhereIdStatement.setInt(5, Integer.parseInt(id));
                    updateProductWhereIdStatement.executeUpdate();
                    System.out.println("Product Record Updated Successfully");
                    clearAllFields();
                    messageLabel.setText("Product Record Updated Successfully");
                    loadInformationIntoTableview();
                }
            }
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }
    /*----------------*/

    // Clear All Input Fields Method //
    private void clearAllFields(){
        idTextField.clear();
        nameTextField.clear();
        descriptionTextField.clear();
        priceTextField.clear();
        messageLabel.setText("");
        categoryComboBox.setValue(null);
    }

    @Override
    public void start(Stage stage) throws Exception {

        /*-------- Initialization of Global Declarations ---------*/

            /*==== Table View ====*/
                // Initialization //
                productTableView = new TableView<>();

                //Table Structure - Columns //
                TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
                TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
                TableColumn<Product, String> descriptionColumn = new TableColumn<>("Description");
                TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
                TableColumn<Product, Category> categoryColumn = new TableColumn<>("Category");

                // Table Structure - Rows //
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
                priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
                categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

                // Add Columns into Table //
                productTableView.getColumns().addAll(idColumn, nameColumn, descriptionColumn, priceColumn, categoryColumn);
            /*========*/

            // Labels //
            idLabel = new Label("ID: ");
            nameLabel = new Label("Name: ");
            descriptionLabel = new Label("Description: ");
            priceLabel = new Label("Price: ");
            categoryLabel = new Label("Category: ");
            messageLabel = new Label();

            // Text Fields //
            idTextField = new TextField();
            idTextField.setPromptText("Enter Id");

            nameTextField = new TextField();
            nameTextField.setPromptText("Enter Name");

            descriptionTextField = new TextField();
            descriptionTextField.setPromptText("Enter Description");

            priceTextField = new TextField();
            priceTextField.setPromptText("Enter Price");

            /*==== ComboBox ====*/
                // Initialization //
                categoryComboBox = new ComboBox<>();

                // Enum Values //
                for (Category category : Category.values()){
                    categoryComboBox.getItems().add(category.toString());
                }
                categoryComboBox.setPromptText("Select Category");
            /*========*/

        /*----------------*/

        /*-------- HBox Layout --------*/

            // Buttons //
            Button insertButton = new Button("New Product");
            insertButton.setOnAction(actionEvent -> insertNewProduct());
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(actionEvent -> deleteExistingProduct());
            Button updateButton = new Button("Update");
            updateButton.setOnAction(actionEvent -> updateExistingProduct());
            Button resetButton = new Button("Reset");
            resetButton.setOnAction(actionEvent -> clearAllFields());

            // HBox Initialization //
            HBox hBox = new HBox(10, insertButton, updateButton, deleteButton, resetButton);
            hBox.setAlignment(Pos.CENTER);
            hBox.setPadding(new Insets(10));

        /*----------------*/

        /*-------- BorderPane Layout --------*/

            // BorderPane Initialization //
            BorderPane borderPane = new BorderPane();
            borderPane.setPadding(new Insets(10));
            borderPane.setCenter(messageLabel);

        /*----------------*/

        /*-------- GridPane Layout --------*/

            // GridPane Initialization //
            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setPadding(new Insets(10,10,10,10));

            // GridPane Structure //
            gridPane.add(idLabel, 0,0);
            gridPane.add(idTextField, 1, 0);
            gridPane.add(nameLabel, 0, 1);
            gridPane.add(nameTextField, 1, 1);
            gridPane.add(descriptionLabel, 0, 2);
            gridPane.add(descriptionTextField, 1, 2);
            gridPane.add(priceLabel, 0, 3);
            gridPane.add(priceTextField, 1, 3);
            gridPane.add(categoryLabel, 0, 4);
            gridPane.add(categoryComboBox, 1, 4);

        /*----------------*/

        /*-------- Layout manager --------*/

        /*==== Starting Methods ====*/

            // Create connection with database //
            setConnectionToDatabase();

            // Load Information of Current Database Data
            loadInformationIntoTableview();

        /*========*/

        // VBox Root //
        VBox root = new VBox(10, gridPane, hBox, borderPane, productTableView);

        // Scene //
        Scene scene = new Scene(root);
        // Image //
        Image iconImage = new Image("file:.\\src\\main\\java\\exercise1\\selenemunoz_comp228lab5\\ProductIcon.png");

        // Stage //
        stage.getIcons().add(iconImage);
        stage.setScene(scene);
        stage.setTitle("Product Management");

        stage.setMinWidth(500);
        stage.setMinHeight(700);
        stage.setMaxWidth(1600);
        stage.setMaxHeight(700);

        stage.show();

        /*----------------*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}
