package extractimages;

import com.jfoenix.controls.JFXTextField;
import com.qoppa.pdf.PDFException;
import com.qoppa.pdf.resources.IImageResource;
import com.qoppa.pdf.resources.IResourceManager;
import com.qoppa.pdfProcess.PDFDocument;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

public class ViewController implements Initializable {

    @FXML
    private Button choosePdf;
    @FXML
    private Label pdfName;
    @FXML
    private ComboBox docType;
    @FXML
    private Button submit;
    @FXML
    private FlowPane imagesFlowPane;
    @FXML
    private JFXTextField name;
    @FXML
    private Label torenamefile;

    private int whichImage;
    private final ObservableList<String> images = FXCollections.observableArrayList();
    private final ObservableList<BufferedImage> BI = FXCollections.observableArrayList();
    @FXML
    private ComboBox signatureType;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        docType.getItems().removeAll(docType.getItems());
        docType.getItems().addAll("PNG", "JPEG", "GIF");
        docType.setPromptText("Please Select Document Type");
        signatureType.getItems().removeAll(signatureType.getItems());
        signatureType.getItems().addAll("Signature 1", "Signature 2", "Signature 3", "Signature 4", "Signature 5");
        signatureType.setPromptText("Please Select Signature Type");

    }

    @FXML
    private void choosePdfAction(ActionEvent event) {

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File To import");
            fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Document", "*.*"));

            File file = fileChooser.showOpenDialog(imagesFlowPane.getScene().getWindow());

            PDFDocument pdfDoc = new PDFDocument(file.getAbsolutePath(), null);
            IResourceManager resourceManager = pdfDoc.getResourceManager();

            // List the images from the document
            System.out.println("List images:");
            List<? extends IImageResource> imgList = resourceManager.listImages();

            File saveDir = new File(file.getParent());
            int count = 0;
            for (IImageResource imgRes : imgList) {
                String imgName = "image_" + (++count);

                //  imgs.add(imgName + ".png");
                // Save the image to a file
                BufferedImage bufferedImg = imgRes.getImage();
                ImageIO.write(bufferedImg, "png", new File(saveDir, imgName + ".png"));
                BI.add(bufferedImg);
                System.out.println(file.getParent() + "\\" + imgName + ".png");
                images.add(file.getParent() + "\\" + imgName + ".png");
                InputStream is = new FileInputStream(file.getParent() + "\\" + imgName + ".png");
                Image image = new Image(is);
                ImageView imageView = new ImageView(image);
                imageView.setX(10);
                imageView.setY(10);
                imageView.setFitWidth(300);
                imageView.setPreserveRatio(true);
                Button btn = new Button();
                btn.setGraphic(imageView);
                imagesFlowPane.getChildren().add(btn);

            }

            ObservableList<Node> ims = imagesFlowPane.getChildren();
            for (int i = 0; i < ims.size(); i++) {
                String image = images.get(i);
                whichImage = i;
                ims.get(i).setOnMouseClicked((javafx.scene.input.MouseEvent e) -> {
                    submit.setDisable(false);

                    torenamefile.setText(image);
                });
            }

            pdfName.setText(file.getName());
        } catch (PDFException | IOException ex) {
        }
        choosePdf.setDisable(true);
    }

    @FXML
    private void submitAction(ActionEvent event) throws IOException {
        String str = torenamefile.getText();
        File file = new File(str);
        if (name.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Pleas provide a name");
            alert.setTitle("Error");
            alert.showAndWait();
        } else {
            ImageIO.write(BI.get(whichImage), docType.getSelectionModel().getSelectedItem().toString().toLowerCase(), new File(file.getParent(), signatureType.getSelectionModel().getSelectedItem().toString().toLowerCase() + "-" + name.getText() + "." + docType.getSelectionModel().getSelectedItem().toString().toLowerCase()));

            name.setText("");
        docType.getSelectionModel().clearSelection();
        signatureType.getSelectionModel().clearSelection();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Image named and saved successfully");
            alert.setTitle("Success");
            alert.showAndWait();

        }
        System.out.println(str);
    }

    @FXML
    private void closeSystem(ActionEvent event) {
        imagesFlowPane.getChildren().removeAll();
        imagesFlowPane.getChildren().clear();
        deleteTempImages();
        System.exit(0);
    }

    private void deleteTempImages() {
        for (int i = 0; i < images.size(); i++) {
            File f = new File(images.get(i));
            System.out.println(f.getAbsolutePath());
            f.delete();
            System.out.println("Deleted");
        }
    }

    @FXML
    private void resetAction(ActionEvent event) {
        choosePdf.setDisable(false);
        pdfName.setText("Select Pdf");

        docType.getSelectionModel().clearSelection();
        signatureType.getSelectionModel().clearSelection();
        
        name.setText("");
        imagesFlowPane.getChildren().clear();
        deleteTempImages();
        images.clear();
    }

}
