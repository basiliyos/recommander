/*
Created with IntelliJ IDEA.
User: cchen
Date: 9/19/13
Time: 9:19 PM
To change this template use File | Settings | File Templates.
 */

import java.io.*;
//import java.net.SocketPermission;
import java.util.ArrayList;

import com.movie.rating.MovieRating;

public class ReadCVS {

    public static void main(String[] args) {

        ReadCVS obj = new ReadCVS();
        obj.run();

    }


    public void run() {

        String csvFile = "C:/Users/cchen/Dropbox/Tutorial/coursera/Introduction to Recommender Systems/HW/Programming HW/HW1/recsys-data-ratings.csv";
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";

        try {


            ArrayList<MovieRating>   allRatings = new ArrayList<MovieRating>();
            br = new BufferedReader(new FileReader(csvFile));

            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] sep_string = line.split(cvsSplitBy);

                MovieRating tempRating = new MovieRating();
                tempRating.userID = Integer.parseInt(sep_string[0]) ;
                tempRating.movieID = Integer.parseInt(sep_string[1]);
                tempRating.rating = Float.parseFloat(sep_string[2]);

                allRatings.add(tempRating) ;
            }

            br.close();

            for (MovieRating aRating : allRatings)           {
                System.out.println(aRating);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
    }

}