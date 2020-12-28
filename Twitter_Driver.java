//Name: Sara Liu        Date: 12/8/17

//version 12.7.2016

import twitter4j.*;       //set the classpath to lib\twitter4j-core-4.0.4.jar
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;
import java.util.StringTokenizer; //below are added
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import twitter4j.Query.Unit;


public class Twitter_Driver
{
   private static PrintStream consolePrint;

   public static void main (String []args) throws TwitterException, IOException
   {
      consolePrint = System.out; // this preserves the standard output so we can get to it later      
   
      // PART 1
      // set up classpath and properties file
          
      TJTwitter bigBird = new TJTwitter(consolePrint);
      
      // Create and set a String called message here
   
      
      String message = "Winter break is here!!";
      //bigBird.tweetOut(message);
      
       
   
      // PART 2
      // Choose a public Twitter user's handle 
      /*
      Scanner scan = new Scanner(System.in);
      consolePrint.print("Please enter a Twitter handle, do not include the @symbol --> ");
      String twitter_handle = scan.next();
       
      // Find and print the most popular word they tweet 
      while (!twitter_handle.equals("done"))
      {
         bigBird.queryHandle(twitter_handle);
         consolePrint.println("The most common word from @" + twitter_handle + " is: " + bigBird.mostPopularWord()+ ".");
         consolePrint.println("The word appears " + bigBird.getFrequencyMax() + " times.");
         consolePrint.println();
         consolePrint.print("Please enter a Twitter handle, do not include the @ symbol --> ");
         twitter_handle = scan.next();
      }
      */
      // PART 3
      bigBird.investigate();
      
      
   }//end main         
      
}//end driver        
      
class TJTwitter 
{
   private Twitter twitter;
   private PrintStream consolePrint;
   private List<Status> statuses;
   private List<String> terms;
   private String popularWord;
   private int frequencyMax;
  
   public TJTwitter(PrintStream console)
   {
      // Makes an instance of Twitter - this is re-useable and thread safe.
      // Connects to Twitter and performs authorizations.
      twitter = TwitterFactory.getSingleton(); 
      consolePrint = console;
      statuses = new ArrayList<Status>();
      terms = new ArrayList<String>();
   }

  /******************  Part 1 *******************/
  /** 
   * This method tweets a given message.
   * @param String  a message you wish to Tweet out
   */
   public void tweetOut(String message) throws TwitterException, IOException
   {
      twitter.updateStatus(message);  
   }

   
  /******************  Part 2 *******************/
  /** 
   * This method queries the tweets of a particular user's handle.
   * @param String  the Twitter handle (username) without the @sign
   */
   @SuppressWarnings("unchecked")
   public void queryHandle(String handle) throws TwitterException, IOException
   {
      statuses.clear();
      terms.clear();
      fetchTweets(handle);
      splitIntoWords();	
      removeCommonEnglishWords();
      sortAndRemoveEmpties();
   }
	
  /** 
   * This method fetches the most recent 2,000 tweets of a particular user's handle and 
   * stores them in an arrayList of Status objects.  Populates statuses.
   * @param String  the Twitter handle (username) without the @sign
   */
   public void fetchTweets(String handle) throws TwitterException, IOException
   {
      // Creates file for dedebugging purposes
      PrintStream fileout = new PrintStream(new FileOutputStream("tweets.txt")); 
      Paging page = new Paging (1,200);
      int p = 1;
      while (p <= 10)
      {
         page.setPage(p);
         statuses.addAll(twitter.getUserTimeline(handle,page)); 
         p++;        
      }
      int numberTweets = statuses.size();
      fileout.println("Number of tweets = " + numberTweets);
   
      int count=1;
      for (Status j: statuses)
      {
         fileout.println(count+".  "+j.getText());
         count++;
      }
   }   

  /** 
   * This method takes each status and splits them into individual words.   
   * Remove punctuation by calling removePunctuation, then store the word in terms.  
   */
   public void splitIntoWords()
   {   
      StringTokenizer strToken = null;
      String temp = null;
      
      for(int i = 0; i < statuses.size(); i++)
      {
         temp = removePunctuation(statuses.get(i).getText());
         strToken = new StringTokenizer(temp);
         while(strToken.hasMoreTokens())
         {
            String str = strToken.nextToken();
            terms.add(str);
         }
      }
   }

  /** 
   * This method removes common punctuation from each individual word.
   * Consider reusing code you wrote for a previous lab.     
   * Consider if you want to remove the # or @ from your words. Could be interesting to keep (or remove).
   * @ param String  the word you wish to remove punctuation from
   * @ return String the word without any punctuation  
   */
   private String removePunctuation( String s )
   {
      //return null;
      String r = "";
      for(int i = 0; i < s.length(); i++)
      {
         if(Character.isLetter(s.charAt(i)) || Character.isDigit(s.charAt(i)) || s.charAt(i) == ' ' || s.charAt(i) == '#')
         {
            r += s.charAt(i);
         }
      }
      return r;
   }

  /** 
   * This method removes common English words from the list of terms.
   * Remove all words found in commonWords.txt  from the argument list.    
   * The count will not be given in commonWords.txt. You must count the number of words in this method.  
   * This method should NOT throw an excpetion.  Use try/catch.   
   */
   @SuppressWarnings("unchecked")
   private void removeCommonEnglishWords()
   {	
      Scanner remover = null;	
      try
      {
         remover = new Scanner(new File("commonWords.txt"));
         while(remover.hasNext())
         {
            String word = remover.nextLine();
            for(int i = 0; i < terms.size(); i++)
               if(terms.get(i).equalsIgnoreCase(word))  {
                  terms.remove(terms.get(i));
                  i--; //need this because when you have "the the the", remove first is "the the" but pointer is on second "the" so you skip the ORIGINAL second "the"
               }    
         } 
      }
      catch(FileNotFoundException e)
      {
      }  
   }

  /** 
   * This method sorts the words in terms in alphabetically (and lexicographic) order.  
   * You should use your sorting code you wrote earlier this year.
   * Remove all empty strings while you are at it.  
   */
   @SuppressWarnings("unchecked")
   public void sortAndRemoveEmpties()
   {
      // String temp;
   //       int maxIndex;
   //       for(int k = 0; k < terms.size(); k++)
   //       {
   //          maxIndex = 0;
   //          for(int i = 0; i < terms.size() - i - 1; i++)
   //          {
   //             if(terms.get(maxIndex).compareTo(terms.get(i)) < 0)
   //                maxIndex = i;
   //          }
   //          temp = terms.get(maxIndex);
   //          terms.set(maxIndex, terms.get(terms.size() - k - 1));
   //          terms.set(terms.size() - k - 1, temp);
   //       }
   
      String temp;      
      for(int i = 0; i < terms.size() - 1; i++) 
      {         
         for(int j = i + 1; j < terms.size(); j++) 
         {
            if(terms.get(i).compareTo(terms.get(j)) > 0) 
            {
               temp = terms.get(i);
               terms.set(i, terms.get(j));
               terms.set(j, temp);           
            }
         }
      }
       
      for(int i = 0; i < terms.size(); i++)
      {
         if(terms.get(i).equals(""))
         {
            terms.remove(terms.get(i));            
         }
      }
   }
  
  /** 
   * This method returns the most common word from terms.    
   * Consider case - should it be case sensitive?  The choice is yours.
   * @return String the word that appears the most times
   * @post will popopulate the frequencyMax variable with the frequency of the most common word 
   */
   @SuppressWarnings("unchecked")
   public String mostPopularWord()
   {
      frequencyMax = 0;
      int count = 0;
      String r = "";
      for(int i = 0; i < terms.size() - 1; i++)
      {
         if(terms.get(i+1).compareTo(terms.get(i)) == 0)
         {
            if(count == 0) 
            {
               count = 2;
            }
            else 
            {
               count++;
            }
         }
         else
         {
            if(count > frequencyMax)
            {
               frequencyMax = count;
               r = terms.get(i);
            }
            count = 0;
         }
      }
      return r;
      //return null;
   }
  
  /** 
   * This method returns the number of times the most common word appears.    
   * Note:  variable is populated in mostPopularWord()
   * @return int frequency of most common word
   */
   public int getFrequencyMax()
   {
      return frequencyMax;
   }


  /******************  Part 3 *******************/
   public void investigate ()
   {
      filterByName("Washington Post");
      countTweetsFromZipCode("covid", 40, -80, 5);  
      birthdayGreeting("handle", "Happy Birthday", 10);
      searchForTweets();
      tjMentionedInBio(40, -80, 10, Query.MILES);
   } // :)
   
   
   public void filterByName(String remove)
   {
      List<Status> myHomeStatus = null;
      try
      {
         myHomeStatus = twitter.getHomeTimeline();
      }
      catch(TwitterException e)
      {
      }
   
      System.out.println("\n>>>>>Showing my home timeline below (unfiltered)<<<<<");
      for (int i = 0; i < myHomeStatus.size(); i++) 
      {
         System.out.println(myHomeStatus.get(i).getUser().getName() + ":" + myHomeStatus.get(i).getText());
      }
   
      System.out.println("\n>>>>>Showing my home timeline below (\"" + remove + "\" no longer present)<<<<<");      
      for(int i = 0; i < myHomeStatus.size(); i++)
      {
         if(myHomeStatus.get(i).getUser().getName().indexOf(remove) == -1)
         {
            System.out.println(myHomeStatus.get(i).getUser().getName() + ":" + myHomeStatus.get(i).getText());
         }
      }     
   }
   
   public void countTweetsFromZipCode (String topic, double latitude, double longitude, double radius)
   {
      System.out.println("\n>>>>>Checking tweets regarding covid originating from my zip code<<<<<");
      
      Query query = new Query(topic);
      query.setGeoCode(new GeoLocation(latitude, longitude), 3, Query.MILES);
      QueryResult result = null;
      
      try
      {
         result = twitter.search(query);
      }
      catch(TwitterException e)
      {
      }
      
      int i = 0;
      for(Status status : result.getTweets()) 
      {
         System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText() + " : " + status.getUser().getLocation());
         i++;
      } 
      System.out.println("\nThe # of tweets for \"covid\" originated from my zip code is: " + i + "\n");
   }

   private void birthdayGreeting(String handle, String message, int timeInterval)
   {
      System.out.println("\n<<<<< birthday message to @" + handle + "<<<<<");
   
      try
      {         
         int i = 1;
         while(i <= 3)
         {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis()));
            DirectMessage dMsg = twitter.sendDirectMessage(handle, message + " - " + timeStamp);
            System.out.println("Sent: " + dMsg.getText() + " to @" + dMsg.getRecipientScreenName());
            i++;
            if(i <= 3)
            {
               Thread.sleep(timeInterval * 1000);
            }
         }
      }
      catch(TwitterException e)
      {
         System.out.println("Something went wrong: " + e);
      }      
      catch(InterruptedException e)
      {
      }
   }
   
   private void searchForTweets()
   {
      Query query = new Query("Miami Dolphins");
      query.setCount(100);
      query.setGeoCode(new GeoLocation(40,-80), 10, Query.MILES);
      query.setSince("2017-12-3");
      try 
      {
         QueryResult result = twitter.search(query);
         System.out.println("\nCount : " + result.getTweets().size()) ;
         for (Status tweet : result.getTweets()) 
         {
            System.out.println("@" + tweet.getUser().getName()+ ": " + tweet.getText());  
         }
      } 
      catch(TwitterException e) 
      {
         e.printStackTrace();
      } 
      System.out.println(); 
   }

   private void tjMentionedInBio(double latitude, double longitude, double radius, Unit unit)
   {
      System.out.println("\n<<<<List of people mention TJ in their Twitter bio>>>>");
   
      try 
      {
         Query query = new Query("TJHSST");
         query.setGeoCode(new GeoLocation(latitude, longitude), radius, unit);
         query.setCount(100);
         QueryResult result = twitter.search(query);
         
         statuses.clear();   
         statuses = result.getTweets();
      
         for(int i = 0; i < statuses.size(); i++)
         {
            String temp = statuses.get(i).getUser().getDescription().toLowerCase();
            if (temp.indexOf("tj") != -1)
            {
               System.out.println(statuses.get(i).getUser().getName() + ":" + statuses.get(i).getUser().getDescription());
            }
         }      
      } 
      catch(TwitterException e) 
      {
         System.out.println("Something went wrong? " + e);
      }      
   }

  /** 
   * This method determines how many people in Arlington, VA 
   * tweet about the Miami Dolphins.  Hint:  not many. :(
   */
   public void sampleInvestigate ()
   {
      Query query = new Query("Miami Dolphins");
      query.setCount(100);
      query.setGeoCode(new GeoLocation(40,-80), 5, Query.MILES);
      query.setSince("2015-12-1");
      try {
         QueryResult result = twitter.search(query);
         System.out.println("Count : " + result.getTweets().size()) ;
         for (Status tweet : result.getTweets()) {
            System.out.println("@"+tweet.getUser().getName()+ ": " + tweet.getText());  
         }
      } 
      catch (TwitterException e) {
         e.printStackTrace();
      } 
      System.out.println(); 
   }  

}  

