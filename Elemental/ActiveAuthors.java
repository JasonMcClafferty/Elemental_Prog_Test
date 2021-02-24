import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;

class Result {
    /*
     * Complete the 'getUsernames' function below.
     *
     * The function is expected to return a STRING_ARRAY.
     * The function accepts INTEGER threshold as parameter.
     */



    public void getPage() {}

    public void getAllPages() {}



    public static List<String> getUsernames(int threshold) throws Exception{
        List<String> usernames = new ArrayList<>();
        /*
        1. we need to get the first page of data to understand how many pages there are
        2. call http://elemental.kieda.com/api/article_users/search?page=1
        3. since we only have regex available as an import
        - get number of pages (from page 1 data - total_pages field)
        - we already have page 1 so we can process that data
        - process page 1
            - extract each username and their submitted fields
            - if submitted > threshold
            - add username to list of names
        - if number of pages > 1
        - call rest get for page 2, 3, and so on and constantly add names to the list of usernames

        */


        return usernames;
    }
}

class Solution {
    public static void main(String[] args ) {
       /* BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("result.txt"));

        int threshold = Integer.parseInt(bufferedReader.readLine().trim());

        List<String> result = Result.getUsernames(threshold);

        bufferedWriter.write(
                result.stream()
                        .collect(joining("\n"))
                        + "\n"
        );
        bufferedReader.close();
        bufferedWriter.close();
        */

        //System.out.println(response.body());

        // The data field holds the author records, this needs split up into comparable authors
        // The total_pages field tells me how many times I need to run a request and create objects
        // If I boil that down into a high level algorithm it's something like:
        //
        //      while requests made <= total_pages (use a for loop to track the page number) :
        //          Request the data
        //          parse the data into relevant comparable objects at the required scope
        //          sort the objects into the list I need based on the threshold number
        //
        // This will need to be tested, refined, refactored etc. but lets get an alpha first.

        //  Step 1: Request the Data
        //      Need a connection the the API endpoint - Setup
        //      Save the data in a variable


        try {
            System.out.print(setupHttpClient());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static List<String> setupHttpClient () throws IOException, InterruptedException {
        String api = "http://elemental.kieda.com/api/article_users/search?page=1";
        List<String> allAuthors;
        ArrayList<String> authorsOverThreshold = new ArrayList<String>();

        // LinkedHashMap for predictable iteration order
        Map<String, String> authorComparator = new LinkedHashMap<String, String>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.print(response.statusCode());
        allAuthors = Arrays.asList(response.body().split("id"));


        for(String authorEntry : allAuthors) {

            System.out.println("\n");
           // System.out.println("\"id" + authorEntry);

            //List<String> authorEntryDecomposedIntoKeyValPairs = Arrays.asList(authorEntry.split(","));
            // System.out.println(authorEntryDecomposedIntoKeyValPairs);

            //check for empty entries below
            String username= regexCheck("\\busername.+?\\b.+?\\b", authorEntry);

            username = username.replace("username:", "");


            // this regex goes to the start of the next word it seems, I need to use a slightly different regex to take the number, not the next word border
            String submitted = regexCheck("\\bsubmitted.+?\\b.+?\\b", authorEntry);

            submitted = submitted.replace("submitted:", "");

            // add usernames if username isn't null and submission count is greater than the threshold (hardcoded as 10)
            if(username.length() > 0 && Integer.parseInt(submitted) > 10) {
                authorComparator.put(username, submitted);
            }
        }

        System.out.println(authorComparator.toString());

        authorsOverThreshold.addAll(authorComparator.keySet());

        return authorsOverThreshold;
    }

    public static String regexCheck(String myRegexPattern, String stringToCheck) {
        String result = "";

        Pattern myPattern = Pattern.compile(myRegexPattern);
        Matcher myMatcher = myPattern.matcher(stringToCheck);

        //System.out.println("regexCheck");

        // if there is a non-null result, print it to system out.
        while (myMatcher.find()) {
            if (myMatcher.group().length() != 0){
                result = myMatcher.group().trim();
            }
        }

        result = result.replace("\"", "");
        System.out.println(result);

        return result;
    }

}
