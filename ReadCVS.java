/*
Created with IntelliJ IDEA.
User: cchen
Date: 9/19/13
Time: 9:19 PM
To change this template use File | Settings | File Templates.
 */

//import java.net.SocketPermission;
import java.util.*;

import com.movie.rating.MovieRating;
import com.movie.rating.ReadCSV_general    ;

public class ReadCVS {

    private HashMap<Integer,Integer> movieID_Dictionary = null;
    private HashMap<Integer,Integer> userID_Dictionary = null;

    public static void main(String[] args) {

        ReadCVS obj = new ReadCVS();
        obj.run();

    }

    void run(){
        // get 3 assigned movie IDs
        int[] movieIDsAssigned = new int[3]; //CC: use int[] rather than Vector[Integer], because Vector can not pre-specify size (capacity is not size)
        movieIDsAssigned[0] = 1597;        //   assigned 1597 , 194, 1900
        movieIDsAssigned[1] = 194;         // example, 11, 121,8587
        movieIDsAssigned[2] = 1900;


        // read in all ratings from the csv file
        String csvFile = "C:/Users/cchen/Dropbox/Tutorial/coursera/Introduction to Recommender Systems/HW/Programming HW/HW1/recsys-data-ratings.csv";
        ReadCSV_general read_csv = new ReadCSV_general(csvFile) ;
        ArrayList<MovieRating>  allRatings = parseContent(read_csv.getContent());     // parse them into MovieRating class

        // create a rating matrix
//        not a good idea to create a matrix due to sparseness

        movieID_Dictionary = create_ObjectID_rowNum_Dictionary(extract_MovieIDs(allRatings));
        userID_Dictionary = create_ObjectID_rowNum_Dictionary(extract_UserIDs(allRatings));
        int[] orderedMovieIDs = create_ObjectID_array(movieID_Dictionary);
//        int[] orderedUserIDs = create_ObjectID_array(userID_Dictionary);

        for (int aMovieID : movieIDsAssigned){
            float[] simpleModelResult = simpleModelRating(aMovieID,allRatings);
            printRatingResult(simpleModelResult, orderedMovieIDs,"",5);
        }
        System.out.println("\r\n");

        for (int aMovieID : movieIDsAssigned){
            float[]  advancedModelResult = advancedModelRating(aMovieID,allRatings);
            printRatingResult(advancedModelResult, orderedMovieIDs,"",5);
        }

    }

    void printRatingResult(float[] ratings,int[] movieIDs,String headingString,int numberResults){
        System.out.println();
        System.out.println(headingString);
        int[] orderedIndex = order(ratings);
        System.out.print(movieIDs[orderedIndex[0]] + ",");
        for (int currentIndex: Arrays.copyOfRange(orderedIndex,1,numberResults+1)){
            System.out.print(movieIDs[currentIndex] + "," + ratings[currentIndex] + ",");
        }
    }

    int[] order(float[] array){
        Map<Float, Integer> map = new TreeMap<Float, Integer>();
        for (int i = 0; i < array.length; ++i) {
            map.put(array[i], i);
        }
        Collection<Integer> indices = map.values();
        Integer[] orderInteger = indices.toArray(new Integer[indices.size()]);
        int[] orders = new int[orderInteger.length];
        for (int rowNum = 0;rowNum   < orders.length; rowNum  ++) {
//            orders[rowNum ] = orderInteger[rowNum];
            orders[orders.length - rowNum - 1] = orderInteger[rowNum];
        }
        return orders;
    }
    int[] create_ObjectID_array(HashMap<Integer,Integer> objectID_Dictionary){
        int[] objectIDs_array = new int[objectID_Dictionary.size()];
        for (Integer aID: objectID_Dictionary.keySet()){
            objectIDs_array[objectID_Dictionary.get(aID)] = aID;
        }
        return objectIDs_array;
    }


    ArrayList<Integer> extract_MovieIDs(ArrayList<MovieRating>  allMovieRating){
        ArrayList<Integer> allMovieIDs =  new ArrayList<Integer>();
        for (MovieRating aMovieRating: allMovieRating) allMovieIDs.add(aMovieRating.movieID);
        return allMovieIDs;
    }


    ArrayList<Integer> extract_UserIDs(ArrayList<MovieRating>  allMovieRating){
        ArrayList<Integer> allUserIDs =  new ArrayList<Integer>();
        for (MovieRating aMovieRating: allMovieRating) allUserIDs.add(aMovieRating.userID);
        return allUserIDs;
    }

    HashMap<Integer,Integer> create_ObjectID_rowNum_Dictionary(ArrayList<Integer> objectIDs){
        Set<Integer> uniqueIDs = new HashSet<Integer>();
        uniqueIDs.addAll(objectIDs);

        HashMap<Integer,Integer> objectID_rowNum_Dictionary =  new HashMap <Integer, Integer>();
        Integer rowNum = 0;
        for (Integer aID: uniqueIDs) objectID_rowNum_Dictionary.put(aID,rowNum++);
        return objectID_rowNum_Dictionary;
    }

    protected int[] countXorNoX_andY(Integer movieX, ArrayList<MovieRating> allMovieRatings, boolean x_OrNoX){

        // get all users watched movie X
        ArrayList<Integer> allUsersDueToX = new ArrayList<Integer>();
        for (MovieRating aMovieRating:allMovieRatings){
            if (aMovieRating.movieID == movieX) allUsersDueToX.add(aMovieRating.userID);
        }


        if (!x_OrNoX)  {
            ArrayList<Integer> fullUserID = new ArrayList<Integer>(userID_Dictionary.keySet());
            fullUserID.removeAll(allUsersDueToX);
            allUsersDueToX = fullUserID;
        }
        int[] countAllMovies = new int[movieID_Dictionary.size()];

        for (MovieRating aMovieRating:allMovieRatings){
            if (allUsersDueToX.contains(aMovieRating.userID)){
                int tempRowID = movieID_Dictionary.get(aMovieRating.movieID);
                countAllMovies[tempRowID] += 1;
            }
        }
        return countAllMovies;
    }


    protected int countX(Integer movieX, ArrayList<MovieRating> allMovieRatings){
        int count = 0;
        for (MovieRating aMovieRating:allMovieRatings){
            if (aMovieRating.movieID == movieX) count += 1;
        }
        return count;
    }

    protected float[] simpleModelRating(Integer controlMovieID,ArrayList<MovieRating> allRatings){
        // (X and Y)/ X
        int countMovieX = countX(controlMovieID,allRatings);
        int[] countMovieXandY = countXorNoX_andY(controlMovieID, allRatings, true);
        float[] simpleRatings = new float[countMovieXandY.length];
        for (int i=0; i<countMovieXandY.length; i++) {
            simpleRatings[i] = ((float) countMovieXandY[i]) / countMovieX;
        }
        return simpleRatings;
    }



    protected float[] fractionNoXbutYoverNoX(Integer controlMovieID, ArrayList<MovieRating> allRatings){
        // (!X and Y)/ !X
        int countNoX = userID_Dictionary.size() - countX(controlMovieID,allRatings);
        int[] countNoMovieXandY = countXorNoX_andY(controlMovieID, allRatings, false);
        float[] resultFraction = new float[countNoMovieXandY.length];
        for (int i=0; i<countNoMovieXandY.length; i++) {
            resultFraction[i] =((float) countNoMovieXandY[i]) / countNoX;
        }
        return resultFraction;
    }

    protected float[] advancedModelRating(Integer aMovieID, ArrayList<MovieRating> allRatings){
        // ((x and y) / x) / ((!x and y) / !x)
        float[] xyOverX = simpleModelRating(aMovieID,allRatings);
        float[] noX_Y_over_NoX = fractionNoXbutYoverNoX(aMovieID, allRatings);
        float[] resultFraction = new float[noX_Y_over_NoX.length];
        for (int i=0; i<noX_Y_over_NoX.length; i++) {
            resultFraction[i] =  xyOverX[i] /  noX_Y_over_NoX[i];
        }
        return resultFraction;
    }

    protected ArrayList<MovieRating> parseContent(ArrayList<String> allLines) {
        String cvsSplitBy = ",";

        ArrayList<MovieRating>   allRatings = new ArrayList<MovieRating>();

        for (String aLine : allLines){
            // use comma as separator
            String[] sep_string = aLine.split(cvsSplitBy);

            allRatings.add(new MovieRating(Integer.parseInt(sep_string[0]),
                    Integer.parseInt(sep_string[1]),
                    Float.parseFloat(sep_string[2])));
        }
        return allRatings;
    }

}