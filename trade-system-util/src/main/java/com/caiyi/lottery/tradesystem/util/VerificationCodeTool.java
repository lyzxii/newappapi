package com.caiyi.lottery.tradesystem.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/** 
*  
* ClassName: VerificationCodeTool <br/> 
* Description: creat verification code <br/> 
* <br/> 
*  
* @author yptian@aliyun.com 
*  
* first made 
* @version 1.0.0<br/> 
* 
*/  
public class VerificationCodeTool {  
   //verification code image width  
   private static final int IMG_WIDTH=146;  
   //verification code image height  
   private static final int IMG_HEIGHT=30;  
   //The number of interference lines  
   private static final int DISTURB_LINE_SIZE = 15;  
   //generate a random number  
   private Random random = new Random();  
   //result  
   private int xyresult;  
   //result random string  
   private String randomString;

   private String number;
   //Here, must be java Unicode code  
   private static final String CVCNUMBERS = "\u96F6\u4E00\u4E8C\u4E09\u56DB\u4E94\u516D\u4E03\u516B\u4E5D\u5341\u4E58\u9664\u52A0\u51CF";  
   //Definition of drawings in the captcha characters font, font name, font style, font size  
   //static final font : In Chinese characters garbled  
 //  private final Font font = new Font("Times New Roman", Font.BOLD, 18);

    private final Font font = new Font("微软雅黑", Font.BOLD, 18);
   //data operator  
   private static final Map<String, Integer> OPMap = new HashMap<String, Integer>();  
     
   static{  
       OPMap.put("*", 11);  
       OPMap.put("/", 12);  
       OPMap.put("+", 13);  
       OPMap.put("-", 14);  
   }  
   /** 
    * The generation of image  verification code 
    * */  
   public BufferedImage drawVerificationCodeImage(){  
       //image  
       BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
       //In memory to create a brush  
       Graphics g = image.getGraphics();  
       //Set the brush color  
//     g.setColor(getRandomColor(200,250));  
       g.setColor(Color.WHITE);  
       //Fill the background color, using the current brush colour changing background color images  
       //The meaning of the four parameters respectively, starting x coordinates, starting y, width, height.  
       //image background  
       g.fillRect(0, 0, IMG_WIDTH, IMG_HEIGHT);  
       //Set the brush color  
       g.setColor(getRandomColor(200,250));  
       //image border  
       g.drawRect(0, 0, IMG_WIDTH-2, IMG_HEIGHT-2);  
         
       //Set disturb line color  
       g.setColor(getRandomColor(110, 133));  
       //Generate random interference lines  
       for(int i =0;i < DISTURB_LINE_SIZE; i++){  
           drawDisturbLine1(g);  
           drawDisturbLine2(g);  
       }  
       //Generate a random number, set return data  
       getRandomMathString();  
       //The generated random string used to save the system  
       StringBuffer logsu = new StringBuffer();  
       for(int j=0,k = randomString.length(); j < k; j++){  
         int chid = 0;  
         if(j==1){  
             chid = OPMap.get(String.valueOf(randomString.charAt(j)));  
         }else{  
             chid = Integer.parseInt(String.valueOf(randomString.charAt(j)));  
         }  
         String ch = String.valueOf(CVCNUMBERS.charAt(chid));  
         logsu.append(ch);  
         drawRandomString((Graphics2D)g,ch, j);  
       }  
       //= ?  
       drawRandomString((Graphics2D)g,"\u7B49\u4E8E\uFF1F", 3);  
       logsu.append("\u7B49\u4E8E \uFF1F");  
       randomString = logsu.toString();  
       //Release the brush object  
       g.dispose();  
       return image;  
   }  
   /** 
    * Get a random string 
    * */  
   private void getRandomMathString(){  
       //Randomly generated number 0 to 10  
       int xx = random.nextInt(10);  
       int yy = random.nextInt(10);  
       //save getRandomString  
       StringBuilder suChinese =  new StringBuilder();  
           //random 0,1,2  
           int Randomoperands = (int) Math.round(Math.random()*2);  
           //multiplication  
           if(Randomoperands ==0){  
               this.xyresult = yy * xx;  
//             suChinese.append(CNUMBERS[yy]);  
               suChinese.append(yy);  
               suChinese.append("*");  
               suChinese.append(xx);  
           //division, divisor cannot be zero, Be divisible  
           }else if(Randomoperands ==1){  
               if(!(xx==0) && yy%xx ==0){  
                   this.xyresult = yy/xx;  
                   suChinese.append(yy);  
                   suChinese.append("/");  
                   suChinese.append(xx);  
               }else{  
                   this.xyresult = yy + xx;  
                   suChinese.append(yy);  
                   suChinese.append("+");  
                   suChinese.append(xx);  
               }  
           //subtraction  
           }else if(Randomoperands ==2){  
                   this.xyresult = yy - xx;  
                   suChinese.append(yy);  
                   suChinese.append("-");  
                   suChinese.append(xx);  
           //add  
           }else{  
                   this.xyresult = yy + xx;  
                   suChinese.append(yy);  
                   suChinese.append("+");  
                   suChinese.append(xx);  
           }  
       this.randomString = suChinese.toString();  
   }  
   /** 
    * Draw a random string 
    * @param g Graphics 
    * @param randomString random string 
    * @param i the random number of characters 
    * */  
   public void drawRandomString(Graphics2D g,String randomvcch,int i){  
       //Set the string font style  
       g.setFont(font);
       //Set the color string
       int rc = random.nextInt(255);  
       int gc = random.nextInt(255);  
       int bc = random.nextInt(255);  
       g.setColor(new Color(rc, gc, bc));  
       //random string  
       //Set picture in the picture of the text on the x, y coordinates, random offset value  
       int x = random.nextInt(3);  
       int y = random.nextInt(2);  
       g.translate(x, y);  
       //Set the font rotation angle  
       int degree = new Random().nextInt() % 15;  
       //Positive point of view  
       g.rotate(degree * Math.PI / 180, 5+i*25, 20);  
       //Character spacing is set to 15 px  
       //Using the graphics context of the current font and color rendering by the specified string for a given text.  
       //The most on the left side of the baseline of the characters in the coordinate system of the graphics context (x, y) location  
       //str- to draw string.x - x coordinate.y - y coordinate.  
       g.drawString(randomvcch, 5+i*25, 20);  
       //Reverse Angle  
       g.rotate(-degree * Math.PI / 180, 5+i*25, 20);  
   }  
   /** 
    *Draw line interference  
    *@param g Graphics 
    * */  
   public void drawDisturbLine1(Graphics g){  
       int x1 = random.nextInt(IMG_WIDTH);  
       int y1 = random.nextInt(IMG_HEIGHT);  
       int x2 = random.nextInt(13);  
       int y2 = random.nextInt(15);  
       //x1 - The first point of the x coordinate.  
       //y1 - The first point of the y coordinate  
       //x2 - The second point of the x coordinate.  
       //y2 - The second point of the y coordinate.  
       //X1 and x2 is the starting point coordinates, x2 and y2 is end coordinates.  
       g.drawLine(x1, y1, x1 + x2, y1 + y2);  
   }  
     
   /** 
    *Draw line interference  
    *@param g Graphics 
    * */  
   public void drawDisturbLine2(Graphics g){  
       int x1 = random.nextInt(IMG_WIDTH);  
       int y1 = random.nextInt(IMG_HEIGHT);  
       int x2 = random.nextInt(13);  
       int y2 = random.nextInt(15);  
       //x1 - The first point of the x coordinate.  
       //y1 - The first point of the y coordinate  
       //x2 - The second point of the x coordinate.  
       //y2 - The second point of the y coordinate.  
       //X1 and x2 is the starting point coordinates, x2 and y2 is end coordinates.  
       g.drawLine(x1, y1, x1 - x2, y1 - y2);  
   }  
   /** 
    * For random color 
    * @param fc fc 
    * @param bc bc 
    * @return color random color 
    * */  
   public Color getRandomColor(int fc,int bc){  
       if(fc > 255){  
           fc = 255;  
       }  
       if(bc > 255){  
           bc = 255;  
       }  
       //Generate random RGB trichromatic  
       int r = fc+random.nextInt(bc -fc - 16);  
       int g = fc+random.nextInt(bc - fc - 14);  
       int b = fc+random.nextInt(bc - fc - 18);  
       return new Color(r, g, b);  
   }  
     
   /** 
    * xyresult.<br/> 
    * 
    * @return  the xyresult <br/> 
    *  
    */  
   public int getXyresult() {  
       return xyresult;  
   }  
     
   /** 
    * randomString.<br/> 
    * 
    * @return  the randomString <br/> 
    *  
    */  
   public String getRandomString() {  
       return randomString;  
   }

    public static void main(String[] args) throws Exception{
        VerificationCodeTool vct = new VerificationCodeTool();
        BufferedImage image = vct.drawVerificationCodeImage();
        ImageIO.write(image, "jpg", new File("/" + "def" + ".jpg"));
        System.out.println(image);
    }


    public BufferedImage getnumberImage(){
        int i = 70;
        int j = 30;
        BufferedImage localBufferedImage = new BufferedImage(i, j, 1);
        Graphics localGraphics = localBufferedImage.getGraphics();
        Random localRandom = new Random();
        localGraphics.setColor(_$1(200, 250));
        localGraphics.fillRect(0, 0, i, j);
        localGraphics.setFont(new Font("Times New Roman", 0, 25));
        localGraphics.setColor(_$1(160, 200));

        String str2 = "";

        for (int k = 0; k < 200; ++k) {
            int l = localRandom.nextInt(i);
            int i1 = localRandom.nextInt(j);
            int i2 = localRandom.nextInt(15);
            int i3 = localRandom.nextInt(15);
            localGraphics.drawLine(l, i1, l + i2, i1 + i3);
        }
        for (int l = 0; l < 4; ++l) {
            String str3 = String.valueOf(localRandom.nextInt(10));
            str2 = str2 + str3;
            localGraphics.setColor(new Color(20 + localRandom.nextInt(110),
                    20 + localRandom.nextInt(110), 20 + localRandom
                    .nextInt(110)));
            localGraphics.drawString(str3, 15 * l + 8, 25);
        }
        localGraphics.dispose();
        localBufferedImage.flush();
        number=str2;
        return localBufferedImage;
    }
    public String getNumber(){
        return number;
    }

    private Color _$1(int paramInt1, int paramInt2) {
        Random localRandom = new Random();
        if (paramInt1 > 255){
            paramInt1 = 255;}
        if (paramInt2 > 255){
            paramInt2 = 255;}
        int i = paramInt1 + localRandom.nextInt(paramInt2 - paramInt1);
        int j = paramInt1 + localRandom.nextInt(paramInt2 - paramInt1);
        int k = paramInt1 + localRandom.nextInt(paramInt2 - paramInt1);
        return new Color(i, j, k);
    }
     
}  