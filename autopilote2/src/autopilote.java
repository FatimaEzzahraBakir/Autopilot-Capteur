
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
public class autopilote {

	
    //lecture des données reçus
	public static JSONObject Lecture(String string) throws JSONException {
		final JSONObject obj = new JSONObject(string);
		return obj;
	}
	
    /********************************** les fonctions de traitement des capteurs ***********************************/
	
	
	public static void TraitementGyroscope(JSONObject objet) throws JSONException {
		JSONObject data=new JSONObject();
		data=objet.getJSONObject("data");
		double x,y,z;
		x=data.getDouble("x");
		y=data.getDouble("y");
		z=data.getDouble("z");
		System.out.println("attitude en x= "+x);
		if(x>0) {
			System.out.println("On donne une impulsion aux réacteurs arrières de " +(-x)+ " radians.");
		}
		else {
			System.out.println("On donne une impulsion aux réacteurs avants de " +(-x)+ " radians.");
		}
		System.out.println("attitude en y= "+y);
		if(y>0) {
			System.out.println("On effectue une rotation vers là gauche de " +(-y)+ " radians.");
		}
		else {
			System.out.println("On effectue une rotation vers là droite de " +(-y)+ " radians.");
		}
		System.out.println("attitude en z= "+z);
		if(z>0) {
			System.out.println("On donne une impulsion aux réacteurs droites de " +(-z)+ " radians.");
		}
		else {
			System.out.println("On donne une impulsion aux réacteurs gauches de " +(-z)+ " radians.");
		}
	}
	
    // capteur ultrason
	public static void TraitementRaspberryPi(JSONObject objet) throws JSONException {
		JSONObject data=new JSONObject();
		data=objet.getJSONObject("data");
		// on fixe une altitude
		final double altitude=3000;
		double y;
		y=data.getDouble("y");
		// on compare la valeur recuperée et la valeur fixée
		if(y<altitude){
			System.out.println("monter avec "+(altitude-y));
		}
		else{
			System.out.println("descendre avec "+(y-altitude));
		}
	}
	
	//capteur contact
	private static void TraitementArduino(JSONObject objet) throws JSONException {
		JSONObject data=new JSONObject();
		data=objet.getJSONObject("data");
		String faces="";
		faces=data.getString("faces");
		switch (faces){
		case "front"://reculer
			System.out.println("diminuer la vitesse des moteurs arrière et augmenter la vitesse des moteurs avant");
		case "behind"://avancer
			System.out.println("diminuer la vitesse des moteurs avant et augmenter la vitesse des moteurs arrière");
		case "left"://incliner vers la droite
			System.out.println("ralentir les moteurs de droite ");
		case "right"://incliner vers la gauche
			System.out.println("ralentir les moteurs de gauche");
		case "top"://descendre
			System.out.println("tourner les moteurs au même régime en augmentant la vitesse ");
		case "bottom"://monter
			System.out.println("tourner les moteurs au même régime en augmentant la vitesse");
		}
	}
	
	
	private static void TraitementAccelero(JSONObject objet) throws JSONException {
		JSONObject data=new JSONObject();
		data=objet.getJSONObject("data");
		double x,y,z;
		// vitesse fixée  m²/s
		double vitesseMax=500;
		x=data.getDouble("x");
		y=data.getDouble("y");
		z=data.getDouble("z");
		if(x<vitesseMax) {
			System.out.println("augmenter la vitesse en x avec"+(vitesseMax-x));
		}
		if(y<vitesseMax) {
			System.out.println("augmenter la vitesse en y avec"+(vitesseMax-y));
		}
		if(z<vitesseMax) {
			System.out.println("augmenter la vitesse en z avec"+(vitesseMax-z));
		}

	}


	public static void main(final String[] argv) throws JSONException, FileNotFoundException, InterruptedException {		
	
		JSONObject objet;
		ArrayList<String> message = new ArrayList<>();
		boolean sent = false;

		// A CHANGER POUR TESTER
		String hostName = "cenva";
		int serverPort = 4444;
		try{
			Socket s = new Socket(hostName, serverPort);


			while(!s.isClosed()){
				
				// liste des données recuperées
				message=Client.getData(sent, s);
				
				
				if(message.size()>0){
					System.out.println("Traitement des données:");
					System.out.println("");
					for(int i=0;i<message.size();i++) {
						objet=Lecture(message.get(i));
						switch (objet.getString("name")){
						case "gyroscope":
							System.out.println("-Gyroscope:");
							TraitementGyroscope(objet) ;
							System.out.println("");
							break;
						case "ultrason":
							System.out.println("-Capteur Ultrason:");
							TraitementRaspberryPi(objet) ;
							System.out.println("");
							break;
						case "contact":
							System.out.println("-Capteur Contact:");
							TraitementArduino(objet) ;
							System.out.println("");
							break;
						case "accelerometre":
							System.out.println("-Accelerométre:");
							TraitementAccelero(objet) ;
							System.out.println("");
							break;
						default:
							System.out.println("-Cas non traité");
							System.out.println("");
							break;
						}

					}
					sent=false;
				}
				System.out.println("==================================================");
				System.out.println("");
				Thread.sleep(3000);

			}
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: Address already in use");
		}

	}
}