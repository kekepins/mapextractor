package map.kikourou;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class KikourouTrainingStat {
	
	public static final String SPORT_CROSS = "34";
	public static final String SPORT_ORIENTATION = "2";
	public static final String SPORT_CAP = "1";
	public static final String SPORT_ROUTE_ULTRA = "23";
	public static final String SPORT_TRAIL = "24";
	public static final String SPORT_TRAIL_ULTRA = "25";
	public static final String SPORT_SKI_FOND = "13";
	public static final String SPORT_CYCL = "3";
	public static final String SPORT_VTT = "4";
	
	public static final String GROUP_ANNEE = "annee";
	
	public static String[] ids = 
		{
		"27308",
		"31809",
		"24559",
		"24276",
		"5",
		"26903",
		"31546",
		"26191",
		"27366",
		"32859",
		"30356",
		"31027",
		"30626",
		"27799",
		"16",
		"31267",
		"25738",
		"30097",
		"27625",
		"25392",
		"31966",
		"29875",
		"31602",
		"31899",
		"28391",
		"31744",
		"30592",
		"30395",
		"1",
		"31129",
		"26367",
		"25255",
		"26493",
		"24942",
		"30129",
		"24365",
		"29511",
		"31036",
		"31182",
		"26543",
		"30340",
		"29799",
		"30990",
		"25124",
		"85",
		"27281",
		"25362",
		"26717",
		"28983",
		"26614",
		"30056",
		"29514",
		"28473",
		"32514",
		"30420",
		"26663",
		"542",
		"29242",
		"31774",
		"30845",
		"29698",
		"29585",
		"26050",
		"27052",
		"24771",
		"241",
		"33029",
		"33834",
		"32522",
		"27155",
		"29639",
		"32237",
		"30402",
		"25445",
		"28874",
		"29468",
		"26383",
		"31562",
		"32043",
		"26813",
		"32532",
		"32026",
		"26230",
		"31272",
		"31519"
		
		};
	public static String[] pseudos = 
		{
		"ch'ti lillois d'vizille",
		"nahu",
		"mico34",
		"FOREST Alex",
		"La Tortue",
		"Stéphanos",
		"anyah",
		"alain94",
		"Bacchus",
		"campdedrôles",
		"KrysDaix",
		"Sabzaina",
		"spagh",
		"KiKiKoureur",
		"LtBlueb",
		"ejouvin",
		"mic31",
		"bubulle",
		"Eric Kikour Roux",
		"jpoggio",
		"ant09",
		"DCA",
		"Aurely42r",
		"Lanternerouge",
		"TomTrailRunner",
		"lemulot",
		"RayaRun",
		"sapi74",
		"Mathias",
		"Insomniac Trailer",
		"Zorglub74",
		"PhilKiKou",
		"benoitb",
		"Bikoon",
		"meyzo",
		"lauca",
		"philippe.u",
		"Arcelle",
		"calpas",
		"brague spirit",
		"ArkaLuc",
		"a_nne",
		"SebKikourou",
		"nicou2000",
		"le_kéké",
		"@lex_38",
		"jepipote",
		"trankilou",
		"Free Wheelin' Nat",
		"Mamanpat",
		"baboune59",
		"janolesurfeur",
		"Laurent39",
		"Albacor38",
		"freddo90",
		"grumlie",
		"schnacka",
		"Jean-Phi",
		"begouz",
		"Benman",
		"Patricia.B",
		"vinch64",
		"Rem",
		"Bert",
		"peky",
		"eric41",
		"Renard74",
		"thija_59",
		"Krapo07",
		"leeson",
		"phoon",
		"bledrunner",
		"Reg",
		"Franciss",
		"Deudeu87",
		"Ponpon",
		"idec59",
		"weeber",
		"Mara31",
		"BOUK honte-du-sport",
		"the_hokeyeur",
		"BrunoC38",
		"fifidumou",
		"Warthog",
		"pitas",
		};

	
	public TrainingInfo displayStat(String kikId, String pseudo, String sportId) throws Exception {
		
		//String url = getUrl( kikId, "0", "2014", SPORT_SKI_FOND, GROUP_ANNEE);
		//System.out.println("Url:" + url);
		String url = getUrl( kikId, "0", "2014", sportId, GROUP_ANNEE);
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		 
		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		//System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		//print result
		//System.out.println(response.toString());
		//String response = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"fr\" lang=\"fr\"><head><title>Parcourir un carnet d'entrainement - Kikourou</title><meta name=\"keywords\" content=\"Kikourou, sport, sports, sportif, sportifs, communaut&eacute; de sportifs, course &agrave; pied, running, jogging, endurance, calendrier, carnets, entra&icirc;nement, &eacute;v&egrave;nement sportif, trail, raids multisports, triathlon, v&eacute;lo, cyclisme, VTT, course d\'orientation, ultra, marathon, CRs, r&eacute;cit, r&eacute;cits, r&eacute;sultats, parcours, GPS, trac&eacute;, trace, classement, challenge, photos, vid&eacute;os, blogs, blog\" /><meta name=\"description\" content=\"Communaut&eacute; de sports d'endurance : calendrier de courses, r&eacute;cits, parcours, trac&eacute;s GPS, carnets d'entra&icirc;nement, r&eacute;sultats (course &agrave; pied, trail, ultra, triathlon, v&eacute;lo, VTT, course d'orientation, raids multisports)\" /><meta http-equiv=\"content-type\" content=\"text/html; charset=iso-8859-1\" /><meta http-equiv=\"content-language\" content=\"fr\" /><meta name=\"author\" content=\"Kikourou.net\" /><meta name=\"generator\" content=\"Kikourou.net\" /><link rel=\"stylesheet\" media=\"screen\" href=\"/css/kikourou.css\" type=\"text/css\" /><link rel=\"shortcut icon\" href=\"/favicon.ico\" /><script type=\"text/javascript\" src=\"http://img.metaffiliation.com/na/na/res/trk/script.js\" ></script><!--[if IE]><script type=\"text/javascript\"><!--sfHover = function() {	var sfEls = document.getElementById(\"nav\").getElementsByTagName(\"LI\");	for (var i=0; i<sfEls.length; i++) {		sfEls[i].onmouseover=function() {			this.className+=\" sfhover\";		}		sfEls[i].onmouseout=function() {			this.className=this.className.replace(new RegExp(\" sfhover\\b\"), \"\");		}	}}if (window.attachEvent) window.attachEvent(\"onload\", sfHover);//--></script><![endif]--><link rel=\"stylesheet\" media=\"screen\" href=\"/css/table.css\" type=\"text/css\" /></head><body><div id=\"page\"><div id=\"header\"><a href=\"/\"><img src=\"/images/entete.gif\" width=\"413\" height=\"140\" alt=\"Kikourou est un site de course a pied, trail, marathon\" /></a><ul id=\"menu_login\">  <li><a href=\"/forum/ucp.php?mode=login\">Connexion</a></li>  <li><a href=\"/forum/ucp.php?mode=register\">Inscription</a></li>  <li><a href=\"/kikourou/contact.php\">Contact</a></li>  <li><a href=\"/aide/\">Aide</a></li></ul><div id=\"titresite\">Communaut&eacute; des sports d'endurance</div><ul class=\"onglets\" id=\"nav\"><li><a href=\"/\">Accueil</a>	<ul>		<li><a href=\"http://m.kikourou.net/\">Version mobile</a></li>		<li><a href=\"/kikourou/pourquoi.php\">Pr&eacute;sentation</a></li>		<li><a href=\"/kikourou/equipe.php\">Qui sommes nous ?</a></li>		<li><a href=\"/kikourou/participer.php\">Participer</a></li>		<li><a href=\"/kikourou/don.php\">Faire un don</a></li>		<li><a href=\"/forum/viewforum.php?f=17\">Nouveaut&eacute;s</a></li>		<li><a href=\"/kikourou/partage.php\">Kikouro&ugrave; partage !</a></li>		<li><a href=\"/kikourou/pub.php\">Logos et banni&egrave;res</a></li>		<li><a href=\"/courirpourlesautres/\">Courir pour les autres</a></li>		<li><a href=\"/tableaudebord.php\">Tableau de bord</a></li>	</ul></li><li><a href=\"/actualite.php\">Actualit&eacute;</a>	<ul>		<li><a href=\"/recits/\">R&eacute;cits</a></li>		<li><a href=\"/resultats/\">R&eacute;sultats</a></li>    <li><a href=\"/photos/\">Photos</a></li>    <li><a href=\"/videos/\">Vid&eacute;os</a></li>		<li><a href=\"/calendrier/liste-avis.php\">Avis sur les courses</a></li>		<li><a href=\"/calendrier/live.php\">Live</a></li>  </ul></li><li class=\"topfocus\"><a href=\"/lesport.php\">Le sport</a>	<ul>		<li><a href=\"/calendrier/\">Calendrier</a></li>		<li><a href=\"/entrainement/\">Carnets d'entra&icirc;nement</a></li>		<li><a href=\"/parcours/?init=1\">Parcours</a></li>		<li><a href=\"/parcours/traces-gps.php\">Recherche GPS</a></li>	</ul></li><li><a href=\"/communaute/\">Communaut&eacute;</a>	<ul>	<li><a href=\"/communaute/\">Les membres</a></li>	<li><a href=\"/groupes/\">Groupes</a></li>	<li><a href=\"/records/\">Records</a></li>  <li><a href=\"/calendrier/navigation.php?init=1&amp;filtrereskik=1&amp;affreskik=1&amp;tri=2\">R&eacute;sultats perso</a></li>	<li><a href=\"/kivaou/\">Kivao&ugrave; ?</a></li>	</ul></li></li><li><a href=\"/forum/viewtopic.php?f=17&t=24693\">Portails</a><ul>  <li><a href=\"http://paris.kikourou.net/\">Ile de France</a></li>  <li><a href=\"http://nord.kikourou.net/\">Nord</a></li>  <li><a href=\"http://savoie.kikourou.net/\">Savoie</a></li>  <li><a href=\"http://ain.kikourou.net/\">Ain</a></li>  <li><a href=\"http://lyon.kikourou.net/\">Lyon</a></li>  <li><a href=\"http://grenoble.kikourou.net/\">Grenoble</a></li>   <li><a href=\"http://13.kikourou.net/\">Bouches du Rh&ocirc;ne</a></li>  </ul></li><li><a href=\"/blog/\">Blogs</a></li><li><a href=\"/forum/\">Forums</a>  <ul>  <li><a href=\"/forum/charte.php\">Charte du forum</a></li>  <li><a href=\"/forum/search.php\">Rechercher</a></li>  <li><a href=\"/forum/faq.php\">FAQ</a></li>  <li><a href=\"/forum/motscles.php\">Mots cl&eacute;</a></li>  <li><a href=\"/forum/map.php\">Par une carte</a></li>  <li><a href=\"/forum/viewforum.php?f=19\">Courir en r&eacute;gion</a></li>  <li><a href=\"/forum/viewforum.php?f=21\">Course &agrave; pied</a></li>  <li><a href=\"/forum/viewforum.php?f=22\">Mat&eacute;riel</a></li>  <li><a href=\"/forum/viewforum.php?f=8\">Bistro</a></li>  <li><a href=\"/forum/viewforum.php?f=40\">Comp&eacute;t</a></li>  <li><a href=\"/forum/\">Tous les forums</a></li>  </ul></li><li><a href=\"/boutique/\">Boutique</a></li><li><a href=\"/association/\">Association</a>	<ul>		<li><a href=\"/association/\">Pr&eacute;sentation</a></li>		<li><a href=\"/association/statuts.php\">Statuts</a></li>		<li><a href=\"/association/lettre.php\">Lettre d'information</a></li>		<li><a href=\"/association/adhesion.php\">Adh&eacute;sion</a></li>		<li><a href=\"/association/ca.php\">Conseil d'Administration</a></li>		<li><a href=\"/association/adherents.php\">Adh&eacute;rents</a></li>		<li><a href=\"/association/bilan.php\">Bilan</a></li>		<li><a href=\"/forum/viewtopic.php?t=12723\">R&eacute;server la banderole</a></li>		<li><a href=\"/calendrier/navigation.php?init=1&amp;affkivaou=1&amp;kikoureur=29990\">Agenda Banderole</a></li>		<li><a href=\"/courirpourlesautres/aunomdanna.php\">ARettToiPourCourir</a></li>			</ul></li></ul></div><div id=\"contenuprincipal\"><table width=\"100%\"><tr><td align=\"center\">        <h2>Les entrainements de le_kéké en 2014</h2><table width=\"100%\" align=\"center\">  <tr>     <td width=\"35%\" align=\"left\">Année :             <a href=\"navigation.php?navannee=2013\">2013</a> -       2014      - <a href=\"navigation.php?navannee=2015\">2015</a>             (<a href=\"navigation.php?navannee=0\">Toutes</a>)             <br>      Mois :             <a href=\"navigation.php?navmois=12\">Décembre</a>       (tous)             <br>    Ou saison :             <a href=\"navigation.php?navsaison=2014\">2014/2015</a>       (toutes)       <br>Ou  :       <a href=\"navigation.php?nav1an=1\">depuis 1 an</a>	      <br>       <form action=\"navigation.php\" method=\"post\">        Sport : <select name=\"navsport\" id=\"navsport\"><option value=\"-1\">Tous</option><option value=\"19\">Adresse</option><option value=\"35\">Alpinisme</option><option value=\"8\">Aquathlon</option><option value=\"27\">Athlétisme</option><option value=\"21\">Autre</option><option value=\"32\">Badminton</option><option value=\"1\">Course à Pied</option><option value=\"23\">Course à Pied (route, ultra)</option><option value=\"24\">Course à Pied (trail)</option><option value=\"25\">Course à Pied (trail, ultra)</option><option value=\"100\">les 4 types de course à pied</option><option value=\"2\">Course d'Orientation</option><option value=\"34\">Cross</option><option value=\"3\">Cyclisme</option><option value=\"9\">Duathlon</option><option value=\"11\">Eaux vives</option><option value=\"30\">Etirements</option><option value=\"20\">Grimpe</option><option value=\"37\">Home Trainer</option><option value=\"28\">Marche</option><option value=\"6\">Multisports</option><option value=\"29\">Musculation</option><option value=\"10\">Natation</option><option value=\"12\">Plouf</option><option value=\"14\">Raquettes</option><option value=\"17\">Raquettes'O</option><option value=\"33\">Renforcement musculaire</option><option value=\"5\">Roller</option><option value=\"18\">Run and Bike</option><option value=\"36\">Ski</option><option selected=\"selected\" value=\"13\">Ski de fond</option><option value=\"31\">Ski de rando</option><option value=\"15\">Ski'O</option><option value=\"22\">Sport collectif</option><option value=\"7\">Triathlon</option><option value=\"4\">VTT</option><option value=\"16\">VTT'O</option></select>         <input name=\"submit\" type=\"submit\" value=\"Changer\">      </form></td>    <td width=\"65%\" align=\"left\">            Grouper par :             <a href=\"navigation.php?groupe=semaine\">semaine</a>,       <a href=\"navigation.php?groupe=semainesport\">semaine-sport</a>,       <a href=\"navigation.php?groupe=mois\">mois</a>,       <a href=\"navigation.php?groupe=moissport\">mois-sport</a>,       <a href=\"navigation.php?groupe=anneesport\">année-sport</a>, <a href=\"navigation.php?groupe=0\">tout afficher</a><br>Voir le <a href=\"navigation.php?type=graphique\">graphique</a>.       <br>afficher <a href=\"navigation.php?aff=2\">(le nombre de séances)</a>           </td>  </tr>  <tr>     <td align=\"center\"><p>              </td>  </tr></table><table class=\"calendrier\" style=\"width:900px\">  <tr>         <th> <a href=\"navigation.php?tri=1\"> Date</a></th>        <th width=\"50\"><a href=\"navigation.php?tri=4\">Durée</a></th>    <th><a href=\"navigation.php?tri=6\">Distance</a><br>      (km)</th>    <th><a href=\"navigation.php?tri=16\">Vitesse</a><br>(km/h)<br><a href=\"navigation.php?affvitesse=1\">modif</a></th>    <th><a href=\"navigation.php?tri=10\">D+</a></th>        <th>FC<br><a href=\"navigation.php?tri=12\">moy</a>/<a href=\"navigation.php?tri=14\">max</a></th>    </tr>  <tr>         <td align=\"center\" nowrap>2014</td>    <td width=\"50\" align=\"center\">47h18'19''</td>    <td align=\"center\">553</td>    <td align=\"center\">11.69</td>    <td align=\"center\">10606</td>    <td align=\"center\">-/0</td>      </tr>    <tr>     <th colspan=\"1\" align=\"center\">       <font size=\"-1\">Moyenne par s&eacute;ance</font></th>        <td align=\"center\">01h49'09''</td>    <td align=\"center\">21.2 km</td>    <td align=\"center\">11.69 km/h</td>    <td align=\"center\">407.9 D+</td>        <td align=\"center\">0/0</td>      </tr>    <tr>     <th colspan=\"1\" align=\"center\">       <font size=\"-1\">Moyenne par jour 365 jours      </font></th>        <td align=\"center\">07'46''</td>    <td align=\"center\">1.5 km</td>    <td>&nbsp;</td>    <td align=\"center\">29       D+</td>        <td width=\"50\">&nbsp;</td>  </tr>      <tr>     <th colspan=\"1\" align=\"center\">       <font size=\"-1\">Moyenne par semaine 52 semaines      </font></th>        <td align=\"center\">54'34''</td>    <td align=\"center\">10.6 kms</td>    <td>&nbsp;</td>    <td align=\"center\">203       D+</td>        <td width=\"50\">&nbsp;</td>  </tr>      <tr>     <th colspan=\"1\" align=\"center\">       <font size=\"-1\">Moyenne par mois 12 mois</font></th>        <td align=\"center\">03h56'31''</td>    <td align=\"center\">46       kms</td>    <td>&nbsp;</td>    <td align=\"center\">883       D+</td>        <td width=\"50\">&nbsp;</td>  </tr>      <tr>     <th colspan=\"1\" align=\"center\">       <font size=\"-1\">Moyenne par année 1 an</font></th>        <td align=\"center\">47h18'19''</td>    <td align=\"center\">553       kms</td>    <td>&nbsp;</td>    <td align=\"center\">10606       D+</td>        <td width=\"50\">&nbsp;</td>  </tr>      <tr>     <th colspan=\"1\" align=\"center\">       <font size=\"-1\"> Total</font></th>        <td align=\"center\">47h18'19''</td>    <td align=\"center\">553 kms       <br><font size=\"-2\">(0 en course)</a>    </td>    <td>&nbsp;</td>    <td align=\"center\">10606 D+</td>        <td width=\"50\">&nbsp;</td>      </tr>  </table><p>Note : que pensez vous des carnets d'entrainement sur Kikouro&ugrave; ? Vous avez besoin de plus de d&eacute;tails dans la description de vos s&eacute;ances ? Vous aimeriez une pr&eacute;sentation plus compl&egrave;te de votre carnet ? Vous avez des id&eacute;es pour am&eacute;liorer tout &ccedil;a ? N'h&eacute;sitez pas &agrave; <a href=\"mailto:webmaster@kikourou.net\">envoyer un message</a> ! </p><p><a href=\"navigation.php?1&kikoureur=85&navannee=2014&navsport=13&tri=2&navdetails=1&type=1&groupe=annee\">Adresse de cette page avec cette configuration</a></p>  </td></tr></table></div><div id=\"pieddepage\"><p><a href=\"/\" title=\"Course a pied, marathon, trail\">Accueil de kikouro&ugrave;</a> - <a href=\"#\">Haut de page</a> - <a href=\"/aide/\">Aide</a> - <a href=\"/kikourou/contact.php\">Contact</a>  - <a href=\"/kikourou/mentionslegales.php\">Mentions l&eacute;gales</a> - <a href=\"http://m.kikourou.net/\">Version mobile</a> - 0.06 sec<br />Kikouro&ugrave; est un site de course &agrave; pied, trail, marathon. Vous trouvez des r&eacute;cits, r&eacute;sultats, photos, vid&eacute;os de course, un calendrier, un forum... Bonne visite !</p><script src=\"http://www.google-analytics.com/urchin.js\" type=\"text/javascript\"></script><script type=\"text/javascript\">_uacct = \"UA-269232-1\";urchinTracker();</script></div></div></body></html>";
		
		Document doc = Jsoup.parse(response.toString());
		Elements trs = doc.select("tr");
		TrainingInfo trainingInfo = new TrainingInfo();
		trainingInfo.id = kikId;
		trainingInfo.pseudo = pseudo;
		
		int idx = 0;
		for ( Element element : trs ) {
			idx ++;
			if ( idx == 5 ) {
				
				Elements tds = element.select("td");
				int idxTrain = 0;
				
				for ( Element elementTd : tds ) {
					//System.out.println( elementTd.text());
					if ( idxTrain == 0 ) {
						trainingInfo.year = elementTd.text();
					}
					else if ( idxTrain == 1 ) {
						trainingInfo.setTime(elementTd.text());
					}
					else if ( idxTrain == 2 ) {
						trainingInfo.distance = elementTd.text();
					}
					else if ( idxTrain == 3 ) {
						trainingInfo.speed = elementTd.text();
					}
					else if ( idxTrain == 4 ) {
						trainingInfo.deniv = elementTd.text();
					}


					idxTrain++;
				}
			}
			
		}
		
		System.out.println(trainingInfo);
		return trainingInfo;
	}
	
	public String getUrl(String kikouId, String month, String year, String sportId, String group ) {
		String url = "http://www.kikourou.net/entrainement/navigation.php?1";
		if (kikouId != null) {
			url += "&kikoureur=" + kikouId;
		}
		
		if ( month != null ) {
			url += "&navmois=" + month;
		}
		
		if ( year != null ) {
			url += "&navannee=" + year;
		}

		if ( sportId != null ) {
			url += "&navsport=" + sportId;
		}
		
		if ( group != null ) {
			url += "&groupe=" + group;
		}

		
		return url;
		
	}
	
	public static void main(String[] args) throws Exception {
		
		//System.setProperty("http.proxyHost", "websurfing1-tin1.esi.adp.com");
	    //System.setProperty("http.proxyPort", "8080");

		//KikourouTrainingStat stat = new KikourouTrainingStat();
		//stat.displayStat("85", "le_kéké");
		//stat.displayStat(ids[0], pseudos[0]);
		System.out.println(ids.length);
		System.out.println(pseudos.length);
		List<TrainingInfo> traingStats = new ArrayList<TrainingInfo>();
		
		int idx = 0;
		for (String id : ids) {
			//System.out.println(ids[idx] + ":" + pseudos[idx]);
			KikourouTrainingStat stat = new KikourouTrainingStat();
			traingStats.add( stat.displayStat(ids[idx], pseudos[idx], SPORT_CYCL));
			idx++;
		}
		
		TrainingStatDisplay display = new TrainingStatDisplay();
		display.display(traingStats, SPORT_CYCL);
	}
}

