package map.kikourou;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class TrainingInfo {
	private static final String SEPARATOR = "|";
	private static final String NULL_STR = "null";
	private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm''ss''''");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("mm''ss''''");
	private SimpleDateFormat displayFormat = new SimpleDateFormat("hh'h'mm'mn'");
	
	public String id;
	public Date getDuration() {
		return duration;
	}


	public void setDuration(Date duration) {
		this.duration = duration;
	}


	private Date duration;
	private String time;
	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
		if ( time != null ) {
			try {
				if ( time.contains("h")) {
					time = time.replace('h', ':');
					duration = sdf.parse(time);
				}
				else {
					duration = sdf2.parse(time);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println("Parse:" + duration);
		}
		
	}


	public String distance;
	public String deniv;
	public String year;
	public String speed;
	public String pseudo;
	
	public String toString() {
		return "\"" + id  + SEPARATOR + pseudo + SEPARATOR + time + SEPARATOR + distance + SEPARATOR + deniv + SEPARATOR + year + SEPARATOR  + speed +"\","; 
	}
	
	public String toForum(int position, String baseUrl) {
		return  position + " [url=" + baseUrl + id + "]" + pseudo + "[/url]" + " : " +
				"[b]" + displayedDate() +  "[/b] - [b]" + distance + " km [/b] - [b]" + deniv + "m D+[/b]";		
	}
	
	private String displayedDate() {
		int day  =duration.getDay();
		Calendar cal =  Calendar.getInstance();
		cal.setTime(duration);
		int hours = (cal.get(Calendar.DAY_OF_YEAR) - 1) * 24 + duration.getHours();
		return "" + hours + "h" + duration.getMinutes() + "mn"; 
	}
	
	
	
	public void fromString(String str) {
		StringTokenizer tokenizer = new StringTokenizer(str, SEPARATOR);
		id = tokenizer.nextToken();
		pseudo = tokenizer.nextToken();
		String timeRead = tokenizer.nextToken();
		if ( !NULL_STR.equals(timeRead)) {
			setTime( timeRead  );
		}
		String distanceRead = tokenizer.nextToken();
		if ( !NULL_STR.equals(distanceRead)) {
			distance = distanceRead;
		}
		String denivRead = tokenizer.nextToken();
		if ( !NULL_STR.equals(deniv)) {
			deniv = denivRead;
		}
		String yearRead = tokenizer.nextToken();
		if ( !NULL_STR.equals(year)) {
			year = yearRead;
		}
		String speedRead = tokenizer.nextToken();
		if ( !NULL_STR.equals(speedRead)) {
			speed = speedRead;
		}
		
	}
}
