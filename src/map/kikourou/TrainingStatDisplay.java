package map.kikourou;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TrainingStatDisplay {
	
	/*public static String[] res = new String[] { 
		"27308|ch'ti lillois d'vizille|null|null|null|null|null",
		"31809|nahu|null|null|null|null|null",
		"24559|mico34|null|null|null|null|null",
		"24276|FOREST Alex|null|null|null|null|null",
		"5|La Tortue|null|null|null|null|null",
		"26903|Stéphanos|null|null|null|null|null",
		"31546|anyah|null|null|null|null|null",
		"27366|Bacchus|null|null|null|null|null",
		"32859|campdedrôles|null|null|null|null|null",
		"30356|KrysDaix|07h00'02''|51.2|1065|2014|7.32",
		"31027|Sabzaina|null|null|null|null|null",
		"30626|spagh|null|null|null|null|null",
		"27799|KiKiKoureur|null|null|null|null|null",
		"16|LtBlueb|10h50'00''|112|1850|2014|10.33",
		"31267|ejouvin|null|null|null|null|null",
		"25738|mic31|null|null|null|null|null",
		"30097|bubulle|null|null|null|null|null",
		"27625|Eric Kikour Roux|null|null|null|null|null",
		"25392|jpoggio|null|null|null|null|null",
		"31966|ant09|05h37'50''|65.8|766|2014|11.69",
		"29875|DCA|null|null|null|null|null",
		"31602|Aurely42r|25h57'42''|227|3538|2014|8.74",
		"31899|Lanternerouge|null|null|null|null|null",
		"28391|TomTrailRunner|01h43'45''|5.1|90|2014|2.98",
		"31744|lemulot|null|null|null|null|null",
		"30592|RayaRun|null|null|null|null|null",
		"30395|sapi74|13h05'22''|152.3|2562|2014|11.63",
		"1|Mathias|11h31'34''|157.8|2747|2014|13.69",
		"31129|Insomniac Trailer|null|null|null|null|null",
		"26367|Zorglub74|28h59'00''|432|6330|2014|14.90",
		"25255|PhilKiKou|null|null|null|null|null",
		"26493|benoitb|null|null|null|null|null",
		"24942|Bikoon|null|null|null|null|null",
		"30129|meyzo|null|null|null|null|null",
		"24365|lauca|null|null|null|null|null",
		"29511|philippe.u|null|null|null|null|null",
		"31036|Arcelle|null|null|null|null|null",
		"31182|calpas|null|null|null|null|null",
		"26543|brague spirit|02h50'00''|30|120|2014|10.58",
		"30340|ArkaLuc|null|null|null|null|null",
		"29799|a_nne|null|null|null|null|null",
		"30990|SebKikourou|null|null|null|null|null",
		"25124|nicou2000|null|null|null|null|null",
		"85|le_kéké|47h18'19''|553|10606|2014|11.69",
		"27281|@lex_38|null|null|null|null|null",
		"25362|jepipote|null|null|null|null|null",
		"26717|trankilou|null|null|null|null|null",
		"28983|Free Wheelin' Nat|08h23'00''|46.1|2359|2014|5.50",
		"26614|Mamanpat|01h23'00''|11|180|2014|7.95",
		"30056|baboune59|null|null|null|null|null",
		"29514|janolesurfeur|null|null|null|null|null",
		"28473|Laurent39|null|null|null|null|null",
		"32514|Albacor38|null|null|null|null|null",
		"30420|freddo90|null|null|null|null|null",
		"26663|grumlie|null|null|null|null|null",
		"542|schnacka|null|null|null|null|null",
		"29242|Jean-Phi|null|null|null|null|null",
		"31774|begouz|null|null|null|null|null",
		"30845|Benman|18h41'35''|204.4|3799|2014|10.93",
		"29698|Patricia.B|null|null|null|null|null",
		"29585|vinch64|null|null|null|null|null",
		"26050|Rem|02h00'00''|8|200|2014|4.00",
		"27052|Bert|null|null|null|null|null",
		"24771|peky|null|null|null|null|null",
		"241|eric41|null|null|null|null|null",
		"33029|Renard74|null|null|null|null|null",
		"33834|thija_59|null|null|null|null|null",
		"32522|Krapo07|null|null|null|null|null",
		"27155|leeson|null|null|null|null|null",
		"29639|phoon|null|null|null|null|null",
		"32237|bledrunner|null|null|null|null|null",
		"30402|Reg|null|null|null|null|null",
		"25445|Franciss|null|null|null|null|null",
		"28874|Deudeu87|null|null|null|null|null",
		"29468|Ponpon|null|null|null|null|null",
		"26383|idec59|null|null|null|null|null",
		"31562|weeber|null|null|null|null|null",
		"32043|Mara31|null|null|null|null|null",
		"26813|BOUK honte-du-sport|null|null|null|null|null",
		"32532|the_hokeyeur|null|null|null|null|null",
		"32026|BrunoC38|null|null|null|null|null",
		"26230|fifidumou|null|null|null|null|null",
		"31272|Warthog|null|null|null|null|null",
		"31519|pitas|null|null|null|null|null"
	};*/
	public static String[] res = new String[] { 
	"27308|ch'ti lillois d'vizille|473h42'27''|10594.7|69548|2014|22.36",
	"31809|nahu|113h11'29''|2090.9|9643|2014|18.47",
	"24559|mico34|10h15'54''|152.1|559|2014|14.81",
	"24276|FOREST Alex|null|null|null|null|null",
	"5|La Tortue|338h30'00''|8666.5|40630|2014|25.60",
	"26903|Stéphanos|null|null|null|null|null",
	"31546|anyah|142h57'26''|2782|35993|2014|19.46",
	"26191|alain94|162h57'00''|2741|19460|2014|16.82",
	"27366|Bacchus|null|null|null|null|null",
	"32859|campdedrôles|null|null|null|null|null",
	"30356|KrysDaix|null|null|null|null|null",
	"31027|Sabzaina|03h43'33''|49.9|382|2014|13.40",
	"30626|spagh|158h00'56''|3241.5|49509|2014|20.51",
	"27799|KiKiKoureur|null|null|null|null|null",
	"16|LtBlueb|202h23'00''|4972.9|59241|2014|24.57",
	"31267|ejouvin|null|null|null|null|null",
	"25738|mic31|null|null|null|null|null",
	"30097|bubulle|null|null|null|null|null",
	"27625|Eric Kikour Roux|null|null|null|null|null",
	"25392|jpoggio|null|null|null|null|null",
	"31966|ant09|32h16'48''|845.2|9684|2014|26.18",
	"29875|DCA|282h53'00''|5987|17420|2014|21.16",
	"31602|Aurely42r|36h02'41''|744.7|9817|2014|20.66",
	"31899|Lanternerouge|02h03'35''|28.7|85|2014|13.94",
	"28391|TomTrailRunner|05h16'15''|78.4|278|2014|14.87",
	"31744|lemulot|03h02'27''|56.8|243|2014|18.67",
	"30592|RayaRun|20h22'37''|422.5|2097|2014|20.73",
	"30395|sapi74|null|null|null|null|null",
	"1|Mathias|184h14'42''|3365.1|39638|2014|18.26",
	"31129|Insomniac Trailer|05h10'10''|0| |2014|0.00",
	"26367|Zorglub74|82h13'00''|1969|19390|2014|23.94",
	"25255|PhilKiKou|309h58'35''|6464.2|101832|2014|20.85",
	"26493|benoitb|95h47'50''|2496.3|23069|2014|26.05",
	"24942|Bikoon|116h10'00''|1918| |2014|16.51",
	"30129|meyzo|124h57'00''|2449.1|15151|2014|19.60",
	"24365|lauca|null|null|null|null|null",
	"29511|philippe.u|30'00''|7.5|25|2014|15.00",
	"31036|Arcelle|135h49'00''|2120.4|10881|2014|15.61",
	"31182|calpas|166h28'24''|3468.7|35905|2014|20.83",
	"26543|brague spirit|50h09'26''|1148.4|15338|2014|22.89",
	"30340|ArkaLuc|14h13'18''|303| |2014|21.30",
	"29799|a_nne|52h36'16''|876.8|17295|2014|16.66",
	"30990|SebKikourou|null|null|null|null|null",
	"25124|nicou2000|160h55'00''|2363.5|15605|2014|14.68",
	"85|le_kéké|46h53'42''|936.3|16473|2014|19.96",
	"27281|@lex_38|02h23'00''|65.5|780|2014|27.48",
	"25362|jepipote|null|null|null|null|null",
	"26717|trankilou|34h56'10''|785.3|13455|2014|22.48",
	"28983|Free Wheelin' Nat|null|null|null|null|null",
	"26614|Mamanpat|06h30'00''|132.5| |2014|20.38",
	"30056|baboune59|103h43'00''|2212.5|9583|2014|21.33",
	"29514|janolesurfeur|04h38'00''|106.5|1170|2014|22.98",
	"28473|Laurent39|34h21'01''|685.6|3461|2014|19.96",
	"32514|Albacor38|18h26'18''|297.2|2212|2014|16.12",
	"30420|freddo90|39h21'51''|986.8|8312|2014|25.07",
	"26663|grumlie|null|null|null|null|null",
	"542|schnacka|01h00'00''|22| |2014|22.00",
	"29242|Jean-Phi|null|null|null|null|null",
	"31774|begouz|null|null|null|null|null",
	"30845|Benman|13h05'13''|302.4|1813|2014|23.10",
	"29698|Patricia.B|null|null|null|null|null",
	"29585|vinch64|null|null|null|null|null",
	"26050|Rem|null|null|null|null|null",
	"27052|Bert|72h19'00''|1987|17975|2014|27.47",
	"24771|peky|null|null|null|null|null",
	"241|eric41|null|null|null|null|null",
	"33029|Renard74|null|null|null|null|null",
	"33834|thija_59|null|null|null|null|null",
	"32522|Krapo07|36h29'00''|788.7|5680|2014|21.62",
	"27155|leeson|15h56'00''|307|350|2014|19.26",
	"29639|phoon|01h35'00''|18|85|2014|11.36",
	"32237|bledrunner|33'26''|7.9|67|2014|14.17",
	"30402|Reg|null|null|null|null|null",
	"25445|Franciss|60h21'48''|1066.8|8256|2014|17.67",
	"28874|Deudeu87|null|null|null|null|null",
	"29468|Ponpon|null|null|null|null|null",
	"26383|idec59|06h28'45''|89.9|89|2014|13.88",
	"31562|weeber|null|null|null|null|null",
	"32043|Mara31|03h55'33''|64|597|2014|16.32",
	"26813|BOUK honte-du-sport|null|null|null|null|null",
	"32532|the_hokeyeur|null|null|null|null|null",
	"32026|BrunoC38|null|null|null|null|null",
	"26230|fifidumou|01h55'00''|25|81|2014|13.04",
	"31272|Warthog|05h51'43''|60|230|2014|10.24",
	"31519|pitas|12h38'00''|345.5|1928|2014|27.34"};
	
	public static void main(String[] args) {
		
		List<TrainingInfo> listTraining = new ArrayList<TrainingInfo>();
		for (String val : res ) {
			TrainingInfo trainingInfo = new TrainingInfo();
			trainingInfo.fromString(val);
			if ( trainingInfo.distance != null) {
				//System.out.println( trainingInfo.toString() );
				listTraining.add(trainingInfo);
			}
		}
		
		Collections.sort(listTraining, new Comparator<TrainingInfo>() {
	        @Override
	        public int compare(TrainingInfo  training1, TrainingInfo  training2) {

	            return  training2.getDuration().compareTo(training1.getDuration());
	        }
	    });
		
		int place = 1;
		for (TrainingInfo trainingInfo : listTraining ) {
			//System.out.println( trainingInfo.toForum(place, "http://www.kikourou.net/entrainement/navigation.php?1&navmois=0&navannee=2014&navsport=13&groupe=annee&kikoureur=") );
			System.out.println( trainingInfo.toForum(place, "http://www.kikourou.net/entrainement/navigation.php?1&navmois=0&navannee=2014&navsport=3&groupe=annee&kikoureur=") );
			place ++;
		}
	}
	
	public void display(List<TrainingInfo> listTrainingInput, String sportId) {
		List<TrainingInfo> listTraining = new ArrayList<TrainingInfo>();
		for (TrainingInfo trainingInfo : listTrainingInput ) {
			
			if ( trainingInfo.distance != null) {
				listTraining.add(trainingInfo);
			}
		}
		
		Collections.sort(listTraining, new Comparator<TrainingInfo>() {
	        @Override
	        public int compare(TrainingInfo  training1, TrainingInfo  training2) {

	            return  training2.getDuration().compareTo(training1.getDuration());
	        }
	    });
		
		int place = 1;
		for (TrainingInfo trainingInfo : listTraining ) {
			//System.out.println( trainingInfo.toForum(place, "http://www.kikourou.net/entrainement/navigation.php?1&navmois=0&navannee=2014&navsport=13&groupe=annee&kikoureur=") );
			System.out.println( trainingInfo.toForum(place, "http://www.kikourou.net/entrainement/navigation.php?1&navmois=0&navannee=2014&navsport=3&groupe=annee&kikoureur=") );
			place ++;
		}
	}
}

