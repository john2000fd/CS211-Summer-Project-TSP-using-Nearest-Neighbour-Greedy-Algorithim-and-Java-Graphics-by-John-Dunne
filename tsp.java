import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;                                      
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;                             
import java.awt.*;
import java.util.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;


import java.io.*;
import java.text.*;
import java.math.*;


 /* CS211 Summer Project TSP using Nearest Neighbour Greedy Algorithim and Java Graphics by John Dunne 19450974*/

public class tsp                       //TSP class                           
{    
    private static UI ui;          //variable for our UI
    public static void main(String[] args)        //main method
    {
       ui = new UI();   //make our UI to run the game 
    }
}



class algo                            //main class
{ 
    private static String bestOrderRoute = ""; //our final answer 
    private static String ordernum;     //variables for each order 
    private static String northgps;
    private static String westgps;
    private static double apacheNorth;
    private static double apacheWest;
    private static int length;
    ArrayList<String> route;        //arraylist for our orders
    



    public algo(String orders)  //our TSP NN algorithim func called with the submit button in UI
    {

        route = new ArrayList<String>();    //our arrarlist to hold our route
        String [] orderSplit1 = orders.split("\\n"); //split up the string first into each line, AKA every order
        length = orderSplit1.length;

        String [] deliverynum = new String [orderSplit1.length];    //arrays for each part of the order 
        String []nor = new String [orderSplit1.length];  
        String []wes = new String [orderSplit1.length]; 
        String r; //our string 

        for(int h = 0; h < orderSplit1.length; h++) //then run through each string in the split array, splitting into each part of the orders
        {
            r = orderSplit1[h];
            String orderSplit2 [] = r.split(","); //now we have an array with all the individual parts of one order
            ordernum = orderSplit2[0];       
            deliverynum[h] = ordernum;     //add info to equivalent arrays 
            northgps = orderSplit2[3];
            nor[h] = northgps;
            westgps = orderSplit2[4];
            wes[h] = westgps;
        } 
        
        for(int h = 0; h < orderSplit1.length; h++)   //adding all our info to the arraylist route, to create split orders 
        {
            route.add(deliverynum[h] + ", " + nor[h] + ", " + wes[h]);   
        } 

        apacheNorth = 53.38197;   //use these as the distance to compare each of our orders to, as its the starting position of Apache Pizza
        apacheWest = -6.59274;
        

        nnRoute ourRoute = new nnRoute(route, apacheNorth,apacheWest);   //this is the root object to do all our calculations 

        for(int j = 0; j < length; j++)    //run through the length of our split array 
        {
            String answer =  ourRoute.nnAlgo();    //call the NN algo, and set its result to our string answer 
            ourRoute.setLatitude(Double.parseDouble(answer.split(",")[1]));      //update the lat to the lat of our answer string 
            ourRoute.setLongitude(Double.parseDouble(answer.split(",")[2]));       //update the long to the lat of our answer string 

            bestOrderRoute = bestOrderRoute + "," + answer.split(",")[0]; //add the bestOrderRoute to itself, plus the orderNum of our answer string 
            ourRoute.removeRoute(answer);   //remove the shortest order from the arraylist using this remove method 
        }

       
        bestOrderRoute = bestOrderRoute.substring(1);   //create a substring of our best route to cut out comma at front
        System.out.print(bestOrderRoute);    //print best route 
        
    }



    public static String getBestOrderRoute()    //returns our best route to the UI 
    {
        return bestOrderRoute;  
    }


}




class nnRoute     //this is our route class 
{
   public ArrayList <String> route;  
   public double northGPS;    //our variables 
   public double westGPS;
   public String sol;
   public String nextLongitude;
   public String nextLatitude;
   public String thirdLongitude;
   public String thirdLatitude;


    public nnRoute(ArrayList <String> route,double northGPS,double westGPS)     //constructor 
    {
        this.route = route;
        this.northGPS = northGPS;
        this.westGPS = westGPS;
    }

    public void setLatitude(double northGPS)   //our setter for latitude
    {
        this.northGPS = northGPS;
    }

    public void setLongitude(double westGPS)   //our setter for longitude
    {
        this.westGPS = westGPS;
    }


   public void removeRoute(String routeString)     //remove method for our arraylist 
   {
        for(int i = 0; i < route.size(); i++)
        {
            if(route.get(i).equals(routeString))
            {
                route.remove(i);
            }
        }
   }


    public String nnAlgo()   //here we are doing NN by using haversine and keeping track of the shortest distance 
    {
        String apacheN = String.valueOf(this.northGPS);   //getting the Coordinates of Apache from our NNroute
        String apacheW = String.valueOf(this.westGPS);
        sol = "";  //our string 
        int loopInt = route.size() -1;  //int value for looping through our array list 
        double temp = 0.0;  //temp value 
        double shortest = haversine(apacheN, route.get(0).split(",")[1], apacheW, route.get(0).split(",")[2]);   //get the distance from apcahe to the first order, this is shortest at first
                                                                                                            
        //start looping, checking the distances using haversine
        for(int m = 1; m < loopInt; m++) //start at next order after first 
        {
           temp = haversine(apacheN, route.get(m).split(",")[1], apacheW, route.get(m).split(",")[2]); //get the long and lat of the next order from our arraylist
           if(temp < shortest)  //if this distance is shorter then our shortest value so far
           {
                shortest = temp;  //make shortest = our new shortest 
                Collections.swap(route, 0, m);  //put this shortest order at the start of the arraylist
           }

        }
            return route.get(0);   //return the shortest

    }


    public double haversine(String latitudeA, String latitudeB, String longitudeA, String longitudeB) //method used to get distance between 2 orders, 
    {
        double latitude1 = Double.parseDouble(latitudeA);
        double longitude1 = Double.parseDouble(longitudeA);
        double latitude2 = Double.parseDouble(latitudeB);
        double longitude2 = Double.parseDouble(longitudeB);
        longitude1 = Math.toRadians(longitude1);   //converting to radians due to easier to work with for eq/n
        longitude2 = Math.toRadians(longitude2);
        latitude1 = Math.toRadians(latitude1);
        latitude2 = Math.toRadians(latitude2);

       // Haversine formula used to calculate the distance 
       double dlon = longitude2 - longitude1;
       double dlat = latitude2 - latitude1;
       double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(latitude1) * Math.cos(latitude2)
       * Math.pow(Math.sin(dlon / 2),2);
            
       double c = 2 * Math.asin(Math.sqrt(a));  

       // Radius of earth in kilometers.
       double r = 6371;

       // calculate the result by multiplying both results together, then return 
       return(c * r);
    }

   
   
}




class UI extends Frame implements ActionListener     //class dealing with graphics and UI
{
 JFrame frame;  //the frame 
 Panel right, bottom;   //panels for different items
 // Inner components that are used in this window
 JLabel map, info;  
 JTextArea input, result;
 Button submit;
 Font font;

    public UI()   //our UI func fired on launch in main 
    {
        
        frame = new JFrame();  //creating our Jframe 
        
        frame.setBounds(0, 0, 900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
        frame.setTitle("TSP Project 19450974");
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setVisible(true);

        right();     //creating panels to hold more info for input and results 
        bottom();
        frame.add(right);
        frame.add(bottom);
        frame.setVisible(true);

    }
 
    public void right()   //making the right part of our window used for inputting the orders
    {
        right = new Panel();     //making the panel to add components to 
        right.setBounds(685, 0, 200, 400);
        right.setBackground(Color.green);
        right.setLayout(null);

        input = new JTextArea();     //making the textfield for input
    	input.setBounds(10, 10, 175, 250);
    	input.setFont(new Font("SansSerif", Font.BOLD, 10));
    	input.setFocusable(true);

        submit = new Button("Submit Orders");    //adding the submit button
    	submit.setBounds(10, 310, 175, 80);
    	submit.setBackground(Color.yellow); 
    	submit.setFocusable(true);
    	submit.setFont(new Font("SansSerif", Font.BOLD, 20));
    	submit.addActionListener(this);
            
        right.add(input);   //adding and making visible 
        right.add(submit);
        input.setVisible(true);
        submit.setVisible(true);
        right.setVisible(true);
    }


    public void bottom()   //our bottom panel, used for output text area 
    {
        bottom = new Panel();
        bottom.setBounds(0, 400, 900, 165);
    	bottom.setBackground(Color.green);
    	bottom.setLayout(null);

        result = new JTextArea();
    	result.setBounds(10, 10, 650, 120);
    	result.setFont(new Font("SansSerif", Font.BOLD, 10));
        


        bottom.add(result);

        bottom.setVisible(true);
        result.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {        //when submit button is clicked 

        String orders = input.getText();    //take the order string 
        algo s = new algo(orders);  //put it into our algo 
        String re = s.getBestOrderRoute();   //get the route 
        result.setText(re); //set the text of the output box to it
        
    }
   
    
} 







