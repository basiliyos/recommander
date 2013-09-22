package com.movie.rating;

/**
  Created with IntelliJ IDEA.
  User: cchen
  Date: 9/19/13
  Time: 10:43 PM
  To change this template use File | Settings | File Templates.
 */
public class MovieRating {
    public int userID,movieID;
    public float rating  ;

    @Override
    public String toString(){
        return String.format(" User ID:" + this.userID + " Movie ID: " + this.movieID + " Rating:" + this.rating );
    }

    public MovieRating(Integer userID, Integer movieID, Float rating){
        this.userID = userID;
        this.movieID = movieID;
        this.rating = rating;
    }

}
