package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import walking.Walk;

public class exif_GUI_controller implements Initializable {
	
	@FXML
	private Button rep_button;
	@FXML
	private Button enc_button;
	
	@FXML
	private Label aff_liste;
	@FXML
	private Label aff_taille;
	
	@FXML
	private ProgressBar progressbar;
	
	private File repertoire;
	private Boolean soletanche;
	private String temporaire;
	private String renomme;
	private String preview;
	
	private String mogrify;
	
	@FXML
	protected void onRepBtn(){
		repertoire = chooseDir();
		//aff_liste.textProperty().set(Arrays.asList(repertoire.listFiles()).stream().map(a -> a.toString()).collect(Collectors.joining("\n")));

	}
	
    protected File chooseDir(){
		
		Stage newStage = new Stage();
		
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("sélectionner le répertoire source");
		File selectedFile = dirChooser.showDialog(newStage);
		if (selectedFile != null) {
			rep_button.setText(selectedFile.toString());
			 return selectedFile;
		}
		else {
			 return (File) null;
		}	
	}
    
    @FXML
	protected void onEncBtn(){
    	
    	System.out.println("\ndébut conversion des noms : \n\t" + repertoire.toPath().toString() + "\n");
    	
		try {
			Walk.walk(repertoire.toPath(), this);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//aff_liste.textProperty().set(Arrays.asList(repertoire.listFiles()).stream().map(a -> a.toString()).collect(Collectors.joining("\n")));
		//aff_liste.textProperty().set(repertoire.toString());
		
		System.out.println("\nfin conversion des noms : \n\t" + repertoire.toPath().toString() + "\n");
		
		System.out.println("\ndébut conversion vers preview : \n\t" + repertoire.toPath().toString() + "\n");
		
		if (repertoire.toString().contains("SOLETANCHE")){
			soletanche = true;
		}
		else if (repertoire.toString().contains("LAFARGE")) {
			soletanche = false;
		}
		
		System.out.println(soletanche ? "___SOLETANCHE___" : "___LAFARGE___");
		
		temporaire = "/mnt/nfs_nas/SATELLITE/SETE_QUAI_H/TIMELAPSES/Full/" + (soletanche ? "SOLETANCHE" : "LAFARGE") + "/RENOMME_temp";
		renomme = "/mnt/nfs_nas/SATELLITE/SETE_QUAI_H/TIMELAPSES/Full/" + (soletanche ? "SOLETANCHE" : "LAFARGE") + "/RENOMME";
		preview = "/mnt/nfs_nas/SATELLITE/SETE_QUAI_H/TIMELAPSES/Preview/" + (soletanche ? "soletanche" : "lafarge");
        mogrify = "mogrify";		
//		temporaire = "/Volumes/NAS/SATELLITE/SETE_QUAI_H/TIMELAPSES/Full/" + (soletanche ? "SOLETANCHE" : "LAFARGE") + "/RENOMME_temp";
//		renomme = "/Volumes/NAS/SATELLITE/SETE_QUAI_H/TIMELAPSES/Full/" + (soletanche ? "SOLETANCHE" : "LAFARGE") + "/RENOMME";
//		preview = "/Volumes/NAS/SATELLITE/SETE_QUAI_H/TIMELAPSES/Preview/" + (soletanche ? "soletanche" : "lafarge");
//		mogrify = "/opt/ImageMagick/bin/mogrify";	
		
		try {
			
			ProcessBuilder processBuilder= new ProcessBuilder(mogrify, "-resize",  "25%", "-path", preview, String.format("%s/*.jpg", temporaire));
//			
//			File log = new File("/home/autor/Desktop/log_resize.txt");
//			processBuilder.redirectErrorStream(true);
//			processBuilder.redirectOutput(Redirect.appendTo(log));
			
			Process p = processBuilder.start(); 
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\nfin conversion vers preview : \n\t" + preview + "\n");
		
		System.out.println("\ndébut copie vers RENOMME : \n\t" + temporaire + "\n");
		
		try {
			ProcessBuilder processBuilder2 = new ProcessBuilder("sh","-c", String.format("mv %s/*.jpg %s", temporaire, renomme));
			
//			File log = new File("/home/autor/Desktop/log_preview.txt");
//			processBuilder2.redirectErrorStream(true);
//			processBuilder2.redirectOutput(Redirect.appendTo(log));
			
			Process p2 = processBuilder2.start(); 
			try {
				p2.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\nfin copie vers RENOMME : \n\t" + temporaire + "\n");

	}
    
    public Label getAffListe(){
    	return aff_liste;
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
