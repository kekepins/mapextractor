package map.export;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JoinImage {
    public static void main(String args[])
    {   
        //String filename = System.getProperty("user.home")+File.separator;
        try {
            BufferedImage img1 = ImageIO.read(new File("C:\\temp\\tiles\\OSM\\OSM-2111-1472-12.png"));
            BufferedImage img2 = ImageIO.read(new File("C:\\temp\\tiles\\OSM\\OSM-2113-1468-12.png"));
            
            BufferedImage joinedImg = joinBufferedImage2(img1,img2);
            boolean success = ImageIO.write(joinedImg, "png", new File("C:\\temp\\joined"+ System.currentTimeMillis()+ ".png"));
            System.out.println("saved success? "+success);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static BufferedImage joinBufferedImage2(BufferedImage img1,BufferedImage img2) {
    	BufferedImage result = new BufferedImage (img1.getWidth(), img1.getHeight(), /*BufferedImage.TYPE_INT_ARGB*/ img1.getType());
    	Graphics g = result.getGraphics();
    	g.drawImage (img1, 0, 0, null);
    	return result;
    }
    /**
     * join two BufferedImage
     * you can add a orientation parameter to control direction
     * you can use a array to join more BufferedImage
     */

    public static BufferedImage joinBufferedImage(BufferedImage img1,BufferedImage img2) {

        //do some calculate first
        int offset  = 0;
        int wid = img1.getWidth()+img2.getWidth()+offset;
        int height = Math.max(img1.getHeight(),img2.getHeight())+offset;
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        //draw image
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, img1.getWidth()+offset, 0);
        g2.dispose();
        return newImage;
    }
}

