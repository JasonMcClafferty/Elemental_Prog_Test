import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
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

        String apiURI = "http://elemental.kieda.com/api/article_users/search?page=1";
        List<String> allUsersAsList;

        // get all users from the API endpoint.
        // split the all users string into individual user strings
        // add individual user strings to allUsers
        allUsersAsList = Arrays.asList(getAllUsersFromAPI(apiURI).split("id"));


        // Extract usernames & submissions
        // Sort and Filter users by submission count
        usernames = extractAndSortFields(allUsersAsList, threshold);

        return usernames;
    }

    public static String getAllUsersFromAPI(String api) throws IOException, InterruptedException {
        int totalPages;
        String allUserData = "";

        // Setting up a http connection
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api))
                .timeout(Duration.ofSeconds(20))
                .build();

        // send a get request(default) and add the response body to String allUserData
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        allUserData += response.body();

        // can be wrapped in a method
        totalPages = Integer.parseInt(regexCheck("\\btotal_pages.+?\\b.+?\\b", response.body())
                                .replace("total_pages:", ""));


        // Get each of the next pages of users
        // Append those results to String allUserData
        for (int i = 2; i <= totalPages; i++) {
            String next_api = api.replace("page=1", "page=" + i);

            HttpRequest next_request = HttpRequest.newBuilder()
                    .uri(URI.create(next_api))
                    .timeout(Duration.ofSeconds(20))
                    .build();

            HttpResponse<String> next_response = client.send(next_request, HttpResponse.BodyHandlers.ofString());
            allUserData += next_response.body();
        }

        return allUserData;
    }

    public static ArrayList<String> extractAndSortFields(List<String> listOfAllAuthors, int threshold) {
        Map<String, String> userSubmissionCount = new LinkedHashMap<String, String>();
        ArrayList<String> usersOverThreshold = new ArrayList<String>();

        for(String userEntry : listOfAllAuthors) {

            String username= regexCheck("\\busername.+?\\b.+?\\b", userEntry)            // Extract the username field from the response string
                    .replace("username:", "");                                         // Extract the username from the username field.

            String submitted = regexCheck("\\bsubmission_count.+?\\b.+?\\b", userEntry)  // Extract the submitted field from response string
                    .replace("submission_count:", "");                                 // Extract the submitted number from the submitted field.


            // add usernames if username isn't null and submission count is greater than the threshold (hardcoded as 10)
            if(username.length() > 0 && Integer.parseInt(submitted) > threshold) {
                userSubmissionCount.put(username, submitted);
            }
        }

        usersOverThreshold.addAll(userSubmissionCount.keySet());          // Reformatting the data structure & extracting author names assuming my sort works

        return usersOverThreshold;
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
        //System.out.println(result);

        return result;
    }
}

class Solution {
    public static void main(String[] args ) throws Exception {
       BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
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
    }
}
