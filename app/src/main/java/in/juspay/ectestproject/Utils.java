package in.juspay.ectestproject;

public class Utils {

    public static String getApiKey(String mid){
        switch (mid){
            case "foodpanda" : return "B7171B010954EE1AB0D5D28FB00472";
            case "paypal" : return "EF8DB368DD44E9CB9A860428E14CFC";
            case "dunzo" : return "6EAEEC04D15415594A433F2A2C7573";
            case "1mg" : return "3D1F3A21442240099A46D5E1D2664813";
            case "gaana" : return "E7080E5194D4C89A045FABF81220C3";
            case "playo" : return "CA611D5786848B0A71240E048EA5B1";
            case "milkbasket" : return "BE62AB31454486180C7FC31219E9FE";
            case "dream11" : return "C9F62CB5C1384FD39D1BC55E76524529";
            case "railyatri_sandbox" : return "5AC4C22C87D41038EEFC56EC91A31D";
            case "ballebaazi" : return "1B219E263F2446BA80E27CD6A2708F";
            case "Anjali_A_L" : return "6F8BA74AFD34D17B1AF518887C3745";
            case "bounce" : return "3FF1185EF5D458491712E472A88A04";
            case "dailyninja" : return "BC507DB54D2450C925025C751F38D8";
        }
        return "";

    }
}


