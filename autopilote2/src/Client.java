
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

import java.io.*;

public class Client {
	// le message a envoyé au bus 
	private static String message="{" 
			+ "      \"name\": \"autopilote\","  
			+ "      \"id\": \"1\","    
			+ "      \"data\": " 
			+ "    {" 
			+ "      \"action\": \"THROTTLE\"," 
			+ "      \"value\": \"0\","          
			+ "    }," 
			+ "}";
	//la lecture du message reçu en utlisant un delimiteur "\n"
	private static String getCutString(InputStream message) throws IOException{
		Scanner sc = new Scanner(message, "UTF-8");
		sc.useDelimiter("\n");
		if(message.available() > 0){
			if(sc.hasNextLine()){
				String str = sc.nextLine();
				return str;
			}
			else
			{
				return "";
			}
		}
		else{
			return "";
		}
	}

	// sent : true si on recoit un message, false sinon
	public static ArrayList<String> getData(boolean sent, Socket s) throws InterruptedException{

		ArrayList<String> data=new ArrayList<>();

		try {
			OutputStream os = s.getOutputStream();
			
            // l'envoie du message 
			os.write((message + "\n").getBytes());
			os.flush();
			
			
			//réception des données 
			InputStream in = s.getInputStream();

			s.setSoTimeout(10000);		
            
			//on ajoute les données recupérées dans la liste 
			for(int i = 0 ; i < 4 ; i++){
				// donnée recupérée
				String currLine = getCutString(in);
				// on verifie si currLine est different du vide ou si egal au données deja recupérées 
				if(currLine != "" && !data.contains(currLine))
					data.add(currLine);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: Address already in use");
		}

		System.out.println("Nombre de messages récupérés: " + data.size());

		if(data.size()>0){
			System.out.println("");
			System.out.println("Liste des structures de données récupérées:");
			sent=true;
			
			for(int i=0; i<data.size(); i++){
				System.out.println(data.get(i));
			}
		}
		System.out.println("");

		return data;
	}

}
