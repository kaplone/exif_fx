package walking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

import application.exif_GUI_controller;
import exif.NewName;
import exif.Rename;

public class Walk {
	
	private static boolean soletanche = false;
	
	private static exif_GUI_controller gui;

//	public static void main(String[] args) {
//		Path homeFolder = Paths.get("/mnt/nfs_nas/SATELLITE/SETE_QUAI_H/TIMELAPSES/Full/SOLETANCHE/2015-06-19");
//		FileVisitor<Path> fileVisitor = new FileSizeVisitor(new Long(50));
//		try {
//			Files.walkFileTree(homeFolder, fileVisitor);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	public static void walk(Path homeFolder, exif_GUI_controller exif_gui) throws FileNotFoundException {
		//Path homeFolder = Paths.get("/mnt/nfs_nas/SATELLITE/SETE_QUAI_H/TIMELAPSES/Full/SOLETANCHE/2015-06-19");
		
		gui = exif_gui;
		
		if (homeFolder.toString().contains("SOLETANCHE")){
			soletanche = true;
		}
		else if (homeFolder.toString().contains("LAFARGE")) {
			soletanche = false;
		}
		else throw new FileNotFoundException();
		
		FileVisitor<Path> fileVisitor = new FileSizeVisitor(new Long(50));
		try {
			Files.walkFileTree(homeFolder, fileVisitor);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
  
	
	static class FileSizeVisitor implements FileVisitor<Path> {
		
		File file;
		File file_o;
		
		FileInputStream fin;
		FileOutputStream fop;

		private Long size;

		public FileSizeVisitor(Long size) {
			this.size = size;
		}

		/**
		 * This is triggered before visiting a directory.
		 */
		@Override
		public FileVisitResult preVisitDirectory(Path path,
				BasicFileAttributes attrs) throws IOException {
			return FileVisitResult.CONTINUE;
		}

		/**
		 * This is triggered when we visit a file.
		 */
		@Override
		public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
				throws IOException {
/*
			long fileSize = attrs.size() / 1024;
			if (fileSize >= this.size) {
				System.out.println("File bigger than " + this.size + "KB  found: "
						+ path);
			}*/
			
			
			if(path.toString().toLowerCase().endsWith(".jpg")){
				
				file = path.toFile();
				fin = new FileInputStream(file);
				
				byte [] mille = new byte[(int) (file.length() - 8)];
			
				byte [] quatre = new byte[4];
				fin.read(quatre);

				byte [] bon_quatre = new byte [4];
				bon_quatre[0] = (byte)0xFF;
				bon_quatre[1] = (byte)0xD8;
				bon_quatre[2] = (byte)0xFF;
				bon_quatre[3] = (byte)0xE1;
				
//				System.out.println(String.format("%02X_%02X_%02X_%02X   %02X_%02X_%02X_%02X", bon_quatre[0],
//						                                                                      bon_quatre[1],
//						                                                                      bon_quatre[2],
//						                                                                      bon_quatre[3],
//						                                                                      quatre[0],
//						                                                                      quatre[1],
//						                                                                      quatre[2],
//						                                                                      quatre[3]));


				if (! Arrays.equals(bon_quatre, quatre)){
					
					System.out.println(path + " ___err___");
					
					file_o = new File("/home/autor/Desktop/Nouveau dossier/", file.getName());
					fop = new FileOutputStream(file_o);

				    fin.skip(4);
				    
				    fin.read(mille);
				    fop.write(mille);
				    
				    fop.flush();
				    fop.close();

				    copier(file_o.toPath());
				}else{
					System.out.println(path.toString().toLowerCase());
				    copier(path);
				}

			}
			

			return FileVisitResult.CONTINUE;
		}

		/**
		 * This is triggered if we cannot visit a Path We log the fact we cannot
		 * visit a specified path .
		 */
		@Override
		public FileVisitResult visitFileFailed(Path path, IOException exc)
				throws IOException {
			// We print the error
			System.err.println("ERROR: Cannot visit path: " + path);
			// We continue the folder walk
			return FileVisitResult.CONTINUE;
		}

		/**
		 * This is triggered after we finish visiting a specified folder.
		 */
		@Override
		public FileVisitResult postVisitDirectory(Path path, IOException exc)
				throws IOException {
			// We continue the folder walk
			return FileVisitResult.CONTINUE;
		}
		
		
		
		String base = "/mnt/nfs_nas/SATELLITE/SETE_QUAI_H/TIMELAPSES/Full/" + (soletanche ? "SOLETANCHE" : "LAFARGE") + "/RENOMME_temp";
		//String base = "/Volumes/NAS/SATELLITE/SETE_QUAI_H/TIMELAPSES/Full/" + (soletanche ? "SOLETANCHE" : "LAFARGE") + "/RENOMME_temp";
		
		void copier (Path p){
			String ps = NewName.newName(p.toString());
	    	Path vers = FileSystems.getDefault().getPath(base, ps);
	    	
	    	vers.toFile().mkdirs();
	        //overwrite existing file, if exists
	        CopyOption[] options = new CopyOption[]{
	          StandardCopyOption.REPLACE_EXISTING,
	          StandardCopyOption.COPY_ATTRIBUTES
	        }; 
	        
	        try {
				Files.copy(p, vers, options);
				gui.getAffListe().textProperty().set(gui.getAffListe().textProperty().get() + ps + "\n");
			} catch (IOException e) {
				// TODO Bloc catch généré automatiquement
				e.printStackTrace();
			}
	    }

	}

}
