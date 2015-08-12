package exif;

import java.io.File;

public class Rename {
	
	public static void main(String[] args) {
		
		String nouveau; 
		
		File directory  = new File(args[0]);
		
		for (File i : directory.listFiles()){
			System.out.print(i);
			nouveau = NewName.newName(i.toString());
			i.renameTo(new File(args[0] + "/" + nouveau));
			System.out.println( " --> " + nouveau );
		}
		
	}

}
