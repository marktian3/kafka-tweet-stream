package marktian3.Kafka;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInput {

    public static List<String> readUserTerms(){

        ArrayList<String> terms = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        for(String term : sc.nextLine().split(" ")){
            terms.add(term);
        }

        sc.close();
        return terms;
    }
}
